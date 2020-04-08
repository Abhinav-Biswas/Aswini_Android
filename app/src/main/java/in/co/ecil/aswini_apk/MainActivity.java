package in.co.ecil.aswini_apk;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
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
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private WebView webview;
    private ProgressBar progressBar;
    String url = "http://aswini.ecil.co.in";
    String current_url = "home";
    LinearLayout myLinearLayout;

    int vcode = BuildConfig.VERSION_CODE;
    boolean vcode_flag = true;

    String appName = "Aswini App";
    int year = Calendar.getInstance().get(Calendar.YEAR);
    String html = "<!DOCTYPE html><head>" +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /><title>About Aswini App</title></head>" +
            "<body><img src=\"file:///android_asset/ic_launcher.png\" alt=\"" + appName + "\"/><h1>" + appName + " 1.0</h1>" +
            "<p>Copyright " + year + " - 2025 ECIL</p><big><b>Author: </b></big>Abhinav Biswas, Sr. Technical Officer, ITSD" +
            "<p><b>Electronics Corporation of India Limited</b> (ECIL)<br>Department of Atomic Energy, Government of India</p><p>" +
            "</p><hr/><p></p><hr/><p><b>Aswini App</b> is a secure Internal Portal for Android Mobile Operating System, " +
            "developed by IT Services Division (ITSD) of Electronics Corporation of India Limited (ECIL), a Govt. of India (Dept. " +
            "of Atomic Energy) Enterprise. <br><br>This App must be used by ECIL employees only.\n<br><br><br>\nFor further details " +
            "contact Administrator:<br>administrator@ecil.co.in<br><br><hr/><h2>Change Log</h2><ul><li>First Version of Aswini " +
            "Android App.</li></ul></body></html>";

    String html1 = "Nil";

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

        //App Update Check
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String check_url ="https://abhinav-biswas.github.io/aswini.github.io/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, check_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Log.e("Response is: "+ response.substring(0,500));
                        vcode = Integer.parseInt(response.substring(14,16));
                        //getSupportActionBar().setTitle(Integer.toString(BuildConfig.VERSION_CODE));
                        if (BuildConfig.VERSION_CODE < vcode && vcode_flag && current_url.contains("home")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                            alertDialog.setTitle("New Update Available!!!");
                            alertDialog.setMessage("Please update this app to the Latest version from Google Play Store.");
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Remind me later", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    vcode_flag = false;
                                    //finish();
                                    //startActivity(getIntent());
                                }
                            });
                            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Open Play Store", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    in = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=in.co.ecil.aswini_apk"));
                                    startActivity(in);
                                    finish();
                                }
                            });
                            alertDialog.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                Toast.makeText(getApplicationContext(), "Auto-Update Check Failed.",
                        Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue.
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);

        if (current_url.contains("home")) {
            //webview.loadDataWithBaseURL("file:///android_res/drawable/", html1, "text/html", "utf-8", null);
            webview.loadDataWithBaseURL("file:///android_asset/images", readFileAsString("index.html"),"text/html", "utf-8", null);
        } else {
            webview.loadUrl(url);
        }
    }

    private String readFileAsString(String sourceHtmlLocation) {
        InputStream is;
        try
        {
            is = getAssets().open(sourceHtmlLocation);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer, "UTF-8");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public  class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if ( url.endsWith(".pdf")){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                //Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setDataAndType(Uri.parse(url), "application/pdf");
                try{
                    view.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    //user does not have a pdf viewer installed
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("No PDF Viewer Application Installed");
                    alertDialog.setMessage("Please install any PDF Viewer application from Google Play Store.");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Skip", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                            //startActivity(getIntent());
                        }
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            in = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=pdf%20viewer"));
                            startActivity(in);
                            finish();
                        }
                    });
                    alertDialog.show();

                }
            } else {
                view.loadUrl(url);
            }
            current_url = url;
            invalidateOptionsMenu();
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            findViewById(R.id.SplashText).setVisibility(View.GONE);
            findViewById(R.id.webView).setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(100);

            if (url.contentEquals("http://aswini.ecil.co.in/quicklinks/") || url.contentEquals("http://aswini.ecil.co.in")) {
                getSupportActionBar().setTitle(R.string.app_long_name);
            } else if (url.contains("payslips.ecil.co.in")) {
                getSupportActionBar().setTitle("Payroll Login");
                //view.getSettings().setUseWideViewPort(false);
            } else if (url.contains("careers.ecil.co.in")) {
                getSupportActionBar().setTitle("Careers Portal");
                //view.getSettings().setUseWideViewPort(true);
            } else if (url.contains("http://aswini.ecil.co.in/teldir/public")) {
                getSupportActionBar().setTitle("Telephone Directory");
                view.getSettings().setUseWideViewPort(true);
            } else if (url.contains("http://aswini.ecil.co.in/latestcirculars")) {
                getSupportActionBar().setTitle("Latest Circulars");
            } else {
                getSupportActionBar().setTitle(view.getTitle());
            }

            if (url.contains("http://aswini.ecil.co.in/quicklinks")) {
                view.loadUrl("javascript:if (typeof(document.getElementsByClassName('col-md-2')[0]) != 'undefined' && " +
                        "document.getElementsByClassName('col-md-2')[0] != null){" +
                        "document.getElementsByClassName('col-md-2')[0].style.position = 'absolute';" +
                        "document.getElementsByClassName('col-md-2')[1].style.position = 'absolute';" +
                        "document.getElementsByClassName('col-md-2')[1].style.top = '0';" +
                        "document.getElementsByClassName('col-md-2')[1].style.right = '0';" +
                        "document.getElementsByClassName('col-md-2')[1].style.padding = 'initial';" +
                        //"document.getElementsByClassName('col-md-1')[0].style.display = 'none';" +
                        "document.getElementsByClassName('site-title')[0].style.margin = '40px 0 0 0';" +
                        "document.getElementsByClassName('site-description')[0].style.padding = '15px 35px 10px 35px';" +
                        //"document.getElementsByClassName('site-description')[0].style.display = 'none';" +
                        "document.getElementById('post-7729').style.margin = '15px';" +
                        "document.getElementById('social-links').style.display = 'none';" +
                        //"document.getElementsByClassName('col-md-3')[0].style.display = 'none';" +
                        //"document.getElementsByClassName('menu-toggle')[0].style.fontSize = '30px';" +
                        "} void 0");
            } else if (url.contains("http://aswini.ecil.co.in/latestcirculars")) {
                view.loadUrl("javascript:if (typeof(document.getElementsByClassName('col-md-2')[0]) != 'undefined' && " +
                        "document.getElementsByClassName('col-md-2')[0] != null){" +
                        "document.getElementsByClassName('col-md-2')[0].style.display = 'none';" +
                        "document.getElementsByClassName('col-md-2')[1].style.display = 'none';" +
                        "document.getElementsByClassName('entry-header')[0].style.display = 'none';" +
                        "document.getElementsByClassName('site-title')[0].style.display = 'none';" +
                        "document.getElementsByClassName('site-description')[0].style.display = 'none';" +
                        //"document.getElementById('post-7729').style.margin = '15px';" +
                        "document.getElementById('social-links').style.display = 'none';" +
                        "[].forEach.call(document.getElementsByClassName('btn-group'), function (el) {el.style.float = 'right';});" +
                        //"document.getElementsByClassName('menu-toggle')[0].style.fontSize = '30px';" +
                        "} void 0");
            } else if (url.contains("http://aswini.ecil.co.in/teldir/public")) {
                view.loadUrl("javascript:" +
                        "(function() {" +
                        "document.getElementsByClassName('collapse navbar-collapse navbar-ex1-collapse')[0].style.display = 'contents';" +
                        "document.getElementsByClassName('navbar-brand')[0].style.display = 'none';" +
                        "document.getElementsByClassName('nav navbar-nav navbar-right')[0].style.display = 'none';" +
                        "document.getElementsByClassName('navbar-toggle')[0].style.display = 'none';" +
                        "void 0;" +
                        "}) ();");
                //Toast.makeText(MainActivity.this, "Error hap" , Toast.LENGTH_LONG).show();
            } else if (url.contains("https://ecprdci.ecil.co.in:8443/nwbc")) {
                view.getSettings().setUseWideViewPort(true);
            } else if (url.contains("careers.ecil.co.in")) {
                view.loadUrl("javascript:" +
                        "(function() {" +
                        "document.getElementsByTagName('td')[0].style.display = 'none';" +
                        "void 0;" +
                        "}) ();");
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(5);

            if (url.contains("mail.ecil.co.in")) {
                //view.loadUrl("about:blank");
                in = getPackageManager().getLaunchIntentForPackage("com.fsck.k9");
                if (in != null) {
                    // We found the activity now start the activity
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                } else {
                    // Bring user to the market or let them choose an app?
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("EC-Mail App Not Installed !!!");
                    alertDialog.setMessage("Please install the EC-Mail Android App from Google Play Store for better User Interface.");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    alertDialog.show();
                }

            }
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            try {
                //Toast.makeText(MainActivity.this, "Error" + description + failingUrl, Toast.LENGTH_LONG).show();
                view.stopLoading();
            } catch (Exception e) {
                //Log.e("ERROR", "ERROR IN CODE: " + e.toString());
                //e.printStackTrace();
                //Toast.makeText(MainActivity.this, "Error "+ e.toString(), Toast.LENGTH_SHORT).show();
            }

            if (view.canGoBack()) {
                view.goBack();
            }

            view.loadUrl("about:blank");
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Secure Connection Error");
            alertDialog.setMessage("Check your Internet connection is enabled or else the Server may be down. Please contact the Administrator");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    startActivity(getIntent());
                }
            });
            /*alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Open EC SSL VPN App", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
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
                    finish();
                }
            });*/

            alertDialog.show();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            //Log.e("ERROR", "ERROR IN CODE: " + error.toString());
            //e.printStackTrace();
            //Toast.makeText(MainActivity.this, "ERROR "+ error.toString(), Toast.LENGTH_LONG).show();
            //handler.proceed(); // Ignore SSL certificate errors
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.notification_error_ssl_cert_invalid);
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }

