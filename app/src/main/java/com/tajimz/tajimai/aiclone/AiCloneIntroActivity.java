package com.tajimz.tajimai.aiclone;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.tajimz.tajimai.databinding.ActivityAiCloneIntroBinding;

public class AiCloneIntroActivity extends AppCompatActivity {
    ActivityAiCloneIntroBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupWindow();
        setupPager();
    }

    private void setupWindow(){
        EdgeToEdge.enable(this);
        binding = ActivityAiCloneIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void setupPager(){
        AiClonePagerAdapter aiClonePagerAdapter = new AiClonePagerAdapter(this);
        binding.pager2.setAdapter(aiClonePagerAdapter);
        binding.pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                binding.progressBar.setProgress((int) (position + positionOffset +1) * 100 / aiClonePagerAdapter.getItemCount());
            }
        });
    }


}