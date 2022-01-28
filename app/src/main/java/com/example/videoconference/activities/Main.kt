package com.example.videoconference.activities



import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.videoconference.R
import com.example.videoconference.models.MainViewModel
import com.example.videoconference.models.UserModel
import com.example.videoconference.ui.*
import com.example.videoconference.utilities.*
import com.example.videoconference.utilities.Constants.stateApp
import com.google.firebase.firestore.DocumentSnapshot

var listChecked = mutableListOf<UserModel>()

class Main : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    override fun onResume() {
        super.onResume()
        stateApp = 1
        sendFCMTokenToDatabase(applicationContext)
        setContent { ListUsers() }
    }

    @SuppressLint("UnrememberedMutableState")
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun ListUsers() {
        val list by mainViewModel.listUsers.observeAsState()
        var heightCard by remember { mutableStateOf(0.dp) }
        var visibleIcon by remember { mutableStateOf(false) }
        var visibleCheck by remember { mutableStateOf(false) }
        val mapChecked by remember { mutableStateOf(hashMapOf<String, Boolean>()) }
        val ass by animateDpAsState(
            if (visibleCheck) 50.dp
            else 0.dp
        )
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            Visibility(visible = visibleIcon) {
                Box(Modifier.padding(end = 60.dp, bottom = 60.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ass),
                        null, modifier = Modifier
                            .size(60.dp)
                            .clickable {
                                onMultipleUsersAction(context = applicationContext)
                            }
                    )
                }
            }
        }
        Column(
            Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TopAppBar(
                backgroundColor = Black,
                contentColor = White,
                contentPadding = PaddingValues(start = 16.dp)
            ) { Text(text = "Абоненты", fontSize = 20.sp) }
            LazyColumn(
                content = {
                    items(list as List<DocumentSnapshot>) { item ->
                        var check by remember {
                            mutableStateOf(false)
                        }
                        val user = remember { mutableStateOf(getUser(item = item)) }
                        if (mapChecked[item.id] == null){
                            mapChecked[item.id] = false
                            check = mapChecked[item.id]!!
                        }
                        if (check) {
                            if (!listChecked.contains(user.value))
                                listChecked.add(user.value)
                        } else listChecked.remove(user.value)
                        visibleCheck = listChecked.size != 0
                        visibleIcon = listChecked.size > 1
                        user.value.apply {
                            if (user_id != getValuePref(applicationContext, "user_id"))
                                CellCard(onLongClick = {
                                    mapChecked.clear()
                                    listChecked.clear()
                                    visibleCheck = !visibleCheck
                                    mapChecked[item.id] = !visibleIcon
                                    check = !check
                                }, height = { heightCard = it }) {
                                    ChangePhoto(fullName = first_name,
                                        photoUrl = avatar,
                                        onClick = {}, visible = visibleCheck,
                                        inRow = {
                                            Visibility(visible = visibleCheck) {
                                                Checkbox(
                                                    checked = check,
                                                    onCheckedChange = { _ ->
                                                        check = !check
                                                        mapChecked[item.id] = !mapChecked[item.id]!!

                                                    }, modifier = Modifier.size(ass)
                                                )
                                            }
                                            NameBlockMainList(
                                                name = first_name,
                                                lastName = last_name
                                            )
                                            Box(Modifier.padding(end = 33.dp)) {
                                                Icon(
                                                    Icons.Default.Call, contentDescription = null,
                                                    Modifier
                                                        .size(if(!visibleCheck) heightCard / 3 else 0.dp)
                                                        .clickable {
                                                            initiateMeeting(
                                                                user.value,
                                                                "audio",
                                                                applicationContext
                                                            )
                                                        }
                                                )
                                            }
                                            Icon(
                                                Icons.Default.VideoCall, contentDescription = null,
                                                Modifier
                                                    .size(if(!visibleCheck) heightCard / 3 else 0.dp)
                                                    .clickable {
                                                        initiateMeeting(
                                                            user.value,
                                                            "video",
                                                            applicationContext
                                                        )
                                                    }
                                            )
                                        }
                                    )
                                }
                        }
                    }
                }, modifier = Modifier.padding(top = 15.dp)
            )
        }
        Dropdown(settings = {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }, signOut = {
            signOut(applicationContext, this)
        })
    }

    override fun onPause() {
        super.onPause()
        Constants.stateApp = 0
    }
}