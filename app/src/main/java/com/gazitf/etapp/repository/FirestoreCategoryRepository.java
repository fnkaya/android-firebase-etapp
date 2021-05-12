package com.gazitf.etapp.repository;

import com.gazitf.etapp.model.CategoryModel;
import com.gazitf.etapp.repository.DbConstants.Categories;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/*
 * @created 22/03/2021 - 6:18 PM
 * @project EtApp
 * @author fnkaya
 */
public class FirestoreCategoryRepository {

    private OnCategoryTaskCompleteCallback onCategoryTaskCompleteCallback;

    private FirebaseFirestore firestore;
    private CollectionReference categoriesRef;

    public FirestoreCategoryRepository(OnCategoryTaskCompleteCallback onCategoryTaskCompleteCallback) {
        this.onCategoryTaskCompleteCallback = onCategoryTaskCompleteCallback;
        firestore = FirebaseFirestore.getInstance();
        categoriesRef = firestore.collection(Categories.COLLECTION);
    }

    public void getCategories() {
        categoriesRef
                .orderBy(Categories.NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        onCategoryTaskCompleteCallback.onCategoryFetchSucceed(task.getResult().toObjects(CategoryModel.class));
                    else
                        onCategoryTaskCompleteCallback.onCategoryFetchFailed(task.getException());
                });
    }

    public interface OnCategoryTaskCompleteCallback {

        void onCategoryFetchSucceed(List<CategoryModel> categoryModelList);

        void onCategoryFetchFailed(Exception e);
    }
}
