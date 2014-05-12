package uk.co.ribot.piggie;

import android.content.Context;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import com.google.gson.annotations.SerializedName;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class PiggieTest {
    @Test
    public void testSingleton() throws Exception {
        Context context = Robolectric.getShadowApplication().getApplicationContext();

        Piggie piggie1 = Piggie.get(context);
        Piggie piggie2 = Piggie.get(context);

        assertTrue(piggie1 == piggie2);
    }

    @Test
    public void testSendString() throws Exception {
        Piggie piggie = PiggieTestUtils.getMockedPiggie();

        PiggieTestUtils.addMockHandler(piggie, new MockWebViewWrapper.Handler(piggie) {
            public void handle(String data) {
                send("OK");
            }
        });

        PiggieTestUtils.Response response = PiggieTestUtils.sendMockData(piggie, "event", String.class, "Hi");
        assertTrue(response.didReceive());
        assertTrue(response.getError() == null);
        assertTrue(response.getResponse().equals("OK"));
    }

    @Test
    public void testSendNumber() throws Exception {
        Piggie piggie = PiggieTestUtils.getMockedPiggie();

        PiggieTestUtils.addMockHandler(piggie, new MockWebViewWrapper.Handler(piggie) {
            public void handle(String data) {
                send("OK");
            }
        });

        PiggieTestUtils.Response response = PiggieTestUtils.sendMockData(piggie, "event", Integer.class, 1);
        assertTrue(response.didReceive());
        assertTrue(response.getError() == null);
        assertTrue(response.getResponse().equals("OK"));
    }

    @Test
    public void testSendObject() throws Exception {
        Piggie piggie = PiggieTestUtils.getMockedPiggie();

        PiggieTestUtils.addMockHandler(piggie, new MockWebViewWrapper.Handler(piggie) {
            public void handle(String data) {
                send("OK");
            }
        });

        Dog jakeTheDog = new Dog("Jake", "Tennis Ball");
        PiggieTestUtils.Response response = PiggieTestUtils.sendMockData(piggie, "event", Dog.class, jakeTheDog);
        assertTrue(response.didReceive());
        assertTrue(response.getError() == null);
        assertTrue(response.getResponse().equals("OK"));
    }

    private boolean stringReceived = false;
    private String stringReceivedError;
    private String stringReceivedResponse;
    @Test
    public void testReceiveString() throws Exception {
        Piggie piggie = PiggieTestUtils.getMockedPiggie();

        PiggieTestUtils.addMockHandler(piggie, new MockWebViewWrapper.Handler(piggie) {
            public void handle(String data) {
                send("A String");
            }
        });

        stringReceived = false;
        stringReceivedError = null;
        stringReceivedResponse = null;

        final CountDownLatch lock = new CountDownLatch(1);

        piggie.execute("event", null, null, new Piggie.Callback<String>() {
            @Override
            public void onSuccess(String response) {
                stringReceived = true;
                stringReceivedError = null;
                stringReceivedResponse = response;
                lock.countDown();
            }

            @Override
            public void onError(String code, String name, String message) {
                stringReceived = true;
                stringReceivedError = message;
                stringReceivedResponse = null;
                lock.countDown();
            }
        });

        lock.await(2000, TimeUnit.MILLISECONDS);

        assertTrue(stringReceived);
        assertTrue(stringReceivedError == null);
        assertTrue(stringReceivedResponse.equals("A String"));
    }

    private boolean numberReceived = false;
    private String numberReceivedError;
    private Integer numberReceivedResponse;
    @Test
    public void testReceiveNumber() throws Exception {
        Piggie piggie = PiggieTestUtils.getMockedPiggie();

        PiggieTestUtils.addMockHandler(piggie, new MockWebViewWrapper.Handler(piggie) {
            public void handle(String data) {
                send("1");
            }
        });

        stringReceived = false;
        stringReceivedError = null;
        stringReceivedResponse = null;

        final CountDownLatch lock = new CountDownLatch(1);

        piggie.execute("event", null, null, new Piggie.Callback<Integer>() {
            @Override
            public void onSuccess(Integer response) {
                numberReceived = true;
                numberReceivedError = null;
                numberReceivedResponse = response;
                lock.countDown();
            }

            @Override
            public void onError(String code, String name, String message) {
                numberReceived = true;
                numberReceivedError = message;
                numberReceivedResponse = null;
                lock.countDown();
            }
        });

        lock.await(2000, TimeUnit.MILLISECONDS);

        assertTrue(numberReceived);
        assertTrue(numberReceivedError == null);
        assertTrue(numberReceivedResponse.equals(1));
    }

    private boolean objectReceived = false;
    private String objectReceivedError;
    private Dog objectReceivedResponse;
    @Test
    public void testReceiveObject() throws Exception {
        Piggie piggie = PiggieTestUtils.getMockedPiggie();

        PiggieTestUtils.addMockHandler(piggie, new MockWebViewWrapper.Handler(piggie) {
            public void handle(String data) {
                send("{\"name\":\"Bob\",\"toy\":\"Ball\"}");
            }
        });

        objectReceived = false;
        objectReceivedError = null;
        objectReceivedResponse = null;

        final CountDownLatch lock = new CountDownLatch(1);

        piggie.execute("event", null, null, new Piggie.Callback<Dog>() {
            @Override
            public void onSuccess(Dog response) {
                objectReceived = true;
                objectReceivedError = null;
                objectReceivedResponse = response;
                lock.countDown();
            }

            @Override
            public void onError(String code, String name, String message) {
                objectReceived = true;
                objectReceivedError = message;
                objectReceivedResponse = null;
                lock.countDown();
            }
        });

        lock.await(2000, TimeUnit.MILLISECONDS);

        assertTrue(objectReceived);
        assertTrue(objectReceivedError == null);
        assertTrue(objectReceivedResponse.getName().equals("Bob"));
        assertTrue(objectReceivedResponse.getToy().equals("Ball"));
    }

    private class Dog {
        @SerializedName("name") private String mName;
        @SerializedName("toy") private String mToy;

        public Dog(String name, String toy) {
            mName = name;
            mToy = toy;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }

        public void setToy(String toy) {
            mToy = toy;
        }

        public String getToy() {
            return mToy;
        }

        public String toString() {
            return "" + getName() + " likes " + getToy();
        }
    }
}
