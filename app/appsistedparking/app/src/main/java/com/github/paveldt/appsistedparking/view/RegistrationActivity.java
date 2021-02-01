package com.github.paveldt.appsistedparking.view;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.paveldt.appsistedparking.R;
import com.github.paveldt.appsistedparking.model.User;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // initialise login link to navigate back to registration
        initLoginLink();
        initRegisterButton();
    }

    private void initRegisterButton() {
        Button registerButton = findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                User u = new User("", "");
//                String result = u.register();
//                Toast.makeText(v.getContext(), result, Toast.LENGTH_LONG).show();

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                // get username
                EditText username = findViewById(R.id.editTextUsername);
                EditText password = findViewById(R.id.editTextPassword);
                String url ="http://10.0.2.2:8080/user/register/?username=" +
                        username.getText().toString().trim() +
                        "&password=" + password.getText().toString().trim();

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String result) {
                                // Display the response
                                Toast.makeText(v.getContext(), result, Toast.LENGTH_LONG).show();
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
     * Initialises a text view as a partly-clickable link to register a new account.
     */
    private void initLoginLink() {
        TextView loginLink = findViewById(R.id.textViewLoginLink);
        String txt = loginLink.getText().toString();

        // only modifying markup, not the text itself
        SpannableString spannableStr = new SpannableString(txt);
        // implement the onClick functionality for the clickable part of the link
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // animate back to the login activity
                Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegistrationActivity.this);
                startActivity(loginIntent, options.toBundle());

                // terminate this activity
                finish();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                // change the colour of the span, aka the clickable link.
                ds.setColor(Color.DKGRAY);
            }
        };

        // set the part of the link that should be clickable.
        // Already Registered? Go to Login.
        spannableStr.setSpan(clickableSpan, 26, 31, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        // update the text of the link to highlight the clickable component
        loginLink.setText(spannableStr);
        // enables clicking on the link to trigger the "onClick" functionality
        loginLink.setMovementMethod(LinkMovementMethod.getInstance());
    }


}
