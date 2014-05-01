package uk.co.ribot.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.text.MessageFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebViewWrapper {
    private static final String TAG = "WebViewWrapper";

    private final Context mContext;
    private final WebView mWebView;
    private final Queue<String> mStatementQueue = new ConcurrentLinkedQueue<>();
    private boolean mIsReady;

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
                Log.d(TAG, "Requested: " + url);
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

                Log.d(TAG, "Finished: " + url);
                mIsReady = true;

                for (String stmt : mStatementQueue) {
                    Log.d(TAG, "Queued request: " + stmt);
                    js(stmt);
                }
                mStatementQueue.clear();
            }
        });

        // Add the JS->Native interface
        mWebView.addJavascriptInterface(new JsToNativeInterface(), "android");

        // Load the bridge webpage
        mWebView.loadUrl("file:///android_asset/bridge/index.html");
    }


    public void js(String js) {
        if (mIsReady) {
            mWebView.loadUrl("javascript:" + js);
        } else {
            mStatementQueue.offer(js);
        }
    }

    private class JsToNativeInterface {
        public JsToNativeInterface() {
        }

        @JavascriptInterface
        public void reply(String error, String response) {
            Log.d(TAG, "Error: " + error);
            Log.d(TAG, "Response: " + response);
        }
    }
}
