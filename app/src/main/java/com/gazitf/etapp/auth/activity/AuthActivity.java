package com.gazitf.etapp.auth.activity;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gazitf.etapp.utils.BaseActivity;
import com.gazitf.etapp.R;

public class AuthActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        overridePendingTransition(R.anim.anim_enter_fade, R.anim.anim_exit_fade);
    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(findViewById(R.id.navigation_host_fragment_auth));
        if (!navController.popBackStack())
            super.onBackPressed();
    }
}