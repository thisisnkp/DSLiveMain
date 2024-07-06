package com.example.dsLive.viewModel;

import androidx.camera.core.Preview;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dsLive.liveStreamming.LiveStramCommentAdapter;
import com.example.dsLive.liveStreamming.LiveViewUserAdapter;
import com.example.dsLive.modelclass.UserRoot;

import org.json.JSONObject;

public class WatchLiveViewModel extends ViewModel {
    public Preview preview;


    public LiveViewUserAdapter liveViewUserAdapter = new LiveViewUserAdapter();
    public LiveStramCommentAdapter liveStramCommentAdapter = new LiveStramCommentAdapter();
    public MutableLiveData<UserRoot.User> clickedComment = new MutableLiveData<>();
    public MutableLiveData<JSONObject> clickedUser = new MutableLiveData<>();

    public void initLister() {
        liveStramCommentAdapter.setOnCommentClickListner((UserRoot.User userDummy) -> {
            clickedComment.setValue(userDummy);
        });
        liveViewUserAdapter.setOnLiveUserAdapterClickLisnter((JSONObject userDummy) -> clickedUser.setValue(userDummy));

    }

}
