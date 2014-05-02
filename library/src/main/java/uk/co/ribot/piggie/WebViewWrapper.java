package uk.co.ribot.piggie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.gson.Gson;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class WebViewWrapper {
    private static final String TAG = "WebViewWrapper";

    private final WebView mWebView;
    private final Handler mMainHandler;
    private final Gson mGson;

    private boolean mIsReady;
    private final Queue<Statement> mPreStartStatementQueue = new ConcurrentLinkedQueue<Statement>();
    private final Map<Double, Statement> mSentRequestStatementMap = new HashMap<Double, Statement>();

    public WebViewWrapper(Context context) {
        mWebView = new WebView(context);
        mMainHandler = new Handler(Looper.getMainLooper());
        mGson = new Gson();

        initWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        // Enable JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);

        // Set the web view client to receive callbacks
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, MessageFormat.format("Error {0}: ''{1}'' from {2}", errorCode, description, failingUrl));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mIsReady) return;
                super.onPageFinished(view, url);

                mIsReady = true;

                for (Statement statement : mPreStartStatementQueue) {
                    js(statement);
                }
                mPreStartStatementQueue.clear();
            }
        });

        // Set the web view chrome client to see console messages
        mWebView.setWebChromeClient(new WebChromeClient());

        // Add the JS->Native interface
        mWebView.addJavascriptInterface(new JsToNativeInterface(), "android");

        // Load the bridge webpage
        mWebView.loadUrl("file:///android_asset/bridge/index.html");
    }

    public <R> void js(String path, String jsonData, Class<R> responseClass, Piggie.Callback<R> callback) {
        js(new Statement<R>(path, jsonData, responseClass, callback));
    }

    private <R> void js(Statement<R> statement) {
        if (!mIsReady) {
            mPreStartStatementQueue.offer(statement);
            return;
        }

        double randomKey;
        do {
            randomKey = Math.round(Math.random() * 40000);
        } while (mSentRequestStatementMap.containsKey(randomKey));
        mSentRequestStatementMap.put(randomKey, statement);

        String jsonData = statement.getJsonData().replace("\"", "\\\"");
        String jsUrl = "javascript:window.bridge.send(" + randomKey + ", \"" + statement.getPath() + "\", \"" + jsonData + "\")";
        mWebView.loadUrl(jsUrl);
    }

    @SuppressWarnings("unchecked") // We are checking the generic type is String before string the String object
    private <R> void respond(Double key, Statement<R> statement, String error, String responseString) {
        final Piggie.Callback<R> callback = statement.getCallback();
        final Class<R> responseClass = statement.getResponseClass();

        if (callback != null) {
            if (error != null) {
                callback.callback(error, null);
            } else {
                R response;
                if (responseClass == String.class) {
                    response = (R) responseString;
                } else {
                    response = mGson.fromJson(responseString, responseClass);
                }
                callback.callback(null, response);
            }
        } else {
            Log.w(TAG, "No callback for key: " + key);
        }
    }

    private class JsToNativeInterface {
        @JavascriptInterface
        public void reply(String key, final String error, final String responseString) {
            final Double doubleKey = Double.parseDouble(key);
            final Statement<?> statement = mSentRequestStatementMap.get(doubleKey);

            mMainHandler.post(new Runnable() {
                public void run() {
                    respond(doubleKey, statement, error, responseString);
                }
            });
        }
    }

    private class Statement<R> {
        private String mPath;
        private String mJsonData;
        private Class<R> mResponseClass;
        private Piggie.Callback<R> mCallback;

        public Statement(String path, String data, Class<R> responseClass, Piggie.Callback<R> callback) {
            mPath = path;
            mJsonData = data;
            mResponseClass = responseClass;
            mCallback = callback;
        }

        public String getPath() {
            return mPath;
        }

        public String getJsonData() {
            return mJsonData;
        }

        public Class<R> getResponseClass() {
            return mResponseClass;
        }

        public Piggie.Callback<R> getCallback() {
            return mCallback;
        }
    }
}
