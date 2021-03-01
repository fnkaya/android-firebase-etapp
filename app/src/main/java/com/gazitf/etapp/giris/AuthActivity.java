package com.gazitf.etapp.giris;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.gazitf.etapp.MainActivity;
import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.ActivityAuthBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = AuthActivity.class.getSimpleName();
    private static final int GOOGLE_SIGN_IN = 100;

    private FirebaseAuth auth;
    private GoogleSignInClient gsic;

    private View rootView;
    private LottieAnimationView lottieAnimationView;
    private TextInputLayout textInputLayoutPhoneNumber;
    private TextInputEditText textInputPhoneNumber;
    private Button buttonSendVerificationCode;
    private SignInButton buttonGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        auth = FirebaseAuth.getInstance();

        ActivityAuthBinding binding = ActivityAuthBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        lottieAnimationView = binding.animationViewAuthLogo;
        textInputLayoutPhoneNumber = binding.textInputLayoutPhoneNumber;
        textInputPhoneNumber = binding.textInputPhoneNumber;
        buttonSendVerificationCode = binding.buttonSendVerificationCode;
        buttonGoogleSignIn = binding.buttonGoogleSignIn;

        configureGoogleSignInOptions();
        initListeners();
    }

    // Configuration for google sign in
    private void configureGoogleSignInOptions() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsic = GoogleSignIn.getClient(this, gso);
    }

    // Initialize listeners
    private void initListeners() {
        // Google sign in button clicked
        buttonGoogleSignIn.setOnClickListener(view -> {
            setAnimation(R.raw.google_sign_in);
            startActivityForResult(gsic.getSignInIntent(), GOOGLE_SIGN_IN);
        });

        // Send verification button clicked
        buttonSendVerificationCode.setOnClickListener(view -> {
            String phoneNumber = textInputPhoneNumber.getText().toString();

            if (phoneNumber.length() == 13) {
                setAnimation(R.raw.verification);
                textInputLayoutPhoneNumber.setError(null);

                PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(AuthActivity.this)
                        .setCallbacks(mCallbacks)
                        .build();

                PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);

            } else {
                textInputLayoutPhoneNumber.setError("Please enter a valid phone number!");
            }
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
                showErrorMessage(e.getLocalizedMessage());
            }
        }
    }

    // Authentication with google account
    private void authWithGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    new Handler().postDelayed(this::startMainActivity, 2000);
                })
                .addOnFailureListener(error -> {
                    setAnimation(R.raw.dance);
                    showErrorMessage(error.getLocalizedMessage());
                });

    }

    // Authentication with phone number
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                // Phone number authentication automatically verified
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    authWithPhoneNumber(phoneAuthCredential);
                }

                // Phone number authentication failed
                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    setAnimation(R.raw.dance);
                    showErrorMessage(e.getLocalizedMessage());
                }

                // User must enter verification code manually
                @Override
                public void onCodeSent(@NonNull String verificationCode, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(verificationCode, forceResendingToken);

                    startOtpActivity(verificationCode);
                }
            };

    private void authWithPhoneNumber(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    startMainActivity();
                })
                .addOnFailureListener(error -> {
                    setAnimation(R.raw.dance);
                    showErrorMessage(error.getLocalizedMessage());
                });
    }

    private void startOtpActivity(@NonNull String verificationCode) {
        Intent otpIntent = new Intent(AuthActivity.this, OtpActivity.class);
        otpIntent.putExtra("auth", verificationCode);
        startActivity(otpIntent);
        setAnimation(R.raw.dance);
    }

    private void startMainActivity() {
        startActivity(new Intent(AuthActivity.this, MainActivity.class));
        this.finish();
    }

    private void setAnimation(int resource) {
        lottieAnimationView.setAnimation(resource);
        lottieAnimationView.playAnimation();
    }

    private void showErrorMessage(String errorText) {
        Snackbar.make(rootView, errorText, Snackbar.LENGTH_LONG).show();
    }
}