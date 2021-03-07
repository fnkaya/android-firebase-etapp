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
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LottieAnimationView lottieAnimationView;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRedirectToRegister;

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
        editTextEmail = binding.textInputLoginEmail;
        editTextPassword = binding.textInputLoginPassword;
        buttonLogin = binding.buttonLogin;
        textViewRedirectToRegister = binding.textViewRedirectRegister;

        auth = FirebaseAuth.getInstance();

        initListeners();
    }

    private void initListeners() {
        buttonLogin.setOnClickListener(view -> validate());

        textViewRedirectToRegister.setOnClickListener(this::navigateToRegister);
    }

    private void validate() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        boolean isValid = true;

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
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    setAnimation(R.raw.failed);
                });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}