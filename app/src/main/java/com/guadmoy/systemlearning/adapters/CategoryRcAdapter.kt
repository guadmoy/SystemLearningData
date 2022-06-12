package com.guadmoy.systemlearning.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.databinding.CategoryItemListBinding
import com.guadmoy.systemlearning.dialoghelper.DialogSpinnerHelper
import kotlinx.android.synthetic.main.main_content.*

class CategoryRcAdapter(var mainActivity: MainActivity): RecyclerView.Adapter<CategoryRcAdapter.CategoryHolder>() {
    val mainList = ArrayList<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
       val binding = CategoryItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryHolder(binding,mainActivity)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.setData(mainList[position])
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    class CategoryHolder(val binding: CategoryItemListBinding,val mainActivity:MainActivity): RecyclerView.ViewHolder(binding.root) {
        fun setData(text:String) = with(binding) {
            tvTitleCategory.text = text
            itemView.setOnClickListener {
                mainActivity.currentCategory = text
                mainActivity.onClickCat()
                mainActivity.cardFilter.visibility = View.GONE
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(list: ArrayList<String>){
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }


}