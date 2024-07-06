package com.example.dsLive.agora;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.dsLive.MainApplication;
import com.example.dsLive.SessionManager;
import com.example.dsLive.activity.BaseActivity;
import com.example.dsLive.agora.rtc.Constants;
import com.example.dsLive.agora.rtc.EngineConfig;
import com.example.dsLive.agora.rtc.EventHandler;
import com.example.dsLive.agora.stats.StatsManager;

import java.io.IOException;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public abstract class AgoraBaseActivity extends BaseActivity implements EventHandler {
    private static final String TAG = "agorabaseactivity";
    SessionManager sessionManager;

    private MediaPlayer player2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        registerRtcEventHandler(this);
    }

    private String FunctionMappingToStickerSound(String sticker_info)
    {
        //giraffe
        if(sticker_info.equals("1720090381762Giraffe.gif"))
        {
            return "puppyLove.mp3";
        }
        else if(sticker_info.equals("1719301477456icegif.gif"))
        {
            return "happySound.mp3";
        }
        else if(sticker_info.equals("1719301153946car-boss.gif"))
        {
            return "beHappyTwo.mp3";
        }
        else if(sticker_info.equals("1716998770754heart-10930_256.gif"))
        {
            return "beHappyOne.mp3";
        }
        else if(sticker_info.equals("17199788687011719924717107watch3d.svga")) {
            return "melody.mp3";
        }
        else if(sticker_info.equals("1719942823526fighter-jet.svga"))
        {
            return "motivational.mp3";
        }
        else if(sticker_info.equals("1719249268814babyheart.gif"))
        {
            return "popBeat.mp3";
        }
        else if(sticker_info.equals("1720092187677SuperCar.svga"))
        {
            return "rapport.mp3";
        }
        else if(sticker_info.equals("1720091700865Rocet.svga"))
        {
            return "shortGitar.mp3";
        }
        else if(sticker_info.equals("1716998770737roseflower.png"))
        {
            return "shortGitar.mp3";
        }
        else if(sticker_info.equals("1716998770768heart-117_256.gif"))
        {
            return "beHappyOne.mp3";
        }

        return "";
    }
    public void StopSound()
    {
        try {
            if (player2 != null) {
                player2.release();
                player2 = null;
            }
        } catch (Exception ignored) {

        }
    }

    public void makeSound(String sticker_info) {
        if (player2 != null) {
            player2.release();
            player2 = null;
        }
        try {
            player2 = new MediaPlayer();
            try {


                // Find the index of the last slash
                int lastIndex = sticker_info.lastIndexOf('/');

// Extract everything after the last slash
                String fileName = sticker_info.substring(lastIndex + 1);

                Log.i("___INFOMY___ : ", "sticker_info :"  + fileName.trim());

                String soundToPlay  = FunctionMappingToStickerSound(fileName.trim());

                Log.i("___INFOMY___", "soundToPlay :" + soundToPlay);

                if(soundToPlay == "")
                {
                    Toast.makeText(getApplicationContext(), " Sorry, Stickers Data Have Been Changed ", Toast.LENGTH_SHORT).show();
                    return;
                }
                //file:///android_asset/Resource
                String file_directory = "Resource/";
                String filename = file_directory + soundToPlay;

                AssetFileDescriptor afd2 = getAssets().openFd(filename);
                //AssetFileDescriptor afd2 = getAssets().openFd("beHappyOne.mp3");

                player2.setDataSource(afd2.getFileDescriptor(), afd2.getStartOffset(), afd2.getLength());
                player2.prepare();
                player2.start();

            } catch (IOException e) {
                Log.e("___INFOMY___", "IO EXCEPTION : " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("___INFOMY___", "Exception: errrr " + e.getMessage());
        }
    }


    protected MainApplication application() {
        return (MainApplication) getApplication();
    }

    protected StatsManager statsManager() {
        return application().statsManager();
    }

    protected RtcEngine rtcEngine() {
        return application().rtcEngine();
    }


    public void configVideo() {
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(
                Constants.VIDEO_DIMENSIONS[config().getVideoDimenIndex()],
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        );
        configuration.mirrorMode = Constants.VIDEO_MIRROR_MODES[config().getMirrorEncodeIndex()];
        rtcEngine().setVideoEncoderConfiguration(configuration);
    }

    protected SurfaceView prepareRtcVideo(int uid, boolean local) {
        // Render local/remote video on a SurfaceView

        SurfaceView surface = RtcEngine.CreateRendererView(getApplicationContext());
        if (local) {
            rtcEngine().setupLocalVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            0,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorLocalIndex()]
                    )
            );
        } else {
            rtcEngine().setupRemoteVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            uid,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorRemoteIndex()]
                    )
            );
        }
        return surface;
    }

    protected EngineConfig config() {
        return application().engineConfig();
    }

    protected void removeRtcVideo(int uid, boolean local) {
        if (rtcEngine() != null) {
            if (local) {
                rtcEngine().setupLocalVideo(null);
            } else {
                rtcEngine().setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid));
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rtcEngine() != null) {
            rtcEngine().leaveChannel();
        }
        removeRtcEventHandler(this);
//        getSocket().disconnect();
    }

}
