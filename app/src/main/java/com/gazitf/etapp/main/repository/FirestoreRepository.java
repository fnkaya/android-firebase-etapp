package com.gazitf.etapp.main.repository;

import androidx.annotation.NonNull;

import com.gazitf.etapp.main.model.Category;
import com.gazitf.etapp.main.repository.FirestoreDbConstants.CategoryConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static com.gazitf.etapp.main.repository.FirestoreDbConstants.ActivitiesConstans;
import static com.gazitf.etapp.main.repository.FirestoreDbConstants.CategoryConstants;

/*
 * @created 22/03/2021 - 6:18 PM
 * @project EtApp
 * @author fnkaya
 */
public class FirestoreRepository {

    private OnFirestoreTaskComplete onFirestoreTaskComplete;

    private FirebaseFirestore firestore;
    private CollectionReference categoriesRef;
    private CollectionReference activitiesRef;

    public FirestoreRepository(OnFirestoreTaskComplete onFirestoreTaskComplete) {
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
        firestore = FirebaseFirestore.getInstance();
        categoriesRef = firestore.collection(CategoryConstants.COLLECTION);
        activitiesRef = firestore.collection(ActivitiesConstans.COLLECTION);
    }

    public void getCategories() {
        categoriesRef
                .orderBy(CategoryConstants.NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                            onFirestoreTaskComplete.onFirestoreTaskSuccessed(task.getResult().toObjects(Category.class));
                        else
                            onFirestoreTaskComplete.onFirestoreTaskFailed(task.getException());
                    }
                });
    }

    public interface OnFirestoreTaskComplete {
        void onFirestoreTaskSuccessed(List<Category> categoryList);

        void onFirestoreTaskFailed(Exception e);
    }
}
