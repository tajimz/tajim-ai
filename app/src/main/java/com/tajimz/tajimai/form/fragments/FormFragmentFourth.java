package com.tajimz.tajimai.form.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tajimz.tajimai.R;
import com.tajimz.tajimai.databinding.FragmentFormFourthBinding;

public class FormFragmentFourth extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentFormFourthBinding binding = FragmentFormFourthBinding.inflate(inflater, container, false);

        ViewPager2 pager = getActivity().findViewById(R.id.pager2);

        binding.btnNextEdu.setOnClickListener(v -> {
            pager.setCurrentItem(4, true);
        });

        return binding.getRoot();
    }
}
