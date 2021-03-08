package com.gazitf.etapp.auth.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.ActivityPhoneVerificationBinding;
import com.gazitf.etapp.utils.AuthInputValidator;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {

    private static final String TAG = PhoneVerificationActivity.class.getSimpleName();
    private View rootView;
    private LottieAnimationView lottieAnimationView;
    private EditText editTextOtpCode;
    private Button buttonVerify;
    private Button buttonResend;

    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallbacks;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private String verificationCode;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityPhoneVerificationBinding binding = ActivityPhoneVerificationBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);
        lottieAnimationView = binding.animationViewVerificationLogo;
        editTextOtpCode = binding.textInputVerificationCode;
        buttonVerify = binding.buttonVerify;
        buttonResend = binding.buttonResend;

        auth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra("phone_number");
        initPhoneAuthCallbacks();

        getVerificationCode(phoneNumber);
        initListeners();
    }

    private void initListeners() {
        // Verify button clicked
        buttonVerify.setOnClickListener(view -> {
            String otp = editTextOtpCode.getText().toString();

            if (validate(otp)) {
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode, otp);
                verifyPhoneAuthentication(phoneAuthCredential);
            }
        });

        // Resend button clicked
        buttonResend.setOnClickListener(view -> {
            getResendVerificationCode(phoneNumber);
        });
    }

    private void initPhoneAuthCallbacks() {
        phoneAuthCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            // Phone number authenticate automatically verified
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                verifyPhoneAuthentication(phoneAuthCredential);
            }

            // Phone number authentication failed
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                setAnimation(R.raw.failed);
                showErrorMessage(e.getLocalizedMessage());
            }

            // User must enter verification code manually
            @Override
            public void onCodeSent(@NonNull String verificationCode, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationCode, forceResendingToken);

                PhoneVerificationActivity.this.verificationCode = verificationCode;
                PhoneVerificationActivity.this.forceResendingToken = forceResendingToken;
                setAnimation(R.raw.received);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };
    }

    private void getVerificationCode(String phoneNumber) {
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(PhoneVerificationActivity.this)
                .setCallbacks(phoneAuthCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    private void getResendVerificationCode(String phoneNumber) {
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(phoneAuthCallbacks)
                .setForceResendingToken(forceResendingToken)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    // Phone authentication manually
    private void verifyPhoneAuthentication(PhoneAuthCredential phoneAuthCredential) {
        auth.signInWithCredential(phoneAuthCredential)
                .addOnSuccessListener(authResult -> {
                    returnSuccessResult();
                })
                .addOnFailureListener(error -> {
                    showErrorMessage(error.getLocalizedMessage());
                });
    }

    private void returnSuccessResult() {
        setResult(RESULT_OK);
        finish();
    }

    private void setAnimation(int resource) {
        lottieAnimationView.setAnimation(resource);
        lottieAnimationView.playAnimation();
    }

    private void showErrorMessage(String errorText) {
        Toast.makeText(PhoneVerificationActivity.this, errorText, Toast.LENGTH_LONG).show();
    }

    private boolean validate(String otp) {
        if (!AuthInputValidator.validateOtpCode(otp)) {
            editTextOtpCode.setError("invalid code");
            return false;
        }
        else {
            editTextOtpCode.setError(null);
            return true;
        }
    }
}