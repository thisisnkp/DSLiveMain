package com.example.dsLive.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.dsLive.chat.MessageFragment;
import com.example.dsLive.liveStreamming.LiveListFragment;
import com.example.dsLive.posts.FeedFragmentMain;
import com.example.dsLive.videocall.OneToOneFragment;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    public ScreenSlidePagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LiveListFragment();
        } else if (position == 1) {
            return new OneToOneFragment();
        } else if (position == 2) {
            return new FeedFragmentMain();
        } else if (position == 3) {
            return new MessageFragment();
        } else {
            return new LiveListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}