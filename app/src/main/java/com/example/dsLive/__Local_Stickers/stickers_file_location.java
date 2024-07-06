package com.example.dsLive.__Local_Stickers;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class stickers_file_location {

    public static boolean my_test_sticker = false;
    public static boolean svga_test = false;


    public static List<String> getStickersFromAssets(Context context) {


        // TODO : ALTERNATE PATH FOR STICKERS:

        // Path to the image in the assets folder
//        String imagePath = "Resource/stickers/1716998770737roseflower.png";
//
//        try {
//            // Use AssetManager to load the image
//            InputStream inputStream = getAssets().open(imagePath);
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            imageView.setImageBitmap(bitmap);
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        List<String> stickers = new ArrayList<>();

        try {
            String[] files = context.getAssets().list("Resource/stickers");
            if (files != null) {
                for (String file : files) {
                    Log.i("___stickers___","file are: "+ file );
                        stickers.add("file:///android_asset/Resource/stickers/" + file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("___stickers___", "size : " + stickers.size());
        Log.v("___stickers___", stickers.toString());
        return stickers;
    }


    public static List<String> getPNG_GIF_StickersFromAssets(Context context) {


        // TODO : ALTERNATE PATH FOR STICKERS:

        // Path to the image in the assets folder
//        String imagePath = "Resource/stickers/1716998770737roseflower.png";
//
//        try {
//            // Use AssetManager to load the image
//            InputStream inputStream = getAssets().open(imagePath);
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            imageView.setImageBitmap(bitmap);
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        List<String> stickers = new ArrayList<>();

        try {
            String[] files = context.getAssets().list("Resource/stickers");
            if (files != null) {
                for (String file : files) {
                    Log.i("___stickers___","file are: "+ file );
                    if(!file.endsWith(".svga")) {
                        stickers.add("file:///android_asset/Resource/stickers/" + file);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("___stickers___", "size : " + stickers.size());
        Log.v("___stickers___", stickers.toString());
        return stickers;
    }

    public static List<String> getSVGAStickersFromAssets(Context context) {

        List<String> svga_stickers = new ArrayList<>();

        try {
            String[] files = context.getAssets().list("Resource/stickers");
            if (files != null) {
                for (String file : files) {
                    Log.i("___stickers___","file are: "+ file );
                    if(file.endsWith(".svga"))
                    {
                        Log.i("___stickers___","SVGA files are: "+ file );
                        svga_stickers.add("file:///android_asset/Resource/stickers/" + file);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return svga_stickers;
    }
}
