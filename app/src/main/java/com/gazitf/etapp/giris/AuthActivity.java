package com.gazitf.etapp.giris;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

    private ConstraintLayout constraintLayoutAuth;
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

        constraintLayoutAuth = findViewById(R.id.constraint_layout_auth);
        lottieAnimationView = findViewById(R.id.animation_view_logo);
        textInputLayoutPhoneNumber = findViewById(R.id.text_input_layout_verification_code);
        textInputPhoneNumber = findViewById(R.id.text_input_verification_code);
        buttonSendVerificationCode = findViewById(R.id.button_verify);
        buttonGoogleSignIn = findViewById(R.id.button_google_sign_in);

        configureGoogleSignInOptions();
        initListeners();
    }

    private void configureGoogleSignInOptions() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsic = GoogleSignIn.getClient(this, gso);
    }

    private void initListeners() {
        // Google sign in button click
        buttonGoogleSignIn.setOnClickListener(view -> {
            setAnimation(R.raw.google_sign_in);
            startActivityForResult(gsic.getSignInIntent(), GOOGLE_SIGN_IN);
        });

        // Send verification button click
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
                textInputLayoutPhoneNumber.setError("Please enter invalid phone number!");
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
                showSnackbar(e.getLocalizedMessage());
            }
        }
    }

    private void authWithGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    new Handler().postDelayed(this::startMainActivity, 2000);
                })
                .addOnFailureListener(error -> {
                    setAnimation(R.raw.dance);
                    showSnackbar(error.getLocalizedMessage());
                });

    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    authWithPhoneNumber(phoneAuthCredential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    setAnimation(R.raw.dance);
                    showSnackbar(e.getLocalizedMessage());
                }

                @Override
                public void onCodeSent(@NonNull String verificationCode, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(verificationCode, forceResendingToken);

                    // sometime the code is not detected automatically o user has to manually enter the code
                    Intent otpIntent = new Intent(AuthActivity.this, OtpActivity.class);
                    otpIntent.putExtra("auth", verificationCode);
                    startActivity(otpIntent);
                    setAnimation(R.raw.dance);
                }
            };

    private void authWithPhoneNumber(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    startMainActivity();
                })
                .addOnFailureListener(error -> {
                    setAnimation(R.raw.dance);
                    showSnackbar(error.getLocalizedMessage());
                });
    }

    private void startMainActivity() {
        startActivity(new Intent(AuthActivity.this, MainActivity.class));
        this.finish();
    }

    private void setAnimation(int resource) {
        lottieAnimationView.setAnimation(resource);
        lottieAnimationView.playAnimation();
    }

    private void showSnackbar(String errorText) {
        Snackbar.make(constraintLayoutAuth, errorText, Snackbar.LENGTH_LONG).show();
    }
}