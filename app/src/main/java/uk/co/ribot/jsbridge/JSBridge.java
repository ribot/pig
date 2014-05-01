package uk.co.ribot.jsbridge;

import android.content.Context;

public class JSBridge {
    private static JSBridge sJSBridge;
    public static JSBridge get(Context context) {
        if (sJSBridge == null) {
            sJSBridge = new JSBridge(context);
        }
        return sJSBridge;
    }

    private WebViewWrapper mWebViewWrapper;

    private JSBridge(Context context) {
        mWebViewWrapper = new WebViewWrapper(context);
    }

    public void send(String path, String data, Callback callback) {
        mWebViewWrapper.js(path, data, callback);
    }

    public interface Callback {
        void callback(String error, String response);
    }
}
