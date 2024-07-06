package com.example.dsLive.__Local_Stickers;

import static com.example.dsLive.__Local_Stickers.stickers_file_location.svga_test;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dsLive.R;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Test_Stickers extends AppCompatActivity {
    private SVGAImageView svgaImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        if(svga_test)
//        {
//            setContentView(R.layout.___test_image_view);
//        }
//        else {
//            setContentView(R.layout.activity_test_stickers);
//        }
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        if(svga_test)
        {
        try {
            String svg_path = "Resource/stickers/17199788687011719924717107watch3d.svga";

          //  List<String> imagePaths = stickers_file_location.getStickersFromAssets(this);

            svgaImageView = findViewById(R.id.svgaImageView);
            SVGAParser parser = new SVGAParser(this);

            // Check if file exists
            try {
                getAssets().open(svg_path).close();
                Log.i("___stickers___", "SVGA file exists");
            } catch (IOException e) {
                Log.e("___stickers___", "SVGA file does not exist", e);
                return;
            }

            parser.decodeFromAssets(svg_path, new SVGAParser.ParseCompletion() {
                @Override
                public void onError() {
                    Log.i("___stickers___","onError " );
                }

                @Override
                public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                    Log.i("___stickers___","onComplete " );
//                    SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
//                    svgaImageView.setImageDrawable(drawable);
//                    svgaImageView.startAnimation();
                }
            }, new SVGAParser.PlayCallback() {
                @Override
                public void onPlay(@NonNull List<? extends File> list) {
                    Log.i("___stickers___","onPlay " );
                }

            });

            svgaImageView.setCallback(new SVGACallback() {
                @Override
                public void onPause() {
                    // Handle pause
                    Log.i("___stickers___","onPause " );
                }

                @Override
                public void onFinished() {
                    // Handle animation finished
                    Log.i("___stickers___","onFinished " );
                    svgaImageView.startAnimation();
                }

                @Override
                public void onRepeat() {
                    // Handle repeat
                    Log.i("___stickers___","onRepeat " );
                }

                @Override
                public void onStep(int frame, double percentage) {
                    // Handle animation step
                   // Log.i("___stickers___","onStep " );
                }
            });

            } catch (Exception e) {
              e.printStackTrace();
            }
        }

        else {
            setContentView(R.layout.activity_test_stickers);
            RecyclerView recyclerView = findViewById(R.id.recyclerView___);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            List<String> imagePaths = stickers_file_location.getStickersFromAssets(this);
            // Check if file exists
            for (String imagePath : imagePaths) {
                try {
                    getAssets().open(imagePath.replace("file:///android_asset/", "")).close();
                    Log.i("___stickers___", " file exists " +imagePath);
                } catch (IOException e) {
                    Log.e("___stickers___", "SVGA file does not exist "+ imagePath + " : " + e);
                    return;
                }
            }
            ImageAdapter adapter = new ImageAdapter(this, imagePaths);
            recyclerView.setAdapter(adapter);

        }

    }
}