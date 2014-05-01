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
        WebViewWrapper webViewWrapper = ((Application) getApplication()).getWebViewWrapper();
        webViewWrapper.js("message", new WebViewWrapper.Callback() {
            @Override
            public void callback(String error, String response) {
                Log.d("MainActivity", "Error: " + error);
                Log.d("MainActivity", "Message: " + response);
            }
        });
    }

    public void buttonMessage2(View v) {
        WebViewWrapper webViewWrapper = ((Application) getApplication()).getWebViewWrapper();
        webViewWrapper.js("message2", new WebViewWrapper.Callback() {
            @Override
            public void callback(String error, String response) {
                Log.d("MainActivity", "Error: " + error);
                Log.d("MainActivity", "Message 2: " + response);
            }
        });
    }
}
