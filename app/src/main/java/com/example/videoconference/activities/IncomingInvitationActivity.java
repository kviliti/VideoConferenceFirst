package com.example.videoconference.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.videoconference.R;
import com.example.videoconference.network.ApiClient;
import com.example.videoconference.network.ApiService;
import com.example.videoconference.utilities.Constants;
import com.example.videoconference.utilities.PreferenceManager;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingInvitationActivity extends AppCompatActivity implements SensorEventListener {
    private String meetingType = null;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;
    private static final int SENSOR_SENSITIVITY = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            field = PowerManager.class.getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());
        setContentView(R.layout.activity_incoming_invitation);
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);
        JSONObject body = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray tokens = new JSONArray();
        String token = getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN);
        tokens.put(token);
        try {
            data.put(Constants.REMOTE_MSG_TYPE , "answer");
            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
            ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                    Constants.getRemoteMessageHeaders(), body.toString()
            ).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {}
                @Override
                public void onFailure(Call<String> call, Throwable t) {}
            });
        } catch (Exception exception) {


        }

        if (meetingType != null) {
            if (meetingType.equals("video")) {
                imageMeetingType.setImageResource(R.drawable.ic_video);
            } else {
                imageMeetingType.setImageResource(R.drawable.ic_audio_wh);
            }
        }

        TextView textFirstChar = findViewById(R.id.textFirstChar);
        TextView textUsername = findViewById(R.id.textUsername);
        TextView textEmail = findViewById(R.id.textEmail);
        String firstName = getIntent().getStringExtra(Constants.KEY_FIRST_NAME);
        String lastName = getIntent().getStringExtra(Constants.KEY_LAST_NAME);
        if (firstName != null) {
            textFirstChar.setText(firstName.substring(0, 1));
        }
        textUsername.setText(String.format(
                "%s %s",
                firstName,
                getIntent().getStringExtra(Constants.KEY_LAST_NAME)
        ));
        textEmail.setText(getIntent().getStringExtra(Constants.KEY_EMAIL));
        ImageView imageAcceptInvitation = findViewById(R.id.imageAcceptInvitation);
        imageAcceptInvitation.setOnClickListener(v -> {
            Constants.stopPlay();
            sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                    getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
        });
        ImageView imageRejectInvitation = findViewById(R.id.imageRejectInvitation);
        imageRejectInvitation.setOnClickListener(v -> {
            Constants.stopPlay();
            sendInvitationResponse(
                Constants.REMOTE_MSG_INVITATION_REJECTED,
                getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
        });
    }

    private void sendInvitationResponse(String type, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);
            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);

        } catch (Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void sendRemoteMessage(String remoteMessageBody, String type) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                        try {
                            PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
                            String firstName = preferenceManager.getString(Constants.KEY_FIRST_NAME);
                            String lastName = preferenceManager.getString(Constants.KEY_LAST_NAME);
                            String username = firstName + " " + lastName;
                            JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
                            userInfo.setDisplayName(username);
                            URL serverURL = new URL("https://meet.jit.si");
                            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                            builder.setServerURL(serverURL);
                            builder.setWelcomePageEnabled(false);
                            builder.setUserInfo(userInfo);
                            builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));
                            if (meetingType.equals("audio")) {
                                builder.setAudioOnly(true);
                            }
                            JitsiMeetActivity.launch(IncomingInvitationActivity.this, builder.build());
                            finish();
                        } catch (Exception exception) {
                            Toast.makeText(IncomingInvitationActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(IncomingInvitationActivity.this, "Завершить звонок", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(IncomingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(IncomingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
                    Toast.makeText(context, "Вызов отклонён", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(IncomingInvitationActivity.this, Main.class));
                    finish();
                }
            }
        }
    };

    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (sensorEvent.values[0] <= SENSOR_SENSITIVITY) {
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}