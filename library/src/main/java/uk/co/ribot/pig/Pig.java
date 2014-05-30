package uk.co.ribot.pig;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pig is the bridge in the middle of your native mobile UI and a some shared JavaScript business logic.
 */
public class Pig {
    private static final String TAG = "Pig";

    // The static singleton instance of Pig
    private static Pig sPig;
    private final HashMap<Double, Message<?>> mSentMessageMap = new HashMap<Double, Message<?>>();
    private final Map<String, List<EventListener>> mEventListenerMap = new HashMap<String, List<EventListener>>();

    /**
     * Get the singleton instance of Pig.
     * @param context A Context used to create Pig with.
     * @return A singleton instance of Pig.
     */
    public static Pig get(Context context) {
        if (sPig == null) {
            sPig = new Pig(context);
        }
        return sPig;
    }

    // An instance of the WebViewWrapper used to hold and communicate with pig-js running in a WebView
    private final WebViewWrapper mWebViewWrapper;
    // An instance of Gson used to convert from a data object to JSON
    private final Gson mGson;

    /**
     * Creates a new instance of Pig.
     * @param context The Context used to create Pig with.
     */
    private Pig(Context context) {
        mWebViewWrapper = new WebViewWrapper(context, this);
        mGson = new Gson();
    }

    /**
     * Creates a new instance of Pig with a given WebViewWrapper
     * @hide
     * @param context The Context used to create Pig with.
     * @param webViewWrapper The instance of WebViewWrapper to use
     */
    Pig(Context context, WebViewWrapper webViewWrapper) {
        mWebViewWrapper = webViewWrapper;
        mGson = new Gson();
    }

    /***********/
    /* REQ/RES */
    /***********/

    /**
     * Send a message to Pig with a given Callback.
     * @param path The path to call in JavaScript.
     * @param callback The callback you want to receive the response back to. May be null.
     * @param <R> The type of response you are expecting. Use String to get the response as a JSON
     *           String, or use another data type to convert using Gson.
     */
    public <R> void execute(String path, Callback<R> callback) {
        execute(path, (Object) null, callback);
    }

    /**
     * Send a message to Pig with a given Callback.
     * @param path The path to call in JavaScript.
     * @param data The data you want to pass to the JavaScript handler. Maybe by null, a valid JSON String or
     *             another data Object to convert with Gson.
     * @param callback The callback you want to receive the response back to. May be null.
     * @param <R> The type of response you are expecting. Use String to get the response as a JSON
     *           String, or use another data type to convert using Gson.
     */
    public <R> void execute(String path, Object data, Callback<R> callback) {
        Class dataClass = null;
        if (data != null) {
            dataClass = data.getClass();
        }

        execute(path, dataClass, data, callback);
    }

    /**
     * Send a message to Pig with a given Callback.
     *
     * You would use the method over the one without dataType if you want to provide a generic data type
     * for Gson to convert.
     *
     * @param path The path to call in JavaScript.
     * @param data The data you want to pass to the JavaScript handler. Maybe by null, a valid JSON String or
     *             another data Object to convert with Gson.
     * @param dataClass The Class of data you are providing.
     * @param callback The callback you want to receive the response back to. May be null.
     * @param <D> The data type you are passing in.
     * @param <R> The type of response you are expecting. Use String to get the response as a JSON
     *           String, or use another data type to convert using Gson.
     */
    @SuppressWarnings("unchecked") // We are checking the type before casting
    public <D, R> void execute(String path, Type dataClass, D data, Callback<R> callback) {
        Type type = callback.getClass().getGenericInterfaces()[0];
        if (type instanceof ParameterizedType) {
            Type responseType = ((ParameterizedType) type).getActualTypeArguments()[0];

            if (responseType instanceof Class) {
                execute(path, dataClass, data, responseType, callback);
            } else {
                throw new IllegalArgumentException("Can't get the Class object from the generic type from the Pig.Callback");
            }
        } else {
            throw new IllegalArgumentException("Can't get the generic type argument from the Pig.Callback");
        }
    }

