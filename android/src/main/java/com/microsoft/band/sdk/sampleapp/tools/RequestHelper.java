package com.microsoft.band.sdk.sampleapp.tools;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by wangyue on 15/10/5.
 */

public class RequestHelper {
    private static RequestHelper mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private RequestHelper(Context context) {
        this.mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestHelper(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
