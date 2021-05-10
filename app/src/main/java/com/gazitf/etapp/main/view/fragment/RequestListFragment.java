package com.gazitf.etapp.main.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestListFragment extends Fragment implements RequestListRecyclerViewAdapter.RequestPostClickListener {

    private FragmentRequestListBinding binding;
    private RecyclerView recyclerView;
    private RequestListRecyclerViewAdapter adapter;

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
                .whereEqualTo(FirestoreDbConstants.RequestConstants.STATUS, FirestoreDbConstants.RequestConstants.PENDING)
                .get()
                .addOnSuccessListener(requestQuerySnapshot -> {
                    Log.i("TAG", "fetchRequests: " + requestQuerySnapshot.size());
                    adapter = new RequestListRecyclerViewAdapter(requireContext(), requestQuerySnapshot.getDocuments(), this);
                    recyclerView.setAdapter(adapter);
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void acceptRequest(String activityId, String requestOwnerId) {
        DocumentReference attendeeRef = firestore
                .collection(FirestoreDbConstants.AttendeeConstants.COLLECTION)
                .document(activityId);
        attendeeRef
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        List<String> attendeeList = (List<String>) data.getOrDefault(FirestoreDbConstants.AttendeeConstants.ATTENDEE_LIST, new ArrayList<String>());
                        if (!attendeeList.contains(requestOwnerId)) {
                            attendeeList.add(requestOwnerId);
                        }

                        attendeeRef.set(data);
                    }else {
                        Map<String, Object> data = new HashMap<>();
                        List<String> attendeeList = new ArrayList<>();
                        attendeeList.add(requestOwnerId);
                        data.put(FirestoreDbConstants.AttendeeConstants.ATTENDEE_LIST, attendeeList);
                        documentSnapshot.getReference().set(data);
                    }

                    firestore
                            .collection(FirestoreDbConstants.RequestConstants.COLLECTION)
                            .document(activityId + requestOwnerId)
                            .update(FirestoreDbConstants.RequestConstants.STATUS, FirestoreDbConstants.RequestConstants.ACCEPTED)
                            .addOnSuccessListener(aVoid -> Toast.makeText(requireActivity(), "Kullanıcı katılımcı listesine eklendi", Toast.LENGTH_LONG).show());

                    fetchRequests();
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void rejectRequest(String activityId, String requestOwnerId) {
        firestore
                .collection(FirestoreDbConstants.RequestConstants.COLLECTION)
                .document(activityId + requestOwnerId)
                .update(FirestoreDbConstants.RequestConstants.STATUS, FirestoreDbConstants.RequestConstants.REJECTED)
                .addOnSuccessListener(aVoid -> Toast.makeText(requireActivity(), "Kullanıcı katılım talebi reddedildi", Toast.LENGTH_LONG).show());

        fetchRequests();
        adapter.notifyDataSetChanged();
    }
}