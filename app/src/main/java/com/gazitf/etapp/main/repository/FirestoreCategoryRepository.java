package com.gazitf.etapp.main.repository;

import com.gazitf.etapp.main.model.CategoryModel;
import com.gazitf.etapp.main.repository.FirestoreDbConstants.CategoryConstants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/*
 * @created 22/03/2021 - 6:18 PM
 * @project EtApp
 * @author fnkaya
 */
public class FirestoreCategoryRepository {

    private OnCategoryTaskComplete onCategoryTaskComplete;

    private FirebaseFirestore firestore;
    private CollectionReference categoriesRef;

    public FirestoreCategoryRepository(OnCategoryTaskComplete onCategoryTaskComplete) {
        this.onCategoryTaskComplete = onCategoryTaskComplete;
        firestore = FirebaseFirestore.getInstance();
        categoriesRef = firestore.collection(CategoryConstants.COLLECTION);
    }

    public void getCategories() {
        categoriesRef
                .orderBy(CategoryConstants.NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        onCategoryTaskComplete.onFetchCategoriesSucceed(task.getResult().toObjects(CategoryModel.class));
                    else
                        onCategoryTaskComplete.onFetchCategoriesFailed(task.getException());
                });
    }

    public interface OnCategoryTaskComplete {
        void onFetchCategoriesSucceed(List<CategoryModel> categoryModelList);

        void onFetchCategoriesFailed(Exception e);
    }
}
