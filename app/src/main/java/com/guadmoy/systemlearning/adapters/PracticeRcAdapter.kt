package com.guadmoy.systemlearning.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.act.EditKyrsAct
import com.guadmoy.systemlearning.act.EditModuleActivity
import com.guadmoy.systemlearning.act.EditPracticeActivity
import com.guadmoy.systemlearning.databinding.ModuleListPracticeBinding
import com.guadmoy.systemlearning.model.Module
import com.guadmoy.systemlearning.model.Practice

class PracticeRcAdapter(val editKyrsAct: EditKyrsAct): RecyclerView.Adapter<PracticeRcAdapter.PracticeHolder>() {
    val practiceArray = ArrayList<Practice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PracticeHolder {
        val binding = ModuleListPracticeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PracticeHolder(binding,editKyrsAct)
    }

    override fun onBindViewHolder(holder: PracticeHolder, position: Int) {
        holder.setData(practiceArray[position])
    }

    override fun getItemCount(): Int {
        return practiceArray.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterWithClear(newList: List<Practice>){
        practiceArray.clear()
        practiceArray.addAll(newList)
        notifyDataSetChanged()
    }


    class PracticeHolder(val binding: ModuleListPracticeBinding,val editKyrsAct: EditKyrsAct): RecyclerView.ViewHolder(binding.root)  {
        fun setData(practice: Practice) = with(binding){
            tvTitleQestion.text = practice.question
            itemView.setOnClickListener{
                val editIntent = Intent(editKyrsAct, EditPracticeActivity::class.java)
                editIntent.putExtra(MainActivity.EDIT_STATE,true)
                editIntent.putExtra("Practice",practice)
                editKyrsAct.startActivity(editIntent)
            }
        }

    }
}