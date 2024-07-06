package com.example.dsLive.liveStreamming;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.dsLive.BuildConfig;
import com.example.dsLive.R;
import com.example.dsLive.RayziUtils;
import com.example.dsLive.SessionManager;
import com.example.dsLive.agora.AgoraBaseActivity;
import com.example.dsLive.agora.stats.LocalStatsData;
import com.example.dsLive.agora.stats.RemoteStatsData;
import com.example.dsLive.agora.stats.StatsData;
import com.example.dsLive.agora.ui.VideoGridContainer;
import com.example.dsLive.bottomsheets.BottomSheetReport_g;
import com.example.dsLive.bottomsheets.BottomSheetReport_option;
import com.example.dsLive.bottomsheets.UserProfileBottomSheet;
import com.example.dsLive.databinding.ActivityWatchLiveBinding;
import com.example.dsLive.emoji.EmojiBottomSheetFragment;
import com.example.dsLive.modelclass.GiftRoot;
import com.example.dsLive.modelclass.GuestProfileRoot;
import com.example.dsLive.modelclass.LiveStramComment;
import com.example.dsLive.modelclass.LiveUserRoot;
import com.example.dsLive.modelclass.StickerRoot;
import com.example.dsLive.modelclass.UserRoot;
import com.example.dsLive.retrofit.Const;
import com.example.dsLive.socket.LiveHandler;
import com.example.dsLive.socket.MySocketManager;
import com.example.dsLive.socket.SocketConnectHandler;
import com.example.dsLive.utils.Filters.FilterRoot;
import com.example.dsLive.utils.Filters.FilterUtils;
import com.example.dsLive.viewModel.EmojiSheetViewModel;
import com.example.dsLive.viewModel.ViewModelFactory;
import com.example.dsLive.viewModel.WatchLiveViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGASoundManager;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.opensource.svgaplayer.utils.log.SVGALogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;

public class WatchLiveActivity extends AgoraBaseActivity {

    private SVGAImageView imageView;
    private SVGAParser parser;
    private Handler watch_gift_gif_png_handler;

    public static boolean svga_locally_showing = true;
    private static final String TAG = "watchliveact";
    ActivityWatchLiveBinding binding;
    SessionManager sessionManager;
    String token = "";
    EmojiBottomSheetFragment emojiBottomsheetFragment;
    private WatchLiveViewModel viewModel;
    private LiveUserRoot.UsersItem host;
    SocketConnectHandler socketConnectHandler = new SocketConnectHandler() {
        @Override
        public void onConnect() {

        }

        @Override
        public void onDisconnect() {

        }

        @Override
        public void onReconnecting() {

        }

        @Override
        public void onReconnected(Object[] args) {
            Log.d(TAG, "onReconnected: " + args[0].toString());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("liveStreamingId", host.getLiveStreamingId());
                jsonObject.put("userId", sessionManager.getUser().getId());
                jsonObject.put("isLive", false);
                MySocketManager.getInstance().getSocket().emit(Const.LIVE_REJOIN, jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    };
    private VideoGridContainer mVideoGridContainer;
    private EmojiSheetViewModel giftViewModel;
    private UserProfileBottomSheet userProfileBottomSheet;
    LiveHandler liveHandler = new LiveHandler() {
        @Override
        public void onSimpleFilter(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(() -> {

                    String filtertype = null;

                    filtertype = args[0].toString();
                    FilterRoot filterRoot = new Gson().fromJson(filtertype, FilterRoot.class);
                    if (filterRoot != null) {
                        if (filterRoot.getTitle().equalsIgnoreCase("None")) {
                            binding.imgFilter.setImageDrawable(null);
                        } else {
                            Glide.with(binding.imgFilter).load(FilterUtils.getDraw(filterRoot.getTitle())).into(binding.imgFilter);
                        }
                    }

                });

            }
        }

        @Override
        public void onAnimatedFilter(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(() -> {

                    String filtertype = null;

                    filtertype = args[0].toString();
                    FilterRoot filterRoot = new Gson().fromJson(filtertype, FilterRoot.class);
                    if (filterRoot != null) {
                        if (filterRoot.getTitle().equalsIgnoreCase("None")) {
                            binding.imgFilter.setImageDrawable(null);
                        } else {
                            Glide.with(binding.imgFilter).load(FilterUtils.getDraw(filterRoot.getTitle())).into(binding.imgFilter);
                        }
                    }

                });

            }
        }

        @Override
        public void onGif(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(() -> {

                    Log.d("___CUSTOM_TAG___", "WATCH LIVE ACTIVITY onGif Called : ");
                    String data = args[0].toString();
                    if (!data.isEmpty()) {
                        StickerRoot.StickerItem sticker_dummy = new Gson().fromJson(data, StickerRoot.StickerItem.class);
                        if (sticker_dummy != null) {
                            binding.imgSticker.setImageURI(sticker_dummy.getSticker());

                            binding.imgSticker.setVisibility(View.VISIBLE);
                            new Handler(Looper.getMainLooper()).postDelayed(() -> binding.imgSticker.setVisibility(View.GONE), 3000);

                        }
                    }

                });

            }
        }

