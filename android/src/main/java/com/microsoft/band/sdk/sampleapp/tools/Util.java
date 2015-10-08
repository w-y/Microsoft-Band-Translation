package com.microsoft.band.sdk.sampleapp.tools;

import android.content.Context;
import android.util.Pair;

import com.microsoft.band.sdk.sampleapp.BandNotificationToEn;
import com.microsoft.band.sdk.sampleapp.BandNotificationToEnActivity;

import net.sourceforge.pinyin4j.PinyinHelper;

public class Util {

    public static String toPinyin(String strMsg) {
        StringBuffer buffer = new StringBuffer();
        char[] text = strMsg.toCharArray();

        for (int i = 0; i < text.length; i++) {
            if (Character.toString(text[i]).matches("[\u4E00-\u9FA5]+")) {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(text[i]);

                if (pinyinArray.length > 0) {
                    buffer.append(pinyinArray[0]);
                }

                buffer.append(" ");
            } else {
                buffer.append(text[i]);
            }
        }

        return buffer.toString().trim();
    }

    public static Pair<String, String> parseMessage(String strMsg) {

        int sepIndex = strMsg.indexOf(":");

        String strContent = sepIndex >= 0 ? strMsg.substring(sepIndex + 1) : strMsg;
        String strTitle = sepIndex >= 0 ? strMsg.substring(0, sepIndex) : "Notification";

        return new Pair<String, String>(strTitle, strContent);
    }

    public static void sendToBand(Context context, String title, String message) {
        BandNotificationToEn mApp = (BandNotificationToEn) context.getApplicationContext();
        BandNotificationToEnActivity mActivity = (BandNotificationToEnActivity) mApp.getCurrentActivity();

        if (mActivity != null) {
            mActivity.sendToBand(title, message);
        }
    }

    public static void appendToUI(Context context, String string) {
        BandNotificationToEn mApp = (BandNotificationToEn) context.getApplicationContext();
        BandNotificationToEnActivity mActivity = (BandNotificationToEnActivity) mApp.getCurrentActivity();

        if (mActivity != null) {
            mActivity.appendToUI(string);
        }
    }

}