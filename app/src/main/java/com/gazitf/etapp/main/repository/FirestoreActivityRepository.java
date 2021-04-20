package com.gazitf.etapp.main.repository;

import com.gazitf.etapp.main.model.ActivityModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static com.gazitf.etapp.main.repository.FirestoreDbConstants.ActivitiesConstans;

/*
 * @created 22/03/2021 - 6:18 PM
 * @project EtApp
 * @author fnkaya
 */
public class FirestoreActivityRepository {

    private OnActivityTaskCompleteCallback onActivityTaskCompleteCallback;
    private OnActivityDetailsTaskCompleteCallback onActivityDetailsTaskCompleteCallback;

    private final CollectionReference activitiesRef;

    public FirestoreActivityRepository(OnActivityTaskCompleteCallback onActivityTaskCompleteCallback) {
        this.onActivityTaskCompleteCallback = onActivityTaskCompleteCallback;
        activitiesRef = FirebaseFirestore.getInstance().collection(ActivitiesConstans.COLLECTION);
    }

    public FirestoreActivityRepository(OnActivityDetailsTaskCompleteCallback onActivityDetailsTaskCompleteCallback) {
        this.onActivityDetailsTaskCompleteCallback = onActivityDetailsTaskCompleteCallback;
        activitiesRef = FirebaseFirestore.getInstance().collection(ActivitiesConstans.COLLECTION);
    }

    public void getActivities() {
        activitiesRef
                .orderBy(ActivitiesConstans.START_DATE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        onActivityTaskCompleteCallback.onActivityListFetchSucceed(task.getResult().toObjects(ActivityModel.class));
                    else
                        onActivityTaskCompleteCallback.onActivityFetchFailed(task.getException());
                });
    }

    public void getActivity(String documentId) {
        activitiesRef
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        onActivityDetailsTaskCompleteCallback.onActivityFetchSucceed(task.getResult().toObject(ActivityModel.class));
                    else
                        onActivityDetailsTaskCompleteCallback.onActivityFetchFailed(task.getException());
                });
    }


    public interface OnActivityTaskCompleteCallback {

        void onActivityListFetchSucceed(List<ActivityModel> activityModelList);

        void onActivityFetchFailed(Exception e);
    }

    public interface OnActivityDetailsTaskCompleteCallback {

        void onActivityFetchSucceed(ActivityModel activityModel);

        void onActivityFetchFailed(Exception e);
    }
}
