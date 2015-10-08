package com.microsoft.band.sdk.sampleapp.tools.translation;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by wangyue on 15/10/6.
 */
public class ErrorListener implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {

        Log.e("ERROR", "request error", error);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < error.networkResponse.data.length && i < 200; i++) {
            sb.append((char)error.networkResponse.data[i]);
        }
        System.out.print(sb);
    }
}
