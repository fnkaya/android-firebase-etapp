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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.gazitf.etapp.R;
import com.gazitf.etapp.auth.activity.PhoneVerificationActivity;
import com.gazitf.etapp.databinding.FragmentRegisterBinding;
import com.gazitf.etapp.utils.AuthInputValidator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
        auth = FirebaseAuth.getInstance();
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        initListeners();
    }

    private void initViews() {
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
                    setNameToAccount(user);
                })
                .addOnFailureListener(error ->
                        showToastMessage(error.toString()));
    }

    // Kullanıcı ismini güncelle
    private void setNameToAccount(FirebaseUser user) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdate)
                .addOnFailureListener(error -> showToastMessage(error.getLocalizedMessage()));

        FirebaseUser currentUser = auth.getCurrentUser();
        Map<String, String> userMap = new HashMap<>();
        userMap.put("displayName", name);
        userMap.put("email", currentUser.getEmail());
        userMap.put("phoneNumber", currentUser.getPhoneNumber());
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(currentUser.getUid())
                .set(userMap)
                .addOnFailureListener(e -> Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_LONG).show());

        user.sendEmailVerification();
        navigateToMainActivity(getView());
    }

    private void navigateToLogin(View view) {
        NavDirections direction = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
        Navigation.findNavController(view).navigate(direction);
    }

    private void navigateToMainActivity(View view) {
        NavDirections direction = RegisterFragmentDirections.actionRegisterFragmentToMainActivity();
        Navigation.findNavController(view).navigate(direction);
        requireActivity().finishAffinity();
    }

    private void showToastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}