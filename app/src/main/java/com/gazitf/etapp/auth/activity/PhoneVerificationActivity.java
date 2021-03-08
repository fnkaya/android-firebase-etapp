package com.gazitf.etapp.auth.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    private LottieAnimationView animationView;
    private EditText editTextOtpCode;
    private TextView textViewCountDown;
    private Button buttonVerify;
    private Button buttonResend;

    private FirebaseAuth auth;
    private CountDownTimer countDownTimer;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private String verificationCode;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityPhoneVerificationBinding binding = ActivityPhoneVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        animationView = binding.animationViewVerificationLogo;
        editTextOtpCode = binding.textInputVerificationCode;
        textViewCountDown = binding.textViewCountDownTimer;
        buttonVerify = binding.buttonVerify;
        buttonResend = binding.buttonResend;

        auth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra("phone_number");
        initPhoneAuthCallbacks();

        getVerificationCode(phoneNumber);
        initListeners();
    }

    // Doğrulama kodu işlemlerini tanımla
    private void initPhoneAuthCallbacks() {
        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                textViewCountDown.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                textViewCountDown.setText(getString(R.string.code_expired));
            }
        };

        phoneAuthCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            // Doğrulama kodu otomatik onaylandı
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                verifyPhoneAuthentication(phoneAuthCredential);
            }

            // Doğrulama kodu alınırken hata oluştu
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                setAnimation(R.raw.failed);
                showErrorMessage(e.getLocalizedMessage());
            }

            // Doğrulama kodu otomatik onaylanmadı
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                verificationCode = verificationId;
                resendingToken = forceResendingToken;
                setAnimation(R.raw.received);

                textViewCountDown.setVisibility(View.VISIBLE);
                countDownTimer.start();
            }

            // Doğrulama kodu 60 saniye içinde onaylanmadı
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);

                buttonVerify.setVisibility(View.GONE);
                buttonResend.setVisibility(View.VISIBLE);
            }
        };
    }

    // Doğrulama kodunu alma isteği gönder
    private void getVerificationCode(String phoneNumber) {
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(PhoneVerificationActivity.this)
                .setCallbacks(phoneAuthCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    private void initListeners() {
        buttonVerify.setOnClickListener(view -> {
            String otp = editTextOtpCode.getText().toString();

            if (validate(otp)) {
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode, otp);
                verifyPhoneAuthentication(phoneAuthCredential);
            }
        });

        buttonResend.setOnClickListener(view -> {
            getResendVerificationCode(phoneNumber);
        });
    }

    // Girilen kodun uygunluğunu kontrol et
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

    // Doğrulama kodunu tekrar alma isteği gönder
    private void getResendVerificationCode(String phoneNumber) {
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(phoneAuthCallbacks)
                .setForceResendingToken(resendingToken)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
        textViewCountDown.setVisibility(View.GONE);
        buttonVerify.setVisibility(View.VISIBLE);
        buttonResend.setVisibility(View.INVISIBLE);
        setAnimation(R.raw.receiving);
    }

    // Kullanıcı girişini yap
    private void verifyPhoneAuthentication(PhoneAuthCredential phoneAuthCredential) {
        auth.signInWithCredential(phoneAuthCredential)
                .addOnSuccessListener(authResult -> {
                    returnSuccessResult();
                })
                .addOnFailureListener(error -> {
                    showErrorMessage(error.getLocalizedMessage());
                });
    }

    // Kayıt olma ekranına sms ile doğrulamanın başarılı sonuçlandığı bilgisini gönder
    private void returnSuccessResult() {
        setResult(RESULT_OK);
        finish();
    }

    private void setAnimation(int resource) {
        animationView.setAnimation(resource);
        animationView.playAnimation();
    }

    private void showErrorMessage(String errorText) {
        Toast.makeText(PhoneVerificationActivity.this, errorText, Toast.LENGTH_LONG).show();
    }
}