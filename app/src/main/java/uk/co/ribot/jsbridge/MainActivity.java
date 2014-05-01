package uk.co.ribot.jsbridge;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebViewWrapper webViewWrapper = ((Application) getApplication()).getWebViewWrapper();
        webViewWrapper.js("message", new WebViewWrapper.Callback() {
            @Override
            public void callback(String error, String response) {
                Log.d("MainActivity", "Error: " + error);
                Log.d("MainActivity", "Message: " + response);
            }
        });

        webViewWrapper.js("message2", new WebViewWrapper.Callback() {
            @Override
            public void callback(String error, String response) {
                Log.d("MainActivity", "Error: " + error);
                Log.d("MainActivity", "Message 2: " + response);
            }
        });
    }
}
