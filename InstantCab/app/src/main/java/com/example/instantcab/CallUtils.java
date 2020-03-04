package com.instantcab.example.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.instantcab.example.R;

public class CallUtils {

    public static void callPhone(Context context,String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }
    public static void sendMail(Context context,String address) {
        try {
            String mailto = address;
            String[] tos = new String[]{mailto};

            Intent emailintent = new Intent(
                    Intent.ACTION_SEND);
            emailintent.putExtra(Intent.EXTRA_EMAIL, tos);
            emailintent.setType("text/html");
//            emailintent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            emailintent.putExtra(Intent.EXTRA_TEXT, "deviceInfo");
            context.startActivity(Intent.createChooser(emailintent,
                    context.getString(R.string.choose_email)));
        } catch (Exception e) {
            //ignore
            Log.e("nmlogs", "sendEmail exception: " + e.getMessage());
        }
    }
}
