package com.example.dsLive.viewModel;

import android.util.Log;

import androidx.camera.core.Preview;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dsLive.liveStreamming.FilterAdapter2;
import com.example.dsLive.liveStreamming.FilterAdapter_tt;
import com.example.dsLive.liveStreamming.HostLiveActivity;
import com.example.dsLive.liveStreamming.LiveStramCommentAdapter;
import com.example.dsLive.liveStreamming.LiveViewUserAdapter;
import com.example.dsLive.liveStreamming.StickerAdapter;
import com.example.dsLive.modelclass.StickerRoot;
import com.example.dsLive.modelclass.UserRoot;
import com.example.dsLive.utils.Filters.FilterRoot;

import org.json.JSONObject;

public class HostLiveViewModel extends ViewModel {
    public FilterAdapter_tt filterAdapter_tt = new FilterAdapter_tt();
    public FilterAdapter2 filterAdapter2 = new FilterAdapter2();
    public StickerAdapter stickerAdapter = new StickerAdapter();

    public LiveViewUserAdapter liveViewUserAdapter = new LiveViewUserAdapter();
    public LiveStramCommentAdapter liveStramCommentAdapter = new LiveStramCommentAdapter();

    public Preview preview;

    public MutableLiveData<Boolean> isShowFilterSheet = new MutableLiveData<>(false);
    public MutableLiveData<FilterRoot> selectedFilter = new MutableLiveData<>();
    public MutableLiveData<FilterRoot> selectedFilter2 = new MutableLiveData<>();
    public MutableLiveData<StickerRoot.StickerItem> selectedSticker = new MutableLiveData<com.example.dsLive.modelclass.StickerRoot.StickerItem>();


    public MutableLiveData<UserRoot.User> clickedComment = new MutableLiveData<UserRoot.User>();
    public MutableLiveData<JSONObject> clickedUser = new MutableLiveData<>();
    public boolean isMuted = false;


    public void onClickSheetClose() {
        isShowFilterSheet.setValue(false);
    }

    public void initLister() {
        filterAdapter_tt.setOnFilterClickListnear(filterRoot -> {
            Log.d(HostLiveActivity.TAG + " viewmodel", "onBindViewHolder: ===========" + filterRoot.getTitle());
            selectedFilter.setValue(filterRoot);
        });
        filterAdapter2.setOnFilterClickListnear(filterRoot -> {
            Log.d(HostLiveActivity.TAG + " viewmodel", "onBindViewHolder: ===========" + filterRoot.getTitle());
            selectedFilter2.setValue(filterRoot);
        });
        stickerAdapter.setOnStickerClickListner(filterRoot -> {
            Log.d(HostLiveActivity.TAG + " viewmodel", "onBindViewHolder: ===========" + filterRoot.getSticker());
            selectedSticker.setValue(filterRoot);
        });
        liveStramCommentAdapter.setOnCommentClickListner((UserRoot.User userDummy) -> clickedComment.setValue(userDummy));
//        liveViewUserAdapter.setOnLiveUserAdapterClickLisnter((JSONObject userDummy) -> clickedUser.setValue(userDummy));
        liveViewUserAdapter.setOnLiveUserAdapterClickLisnter((JSONObject userDummy) -> {
            // Log the userDummy
            Log.d("LiveUserAdapter___", "User clicked: " + userDummy.toString());

            // Set the clicked user
            clickedUser.setValue(userDummy);
        });
    }


}
