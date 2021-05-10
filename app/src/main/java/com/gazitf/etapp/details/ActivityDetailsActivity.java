package com.gazitf.etapp.details;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.ActivityDetailsBinding;
import com.gazitf.etapp.databinding.BottomSheetDialogAttendRequestBinding;
import com.gazitf.etapp.model.ActivityModel;
import com.gazitf.etapp.model.CategoryModel;
import com.gazitf.etapp.repository.FirestoreActivityRepository;
import com.gazitf.etapp.repository.FirestoreDbConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityDetailsActivity extends AppCompatActivity implements FirestoreActivityRepository.OnActivityDetailsTaskCompleteCallback, OnMapReadyCallback {

    private ActivityDetailsBinding binding;
    private MaterialToolbar toolbar;

    private FirestoreActivityRepository repository;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private GoogleMap map;
    private LatLng latLng;

    private String activityId;
    private String activityOwnerId;
    private int attendeeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        activityId = getIntent().getStringExtra(FirestoreDbConstants.ActivitiesConstants.DOCUMENT_ID);
        repository = new FirestoreActivityRepository(this);
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        fetchActivityPostDetails();
        initViews();
        initListeners();
    }

    private void fetchActivityPostDetails() {
        repository.getActivity(activityId);
    }

    private void initViews() {
        toolbar = binding.toolbarActivityDetails;
    }

    private void initListeners() {
        toolbar.setNavigationOnClickListener(view -> this.finish());

        binding.buttonShowRequestDialog.setOnClickListener(showButton -> {
            initBottomSheetDialog();
        });

        binding.buttonToggleFavorite.setOnClickListener(favoriteButton -> {
            toggleFavoriteList();
        });

        binding.buttonCancelAttendRequest.setOnClickListener(cancelButton -> {
            showRemoveAttendRequestDialog();
        });

        binding.buttonLeaveAttendeeList.setOnClickListener(leaveButton -> {
            showLeaveFromAttendListDialog();
        });

        addListenerToFavoriteList();
    }

    private void addListenerToFavoriteList() {
        firestore
                .collection(FirestoreDbConstants.FavoritesConstants.COLLECTION)
                .document(currentUser.getUid())
                .addSnapshotListener((documentSnapshot, error) -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> data = documentSnapshot.getData();
                List<String> favoriteList = (List<String>) data.getOrDefault(FirestoreDbConstants.FavoritesConstants.FAVORITE_LIST, new ArrayList<String>());
                if (favoriteList.contains(activityId)) {
                    binding.buttonToggleFavorite.setColorFilter(getResources().getColor(R.color.colorRed));
                } else {
                    binding.buttonToggleFavorite.setColorFilter(getResources().getColor(R.color.colorBlack));
                }
            }
            else {
                binding.buttonToggleFavorite.setColorFilter(getResources().getColor(R.color.colorBlack));
            }
        });
    }

    private void initBottomSheetDialog() {
        BottomSheetDialogAttendRequestBinding bottomSheetBinding = BottomSheetDialogAttendRequestBinding.inflate(getLayoutInflater());
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(bottomSheetBinding.bottomSheetContainer);
        bottomSheetDialog.setCancelable(false);

        bottomSheetBinding.textInputLayoutBottomSheetDialog.setError(null);

        bottomSheetBinding.buttonSendAttendRequest.setOnClickListener(sendButton -> {
            Editable text = bottomSheetBinding.textInputAttendRequestMessage.getText();
            if (text != null && text.length() != 0) {
                saveAttendRequest(text.toString().trim());
                bottomSheetDialog.dismiss();
            } else
                bottomSheetBinding.textInputLayoutBottomSheetDialog.setError("Lütfen bir mesaj giriniz!");
        });

        bottomSheetBinding.buttonDismissBottomSheetDialog.setOnClickListener(cancelButton -> {
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void saveAttendRequest(String requestMessage) {
        Map<String, Object> requestData = Map.of(FirestoreDbConstants.RequestConstants.REQUEST_MESSAGE, requestMessage,
                FirestoreDbConstants.RequestConstants.OWNER_ID, currentUser.getUid(),
                FirestoreDbConstants.RequestConstants.ACTIVITY_ID, activityId,
                FirestoreDbConstants.RequestConstants.ACTIVITY_OWNER_ID, activityOwnerId,
                FirestoreDbConstants.RequestConstants.OWNER_NAME, currentUser.getDisplayName(),
                FirestoreDbConstants.RequestConstants.REQUEST_DATE, Timestamp.now(),
                FirestoreDbConstants.RequestConstants.STATUS, FirestoreDbConstants.RequestConstants.PENDING);

        firestore
                .collection(FirestoreDbConstants.RequestConstants.COLLECTION)
                .document(activityId + currentUser.getUid())
                .set(requestData)
                .addOnSuccessListener(aVoid -> Toast.makeText(ActivityDetailsActivity.this, "Etkinliğe katılım talebiniz iletildi", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(ActivityDetailsActivity.this, "Etkinliğe katılım talebiniz iletilemedi.\nLütfen daha sonra tekrar deneyiniz", Toast.LENGTH_LONG).show());
    }

    private void showRemoveAttendRequestDialog() {
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.icon_cancel)
                .setTitle("Emin misiniz?")
                .setMessage("Katılım talebini iptal etmek istiyor musunuz?")
                .setCancelable(false)
                .setNegativeButton("VAZGEÇ", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("İPTAL ET", (dialog, which) -> removeAttendRequest())
                .show();
    }

    private void showLeaveFromAttendListDialog() {
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.icon_cancel)
                .setTitle("Emin misiniz?")
                .setMessage("Katılımcı listesinden istiyor musunuz?")
                .setCancelable(false)
                .setNegativeButton("VAZGEÇ", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("AYRIL", (dialog, which) -> leaveFromAttendList())
                .show();
    }

    private void removeAttendRequest() {
        firestore
                .collection(FirestoreDbConstants.RequestConstants.COLLECTION)
                .document(activityId + currentUser.getUid())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(ActivityDetailsActivity.this, "Etkinliğe katılım talebiniz iptal edildi", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(ActivityDetailsActivity.this, "Etkinliğe katılım talebiniz iptal edilemedi.\nLütfen daha sonra tekrar deneyiniz", Toast.LENGTH_LONG).show());
    }

    private void leaveFromAttendList() {
        firestore
                .collection(FirestoreDbConstants.AttendeeConstants.COLLECTION)
                .document(activityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        List<String> attendeeList = (List<String>) data.getOrDefault(FirestoreDbConstants.AttendeeConstants.ATTENDEE_LIST, new ArrayList<>());
                        if (attendeeList.contains(currentUser.getUid())) {
                            attendeeList.remove(currentUser.getUid());
                            documentSnapshot.getReference().set(data)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(ActivityDetailsActivity.this, "Katılımcı listesinden ayrıldınız", Toast.LENGTH_LONG).show());
                        }
                    }
                });
    }

    private void toggleFavoriteList() {
        DocumentReference documentRef = firestore
                .collection(FirestoreDbConstants.FavoritesConstants.COLLECTION)
                .document(currentUser.getUid());

        documentRef
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        List<String> favoriteList = (List<String>) data.getOrDefault(FirestoreDbConstants.FavoritesConstants.FAVORITE_LIST, new ArrayList<String>());
                        if (favoriteList.contains(activityId)) {
                            favoriteList.remove(activityId);
                            Toast.makeText(ActivityDetailsActivity.this, "Etkinlik favoriler listenizden kaldırıldı", Toast.LENGTH_LONG).show();
                        } else {
                            favoriteList.add(activityId);
                            Toast.makeText(ActivityDetailsActivity.this, "Etkinlik favoriler listenize eklendi", Toast.LENGTH_LONG).show();
                        }
                        documentRef.set(data);
                    }
                    else {
                        Map<String, Object> data = new HashMap<>();
                        List<String> favoriteList = new ArrayList<>();
                        favoriteList.add(activityId);
                        data.put(FirestoreDbConstants.FavoritesConstants.FAVORITE_LIST, favoriteList);

                        documentRef.set(data).addOnSuccessListener(aVoid -> Toast.makeText(ActivityDetailsActivity.this, "Etkinlik favoriler listenize eklendi", Toast.LENGTH_LONG).show());
                    }
                });
    }

    @Override
    public void onActivityFetchSucceed(ActivityModel activityModel) {
        activityOwnerId = activityModel.getOwnerId();

        toolbar.setTitle(activityModel.getName());
        binding.textViewActivityDescriptionDetails.setText(activityModel.getDescription());
        Integer quota = activityModel.getQuota();
        binding.textViewActivityQuota.setText(getResources().getString(R.string.activity_quota, quota));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd - HH:mm");
        Date startDate = activityModel.getStartDate().toDate();
        LocalDateTime startDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        binding.textViewActivityStartDateDetails.setText(startDateTime.format(formatter));
        Date endDate = activityModel.getEndDate().toDate();
        LocalDateTime endDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        binding.textViewActivityEndDateDetails.setText(endDateTime.format(formatter));
        handleActivityOwnerDetails(activityModel);
        handleCategoryDetails(activityModel);
        handleQuota(quota);

        latLng = new LatLng(activityModel.getLocation().getLatitude(), activityModel.getLocation().getLongitude());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_activity_details);
        mapFragment.getMapAsync(this);
    }

    private void handleActivityOwnerDetails(ActivityModel activityModel) {
        String ownerId = activityModel.getOwnerId();
        FirebaseFirestore.getInstance()
                .collection(FirestoreDbConstants.UsersConstants.COLLECTION)
                .document(ownerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        String displayName = documentSnapshot.getString("displayName");
                        String photoUrl = documentSnapshot.getString("photoUrl");
                        loadOwnerInformation(displayName, photoUrl);
                    } else
                        Log.i("TAG", "handleActivityOwnerDetails: " + task.getException().getMessage());
                })
                .addOnFailureListener(e -> Toast.makeText(ActivityDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadOwnerInformation(String displayName, String photoUrl) {
        binding.textViewActivityOwnerName.setText(displayName);
        Picasso.get()
                .load(photoUrl)
                .into(binding.imageViewActivityOwnerImage);
    }

    private void handleCategoryDetails(ActivityModel activityModel) {
        activityModel.getCategoryRef()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CategoryModel categoryModel = task.getResult().toObject(CategoryModel.class);
                        if (categoryModel != null)
                            Picasso.get()
                                    .load(categoryModel.getImageUrl())
                                    .into(binding.imageViewActivityImageDetails);
                    }
                });
    }

    private void handleQuota(Integer quota) {
        firestore
                .collection(FirestoreDbConstants.AttendeeConstants.COLLECTION)
                .document(activityId)
                .addSnapshotListener((attendeeSnapshot, error) -> {
                    if (attendeeSnapshot.exists()) {
                        //Katılımcı listesi mevcut
                        List<String> attendeeList = (List<String>) attendeeSnapshot.get(FirestoreDbConstants.AttendeeConstants.ATTENDEE_LIST);
                        attendeeNumber = attendeeList.size();
                        binding.textViewActivityAttendeeNumber.setText(getResources().getString(R.string.activity_attendee_number, attendeeNumber));

                        if (attendeeList.contains(currentUser.getUid())) {
                            //Kullanıcı katılımcı listesinde var
                            toggleButtonsVisibility(View.GONE, View.GONE, View.VISIBLE, View.GONE);
                        } else {
                            //Kullanıcı katılımcı listesinde yok
                            toggleButtonsVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE);
                        }
                    }else {
                        //Katılımcı listesi mevcut değil
                        binding.textViewActivityAttendeeNumber.setText(getResources().getString(R.string.activity_attendee_number, 0));
                        toggleButtonsVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE);
                    }
                });

        firestore
                .collection(FirestoreDbConstants.RequestConstants.COLLECTION)
                .document(activityId + currentUser.getUid())
                .addSnapshotListener((requestSnapshot, error) -> {
                    if (requestSnapshot.exists()) {
                        //Request mevcut
                        String requestStatus = (String) requestSnapshot.get(FirestoreDbConstants.RequestConstants.STATUS);
                        if (FirestoreDbConstants.RequestConstants.PENDING.equals(requestStatus))
                            toggleButtonsVisibility(View.GONE, View.VISIBLE, View.GONE, View.GONE);
                        else if (FirestoreDbConstants.RequestConstants.ACCEPTED.equals(requestStatus))
                            toggleButtonsVisibility(View.GONE, View.GONE, View.VISIBLE, View.GONE);
                        else if (FirestoreDbConstants.RequestConstants.REJECTED.equals(requestStatus)) {
                            binding.textViewActivityRequestStatus.setText("Etkinlik katılım talebiniz etkinlik sahibi tarafından reddedildi");
                            toggleButtonsVisibility(View.GONE, View.GONE, View.GONE, View.VISIBLE);
                        }


                    }else{
                        //Request mevcut değil
                        if (attendeeNumber < quota)
                            toggleButtonsVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE);
                        else {
                            toggleButtonsVisibility(View.GONE, View.GONE, View.GONE, View.VISIBLE);
                            binding.textViewActivityRequestStatus.setText("KATILIMCI SINIRINA ULAŞILDI");
                        }
                    }
                });
    }

    private void toggleButtonsVisibility(int showButton, int cancelRequestButton, int leaveAttendButton, int quotaFilledText) {
        binding.buttonShowRequestDialog.setVisibility(showButton);
        binding.buttonCancelAttendRequest.setVisibility(cancelRequestButton);
        binding.buttonLeaveAttendeeList.setVisibility(leaveAttendButton);
        binding.textViewActivityRequestStatus.setVisibility(quotaFilledText);
    }

    @Override
    public void onActivityFetchFailed(Exception e) {
        Log.i("TAG", "onActivityFetchFailed: ", e);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions().position(latLng).title(toolbar.getTitle().toString()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }
}