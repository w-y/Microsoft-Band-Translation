package com.microsoft.band.sdk.sampleapp.tools.translation;

import android.content.Context;
import android.util.Log;

import com.microsoft.band.sdk.sampleapp.tools.StoreHelper;

import org.json.JSONException;
import org.json.JSONObject;
;
import java.util.Calendar;


/**
 * Created by wangyue on 15/9/20.
 */
public class AccessTokenAuthentication {
    private Context mCtx;
    private static AccessTokenAuthentication mInstance;

    private AccessTokenAuthentication(Context context) {
        this.mCtx = context;
    }

    public static synchronized AccessTokenAuthentication getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AccessTokenAuthentication(context);
        }
        return mInstance;
    }

    public void updateToken(String response) {
        try {
            JSONObject jObject = new JSONObject(response);
            String accessToken = jObject.getString("access_token");
            String expiresIn = jObject.getString("expires_in");

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, Integer.parseInt(expiresIn));
            Long expiredDate  = calendar.getTimeInMillis();

            StoreHelper store = StoreHelper.getInstance(mCtx);
            store.put("accessToken", accessToken);
            store.put("expiredDate", expiredDate.toString());

        } catch (JSONException e) {
            Log.e("ERROR", "exception", e);
        }
    }

    public boolean isTokenExpired() {

        StoreHelper store = StoreHelper.getInstance(mCtx);
        String expiredDate = store.getByKey("expiredDate");
        Calendar calendar = Calendar.getInstance();

        if (!expiredDate.isEmpty()) {
            Long expireMillis = Long.parseLong(expiredDate);
            Long timeInMillis = calendar.getTimeInMillis();

            if (timeInMillis > expireMillis) {
                return true;
            }
            return false;
        }
        return true;
    }
}