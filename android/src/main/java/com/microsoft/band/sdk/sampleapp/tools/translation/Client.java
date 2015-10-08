package com.microsoft.band.sdk.sampleapp.tools.translation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import com.microsoft.band.sdk.sampleapp.tools.RequestHelper;
import com.microsoft.band.sdk.sampleapp.tools.StoreHelper;
import com.microsoft.band.sdk.sampleapp.tools.Util;
import com.microsoft.band.sdk.sampleapp.tools.XmlParserHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by wangyue on 15/10/5.
 */
public class Client {
    private Context mCtx;
    private static Client mInstance;

    private Client(Context context) {
        this.mCtx = context;
    }

    public static synchronized Client getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Client(context);
        }
        return mInstance;
    }

    public void translate(String inputStr) {
        new TranslateTask(inputStr).execute();
    }

    private void translateRequest(String inputStr) {

        String translateMethodUrl = this.buildTranslateUrl("", "en", inputStr);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, translateMethodUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String message = XmlParserHelper.getInstance().parse(response);
                        Pair<String, String> pair = Util.parseMessage(message);
                        Util.sendToBand(mCtx, pair.first, pair.second);
                    }
                },
                new ErrorListener()
        ) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();

                String accessToken = StoreHelper.getInstance(mCtx).getByKey("accessToken");

                if (accessToken.isEmpty()) {
                    return params;
                }

                params.put("Authorization", "Bearer " + accessToken);

                return params;
            }
        };

        RequestHelper.getInstance(mCtx).addToRequestQueue(stringRequest);
    }

    private class TranslateTask extends AsyncTask<Void, Void, Void> {
        String inputUrl;

        public TranslateTask(String inputUrl) {
            this.inputUrl = inputUrl;
        }

        @Override
        protected Void doInBackground(Void... params) {

            RequestFuture<String> future = RequestFuture.newFuture();

            Response.ErrorListener errorListener = new ErrorListener();

            StringRequest request = new StringRequest(Request.Method.POST, Config.AUTHURL, future, future) {

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {

                    StringBuffer httpPostBody = new StringBuffer();

                    httpPostBody.append("grant_type="+Config.GRANTTYPE+"&scope="+Config.SCOPEURL);

                    try {
                        httpPostBody.append("&client_id=" + URLEncoder.encode(Config.CLIENTID, "UTF-8")+ "&client_secret=" + URLEncoder.encode(Config.CLIENTSECRET, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        Log.e("ERROR", "exception", e);
                        return null;
                    }
                    return httpPostBody.toString().getBytes();
                }
            };

            if (AccessTokenAuthentication.getInstance(mCtx).isTokenExpired()) {

                RequestHelper.getInstance(mCtx).addToRequestQueue(request);

                try {
                    String response = future.get(Config.TIMEOUT, TimeUnit.SECONDS);

                    Log.d("response", response);

                    AccessTokenAuthentication.getInstance(mCtx).updateToken(response);

                } catch (InterruptedException e) {

                    Log.e("ERROR", "Auth api call interrupted.", e);
                    errorListener.onErrorResponse(new VolleyError(e));
                    return null;

                } catch (TimeoutException e) {

                    Log.e("ERROR", "Auth api call timed out.", e);
                    Util.appendToUI(mCtx, "Auth api call timed out\n");
                    errorListener.onErrorResponse(new VolleyError(e));
                    return null;

                } catch (ExecutionException e) {

                    Log.e("ERROR", "Auth api call error.", e);
                    return null;
                }

                translateRequest(inputUrl);

            } else {

                translateRequest(inputUrl);

            }

            return null;
        }
    }

    private String buildTranslateUrl(String from, String to, String inputStr) {
        String fromLanguage = from;
        String toLanguage   = to;
        String category     = "general";

        String translateMethodUrl = null;

        try {
            translateMethodUrl = "http://api.microsofttranslator.com/V2/Http.svc/Translate?text=" + URLEncoder.encode(inputStr, "UTF-8") + "&from=" + fromLanguage + "&to=" + toLanguage + "&category=" + category;
        } catch (UnsupportedEncodingException e) {
            Log.e("ERROR", "exception", e);
        }
        return translateMethodUrl;
    }
}