package uk.co.ribot.piggie;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class MockWebViewWrapper extends WebViewWrapper {
    private final Map<String, Handler> mMockHandlers = new HashMap<String, Handler>();
    private Piggie mPiggie;

    public MockWebViewWrapper(Context context) {
        super(context, null);
    }

    public void setPiggie(Piggie piggie) {
        mPiggie = piggie;
    }

    @Override
    public <R> void js(Double key, String path, String jsonData, Class<R> responseClass, Piggie.Callback<R> callback) {
        Handler handler = mMockHandlers.get(path);
        if (handler != null) {
            handler.doHandler(key, jsonData);
        } else {
            System.out.println("[WARN](MockWebViewWrapper) No mock handler for " + path);
        }
    }

    public void addMockHandler(String path, Handler handler) {
        // TODO: Check if there's already a mock handler for this path
        mMockHandlers.put(path, handler);
    }

    public static abstract class Handler {
        private Double mKey;
        private final Piggie mPiggie;

        public Handler(Piggie piggie) {
            mPiggie = piggie;
        }

        public void doHandler(Double key, String data) {
            mKey = key;
            handle(data);
        }

        abstract void handle(String data);

        // TODO: Stop multiple responses
        void error(String error) {
            mPiggie.response(mKey, error, null);
        }
        void send(String response) {
            mPiggie.response(mKey, null, response);
        }
    }
}
