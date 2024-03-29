package com.gazitf.etapp.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.databinding.RecyclerViewItemActivityBinding;
import com.gazitf.etapp.model.ActivityModel;
import com.gazitf.etapp.model.CategoryModel;
import com.gazitf.etapp.repository.DbConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * @created 22/03/2021 - 5:54 PM
 * @project EtApp
 * @author fnkaya
 */
public class ActivityListRecyclerViewAdapter extends RecyclerView.Adapter<ActivityListRecyclerViewAdapter.ActivitiesViewHolder> {

    private List<ActivityModel> activityList;
    private RequestActivityPostClickListener requestActivityPostClickListener;

    public ActivityListRecyclerViewAdapter(List<ActivityModel> activityList, RequestActivityPostClickListener requestActivityPostClickListener) {
        this.activityList = activityList;
        this.requestActivityPostClickListener = requestActivityPostClickListener;
    }

    @NonNull
    @Override
    public ActivitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerViewItemActivityBinding binding = RecyclerViewItemActivityBinding.inflate(inflater, parent, false);

        return new ActivitiesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivitiesViewHolder holder, int position) {
        ActivityModel activityModel = activityList.get(position);
        holder.textViewName.setText(activityModel.getName());
        holder.textViewDescription.setText(activityModel.getDescription());
        Date date = activityModel.getStartDate().toDate();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd - HH:mm");
        holder.textViewStartDate.setText(localDateTime.format(formatter));

        activityModel.getCategoryRef()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CategoryModel categoryModel = task.getResult().toObject(CategoryModel.class);
                        if (categoryModel != null)
                            Picasso.get()
                                    .load(categoryModel.getImageUrl())
                                    .into(holder.imageViewActivityImage);
                    }
                });

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection(DbConstants.Favorites.COLLECTION)
                .document(currentUserId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        List<String> favoriteList = (List<String>) data.get(DbConstants.Favorites.FAVORITE_LIST);
                        holder.textViewFavorite.setText(String.valueOf(favoriteList.size()));
                    }
                });

        holder.imageViewActivityImage.setOnClickListener(view -> {
            requestActivityPostClickListener.navigateToPostDetails(activityModel.getId());
        });

        holder.textViewSharePost.setOnClickListener(view -> {
            requestActivityPostClickListener.sharePost(activityModel.getId());
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public class ActivitiesViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewActivityImage;
        private TextView textViewName, textViewDescription, textViewStartDate, textViewFavorite, textViewSharePost;

        public ActivitiesViewHolder(@NonNull RecyclerViewItemActivityBinding binding) {
            super(binding.getRoot());

            imageViewActivityImage = binding.imageViewActivityImage;
            textViewName = binding.textViewActivityName;
            textViewDescription = binding.textViewActivityDescription;
            textViewStartDate = binding.textViewActivityStartDate;
            textViewFavorite = binding.textViewActivityFavorite;
            textViewSharePost = binding.textViewActivityShare;
        }
    }

    public interface RequestActivityPostClickListener {
        void navigateToPostDetails(String documentId);
        void sharePost(String documentId);
    }
}
