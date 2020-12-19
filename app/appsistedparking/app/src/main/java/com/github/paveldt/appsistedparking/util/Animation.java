package com.github.paveldt.appsistedparking.util;

import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;

public class Animation {
    public static void slideAnimation(Window w) {
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.LEFT);
        slide.setDuration(400);
        slide.setInterpolator(new DecelerateInterpolator());
        w.setExitTransition(slide);
        w.setEnterTransition(slide);
    }
}
