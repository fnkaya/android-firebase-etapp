package com.gazitf.etapp.main.modelview;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gazitf.etapp.main.model.Category;
import com.gazitf.etapp.main.repository.FirestoreRepository;

import java.util.List;

/*
 * @created 23/03/2021 - 8:08 PM
 * @project EtApp
 * @author fnkaya
 */
public class HomeViewModel extends ViewModel implements FirestoreRepository.OnFirestoreTaskComplete {

    private MutableLiveData<List<Category>> categoryList;
    private final FirestoreRepository repository;

    public HomeViewModel() {
        categoryList = new MutableLiveData<>();
        repository = new FirestoreRepository(this);
        repository.getCategories();
    }

    public LiveData<List<Category>> getCategoryList() {
        return categoryList;
    }

    @Override
    public void onFirestoreTaskSuccessed(List<Category> categoryList) {
        this.categoryList.setValue(categoryList);
    }

    @Override
    public void onFirestoreTaskFailed(Exception e) {

    }
}
