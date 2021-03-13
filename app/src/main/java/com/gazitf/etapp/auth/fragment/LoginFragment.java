package com.gazitf.etapp.auth.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.gazitf.etapp.main.view.activity.MainActivity;
import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.FragmentLoginBinding;
import com.gazitf.etapp.utils.AuthInputValidator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LottieAnimationView lottieAnimationView;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRedirectToRegister, textViewForgotPassword;

    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lottieAnimationView = binding.animationViewAuthLogo;
        inputLayoutEmail = binding.textInputLayoutLoginEmail;
        inputLayoutPassword = binding.textInputLayoutLoginPassword;
        editTextEmail = binding.textInputLoginEmail;
        editTextPassword = binding.textInputLoginPassword;
        buttonLogin = binding.buttonLogin;
        textViewRedirectToRegister = binding.textViewRedirectRegister;
        textViewForgotPassword = binding.textViewForgotPassword;

        auth = FirebaseAuth.getInstance();

        initListeners();
    }

    private void initListeners() {
        buttonLogin.setOnClickListener(view -> validate());

        textViewRedirectToRegister.setOnClickListener(this::navigateToRegister);

        textViewForgotPassword.setOnClickListener(this::navigateToForgotPassword);
    }

    private void validate() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        inputLayoutEmail.setError(null);
        inputLayoutPassword.setError(null);
        boolean isValid = true;

        if (!AuthInputValidator.validateEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.invalid_email));
            isValid = false;
        }
        if (!AuthInputValidator.validatePassword(password)) {
            inputLayoutPassword.setError(getString(R.string.invalid_password));
            isValid = false;
        }

        if (isValid)
            signIn(email, password);
    }

    // Kullanıcı girişi yap
    private void signIn(String email, String password) {
        setAnimation(R.raw.sign_in);

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser().isEmailVerified())
                        startMainActivity();
                    else
                        showSendEmailVerificationDialog();
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    setAnimation(R.raw.failed);
                });
    }


    private void showSendEmailVerificationDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("E-mail adresi doğrulama")
                .setMessage("E-mail adresiniz henüz doğrulanmamış.\nDoğrulama iletisini tekrar almak ister misiniz?")
                .setPositiveButton("Tekrar Gönder", (dialog, which) -> sendEmailVerification(auth.getCurrentUser()))
                .setNegativeButton("İptal", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(response ->
                        Toast.makeText(getActivity(), "E-mail doğrulama bağlantısı gönderildi.", Toast.LENGTH_LONG).show());
    }

    private void startMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finishAffinity();
    }

    private void setAnimation(int resource) {
        lottieAnimationView.setAnimation(resource);
        lottieAnimationView.playAnimation();
    }

    private void navigateToRegister(View view) {
        NavDirections direction = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();
        Navigation.findNavController(view).navigate(direction);
    }


    private void navigateToForgotPassword(View view) {
        NavDirections direction = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment();
        Navigation.findNavController(view).navigate(direction);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}