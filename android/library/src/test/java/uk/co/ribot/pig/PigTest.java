package uk.co.ribot.pig;

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
public class PigTest {
    @Test
    public void testSingleton() throws Exception {
        Context context = Robolectric.getShadowApplication().getApplicationContext();

        Pig pig1 = Pig.get(context);
        Pig pig2 = Pig.get(context);

        assertTrue(pig1 == pig2);
    }

    @Test
    public void testSendString() throws Exception {
        Pig pig = PigTestUtils.getMockedPiggie();

        PigTestUtils.addMockHandler(pig, new MockWebViewWrapper.Handler(pig) {
            public void handle(String data) {
                send("OK");
            }
        });

        PigTestUtils.Response response = PigTestUtils.sendMockData(pig, "event", String.class, "Hi");
        assertTrue(response.didReceive());
        assertTrue(response.getError() == null);
        assertTrue(response.getResponse().equals("OK"));
    }

    @Test
    public void testSendNumber() throws Exception {
        Pig pig = PigTestUtils.getMockedPiggie();

        PigTestUtils.addMockHandler(pig, new MockWebViewWrapper.Handler(pig) {
            public void handle(String data) {
                send("OK");
            }
        });

        PigTestUtils.Response response = PigTestUtils.sendMockData(pig, "event", Integer.class, 1);
        assertTrue(response.didReceive());
        assertTrue(response.getError() == null);
        assertTrue(response.getResponse().equals("OK"));
    }

    @Test
    public void testSendObject() throws Exception {
        Pig pig = PigTestUtils.getMockedPiggie();

        PigTestUtils.addMockHandler(pig, new MockWebViewWrapper.Handler(pig) {
            public void handle(String data) {
                send("OK");
            }
        });

        Dog jakeTheDog = new Dog("Jake", "Tennis Ball");
        PigTestUtils.Response response = PigTestUtils.sendMockData(pig, "event", Dog.class, jakeTheDog);
        assertTrue(response.didReceive());
        assertTrue(response.getError() == null);
        assertTrue(response.getResponse().equals("OK"));
    }

    private boolean stringReceived = false;
    private String stringReceivedError;
    private String stringReceivedResponse;
    @Test
    public void testReceiveString() throws Exception {
        Pig pig = PigTestUtils.getMockedPiggie();

        PigTestUtils.addMockHandler(pig, new MockWebViewWrapper.Handler(pig) {
            public void handle(String data) {
                send("A String");
            }
        });

        stringReceived = false;
        stringReceivedError = null;
        stringReceivedResponse = null;

        final CountDownLatch lock = new CountDownLatch(1);

        pig.execute("event", new Pig.Callback<String>() {
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
        Pig pig = PigTestUtils.getMockedPiggie();

        PigTestUtils.addMockHandler(pig, new MockWebViewWrapper.Handler(pig) {
            public void handle(String data) {
                send("1");
            }
        });

        stringReceived = false;
        stringReceivedError = null;
        stringReceivedResponse = null;

        final CountDownLatch lock = new CountDownLatch(1);

        pig.execute("event", new Pig.Callback<Integer>() {
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
        Pig pig = PigTestUtils.getMockedPiggie();

        PigTestUtils.addMockHandler(pig, new MockWebViewWrapper.Handler(pig) {
            public void handle(String data) {
                send("{\"name\":\"Bob\",\"toy\":\"Ball\"}");
            }
        });

        objectReceived = false;
        objectReceivedError = null;
        objectReceivedResponse = null;

        final CountDownLatch lock = new CountDownLatch(1);

        pig.execute("event", new Pig.Callback<Dog>() {
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

    private boolean emitNoData_emited = false;
    private String emitNoData_event;
    private String emitNoData_data;
    @Test
    public void testEmitEventNoData() throws Exception {
        Pig pig = PigTestUtils.getMockedPiggie();

        emitNoData_emited = false;
        emitNoData_event = null;
        emitNoData_data = null;

        final CountDownLatch lock = new CountDownLatch(1);

        pig.addListener("event", new Pig.EventListener() {
            @Override
            public void onEvent(String event, String data) {
                emitNoData_emited = true;
                emitNoData_event = event;
                emitNoData_data = data;
                lock.countDown();
            }
        });

        pig.emit("event");

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
        Pig pig = PigTestUtils.getMockedPiggie();

        emitData_emited = false;
        emitData_event = null;
        emitData_data = null;

        final CountDownLatch lock = new CountDownLatch(1);

        pig.addListener("event", new Pig.EventListener() {
            @Override
            public void onEvent(String event, String data) {
                emitData_emited = true;
                emitData_event = event;
                emitData_data = data;
                lock.countDown();
            }
        });

        pig.emit("event", "data");

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
