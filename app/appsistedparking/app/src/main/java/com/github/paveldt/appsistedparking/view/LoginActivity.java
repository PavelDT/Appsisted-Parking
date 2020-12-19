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
import android.widget.TextView;

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
}