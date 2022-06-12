package com.guadmoy.systemlearning.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.act.EditKyrsAct
import com.guadmoy.systemlearning.act.EditModuleActivity

import com.guadmoy.systemlearning.databinding.ModuleListItemBinding
import com.guadmoy.systemlearning.model.Module


class ModuleEditRcAdapter(val editKyrsAct: EditKyrsAct): RecyclerView.Adapter<ModuleEditRcAdapter.ModuleHolder>() {
    val moduleArray = ArrayList<Module>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleHolder {
        val binding = ModuleListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ModuleHolder(binding, editKyrsAct)
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


    class ModuleHolder(val binding: ModuleListItemBinding, val editKyrsAct: EditKyrsAct): RecyclerView.ViewHolder(binding.root)  {
        fun setData(module: Module) = with(binding) {
            tvTitleModuleItem.text = module.nameTheme
            itemView.setOnClickListener{
                val editIntent = Intent(editKyrsAct,EditModuleActivity::class.java)
                editIntent.putExtra(MainActivity.EDIT_STATE,true)
                editIntent.putExtra("Module",module)
                editKyrsAct.startActivity(editIntent)
            }
        }

    }


}