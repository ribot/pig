package uk.co.ribot.piggie;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Piggie is the bridge in the middle of your native mobile UI and a some shared JavaScript business logic.
 */
public class Piggie {
    private static final String TAG = "Piggie";

    // The static singleton instance of Piggie
    private static Piggie sPiggie;
    private final HashMap<Double, Message<?>> mSentMessageMap = new HashMap<Double, Message<?>>();

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
        mWebViewWrapper = new WebViewWrapper(context, this);
        mGson = new Gson();
    }

    /**
     * Creates a new instance of Piggie with a given WebViewWrapper
     * @hide
     * @param context The Context used to create Piggie with.
     * @param webViewWrapper The instance of WebViewWrapper to use
     */
    Piggie(Context context, WebViewWrapper webViewWrapper) {
        mWebViewWrapper = webViewWrapper;
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
    @SuppressWarnings("unchecked") // We are checking the type before casting
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
                sendAndStore(path, json, clazz, callback);
            } else {
                throw new IllegalArgumentException("Can't get the Class object from the generic type from the Piggie.Callback");
            }
        } else {
            throw new IllegalArgumentException("Can't get the generic type argument from the Piggie.Callback");
        }
    }

    private <R> void sendAndStore(String path, String json, Class<R> responseType, Callback<R> callback) {
        // Generate a random key so we can match the response later
        double randomKey;
        do {
            randomKey = Math.round(Math.random() * 40000);
        } while (mSentMessageMap.containsKey(randomKey));

        // Store the callback for the response later
        // TODO: Do we need to do this if callback == null?
        Message<R> message = new Message<R>(path, responseType, callback);
        mSentMessageMap.put(randomKey, message);

        // Send the request through the javascript layer
        mWebViewWrapper.js(randomKey, path, json, responseType, callback);
    }

    // TODO: Find a way to avoid unchecked operations
    void response(Double key, String error, String responseString) {
        Message message = mSentMessageMap.remove(key);
        // TODO: Check for null message from the map. Shouldn't happen though

        final Piggie.Callback callback = message.getCallback();
        final Class responseClass = message.getResponseType();

        if (callback != null) {
            if (error != null) {
                callback.callback(error, null);
            } else {
                Object response;
                if (responseClass == String.class) {
                    response = responseString;
                } else {
                    response = mGson.fromJson(responseString, responseClass);
                }
                callback.callback(null, response);

            }
        } else {
            Log.w(TAG, "No callback for key: " + key);
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

    private class Message<R> {
        private String mPath;

        private Class<R> mResponseType;
        private Callback<R> mCallback;
        private Message(String path, Class<R> responseType, Callback<R> callback) {
            this.mPath = path;
            this.mResponseType = responseType;
            this.mCallback = callback;
        }

        public String getPath() {
            return mPath;
        }

        public Class<R> getResponseType() {
            return mResponseType;
        }

        public Callback<R> getCallback() {
            return mCallback;
        }
    }
}
