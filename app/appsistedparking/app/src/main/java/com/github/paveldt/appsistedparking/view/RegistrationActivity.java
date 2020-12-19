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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.paveldt.appsistedparking.R;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // initialise login link to navigate back to registration
        initLoginLink();
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
