package uk.co.ribot.jsbridge;

public class Application extends android.app.Application {
    private WebViewWrapper mWebViewWrapper;

    @Override
    public void onCreate() {
        super.onCreate();

        mWebViewWrapper = new WebViewWrapper(this);
    }

    public WebViewWrapper getWebViewWrapper() {
        return mWebViewWrapper;
    }
}
