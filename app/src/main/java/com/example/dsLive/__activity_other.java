package com.example.dsLive;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class __activity_other extends AppCompatActivity {

    private WebView webView;
    private String URL = "https://pay.dslive.live?uid=";
    private String userid= "";
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other);
        LoadWeb();
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });



    }

    public void LoadWeb() {

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        sessionManager = new SessionManager(__activity_other.this);
        userid = sessionManager.getUser().getUniqueId();
        Log.i("____________________________", URL + userid);
        //"https://pay.dslive.live?uid=666eafd2be0dfb4910dc92b3"
        webView.loadUrl(URL);
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Handle the error here
                Log.e("MainApplication", "Error: " + error.getDescription());
                    Toast.makeText(view.getContext(), "Failed to load: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }

            public void onPageFinished(WebView view, String url) {
                //Hide the SwipeReefreshLayout

            }
        });
    }
}