package uk.co.ribot.jsbridge;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebViewWrapper webViewWrapper = ((Application) getApplication()).getWebViewWrapper();
        webViewWrapper.js("window.bridge.send(\"message\")");
    }
}
