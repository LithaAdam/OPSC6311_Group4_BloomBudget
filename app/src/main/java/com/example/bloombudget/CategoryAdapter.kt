package com.example.bloombudget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bloombudget.databinding.ItemCategoryBinding

class CategoryAdapter(private val categories: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.categoryTitle.text = category.name
        val context = holder.binding.root.context
        holder.binding.categoryBudget.text = context.getString(R.string.budget_prefix, category.budget)
        holder.binding.categoryIcon.setImageResource(category.iconResId)
    }

    override fun getItemCount() = categories.size
}
