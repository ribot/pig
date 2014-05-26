package uk.co.ribot.pig;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class MockWebViewWrapper extends WebViewWrapper {
    private final Map<String, Handler> mMockHandlers = new HashMap<String, Handler>();
    private Pig mPig;

    public MockWebViewWrapper(Context context) {
        super(context, null);
    }

    public void setPiggie(Pig pig) {
        mPig = pig;
    }

    @Override
    public <R> void execute(Double key, String path, String jsonData, Pig.Callback<R> callback) {
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
        private final Pig mPig;

        public Handler(Pig pig) {
            mPig = pig;
        }

        public void doHandler(Double key, String data) {
            mKey = key;
            handle(data);
        }

        abstract void handle(String data);

        // TODO: Stop multiple responses
        void error(String code, String name, String message) {
            mPig.errorResponse(mKey, code, name, message);
        }
        void send(String response) {
            mPig.successResponse(mKey, response);
        }
    }
}
