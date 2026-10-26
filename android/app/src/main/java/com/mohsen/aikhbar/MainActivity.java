package com.mohsen.aikhbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * غلاف رقيق يفتح لوحة التحكم الحيّة.
 * أي تعديل يُرفع إلى GitHub Pages يظهر هنا بلا إعادة بناء التطبيق.
 */
public class MainActivity extends Activity {

    private static final String PANEL_URL = "https://12mohsen.github.io/aikhbar-panel/";

    private WebView web;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // لازم لحفظ الرمز والإعدادات
        s.setDatabaseEnabled(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setSupportZoom(false);
        web.setWebViewClient(new WebViewClient());

        setContentView(web);

        if (state != null) {
            web.restoreState(state);
        } else {
            web.loadUrl(PANEL_URL);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    public boolean onKeyDown(int code, KeyEvent e) {
        if (code == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(code, e);
    }
}
