package com.guadmoy.systemlearning.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.act.DescriptionCourse
import com.guadmoy.systemlearning.act.EditFeedbackActivity
import com.guadmoy.systemlearning.act.EditPracticeActivity
import com.guadmoy.systemlearning.databinding.ListFeedbackBinding
import com.guadmoy.systemlearning.model.Course
import com.guadmoy.systemlearning.model.Feedback
import com.guadmoy.systemlearning.model.Practice
import com.squareup.picasso.Picasso


class FeedbackRcAdapter(val descriptionCourse: DescriptionCourse) :
    RecyclerView.Adapter<FeedbackRcAdapter.FeedbackHolder>() {
    val feedbackArray = ArrayList<Feedback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackHolder {
        val binding =
            ListFeedbackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedbackHolder(binding, descriptionCourse)
    }

    override fun onBindViewHolder(holder: FeedbackHolder, position: Int) {
        holder.setData(feedbackArray[position])
    }

    override fun getItemCount(): Int {
        return feedbackArray.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterWithClear(newList: List<Feedback>){
        feedbackArray.clear()
        feedbackArray.addAll(newList)
        notifyDataSetChanged()
    }

    class FeedbackHolder(
        val binding: ListFeedbackBinding,
        val descriptionCourse: DescriptionCourse
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setData(feedback: Feedback) = with(binding) {
            tvFeedbackTeacher.text = feedback.teacherName
            Picasso.get().load(feedback.teacherPhoto).into(ivFeedbackTeacher)
            tvFeedbackDes.text = feedback.des
            ratingBarViewFeedback.rating = feedback.rating!!
        }

        private fun isOwner(feedback: Feedback):Boolean{
            return feedback.uid == descriptionCourse.mAuth.uid
        }
    }
}