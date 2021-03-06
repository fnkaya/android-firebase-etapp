package com.gazitf.etapp.auth.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.gazitf.etapp.MainActivity;
import com.gazitf.etapp.auth.activity.PhoneVerificationActivity;
import com.gazitf.etapp.databinding.FragmentRegisterBinding;
import com.gazitf.etapp.utils.AuthInputValidator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterFragment extends Fragment {

    private static final int PHONE_VERIFICATION_REQUEST_CODE = 202;

    private FragmentRegisterBinding binding;
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

        editTextName = binding.textInputName;
        editTextEmail = binding.textInputRegisterEmail;
        editTextPhoneNumber = binding.textInputPhoneNumber;
        editTextPassword = binding.textInputRegisterPassword;
        buttonRegister = binding.buttonRegister;
        textViewRedirectToLogin = binding.textViewRedirectLogin;

        if (savedInstanceState != null) {
            name = savedInstanceState.getString("name");
            email = savedInstanceState.getString("email");
            phoneNumber = savedInstanceState.getString("phone");
            password = savedInstanceState.getString("password");

            editTextName.setText(name);
            editTextEmail.setText(email);
            editTextPhoneNumber.setText(phoneNumber);
            editTextPassword.setText(password);
        }

        auth = FirebaseAuth.getInstance();

        initListeners();
    }

    private void initListeners() {
        buttonRegister.setOnClickListener(view -> validate());

        textViewRedirectToLogin.setOnClickListener(this::navigateToLogin);
    }

    private void validate() {
        name = editTextName.getText().toString();
        email = editTextEmail.getText().toString();
        phoneNumber = editTextPhoneNumber.getText().toString();
        password = editTextPassword.getText().toString();
        boolean isValid = true;

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

        if (isValid)
            startVerificationActivity();
    }

    private void startVerificationActivity() {
        Intent verificationIntent = new Intent(getActivity(), PhoneVerificationActivity.class);
        verificationIntent.putExtra("phone_number", phoneNumber);
        startActivityForResult(verificationIntent, PHONE_VERIFICATION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHONE_VERIFICATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK)
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
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finishAffinity();
    }

    private void showErrorMessage(String errorText) {
        Toast.makeText(getActivity(), errorText, Toast.LENGTH_LONG).show();
    }

    private void navigateToLogin(View view) {
        NavDirections direction = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
        Navigation.findNavController(view).navigate(direction);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("name", name);
        outState.putString("email", email);
        outState.putString("phone", phoneNumber);
        outState.putString("password", password);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}