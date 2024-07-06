package com.example.dsLive.emoji;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dsLive.BuildConfig;
import com.example.dsLive.MainApplication;
import com.example.dsLive.R;
import com.example.dsLive.databinding.ItemEmojiGridBinding;
import com.example.dsLive.liveStreamming.WatchLiveActivity;
import com.example.dsLive.modelclass.GiftRoot;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EmojiGridAdapter extends RecyclerView.Adapter<EmojiGridAdapter.EmojiViewHolder> {

    OnEmojiSelectLister onEmojiSelectLister;
    List<GiftRoot.GiftItem> giftRootDummies = new ArrayList<>();
    private Context context;

    public OnEmojiSelectLister getOnEmojiSelectLister() {
        return onEmojiSelectLister;
    }

    public void setOnEmojiSelectLister(OnEmojiSelectLister onEmojiSelectLister) {
        this.onEmojiSelectLister = onEmojiSelectLister;
    }

    @Override
    public EmojiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new EmojiViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emoji_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(EmojiViewHolder holder, int position) {

        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return giftRootDummies.size();
    }

    public void addData(List<GiftRoot.GiftItem> giftRootDummy) {
        this.giftRootDummies.addAll(giftRootDummy);
        notifyItemRangeInserted(this.giftRootDummies.size(), giftRootDummy.size());
    }

    public void clear() {
        this.giftRootDummies.clear();
    }

    public class EmojiViewHolder extends RecyclerView.ViewHolder {
        ItemEmojiGridBinding binding;

        public EmojiViewHolder(View itemView) {
            super(itemView);
            binding = ItemEmojiGridBinding.bind(itemView);
        }

        public void setData(int position) {
            GiftRoot.GiftItem gift = giftRootDummies.get(position);
//            gift.getImage() type storage/1719942823526fighter-jet.svga'
//            Log.i("___sticker___", "gift type " + gift.toString());
//            Log.i("___sticker___", "gift.getSvgaImage() type " + gift.getSvgaImage());
//            Log.i("___sticker___", "gift.getImage() type " + gift.getImage());
            if (gift.getImage().endsWith("svga")) {
                // bind svga gifts
                int lastSlashIndex = gift.getImage().lastIndexOf("/");

                // If "/" is found, extract the substring after it
                if (lastSlashIndex != -1) {
                     gift.getImage().substring(lastSlashIndex + 1);
                    Log.i("___sticker___", "gift.getImage().substring(lastSlashIndex + 1) " + gift.getImage().substring(lastSlashIndex + 1));

                    SVGAImageView imageView = binding.svgaImageView;
                    SVGAParser parser = new SVGAParser(context);
                    String ttttemp_name = Mapping_URL_TO_LOCAL_STORAGE(gift.getImage().substring(lastSlashIndex + 1));
                    Log.i("___sticker___", "Mapping_URL_TO_LOCAL_STORAGE " + ttttemp_name);
                    parser.decodeFromAssets(ttttemp_name, new SVGAParser.ParseCompletion() {
                        @Override
                        public void onError() {
                            Log.i("___stickers___","onError emoji grid adapter" );
                        }

                        @Override
                        public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {

                            // Retrieve the duration of the animation
//                            double duration = (double) svgaVideoEntity.getFrames() / svgaVideoEntity.getFPS();
//                            Log.i("SVGA Animation Duration", "Duration: " + duration + " seconds");


                            Log.i("___stickers___","onComplete EMOJI GRID ADAPTER" );
                            SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                            imageView.setImageDrawable(drawable);
                            imageView.startAnimation();

                        }
                    }, new SVGAParser.PlayCallback() {
                        @Override
                        public void onPlay(@NonNull List<? extends File> list) {
                            Log.i("___stickers___","onPlay emoji grid adapter" );
                        }

                    });


                    imageView.setCallback(new SVGACallback() {
                        @Override
                        public void onPause() {
                            // Handle pause
                            Log.i("___stickers___","onPause emoji grid adapter" );
                        }

                        @Override
                        public void onFinished() {
                            // Handle animation finished
                            Log.i("___stickers___","onFinished emoji grid adapter" );
                            imageView.startAnimation();
                        }

                        @Override
                        public void onRepeat() {
                            // Handle repeat
                            Log.i("___stickers___","onRepeat emoji grid adapter" );
                        }

                        @Override
                        public void onStep(int frame, double percentage) {
                            // Handle animation step
                            // Log.i("___stickers___","onStep " );
                        }
                    });


                }
                else
                {
                    Log.e("___sticker___","gift item don't contain /");
                }
//                Log.i("___sticker___", "SVGA gift.getImage() type " + gift.getImage());



            }

            else {
                // don't bind svga gifts
                //Log.i("___sticker___", "GIF/PNG gift.getImage() type " + gift.getImage());
//                Glide.with(binding.getRoot()).load(BuildConfig.BASE_URL + gift.getImage())
//                        .apply(MainApplication.requestOptions)
//                        .thumbnail(Glide.with(context).load(R.drawable.loadergif))
//                        .into(binding.imgEmoji);

                int lastSlashIndex = gift.getImage().lastIndexOf("/");
                String ttttemp_name = "file:///android_asset/Resource/stickers/" + gift.getImage().substring(lastSlashIndex + 1);


                //String ttttemp_name = Mapping_URL_TO_LOCAL_STORAGE(gift.getImage().substring(lastSlashIndex + 1));
                Log.d("___sticker___", "GIF/PNG ttttemp_name " + ttttemp_name);

                Glide.with(binding.getRoot())
                        .load(ttttemp_name) // specify your path in the assets folder
                        .apply(MainApplication.requestOptions)
                        .thumbnail(Glide.with(context).load(R.drawable.loadergif))
                        .into(binding.imgEmoji);

            }

            binding.tvCoin.setText(String.valueOf(giftRootDummies.get(position).getCoin()));

            binding.getRoot().setOnClickListener(v -> {
                binding.itememoji.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_selected_5dp));
                onEmojiSelectLister.onEmojiSelect(binding, giftRootDummies.get(position));
            });
        }
    }

    private String Mapping_URL_TO_LOCAL_STORAGE(String URL_String)
    {
        //Log.i("___stickers___", "Mapping_URL_TO_LOCAL_STORAGE (filename) : " + URL_String);
        return "Resource/stickers/" + URL_String.trim();

    }

}
