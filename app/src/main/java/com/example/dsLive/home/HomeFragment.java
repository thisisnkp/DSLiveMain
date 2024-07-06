package com.example.dsLive.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.dsLive.R;
import com.example.dsLive.activity.BaseFragment;
import com.example.dsLive.databinding.FragmentHomeBinding;
import com.example.dsLive.liveStreamming.LiveFragmentMain;

import java.util.ArrayList;


public class HomeFragment extends BaseFragment {

    FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        openFragmet(new LiveFragmentMain());
//        sliderImage();

        return binding.getRoot();
    }

    public void openFragmet(Fragment fragment) {
        getChildFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameHome, fragment).commit();
    }



    public void sliderImage(){
        ArrayList<SlideModel> list= new ArrayList<>();
        list.add(new SlideModel(R.drawable.banner, ScaleTypes.FIT));
        binding.imageSlider.setImageList(list);
    }


}