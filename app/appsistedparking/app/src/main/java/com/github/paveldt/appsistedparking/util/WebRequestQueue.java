package com.github.paveldt.appsistedparking.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public final class WebRequestQueue {

    // slightly unusual, WebRequestQueue acts as a wrapper for the volley library's
    // RequestQueue object that needs to be shared through the application.
    private static RequestQueue instance;

    // encorficng singleton requires private constructor
    private WebRequestQueue() {}

    /**
     * Returns an instance of the volley singleton queue for web requests
     * @param context
     * @return RequestQueue - volley library object for holding web requests
     */
    public static RequestQueue getInstance(Context context) {

        // init singleton for queue if its null
        if (instance == null) {
            instance = Volley.newRequestQueue(context);
        }

        return instance;
    }
}
