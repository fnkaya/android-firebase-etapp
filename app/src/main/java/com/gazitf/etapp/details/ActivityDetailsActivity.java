package com.gazitf.etapp.details;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
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
    private String documentId;

    private FirestoreActivityRepository repository;
    private FirebaseAuth auth;
    private GoogleMap map;
    private LatLng latLng;

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        documentId = getIntent().getStringExtra(FirestoreDbConstants.ActivitiesConstans.DOCUMENT_ID);
        repository = new FirestoreActivityRepository(this);

        fetchActivityPostDetails();
        initViews();
        initListeners();
    }

    private void fetchActivityPostDetails() {
        repository.getActivity(documentId);
    }

    private void initViews() {
        toolbar = binding.toolbarActivityDetails;
    }

    private void initListeners() {
        toolbar.setNavigationOnClickListener(view -> this.finish());

        binding.buttonShowRequestDialog.setOnClickListener(showButton -> {
            initBottomSheetDialog();
        });

        binding.buttonAddFavoriteList.setOnClickListener(favoriteButton -> {
            addFavoriteList();
        });

        addListenerToFavoriteList();
    }

    private void addListenerToFavoriteList() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentRef = FirebaseFirestore.getInstance()
                .collection(FirestoreDbConstants.FavoritesConstans.COLLECTION)
                .document(currentUserId);

        documentRef.addSnapshotListener((documentSnapshot, error) -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> data = documentSnapshot.getData();
                List<String> favoriteList = (List<String>) data.getOrDefault(FirestoreDbConstants.FavoritesConstans.FAVORITE_LIST, new ArrayList<String>());
                if (favoriteList.contains(documentId)) {
                    binding.buttonAddFavoriteList.setText("KALDIR");
                    binding.buttonAddFavoriteList.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                else {
                    binding.buttonAddFavoriteList.setText("EKLE");
                    binding.buttonAddFavoriteList.setTextColor(getResources().getColor(R.color.colorRed));
                }
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
                Toast.makeText(this, text.toString(), Toast.LENGTH_LONG).show();
                bottomSheetDialog.dismiss();
            } else
                bottomSheetBinding.textInputLayoutBottomSheetDialog.setError("Lütfen bir mesaj giriniz!");
        });

        bottomSheetBinding.buttonCancelAttendRequest.setOnClickListener(cancelButton -> {
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void addFavoriteList() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentRef = FirebaseFirestore.getInstance()
                .collection(FirestoreDbConstants.FavoritesConstans.COLLECTION)
                .document(currentUserId);

        documentRef
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        List<String> favoriteList = (List<String>) data.getOrDefault(FirestoreDbConstants.FavoritesConstans.FAVORITE_LIST, new ArrayList<String>());
                        if (favoriteList.contains(documentId)) {
                            favoriteList.remove(documentId);
                            Toast.makeText(ActivityDetailsActivity.this, "Etkinlik favoriler listenizden silindi", Toast.LENGTH_LONG).show();
                        }
                        else{
                            favoriteList.add(documentId);
                            Toast.makeText(ActivityDetailsActivity.this, "Etkinlik favoriler listenize eklendi", Toast.LENGTH_LONG).show();
                        }
                        documentRef.set(data);
                    }
                });
    }

    @Override
    public void onActivityFetchSucceed(ActivityModel activityModel) {
        toolbar.setTitle(activityModel.getName());
        binding.textViewActivityDescriptionDetails.setText(activityModel.getDescription());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd - HH:mm");
        Date startDate = activityModel.getStartDate().toDate();
        LocalDateTime startDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        binding.textViewActivityStartDateDetails.setText(startDateTime.format(formatter));
        Date endDate = activityModel.getEndDate().toDate();
        LocalDateTime endDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        binding.textViewActivityEndDateDetails.setText(endDateTime.format(formatter));
        handleActivityOwnerDetails(activityModel);
        handleCategoryDetails(activityModel);

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