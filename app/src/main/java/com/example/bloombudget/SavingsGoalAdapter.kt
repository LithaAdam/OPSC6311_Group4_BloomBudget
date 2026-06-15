package com.example.bloombudget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bloombudget.databinding.ItemSavingsGoalBinding

class SavingsGoalAdapter(private val goals: List<SavingsGoal>) :
    RecyclerView.Adapter<SavingsGoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(val binding: ItemSavingsGoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemSavingsGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        val context = holder.itemView.context
        holder.binding.goalTitle.text = goal.title
        holder.binding.goalAmount.text = context.getString(R.string.format_goal_amount, goal.currentAmount, goal.targetAmount)
        
        val progress = if (goal.targetAmount > 0) {
            ((goal.currentAmount / goal.targetAmount) * 100).toInt().coerceIn(0, 100)
        } else 0
        holder.binding.goalProgressBar.progress = progress
    }

    override fun getItemCount() = goals.size
}
