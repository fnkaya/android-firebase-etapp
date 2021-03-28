package com.gazitf.etapp.profile;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gazitf.etapp.utils.AuthInputValidator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.security.AuthProvider;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * @created 27/03/2021 - 11:37 AM
 * @project EtApp
 * @author fnkaya
 */
public class ProfileViewModel extends AndroidViewModel {

    private final FirebaseUser user;
    private final MutableLiveData<Boolean> editMode = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> phoneEditMode = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> pending = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> emailVerified = new MutableLiveData<>();

    public ProfileViewModel(Application application) {
        super(application);
        user = FirebaseAuth.getInstance().getCurrentUser();
        emailVerified.setValue(user.isEmailVerified());
    }

    public LiveData<Boolean> getEditMode() {
        return editMode;
    }

    public LiveData<Boolean> getPhoneEditMode() {
        return phoneEditMode;
    }

    public LiveData<Boolean> getPending() {
        return pending;
    }

    public LiveData<Boolean> getEmailVerified() {
        return emailVerified;
    }

    public FirebaseUser getUser() {
        return user;
    }

    @BindingAdapter("profileImage")
    public static void loadImage(ImageView view, Uri imageUrl) {
        if (imageUrl != null)
            Picasso
                    .get()
                    .load(imageUrl)
                    .into(view);
    }

    public void toggleEditMode() {
        editMode.setValue(!editMode.getValue());
    }

    public void togglePhoneEditMode() { phoneEditMode.setValue(!phoneEditMode.getValue()); }

    public void updateUserDisplayNameAndEmail(String displayName, String email) {
        pending.postValue(true);

        UserProfileChangeRequest userDisplayNameChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        updateUserDisplayName(userDisplayNameChangeRequest, email);
    }

    private void updateUserDisplayName(UserProfileChangeRequest profileChangeRequest, String email) {
        user
                .updateProfile(profileChangeRequest)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        updateUserEmail(email);
                    else
                        Toast.makeText(getApplication().getApplicationContext(), "Güncelleme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                });
    }

    private void updateUserEmail(String email) {
        user.verifyBeforeUpdateEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(getApplication().getApplicationContext(), "Lütfen yeni mail adresinize gönderilen doğrulama bağlantısına tıklayınız.", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplication().getApplicationContext(), "Güncelleme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                    pending.postValue(false);
                    editMode.postValue(false);
                });
    }

    public void updateProfilePhoto(Bitmap bitmap) {
        pending.postValue(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("profilePhotos").child(userId + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        getDownloadUrl(reference);
                    else {
                        pending.postValue(false);
                        Toast.makeText(getApplication().getApplicationContext(), "Profil fotoğrafı güncellenirken bir hata oluştu. Lütfen tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        setUserPhotoUrl(task.getResult());
                    else {
                        pending.postValue(false);
                        Toast.makeText(getApplication().getApplicationContext(), "Profil fotoğrafı güncellenirken bir hata oluştu. Lütfen tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setUserPhotoUrl(Uri uri) {
        UserProfileChangeRequest userProfilePhotoChangeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(userProfilePhotoChangeRequest)
                .addOnCompleteListener(task -> {
                            pending.postValue(false);

                            if (task.isSuccessful())
                                Toast.makeText(getApplication().getApplicationContext(), "Profil fotoğrafı güncellendi.", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getApplication().getApplicationContext(), "Profil fotoğrafı güncellenirken bir hata oluştu. Lütfen tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                        }
                );
    }

    public void sendEmailVerification() {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(getApplication().getApplicationContext(), "Doğrulama bağlantısı gönderildi", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplication().getApplicationContext(), "Doğrulama bağlantısı gönderilirken bir sorun oluştu", Toast.LENGTH_LONG).show();
                });
    }

    public void updatePassword(String currentPassword, String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        user.reauthenticate(credential)
                .addOnSuccessListener(task -> {
                        if (!AuthInputValidator.checkPasswordsAreDifferent(currentPassword, newPassword)) {
                            Toast.makeText(getApplication().getApplicationContext(), "Yeni şifreniz geçerli şifreniz ile aynı olamaz", Toast.LENGTH_LONG).show();
                            return;
                        }

                        user.updatePassword(newPassword)
                                .addOnSuccessListener(voidTask ->
                                        Toast.makeText(getApplication().getApplicationContext(), "Şifreniz başarıyla güncellendi", Toast.LENGTH_LONG).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(getApplication().getApplicationContext(), "Şifre güncellenirken bir hata oluştu", Toast.LENGTH_LONG).show());
                        })
                .addOnFailureListener(e ->
                        Toast.makeText(getApplication().getApplicationContext(), "Geçerli parolanızı yanlış girdiniz", Toast.LENGTH_LONG).show());
    }

    public void updatePhoneNumber(String phoneNumber) {
        //TODO Telefon numarası güncellemesi yapılacak
    }

    public void deleteAccount(String email, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnSuccessListener(task ->
                    user.delete()
                        .addOnFailureListener(e -> Toast.makeText(getApplication().getApplicationContext(), "Hesap silinirken bir hata oluştu", Toast.LENGTH_LONG).show()))
                .addOnFailureListener(e ->
                        Toast.makeText(getApplication().getApplicationContext(), "Hatalı e-mail veya şifre", Toast.LENGTH_LONG).show());
    }
}
