package uk.co.ribot.piggie;

import android.content.Context;

public class Piggie {
    private static Piggie sPiggie;
    public static Piggie get(Context context) {
        if (sPiggie == null) {
            sPiggie = new Piggie(context);
        }
        return sPiggie;
    }

    private WebViewWrapper mWebViewWrapper;

    private Piggie(Context context) {
        mWebViewWrapper = new WebViewWrapper(context);
    }

    public void send(String path, Callback callback) {
        send(path, "", callback);
    }

    public void send(String path, String data, Callback callback) {
        mWebViewWrapper.js(path, data, callback);
    }

    public interface Callback {
        void callback(String error, String response);
    }
}
