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
import androidx.databinding.DataBindingUtil;

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

    private static final long CODE_EXPIRATION_TIME = 60L;

    private LottieAnimationView animationView;
    private EditText editTextVerificationCode;
    private TextView textViewCountDown;
    private Button buttonVerify;
    private Button buttonResend;

    private FirebaseAuth auth;
    private CountDownTimer countDownTimer;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private String verificationId;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariables();
        initViews();
        initListeners();
        getVerificationCode(phoneNumber);
    }

    /*
        Değişkenlerin tanımlanması
     */
    private void initVariables() {
        auth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra("phone_number");
        initPhoneAuthCallbacks();
    }

    /*
        Arayüz elemanlarının tanımlanması
     */
    private void initViews() {
        final ActivityPhoneVerificationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_verification);
        animationView = binding.animationViewVerificationLogo;
        editTextVerificationCode = binding.textInputVerificationCode;
        textViewCountDown = binding.textViewCountDownTimer;
        buttonVerify = binding.buttonVerify;
        buttonResend = binding.buttonResend;
    }

    /*
        Arayüz elemanları için olay dinlenme fonksiyonlarının tanımlanması
     */
    private void initListeners() {
        // Onayla butonu tıklandığında
        buttonVerify.setOnClickListener(view -> {
            String verificationCode = editTextVerificationCode.getText().toString();

            if (validate(verificationCode)) {
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
                verifyPhoneAuthentication(phoneAuthCredential);
            }
        });

        // Tekrar gönder butonu tıklandığında
        buttonResend.setOnClickListener(view -> getResendVerificationCode(phoneNumber));
    }

    /*
        Geri sayım için countDownTimer ve
        Sms ile doğrulama işleminde kullanılacak callback fonksiyonların tanımlanması
     */
    private void initPhoneAuthCallbacks() {
        countDownTimer = new CountDownTimer(CODE_EXPIRATION_TIME * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long reaminingSeconds = millisUntilFinished / 1000;
                textViewCountDown.setTextColor(reaminingSeconds <= 15 ? getColor(R.color.colorRed) : getColor(R.color.colorBlack));
                textViewCountDown.setText("Seconds remaining: " + reaminingSeconds);
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

            // Doğrulama kodu otomatik onaylanamadı
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationId = s;
                resendingToken = forceResendingToken;
                setAnimation(R.raw.received);

                textViewCountDown.setVisibility(View.VISIBLE);
                countDownTimer.start();
            }

            // Doğrulama kodu belirtilen süre içerisinde otomatik onaylanamadı
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);

                buttonVerify.setVisibility(View.GONE);
                buttonResend.setVisibility(View.VISIBLE);
            }
        };
    }

    /*
        Doğrulama kodunu alma isteği gönder
     */
    private void getVerificationCode(String phoneNumber) {
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(CODE_EXPIRATION_TIME, TimeUnit.SECONDS)
                .setActivity(PhoneVerificationActivity.this)
                .setCallbacks(phoneAuthCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    /*
        Girilen kodun uygunluğunu kontrol et
     */
    private boolean validate(String verificationCode) {
        if (!AuthInputValidator.validateVerificationCode(verificationCode)) {
            editTextVerificationCode.setError("invalid code");
            return false;
        }
        else {
            editTextVerificationCode.setError(null);
            return true;
        }
    }

    /*
        Doğrulama kodunu tekrar alma isteği gönder
     */
    private void getResendVerificationCode(String phoneNumber) {
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(CODE_EXPIRATION_TIME, TimeUnit.SECONDS)
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

    /*
        Kullanıcı girişini gerçekleştir
     */
    private void verifyPhoneAuthentication(PhoneAuthCredential phoneAuthCredential) {
        auth.signInWithCredential(phoneAuthCredential)
                .addOnSuccessListener(authResult -> returnSuccessResult())
                .addOnFailureListener(error -> showErrorMessage(error.getLocalizedMessage()));
    }

    /*
        Kayıt olma ekranına sms ile doğrulamanın başarılı sonuçlandığı bilgisini gönder
     */
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