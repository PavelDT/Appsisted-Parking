package com.github.paveldt.appsistedparking.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.paveldt.appsistedparking.R;
import com.github.paveldt.appsistedparking.util.Animation;
import com.github.paveldt.appsistedparking.util.JSONUtil;
import com.github.paveldt.appsistedparking.util.WebRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    // web request queue
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Animation.slideAnimation(getWindow());
        setContentView(R.layout.activity_authentication);

        // Instantiate the RequestQueue.
        queue = WebRequestQueue.getInstance(this);

        // initialise registration link
        initRegisterLink();
        // init login button
        initLoginButton();

        // as the login activity is the main entry point to the app, the notification
        // channel is initialised here
        createNotificationChannel();
    }

    /**
     * Initialises a text view as a partly-clickable link to register a new account.
     */
    private void initRegisterLink() {
        TextView registrationLink = findViewById(R.id.textViewRegisterLink);
        String txt = registrationLink.getText().toString();

        // only modifying markup, not the text itself
        SpannableString spannableStr = new SpannableString(txt);
        // implement the onClick functionality for the clickable part of the link
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // animate to the registration activity
                Intent registrationIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this);
                startActivity(registrationIntent, options.toBundle());

                // important, the login activity cannot finish here as a user might navigate back
                // to it after signing out or choosing to not register but login instead.
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                // change the colour of the span, aka the clickable link.
                ds.setColor(Color.DKGRAY);
            }
        };

        // set the part of the link that should be clickable.
        spannableStr.setSpan(clickableSpan, 23, 31, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        // update the text of the link to highlight the clickable component
        registrationLink.setText(spannableStr);
        // enables clicking on the link to trigger the "onClick" functionality
        registrationLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Initialise login button for checking if a user exists and allowing access to the parking view.
     */
    private void initLoginButton() {
        Button loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // get username & password from controls
                final EditText username = findViewById(R.id.editTextUsername);
                EditText password = findViewById(R.id.editTextPassword);

                // build request url that requires username and password as params
                String url = "http://10.0.2.2:8080/user/login/?username=" +
                        username.getText().toString().trim() +
                        "&password=" + password.getText().toString().trim();


                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String result) {
                                try {
                                    final JSONObject user = JSONUtil.jsonStrToObject(result);
                                    // SUCCESS - the user logged in correctly
                                    if (user.getString("username").equals(username.getText().toString().trim())) {

                                        // move to the parking activity
                                        Intent parkingIntent = new Intent(LoginActivity.this, ParkingActivity.class);
                                        // pass param for user to parking activity
                                        parkingIntent.putExtra("username", username.getText().toString().trim());
                                        parkingIntent.putExtra("balance", user.getDouble("balance") + "");
                                        // update preference settings
                                        parkingIntent.putExtra("location", user.getString("settingLocation"));
                                        parkingIntent.putExtra("site", user.getString("settingSite"));
                                        startActivity(parkingIntent);

                                        // terminate this activity
                                        finish();
                                    } else {
                                        String msg = "Failed to log in - unrecognised username / password combination.";
                                        Toast.makeText(v.getContext(), msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException jex) {
                                    Log.e("Login", "Failed to handle user login response");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(v.getContext(), "<--ERROR--> " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });
    }

    /**
     * Needed for the ability to issue notifications to the android OS
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // channel name must be unique and can only be create once per device.
            String channelId = getResources().getString(R.string.notification_channel);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelId, importance);
            channel.setDescription("");
            // mute the channel, this removes oddities with TTS
            channel.setSound(null, null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviours after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.deleteNotificationChannel(channelId);
            notificationManager.createNotificationChannel(channel);
        }
    }
}