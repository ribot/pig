package uk.co.ribot.piggie;

import android.content.Context;

public class MockWebViewWrapper extends WebViewWrapper {
    public MockWebViewWrapper(Context context) {
        super(context);
    }

    @Override
    public <R> void js(String path, String jsonData, Class<R> responseClass, Piggie.Callback<R> callback) {
        // TODO: Implement a fancy callback system
    }
}
