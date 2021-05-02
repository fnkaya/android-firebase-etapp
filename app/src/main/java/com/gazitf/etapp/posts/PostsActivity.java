package com.gazitf.etapp.posts;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.ActivityPostsBinding;
import com.gazitf.etapp.model.ActivityModel;
import com.gazitf.etapp.repository.FirestoreActivityRepository;

import java.util.List;

public class PostsActivity extends AppCompatActivity implements FirestoreActivityRepository.OnActivityTaskCompleteCallback, PostListRecyclerViewAdapter.PostClickListener {

    ActivityPostsBinding binding;

    FirestoreActivityRepository activityRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_posts);

        activityRepository = new FirestoreActivityRepository(this);
        activityRepository.getUsersActivities();

        binding.toolbarPosts.setNavigationOnClickListener(btn -> {
            this.finish();
        });
    }

    @Override
    public void onActivityListFetchSucceed(List<ActivityModel> activityModelList) {
        RecyclerView recyclerViewPosts = binding.recyclerViewPosts;
        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPosts.setAdapter(new PostListRecyclerViewAdapter(activityModelList, this));
    }

    @Override
    public void onActivityFetchFailed(Exception e) {
        Log.i("TAG", "onActivityFetchFailed: ", e);
    }

    @Override
    public void navigateToPostDetails(String documentId) {

    }
}