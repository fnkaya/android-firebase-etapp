package com.gazitf.etapp.main.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gazitf.etapp.R;
import com.gazitf.etapp.auth.activity.AuthActivity;
import com.gazitf.etapp.auth.activity.SplashActivity;
import com.gazitf.etapp.databinding.ActivityMainBinding;
import com.gazitf.etapp.main.view.fragment.HomeFragment;
import com.gazitf.etapp.main.view.fragment.MessageFragment;
import com.gazitf.etapp.main.view.fragment.RequestListFragment;
import com.gazitf.etapp.main.view.fragment.SearchFragment;
import com.gazitf.etapp.main.view.fragment.WatchListFragment;
import com.gazitf.etapp.posts.PostsActivity;
import com.gazitf.etapp.profile.ProfileActivity;
import com.gazitf.etapp.utils.BaseActivity;
import com.gazitf.etapp.utils.LocaleHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final float END_SCALE = 0.7f;

    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;
    private NavigationView sideNavigationView;
    private Button buttonLogout;
    private MaterialAutoCompleteTextView autoCompleteTextView;
    private CircleImageView imageViewUserProfile;
    private TextView textViewUserEmail;
    private ConstraintLayout layoutContent;
    private ChipNavigationBar chipNavigationBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        initViews();
        setupBottomNavigationMenu();
        overridePendingTransition(R.anim.anim_enter_fade, R.anim.anim_exit_fade);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupLanguageSelector();
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }

    private void initViews() {
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        drawerLayout = binding.layoutNavigationDrawer;
        toolbar = binding.toolbarMain;
        sideNavigationView = binding.sideNavigationView;
        buttonLogout = binding.buttonLogout;
        autoCompleteTextView = binding.autoCompleteTextViewLanguageSelector;
        imageViewUserProfile = binding.imageProfile;
        textViewUserEmail = binding.textViewUserEmail;
        layoutContent = binding.layoutContent;
        chipNavigationBar = binding.bottomNavigationBar;
        setupSideNavigationMenu();
    }

    /*
        Yandan açılan menünün ayarlanması
    */
    private void setupSideNavigationMenu() {
        sideNavigationView.bringToFront();
        sideNavigationView.setNavigationItemSelectedListener(this);
        animateNavigationDrawer();

        toolbar.setNavigationOnClickListener(view -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });

        buttonLogout.setOnClickListener(view -> auth.signOut());
    }

    /*
        Menü açılırken gerçekleşecek animasyon
     */
    private void animateNavigationDrawer() {
//        drawerLayout.setScrimColor(getColor(R.color.colorMaterialLightGreen));
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

    /*
        Açılır menü seçenekleri
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.menu_item_posts:
                startActivity(new Intent(this, PostsActivity.class));
                break;
        }

        return true;
    }

    /*
        Geri tuşuna basıldığında menu nun kapatılması
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    /*
        Alt menü seçenekleri
     */
    @SuppressLint("NonConstantResourceId")
    private void setupBottomNavigationMenu() {
        // Activity başladığında seçilecek menu item
        chipNavigationBar.setItemSelected(R.id.menu_item_home, true);
        // Activity açıldığında gösterilecek fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.navigation_host_fragment_main, new HomeFragment())
                .commit();
        toolbar.setTitle("EtApp");

        /*
            Menu item seçildiğinde yapılacaklar
         */
        chipNavigationBar.setOnItemSelectedListener(id -> {
            Fragment fragment = null;
            String title = null;

            switch (id) {
                case R.id.menu_item_home:
                    fragment = new HomeFragment();
                    title = "EtApp";
                    break;
                case R.id.menu_item_watchList:
                    fragment = new WatchListFragment();
                    title = "Favori Etkinlikler";
                    break;
                case R.id.menu_item_message:
                    fragment = new MessageFragment();
                    title = "Mesajlar";
                    break;
                case R.id.menu_item_requestList:
                    fragment = new RequestListFragment();
                    title = "Katılım Talepleri";
                    break;
                case R.id.menu_item_search:
                    fragment = new SearchFragment();
                    title = "Etkinlik Arama";
                    break;
            }

            // Seçilen menu item'a göre fragment gösterme
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.anim_enter_fade, R.anim.anim_exit_fade)
                    .replace(R.id.navigation_host_fragment_main, fragment)
                    .commit();
            toolbar.setTitle(title);
        });
    }

    /*
       Language selector
    */
    private void setupLanguageSelector() {
        String[] languages = getResources().getStringArray(R.array.languages);
        String currentLanguage = LocaleHelper.getLanguage(this);

        // Geçerli dili dropdown menu nün default seçimi yap
        switch (currentLanguage) {
            case "tr":
                autoCompleteTextView.setText(languages[0], false);
                break;
            case "en":
                autoCompleteTextView.setText(languages[1], false);
                break;
            default:
                break;
        }

        // Geçerli dili arraydan sil
        switch (currentLanguage) {
            case "tr":
                languages = new String[] {languages[1]};
                break;
            case "en":
                languages = new String[] {languages[0]};
                break;
            default:
                break;
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_language_selector_item, languages);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            switch (currentLanguage) {
                case "tr":
                    changeLocale("en");
                    break;
                case "en":
                    changeLocale("tr");
                    break;
                default:
                    break;
            }
        });
    }

    /*
        Uygulama dilinin değiştirilmesi
     */
    private void changeLocale(String language) {
        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();
        getResources().updateConfiguration(resources.getConfiguration(), resources.getDisplayMetrics());
        startActivity(new Intent(this, SplashActivity.class));
        this.finish();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, AuthActivity.class));
            this.finish();
        } else
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