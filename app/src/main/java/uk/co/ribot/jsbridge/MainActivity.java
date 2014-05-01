package uk.co.ribot.jsbridge;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonMessage1(View v) {
        JSBridge.get(this).send("message", "", new JSBridge.Callback() {
            @Override
            public void callback(String error, String response) {
                Log.d("MainActivity", "Error: " + error);
                Log.d("MainActivity", "Message: " + response);
            }
        });
    }

    public void buttonMessage2(View v) {
        JSBridge.get(this).send("message2", "", new JSBridge.Callback() {
            @Override
            public void callback(String error, String response) {
                Log.d("MainActivity", "Error: " + error);
                Log.d("MainActivity", "Message 2: " + response);
            }
        });
    }

    public void buttonData(View v) {
        JSBridge.get(this).send("data", "{\"first\": 2, \"second\": 2}", new JSBridge.Callback() {
            @Override
            public void callback(String error, String response) {
                Log.d("MainActivity", "Error: " + error);
                Log.d("MainActivity", "Data: " + response);
            }
        });
    }
}
