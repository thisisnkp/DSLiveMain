package com.example.dsLive.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.dsLive.MainApplication;
import com.example.dsLive.NetWorkChangeReceiver;
import com.example.dsLive.R;
import com.example.dsLive.adapter.ScreenSlidePagerAdapter;
import com.example.dsLive.databinding.ActivityMainBinding;
import com.example.dsLive.liveStreamming.WatchLiveActivity;
import com.example.dsLive.modelclass.LiveUserRoot;
import com.example.dsLive.modelclass.PostRoot;
import com.example.dsLive.modelclass.ReliteRoot;
import com.example.dsLive.popups.PrivacyPopup_g;
import com.example.dsLive.posts.FeedListActivity;
import com.example.dsLive.reels.ReelsActivity;
import com.example.dsLive.retrofit.Const;
import com.example.dsLive.socket.MySocketManager;
import com.example.dsLive.user.guestUser.GuestActivity;
import com.google.gson.Gson;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

//import me.ibrahimsn.lib.OnItemSelectedListener;
//import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    final Context context = this;
    private NetWorkChangeReceiver netWorkChangeReceiver;
    ScreenSlidePagerAdapter screenSlidePagerAdapter;
    private int position = 0;

    @Override
    protected void onStart() {
        super.onStart();
        MainApplication.isAppOpen = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // Find the menu item by id
        int menuItemId = R.id.miHome;
        View menuItemView = findViewById(menuItemId);
        // Perform click action programmatically
        menuItemView.performClick();

        if (!MySocketManager.getInstance().globalConnecting || !MySocketManager.getInstance().globalConnected) {
            getApp().initGlobalSocket();
        }

        initRequest();
        initMain();

    }

    private void initRequest() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        } else {
            if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        if (!sessionManager.getBooleanValue(Const.POLICY_ACCEPTED)) {
            new PrivacyPopup_g(this, new PrivacyPopup_g.OnSubmitClickListnear() {
                @Override
                public void onAccept() {
                    sessionManager.saveBooleanValue(Const.POLICY_ACCEPTED, true);
                }

                @Override
                public void onDeny() {
                    finishAffinity();
                }

            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getString(R.string.per_deny), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getString(R.string.per_deny), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        initMain();
    }

    private void initMain() {
        Log.w("__BASE_ACTIVITY__", "Calling InitMain() from MainActivity");
        getStrickers();
        getAdsKeys();
        startReceiver();
        handleBranchData();
        makeOnlineUser();
        initBottomBar();
        screenSlidePagerAdapter = new ScreenSlidePagerAdapter(MainActivity.this);
        binding.viewpagerMain.setAdapter(screenSlidePagerAdapter);
        binding.viewpagerMain.setUserInputEnabled(false);




    }

    private void handleBranchData() {
        Intent intent = getIntent();
        String branchData = intent.getStringExtra(Const.DATA);
        String type = intent.getStringExtra(Const.TYPE);
        if (branchData != null && !branchData.isEmpty()) {
            switch (type) {
                case "POST" -> {
                    PostRoot.PostItem post = new Gson().fromJson(branchData, PostRoot.PostItem.class);
                    List<PostRoot.PostItem> list = new ArrayList<>();
                    list.add(post);
                    startActivity(new Intent(this, FeedListActivity.class).putExtra(Const.POSITION, 0).putExtra(Const.DATA, new Gson().toJson(list)));

                }
                case "RELITE" -> {
                    ReliteRoot.VideoItem post = new Gson().fromJson(branchData, ReliteRoot.VideoItem.class);
                    List<ReliteRoot.VideoItem> list = new ArrayList<>();
                    list.add(post);
                    startActivity(new Intent(this, ReelsActivity.class).putExtra(Const.POSITION, 0).putExtra(Const.DATA, new Gson().toJson(list)));
                }
                case "PROFILE" -> {
                    startActivity(new Intent(this, GuestActivity.class).putExtra(Const.USERID, branchData));
                }
                case "LIVE" -> {
                    LiveUserRoot.UsersItem usersItem = new Gson().fromJson(branchData, LiveUserRoot.UsersItem.class);
                    Log.d("TAG", "handleBranchData: live  " + usersItem.toString());
                    startActivity(new Intent(this, WatchLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(usersItem)));
                }
            }
        }
    }

    private void setUpFragment(int position) {
        binding.viewpagerMain.setCurrentItem(position);
    }




    @SuppressLint("NonConstantResourceId")
    private void initBottomBar() {
        int lastColor;
        View  container = findViewById(R.id.container);
        TextView  title = findViewById(R.id.title);
        ChipNavigationBar menu = findViewById(R.id.bottom_menu);


        lastColor = ((ColorDrawable) container.getBackground()).getColor();

        // Delayed execution to ensure view is fully initialized

        menu.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                int color;
                String text;
                switch (id) {
                    case R.id.miHome:

                        color = ContextCompat.getColor(MainActivity.this, R.color.pink);
                        changeFragment(0, id);
                        text = "Home";
                        return;
                    case R.id.placeholder:
                        color = ContextCompat.getColor(MainActivity.this, R.color.pink);
                        startActivity(new Intent(MainActivity.this, GotoLiveActivityNew.class));
                        text = "Live";
                        return;
                    case R.id.miFeed:
                        color = ContextCompat.getColor(MainActivity.this, R.color.pink);
                        changeFragment(2, id);
                        text = "Reels";
                        return;
                    case R.id.miMessage:
                        color = ContextCompat.getColor(MainActivity.this, R.color.pink);
                        changeFragment(3, id);
                        text = "Messages";
                        return;
                    default:
                        return;
                }

            }
        });
        menu.setItemEnabled(R.id.miHome, true);


    }

        // SmoothBottomBar bottomBar = findViewById(R.id.bottomBar);

       /* bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int itemId) {
                switch (itemId) {
                    case R.id.miHome:
                        changeFragment(0, itemId);
                        return true;
                    case R.id.miRandomCall:
                        changeFragment(1, itemId);
                        return true;
                    case R.id.placeholder:
                        startActivity(new Intent(MainActivity.this, GotoLiveActivityNew.class));
                        return true;
                    case R.id.miFeed:
                        changeFragment(2, itemId);
                        return true;
                    case R.id.miMessage:
                        changeFragment(3, itemId);
                        return true;
                    default:
                        return false;
                }
            }
        });*/




    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        MainApplication.isAppOpen = false;
        super.onDestroy();
    }

    protected void startReceiver() {
        netWorkChangeReceiver = new NetWorkChangeReceiver(this::showHideInternet);
        registerNetworkBroadcastForNougat();
    }

    private void registerNetworkBroadcastForNougat() {
        registerReceiver(netWorkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(netWorkChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    private void showHideInternet(Boolean isOnline) {
        Log.d("TAG", "showHideInternet: " + isOnline);
        final TextView tvInternetStatus = findViewById(R.id.tv_internet_status);

        if (isOnline) {
            if (tvInternetStatus != null && tvInternetStatus.getVisibility() == View.VISIBLE && tvInternetStatus.getText().toString().equalsIgnoreCase(getString(R.string.no_internet_connection))) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                tvInternetStatus.setText(R.string.back_online);
                new Handler().postDelayed(() -> slideToTop(tvInternetStatus), 200);
            }
        } else {
            if (tvInternetStatus != null) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                tvInternetStatus.setText(R.string.no_internet_connection);
                if (tvInternetStatus.getVisibility() == View.GONE) {
                    slideToBottom(tvInternetStatus);
                }
            }
        }
    }

    private void slideToTop(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        view.startAnimation(animation);
    }

    private void slideToBottom(final View view) {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

        view.startAnimation(animation);
    }

    public void changeFragment(int position, int itemId) {
        // Handle fragment transaction based on position (if needed)
        setUpFragment(position);

        // Optionally, if you want to select an item programmatically
    //    SmoothBottomBar bottomBar = findViewById(R.id.bottomBar);
       // bottomBar.setItemActiveIndex(itemId); // Use appropriate method if available
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpFragment(position);
        changeFragment(0, R.id.miHome);
    }

    @Override
    public void onBackPressed() {

        if (binding.viewpagerMain.getCurrentItem() == 0) {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.exitdialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView yes = dialog.findViewById(R.id.tv_exit);
            TextView no = dialog.findViewById(R.id.tvno);

            yes.setOnClickListener(v -> {
                super.onBackPressed();
                dialog.dismiss();
                finishAffinity();
            });

            no.setOnClickListener(v -> {
                dialog.dismiss();

            });

            dialog.show();
        } else {
            changeFragment(0, R.id.miHome);
        }

    }
}