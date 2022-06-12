package com.guadmoy.systemlearning.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.databinding.ActivityEditPracticeBinding
import com.guadmoy.systemlearning.model.Course
import com.guadmoy.systemlearning.model.DbManager
import com.guadmoy.systemlearning.model.Module
import com.guadmoy.systemlearning.model.Practice
import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel

class EditPracticeActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditPracticeBinding
    private var practice: Practice? = null
    private var course: Course? = null
    private val fireBaseViewModel: FirebaseViewModel by viewModels()
    private val dbManager = DbManager()
    private var isEditState = false
    private var courseKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkEditState()
    }

    private fun isEditState():Boolean{
        return intent.getBooleanExtra(MainActivity.EDIT_STATE,false)
    }

    private fun checkEditState(){

        if(isEditState()){
            practice = intent.getSerializableExtra("Practice") as Practice
            isEditState = true
            courseKey = practice!!.keyCourse.toString()
            fillViews(practice!!)
        }else{
            course = intent.getSerializableExtra("CourseKey") as Course
            courseKey = course?.key.toString()
        }
    }

    fun onClickPublish(view: View){
        val tempKey = practice?.key
        practice = fillModule()
        if(isEditState){
            dbManager.publishModulePractice(practice!!.copy(key = tempKey),onPublishFinish())
        }else{
            dbManager.publishModulePractice(practice!!,onPublishFinish())
            dbManager.publishCourse(course?.copy(uid = course!!.uid, countModulePractice = course!!.countModulePractice!! +1)!!, onPublishFinish())
        }
    }
    private fun onPublishFinish() : DbManager.FinishWorkListener{
        return object : DbManager.FinishWorkListener{
            override fun onFinish() {
                finish()
            }
        }
    }

    private fun fillModule():Practice{
        val practice:Practice
        binding.apply {
            practice = Practice(
                edPracticQ.text.toString(),
                edPracticA.text.toString(),
                typeQuestion.checkedRadioButtonId,

                dbManager.dbCourses.push().key,
                courseKey,
            )
        }
        return practice
    }

    private  fun fillViews(practice: Practice) = with(binding){
        edPracticQ.setText(practice.question)
        typeQuestion.check(practice.typeQuestion!!)
        edPracticA.setText(practice.answer)
    }

    fun onClickBack(view: View) {
        finish()
    }

}