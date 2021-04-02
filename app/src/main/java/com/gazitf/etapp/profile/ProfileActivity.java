package com.gazitf.etapp.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.gazitf.etapp.R;
import com.gazitf.etapp.auth.activity.AuthActivity;
import com.gazitf.etapp.databinding.ActivityProfileBinding;
import com.gazitf.etapp.databinding.DialogChangePasswordBinding;
import com.gazitf.etapp.databinding.DialogDeleteAccountBinding;
import com.gazitf.etapp.utils.AuthInputValidator;
import com.gazitf.etapp.utils.LoadingDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 201;
    private final int REQUEST_CODE_GALLERY_INTENT = 202;
    private final int REQUEST_CODE_CAMERA_PERMISSON = 301;
    private final int REQUEST_CODE_CAMERA_INTENT = 302;

    private ActivityProfileBinding binding;
    private CircleImageView imageViewProfilePhoto;
    private EditText editTextDisplayName, editTextEmail;

    private ProfileViewModel viewModel;
    private LoadingDialog loadingDialog;

    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        initViews();
        initObservers();
        initListeners();
    }

    private void initViews() {
        imageViewProfilePhoto = binding.avatarIv;
        editTextDisplayName = binding.editTextName;
        editTextEmail = binding.editTextEmail;
    }

    private void initObservers() {
        viewModel.getPending().observe(this, pending -> {
            if (pending)
                loadingDialog.startDialog();
            else
                loadingDialog.dismissDialog();
        });
    }

    private void initListeners() {
        binding.buttonEditProfileImage.setOnClickListener(view -> showImageOptions());
        binding.buttonSave.setOnClickListener(view -> updateUserDisplayNameAndEmail());
        binding.buttonChangePassword.setOnClickListener(view -> showUpdatePasswordDialog());
        binding.buttonDeleteAccount.setOnClickListener(view -> showDeleteAccountDialog());
        binding.buttonEditPhoneNumberConfirm.setOnClickListener(view -> updatePhoneNumber());
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }

    private void showImageOptions() {
        String[] options = {"Kamera", "Galeri", "İptal"};
        new MaterialAlertDialogBuilder(this)
                .setTitle("Profil fotoğrafı")
                .setItems(options, (dialog, which) -> {
                    switch (options[which]) {
                        case "Galeri":
                            if (checkGalleryPermission())
                                openGallery();
                            else
                                requestGalleryPermission();
                            break;
                        case "Kamera":
                            if (checkCameraPermision())
                                openCamera();
                            else
                                requestCameraPermission();
                            break;
                        case "İptal":
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private boolean checkGalleryPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY_INTENT);
    }

    private boolean checkCameraPermision() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA_INTENT);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSON);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }

        if (requestCode == REQUEST_CODE_CAMERA_PERMISSON) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bitmap imageBitmap = null;

        if (requestCode == REQUEST_CODE_GALLERY_INTENT && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            if (imageUri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    try {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                        imageBitmap = ImageDecoder.decodeBitmap(source);
                        imageViewProfilePhoto.setImageBitmap(imageBitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "onActivityResult: ", e);
                    }
                }

                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageViewProfilePhoto.setImageBitmap(imageBitmap);
                } catch (IOException e) {
                    Log.i("TAG", "onActivityResult: ", e);
                }
            }
        }

        if (requestCode == REQUEST_CODE_CAMERA_INTENT && resultCode == RESULT_OK && data != null) {
            imageBitmap = (Bitmap) data.getExtras().get("data");
            imageViewProfilePhoto.setImageBitmap(imageBitmap);
        }

        if (imageBitmap != null)
            viewModel.updateProfilePhoto(imageBitmap);

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUserDisplayNameAndEmail() {
        if (validate()) {
            String displayName = editTextDisplayName.getText().toString();
            String email = editTextEmail.getText().toString();

            viewModel.updateUserDisplayNameAndEmail(displayName, email);
        }
    }

    private boolean validate() {
        boolean result = true;
        editTextDisplayName.setError(null);
        editTextEmail.setError(null);

        String displayName = editTextDisplayName.getText().toString();
        String email = editTextEmail.getText().toString();

        if (!AuthInputValidator.validateName(displayName)) {
            result = false;
            editTextDisplayName.setError(getString(R.string.invalid_name));
        }

        if (!AuthInputValidator.validateEmail(email)) {
            result = false;
            editTextEmail.setError(getString(R.string.invalid_email));
        }

        return result;
    }

    private void showUpdatePasswordDialog() {
        DialogChangePasswordBinding binding = DialogChangePasswordBinding.inflate(getLayoutInflater(), null, false);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Şifre Değiştir")
                .setIcon(R.drawable.icon_lock)
                .setView(binding.getRoot())
                .show();

        binding.buttonCancelChangePassword.setOnClickListener(view -> dialog.dismiss());
        binding.buttonUpdatePassword.setOnClickListener(view -> {
            EditText editTextCurrentPassword = binding.editTextCurrentPassword;
            TextInputLayout textInputLayoutNewPassword = binding.textInputLayoutNewPassword;
            TextInputLayout textInputLayoutNewPasswordRepeat = binding.textInputLayoutNewPasswordRepeat;
            String currentPassword = editTextCurrentPassword.getText().toString();
            String newPassword = textInputLayoutNewPassword.getEditText().getText().toString();
            String newPasswordRepeat = textInputLayoutNewPasswordRepeat.getEditText().getText().toString();

            textInputLayoutNewPassword.setError(null);
            textInputLayoutNewPasswordRepeat.setError(null);

            if (!newPassword.equals(newPasswordRepeat)) {
                textInputLayoutNewPasswordRepeat.setError("Şifreler uyuşmuyor");
                return;
            }

            if (!AuthInputValidator.validatePassword(newPassword)) {
                textInputLayoutNewPassword.setError(getString(R.string.invalid_password));
                return;
            }

            viewModel.updatePassword(currentPassword, newPassword);
        });
    }

    private void updatePhoneNumber() {
        TextInputLayout textInputLayoutProfilePhoneNumber = binding.textInputLayoutProfilePhoneNumber;
        String phoneNumber = textInputLayoutProfilePhoneNumber.getEditText().getText().toString();

        textInputLayoutProfilePhoneNumber.setError(null);

        if (!AuthInputValidator.validatePhoneNumber(phoneNumber)) {
            textInputLayoutProfilePhoneNumber.setError(getString(R.string.invalid_phone_number));
            return;
        }

        /*viewModel.updatePhoneNumber(getString(R.string.tr_90) + phoneNumber);*/
        viewModel.togglePhoneEditMode();
    }

    private void showDeleteAccountDialog() {
        DialogDeleteAccountBinding binding = DialogDeleteAccountBinding.inflate(getLayoutInflater(), null, false);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.icon_delete_forever)
                .setTitle("Hesabınızı silmek istiyor musunuz?")
                .setMessage("Hesabınızı kalıcı olarak silmek üzeresiniz.\nBu işlemi gerçekleştirmek istediğinize eminseniz e-mail adresinizi ve şifrenizi girerek işlemi tamamlayabilirsiniz.")
                .setView(binding.getRoot())
                .show();

        binding.buttonCancelDeleteAccount.setOnClickListener(view -> dialog.dismiss());
        binding.buttonConfirmDeleteAccount.setOnClickListener(view ->
                viewModel.deleteAccount(binding.editTextDeleteAccountEmail.getText().toString(),
                        binding.editTextDeleteAccountPassword.getText().toString()));
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, AuthActivity.class));
            this.finishAffinity();
        }
    }
}

