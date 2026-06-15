package com.example.bloombudget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bloombudget.databinding.ItemTransactionBinding

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.binding.root.context
        holder.binding.titleTextView.text = transaction.title
        holder.binding.dateTextView.text = transaction.date

        if (transaction.amount < 0) {
            holder.binding.amountTextView.text = context.getString(R.string.format_amount_negative, -transaction.amount)
            holder.binding.amountTextView.setTextColor(0xFFFF0000.toInt())
        } else {
            holder.binding.amountTextView.text = context.getString(R.string.format_amount_positive, transaction.amount)
            holder.binding.amountTextView.setTextColor(0xFF00AA00.toInt())
        }

        holder.binding.iconImageView.setImageResource(transaction.iconResId)
    }

    override fun getItemCount() = transactions.size
}
