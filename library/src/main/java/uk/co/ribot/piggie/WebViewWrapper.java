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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebViewWrapper {
    private static final String TAG = "WebViewWrapper";

    private final WebView mWebView;
    private final Handler mMainHandler;
    private boolean mIsReady;

    private final Queue<QueueStatement> mStatementQueue = new ConcurrentLinkedQueue<QueueStatement>();
    private final Map<Double, Piggie.Callback> callbackMap = new HashMap<Double, Piggie.Callback>();

    public WebViewWrapper(Context context) {
        mWebView = new WebView(context);
        mMainHandler = new Handler(Looper.getMainLooper());

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

                for (QueueStatement statement : mStatementQueue) {
                    js(statement.getPath(), statement.getJsonData(), statement.getCallback());
                }
                mStatementQueue.clear();
            }
        });

        // Set the web view chrome client to see console messages
        mWebView.setWebChromeClient(new WebChromeClient());

        // Add the JS->Native interface
        mWebView.addJavascriptInterface(new JsToNativeInterface(), "android");

        // Load the bridge webpage
        mWebView.loadUrl("file:///android_asset/bridge/index.html");
    }

    public void js(String path, String jsonData, Piggie.Callback callback) {
        if (!mIsReady) {
            mStatementQueue.offer(new QueueStatement(path, jsonData, callback));
            return;
        }

        double randomKey;
        do {
            randomKey = Math.round(Math.random() * 40000);
        } while (callbackMap.containsKey(randomKey));

        jsonData = jsonData.replace("\"", "\\\"");
        callbackMap.put(randomKey, callback);

        String jsUrl = "javascript:window.bridge.send(" + randomKey + ", \"" + path + "\", \"" + jsonData + "\")";
        mWebView.loadUrl(jsUrl);
    }

    private class JsToNativeInterface {
        @JavascriptInterface
        public void reply(String key, final String error, final String response) {
            Double doubleKey = Double.parseDouble(key);

            final Piggie.Callback callback = callbackMap.get(doubleKey);
            if (callback != null) {
                mMainHandler.post(new Runnable() {
                    public void run() {
                        callback.callback(error, response);
                    }
                });
            } else {
                Log.w(TAG, "No callback for key: " + doubleKey);
            }
        }
    }

    private class QueueStatement {
        private String mPath;
        private String mJsonData;
        private Piggie.Callback mCallback;

        public QueueStatement(String path, String data, Piggie.Callback callback) {
            mPath = path;
            mJsonData = data;
            mCallback = callback;
        }

        public String getPath() {
            return mPath;
        }

        public String getJsonData() {
            return mJsonData;
        }

        public Piggie.Callback getCallback() {
            return mCallback;
        }
    }
}
