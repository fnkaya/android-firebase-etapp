package com.gazitf.etapp.auth.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.gazitf.etapp.R;
import com.gazitf.etapp.auth.activity.AuthActivity;
import com.gazitf.etapp.auth.activity.PhoneVerificationActivity;
import com.gazitf.etapp.databinding.FragmentRegisterBinding;
import com.gazitf.etapp.utils.AuthInputValidator;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterFragment extends Fragment {

    private static final int PHONE_VERIFICATION_REQUEST_CODE = 202;

    private FragmentRegisterBinding binding;
    private Toolbar toolbar;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPhoneNumber, inputLayoutPassword;
    private EditText editTextName, editTextEmail, editTextPhoneNumber, editTextPassword;
    private Button buttonRegister;
    private TextView textViewRedirectToLogin;

    private FirebaseAuth auth;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = binding.toolbarRegister;
        inputLayoutName = binding.textInputLayoutName;
        inputLayoutEmail = binding.textInputLayoutRegisterEmail;
        inputLayoutPhoneNumber = binding.textInputLayoutPhoneNumber;
        inputLayoutPassword = binding.textInputLayoutRegisterPassword;
        editTextName = binding.textInputName;
        editTextEmail = binding.textInputRegisterEmail;
        editTextPhoneNumber = binding.textInputPhoneNumber;
        editTextPassword = binding.textInputRegisterPassword;
        buttonRegister = binding.buttonRegister;
        textViewRedirectToLogin = binding.textViewRedirectLogin;

        setupToolbar();

        auth = FirebaseAuth.getInstance();

        initListeners();
    }

    private void setupToolbar() {
        AuthActivity authActivity = (AuthActivity) requireActivity();
        authActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = authActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
        }
    }

    private void initListeners() {
        buttonRegister.setOnClickListener(view -> validate());

        textViewRedirectToLogin.setOnClickListener(this::navigateToLogin);

        toolbar.setNavigationOnClickListener(this::navigateToLogin);
    }

    private void validate() {
        name = editTextName.getText().toString();
        email = editTextEmail.getText().toString();
        phoneNumber = editTextPhoneNumber.getText().toString();
        password = editTextPassword.getText().toString();

        inputLayoutName.setError(null);
        inputLayoutEmail.setError(null);
        inputLayoutPhoneNumber.setError(null);
        inputLayoutPassword.setError(null);
        boolean isValid = true;

        if (!AuthInputValidator.validateName(name)) {
            inputLayoutName.setError(getString(R.string.invalid_name));
            isValid = false;
        }
        if (!AuthInputValidator.validateEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.invalid_email));
            isValid = false;
        }
        if (!AuthInputValidator.validatePhoneNumber(phoneNumber)) {
            inputLayoutPhoneNumber.setError(getString(R.string.invalid_phone_number));
            isValid = false;
        }
        if (!AuthInputValidator.validatePassword(password)) {
            inputLayoutPassword.setError(getString(R.string.invalid_password));
            isValid = false;
        }

        if (isValid)
            startVerificationActivity();
    }

    // Doğrulama kodu alınacak sayfayı aç
    private void startVerificationActivity() {
        Intent verificationIntent = new Intent(getActivity(), PhoneVerificationActivity.class);
        verificationIntent.putExtra("phone_number", phoneNumber);
        startActivityForResult(verificationIntent, PHONE_VERIFICATION_REQUEST_CODE);
    }

    // Doğrulama kodu sayfasından gelen sonucu al
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHONE_VERIFICATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK)
                linkEmailToAccount();
            else
                showToastMessage("Telefon numarası doğrulanması sırasında bir sorun oluştu!");
        }
    }

    // Email adresini onaylanan telefon numarasına bağla
    private void linkEmailToAccount() {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        auth.getCurrentUser().linkWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    sendVerificationEmail(user);
                    setNameToAccount(user);
                })
                .addOnFailureListener(error ->
                        showToastMessage(error.toString()));
    }

    // Email doğrulama bağlantısı gönder
    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(authResult -> {
                    showToastMessage("Lütfen e-mail adresine gönderilen bağlantıya tıklayarak e-mail adresinizi doğrulayınız.");
                });
    }

    // Kullanıcı ismini güncelle
    private void setNameToAccount(FirebaseUser user) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdate)
                .addOnFailureListener(error -> showToastMessage(error.getLocalizedMessage()));

        navigateToLogin(getView());
    }


    private void showToastMessage(String errorText) {
        Toast.makeText(getActivity(), errorText, Toast.LENGTH_LONG).show();
    }

    private void navigateToLogin(View view) {
        NavDirections direction = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
        Navigation.findNavController(view).navigate(direction);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}