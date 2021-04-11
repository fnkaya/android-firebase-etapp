package com.gazitf.etapp.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.databinding.RecyclerViewItemActivityBinding;
import com.gazitf.etapp.generated.callback.OnClickListener;
import com.gazitf.etapp.main.model.ActivityModel;
import com.gazitf.etapp.main.model.CategoryModel;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
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
public class ActivityListRecyclerViewAdapter extends RecyclerView.Adapter<ActivityListRecyclerViewAdapter.ActivitiesViewHolder> {

    private List<ActivityModel> activityList;
    private PostClickListener postClickListener;

    public ActivityListRecyclerViewAdapter(List<ActivityModel> activityList, PostClickListener postClickListener) {
        this.activityList = activityList;
        this.postClickListener = postClickListener;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
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

        holder.imageViewActivityImage.setOnClickListener(view -> {
            postClickListener.navigateToPostDetails(activityModel.getId());
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public class ActivitiesViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewActivityImage;
        private TextView textViewName, textViewDescription, textViewStartDate, textViewEndDate, textViewLocation, textViewCategory, textViewCreatedDate;

        public ActivitiesViewHolder(@NonNull RecyclerViewItemActivityBinding binding) {
            super(binding.getRoot());

            imageViewActivityImage = binding.imageViewActivityImage;
            textViewName = binding.textViewActivityName;
            textViewDescription = binding.textViewActivityDescription;
            textViewStartDate = binding.textViewActivityStartDate;
        }
    }

    public interface PostClickListener{
        void navigateToPostDetails(String documentRef);
    }
}
