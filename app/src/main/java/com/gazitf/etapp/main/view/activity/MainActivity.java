package com.gazitf.etapp.main.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.gazitf.etapp.R;
import com.gazitf.etapp.auth.activity.AuthActivity;
import com.gazitf.etapp.databinding.ActivityMainBinding;
import com.gazitf.etapp.main.view.fragment.HomeFragment;
import com.gazitf.etapp.main.view.fragment.MessageFragment;
import com.gazitf.etapp.main.view.fragment.PostFragment;
import com.gazitf.etapp.main.view.fragment.WatchListFragment;
import com.gazitf.etapp.profile.ProfileActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final float END_SCALE = 0.7f;

    private DrawerLayout drawerLayout;
    private NavigationView sideNavigationView;
    private ChipNavigationBar chipNavigationBar;
    private ConstraintLayout layoutContent;
    private ImageButton buttonSideNavigation;
    private Button buttonLogout;
    private CircleImageView imageViewUserProfile;
    private TextView textViewUserEmail;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        drawerLayout = binding.layoutNavigationDrawer;
        sideNavigationView = binding.sideNavigationView;
        chipNavigationBar = binding.bottomNavigationView;
        layoutContent = binding.layoutContent;
        buttonSideNavigation = binding.buttonNavigationDrawer;
        buttonLogout = binding.buttonLogout;
        imageViewUserProfile = binding.imageProfile;
        textViewUserEmail = binding.textViewUserEmail;

        auth = FirebaseAuth.getInstance();

        sideNavigationMenu();
        bottomNavigationMenu();
    }

    // Yandan açılan menünün ayarlanması
    private void sideNavigationMenu() {
        sideNavigationView.bringToFront();
        sideNavigationView.setNavigationItemSelectedListener(this);
        animateNavigationDrawer();

        buttonSideNavigation.setOnClickListener(view -> {

            if (drawerLayout.isDrawerVisible(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });

        buttonLogout.setOnClickListener(view -> auth.signOut());
    }

    // Menü açılırken gerçekleşecek animasyon
    private void animateNavigationDrawer() {
        drawerLayout.setScrimColor(getColor(R.color.colorMaterialLightGreen));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                layoutContent.setScaleX(offsetScale);
                layoutContent.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = layoutContent.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                layoutContent.setTranslationX(xTranslation);
            }
        });
    }

    // Geri tuşuna basıldığında menu nun kapatılması
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    // Açılır menü seçenekleri
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.menu_item_create:
                startActivity(new Intent(MainActivity.this, CreateActivity.class));
                break;
        }

        return true;
    }

    // Alt menü seçenekleri
    private void bottomNavigationMenu() {
        // Activity başladığında seçilecek menu item
        chipNavigationBar.setItemSelected(R.id.menu_item_home, true);
        // Activity açıldığında gösterilecek fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.bottom_navigation_host_fragment, new HomeFragment())
                .commit();

        // Menu item seçildiğinde yapılacaklar
        chipNavigationBar.setOnItemSelectedListener(id -> {
            Fragment fragment = null;

            switch (id) {
                case R.id.menu_item_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.menu_item_watchList:
                    fragment = new WatchListFragment();
                    break;
                case R.id.menu_item_message:
                    fragment = new MessageFragment();
                    break;
                case R.id.menu_item_post:
                    fragment = new PostFragment();
                    break;
            }

            // Seçilen menu item'a göre fragment gösterme
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.anim_enter_fade, R.anim.anim_exit_fade)
                    .replace(R.id.bottom_navigation_host_fragment, fragment)
                    .commit();
        });
    }

     /*
     Kullanıcı giriş ve çıkış yaptığında yapılacak işlemler
      */
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
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            this.finish();
        }
        else
            loadUserInformation(currentUser);
    }

    private void loadUserInformation(FirebaseUser user) {
        textViewUserEmail.setText(user.getEmail());
        if (user.getPhotoUrl() != null) {
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .into(imageViewUserProfile);
        }
    }
}