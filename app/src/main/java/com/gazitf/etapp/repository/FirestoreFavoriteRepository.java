package com.gazitf.etapp.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static com.gazitf.etapp.repository.DbConstants.Favorites;

/*
 * @created 22/03/2021 - 6:18 PM
 * @project EtApp
 * @author fnkaya
 */
public class FirestoreFavoriteRepository {

    private OnActivityTaskCompleteCallback onActivityTaskCompleteCallback;

    private final CollectionReference favoritiesRef;

    public FirestoreFavoriteRepository(OnActivityTaskCompleteCallback onActivityTaskCompleteCallback) {
        this.onActivityTaskCompleteCallback = onActivityTaskCompleteCallback;
        favoritiesRef = FirebaseFirestore.getInstance().collection(Favorites.COLLECTION);
    }

    public void getActivities() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritiesRef
                .document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot result = task.getResult();
                        if (result.exists())
                            onActivityTaskCompleteCallback.onActivityListFetchSucceed((List<String>) result.getData().get(Favorites.FAVORITE_LIST));
                    } else
                        onActivityTaskCompleteCallback.onActivityFetchFailed(task.getException());
                });
    }

    public interface OnActivityTaskCompleteCallback {
        void onActivityListFetchSucceed(List<String> activityIdList);
        void onActivityFetchFailed(Exception e);
    }
}
