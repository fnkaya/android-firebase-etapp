package com.gazitf.etapp.giris;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.gazitf.etapp.MainActivity;
import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.ActivityOtpBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String otp;

    private View rootView;
    private TextInputLayout textInputLayoutVerificationCode;
    private TextInputEditText textInputVerificationCode;
    private Button buttonVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityOtpBinding binding = ActivityOtpBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);
        textInputLayoutVerificationCode = binding.textInputLayoutVerificationCode;
        textInputVerificationCode = binding.textInputVerificationCode;
        buttonVerify = binding.buttonVerify;

        auth = FirebaseAuth.getInstance();
        otp = getIntent().getStringExtra("auth");

        initListeners();

    }

    // Initialize listeners
    private void initListeners() {
        // Verify button clicked
        buttonVerify.setOnClickListener(view -> {
            String verification_code = textInputVerificationCode.getText().toString();

            if (verification_code.length() == 6){
                textInputLayoutVerificationCode.setError(null);

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otp, verification_code);
                authWithPhoneNumber(credential);
            }
            else{
                textInputLayoutVerificationCode.setError("Please enter verification code");
            }
        });
    }

    // Phone authentication manually
    private void authWithPhoneNumber(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    startActivity(new Intent(OtpActivity.this, MainActivity.class));
                    this.finishAffinity();
                })
                .addOnFailureListener(error -> {
                    Snackbar.make(rootView, error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                });
    }
}