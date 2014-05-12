package uk.co.ribot.piggie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.text.MessageFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class WebViewWrapper {
    private static final String TAG = "WebViewWrapper";

    private final Piggie mPiggie;
    private final WebView mWebView;
    private final Handler mMainHandler;

    private boolean mIsReady;
    private final Queue<Statement> mPreStartStatementQueue = new ConcurrentLinkedQueue<Statement>();
    private final Queue<Pair<String, String>> mPreStartEventEmitQueue = new ConcurrentLinkedQueue<Pair<String, String>>();

    public WebViewWrapper(Context context, Piggie piggie) {
        mPiggie = piggie;
        mWebView = new WebView(context);
        mMainHandler = new Handler(Looper.getMainLooper());

        initWebView();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
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
                    execute(statement);
                }
                mPreStartStatementQueue.clear();

                for (Pair<String, String> event : mPreStartEventEmitQueue) {
                    emit(event.first, event.second);
                }
                mPreStartEventEmitQueue.clear();
            }
        });

        // Set the web view chrome client to see console messages
        mWebView.setWebChromeClient(new WebChromeClient());

        // Add the JS->Native interface
        mWebView.addJavascriptInterface(new JsToNativeInterface(), "android");

        // Load the bridge webpage
        mWebView.loadUrl("file:///android_asset/bridge/index.html");
    }

    public <R> void execute(Double key, String path, String jsonData, Class<R> responseClass, Piggie.Callback<R> callback) {
        execute(new Statement<R>(key, path, jsonData, responseClass, callback));
    }

    private <R> void execute(Statement<R> statement) {
        if (!mIsReady) {
            mPreStartStatementQueue.offer(statement);
            return;
        }

        String jsonData = statement.getJsonData().replace("\"", "\\\"");
        String jsUrl = "javascript:window.piggie._execute(" + statement.getKey() + ", \"" + statement.getPath() + "\", \"" + jsonData + "\")";
        mWebView.loadUrl(jsUrl);
    }

    public void emit(String event, String data) {
        if (!mIsReady) {
            mPreStartEventEmitQueue.offer(Pair.create(event, data));
            return;
        }

        if (data == null) {
            data = "";
        }

        String jsonType = event.replace("\"", "\\\"");
        String jsonData = data.replace("\"", "\\\"");
        String jsUrl = "javascript:window.piggie._nativeEmit(\"" + jsonType + "\", \"" + jsonData + "\")";
        mWebView.loadUrl(jsUrl);
    }

    private class JsToNativeInterface {
        @JavascriptInterface
        public void reply(String key, final String error, final String responseString) {
            final Double doubleKey = Double.parseDouble(key);
            mMainHandler.post(new Runnable() {
                public void run() {
                    mPiggie.response(doubleKey, error, responseString);
                }
            });
        }

        @JavascriptInterface
        public void event(final String type, final String data) {
            mMainHandler.post(new Runnable() {
                public void run() {
                    mPiggie.handleEvent(type, data);
                }
            });
        }
    }

    private class Statement<R> {
        private Double mKey;

        private String mPath;
        private String mJsonData;
        private Class<R> mResponseClass;
        private Piggie.Callback<R> mCallback;
        public Statement(Double key, String path, String data, Class<R> responseClass, Piggie.Callback<R> callback) {
            mKey = key;
            mPath = path;
            mJsonData = data;
            mResponseClass = responseClass;
            mCallback = callback;
        }

        public Double getKey() {
            return mKey;
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
