package com.example.dsLive.__Local_Stickers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dsLive.R;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SVGA = 0;
    private static final int VIEW_TYPE_IMAGE = 1;

    private final Context context;
    private final List<String> imagePaths;

    public ImageAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getItemViewType(int position) {
        String path = imagePaths.get(position);
        if (path.endsWith(".svga")) {
            return VIEW_TYPE_SVGA;
        } else {
            return VIEW_TYPE_IMAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SVGA) {
            Log.i("___123stickers___","viewType == VIEW_TYPE_SVGA  " );
            View view = LayoutInflater.from(context).inflate(R.layout.___test_image_view, parent, false);
            return new SVGAViewHolder(view);
        } else {
            Log.i("___123stickers___","viewType normal png,jpeg  " );
            View view = LayoutInflater.from(context).inflate(R.layout.___test_image_view, parent, false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);

        Log.i("___stickers___", imagePath);
        if (holder instanceof SVGAViewHolder) {
            Log.i("___123stickers___","holder instanceof SVGAViewHolder  " );

            Log.e("___stickers___", "imagePath: " + imagePath);
//            imagePath: file:///android_asset/Resource/stickers/17199788687011719924717107watch3d.svga

            // Remove "file:///android_asset/" prefix to get the asset path
            String assetPath = imagePath.replace("file:///android_asset/", "");


            Log.e("___stickers___", "assetPath: " + assetPath);
//           assetPath: Resource/stickers/17199788687011719924717107watch3d.svga


            // Check if the asset file exists
            try {
                InputStream inputStream = context.getAssets().open(assetPath);
                inputStream.close();
            } catch (IOException e) {
                Log.e("___stickers___", "SVGA file does not exist: " + assetPath, e);
                return;
            }

            SVGAViewHolder svgaHolder = (SVGAViewHolder) holder;
            SVGAParser parser = new SVGAParser(context);
            parser.decodeFromAssets(assetPath, new SVGAParser.ParseCompletion() {
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
                    SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                    svgaHolder.svgaImageView.setImageDrawable(drawable);
                    svgaHolder.svgaImageView.startAnimation();
                }
            }, new SVGAParser.PlayCallback() {
                @Override
                public void onPlay(@NonNull List<? extends File> list) {
                    Log.i("___stickers___","onPlay " );
                }

            });

            svgaHolder.svgaImageView.setCallback(new SVGACallback() {
                @Override
                public void onPause() {
                    // Handle pause
                    Log.i("___stickers___","onPause " );
                }

                @Override
                public void onFinished() {
                    // Handle animation finished
                    Log.i("___stickers___","onFinished " );
                    svgaHolder.svgaImageView.startAnimation();
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

        }
        else if (holder instanceof ImageViewHolder) {
            Log.i("___123stickers___","holder instanceof ImageViewHolder  " );
            ImageViewHolder imageHolder = (ImageViewHolder) holder;
            Glide.with(context)
                    .load(imagePath)
                    .into(imageHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    static class SVGAViewHolder extends RecyclerView.ViewHolder {
        SVGAImageView svgaImageView;

        public SVGAViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.i("___123stickers___","SVGAViewHolder extends RecyclerView.ViewHolder  " );
            svgaImageView = itemView.findViewById(R.id.svgaImageView);
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.i("___123stickers___","ImageViewHolder extends RecyclerView.ViewHolder " );
            imageView = itemView.findViewById(R.id.imageView___);
        }
    }
}
