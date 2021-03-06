package com.gazitf.etapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.gazitf.etapp.MainActivity;
import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.ActivitySignInBinding;
import com.gazitf.etapp.utils.AuthInputValidator;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();

    private View rootView;
    private LottieAnimationView lottieAnimationView;
    private EditText editTextEmail, editTextPassword;
    private Button buttonSignIn;
    private TextView textViewRedirectSignUp;

    private FirebaseAuth auth;
    private boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivitySignInBinding binding = ActivitySignInBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);
        lottieAnimationView = binding.animationViewAuthLogo;
        editTextEmail = binding.textInputLoginEmail;
        editTextPassword = binding.textInputLoginPassword;
        buttonSignIn = binding.buttonSignIn;
        textViewRedirectSignUp = binding.textViewRedirectSignUp;

        auth = FirebaseAuth.getInstance();

        initListeners();
    }

    // Initialize listeners
    private void initListeners() {
        buttonSignIn.setOnClickListener(view -> validate());

        textViewRedirectSignUp.setOnClickListener(view -> startSignUpActivity());
    }

    private void validate() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        isValid = true;

        if (!AuthInputValidator.validateEmail(email)) {
            editTextEmail.setError("invalid email");
            isValid = false;
        }
        if (!AuthInputValidator.validatePassword(password)) {
            editTextPassword.setError("invalid password");
            isValid = false;
        }

        if (isValid)
            signIn(email, password);
    }

    private void signIn(String email, String password) {
        setAnimation(R.raw.sign_in);
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult ->
                        startMainActivity())
                .addOnFailureListener(error -> {
                        Toast.makeText(SignInActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        setAnimation(R.raw.failed);
                });
    }

    private void startSignUpActivity() {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }

    private void startMainActivity() {
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        this.finishAffinity();
    }

    private void setAnimation(int resource) {
        lottieAnimationView.setAnimation(resource);
        lottieAnimationView.playAnimation();
    }
}