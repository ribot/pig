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
            public void callback(String error, String response) {
                stringReceived = true;
                stringReceivedError = error;
                stringReceivedResponse = response;
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
            public void callback(String error, Integer response) {
                numberReceived = true;
                numberReceivedError = error;
                numberReceivedResponse = response;
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
            public void callback(String error, Dog response) {
                objectReceived = true;
                objectReceivedError = error;
                objectReceivedResponse = response;
                lock.countDown();
            }
        });

        lock.await(2000, TimeUnit.MILLISECONDS);

        assertTrue(objectReceived);
        assertTrue(objectReceivedError == null);
        assertTrue(objectReceivedResponse.getName().equals("Bob"));
        assertTrue(objectReceivedResponse.getToy().equals("Ball"));
    }

    private boolean emitNoData_emited = false;
    private String emitNoData_event;
    private String emitNoData_data;
    @Test
    public void testEmitEventNoData() throws Exception {
        Piggie piggie = PiggieTestUtils.getMockedPiggie();

        emitNoData_emited = false;
        emitNoData_event = null;
        emitNoData_data = null;

        final CountDownLatch lock = new CountDownLatch(1);

        piggie.addListener("event", new Piggie.EventListener() {
            @Override
            public void onEvent(String event, String data) {
                emitNoData_emited = true;
                emitNoData_event = event;
                emitNoData_data = data;
                lock.countDown();
            }
        });

        piggie.emit("event");

        lock.await(2000, TimeUnit.MILLISECONDS);

        assertTrue(emitNoData_emited);
        assertTrue(emitNoData_event.equals("event"));
        assertTrue(emitNoData_data == null);
    }

    private boolean emitData_emited = false;
    private String emitData_event;
    private String emitData_data;
    @Test
    public void testEmitEventData() throws Exception {
        Piggie piggie = PiggieTestUtils.getMockedPiggie();

        emitData_emited = false;
        emitData_event = null;
        emitData_data = null;

        final CountDownLatch lock = new CountDownLatch(1);

        piggie.addListener("event", new Piggie.EventListener() {
            @Override
            public void onEvent(String event, String data) {
                emitData_emited = true;
                emitData_event = event;
                emitData_data = data;
                lock.countDown();
            }
        });

        piggie.emit("event", "data");

        lock.await(2000, TimeUnit.MILLISECONDS);

        assertTrue(emitData_emited);
        assertTrue(emitData_event.equals("event"));
        assertTrue(emitData_data.equals("data"));
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
