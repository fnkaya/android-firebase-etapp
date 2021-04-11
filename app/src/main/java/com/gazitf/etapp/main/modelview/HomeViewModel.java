package com.gazitf.etapp.main.modelview;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gazitf.etapp.main.model.ActivityModel;
import com.gazitf.etapp.main.model.CategoryModel;
import com.gazitf.etapp.main.repository.FirestoreActivityRepository;
import com.gazitf.etapp.main.repository.FirestoreCategoryRepository;

import java.util.List;

/*
 * @created 23/03/2021 - 8:08 PM
 * @project EtApp
 * @author fnkaya
 */
public class HomeViewModel extends ViewModel implements FirestoreCategoryRepository.OnCategoryTaskComplete, FirestoreActivityRepository.OnActivityTaskComplete {

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

    public void getActivityDetails(String documentId) {
        activityRepository.getActivity(documentId);
    }

    @Override
    public void onFetchCategoriesSucceed(List<CategoryModel> categoryModelList) {
        categoryList.setValue(categoryModelList);
    }

    @Override
    public void onFetchCategoriesFailed(Exception e) {

    }

    @Override
    public void onFetchActivitiesSucceed(List<ActivityModel> activityModelList) {
        activityList.setValue(activityModelList);
    }

    @Override
    public void onFetchActivityDetailsSuccedd(ActivityModel activityModel) {

    }

    @Override
    public void onFetchFailed(Exception e) {

    }
}
