package com.gazitf.etapp.giris;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.gazitf.etapp.MainActivity;
import com.gazitf.etapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = AuthActivity.class.getSimpleName();
    private static final int GOOGLE_SIGN_IN = 100;

    private FirebaseAuth auth;
    private GoogleSignInClient gsic;

    private LottieAnimationView lottieAnimationView;
    private LinearProgressIndicator progressIndicator;
    private TextInputEditText textInputPhoneNumber;
    private SignInButton buttonGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        auth = FirebaseAuth.getInstance();

        lottieAnimationView = findViewById(R.id.animation_view_logo);
        progressIndicator = findViewById(R.id.progress_indicator_auth);
        textInputPhoneNumber = findViewById(R.id.text_input_phone_number_auth);
        buttonGoogleSignIn = findViewById(R.id.button_google_sign_in);

        configureGoogleSignInOptions();
        initListeners();


    }

    // Google sign in ayarları
    private void configureGoogleSignInOptions() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsic = GoogleSignIn.getClient(this, gso);
    }

    private void initListeners() {
        buttonGoogleSignIn.setOnClickListener(view -> {
            progressIndicator.setVisibility(View.VISIBLE);
            startActivityForResult(gsic.getSignInIntent(), GOOGLE_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null)
                    authWithGoogleAccount(account);

            } catch (ApiException e) {
                Log.e(TAG, "onActivityResult: ", e);
            }
        }
    }

    private void authWithGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    this.finish();
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG)
                            .show();
                });

        progressIndicator.setVisibility(View.GONE);
    }
}