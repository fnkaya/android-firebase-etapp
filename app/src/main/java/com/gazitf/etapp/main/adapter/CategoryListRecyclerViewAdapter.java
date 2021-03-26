package com.gazitf.etapp.main.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.databinding.RecyclerViewItemCategoryBinding;
import com.gazitf.etapp.main.model.Category;
import com.squareup.picasso.Picasso;

import java.util.List;

/*
 * @created 22/03/2021 - 5:54 PM
 * @project EtApp
 * @author fnkaya
 */
public class CategoryListRecyclerViewAdapter extends RecyclerView.Adapter<CategoryListRecyclerViewAdapter.CategoriesViewHolder> {

    private List<Category> categoryList;

    public CategoryListRecyclerViewAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
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
        Category category = categoryList.get(position);
        Picasso.get()
                .load(category.getImageUrl())
                .into(holder.imageViewImageUrl);
        holder.textViewName.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
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
