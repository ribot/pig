package uk.co.ribot.piggie;

import android.app.Activity;
import android.content.Context;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
    public void testSendAndReceive() throws Exception {
        Piggie piggie = PiggieTestUtils.getMockedPiggie();

        PiggieTestUtils.addMockHandler(piggie, new MockWebViewWrapper.Handler(piggie) {
            public void handle(String data) {
                send("OK");
            }
        });

        PiggieTestUtils.Response response = PiggieTestUtils.sendMockData(piggie, "event", null, null);
        assertTrue(response.didReceive());
        assertTrue(response.getError() == null);
        assertTrue(response.getResponse().equals("OK"));
    }
}
