package com.gazitf.etapp.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.RecyclerViewItemFavoriteListBinding;
import com.gazitf.etapp.model.ActivityModel;
import com.gazitf.etapp.model.CategoryModel;
import com.gazitf.etapp.repository.FirestoreDbConstants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/*
 * @created 22/03/2021 - 5:54 PM
 * @project EtApp
 * @author fnkaya
 */
public class FavoriteListRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteListRecyclerViewAdapter.FavoritiesViewHolder> {

    private List<String> activityIdList;
    private PostClickListener postClickListener;

    public FavoriteListRecyclerViewAdapter(List<String> activityIdList, PostClickListener postClickListener) {
        this.activityIdList = activityIdList;
        this.postClickListener = postClickListener;
    }

    @NonNull
    @Override
    public FavoritiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerViewItemFavoriteListBinding binding = RecyclerViewItemFavoriteListBinding.inflate(inflater, parent, false);

        return new FavoritiesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritiesViewHolder holder, int position) {
        String activityId = activityIdList.get(position);

        FirebaseFirestore.getInstance()
                .collection(FirestoreDbConstants.ActivitiesConstans.COLLECTION)
                .document(activityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ActivityModel activityModel = documentSnapshot.toObject(ActivityModel.class);

                        holder.textViewFavoriteActivityName.setText(activityModel.getName());
                        Date startDate = activityModel.getStartDate().toDate();
                        LocalDateTime ldtStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        Date endDate = activityModel.getEndDate().toDate();
                        LocalDateTime ldtEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd - HH:mm");
                        holder.textViewFavoriteActivityStartDate.setText(ldtStartDate.format(formatter));
                        holder.textViewFavoriteActivityEndDate.setText(ldtEndDate.format(formatter));
                        activityModel.getCategoryRef()
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        CategoryModel categoryModel = task.getResult().toObject(CategoryModel.class);
                                        if (categoryModel != null)
                                            Picasso.get()
                                                    .load(categoryModel.getImageUrl())
                                                    .placeholder(R.drawable.progress_animation)
                                                    .into(holder.imageViewFavoriteActivityImage);
                                    }
                                });

                        holder.imageViewFavoriteActivityImage.setOnClickListener(view -> {
                            postClickListener.navigateToPostDetails(activityModel.getId());
                        });

                        holder.buttonRemoveActivityFromFavoriteList.setOnClickListener(view -> {
                            postClickListener.removeActivityFromFavoriteList(activityModel.getId());
                        });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return activityIdList.size();
    }

    public class FavoritiesViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewFavoriteActivityImage;
        private TextView textViewFavoriteActivityName, textViewFavoriteActivityStartDate, textViewFavoriteActivityEndDate;
        private Button buttonRemoveActivityFromFavoriteList;

        public FavoritiesViewHolder(@NonNull RecyclerViewItemFavoriteListBinding binding) {
            super(binding.getRoot());

            imageViewFavoriteActivityImage = binding.imageViewFavoriteActivityImage;
            textViewFavoriteActivityName = binding.textViewFavoriteActivityName;
            textViewFavoriteActivityStartDate = binding.textViewFavoriteActivityStartDate;
            textViewFavoriteActivityEndDate = binding.textViewFavoriteActivityEndDate;
            buttonRemoveActivityFromFavoriteList = binding.buttonRemoveFromFavoriteList;
        }
    }

    public interface PostClickListener{
        void navigateToPostDetails(String documentId);
        void removeActivityFromFavoriteList(String documentId);
    }
}
