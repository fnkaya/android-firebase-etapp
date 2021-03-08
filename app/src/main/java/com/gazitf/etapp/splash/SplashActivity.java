package com.gazitf.etapp.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.gazitf.etapp.auth.activity.AuthActivity;
import com.gazitf.etapp.main.view.activity.MainActivity;
import com.gazitf.etapp.R;
import com.gazitf.etapp.onboard.view.OnboardActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth auth;
    private SharedPreferences onBoardingPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        new Handler().postDelayed(() -> {
            if (auth.getCurrentUser() == null) {
                onBoardingPreferences = getSharedPreferences("on_boarding_screen", MODE_PRIVATE);
                boolean isFirstTime = onBoardingPreferences.getBoolean("is_first_time", true);

                if (isFirstTime) {
                    SharedPreferences.Editor editor = onBoardingPreferences.edit();
                    editor.putBoolean("is_first_time", false);
                    editor.apply();

                    startActivity(new Intent(SplashActivity.this, OnboardActivity.class));
                } else
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));

            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            this.finish();
        }, 2000);
    }
}