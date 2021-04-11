package com.gazitf.etapp.main.repository;

import com.gazitf.etapp.main.model.ActivityModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static com.gazitf.etapp.main.repository.FirestoreDbConstants.*;

/*
 * @created 22/03/2021 - 6:18 PM
 * @project EtApp
 * @author fnkaya
 */
public class FirestoreActivityRepository {

    private OnActivityTaskComplete onActivityTaskComplete;

    private FirebaseFirestore firestore;
    private CollectionReference activitiesRef;

    public FirestoreActivityRepository(OnActivityTaskComplete onActivityTaskComplete) {
        this.onActivityTaskComplete = onActivityTaskComplete;
        firestore = FirebaseFirestore.getInstance();
        activitiesRef = firestore.collection(ActivitiesConstans.COLLECTION);
    }

    public void getActivities() {
        activitiesRef
                .orderBy(ActivitiesConstans.START_DATE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        onActivityTaskComplete.onFetchActivitiesSucceed(task.getResult().toObjects(ActivityModel.class));
                    else
                        onActivityTaskComplete.onFetchFailed(task.getException());
                });
    }

    public void getActivity(String documentId) {
        activitiesRef
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        onActivityTaskComplete.onFetchActivityDetailsSuccedd(task.getResult().toObject(ActivityModel.class));
                    else
                        onActivityTaskComplete.onFetchFailed(task.getException());
                });
    }


    public interface OnActivityTaskComplete {
        void onFetchActivitiesSucceed(List<ActivityModel> activityModelList);

        void onFetchActivityDetailsSuccedd(ActivityModel activityModel);

        void onFetchFailed(Exception e);
    }
}
