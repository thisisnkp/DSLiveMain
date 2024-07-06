package com.example.dsLive.liveStreamming;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.dsLive.MyLoader;
import com.example.dsLive.R;
import com.example.dsLive.activity.BaseFragment;
import com.example.dsLive.activity.MainActivity;
import com.example.dsLive.activity.ProfileActivity;
import com.example.dsLive.adapter.DotAdaptr;
import com.example.dsLive.databinding.FragmentLiveListBinding;
import com.example.dsLive.home.adapter.BannerAdapter;
import com.example.dsLive.modelclass.LiveUserRoot;
import com.example.dsLive.retrofit.Const;
import com.example.dsLive.retrofit.RetrofitBuilder;
import com.example.dsLive.user.SearchActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LiveListFragment extends BaseFragment {

    public static final String TAG = "LiveListFragment";
    FragmentLiveListBinding binding;
    LiveListAdapter liveListAdapter = new LiveListAdapter();
    BannerAdapter bannerAdapter = new BannerAdapter();
    MyLoader myLoader = new MyLoader();
    private int start = 0;
    private String type = "All";

    public LiveListFragment() {
    }

    public LiveListFragment(String type) {
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live_list, container, false);
        binding.setLoader(myLoader);
        initView();
        initLister();
        sliderImage();
        getData(false);
        return binding.getRoot();
    }



    private void initLister() {
        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            getData(false);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            getData(true);
        });

        binding.ivProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.ivSearch.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SearchActivity.class));
            doTransition(Const.BOTTOM_TO_UP);
        });
        binding.tvPopular.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        });
        binding.tvFreshers.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MainActivity.class));

        });
        binding.tvSpotlight.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MainActivity.class));

        });
        binding.tvPksoon.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MainActivity.class));

        });

    }
    public void sliderImage(){
        ArrayList<SlideModel> list= new ArrayList<>();
        list.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));
        list.add(new SlideModel(R.drawable.sliderimg2, ScaleTypes.FIT));
        list.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        binding.imageSlider.setImageList(list);
    }

    private void getData(boolean isLoadMore) {

        if (isLoadMore) {
            start = start + Const.LIMIT;
            myLoader.isLoadingmore.set(true);
        } else {
            myLoader.isFristTimeLoading.set(true);
            start = 0;
            liveListAdapter.clear();
        }

        myLoader.noData.set(false);
        Call<LiveUserRoot> call = RetrofitBuilder.create().getLiveUsersList(sessionManager.getUser().getId(), type, "false", start, Const.LIMIT);
        call.enqueue(new Callback<LiveUserRoot>() {
            @Override
            public void onResponse(Call<LiveUserRoot> call, Response<LiveUserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getUsers().isEmpty()) {
                        liveListAdapter.addData(response.body().getUsers());
                        Log.d(TAG, "onResponse:  users ==================================================" + response.body().getUsers());
                    } else if (start == 0) {
                        myLoader.noData.set(true);
                    }
                }
                myLoader.isLoadCompleted.postValue(true);
                myLoader.isFristTimeLoading.set(false);
                myLoader.isLoadingmore.set(false);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<LiveUserRoot> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initView() {
        binding.rvVideos.setAdapter(liveListAdapter);
        int resId = R.anim.layout_anim_scale_in;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.rvVideos.setLayoutAnimation(animation);
        liveListAdapter.notifyDataSetChanged();

        bannerAdapter.addData(sessionManager.getBannerList());
        binding.rvBanner.setAdapter(bannerAdapter);
        new PagerSnapHelper().attachToRecyclerView(binding.rvBanner);
        if (bannerAdapter.getItemCount() >= 2) {
            setupLogicAutoSlider();
        }

    }

    private void setupLogicAutoSlider() {
        DotAdaptr dotAdapter = new DotAdaptr(bannerAdapter.getItemCount(), R.color.pink);
        binding.rvDots.setAdapter(dotAdapter);
        binding.rvBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager myLayoutManager = (LinearLayoutManager) binding.rvBanner.getLayoutManager();
                int scrollPosition = myLayoutManager.findFirstVisibleItemPosition();
                dotAdapter.changeDot(scrollPosition);
            }
        });
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int pos = 0;
            boolean flag = true;

            @Override
            public void run() {
                if (pos == bannerAdapter.getItemCount() - 1) {
                    flag = false;
                } else if (pos == 0) {
                    flag = true;
                }
                if (flag) {
                    pos++;
                } else {
                    pos--;
                }
                binding.rvBanner.smoothScrollToPosition(pos);
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 2000);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!getActivity().isFinishing()) {
            binding.ivProfile.setUserImage(sessionManager.getUser().getImage(), sessionManager.getUser().isIsVIP(), getContext(), 10);
        }
    }
}