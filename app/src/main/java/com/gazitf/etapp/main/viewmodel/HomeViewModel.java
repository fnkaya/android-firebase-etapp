package com.gazitf.etapp.main.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gazitf.etapp.model.ActivityModel;
import com.gazitf.etapp.model.CategoryModel;
import com.gazitf.etapp.repository.DbConstants;
import com.gazitf.etapp.repository.FirestoreActivityRepository;
import com.gazitf.etapp.repository.FirestoreCategoryRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;

/*
 * @created 23/03/2021 - 8:08 PM
 * @project EtApp
 * @author fnkaya
 */
public class HomeViewModel extends ViewModel implements FirestoreCategoryRepository.OnCategoryTaskCompleteCallback, FirestoreActivityRepository.OnActivityTaskCompleteCallback {

    private MutableLiveData<List<CategoryModel>> categoryList;
    private MutableLiveData<List<ActivityModel>> activityList;
    private final FirestoreCategoryRepository categoryRepository;
    private final FirestoreActivityRepository activityRepository;

    public HomeViewModel() {
        categoryList = new MutableLiveData<>();
        activityList = new MutableLiveData<>();
        categoryRepository = new FirestoreCategoryRepository(this);
        activityRepository = new FirestoreActivityRepository(this);
        categoryRepository.getCategories();
        activityRepository.getActivities();
    }

    public LiveData<List<CategoryModel>> getCategoryList() {
        return categoryList;
    }

    public LiveData<List<ActivityModel>> getActivityList() {
        return activityList;
    }

    public void getActivitiesByCategory(String categoryId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection(DbConstants.Categories.COLLECTION)
                .document(categoryId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        firestore
                                .collection(DbConstants.Activities.COLLECTION)
                                .whereEqualTo(DbConstants.Activities.CATEGORY, documentSnapshot.getReference())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        List<ActivityModel> activityModelList = queryDocumentSnapshots.toObjects(ActivityModel.class);
                                        activityList.postValue(activityModelList);
                                    }
                                    else
                                        activityList.postValue(Collections.emptyList());
                                });
                    }
                });
    }

    @Override
    public void onCategoryFetchSucceed(List<CategoryModel> categoryModelList) {
        categoryList.setValue(categoryModelList);
    }

    @Override
    public void onCategoryFetchFailed(Exception e) {
        Log.i("TAG", "onCategoryFetchFailed: ", e);
    }

    @Override
    public void onActivityListFetchSucceed(List<ActivityModel> activityModelList) {
        activityList.setValue(activityModelList);
    }

    @Override
    public void onActivityFetchFailed(Exception e) {
        Log.i("TAG", "onCategoryFetchFailed: ", e);
    }
}
