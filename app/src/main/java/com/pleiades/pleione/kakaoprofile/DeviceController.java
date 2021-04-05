package com.pleiades.pleione.kakaoprofile;

import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.pleiades.pleione.kakaoprofile.ui.activity.main.MainActivity;

import static android.content.Context.WINDOW_SERVICE;

public class DeviceController {
    public static int widthMax = 0;

    public static int getWidthMax() {
        if (widthMax > 0)
            return widthMax;
        else {
            WindowManager windowManager = (WindowManager) MainActivity.applicationContext.getSystemService(WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }
    }

    public static int getAdWidth(){
        WindowManager windowManager = (WindowManager) MainActivity.applicationContext.getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        return (int) (widthPixels / density);
    }
}
