package com.gazitf.etapp.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.databinding.RecyclerViewItemActivityBinding;
import com.gazitf.etapp.main.model.ActivityModel;
import com.gazitf.etapp.main.model.CategoryModel;
import com.google.firebase.Timestamp;

import java.util.List;

/*
 * @created 22/03/2021 - 5:54 PM
 * @project EtApp
 * @author fnkaya
 */
public class ActivityListRecyclerViewAdapter extends RecyclerView.Adapter<ActivityListRecyclerViewAdapter.ActivitiesViewHolder> {

    private List<ActivityModel> activityList;

    public ActivityListRecyclerViewAdapter(List<ActivityModel> activityList) {
        this.activityList = activityList;
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
        holder.textViewStartDate.setText(activityModel.getStartDate().toString());
        holder.textViewEndDate.setText(activityModel.getEndDate().toString());
        holder.textViewLocation.setText(activityModel.getLocation().toString());
        holder.textViewCreatedDate.setText(activityModel.getCreatedDate().toDate().toString());
        activityModel.getCategoryRef()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CategoryModel categoryModel = task.getResult().toObject(CategoryModel.class);
                        holder.textViewCategory.setText(categoryModel.getName());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivitiesViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName, textViewDescription, textViewStartDate, textViewEndDate, textViewLocation, textViewCategory, textViewCreatedDate;

        public ActivitiesViewHolder(@NonNull RecyclerViewItemActivityBinding binding) {
            super(binding.getRoot());

            textViewName = binding.textViewActivityName;
            textViewDescription = binding.textViewActivityDescription;
            textViewStartDate = binding.textViewActivityStartDate;
            textViewEndDate = binding.textViewActivityEndDate;
            textViewLocation = binding.textViewActivityLocation;
            textViewCategory = binding.textViewActivityCategory;
            textViewCreatedDate = binding.textViewActivityCreatedDate;
        }
    }
}
