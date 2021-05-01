package com.gazitf.etapp.main.modelview;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gazitf.etapp.model.ActivityModel;
import com.gazitf.etapp.model.CategoryModel;
import com.gazitf.etapp.repository.FirestoreActivityRepository;
import com.gazitf.etapp.repository.FirestoreCategoryRepository;

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
