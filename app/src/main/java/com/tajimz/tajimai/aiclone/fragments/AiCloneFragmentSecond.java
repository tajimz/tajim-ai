package com.tajimz.tajimai.aiclone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tajimz.tajimai.R;
import com.tajimz.tajimai.databinding.FragmentAiCloneSecondBinding;
import com.tajimz.tajimai.form.FormActivity;


public class AiCloneFragmentSecond extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAiCloneSecondBinding binding;
        binding = FragmentAiCloneSecondBinding.inflate(inflater, container, false);
        ViewPager2 pager = getActivity().findViewById(R.id.pager2);

        binding.btnNext.setOnClickListener(v->{

            getActivity().startActivity(new Intent(getActivity(), FormActivity.class));
            getActivity().finish();

        });
        binding.btnBack.setOnClickListener(v->{
            pager.setCurrentItem(0);
        });
        return binding.getRoot();
    }
}