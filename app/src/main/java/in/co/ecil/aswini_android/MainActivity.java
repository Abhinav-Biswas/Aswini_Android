package in.co.ecil.aswini_android;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private WebView webview;
    private ProgressBar progressBar;
    String url = "http://aswini.ecil.co.in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview =(WebView)findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);

        webview.setWebViewClient(new myWebViewClient());
        webview.setWebChromeClient(new myWebChromeClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.loadUrl(url);
    }

    public  class myWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(100);
            view.loadUrl("javascript:if (typeof(document.getElementsByClassName('col-md-2')[0]) != 'undefined' && " +
                    "document.getElementsByClassName('col-md-2')[0] != null){" +
                    "document.getElementsByClassName('col-md-2')[0].style.position = 'absolute';" +
                    "document.getElementsByClassName('col-md-2')[1].style.position = 'absolute';" +
                    "document.getElementsByClassName('col-md-2')[1].style.top = '0';" +
                    "document.getElementsByClassName('col-md-2')[1].style.right = '0';" +
                    "document.getElementsByClassName('col-md-2')[1].style.padding = 'initial';" +
                    "document.getElementsByClassName('col-md-1')[0].style.display = 'none';" +
                    "document.getElementsByClassName('site-title')[0].style.margin = '45px 0 0 0';" +
                    "document.getElementsByClassName('site-description')[0].style.margin = '15px';" +
                    "document.getElementsByClassName('col-md-3')[0].style.display = 'none';" +
                    "document.getElementsByClassName('menu-toggle')[0].style.fontSize = '30px';" +
                    "document.getElementById('social-links').style.display = 'none';" +
                    "} void 0");
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
    }

    private class myWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            progressBar.setProgress(progress);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
