package uk.co.ribot.piggie;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Piggie is the bridge in the middle of your native mobile UI and a some shared JavaScript business logic.
 */
public class Piggie {
    // The static singleton instance of Piggie
    private static Piggie sPiggie;

    /**
     * Get the singleton instance of Piggie.
     * @param context A Context used to create Piggie with.
     * @return A singleton instance of Piggie.
     */
    public static Piggie get(Context context) {
        if (sPiggie == null) {
            sPiggie = new Piggie(context);
        }
        return sPiggie;
    }

    // An instance of the WebViewWrapper used to hold and communicate with piggie-js running in a WebView
    private final WebViewWrapper mWebViewWrapper;
    // An instance of Gson used to convert from a data object to JSON
    private final Gson mGson;

    /**
     * Creates a new instance of Piggie.
     * @param context The Context used to create Piggie with.
     */
    private Piggie(Context context) {
        mWebViewWrapper = new WebViewWrapper(context);
        mGson = new Gson();
    }

    /**
     * Send a message to Piggie with a given Callback.
     * @param path The path to call in JavaScript.
     * @param callback The callback you want to receive the response back to. May be null.
     * @param <R> The type of response you are expecting. Use String to get the response as a JSON
     *           String, or use another data type to convert using Gson.
     */
    public <R> void send(String path, Callback<R> callback) {
        send(path, null, callback);
    }

    /**
     * Send a message to Piggie with a given Callback.
     * @param path The path to call in JavaScript.
     * @param data The data you want to pass to the JavaScript handler. Maybe by null, a valid JSON String or
     *             another data Object to convert with Gson.
     * @param callback The callback you want to receive the response back to. May be null.
     * @param <R> The type of response you are expecting. Use String to get the response as a JSON
     *           String, or use another data type to convert using Gson.
     */
    public <R> void send(String path, Object data, Callback<R> callback) {
        Class dataClass = null;
        if (data != null) {
            dataClass = data.getClass();
        }

        send(path, dataClass, data, callback);
    }

    /**
     * Send a message to Piggie with a given Callback.
     *
     * You would use the method over the one without dataType if you want to provide a generic data type
     * for Gson to convert.
     *
     * @param path The path to call in JavaScript.
     * @param data The data you want to pass to the JavaScript handler. Maybe by null, a valid JSON String or
     *             another data Object to convert with Gson.
     * @param dataClass The Class of data you are providing.
     * @param callback The callback you want to receive the response back to. May be null.
     * @param <R> The type of response you are expecting. Use String to get the response as a JSON
     *           String, or use another data type to convert using Gson.
     */
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

    /**
     * Attempts to parse the JSON String to check if it is valid.
     * @param string The JSON String to check.
     * @return Returns true if the JSON String was valid.
     */
    private boolean isAlreadyJson(String string) {
        try {
            new JsonParser().parse(string);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    /**
     * A callback used to get a response from a Piggie JavaScript handler.
     * @param <R> The type of response you are expecting in the response.
     */
    public interface Callback<R> {
        /**
         * Called when there is a response from a Piggy JavaScript handler.
         * @param error Null if there was no error. A String with the error description if there was.
         * @param response Null if there was no response or an error, a response of the type R if there was a response.
         *                 If R is String then the response is an untouched JSON String. If not the String was
         *                 converted to an R Object using Gson.
         */
        void callback(String error, R response);
    }
}
