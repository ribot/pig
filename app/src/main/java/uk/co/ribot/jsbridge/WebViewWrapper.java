package uk.co.ribot.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebViewWrapper {
    private static final String TAG = "WebViewWrapper";

    private final Context mContext;
    private final WebView mWebView;
    private boolean mIsReady;

    private final Queue<Pair<String, Callback>> mStatementQueue = new ConcurrentLinkedQueue<>();
    private final Map<Double, Callback> callbackMap = new HashMap<>();

    public WebViewWrapper(Context context) {
        mContext = context;
        mWebView = new WebView(context);

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

                for (Pair<String, Callback> statement : mStatementQueue) {
                    js(statement.first, statement.second);
                }
                mStatementQueue.clear();
            }
        });

        // Add the JS->Native interface
        mWebView.addJavascriptInterface(new JsToNativeInterface(), "android");

        // Load the bridge webpage
        mWebView.loadUrl("file:///android_asset/bridge/index.html");
    }


    public void js(String path, Callback callback) {
        if (mIsReady) {
            double randomKey;
            do {
                randomKey = Math.round(Math.random() * 40000);
            } while (callbackMap.containsKey(randomKey));

            callbackMap.put(randomKey, callback);
            mWebView.loadUrl("javascript:window.bridge.send(" + randomKey + ", \"" + path + "\")");
        } else {
            mStatementQueue.offer(Pair.create(path, callback));
        }
    }

    private class JsToNativeInterface {
        @JavascriptInterface
        public void reply(String key, String error, String response) {
            Double doubleKey = Double.parseDouble(key);

            Callback callback = callbackMap.get(doubleKey);
            if (callback != null) {
                callback.callback(error, response);
            } else {
                Log.w(TAG, "No callback for key: " + doubleKey);
            }
        }
    }

    public interface Callback {
        void callback(String error, String response);
    }
}
