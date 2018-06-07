package in.co.ecil.aswini_android;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private WebView webview;
    private ProgressBar progressBar;
    String url = "http://aswini.ecil.co.in";
    String current_url = "http://aswini.ecil.co.in";
    LinearLayout myLinearLayout;

    public static final String PREFERENCES = "PREFERENCES_NAME";
    public static final String WEB_LINKS = "links";
    public static final String WEB_TITLE = "title";

    NavigationView navigationView;
    private Intent in;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.nav_view);
        myLinearLayout = findViewById(R.id.main_content);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.app_long_name);

        if (getIntent().getExtras() != null) {
            url = getIntent().getStringExtra("url");
            current_url = getIntent().getStringExtra("url");
            getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        }

        webview = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        webview.setWebViewClient(new myWebViewClient());
        webview.setWebChromeClient(new myWebChromeClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //webview.getSettings().setAppCacheMaxSize( 10 * 1024 * 1024 ); // 10MB

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //webview.clearCache(true);
        //webview.clearHistory();
        webview.setDownloadListener(new DownloadListener()
        {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                        mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(MainActivity.this,
                        Environment.DIRECTORY_DOWNLOADS,".pdf");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                /*try {
                    dm.enqueue(request);
                } catch (Exception e){
                    Log.e(e.getClass().getName(), e.getMessage(), e);
                }*/
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File",
                        Toast.LENGTH_LONG).show();
            }});

        webview.loadUrl(url);
        Log.d("MYAFTERLOAD", "On Load .....");
    }

    public  class myWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            current_url = url;
            invalidateOptionsMenu();
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(100);

            if (url.contentEquals("http://aswini.ecil.co.in/")) {
                getSupportActionBar().setTitle(R.string.app_long_name);
            }
            else {
                getSupportActionBar().setTitle(view.getTitle());
            }

            if (url.contains("http://aswini.ecil.co.in")) {
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
            else if (url.contains("http://aswini.ecil.co.in/teldir/public")) {
                view.getSettings().setUseWideViewPort(true);
            }
            else if (url.contains("https://ecprdci.ecil.co.in:8443/nwbc")) {
                view.getSettings().setUseWideViewPort(true);
            }
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(5);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            try {
                view.stopLoading();
            } catch (Exception e) {
            }

            if (view.canGoBack()) {
                view.goBack();
            }

            view.loadUrl("about:blank");
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Secure Connection Error");
            alertDialog.setMessage("Check your Internet connection is enabled & EC SSL VPN Connection is configured properly.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    startActivity(getIntent());
                }
            });

            alertDialog.show();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }

    private class myWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            progressBar.setProgress(progress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (current_url != "http://aswini.ecil.co.in") {
                getSupportActionBar().setTitle(title);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
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

        if (current_url != "http://aswini.ecil.co.in") {
            getMenuInflater().inflate(R.menu.browser, menu);

            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            String links = sharedPreferences.getString(WEB_LINKS, null);

            if (links != null) {

                Gson gson = new Gson();
                ArrayList<String> linkList = gson.fromJson(links, new TypeToken<ArrayList<String>>() {
                }.getType());

                if (linkList.contains(current_url)) {
                    menu.getItem(0).setIcon(R.drawable.ic_bookmark_black_24dp);
                } else {
                    menu.getItem(0).setIcon(R.drawable.ic_bookmark_border_black_24dp);
                }
            } else {
                menu.getItem(0).setIcon(R.drawable.ic_bookmark_border_black_24dp);
            }
        }
        else {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        if (item.getItemId() == R.id.action_settings) {

            in = getPackageManager().getLaunchIntentForPackage("de.blinkt.openvpn");
            if (in != null) {
                // We found the activity now start the activity
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
            } else {
                // Bring user to the market or let them choose an app?
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("EC SSL VPN App Not Installed !!!");
                alertDialog.setMessage("Please install & configure EC SSL VPN Android App or Contact the administrator.");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alertDialog.show();
            }
            return true;
        }

        if (item.getItemId() == R.id.action_about) {
            String appName = getString(R.string.app_long_name);
            int year = Calendar.getInstance().get(Calendar.YEAR);
            StringBuilder html = new StringBuilder()
                    .append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />")
                    .append("<title>About Aswini App</title>")
                    .append("<img src=\"file:///android_asset/ic_launcher.png\" alt=\"").append(appName).append("\"/>")
                    .append("<h1>" + appName + " 1.0</h1>")
                    .append("<p>Copyright " + year + " - 2025 ECIL</p>")
                    .append("<big><b>Author: </b></big>Abhinav Biswas, Technical Officer, ITSD")
                    .append("<p><b>Electronics Corporation of India Limited</b> (ECIL)<br>")
                    .append("Department of Atomic Energy, Government of India")
                    .append("</p><p>")
                    .append("</p><hr/><p>")
                    .append("</p><hr/><p>")
                    .append("<b>Aswini App</b> is a secure VPN-only Intranet Portal for Android Mobile Operating System, developed by IT Services Division (ITSD) of Electronics Corporation of India Limited (ECIL), a Govt. of India (Dept. of Atomic Energy) Enterprise. <br><br>This App must be used by ECIL employees only.\n" +
                            "<br><br><br>\n")
                    .append("For further details contact Administrator:<br>administrator@ecil.co.in")
                    .append("<br><br><hr/><h2>Change Log</h2><ul><li>First Version of Aswini Android App.</li></ul>");

            webview.loadDataWithBaseURL("file:///android_res/drawable/", html.toString(), "text/html", "utf-8", null);
            getSupportActionBar().setTitle("About Aswini App");
        }

        if (item.getItemId() == R.id.action_bookmark) {

            String message;

            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            String jsonLink = sharedPreferences.getString(WEB_LINKS, null);
            String jsonTitle = sharedPreferences.getString(WEB_TITLE, null);


            if (jsonLink != null && jsonTitle != null) {

                Gson gson = new Gson();
                ArrayList<String> linkList = gson.fromJson(jsonLink, new TypeToken<ArrayList<String>>() {
                }.getType());

                ArrayList<String> titleList = gson.fromJson(jsonTitle, new TypeToken<ArrayList<String>>() {
                }.getType());

                if (linkList.contains(current_url)) {
                    linkList.remove(current_url);
                    titleList.remove(webview.getTitle().trim());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                    editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                    editor.apply();


                    message = "Bookmark Removed";

                } else {
                    linkList.add(current_url);
                    titleList.add(webview.getTitle().trim());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                    editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                    editor.apply();

                    message = "Bookmarked";
                }
            } else {

                ArrayList<String> linkList = new ArrayList<>();
                ArrayList<String> titleList = new ArrayList<>();
                linkList.add(current_url);
                titleList.add(webview.getTitle());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                editor.apply();

                message = "Bookmarked";
            }

            Snackbar snackbar = Snackbar.make(myLinearLayout, message, Snackbar.LENGTH_LONG);
            snackbar.show();

            invalidateOptionsMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home :
                navigationView.getMenu().getItem(0).setChecked(false);
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.nav_ecmail :
                navigationView.getMenu().getItem(1).setChecked(false);
                //startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse("file:///android_asset/k9mail-release.apk"),"application/vnd.android.package-archive"));

                in = getPackageManager().getLaunchIntentForPackage("com.fsck.k9");
                if (in != null) {
                    // We found the activity now start the activity
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                } else {
                    // Bring user to the market or let them choose an app?
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("EC-Mail App Not Installed !!!");
                    alertDialog.setMessage("Please install the EC-Mail Android App or Contact the administrator.");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    alertDialog.show();
                }

                break;

            case R.id.nav_att :
                navigationView.getMenu().getItem(2).setChecked(false);
                in = new Intent(this, MainActivity.class);
                in.putExtra("url", "https://ecprdci.ecil.co.in:8443/sap/bc/gui/sap/its/zhrtmr017/");
                startActivity(in);
                break;

            case R.id.nav_emp :
                navigationView.getMenu().getItem(3).setChecked(false);
                in = new Intent(this, MainActivity.class);
                in.putExtra("url", "http://aswini.ecil.co.in/teldir/public/");
                startActivity(in);
                break;

            case R.id.nav_ess :
                navigationView.getMenu().getItem(4).setChecked(false);
                in = new Intent(this, MainActivity.class);
                in.putExtra("url", "https://ecprdci.ecil.co.in:8443/nwbc");
                startActivity(in);
                break;

            case R.id.nav_pay :
                navigationView.getMenu().getItem(5).setChecked(false);
                in = new Intent(this, MainActivity.class);
                in.putExtra("url", "http://payslips.ecil.co.in/FAG_EDP/");
                startActivity(in);
                break;

            case R.id.nav_bookmark :
                navigationView.getMenu().getItem(6).setChecked(false);
                startActivity(new Intent(this, BookmarkActivity.class));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