    /**
     * Send a message to Pig with a given Callback.
     *
     * You would use this method if you need to pass in a response type but do not need to pass in any data.
     *
     * @param path The path to call in JavaScript.
     * @param responseType The Type of data you are expecting.
     * @param callback The callback you want to receive the response back to. May be null.
     * @param <R> The type of response you are expecting. Use String to get the response as a JSON
     *           String, or use another data type to convert using Gson.
     */
    public <R> void execute(String path, Type responseType, Callback<R> callback) {
        execute(path, (String) null, responseType, callback);
    }

    /**
     * Send a message to Pig with a given Callback.
     *
     * You would use this method if you need to pass in a response type and some data with a non-generic type..
     *
     * @param path The path to call in JavaScript.
     * @param responseType The Type of data you are expecting.
     * @param callback The callback you want to receive the response back to. May be null.
     * @param <R> The type of response you are expecting. Use String to get the response as a JSON
     *           String, or use another data type to convert using Gson.
     */
    public <D, R> void execute(String path, D data, Type responseType, Callback<R> callback) {
        Class dataClass = null;
        if (data != null) {
            dataClass = data.getClass();
        }

        execute(path, dataClass, data, responseType, callback);
    }

    /**
     * Send a message to Pig with a given Callback.
     *
     * You would use this method if you need to pass in a response type.
     *
     * @param path The path to call in JavaScript.
     * @param dataType The Type of data you are providing.
     * @param data The data you want to pass to the JavaScript handler. Maybe by null, a valid JSON String or
     *             another data Object to convert with Gson.
     * @param responseType The Type of data you are expecting.
     * @param callback The callback you want to receive the response back to. May be null.
     * @param <D> The data type you are passing in.
     * @param <R> The type of response you are expecting. Use String to get the response as a JSON
     *           String, or use another data type to convert using Gson.
     */
    public <D, R> void execute(String path, Type dataType, D data, Type responseType, Callback<R> callback) {
        String json = "";
        if (data != null) {
            if (data instanceof String && isAlreadyJson((String) data)) {
                json = (String) data;
            } else {
                json = mGson.toJson(data, dataType);
            }
        }

        execute(path, json, responseType, callback);
    }

    /**
     * Send a message to Pig with a given Callback.
     *
     * You would use this method if you have already got a JSON encoded string.
     *
     * @param path The path to call in JavaScript.
     * @param json The json encoded data to pass to JavaScript.
     * @param responseType The Type of data you are expecting.
     * @param callback The callback you want to receive the response back to. May be null.
     * @param <R> The type of response you are expecting. Use String to get the response as a JSON
     *           String, or use another data type to convert using Gson.
     */
    public <R> void execute(String path, String json, Type responseType, Callback<R> callback) {
        // Make json an empty String if no data is passed in
        if (json == null) {
            json = "";
        }

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
        mWebViewWrapper.execute(randomKey, path, json, callback);
    }

    /**
     * Used to receive and process responses from the JS layer via a WebViewWrapper.
     **/
    // TODO: Find a way to avoid unchecked operations
    void successResponse(Double key, String responseString) {
        Message message = mSentMessageMap.remove(key);
        // TODO: Check for null message from the map. Shouldn't happen though

        final Pig.Callback callback = message.getCallback();
        final Type responseClass = message.getResponseType();
        if (callback != null) {
            Object response;
            if (responseClass.equals(String.class)) {
                response = responseString;
            } else if (responseClass.equals(Void.class)) {
                response = null;
            } else {
                response = mGson.fromJson(responseString, responseClass);
            }
            callback.onSuccess(response);
        } else {
            Log.w(TAG, "No success callback for key: " + key);
        }
    }

