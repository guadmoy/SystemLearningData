package com.guadmoy.systemlearning.frag

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guadmoy.systemlearning.act.EditKyrsAct
import com.guadmoy.systemlearning.databinding.SelectImageFragItemBinding
import com.guadmoy.systemlearning.utils.AdapterCallback
import com.guadmoy.systemlearning.utils.ImagePicker
import com.guadmoy.systemlearning.utils.ItemTouchMoveCallback

class SelectImageRvAdapter(val adapterCallback: AdapterCallback) : RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(),ItemTouchMoveCallback.ItemTouchAdapter {
    val mainArray = ArrayList<Bitmap>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val viewBinding = SelectImageFragItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageHolder(viewBinding,parent.context ,this)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])

    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    override fun onMove(startPos: Int, targetPos: Int) {
        val targetItem = mainArray[targetPos]
        mainArray[targetPos] = mainArray[startPos]
        mainArray[startPos] = targetItem
        notifyItemMoved(startPos,targetPos)
    }


    class ImageHolder(val viewBinding: SelectImageFragItemBinding, val context:Context, val adapter: SelectImageRvAdapter) : RecyclerView.ViewHolder(viewBinding.root) {

            fun setData(iem:Bitmap){
                viewBinding.ibEditItem.setOnClickListener{
                    ImagePicker.getSingleSelectImage(context as EditKyrsAct)
                }
                viewBinding.ibDeleteItem.setOnClickListener{
                    adapter.mainArray.removeAt(adapterPosition)
                    adapter.notifyItemRemoved(adapterPosition)
                    for(n in 0 until adapter.mainArray.size) adapter.notifyItemChanged(n)
                    adapter.adapterCallback.onItemDelete()
                }

                viewBinding.ivContent.setImageBitmap(iem)

            }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(newList: List<Bitmap>, needClear:Boolean){
        if(needClear) mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }
}