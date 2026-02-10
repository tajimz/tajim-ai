package com.tajimz.tajimai.aiclone.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tajimz.tajimai.R;
import com.tajimz.tajimai.databinding.FragmentAiCloneFirstBinding;

public class AiCloneFragmentFirst extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAiCloneFirstBinding binding;
        binding = FragmentAiCloneFirstBinding.inflate(inflater, container, false);
        ViewPager2 pager = getActivity().findViewById(R.id.pager2);
        binding.btnNext.setOnClickListener(v->{
            pager.setCurrentItem(1);

        });
        binding.btnBack.setOnClickListener(v->{
            getActivity().onBackPressed();
        });
        return binding.getRoot();
    }
}