    /**
     * Used to receive and process responses from the JS layer via a WebViewWrapper.
     **/
    // TODO: Find a way to avoid unchecked operations
    void errorResponse(Double key, String code, String name, String errorMessage) {
        Message message = mSentMessageMap.remove(key);
        // TODO: Check for null message from the map. Shouldn't happen though

        final Pig.Callback callback = message.getCallback();
        if (callback != null) {
            callback.onError(code, name, errorMessage);
        } else {
            Log.w(TAG, "No error callback for key: " + key);
        }
    }

    /**********/
    /* EVENTS */
    /**********/

    /**
     * Register a listener for the given event.
     * @param event The event to listen for.
     * @param listener The listener
     */
    public void addListener(String event, EventListener listener) {
        // Get and possibly setup the list of listeners
        List<EventListener> currentListeners = mEventListenerMap.get(event);
        if (currentListeners == null) {
            currentListeners = new ArrayList<EventListener>();
        }

        // Add the listener to the list and add it back to the map
        currentListeners.add(listener);
        mEventListenerMap.put(event, currentListeners);
    }

    /**
     * Stop listening for the given event.
     * @param event The event to stop listening for.
     * @param listener The listener
     */
    public void removeListener(String event, EventListener listener) {
        // Get and possibly setup the list of listeners
        List<EventListener> currentListener = mEventListenerMap.get(event);
        if (currentListener == null) {
            currentListener = new ArrayList<EventListener>();
        }

        // Remove the listener from the list
        currentListener.remove(listener);
        mEventListenerMap.put(event, currentListener);
    }

    /**
     * Emit an event which can be picked up by native code or in JavaScript.
     * @param event The event name to emit.
     */
    public void emit(String event) {
        emit(event, null);
    }

    /**
     * Emit an event which can be picked up by native code or in JavaScript.
     * @param event The event name to emit.
     * @param data The data to emit.
     */
    public void emit(String event, String data) {
        // Send the event for the JavaScript side to handle
        mWebViewWrapper.emit(event, data);
        // Distribute the event on the native side of the bridge
        handleEvent(event, data);
    }

    /**
     * Used to receive and process incoming JavaScript events
     **/
    void handleEvent(String event, String data) {
        // Get the list of listeners
        List<EventListener> listeners = mEventListenerMap.get(event);

        // Loop through and execute all event listeners
        if (listeners != null) {
            for (EventListener listener : listeners) {
                listener.onEvent(event, data);
            }
        }
    }

    /**
     * Get the instance of WebViewWrapper we are using.
     * @return Returns the WebViewWrapper instance
     **/
    WebViewWrapper getWebViewWrapper() {
        return mWebViewWrapper;
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
     * An interface used to get a callback for triggered events.
     */
    public interface EventListener {
        /**
         * Called when an event you have registered for has been trigger in JavaScript or Native code.
         * @param event The name of the event which was triggered.
         * @param data The data associated with the event. May be null.
         */
        void onEvent(String event, String data);
    }

    /**
     * A callback used to get a response from a Pig JavaScript handler.
     * @param <R> The type of response you are expecting in the response.
     */
    public interface Callback<R> {
        /**
         * Called when there is a successful response from a Piggy JavaScript handler.
         * @param response Null if there was no response or a response of type R. If R is String then the response
         *                 is an untouched JSON String. If not the String was converted to an R Object using Gson.
         */
        void onSuccess(R response);

        /**
         * Called when there is an error reported from a Piggy JavaScript handler.
         * @param code The status code of the error. Use to check for a specific error.
         * @param name The type of error that was reported.
         * @param message The message reported for the error
         */
        void onError(String code, String name, String message);
    }

    private class Message<R> {
        private String mPath;

        private Type mResponseType;
        private Callback<R> mCallback;
        private Message(String path, Type responseType, Callback<R> callback) {
            this.mPath = path;
            this.mResponseType = responseType;
            this.mCallback = callback;
        }

        public String getPath() {
            return mPath;
        }

        public Type getResponseType() {
            return mResponseType;
        }

        public Callback<R> getCallback() {
            return mCallback;
        }
    }
}
