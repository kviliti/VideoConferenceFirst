package com.example.videoconference.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.videoconference.activities.JitsiActivity
import com.example.videoconference.utilities.Constants
import com.example.videoconference.utilities.closeNotification
import com.example.videoconference.utilities.sendMessageToFMS


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.getStringExtra("callAnswer")) {
            "cancel" -> {
                sendMessageToFMS(
                    intent.getStringExtra
                        (Constants.REMOTE_MSG_INVITATION_RESPONSE)!!,
                    type = Constants.REMOTE_MSG_INVITATION_RESPONSE,
                    Constants.REMOTE_MSG_INVITATION_REJECTED
                )
                closeNotification(context!!)
            }
            "getCall" -> {
                context?.startActivity(
                    Intent(
                        context,
                        JitsiActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("meetingType", intent.getStringExtra("meetingType"))
                        .putExtra("meetingRoom", intent.getStringExtra("meetingRoom"))
                )
                sendMessageToFMS(
                    intent.getStringExtra
                        (Constants.REMOTE_MSG_INVITATION_RESPONSE)!!,
                    type = Constants.REMOTE_MSG_INVITATION_RESPONSE,
                    Constants.REMOTE_MSG_INVITATION_ACCEPTED
                )
                closeNotification(context!!)
            }
            "close" -> closeNotification(context!!)
        }
    }
}