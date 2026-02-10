package com.tajimz.tajimai.form;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.tajimz.tajimai.form.fragments.FormFragmentFifth;
import com.tajimz.tajimai.form.fragments.FormFragmentFirst;
import com.tajimz.tajimai.form.fragments.FormFragmentFourth;
import com.tajimz.tajimai.form.fragments.FormFragmentSecond;
import com.tajimz.tajimai.form.fragments.FormFragmentThird;

public class FormPagerAdapter extends FragmentStateAdapter {
    public FormPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FormFragmentFirst();
            case 1:
                return new FormFragmentSecond();
            case 2:
                return new FormFragmentThird();
            case 3:
                return new FormFragmentFourth();
            case 4:
                return new FormFragmentFifth();
            default:
                return new FormFragmentFirst();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
