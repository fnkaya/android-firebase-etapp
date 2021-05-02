package com.gazitf.etapp.onboard.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.gazitf.etapp.R;
import com.gazitf.etapp.auth.activity.AuthActivity;
import com.gazitf.etapp.databinding.ActivityOnboardBinding;
import com.gazitf.etapp.onboard.adapter.SliderAdapter;

public class OnBoardActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout layoutDots;
    private Button buttonSkip, buttonGetStarted;
    private ImageButton buttonNext;
    private TextView[] dots;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActivityOnboardBinding binding = ActivityOnboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewPager = binding.viewPagerOnboard;
        layoutDots = binding.linearLayoutDots;
        buttonSkip = binding.buttonSkip;
        buttonGetStarted = binding.buttonGetStarted;
        buttonNext = binding.buttonNext;

        viewPager.setAdapter(new SliderAdapter(this));
        viewPager.addOnPageChangeListener(pageChangeListener);

        initListeners();
        addDots(0);
    }

    private void initListeners() {
        buttonSkip.setOnClickListener(view -> startAuthActivity());

        buttonGetStarted.setOnClickListener(view -> startAuthActivity());

        buttonNext.setOnClickListener(view -> viewPager.setCurrentItem(++currentPosition));
    }

    private void addDots(int position) {
        dots = new TextView[4];
        layoutDots.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);

            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getColor(R.color.colorBlack));
        }
    }

    private final ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPosition = position;

            // Son sayfada yapılacaklar
            if (position == dots.length - 1) {
                Animation bottomAnimation = AnimationUtils.loadAnimation(OnBoardActivity.this, R.anim.anim_bottom);
                buttonGetStarted.setAnimation(bottomAnimation);
                buttonGetStarted.setVisibility(View.VISIBLE);
                buttonSkip.setVisibility(View.INVISIBLE);
                buttonNext.setVisibility(View.INVISIBLE);
            } else {
                buttonGetStarted.setVisibility(View.INVISIBLE);
                buttonSkip.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void startAuthActivity() {
        startActivity(new Intent(OnBoardActivity.this, AuthActivity.class));
        this.finish();
    }
}