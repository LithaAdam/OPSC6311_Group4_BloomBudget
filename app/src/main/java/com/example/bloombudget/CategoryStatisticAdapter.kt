package com.example.bloombudget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bloombudget.databinding.ItemCategoryStatisticsBinding

data class CategoryStatistic(
    val name: String,
    val amount: Double,
    val percentage: Double,
    val colorResId: Int
)

class CategoryStatisticAdapter(private val statistics: List<CategoryStatistic>) :
    RecyclerView.Adapter<CategoryStatisticAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCategoryStatisticsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryStatisticsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stat = statistics[position]
        val context = holder.binding.root.context
        holder.binding.categoryNameText.text = stat.name
        holder.binding.categoryAmountText.text = context.getString(R.string.format_amount_positive, stat.amount)
        holder.binding.categoryPercentageText.text = context.getString(R.string.format_percentage, stat.percentage)
        holder.binding.categoryDot.setBackgroundResource(stat.colorResId)
    }

    override fun getItemCount() = statistics.size
}
