package edu.upc.eetac.dsa.pokeetacgo.serviceLibrary;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by ericmassip on 15/12/16.
 */

public class PokEETACRestClient {
    private static final String TAG="PokEETAC";
    private static String BASE_URL = "http://147.83.7.207:8080/pokeetac";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static StringEntity getObjectAsStringEntity(Object object) {
        StringEntity entity = null;
        try {
            entity = new StringEntity(new Gson().toJson(object));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context, String url, StringEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), entity, contentType, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void sayHello() {
        client.get(BASE_URL, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Error saying hello");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i(TAG, "Success saying hello: " + responseString);
            }
        });
    }
}
