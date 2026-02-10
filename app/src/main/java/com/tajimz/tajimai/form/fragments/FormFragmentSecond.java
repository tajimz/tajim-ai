package com.tajimz.tajimai.form.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tajimz.tajimai.R;
import com.tajimz.tajimai.databinding.FragmentAiCloneSecondBinding;
import com.tajimz.tajimai.databinding.FragmentFormFirstBinding;
import com.tajimz.tajimai.databinding.FragmentFormSecondBinding;

public class FormFragmentSecond extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentFormSecondBinding binding;
        binding = FragmentFormSecondBinding.inflate(inflater, container, false);
        ViewPager2 pager = getActivity().findViewById(R.id.pager2);
        binding.btnNext.setOnClickListener(v->{
            pager.setCurrentItem(2, true);

        });

        return binding.getRoot();
    }
}