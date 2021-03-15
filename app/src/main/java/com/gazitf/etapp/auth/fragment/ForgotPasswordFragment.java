package com.gazitf.etapp.auth.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.FragmentForgotPasswordBinding;
import com.gazitf.etapp.utils.AuthInputValidator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;
    private Toolbar toolbar;
    private TextInputLayout inputLayoutEmailToResetPassword;
    private TextView textViewEmailToResetPassword;
    private Button buttonSendResetLink;

    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = binding.toolbarForgotPassword;
        inputLayoutEmailToResetPassword = binding.textInputLayoutEmailToResetPassword;
        textViewEmailToResetPassword = binding.textInputEmailToResetPassword;
        buttonSendResetLink = binding.buttonSendLinkToResetPassword;

        auth = FirebaseAuth.getInstance();

        initListeners();
    }

    private void initListeners() {
        buttonSendResetLink.setOnClickListener(view -> validate());

        toolbar.setNavigationOnClickListener(this::navigateToLogin);
    }

    private void validate() {
        String email = textViewEmailToResetPassword.getText().toString();
        inputLayoutEmailToResetPassword.setError(null);

        if (AuthInputValidator.validateEmail(email))
            sendResetPasswordLink(email);
        else
            inputLayoutEmailToResetPassword.setError(getString(R.string.invalid_email));
    }

    private void sendResetPasswordLink(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(response -> {
                    navigateToLogin(getView());
                    showToastMessage("Parola sıfırlama bağlantısı gönderildi.");
                })
                .addOnFailureListener(error ->
                        showToastMessage(error.getLocalizedMessage()));
    }

    private void showToastMessage(String localizedMessage) {
        Toast.makeText(getActivity(), localizedMessage, Toast.LENGTH_LONG).show();
    }

    private void navigateToLogin(View view) {
        NavDirections direction = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment();
        Navigation.findNavController(view).navigate(direction);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}