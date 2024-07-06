package com.example.dsLive.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;

import com.example.dsLive.R;
import com.example.dsLive.databinding.ActivitySettingBinding;
import com.example.dsLive.popups.PopupBuilder;
import com.example.dsLive.retrofit.Const;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class SettingActivity extends BaseActivity {
    ActivitySettingBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        getWindow().setStatusBarColor(Color.parseColor("#292132"));

        binding.tvVerson.setText(getText(R.string.app_name) + " : 1.0");

        initView();
        initLister();

    }

    private void initView() {

        binding.switchNotification.setChecked(sessionManager.isNotificationOn());

        if (isRTL(this)) {
            binding.notification.setGravity(Gravity.END);
            binding.termsofservice.setGravity(Gravity.END);
            binding.privacypolicy.setGravity(Gravity.END);
            binding.aboutus.setGravity(Gravity.END);
            binding.logout.setGravity(Gravity.END);

            binding.backbtn.setScaleX(isRTL(this) ? -1 : 1);
        }
    }

    private void initLister() {

        binding.switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sessionManager.notificationOnOff(b);
            }
        });

        binding.btnLogout.setOnClickListener(v -> {
            new PopupBuilder(this).showReliteDiscardPopup("Are you sure you want logout?", "", "Continue", "Cancel", () -> {
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                googleSignInClient.signOut();
                sessionManager.saveBooleanValue(Const.ISLOGIN, false);
                Intent intent = new Intent(this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finishAffinity();
                startActivity(intent);
            });


        });
    }

    // HARDCODING THE URL;

    public void onClickTerms(View view) {
        //https://www.dslive.live/termsofuse
        WebActivity.open(this, "Terms of Service", "https://www.termsfeed.com/live/a1834906-0d59-431c-a387-835a8bc4d328");
    }


    public void onClickPrivacy(View view) {
        //https://www.dslive.live/policies
        WebActivity.open(this, "Privacy Policy", "https://www.termsfeed.com/live/65fc3b5d-2e6c-4953-be31-62acae9a6b0b");
    }

    public void onClickAbout(View view) {
        //WebActivity.open(this, "About Us", sessionManager.getSetting().getPrivacyPolicyLink());
        WebActivity.open(this, "About Us", "https://dslive.live/about.html");
    }

    public void onClickRefund(View view) {
        WebActivity.open(this, "Refund", "https://www.termsfeed.com/live/1cb9f22b-03f6-471d-bd94-ba5a2f4fede7");
    }

}