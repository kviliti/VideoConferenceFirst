package com.example.videoconference.utilities



import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoconference.R
import com.example.videoconference.activities.OutgoingInvitationActivity
import com.example.videoconference.activities.SettingsActivity
import com.example.videoconference.activities.SignInActivity
import com.example.videoconference.activities.listChecked
import com.example.videoconference.models.User
import com.example.videoconference.models.UserModel
import com.example.videoconference.network.ApiClient
import com.example.videoconference.network.ApiService
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun changePhotoUser(context: SettingsActivity) = CoroutineScope(Dispatchers.IO).launch {
    context.selectImageLauncher.launch(
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .getIntent(context)
    )
}

fun putPhotoToStorage(
    uri: Uri,
    id: String,
    function: (String) -> Unit
) {
    val path = FirebaseStorage.getInstance().reference.child("avatars")
        .child(id)
    path.putFile(uri)
        .addOnSuccessListener {
            path.downloadUrl.addOnSuccessListener { urine ->
                function(urine.toString())
            }
        }
}

fun getValuePref(applicationContext: Context, key: String): String{
    return if (!PreferenceManager(applicationContext).getString(key).isNullOrEmpty())
        PreferenceManager(applicationContext).getString(key) else ""
}

fun getCurrentUser(applicationContext: Context): UserModel{
    return UserModel(getValuePref(applicationContext, "user_id"),
        getValuePref(applicationContext, "first_name"),
        getValuePref(applicationContext, "last_name"),
        getValuePref(applicationContext, "avatar"),
        getValuePref(applicationContext, "email"),
        getValuePref(applicationContext, "fcm_token"),
        getValuePref(applicationContext, "password"))
}

fun getUser(item: DocumentSnapshot): UserModel{
    return UserModel(
        user_id = item.id,
        first_name = if (item.getString("first_name") != null) item.getString("first_name")!! else "",
        last_name = if (item.getString("last_name") != null) item.getString("last_name")!! else "",
        email = if (item.getString("email") != null) item.getString("email")!! else "",
        token = if (item.getString("fcm_token") != null) item.getString("fcm_token")!! else "",
        avatar = if (item.getString("avatar") != null) item.getString("avatar")!! else "",
        password = if (item.getString("password") != null) item.getString("password")!! else ""
    )
}

fun sendFCMTokenToDatabase(context: Context) {
    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task->
        if (task.isSuccessful && task.result != null) {
            val database = FirebaseFirestore.getInstance()
            val documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                    getValuePref(context, "user_id")
                )
            documentReference.update(Constants.KEY_FCM_TOKEN, task.result!!.token)
                .addOnFailureListener {
                    showToast("Ошибка при получении токена", context)
                }
        }
    }
}

fun initiateMeeting(user: UserModel, type: String, context: Context) {
    if (user.token.isNullOrEmpty()) {
        showToast("Невозможно позвонить" + user.first_name + " " + user.last_name, context)
    } else {
        val intent = Intent(context, OutgoingInvitationActivity::class.java)
            .addFlags(FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("user", "user")
        intent.putExtra("type", type)
        intent.putExtra("firstName", user.first_name)
        intent.putExtra("avatar", user.avatar)
        intent.putExtra("lastName", user.last_name)
        intent.putExtra("email", user.email)
        intent.putExtra("token", user.token)
        context.startActivity(intent)
    }
}

fun signOut(context: Context, activity: Activity) {
    showToast("Выход...", context)
    val documentReference =
        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS).document(
            getValuePref(context, "user_id")
        )
    val updates = HashMap<String, Any>()
    updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
    documentReference.update(updates)
        .addOnSuccessListener {
            val editor =
                context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,
                    AppCompatActivity.MODE_PRIVATE
                ).edit()
            editor.clear()
            editor.apply()
            context.startActivity(Intent(context, SignInActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK))
            activity.finish()
        }
        .addOnFailureListener { showToast("Невозможно выйти", context) }
}

fun onMultipleUsersAction(context: Context) {
    val listArray = ArrayList<User>()
    listChecked.forEach {
        val user = User()
        user.firstName = it.first_name
        user.email = it.email
        user.lastName = it.last_name
        user.token = it.token
        listArray.add(user)
        if (listArray.size == listChecked.size)
        {
            val intent = Intent(context, OutgoingInvitationActivity::class.java).addFlags(
                FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("selectedUsers", Gson().toJson(listArray))
            intent.putExtra("type", "audio")
            intent.putExtra("isMultiple", true)
            context.startActivity(intent)
        }
    }
}

fun sendMessageToFMS(token: String, type: String, callAnswer: String){
    val tokens = JSONArray()
    tokens.put(token)
    val body = JSONObject()
    val data = JSONObject()
    data
        .put(
            Constants.REMOTE_MSG_TYPE,
            type)
        .put(
            Constants.REMOTE_MSG_INVITATION_RESPONSE,
            callAnswer)
    body
        .put(Constants.REMOTE_MSG_DATA, data)
        .put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)
    ApiClient.getClient()?.create(ApiService::class.java)?.sendRemoteMessage(
        Constants.getRemoteMessageHeaders(), body.toString()
    )?.enqueue(object : Callback<String?> {
        override fun onResponse(call: Call<String?>, response: Response<String?>) {}
        override fun onFailure(call: Call<String?>, t: Throwable) {}
    })
}

fun closeNotification(context: Context){
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager
    notificationManager.cancel(R.string.default_notification_channel_id)
}

