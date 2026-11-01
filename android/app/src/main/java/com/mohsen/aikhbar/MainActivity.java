package com.mohsen.aikhbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * غلاف رقيق يفتح لوحة التحكم الحيّة.
 * أي تعديل يُرفع إلى GitHub Pages يظهر هنا بلا إعادة بناء التطبيق.
 *
 * شبكات الجوال تقطع الاتصال أحياناً (ERR_CONNECTION_RESET)، فالتطبيق
 * يعيد المحاولة مرة، ثم يعرض آخر نسخة محفوظة بدل شاشة الخطأ.
 */
public class MainActivity extends Activity {

    private static final String PANEL_URL = "https://12mohsen.github.io/aikhbar-panel/";

    private WebView web;
    private WebSettings cfg;
    private int tries = 0;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        web = new WebView(this);
        cfg = web.getSettings();
        cfg.setJavaScriptEnabled(true);
        cfg.setDomStorageEnabled(true);      // لازم لحفظ الرمز والإعدادات
        cfg.setDatabaseEnabled(true);
        cfg.setCacheMode(WebSettings.LOAD_DEFAULT);   // يخزّن، فيبقى بديل عند تعذّر الشبكة
        cfg.setSupportZoom(false);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView v, String url) {
                tries = 0;                                  // نجح التحميل
                cfg.setCacheMode(WebSettings.LOAD_DEFAULT);
            }

            @Override
            public void onReceivedError(WebView v, WebResourceRequest req, WebResourceError err) {
                if (req == null || !req.isForMainFrame()) {
                    return;                                 // خطأ في مورد فرعي: يُتجاهل
                }
                tries++;
                if (tries == 1) {
                    v.postDelayed(new Runnable() {
                        public void run() { web.loadUrl(PANEL_URL); }
                    }, 1200);                               // محاولة ثانية بعد لحظة
                } else if (tries == 2) {
                    cfg.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    v.loadUrl(PANEL_URL);                   // آخر نسخة محفوظة
                }
            }
        });

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
        if (code == KeyEvent.KEYCODE_BACK) {
            if (web.canGoBack()) {
                web.goBack();
                return true;
            }
        }
        return super.onKeyDown(code, e);
    }

    /** ضغطة مطوّلة على زر الرجوع: تجاهل المخزَّن واجلب أحدث لوحة. */
    @Override
    public boolean onKeyLongPress(int code, KeyEvent e) {
        if (code == KeyEvent.KEYCODE_BACK) {
            tries = 0;
            web.clearCache(true);
            cfg.setCacheMode(WebSettings.LOAD_NO_CACHE);
            web.loadUrl(PANEL_URL + "?v=" + System.currentTimeMillis());
            return true;
        }
        return super.onKeyLongPress(code, e);
    }
}
