package com.tajimz.tajimai.form.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tajimz.tajimai.R;
import com.tajimz.tajimai.databinding.FragmentFormThirdBinding;

public class FormFragmentThird extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentFormThirdBinding binding = FragmentFormThirdBinding.inflate(inflater, container, false);

        ViewPager2 pager = getActivity().findViewById(R.id.pager2);

        binding.btnNextHealth.setOnClickListener(v -> {
            pager.setCurrentItem(3, true);

        });

        return binding.getRoot();
    }
}
