package com.gazitf.etapp.main.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.gazitf.etapp.auth.activity.AuthActivity;
import com.gazitf.etapp.auth.activity.SplashActivity;
import com.gazitf.etapp.main.view.fragment.HomeFragment;
import com.gazitf.etapp.main.view.fragment.MessageFragment;
import com.gazitf.etapp.main.view.fragment.PostFragment;
import com.gazitf.etapp.R;
import com.gazitf.etapp.main.view.fragment.WatchListFragment;
import com.gazitf.etapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    private BottomNavigationView bottomNavigationView;
    private ChipNavigationBar chipNavigationBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        chipNavigationBar = binding.bottomNavigationView;

        auth = FirebaseAuth.getInstance();

        chipNavigationBar.showBadge(R.id.messageFragment, 10);
        bottomNavigationMenu();
    }

    /*private void bottomNavigationMenuV21() {
        bottomNavigationView = binding.bottomNavigationViewV21;
        NavController navController = Navigation.findNavController(this, R.id.bottom_navigation_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }*/

    private void bottomNavigationMenu() {
       chipNavigationBar.setItemSelected(R.id.homeFragment, true);

        chipNavigationBar.setOnItemSelectedListener(id -> {
            Fragment fragment = null;

            switch (id) {
                case R.id.homeFragment:
                    fragment = new HomeFragment();
                    break;
                case R.id.watchListFragment:
                    fragment = new WatchListFragment();
                    break;
                case R.id.messageFragment:
                    fragment = new MessageFragment();
                    break;
                case R.id.postFragment:
                    fragment = new PostFragment();
                    break;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.bottom_navigation_host_fragment, fragment)
                    .commit();
        });
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
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            this.finish();
        }
    }
}