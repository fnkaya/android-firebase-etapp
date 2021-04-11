package com.gazitf.etapp.details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.ActivityDetailsBinding;
import com.gazitf.etapp.databinding.BottonSheetDialogAttendRequestBinding;
import com.gazitf.etapp.main.model.ActivityModel;
import com.gazitf.etapp.main.model.CategoryModel;
import com.gazitf.etapp.main.repository.FirestoreActivityRepository;
import com.gazitf.etapp.main.repository.FirestoreDbConstants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class ActivityDetailsActivity extends AppCompatActivity implements FirestoreActivityRepository.OnActivityTaskComplete {

    private ActivityDetailsBinding binding;
    private String documentRef;

    private FirestoreActivityRepository repository;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        documentRef = getIntent().getStringExtra(FirestoreDbConstants.ActivitiesConstans.DOCUMENT_ID);
        repository = new FirestoreActivityRepository(this);
        fetchActivityPostDetails();

        initListeners();
    }

    private void fetchActivityPostDetails() {
        repository.getActivity(documentRef);
    }

    private void initListeners() {
        BottonSheetDialogAttendRequestBinding bottomSheetBinding = BottonSheetDialogAttendRequestBinding.inflate(getLayoutInflater());
        BottomSheetDialog bottomSheetDialog =  new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(bottomSheetBinding.bottomSheetContainer);
        bottomSheetDialog.setCancelable(false);

        binding.buttonShowRequestDialog.setOnClickListener(showButton -> {
            bottomSheetBinding.textInputLayoutBottomSheetDialog.setError(null);

            bottomSheetBinding.buttonSendAttendRequest.setOnClickListener(sendButton -> {
                Editable text = bottomSheetBinding.textInputAttendRequestMessage.getText();
                if (text != null &&text.length() != 0) {
                    Toast.makeText(this, text.toString(), Toast.LENGTH_LONG).show();
                    bottomSheetDialog.dismiss();
                }
                else
                    bottomSheetBinding.textInputLayoutBottomSheetDialog.setError("Lütfen bir mesaj giriniz!");
            });

            bottomSheetBinding.buttonCancelAttendRequest.setOnClickListener(cancelButton -> {
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();
        });
    }

    @Override
    public void onFetchActivitiesSucceed(List<ActivityModel> activityModelList) {

    }

    @Override
    public void onFetchActivityDetailsSuccedd(ActivityModel activityModel) {
        binding.textViewActivityNameDetails.setText(activityModel.getName());
        binding.textViewActivityDescriptionDetails.setText(activityModel.getDescription());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Date startDate = activityModel.getStartDate().toDate();
        LocalDateTime startDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        binding.textViewActivityStartDateDetails.setText(startDateTime.format(formatter));
        Date endDate = activityModel.getEndDate().toDate();
        LocalDateTime endDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        binding.textViewActivityEndDateDetails.setText(endDateTime.format(formatter));
        binding.textViewActivityLocationDetails.setText(activityModel.getLocation().toString());
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
        binding.textViewDetailsOwnerName.setText("Furkan Kaya");
    }

    @Override
    public void onFetchFailed(Exception e) {

    }
}