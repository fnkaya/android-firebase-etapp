package com.gazitf.etapp.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.RecyclerViewItemCategoryBinding;
import com.gazitf.etapp.main.model.ActivityShowModel;
import com.gazitf.etapp.main.model.Category;

import java.util.ArrayList;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityHolder> {

    // etkinlik listelede oluşturulan liste buraya bağlanacak
    private ArrayList <ActivityShowModel> activityList;

    public ActivityAdapter (ArrayList<ActivityShowModel> activityList) {
        this.activityList = activityList;
    }
    @NonNull
    @Override
    public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_activity,parent,false);

        return new ActivityHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
        // recycler.xml içindeki kısımlara ne yazılacağını bağlayacağız.
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityHolder extends RecyclerView.ViewHolder {
        public ActivityHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
