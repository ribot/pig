package uk.co.ribot.piggie;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Piggie {
    private static Piggie sPiggie;
    private final Gson mGson;

    public static Piggie get(Context context) {
        if (sPiggie == null) {
            sPiggie = new Piggie(context);
        }
        return sPiggie;
    }

    private WebViewWrapper mWebViewWrapper;

    private Piggie(Context context) {
        mWebViewWrapper = new WebViewWrapper(context);
        mGson = new Gson();
    }

    public <R> void send(String path, Class<R> responseClass, Callback<R> callback) {
        send(path, null, responseClass, callback);
    }

    public <R> void send(String path, Object data, Class<R> responseClass, Callback<R> callback) {
        Class dataClass = null;
        if (data != null) {
            dataClass = data.getClass();
        }

        send(path, dataClass, data, responseClass, callback);
    }

    public <D, R> void send(String path, Class<D> dataClass, D data, Class<R> responseClass, Callback<R> callback) {
        String json = "";
        if (data != null) {
            if (data instanceof String && isAlreadyJson((String) data)) {
                json = (String) data;
            } else {
                json = mGson.toJson(data, dataClass);
            }
        }

        mWebViewWrapper.js(path, json, responseClass, callback);
    }

    private boolean isAlreadyJson(String string) {
        try {
            new JsonParser().parse(string);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    public interface Callback<R> {
        void callback(String error, R response);
    }
}
