package com.gazitf.etapp.main.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gazitf.etapp.databinding.FragmentWatchListBinding;
import com.gazitf.etapp.details.ActivityDetailsActivity;
import com.gazitf.etapp.main.adapter.FavoriteListRecyclerViewAdapter;
import com.gazitf.etapp.repository.FirestoreDbConstants;
import com.gazitf.etapp.repository.FirestoreFavoriteRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class WatchListFragment extends Fragment implements FavoriteListRecyclerViewAdapter.FavoritePostClickListener, FirestoreFavoriteRepository.OnActivityTaskCompleteCallback {

    private FragmentWatchListBinding binding;
    private FirestoreFavoriteRepository favoriteRepository;
    private FavoriteListRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        favoriteRepository = new FirestoreFavoriteRepository(this);
        favoriteRepository.getActivities();

        binding = FragmentWatchListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerViewFavorites.setHasFixedSize(true);
        binding.recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }


    @Override
    public void navigateToPostDetails(String documentId) {
        Intent intent = new Intent(requireActivity(), ActivityDetailsActivity.class);
        intent.putExtra(FirestoreDbConstants.ActivitiesConstants.DOCUMENT_ID, documentId);
        startActivity(intent);
    }

    @Override
    public void removeActivityFromFavoriteList(String documentId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentRef = FirebaseFirestore.getInstance()
                .collection(FirestoreDbConstants.FavoritesConstants.COLLECTION)
                .document(currentUserId);

        documentRef
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();
                            List<String> favoriteList = (List<String>) data.get(FirestoreDbConstants.FavoritesConstants.FAVORITE_LIST);

                            if (favoriteList.contains(documentId)) {
                                favoriteList.remove(documentId);
                                documentRef.set(data);
                                onActivityListFetchSucceed(favoriteList);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    @Override
    public void onActivityListFetchSucceed(List<String> activityIdList) {
        adapter = new FavoriteListRecyclerViewAdapter(activityIdList, this);
        binding.recyclerViewFavorites.setAdapter(adapter);
    }

    @Override
    public void onActivityFetchFailed(Exception e) {
            Log.i("TAG", "onActivityFetchFailed: ", e);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}