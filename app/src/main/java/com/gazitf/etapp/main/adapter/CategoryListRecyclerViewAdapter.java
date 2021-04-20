package com.gazitf.etapp.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.databinding.RecyclerViewItemCategoryBinding;
import com.gazitf.etapp.model.CategoryModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/*
 * @created 22/03/2021 - 5:54 PM
 * @project EtApp
 * @author fnkaya
 */
public class CategoryListRecyclerViewAdapter extends RecyclerView.Adapter<CategoryListRecyclerViewAdapter.CategoriesViewHolder> {

    private List<CategoryModel> categoryModelList;

    public CategoryListRecyclerViewAdapter(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerViewItemCategoryBinding binding = RecyclerViewItemCategoryBinding.inflate(inflater, parent, false);

        return new CategoriesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        CategoryModel categoryModel = categoryModelList.get(position);
        Picasso.get()
                .load(categoryModel.getImageUrl())
                .into(holder.imageViewImageUrl);
        holder.textViewName.setText(categoryModel.getName());
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewImageUrl;
        private TextView textViewName;

        public CategoriesViewHolder(@NonNull RecyclerViewItemCategoryBinding binding) {
            super(binding.getRoot());

            imageViewImageUrl = binding.imageViewCategoryImage;
            textViewName = binding.textViewCategoryName;
        }
    }
}