/*        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                if (url.contains("Integrated_Security_System_Tyre_Killer.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(Uri.parse(url)), "UTF-8", getResources().openRawResource(R.raw.isstk));
                }
                if (url.contains("Access_Control_System.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(Uri.parse(url)), "UTF-8", getResources().openRawResource(R.raw.acs));
                }
                if (url.contains("Antena_Platform_Unit_for_Tejas.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(Uri.parse(url)), "UTF-8", getResources().openRawResource(R.raw.apuft));
                }
                if (url.contains("NPR_Card_Making_Unit_at_Tirupati.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(Uri.parse(url)), "UTF-8", getResources().openRawResource(R.raw.ncmuat));
                }
                if (url.contains("Electronics_Fuzes_for_Artillery_Guns.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(Uri.parse(url)), "UTF-8", getResources().openRawResource(R.raw.effag));
                }
                if (url.contains("Hassan_Antennas_-_Banglore.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(Uri.parse(url)), "UTF-8", getResources().openRawResource(R.raw.hab));
                }
                if (url.contains("Limb_Monitor.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(Uri.parse(url)), "UTF-8", getResources().openRawResource(R.raw.lm));
                }
                if (url.contains("Mobile_system_for_Missile_Checkout.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(Uri.parse(url)), "UTF-8", getResources().openRawResource(R.raw.msfmc));
                }
                if (url.contains("Tarapur_Control_Room.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(Uri.parse(url)), "UTF-8", getResources().openRawResource(R.raw.tcr));
                }
                if (url.contains("32m_DSN_Antenna_for_Mars_Orbiter_Mission.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(Uri.parse(url)), "UTF-8", getResources().openRawResource(R.raw.dafmom));
                }
                if (url.contains("EVM_Mark_5.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(Uri.parse(url)), "UTF-8", getResources().openRawResource(R.raw.em5));
                }
                return super.shouldInterceptRequest(view, url);
            }
            else {
                if (request.getUrl().getEncodedPath().contains("Integrated_Security_System_Tyre_Killer.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(request.getUrl()), "UTF-8", getResources().openRawResource(R.raw.isstk));
                }
                if (request.getUrl().getEncodedPath().contains("Access_Control_System.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(request.getUrl()), "UTF-8", getResources().openRawResource(R.raw.acs));
                }
                if (request.getUrl().getEncodedPath().contains("Antena_Platform_Unit_for_Tejas.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(request.getUrl()), "UTF-8", getResources().openRawResource(R.raw.apuft));
                }
                if (request.getUrl().getEncodedPath().contains("NPR_Card_Making_Unit_at_Tirupati.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(request.getUrl()), "UTF-8", getResources().openRawResource(R.raw.ncmuat));
                }
                if (request.getUrl().getEncodedPath().contains("Electronics_Fuzes_for_Artillery_Guns.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(request.getUrl()), "UTF-8", getResources().openRawResource(R.raw.effag));
                }
                if (request.getUrl().getEncodedPath().contains("Hassan_Antennas_-_Banglore.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(request.getUrl()), "UTF-8", getResources().openRawResource(R.raw.hab));
                }
                if (request.getUrl().getEncodedPath().contains("Limb_Monitor.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(request.getUrl()), "UTF-8", getResources().openRawResource(R.raw.lm));
                }
                if (request.getUrl().getEncodedPath().contains("Mobile_system_for_Missile_Checkout.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(request.getUrl()), "UTF-8", getResources().openRawResource(R.raw.msfmc));
                }
                if (request.getUrl().getEncodedPath().contains("Tarapur_Control_Room.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(request.getUrl()), "UTF-8", getResources().openRawResource(R.raw.tcr));
                }
                if (request.getUrl().getEncodedPath().contains("32m_DSN_Antenna_for_Mars_Orbiter_Mission.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(request.getUrl()), "UTF-8", getResources().openRawResource(R.raw.dafmom));
                }
                if (request.getUrl().getEncodedPath().contains("EVM_Mark_5.jpg")) {
                    ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    return new WebResourceResponse(contentResolver.getType(request.getUrl()), "UTF-8", getResources().openRawResource(R.raw.em5));
                }
                return super.shouldInterceptRequest(view, request);
            }
        }*/
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

        if ((current_url.contains("http://aswini.ecil.co.in/quicklinks/")) || (current_url.contains("home"))){
            getMenuInflater().inflate(R.menu.main, menu);
            //getMenuInflater().inflate(R.menu.browser, menu);
        }
        else {
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

/*        if (item.getItemId() == R.id.action_settings) {

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
        }*/

        if (item.getItemId() == R.id.action_about) {
            webview.loadDataWithBaseURL("file:///android_res/drawable/", html, "text/html", "utf-8", null);
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
                //navigationView.getMenu().getItem(0).setChecked(false);
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.nav_att :
                //navigationView.getMenu().getItem(1).setChecked(false);
                in = new Intent(this, MainActivity.class);
                in.putExtra("url", "https://ecprdci.ecil.co.in:8443/sap/bc/gui/sap/its/zhrtmr017/");
                //in.putExtra("home", false);
                startActivity(in);
                break;

            case R.id.nav_ess :
                //navigationView.getMenu().getItem(2).setChecked(false);
                in = new Intent(this, MainActivity.class);
                in.putExtra("url", "https://ecprdci.ecil.co.in:8443/nwbc");
                startActivity(in);
                break;

            case R.id.nav_pay :
                //navigationView.getMenu().getItem(3).setChecked(false);
                in = new Intent(this, MainActivity.class);
                in.putExtra("url", "https://payslips.ecil.co.in/");
                startActivity(in);
                break;

            case R.id.nav_gst :
                //navigationView.getMenu().getItem(4).setChecked(false);
                in = new Intent(this, MainActivity.class);
                in.putExtra("url", "https://gstn.ecil.co.in:8080/");
                startActivity(in);
                break;

            case R.id.nav_car :
                //navigationView.getMenu().getItem(5).setChecked(false);
                in = new Intent(this, MainActivity.class);
                in.putExtra("url", "http://careers.ecil.co.in/");
                startActivity(in);
                break;

            case R.id.nav_emp :
                //navigationView.getMenu().getItem(6).setChecked(false);
                in = new Intent(this, MainActivity.class);
                in.putExtra("url", "http://aswini.ecil.co.in/teldir/public/");
                startActivity(in);
                break;

            case R.id.nav_ecmail :
                //navigationView.getMenu().getItem(7).setChecked(false);
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
                    alertDialog.setMessage("Please install the EC-Mail Android App from Google Play Store or Contact the administrator.");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    alertDialog.show();
                }
                break;

            case R.id.nav_bookmark :
                //navigationView.getMenu().getItem(8).setChecked(false);
                startActivity(new Intent(this, BookmarkActivity.class));
                break;

            case R.id.nav_about :
                webview.loadDataWithBaseURL("file:///android_res/drawable/", html, "text/html", "utf-8", null);
                getSupportActionBar().setTitle("About VIKAS - FLC Reporting App");
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
