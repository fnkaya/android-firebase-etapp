package com.gazitf.etapp.auth.activity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.gazitf.etapp.main.view.activity.MainActivity;
import com.gazitf.etapp.R;
import com.gazitf.etapp.onboard.view.OnBoardActivity;
import com.gazitf.etapp.utils.BaseActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends BaseActivity implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth auth;
    private SharedPreferences onBoardingPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void showAlertDialog() {
        new MaterialAlertDialogBuilder(this)
                .setIcon(ContextCompat.getDrawable(this, R.drawable.icon_wifi_off))
                .setTitle("Internet Bağlantı Sorunu")
                .setMessage("Lütfen isternet bağlantınızı kontrol edip tekrar deneyiniz.")
                .setPositiveButton("Ayarlar", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Çıkış", (dialog, which) -> finish())
                .show();
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
            // Internet bağlantısını kontrol et
            if (!checkInternetConnection()) {
                showAlertDialog();
                return;
            }

            // Giriş yapmış bir kullanıcı yoksa
            FirebaseUser user = auth.getCurrentUser();

            if (user == null) {
                // Kullanıcının uygulamayı ilk defa kullandığı bilgisini al
                onBoardingPreferences = getSharedPreferences("on_boarding_screen", MODE_PRIVATE);
                boolean isFirstTime = onBoardingPreferences.getBoolean("is_first_time", true);

                // ilk defa kullanıyorsa bilgiyi false olarak güncelle
                if (isFirstTime)
                    startOnBoardingActivity();
                else
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));

            } else {
                // Kullanıcının email adresi doğrulanmış mı kontrolü
                if (user.isEmailVerified())
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            }
            this.finish();
        }, 2000);
    }

    private void startOnBoardingActivity() {
        SharedPreferences.Editor editor = onBoardingPreferences.edit();
        editor.putBoolean("is_first_time", false);
        editor.apply();
        startActivity(new Intent(SplashActivity.this, OnBoardActivity.class));
    }
}