package com.guadmoy.systemlearning.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.act.*
import com.guadmoy.systemlearning.databinding.ActivityPracticeBinding

import com.guadmoy.systemlearning.databinding.ModuleListItemBinding
import com.guadmoy.systemlearning.databinding.ModuleListPracticeBinding
import com.guadmoy.systemlearning.model.Account
import com.guadmoy.systemlearning.model.Course
import com.guadmoy.systemlearning.model.Module
import com.guadmoy.systemlearning.model.Practice
import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel


class PracticeViewRcAdapter(val descriptionCourse: DescriptionCourse): RecyclerView.Adapter<PracticeViewRcAdapter.PracticeHolder>() {
    val practiceArray = ArrayList<Practice>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PracticeHolder {
        val binding = ModuleListPracticeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PracticeHolder(binding, descriptionCourse)
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


    class PracticeHolder(val binding: ModuleListPracticeBinding, val descriptionCourse: DescriptionCourse): RecyclerView.ViewHolder(binding.root)  {
        fun setData(practice: Practice) = with(binding) {
            tvTitleQestion.text = practice.question
            showPanels(isOwner(descriptionCourse.coursetemp!!))
            btEditPractice.setOnClickListener{
                val editIntent = Intent(descriptionCourse,EditPracticeActivity::class.java)
                editIntent.putExtra(MainActivity.EDIT_STATE,true)
                editIntent.putExtra("Practice",practice)
                descriptionCourse.startActivity(editIntent)
            }
            tvEditPractice.setOnClickListener{
                val editIntent = Intent(descriptionCourse,EditPracticeActivity::class.java)
                editIntent.putExtra(MainActivity.EDIT_STATE,true)
                editIntent.putExtra("Practice",practice)
                descriptionCourse.startActivity(editIntent)
            }

            practiceCourse.setOnClickListener{
                val editIntent = Intent(descriptionCourse,PracticeActivity::class.java)
                editIntent.putExtra("PRACTICE_DATA",practice)
                editIntent.putExtra("course",descriptionCourse.coursetemp)
                descriptionCourse.startActivity(editIntent)
            }
        }

        private fun isOwner(course: Course):Boolean{
            return course.uid == descriptionCourse.mAuth.uid
        }

        private fun showPanels(isOwner:Boolean){
            if(isOwner) {
                binding.editPanel.visibility  = View.VISIBLE
            }else{
                binding.editPanel.visibility  = View.GONE
            }
        }

    }




}