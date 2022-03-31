package com.abgames.tictache.Tools;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorRes;

public class Tools {

    public static void setSystemBarColor(Activity act, @ColorRes int color) {
        Window window = act.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(act.getResources().getColor(color));
    }


    public static void toggleArrow(boolean show, View view) {
        toggleArrow(show, view, true);
    }

    public static void toggleArrow(boolean show, View view, boolean delay) {
        if (show) {
            view.animate().setDuration(delay ? 200 : 0).rotation(180);
        } else {
            view.animate().setDuration(delay ? 200 : 0).rotation(0);
        }
    }

}
