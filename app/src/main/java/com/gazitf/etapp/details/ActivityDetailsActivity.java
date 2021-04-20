package com.gazitf.etapp.details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.Toast;

import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.ActivityDetailsBinding;
import com.gazitf.etapp.databinding.BottonSheetDialogAttendRequestBinding;
import com.gazitf.etapp.main.model.ActivityModel;
import com.gazitf.etapp.main.model.CategoryModel;
import com.gazitf.etapp.main.repository.FirestoreActivityRepository;
import com.gazitf.etapp.main.repository.FirestoreDbConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ActivityDetailsActivity extends AppCompatActivity implements FirestoreActivityRepository.OnActivityDetailsTaskCompleteCallback, OnMapReadyCallback {

    private ActivityDetailsBinding binding;
    private String documentRef;

    private FirestoreActivityRepository repository;
    private FirebaseAuth auth;
    private GoogleMap map;
    private LatLng latLng;

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        documentRef = getIntent().getStringExtra(FirestoreDbConstants.ActivitiesConstans.DOCUMENT_ID);
        repository = new FirestoreActivityRepository(this);

        fetchActivityPostDetails();
        initViews();
        initListeners();
    }

    private void fetchActivityPostDetails() {
        repository.getActivity(documentRef);
    }

    private void initViews() {
        toolbar = binding.toolbarActivityDetails;
    }

    private void initListeners() {


        binding.buttonShowRequestDialog.setOnClickListener(showButton -> {
            initBottomSheetDialog();
        });

        toolbar.setNavigationOnClickListener(view -> this.finish());
    }

    private void initBottomSheetDialog() {
        BottonSheetDialogAttendRequestBinding bottomSheetBinding = BottonSheetDialogAttendRequestBinding.inflate(getLayoutInflater());
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

    @Override
    public void onActivityFetchSucceed(ActivityModel activityModel) {
        toolbar.setTitle(activityModel.getName());
        binding.textViewActivityDescriptionDetails.setText(activityModel.getDescription());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd - HH:mm");
        Date startDate = activityModel.getStartDate().toDate();
        LocalDateTime startDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        binding.textViewActivityStartDateDetails.setText(startDateTime.format(formatter));
        Date endDate = activityModel.getEndDate().toDate();
        LocalDateTime endDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        binding.textViewActivityEndDateDetails.setText(endDateTime.format(formatter));
        handleCategoryDetails(activityModel);

        latLng = new LatLng(activityModel.getLocation().getLatitude(), activityModel.getLocation().getLongitude());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_activity_details);
        mapFragment.getMapAsync(this);
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