        @Override
        public void onComment(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(() -> {

                    Log.d(TAG, "commentlister : " + args[0]);

                    String data = args[0].toString();
                    if (!data.isEmpty()) {
                        try {
                            String temp_correctin = args[0].toString();
                            JSONObject json = new JSONObject(temp_correctin);
                            String comment = json.getString("comment");

                            if(comment.contains("Gifted"))
                            {
                                Log.d(TAG, "WE WILL MODIFY THIS JSON");

                                String modifiedString = comment.replaceAll("\\d", "");
                                Log.d(TAG, "modifiedString is : " +modifiedString);
                                json.put("comment", modifiedString);
                                Log.d(TAG, "modifiedJson is : " + json.toString());
                            }
                            else{
                                Log.d(TAG, "we will not modify this json ");
                            }

                            //JSONObject jsonObject = new JSONObject(data.toString());

                            LiveStramComment liveStramComment = new Gson().fromJson(json.toString(), LiveStramComment.class);

                            if (liveStramComment != null) {
                                viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
                                binding.rvComments.smoothScrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });

            }
        }



        @Override
        public void onGift(Object[] args) {
            runOnUiThread(() -> {

                Log.d("___WatchLiveActivity___", "args: : " + Arrays.toString(args));
                if (args[0] != null) {
                    String data = args[0].toString();
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        if (jsonObject.get("gift") != null) {
                            Log.d("___sticker___", "json gift : " + jsonObject.toString());
                            GiftRoot.GiftItem giftData = new Gson().fromJson(jsonObject.get("gift").toString(), GiftRoot.GiftItem.class);
                            if (giftData != null) {
                                Log.d("___sticker___", "giftData != null");
                                String finalGiftLink = null;
                                List<GiftRoot.GiftItem> giftItemList = sessionManager.getGiftsList(giftData.getCategory());
                                Log.d("___sticker___", "giftItemList.size()" + giftItemList.size());
                                for (int i = 0; i < giftItemList.size(); i++) {
                                    if (giftData.getId().equals(giftItemList.get(i).getId())) {
                                        finalGiftLink = BuildConfig.BASE_URL + giftItemList.get(i).getImage();
                                        Log.d("___sticker___", "finalGiftLink" + finalGiftLink);
                                    }
                                }
                                Log.d("___sticker___", "giftData.getType() " + giftData.getType() );
                                Log.d("___sticker___", "giftData.getCount() " + giftData.getCount() );
                                // if (giftData.getType() == 2) {
                                if (finalGiftLink != null && finalGiftLink.endsWith(".svga")) {

                                    Log.d("___sticker___", "giftReciveListnear:" + finalGiftLink);

                                    Animation zoomin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                                    binding.svgaImage.startAnimation(zoomin);

//                                binding.svgaImage.startAnimation(slide_up);

                                    if(svga_locally_showing)
                                    {
                                        StopSound();
                                        binding.imgGiftCountWatch.setVisibility(View.GONE);
                                        binding.lytGift.setVisibility(View.GONE);
                                        binding.tvGiftUserName.setVisibility(View.GONE);

                                        binding.imgGiftCountWatchSVGA.setVisibility(View.VISIBLE);
                                        Glide.with(getApplicationContext()).load(RayziUtils.getImageFromNumber(giftData.getCount()))
                                                .into(binding.imgGiftCountWatchSVGA);

                                        try {
                                            binding.imgGiftWatch.setImageDrawable(null);
                                            binding.imgGiftWatch.setVisibility(View.GONE);

                                        } catch (Exception ignored) {
                                        }

                                        SVGACallback callback = new SVGACallback() {
                                            @Override
                                            public void onPause() {

                                                // Handle pause
                                                Log.i("___stickers___WATCH","onPause Host Live Activity" );
                                            }

                                            @Override
                                            public void onFinished() {
                                                // Handle animation finished
                                                binding.lytGift.setVisibility(View.GONE);
                                                binding.tvGiftUserName.setVisibility(View.GONE);
                                                try {
                                                    if(imageView.getVisibility() == View.VISIBLE)
                                                    {
                                                        Log.i("___stickers___WATCH","onFinished Host Live Activity" );
                                                        imageView.setVisibility(View.GONE);
                                                        imageView.clear();
                                                        imageView.clearAnimation();
                                                        binding.imgGiftCountWatch.setVisibility(View.GONE);
                                                        binding.imgGiftCountWatchSVGA.setVisibility(View.GONE);
                                                    }
                                                    //drawable[0].stop();
                                                } catch (Exception e) {
                                                    Log.e("___stickers___WATCH","onFinished CATCH Host Live Activity : " + e.toString() );
                                                }
                                                //imageView.startAnimation();
                                            }

                                            @Override
                                            public void onRepeat() {
                                                // Handle repeat
                                                binding.lytGift.setVisibility(View.GONE);
                                                binding.tvGiftUserName.setVisibility(View.GONE);
                                                try {
                                                    if(imageView.getVisibility() == View.VISIBLE) {
                                                        Log.i("___stickers___WATCH", "onRepeat Host Live Activity");
                                                        imageView.setVisibility(View.GONE);
                                                        imageView.clear();
                                                        imageView.clearAnimation();
                                                        binding.imgGiftCountWatch.setVisibility(View.GONE);
                                                        binding.imgGiftCountWatchSVGA.setVisibility(View.GONE);
                                                    }
                                                    //drawable[0].stop();
                                                } catch (Exception e) {
                                                    Log.e("___stickers___WATCH","onFinished CATCH Host Live Activity : " + e.toString() );
                                                }
                                            }

                                            @Override
                                            public void onStep(int frame, double percentage) {
                                                // Handle animation step
                                                // Log.i("___stickers___WATCH","onStep " );
                                            }
                                        };
                                        imageView.setCallback(callback);
                                        String temp = giftData.getImage();
                                        //Log.i("___sticker___", "giftData.getImage() : " + temp);
                                        String second_temp = Mapping_URL_TO_LOCAL_STORAGE(temp.trim());
                                        //Log.i("___sticker___", "Mapping_URL_TO_LOCAL_STORAGE(temp.trim()) " + second_temp);


                                        //Log.i("___stickers___", "SVGA file testing" );
                                        getAssets().open(second_temp).close();
                                        //Log.i("___stickers___", "SVGA file exists IN LOCAL PATH : " + second_temp);

                                        final SVGADrawable[] drawable = new SVGADrawable[1];
                                        binding.imgGiftCountWatch.setVisibility(View.VISIBLE);

                                        //Log.i("___sticker___", "giftData.getImage() : " + temp);
                                        makeSound(temp);

                                        String name = jsonObject.getString("userName").toString();
                                        binding.tvGiftUserName.setText(name + " Sent a gift");
                                        binding.lytGift.setVisibility(View.VISIBLE);
                                        binding.tvGiftUserName.setVisibility(View.VISIBLE);

                                        parser.decodeFromAssets(second_temp, new SVGAParser.ParseCompletion() {
                                            @Override
                                            public void onError() {
                                                Log.i("___stickers___WATCH","onError " );
                                            }

                                            @Override
                                            public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {

                                                binding.imgGiftCountWatch.setVisibility(View.GONE);
                                                binding.imgGiftCountWatchSVGA.setVisibility(View.VISIBLE);
                                                // Retrieve the duration of the animation
                                                // Retrieve the duration of the animation
                                                //double duration = (double) svgaVideoEntity.getFrames() / svgaVideoEntity.getFPS();
                                                //Log.i("SVGA Animation Duration", "Duration: " + duration + " seconds");

                                                if (imageView.getVisibility() == View.VISIBLE) {

                                                    try {
                                                        Log.d("___stickers___WATCH","__________STOPPING_PREVIOUS_ANIMATON_IMMEDIATELY______ ");
                                                        stopAndTriggerFinish(imageView, callback);
                                                        imageView.setVisibility(View.GONE);
                                                        imageView.clearAnimation();
                                                        imageView.clear();
                                                        drawable[0] = null;
                                                        SVGASoundManager.INSTANCE.release();

                                                        binding.imgGiftCountWatch.setVisibility(View.GONE);
                                                        binding.imgGiftCountWatchSVGA.setVisibility(View.GONE);

                                                        binding.imgGiftCountWatchSVGA.setVisibility(View.VISIBLE);
                                                        Glide.with(getApplicationContext()).load(RayziUtils.getImageFromNumber(giftData.getCount()))
                                                                .into(binding.imgGiftCountWatchSVGA);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Log.i("___stickers___WATCH","Host LIVE catch e.printStackTrace() :" + e.toString());
                                                    }
                                                    // The view is visible
                                                }

                                                imageView.setVisibility(View.VISIBLE);

                                                Log.i("___stickers___WATCH","onComplete Host Live Activity" );
                                                drawable[0] = new SVGADrawable(svgaVideoEntity);
                                                imageView.setImageDrawable(drawable[0]);
                                                imageView.startAnimation();
                                                //SVGASoundManager.INSTANCE.setVolume(1f, svgaVideoEntity);

                                                // Get the current LayoutParams of the ImageView
//                                                new Handler().postDelayed(() -> {
//                                                    imageView.setVisibility(View.GONE);
//                                                    imageView.clear();
//                                                }, 6000);


                                            }
                                        }, new SVGAParser.PlayCallback() {
                                            @Override
                                            public void onPlay(@NonNull List<? extends File> list) {
                                                Log.i("___stickers___WATCH","onPlay Host Live Activity" );
                                            }

                                        });

                                    }
                                    else
                                    {

                                        SVGAImageView imageView = binding.svgaImage;
                                        SVGAParser parser = new SVGAParser(WatchLiveActivity.this);
                                        try {
                                            parser.decodeFromURL(new URL(finalGiftLink), new SVGAParser.ParseCompletion() {
                                                @Override
                                                public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                                                    SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                                                    imageView.setImageDrawable(drawable);
                                                    imageView.startAnimation();
                                                    Log.d("___sticker___", "setData: " + giftData.getImage());
                                                    new Handler(Looper.myLooper()).postDelayed(() -> {
                                                        binding.svgaImage.setVisibility(View.GONE);
                                                        binding.svgaImage.clear();
                                                    }, 5000);
                                                }

                                                @Override
                                                public void onError() {

                                                }
                                            }, new SVGAParser.PlayCallback() {
                                                @Override
                                                public void onPlay(@NonNull List<? extends File> list) {

                                                }
                                            });
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    //Glide.with(WatchLiveActivity.this).load(finalGiftLink).into(binding.svgaImage);

                                } else if (finalGiftLink != null && finalGiftLink.endsWith(".png") || finalGiftLink.endsWith(".gif")) {
                                    binding.imgGiftCountWatchSVGA.setVisibility(View.GONE);
                                    binding.imgGiftCountWatch.setVisibility(View.GONE);


                                    Log.d("___WatchGif___", "Previous Stage Of Watch_gift_gif_png_handler : " + watch_gift_gif_png_handler);


                                    if(watch_gift_gif_png_handler==null)
                                    {
                                        // this means 8 sec has passed succesfully
                                        watch_gift_gif_png_handler = new Handler(Looper.getMainLooper());
                                        watch_gift_gif_png_handler.postDelayed(new Runnable(){
                                            @Override
                                            public void run() {
                                                binding.lytGift.setVisibility(View.GONE);
                                                binding.tvGiftUserName.setVisibility(View.GONE);
                                                binding.tvGiftUserName.setText("");
                                                binding.imgGiftWatch.setVisibility(View.GONE);
                                                binding.imgGiftCountWatch.setVisibility(View.GONE);
                                                watch_gift_gif_png_handler = null;
                                            }
                                        }, 7000);
                                    }
                                    else
                                    {
                                        watch_gift_gif_png_handler.removeCallbacksAndMessages(null);
                                        // in the middle user has interact for anotherr stickers
                                        binding.lytGift.setVisibility(View.GONE);
                                        binding.tvGiftUserName.setVisibility(View.GONE);
                                        binding.tvGiftUserName.setText("");
                                        binding.imgGiftWatch.setVisibility(View.GONE);
                                        binding.imgGiftCountWatch.setVisibility(View.GONE);
                                        StopSound();

                                        watch_gift_gif_png_handler = new Handler(Looper.getMainLooper());
                                        watch_gift_gif_png_handler.postDelayed(new Runnable(){
                                            @Override
                                            public void run() {
                                                binding.lytGift.setVisibility(View.GONE);
                                                binding.tvGiftUserName.setVisibility(View.GONE);
                                                binding.tvGiftUserName.setText("");
                                                binding.imgGiftWatch.setVisibility(View.GONE);
                                                binding.imgGiftCountWatch.setVisibility(View.GONE);
                                                watch_gift_gif_png_handler = null;
                                            }
                                        }, 7000);
                                    }


                                    Log.d("___WatchGif___", "After Stage Of Watch_gift_gif_png_handler : " + watch_gift_gif_png_handler);


                                    try {
                                        if(imageView.getVisibility() == View.VISIBLE)
                                        {
                                            Log.i("___stickers___WATCH","onFinished Host Live Activity" );
                                            imageView.setVisibility(View.GONE);
                                            imageView.clear();
                                            imageView.clearAnimation();
                                        }
                                        //drawable[0].stop();
                                    } catch (Exception ignored) {
                                    }

                                    Log.d("___sticker___", "else if part is executing of gists png,jpeg,gif");
                                    String name = jsonObject.getString("userName").toString();
                                    binding.tvGiftUserName.setText(name + " Sent a gift");
                                    binding.lytGift.setVisibility(View.VISIBLE);
                                    binding.tvGiftUserName.setVisibility(View.VISIBLE);
                                    //Log.d("___sticker___", "else if part is executing of gists png,jpeg,gif");

//                                    Glide.with(binding.imgSticker).load(finalGiftLink).into(binding.imgSticker);

                                    String temp = giftData.getImage();
                                    //Log.i("___sticker___", "giftData.getImage() : " + temp);
                                    makeSound(temp);

                                    int lastSlashIndex = finalGiftLink.lastIndexOf("/");
                                    String ttttemp_name = "file:///android_asset/Resource/stickers/" + finalGiftLink.substring(lastSlashIndex + 1);

                                    Log.i("___WatchLiveUrl___", "ttttemp_name : "+ ttttemp_name);

                                    binding.imgGiftWatch.setVisibility(View.VISIBLE);

                                    Glide.with(binding.imgGiftWatch)
                                            .load(ttttemp_name)
                                            .into(binding.imgGiftWatch);

                                    binding.imgGiftCountWatch.setVisibility(View.VISIBLE);

                                    Glide.with(binding.imgGiftCountWatch).load(RayziUtils.getImageFromNumber(giftData.getCount()))
                                            .into(binding.imgGiftCountWatch);

                                    Log.d(TAG, "giftReciveListnear: ma jay che ++++++++++++++++++++++++++++++++" + finalGiftLink);

                                    // Example of removing callbacks (canceling the delayed task)
//                                    if(watch_gift_gif_png_handler == null) {
//                                        myRunnable_watch_gift = new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                // Task to be performed
//                                                // Perform some delayed task here
//                                                binding.lytGift.setVisibility(View.GONE);
//                                                binding.tvGiftUserName.setVisibility(View.GONE);
//                                                binding.tvGiftUserName.setText("");
//                                                binding.imgGiftWatch.setVisibility(View.GONE);
//                                                binding.imgGiftCountWatch.setVisibility(View.GONE);
//                                            }
//                                        };
//                                    }

                                    //Log.d("___WatchGif___", "Previous Stage Of Watch_gift_gif_png_handler : " + watch_gift_gif_png_handler);
//                                    if(watch_gift_gif_png_handler == null)
//                                    {
//                                        watch_gift_gif_png_handler = new Handler(Looper.getMainLooper());
//                                        Log.d("___WatchGif___", "watch_gift_gif_png_handler : " + watch_gift_gif_png_handler);
//                                        Log.d("___WatchGif___", "registering new callback" );
//                                        postDelayedAction(myRunnable_watch_gift, 8000);
//                                    }
//                                    else
//                                    {
//                                        Log.d("___WatchGif___", "else part of watch_gift_gif_png_handler : " + watch_gift_gif_png_handler);
//                                        Log.d("___WatchGif___", "___ REMOVING CALLBACKS FOR PREVIOUS RUNNABLE ___ " );
//
//                                        removeCallbacks(myRunnable_watch_gift);
//                                        watch_gift_gif_png_handler = null;
//                                        myRunnable_watch_gift=null;
//                                        watch_gift_gif_png_handler = new Handler(Looper.getMainLooper());
//                                        myRunnable_watch_gift = new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                // Task to be performed
//                                                // Perform some delayed task here
//                                                binding.lytGift.setVisibility(View.GONE);
//                                                binding.tvGiftUserName.setVisibility(View.GONE);
//                                                binding.tvGiftUserName.setText("");
//                                                binding.imgGiftWatch.setVisibility(View.GONE);
//                                                binding.imgGiftCountWatch.setVisibility(View.GONE);
//                                            }
//                                        };
//                                        postDelayedAction(myRunnable_watch_gift, 8000);
//                                    }

                                }
                            }

                        }
                    } catch (JSONException e) {
                        Log.e("___sticker___", "JSONException(e) " + e.toString());
                    } catch (IOException e) {
                        Log.e("___sticker___", "IOException(e) " + e.toString());
                    }

                }

                if (args[1] != null) {  // gift sender user
                    Log.d(TAG, "user string   : " + args[1].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[1].toString());
                        UserRoot.User user = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        if (user != null) {
                            Log.d(TAG, ":getted user    " + user.toString());
                            if (user.getId().equals(sessionManager.getUser().getId())) {
                                sessionManager.saveUser(user);
                                giftViewModel.localUserCoin.setValue(user.getRCoin());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if (args[2] != null) {   // host
                    Log.d(TAG, "host string   : " + args[2].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[2].toString());
                        UserRoot.User host = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        if (host != null) {
                            Log.d(TAG, ":getted host    " + host.toString());
                            binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            });
        }
//
//        public void postDelayedAction(final Runnable action, final long delayMillis) {
//            watch_gift_gif_png_handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    action.run();
//                    removeCallbacks(action);
//                }
//            }, delayMillis);
//        }
//
//        public void removeCallbacks(Runnable runnable) {
//            if(runnable != null)
//            {
//                watch_gift_gif_png_handler.removeCallbacks(runnable);
//                runnable = null;
//                watch_gift_gif_png_handler = null;
//            }
//            else {
//                Log.e("___WatchGif___", "___ RUNNABLE IS NULL ___ " );
//            }
//
//        }

        @Override
        public void onView(Object[] args1) {
            runOnUiThread(() -> {
                Object args = args1[0];
                Log.d(TAG, "viewListner : " + args.toString());

                try {
                    JSONArray jsonArray = new JSONArray(args.toString());
                    viewModel.liveViewUserAdapter.addData(jsonArray);
                    binding.tvViewUserCount.setText(String.valueOf(jsonArray.length()));
                    Log.d(TAG, "views2 : " + jsonArray);
                } catch (JSONException e) {
                    Log.d(TAG, "207: ");
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onGetUser(Object[] args1) {
            runOnUiThread(() -> {
                if (args1[0] != null) {
                    String data = args1[0].toString();
                    Log.d(TAG, "initLister: usr sty1 " + data);
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(data);
                    Log.d(TAG, "initLister: usr sty2 " + mJson);
                    Gson gson = new Gson();
                    GuestProfileRoot.User userData = gson.fromJson(mJson, GuestProfileRoot.User.class);

                    if (userData != null) {
                        if (userData.getUserId().equals(host.getLiveUserId())) {
                            userProfileBottomSheet.show(false, userData, host.getLiveStreamingId());
                        } else {
                            userProfileBottomSheet.show(false, userData, "");
                        }
                    }
                }
            });
        }

        @Override
        public void onBlock(Object[] args) {
            Log.d(TAG, "blockedUsersListner: " + args[0].toString());
            runOnUiThread(() -> {
                if (args[0] != null) {
                    Object data = args[0];
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        JSONArray blockedList = jsonObject.getJSONArray("blocked");
                        for (int i = 0; i < blockedList.length(); i++) {
                            Log.d(TAG, "block user : " + blockedList.get(i).toString());
                            if (blockedList.get(i).toString().equals(sessionManager.getUser().getId())) {
                                Toast.makeText(WatchLiveActivity.this, "You are blocked by host", Toast.LENGTH_SHORT).show();
                                new Handler(Looper.myLooper()).postDelayed(() -> endLive(), 500);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onLiveRejoin(Object[] args1) {

        }
    };

    public void stopAndTriggerFinish(SVGAImageView svgaImageView, SVGACallback callback) {
        svgaImageView.stopAnimation();
        if (callback != null) {
            callback.onFinished();
        }
    }
    private boolean isVideoDecoded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch_live);

        imageView = binding.svgaImage;

        imageView.setLoops(1);
        imageView.setClearsAfterDetached(true);
        imageView.setFillMode(SVGAImageView.FillMode.Clear);
        SVGALogger.INSTANCE.setLogEnabled(true);
        SVGAParser.Companion.shareParser().init(this);
        parser = new SVGAParser(WatchLiveActivity.this);

        watch_gift_gif_png_handler = new Handler(Looper.getMainLooper());


        MySocketManager.getInstance().addLiveListener(liveHandler);
        MySocketManager.getInstance().addSocketConnectHandler(socketConnectHandler);

        giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new WatchLiveViewModel()).createFor()).get(WatchLiveViewModel.class);

        sessionManager = new SessionManager(this);
        binding.setViewModel(viewModel);
        viewModel.initLister();
        giftViewModel.getGiftCategory();

        Intent intent = getIntent();
        String userStr = intent.getStringExtra(Const.DATA);
        if (userStr != null && !userStr.isEmpty()) {
            host = new Gson().fromJson(userStr, LiveUserRoot.UsersItem.class);
            token = host.getToken();

            if (!isFinishing()) {
                binding.ivProfile.setUserImage(host.getImage(), host.isIsVIP(), WatchLiveActivity.this, 16);
            }

            binding.tvCountry.setText(String.valueOf(host.getCountry()));
            if (host.getCountry() == null || host.getCountry().isEmpty()) {
                binding.tvCountry.setVisibility(View.GONE);
            }

            binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
            binding.tvName.setText(host.getName());
            binding.tvUserId.setText(host.getUsername());

            // init agora cred
            initView();
            joinChannel();
            initLister();


            // Observe changes in the RecyclerView layout to scroll to bottom
            binding.rvComments.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    binding.rvComments.scrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);

                }
            });
            binding.rvComments.scrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);
        }
    }

    private void addLessView(boolean isAdd) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("liveStreamingId", host.getLiveStreamingId());
            jsonObject.put("liveUserMongoId", host.getId());
            jsonObject.put("userId", sessionManager.getUser().getId());
            jsonObject.put("isVIP", sessionManager.getUser().isIsVIP());
            jsonObject.put("image", sessionManager.getUser().getImage());
            if (isAdd) {
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_ADDVIEW, jsonObject);
            } else {
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESSVIEW, jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void joinChannel() {
        // Initialize token, extra info here before joining channel
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name and uid that
        // you use to generate this token.
        try {


            if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
                token = null; // default, no token
            }

            // Sets the channel profile of the Agora RtcEngine.
            // The Agora RtcEngine differentiates channel profiles and applies different optimization algorithms accordingly. For example, it prioritizes smoothness and low latency for a video call, and prioritizes video quality for a video broadcast.
            rtcEngine().setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine().enableVideo();

            configVideo();
            Log.d("TAG", "joinChannel: config()getChannelName" + config().getChannelName());
            Log.d("TAG", "joinChannel: token == " + token);
            Log.d("TAG", "joinChannel: host.getChannel() ===== " + host.getChannel());
            rtcEngine().joinChannel(token, host.getChannel(), "", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mVideoGridContainer = binding.liveVideoGridLayout;
        mVideoGridContainer.setStatsManager(statsManager());
        emojiBottomsheetFragment = new EmojiBottomSheetFragment();
        userProfileBottomSheet = new UserProfileBottomSheet(this);
        if (rtcEngine() == null) {
            Log.d(TAG, "initView: rtc engine null");
            return;
        }
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endLive();
    }

    private void endLive() {
        addLessView(false);
        try {
            removeRtcVideo(0, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVideoGridContainer.removeUserVideo(0, true);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySocketManager.getInstance().removeLiveListener(liveHandler);
        MySocketManager.getInstance().removeSocketConnectHandler(socketConnectHandler);
        statsManager().clearAllData();
    }

    private void initLister() {
        viewModel.clickedComment.observe(this, user -> {
            getUser(user.getId());
        });
        viewModel.clickedUser.observe(this, user -> {
            try {
                getUser(user.get("userId").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        binding.lytHost.setOnClickListener(v -> getUser(host.getLiveUserId()));

        giftViewModel.finelGift.observe(this, giftItem -> {
            if (giftItem != null) {
                int totalCoin = giftItem.getCoin() * giftItem.getCount();
                if (sessionManager.getUser().getRCoin() < totalCoin) {
                    Toast.makeText(WatchLiveActivity.this, "You not have enough diamonds to send gift", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("senderUserId", sessionManager.getUser().getId());
                    jsonObject.put("receiverUserId", host.getLiveUserId());
                    jsonObject.put("liveStreamingId", host.getLiveStreamingId());
                    jsonObject.put("userName", sessionManager.getUser().getName());
                    jsonObject.put("coin", giftItem.getCoin() * giftItem.getCount());
                    jsonObject.put("gift", new Gson().toJson(giftItem));
                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_NORMALUSER_GIFT, jsonObject);
                    Log.d(TAG, "initLister: EVENT_NORMALUSER_GIFT ==========================");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void getUser(String userId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromUserId", sessionManager.getUser().getId());
            jsonObject.put("toUserId", userId);
            MySocketManager.getInstance().getSocket().emit(Const.EVENT_GET_USER, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickSendComment(View view) {
        String comment = binding.etComment.getText().toString();
        if (!comment.isEmpty()) {
            binding.etComment.setText("");
            LiveStramComment liveStramComment = new LiveStramComment(host.getLiveStreamingId(), comment, sessionManager.getUser(), false);
            MySocketManager.getInstance().getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));
        }
    }

    public void onGiftSendAddComment(String selectedGiftJson) {
        String comment = "\uD83C\uDF81 Gifted:";
        try {
            // Parse the JSON string to extract the filename from the image path
            JSONObject giftJson = new JSONObject(selectedGiftJson);
            String imagePath = giftJson.getString("image");
            String filename = imagePath.substring(imagePath.lastIndexOf('/') + 1, imagePath.lastIndexOf('.'));

            // Include the filename in the comment
            comment += " " + filename;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.etComment.setText("");
        LiveStramComment liveStramComment = new LiveStramComment(host.getLiveStreamingId(), comment, sessionManager.getUser(), false);
        MySocketManager.getInstance().getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));
    }



    public void onclickShare(View view) {

        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle("Watch Live Video")
                .setContentDescription("By : " + host.getName())
                .setContentImageUrl(host.getImage())
                .setContentMetadata(new ContentMetadata().addCustomMetadata("type", "LIVE").addCustomMetadata(Const.DATA, new Gson().toJson(host)));

        LinkProperties lp = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")

                .addControlParameter("", "")
                .addControlParameter("", Long.toString(Calendar.getInstance().getTimeInMillis()));

        buo.generateShortUrl(this, lp, (url, error) -> {
            Log.d(TAG, "initListnear: branch url" + url);
            try {
                Log.d(TAG, "initListnear: share");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareMessage = url;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                Log.d(TAG, "initListnear: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onErr(int err) {
        Log.d(TAG, "onErr: " + err);
    }

    @Override
    public void onConnectionLost() {
        Log.d(TAG, "onConnectionLost: ");
    }

    @Override
    public void onVideoStopped() {
        Log.d(TAG, "onVideoStopped: ");
    }


    public void onclickGiftIcon(View view) {
        emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        runOnUiThread(() -> {
            binding.hostProfileBig.setVisibility(View.GONE);
            binding.mining.setVisibility(View.GONE);
            isVideoDecoded = true;
            renderRemoteUser(uid);
            addLessView(true);
        });
    }

    private void renderRemoteUser(int uid) {
        Log.d(TAG, "renderRemoteUser: ");
        SurfaceView surface = prepareRtcVideo(uid, false);
        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
        LiveStramComment liveStramComment = new LiveStramComment(host.getLiveStreamingId(), "", sessionManager.getUser(), true);
        MySocketManager.getInstance().getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
        mVideoGridContainer.removeUserVideo(uid, false);
    }


    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        Log.d(TAG, "onLeaveChannel: stts" + stats);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        runOnUiThread(() -> new Handler().postDelayed(() -> {
            if (isVideoDecoded) {
                Log.d(TAG, "onJoinChannelSuccess: isVideoDecoded true male che -========= ");
            } else {
                Toast.makeText(WatchLiveActivity.this, "Live has Ended.", Toast.LENGTH_SHORT).show();
                endLive();
            }
            Log.d(TAG, "onJoinChannelSuccess: isVideoDecoded === " + isVideoDecoded);
        }, 4000));
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.d(TAG, "onUserOffline: " + uid + " reason" + reason);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteUser(uid);
                endLive();
            }
        });
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.d(TAG, "onUserJoined: " + uid + "  elapsed" + elapsed);
    }

    @Override
    public void onLastmileQuality(int quality) {

    }


    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {

    }


    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;
        VideoEncoderConfiguration.VideoDimensions mVideoDimension = VideoEncoderConfiguration.VD_1280x720;
        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }


    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }


    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }


    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }

    public void onClickReport(View view) {
        new BottomSheetReport_option(WatchLiveActivity.this, new BottomSheetReport_option.OnReportedListener() {
            @Override
            public void onReported() {
                new BottomSheetReport_g(WatchLiveActivity.this, host.getLiveUserId(), () -> {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout,
                            (ViewGroup) findViewById(R.id.customtoastlyt));

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                });
            }

            @Override
            public void onBlocked() {
                finish();
            }
        });

    }

    private String Mapping_URL_TO_LOCAL_STORAGE(String URL_String)
    {
        Log.i("___stickers___", "Mapping_URL_TO_LOCAL_STORAGE (URL_String) : " + URL_String);
        String fileName = getFileName(URL_String);
        Log.i("___stickers___", "Mapping_URL_TO_LOCAL_STORAGE (filename) : " + fileName);

        return "Resource/stickers/" + fileName.trim();

    }

    public static String getFileName(String filePath) {
        // Find the last occurrence of "/"
        int lastSlashIndex = filePath.lastIndexOf("/");

        // If "/" is found, extract the substring after it
        if (lastSlashIndex != -1) {
            return filePath.substring(lastSlashIndex + 1);
        } else {
            // If "/" is not found, return the original string
            return filePath;
        }
    }

}