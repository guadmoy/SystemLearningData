package com.guadmoy.systemlearning.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.act.DescriptionCourse
import com.guadmoy.systemlearning.act.EditKyrsAct
import com.guadmoy.systemlearning.act.EditModuleActivity
import com.guadmoy.systemlearning.act.ModuleActivity

import com.guadmoy.systemlearning.databinding.ModuleListItemBinding
import com.guadmoy.systemlearning.model.Account
import com.guadmoy.systemlearning.model.Course
import com.guadmoy.systemlearning.model.Module
import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel


class ModuleViewRcAdapter(val descriptionCourse: DescriptionCourse): RecyclerView.Adapter<ModuleViewRcAdapter.ModuleHolder>() {
    val moduleArray = ArrayList<Module>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleHolder {
        val binding = ModuleListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ModuleHolder(binding, descriptionCourse)
    }

    override fun onBindViewHolder(holder: ModuleHolder, position: Int) {
        holder.setData(moduleArray[position])
    }

    override fun getItemCount(): Int {
        return moduleArray.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterWithClear(newList: List<Module>){
        moduleArray.clear()
        moduleArray.addAll(newList)
        notifyDataSetChanged()
    }


    class ModuleHolder(val binding: ModuleListItemBinding, val descriptionCourse: DescriptionCourse): RecyclerView.ViewHolder(binding.root)  {
        fun setData(module: Module) = with(binding) {
            tvTitleModuleItem.text = module.nameTheme
            showPanels(isOwner(descriptionCourse.coursetemp!!))
            ibEditModule.setOnClickListener{
                val editIntent = Intent(descriptionCourse,EditModuleActivity::class.java)
                editIntent.putExtra(MainActivity.EDIT_STATE,true)
                editIntent.putExtra("Module",module)
                descriptionCourse.startActivity(editIntent)
            }
            tvEditModule.setOnClickListener{
                val editIntent = Intent(descriptionCourse,EditModuleActivity::class.java)
                editIntent.putExtra(MainActivity.EDIT_STATE,true)
                editIntent.putExtra("Module",module)
                descriptionCourse.startActivity(editIntent)
            }

            moduleCourse.setOnClickListener{
                val editIntent = Intent(descriptionCourse,ModuleActivity::class.java)
                editIntent.putExtra("MODULE_DATA",module)
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