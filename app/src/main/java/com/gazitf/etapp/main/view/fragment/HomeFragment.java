package com.gazitf.etapp.main.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gazitf.etapp.databinding.FragmentHomeBinding;
import com.gazitf.etapp.main.adapter.ActivityListRecyclerViewAdapter;
import com.gazitf.etapp.main.adapter.CategoryListRecyclerViewAdapter;
import com.gazitf.etapp.main.modelview.HomeViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    private FragmentHomeBinding binding;
    private RecyclerView recyclerViewCategories, recyclerViewActivities;

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
            ActivityListRecyclerViewAdapter adapter = new ActivityListRecyclerViewAdapter(activityList);
            recyclerViewActivities.setAdapter(adapter);
        });
    }

    private void initViews() {
        recyclerViewCategories = binding.recyclerViewCategories;
        setupRecyclerViewCategories();
        recyclerViewActivities = binding.recyclerViewActivities;
        setupRecyclerViewActivities();
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}