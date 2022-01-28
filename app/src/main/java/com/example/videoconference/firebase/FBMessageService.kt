package com.example.videoconference.firebase

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.videoconference.activities.IncomingInvitationActivity
import com.example.videoconference.notification.NotificationReceiver
import com.example.videoconference.notification.createMessageChannel
import com.example.videoconference.notification.createNotification
import com.example.videoconference.utilities.Constants
import com.example.videoconference.utilities.sendMessageToFMS
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FBMessageService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        registerReceiver(NotificationReceiver(),
            IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE))
        val type = remoteMessage.data[Constants.REMOTE_MSG_TYPE]
        if (type != null) {
            when(type){
                "answer" -> {
                    LocalBroadcastManager.getInstance(applicationContext)
                        .sendBroadcast(Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                           .putExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE, "тыква"))
                }
                Constants.REMOTE_MSG_INVITATION -> {
                    if (Constants.stateApp == 1) {
                        Constants.startPlay(this, "in")
                        startActivity(Intent(applicationContext, IncomingInvitationActivity::class.java)
                            .putExtra(
                                Constants.REMOTE_MSG_MEETING_TYPE,
                                remoteMessage.data[Constants.REMOTE_MSG_MEETING_TYPE])
                            .putExtra(
                                Constants.KEY_FIRST_NAME,
                                remoteMessage.data[Constants.KEY_FIRST_NAME])
                            .putExtra(
                                Constants.KEY_LAST_NAME,
                                remoteMessage.data[Constants.KEY_LAST_NAME])
                            .putExtra(
                                Constants.KEY_EMAIL,
                                remoteMessage.data[Constants.KEY_EMAIL])
                            .putExtra(
                                Constants.REMOTE_MSG_INVITER_TOKEN,
                                remoteMessage.data[Constants.REMOTE_MSG_INVITER_TOKEN])
                            .putExtra(
                                Constants.REMOTE_MSG_MEETING_ROOM,
                                remoteMessage.data[Constants.REMOTE_MSG_MEETING_ROOM])
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    } else {
                        showNotification(remoteMessage)
                    }
                }
                Constants.REMOTE_MSG_INVITATION_RESPONSE -> {
                    Constants.stopPlay()
                    if ( remoteMessage.data[Constants.REMOTE_MSG_INVITATION_RESPONSE]
                        == Constants.REMOTE_MSG_INVITATION_CANCELLED)
                            sendBroadcast(Intent(this,
                                NotificationReceiver::class.java)
                            .putExtra("callAnswer", "close"))
                    LocalBroadcastManager.getInstance(applicationContext)
                        .sendBroadcast(Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                        .putExtra(
                            Constants.REMOTE_MSG_INVITATION_RESPONSE,
                            remoteMessage.data[Constants.REMOTE_MSG_INVITATION_RESPONSE]
                        ))
                }
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag", "RemoteViewLayout")
    private fun showNotification(remoteMessage: RemoteMessage) =
        CoroutineScope(Dispatchers.IO).launch {
            sendMessageToFMS(
                type = "answer",
                token = remoteMessage.data[Constants.REMOTE_MSG_INVITER_TOKEN]!!,
                callAnswer = "тыква"
            )
            createMessageChannel(this@FBMessageService)
          createNotification(this@FBMessageService, remoteMessage)
        }
}

