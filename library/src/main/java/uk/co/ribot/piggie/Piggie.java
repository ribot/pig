package uk.co.ribot.piggie;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Piggie {
    private static final String TAG = "Piggie";

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

    public void send(String path, Callback callback) {
        send(path, null, callback);
    }

    public void send(String path, Object data, Callback callback) {
        Class dataClass = null;
        if (data != null) {
            dataClass = data.getClass();
        }

        send(path, data, dataClass, callback);
    }

    public void send(String path, Object data, Class dataClass, Callback callback) {
        String json = "";
        if (data != null) {
            if (data instanceof String && isAlreadyJson((String) data)) {
                json = (String) data;
            } else {
                json = mGson.toJson(data, dataClass);
            }
        }
        Log.d(TAG, "Json: " + json);

        mWebViewWrapper.js(path, json, callback);
    }

    private boolean isAlreadyJson(String string) {
        try {
            new JsonParser().parse(string);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    public interface Callback {
        void callback(String error, String response);
    }
}
