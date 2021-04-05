package com.pleiades.pleione.kakaoprofile.ui.toast;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.pleiades.pleione.kakaoprofile.DeviceController;
import com.pleiades.pleione.kakaoprofile.R;
import com.pleiades.pleione.kakaoprofile.prefs.PrefsController;

import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_REMOVE_ADS;
import static com.pleiades.pleione.kakaoprofile.ui.toast.ToastConfig.HIGH_POSITION;

public class ToastController {
    private Context context;

    public ToastController(Context context) {
        this.context = context;
    }

    private Toast createCustomToast(int duration, String message) {
        // initialize toast
        Toast toast = Toast.makeText(context, message, duration);

        // case android R
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            // set background color
            View view = toast.getView();
            view.getBackground().setColorFilter(context.getResources().getColor(R.color.colorKakaoBlackText, null), PorterDuff.Mode.SRC_ATOP);

            // set text view
            TextView textView = (TextView) view.findViewById(android.R.id.message);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextColor(Color.WHITE);
        }

        return toast;

//        Toast toast = new Toast(context);
//
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View toastView = layoutInflater.inflate(R.layout.toast_custom, (ViewGroup)((Activity)context).findViewById(R.id.toast_layout));
//
//        TextView toastMessage = toastView.findViewById(R.id.toast_message);
//        toastMessage.setText(message);
//
//        toast.setDuration(duration);
//        toast.setView(toastView);
    }

    public void showCustomToast(int position, int duration, int additionalOffsetId, int stringResourceId) {
        // set message
        String message = context.getString(stringResourceId);

        // toast initialize
        Toast toast = createCustomToast(duration, message);

        // default offset pixel
        int i = Resources.getSystem().getIdentifier("toast_y_offset", "dimen", "android");
        int defaultOffset = context.getResources().getDimensionPixelSize(i);
        int additionalOffset = context.getResources().getDimensionPixelSize(additionalOffsetId);

        if (position == HIGH_POSITION)
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, defaultOffset + additionalOffset);
        else {
            // add ad size
            if(!PrefsController.getBoolean(KEY_REMOVE_ADS))
                additionalOffset = additionalOffset + dpToPx(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, DeviceController.getAdWidth()).getHeight());

            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, defaultOffset + additionalOffset);
        }

        // show
        toast.show();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
