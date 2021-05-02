package com.gazitf.etapp.posts;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.databinding.RecyclerViewItemPostListBinding;
import com.gazitf.etapp.model.ActivityModel;
import com.gazitf.etapp.repository.FirestoreDbConstants;
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
public class PostListRecyclerViewAdapter extends RecyclerView.Adapter<PostListRecyclerViewAdapter.PostsViewHolder> {

    private final List<ActivityModel> activityModelList;
    private final PostClickListener postClickListener;

    public PostListRecyclerViewAdapter(List<ActivityModel> activityModelList, PostClickListener postClickListener) {
        this.activityModelList = activityModelList;
        this.postClickListener = postClickListener;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerViewItemPostListBinding binding = RecyclerViewItemPostListBinding.inflate(inflater, parent, false);

        return new PostsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        Log.i("qweasdzxc", "getItemCount: calis" );
        ActivityModel activityModel = activityModelList.get(position);
        holder.textViewActivityName.setText(activityModel.getName());
        Date startDate = activityModel.getStartDate().toDate();
        LocalDateTime ldtStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Date endDate = activityModel.getEndDate().toDate();
        LocalDateTime ldtEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd - HH:mm");
        holder.textViewActivityStartDate.setText(ldtStartDate.format(formatter));
        holder.textViewActivityEndDate.setText(ldtEndDate.format(formatter));
        activityModel.getCategoryRef()
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String photoUrl = (String) documentSnapshot.get(FirestoreDbConstants.CategoryConstants.IMAGE_URL);
                        Log.i("qweqweqwe", "onBindViewHolder: " + photoUrl);
                        Picasso.get()
                                .load(photoUrl)
                                .into(holder.imageViewActivityımage);
                    }
                });

        holder.buttonNavigatePostDetails.setOnClickListener(btn -> {
            postClickListener.navigateToPostDetails(activityModel.getId());
        });
    }

    @Override
    public int getItemCount() {
        return activityModelList.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewActivityımage;
        private TextView textViewActivityName, textViewActivityStartDate, textViewActivityEndDate;
        private Button buttonNavigatePostDetails;

        public PostsViewHolder(@NonNull RecyclerViewItemPostListBinding binding) {
            super(binding.getRoot());

            imageViewActivityımage = binding.imageViewPostActivityImage;
            textViewActivityName = binding.textViewPostActivityName;
            textViewActivityStartDate = binding.textViewPostActivityStartDate;
            textViewActivityEndDate = binding.textViewPostActivityEndDate;
            buttonNavigatePostDetails = binding.buttonEditPost;
        }
    }

    public interface PostClickListener {
        void navigateToPostDetails(String documentId);
    }
}
