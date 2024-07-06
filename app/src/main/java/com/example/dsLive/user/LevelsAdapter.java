package com.example.dsLive.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dsLive.BuildConfig;
import com.example.dsLive.MainApplication;
import com.example.dsLive.R;
import com.example.dsLive.databinding.ItemLevelBinding;
import com.example.dsLive.modelclass.LevelRoot;

import java.util.ArrayList;
import java.util.List;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.LevelsViewHolder> {

    private Context context;
    private List<LevelRoot.LevelItem> list = new ArrayList<>();

    public LevelsAdapter(List<LevelRoot.LevelItem> list) {
        this.list = list;
    }

    @Override
    public LevelsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new LevelsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level, parent, false));
    }

    @Override
    public void onBindViewHolder(LevelsViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class LevelsViewHolder extends RecyclerView.ViewHolder {
        ItemLevelBinding binding;

        public LevelsViewHolder(View itemView) {
            super(itemView);
            binding = ItemLevelBinding.bind(itemView);
        }

        public void setData(int position) {
            Glide.with(context).load(BuildConfig.BASE_URL + list.get(position).getImage())
                    .apply(MainApplication.requestOptions)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.logo);
            binding.tvCoins.setText(String.valueOf(list.get(position).getCoin()));
            binding.tvLevel.setText(String.valueOf(list.get(position).getName()));
        }
    }
}
