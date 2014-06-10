package uk.co.ribot.pig;

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
import android.webkit.WebSettings;

import java.text.MessageFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class WebViewWrapper {
    private static final String TAG = "WebViewWrapper";

    private final Pig mPig;
    private final WebView mWebView;
    private final Handler mMainHandler;

    private boolean mIsReady;
    private final Queue<Statement> mPreStartStatementQueue = new ConcurrentLinkedQueue<Statement>();
    private final Queue<Pair<String, String>> mPreStartEventEmitQueue = new ConcurrentLinkedQueue<Pair<String, String>>();

    public WebViewWrapper(Context context, Pig pig) {
        mPig = pig;
        mWebView = new WebView(context);
        mMainHandler = new Handler(Looper.getMainLooper());

        initWebView(context);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebView(Context context) {
        WebSettings settings = mWebView.getSettings();

        // Enable JavaScript
        settings.setJavaScriptEnabled(true);
        // Enable data storage
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(context.getFilesDir().getAbsolutePath());

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

    public <R> void execute(String key, String path, String jsonData) {
        execute(new Statement<R>(key, path, jsonData));
    }

    private <R> void execute(Statement<R> statement) {
        if (!mIsReady) {
            mPreStartStatementQueue.offer(statement);
            return;
        }

        String jsonData = statement.getJsonData().replace("\"", "\\\"");
        String jsUrl = "javascript:window.pig._execute(\"" + statement.getKey() + "\", \"" + statement.getPath() + "\", \"" + jsonData + "\")";
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
        String jsUrl = "javascript:window.pig.emit(\"" + jsonType + "\", \"" + jsonData + "\", true)";
        mWebView.loadUrl(jsUrl);
    }

    private class JsToNativeInterface {
        @JavascriptInterface
        public void success(final String key, final String responseString) {
            mMainHandler.post(new Runnable() {
                public void run() {
                    mPig.successResponse(key, responseString);
                }
            });
        }

        @JavascriptInterface
        public void fail(final String key, final String code, final String name, final String message) {
            mMainHandler.post(new Runnable() {
                public void run() {
                    mPig.errorResponse(key, code, name, message);
                }
            });
        }

        @JavascriptInterface
        public void event(final String type, final String data) {
            mMainHandler.post(new Runnable() {
                public void run() {
                    mPig.handleEvent(type, data);
                }
            });
        }
    }

    private class Statement<R> {
        private String mKey;
        private String mPath;
        private String mJsonData;

        public Statement(String key, String path, String data) {
            mKey = key;
            mPath = path;
            mJsonData = data;
        }

        public String getKey() {
            return mKey;
        }

        public String getPath() {
            return mPath;
        }

        public String getJsonData() {
            return mJsonData;
        }
    }
}
