package com.github.paveldt.appsistedparking.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.android.volley.toolbox.Volley;
import com.github.paveldt.appsistedparking.R;
import com.github.paveldt.appsistedparking.util.Animation;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Animation.slideAnimation(getWindow());
        setContentView(R.layout.activity_authentication);

        // initialise registration link
        initRegisterLink();
        // init login button
        initLoginButton();
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
                // Instantiate the RequestQueue.
                // todo -- reuse the request queue instead of re-instantiating it
                //         every single time a user registers
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                // get username & password from controls
                EditText username = findViewById(R.id.editTextUsername);
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
                                // SUCCESS - the user loged in correctly
                                if (result.toLowerCase().equals("true")) {

                                    // move to the parking activity
                                    Intent parkingIntent = new Intent(LoginActivity.this, ParkingActivity.class);
                                    startActivity(parkingIntent);

                                    // todo -- add a session of some kind so the user doesn't need
                                    // todo -- to constantly log in on every app start

                                    // terminate this activity
                                    finish();
                                } else {
                                    String msg = "Failed to log in - unrecognised username / password combination.";
                                    Toast.makeText(v.getContext(), msg, Toast.LENGTH_LONG).show();
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
}