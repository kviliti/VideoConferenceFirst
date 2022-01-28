package com.example.videoconference.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberImagePainter
import com.example.videoconference.R
import com.example.videoconference.utilities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.HashMap

class SettingsActivity : AppCompatActivity() {
    private var imageUriState = mutableStateOf<Uri?>(null)
    var selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultPhoto ->
            if (resultPhoto.resultCode == Activity.RESULT_OK && resultPhoto.data != null) {
                imageUriState.value = CropImage.getActivityResult(resultPhoto.data).uri
                PreferenceManager(applicationContext).putString("avatar", imageUriState.value.toString() )
                CoroutineScope(Dispatchers.IO).launch {
                    putPhotoToStorage(CropImage.getActivityResult(resultPhoto.data).uri,
                        PreferenceManager(applicationContext).getString(Constants.KEY_USER_ID)){ url ->
                        val user = HashMap<String, Any>()
                        user["avatar"] = url
                        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                            .document(PreferenceManager(applicationContext).getString(Constants.KEY_USER_ID))
                            .update(user)
                    }
                }
            }
        }

    @ExperimentalFoundationApi
    override fun onResume() {
        super.onResume()
        supportActionBar?.title = "Настройки"
        setContent {
            Settings()
        }
    }

    @ExperimentalFoundationApi
    @Composable
    fun Settings() {
        val pref = PreferenceManager(applicationContext)
        var name by remember { mutableStateOf("") }
        var secondName by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imState = if (imageUriState.value != null)
                rememberImagePainter(data = imageUriState.value)
            else if (pref.getString("avatar").isNullOrEmpty())
                painterResource(id = R.drawable.anonymous_a)
            else rememberImagePainter(data = pref.getString("avatar").toUri())
            TopAppBar(contentColor = Color.White, backgroundColor = Color.Black,
                contentPadding = PaddingValues(start = 16.dp)) {
                Text(text = "Настройки", fontSize = 20.sp)
            }
            Box(Modifier.padding(top = 20.dp)) {
                Image(
                    painter = imState,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable { changePhotoUser(this@SettingsActivity) },
                    alignment = Alignment.Center,
                    contentScale = ContentScale.FillWidth
                )
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(text = "Имя") },
                label = { Text(text = pref.getString(Constants.KEY_FIRST_NAME)) },
                modifier = Modifier.padding(bottom = 6.dp, top = 10.dp)
            )
            OutlinedTextField(value = secondName, onValueChange = {
                secondName = it
            }, placeholder = { Text(text = "Фамилия")
            }, label = { Text(text = pref.getString(Constants.KEY_LAST_NAME)) },
                modifier = Modifier.padding(bottom = 12.dp))
            Button(onClick = {
                val user = HashMap<String, Any>()
                user[Constants.KEY_FIRST_NAME] = name
                user[Constants.KEY_LAST_NAME] = secondName
                if (name.isNotEmpty() && secondName.isNotEmpty())
                    FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                        .document(pref.getString(Constants.KEY_USER_ID))
                        .update(user).addOnCompleteListener {
                            showToast("Данные обновлены", this@SettingsActivity)
                            pref.putString(Constants.KEY_LAST_NAME, secondName)
                            pref.putString(Constants.KEY_FIRST_NAME, name)
                            pref.putString(Constants.KEY_FIRST_NAME, name)
                        }
                else showToast("Введите имя и фамилию", this@SettingsActivity)
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black, contentColor = Color.White)) {
                Text(text = "Сохранить")
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, Main::class.java))
        finish()
    }
}