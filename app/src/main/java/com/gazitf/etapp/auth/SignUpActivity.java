package com.gazitf.etapp.auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gazitf.etapp.MainActivity;
import com.gazitf.etapp.databinding.ActivitySignUpBinding;
import com.gazitf.etapp.utils.AuthInputValidator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private static final int PHONE_VERIFICATION_REQUEST_CODE = 202;

    private View rootView;
    private EditText editTextName, editTextEmail, editTextPhoneNumber, editTextPassword;
    private Button buttonSignUp;

    private FirebaseAuth auth;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View init
        final ActivitySignUpBinding binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);
        editTextName = binding.textInputName;
        editTextEmail = binding.textInputRegisterEmail;
        editTextPhoneNumber = binding.textInputPhoneNumber;
        editTextPassword = binding.textInputRegisterPassword;
        buttonSignUp = binding.buttonSignUp;

        // Variable init
        auth = FirebaseAuth.getInstance();

        // Methods
        initListeners();
    }

    private void initListeners() {
        buttonSignUp.setOnClickListener(view -> verifyPhoneNumber());
    }

    private void verifyPhoneNumber() {
        name = editTextName.getText().toString();
        email = editTextEmail.getText().toString();
        phoneNumber = editTextPhoneNumber.getText().toString();
        password = editTextPassword.getText().toString();

        validate();

        if (isValid)
            startVerificationActivity();
    }

    private void startVerificationActivity() {
        Intent verificationIntent = new Intent(SignUpActivity.this, PhoneVerificationActivity.class);
        verificationIntent.putExtra("phone_number", phoneNumber);
        startActivityForResult(verificationIntent, PHONE_VERIFICATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHONE_VERIFICATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK)
                linkEmailToAccount();
            else
                showErrorMessage("Telefon numarası doğrulanması sırasında bir sorun oluştu!");
        }
    }

    private void linkEmailToAccount() {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        auth.getCurrentUser().linkWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    setNameToAccount();
                })
                .addOnFailureListener(error ->
                        showErrorMessage(error.toString()));
    }

    private void setNameToAccount() {
        FirebaseUser user = auth.getCurrentUser();

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdate)
                .addOnFailureListener(error -> showErrorMessage(error.getLocalizedMessage()));

        startMainActivity();
    }

    private void startMainActivity() {
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        this.finishAffinity();
    }

    private void showErrorMessage(String errorText) {
        Toast.makeText(SignUpActivity.this, errorText, Toast.LENGTH_LONG).show();
    }

    private void validate() {
        editTextName.setError(null);
        editTextEmail.setError(null);
        editTextPhoneNumber.setError(null);
        editTextPassword.setError(null);
        isValid = true;

        if (!AuthInputValidator.validateName(name)) {
            editTextName.setError("invalid name");
            isValid = false;
        }
        if (!AuthInputValidator.validateEmail(email)) {
            editTextEmail.setError("invalid email");
            isValid = false;
        }
        if (!AuthInputValidator.validatePhoneNumber(phoneNumber)) {
            editTextPhoneNumber.setError("invalid phone number");
            isValid = false;
        }
        if (!AuthInputValidator.validatePassword(password)) {
            editTextPassword.setError("invalid password");
            isValid = false;
        }
    }
}