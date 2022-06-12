package com.guadmoy.systemlearning.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.databinding.ActivityEditFeedbackBinding
import com.guadmoy.systemlearning.model.*
import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel

class EditFeedbackActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditFeedbackBinding
    private val dbManager = DbManager()
    private var account: Account? = null
    private var courseKey = ""
    private var course: Course? = null
    private var feedback: Feedback? = null
    private var isEditState = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        checkEditState()
    }

    private fun isEditState():Boolean{
        return intent.getBooleanExtra(MainActivity.EDIT_STATE,false)
    }
    private fun checkEditState(){

        if(isEditState()){
            feedback = intent.getSerializableExtra("Feedback") as Feedback
            account = intent.getSerializableExtra("Account") as Account
            isEditState = true
            courseKey = feedback!!.keyCourse.toString()
            fillViews(feedback!!)
        }else{
            course = intent.getSerializableExtra("CourseKey") as Course
            courseKey = course?.key.toString()
            account = intent.getSerializableExtra("Account") as Account
        }
    }

    fun onClickPublish(view: View){
        val tempKey = feedback?.key
        feedback = fillModule()
        if(isEditState){
            dbManager.publishModuleFeedback(feedback!!.copy(key = tempKey),onPublishFinish())
        }else{
            dbManager.publishModuleFeedback(feedback!!,onPublishFinish())
            dbManager.publishCourse(course!!.copy(key = courseKey, ratingCounterPeople = course!!.ratingCounterPeople +1,
                ratingCounter = (course!!.ratingCounter + ((binding.ratingBarPractices.rating + binding.ratingBarLectures.rating + binding.ratingBarContent.rating)/3))),onPublishFinish())
        }
    }
    private fun onPublishFinish() : DbManager.FinishWorkListener{
        return object : DbManager.FinishWorkListener{
            override fun onFinish() {
                finish()
            }
        }
    }

    private fun fillModule(): Feedback {
        val feedback: Feedback
        binding.apply {
            feedback = Feedback(
                edTextNoLike.text.toString(),
                edTextLike.text.toString(),
                edTextDes.text.toString(),
                ((binding.ratingBarPractices.rating + binding.ratingBarLectures.rating + binding.ratingBarContent.rating)/3),
                account?.uid,
                account?.name!!,
                account?.avatarImage,

                dbManager.dbCourses.push().key,
                courseKey,
            )
        }
        return feedback
    }

    private  fun fillViews(feedback: Feedback) = with(binding){
        edTextNoLike.setText(feedback.negative)
        edTextLike.setText(feedback.positive)
        edTextDes.setText(feedback.des)
    }
}