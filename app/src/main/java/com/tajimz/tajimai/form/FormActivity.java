package com.tajimz.tajimai.form;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.tajimz.tajimai.R;
import com.tajimz.tajimai.databinding.ActivityFormBinding;

public class FormActivity extends AppCompatActivity {
    ActivityFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        setupAdapter();



    }
    private void setupWindow(){
        EdgeToEdge.enable(this);
        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupAdapter(){
        FormPagerAdapter formPagerAdapter = new FormPagerAdapter(this);
        binding.pager2.setAdapter(formPagerAdapter);
        binding.pager2.setUserInputEnabled(false);
        binding.pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                binding.progressBar.setProgress((int) (position + positionOffset +1) * 100 / formPagerAdapter.getItemCount());
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.pager2.getCurrentItem() == 0) {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                } else {
                    binding.pager2.setCurrentItem(binding.pager2.getCurrentItem() - 1);
                }
            }
        });
    }


}