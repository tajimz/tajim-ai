package com.tajimz.tajimai.aiclone;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.tajimz.tajimai.aiclone.fragments.AiCloneFragmentFirst;
import com.tajimz.tajimai.aiclone.fragments.AiCloneFragmentSecond;

public class AiClonePagerAdapter extends FragmentStateAdapter {
    public AiClonePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new AiCloneFragmentFirst();
            case 1:
                return new AiCloneFragmentSecond();
            default:
                return new AiCloneFragmentFirst();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
