package com.guadmoy.systemlearning.utils

import androidx.recyclerview.widget.DiffUtil
import com.guadmoy.systemlearning.model.Course

class DiffUtillHelper(val oldList: List<Course>, val newList: List<Course>): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}