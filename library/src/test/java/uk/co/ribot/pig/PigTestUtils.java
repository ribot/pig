package uk.co.ribot.pig;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import android.content.Context;

import org.robolectric.Robolectric;

public class PigTestUtils {
    private static boolean received = false;
    private static String receivedError;
    private static String receivedResponse;

    /**
     * Gets a new instance og Piggie with a mocked WebViewWrapper
     **/
    public static Pig getMockedPiggie() throws Exception {
        Context context = Robolectric.getShadowApplication().getApplicationContext();
        MockWebViewWrapper wrapper = new MockWebViewWrapper(context);
        Pig pig = new Pig(context, wrapper);
        wrapper.setPiggie(pig);

        return pig;
    }

    /**
     * Adds a new mock handler to the instance of Piggie
     **/
    public static void addMockHandler(Pig pig, MockWebViewWrapper.Handler handler) throws Exception {
        MockWebViewWrapper wrapper = (MockWebViewWrapper) pig.getWebViewWrapper();
        wrapper.addMockHandler("event", handler);
    }

    /**
     * Send some data to the given mocked piggie and pass back the response syncronassly.
     * Check received is true to make sure the 2s timeout wasn't hit
     **/
    public static <D> Response sendMockData(Pig pig, String path, Class<D> dataClass, D data) throws Exception {
        received = false;
        receivedError = null;
        receivedResponse = null;

        final CountDownLatch lock = new CountDownLatch(1);

        pig.execute(path, dataClass, data, new Pig.Callback<String>() {
            @Override
            public void onSuccess(String response) {
                received = true;
                receivedError = null;
                receivedResponse = response;
                lock.countDown();
            }

            @Override
            public void onError(String code, String name, String message) {
                received = true;
                receivedError = message;
                receivedResponse = null;
                lock.countDown();
            }
        });

        lock.await(2000, TimeUnit.MILLISECONDS);

        return new Response(received, receivedError, receivedResponse);
    }

    /**
     * The response from the sent mocked data
     **/
    public static class Response {
        private boolean mReceived;
        private String mReceivedError;
        private String mReceivedResponse;

        public Response(boolean received, String receivedError, String receivedResponse) {
            mReceived = received;
            mReceivedResponse = receivedResponse;
            mReceivedError = receivedError;
        }

        public boolean didReceive() {
            return mReceived;
        }

        public String getError() {
            return mReceivedError;
        }

        public String getResponse() {
            return mReceivedResponse;
        }
    }
}
