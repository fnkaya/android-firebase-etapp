package com.gazitf.etapp.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.RecyclerViewItemCategoryBinding;
import com.gazitf.etapp.main.model.Category;
import com.squareup.picasso.Picasso;

import java.util.List;

/*
 * @created 22/03/2021 - 5:54 PM
 * @project EtApp
 * @author fnkaya
 */
public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.CategoriesViewHolder> {

    private List<Category> categories;

    public CategoriesRecyclerViewAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_category, parent, false);

        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        Category category = categories.get(position);
        Picasso.get()
                .load(category.getImageUrl())
                .into(holder.imageViewImageUrl);
        holder.textViewName.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {

        private RecyclerViewItemCategoryBinding binding;
        private ImageView imageViewImageUrl;
        private TextView textViewName;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
            binding = RecyclerViewItemCategoryBinding.inflate(inflater, (ViewGroup) itemView, false);

            imageViewImageUrl = binding.imageViewCategoryImage;
            textViewName = binding.textViewCategoryName;

            /*imageViewImageUrl = itemView.findViewById(R.id.image_view_category_image);
            textViewName = itemView.findViewById(R.id.text_view_category_name);*/
        }
    }
}
