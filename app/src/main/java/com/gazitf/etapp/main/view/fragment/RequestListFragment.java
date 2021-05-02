package com.gazitf.etapp.main.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.databinding.FragmentRequestListBinding;
import com.gazitf.etapp.main.adapter.RequestListRecyclerViewAdapter;
import com.gazitf.etapp.repository.FirestoreDbConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class RequestListFragment extends Fragment implements RequestListRecyclerViewAdapter.RequestPostClickListener {

    private FragmentRequestListBinding binding;
    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        fetchRequests();

        binding = FragmentRequestListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.recyclerViewRequestListActivities;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    private void fetchRequests() {
        firestore
                .collection(FirestoreDbConstants.RequestConstants.COLLECTION)
                .whereEqualTo(FirestoreDbConstants.RequestConstants.ACTIVITY_OWNER_ID, currentUser.getUid())
                .get()
                .addOnSuccessListener(requestQuerySnapshot -> {
                    Log.i("TAG", "fetchRequests: " + requestQuerySnapshot.size());
                    recyclerView.setAdapter(new RequestListRecyclerViewAdapter(requestQuerySnapshot.getDocuments(), this));
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void acceptRequest(String requestOwnerId) {

    }

    @Override
    public void rejectRequest(String requestOwnerId) {

    }
}