package uk.co.ribot.piggie;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

    public <R> void send(String path, Callback<R> callback) {
        send(path, null, callback);
    }

    public <R> void send(String path, Object data, Callback<R> callback) {
        Class dataClass = null;
        if (data != null) {
            dataClass = data.getClass();
        }

        send(path, dataClass, data, callback);
    }

    public <D, R> void send(String path, Class<D> dataClass, D data, Callback<R> callback) {
        String json = "";
        if (data != null) {
            if (data instanceof String && isAlreadyJson((String) data)) {
                json = (String) data;
            } else {
                json = mGson.toJson(data, dataClass);
            }
        }

        Type type = callback.getClass().getGenericInterfaces()[0];
        if (type instanceof ParameterizedType) {
            Type responseType = ((ParameterizedType) type).getActualTypeArguments()[0];

            if (responseType instanceof Class) {
                Class<R> clazz = (Class<R>) responseType;
                mWebViewWrapper.js(path, json, clazz, callback);
            } else {
                throw new IllegalArgumentException("Can't get the Class object from the generic type from the Piggie.Callback");
            }
        } else {
            throw new IllegalArgumentException("Can't get the generic type argument from the Piggie.Callback");
        }
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
