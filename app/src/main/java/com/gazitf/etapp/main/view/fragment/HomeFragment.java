package com.gazitf.etapp.main.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.create.CreateActivity;
import com.gazitf.etapp.databinding.FragmentHomeBinding;
import com.gazitf.etapp.details.ActivityDetailsActivity;
import com.gazitf.etapp.main.adapter.ActivityListRecyclerViewAdapter;
import com.gazitf.etapp.main.adapter.CategoryListRecyclerViewAdapter;
import com.gazitf.etapp.main.modelview.HomeViewModel;
import com.gazitf.etapp.repository.FirestoreDbConstants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment implements ActivityListRecyclerViewAdapter.PostClickListener {

    private HomeViewModel viewModel;

    private FragmentHomeBinding binding;
    private RecyclerView recyclerViewCategories, recyclerViewActivities;
    private FloatingActionButton buttonCreateActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        initListeners();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        viewModel.getCategoryList().observe(getViewLifecycleOwner(), categoryList -> {
            CategoryListRecyclerViewAdapter adapter = new CategoryListRecyclerViewAdapter(categoryList);
            recyclerViewCategories.setAdapter(adapter);
        });
        viewModel.getActivityList().observe(getViewLifecycleOwner(), activityList -> {
            ActivityListRecyclerViewAdapter adapter = new ActivityListRecyclerViewAdapter(activityList, this);
            recyclerViewActivities.setAdapter(adapter);
        });
    }

    private void initViews() {
        recyclerViewCategories = binding.recyclerViewCategories;
        setupRecyclerViewCategories();
        recyclerViewActivities = binding.recyclerViewActivities;
        setupRecyclerViewActivities();
        buttonCreateActivity = binding.buttonCreateActivity;
    }

    private void initListeners() {
        buttonCreateActivity.setOnClickListener(buttonView -> startActivity(new Intent(requireActivity(), CreateActivity.class)));
    }

    private void setupRecyclerViewCategories() {
        recyclerViewCategories.setHasFixedSize(true);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupRecyclerViewActivities() {
        recyclerViewActivities.setHasFixedSize(true);
        recyclerViewActivities.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    @Override
    public void navigateToPostDetails(String documentId) {
        Intent intent = new Intent(requireActivity(), ActivityDetailsActivity.class);
        intent.putExtra(FirestoreDbConstants.ActivitiesConstans.DOCUMENT_ID, documentId);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}