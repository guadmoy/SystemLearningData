package com.guadmoy.systemlearning.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.databinding.ActivityPracticeBinding
import com.guadmoy.systemlearning.model.Course
import com.guadmoy.systemlearning.model.DbManager
import com.guadmoy.systemlearning.model.Module
import com.guadmoy.systemlearning.model.Practice
import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel

class PracticeActivity : AppCompatActivity() {
    lateinit var rootElement: ActivityPracticeBinding
    private var practice: Practice? = null
    private var course: Course? = null
    private val dbManager = DbManager()
    var isDone = true
    private val fireBaseViewModel: FirebaseViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        isFindModule()
    }


    fun isFindModule() {
        practice = intent.getSerializableExtra("PRACTICE_DATA") as Practice

        course = intent.getSerializableExtra("course") as Course
        fillViews(practice!!)
    }

    fun onClickBack(view: View) {
        finish()
    }

    private fun fillViews(practice: Practice) = with(rootElement) {
        editAnswerInput.visibility = View.GONE
        changeAnswer.visibility = View.GONE
        tvTitlePracticeNameMain.text = practice.question
        Log.d("MyLog", practice.typeQuestion.toString())
        if (practice.typeQuestion == 2131296347 || practice.typeQuestion == 2131296831) {
            editAnswerInput.visibility = View.VISIBLE
        }
        if (practice.typeQuestion == 2131296440) {
            changeAnswer.visibility = View.VISIBLE
            val answer = practice.answer!!.split(";")
            radioButton.text = answer[0];
            radioButton2.text = answer[1];
            radioButton3.text = answer[2];
            radioButton4.text = answer[3];
        }
        if (practice.typeQuestion == 2131296993) {
            editAnswerInput.visibility = View.GONE
            changeAnswer.visibility = View.GONE
            doneInput.visibility = View.GONE
        }

    }

    fun onCLickDone(view: View) {
        if (practice!!.typeQuestion == 2131296347 || practice!!.typeQuestion == 2131296831) {
            if (rootElement.editAnswerInput.text.toString() == practice!!.answer) {
                dbManager.publishCoursePracticeProgress(practice!!, onPublishFinish())
                dbManager.publishCourseProgress(
                    course?.copy(
                        key = course!!.key,
                        countModulePracticeDone = course!!.countModulePracticeDone!! + 1
                    )!!, onPublishFinish()
                )
            } else {
                Toast.makeText(this, "Неверный ответ", Toast.LENGTH_LONG).show()
            }
        }
        if (practice!!.typeQuestion == 2131296440) {
            if (findViewById<RadioButton>(rootElement.changeAnswer.checkedRadioButtonId).text.toString() == practice!!.answer!!.split(
                    ";"
                )[0]
            ) {
                dbManager.publishCoursePracticeProgress(practice!!, onPublishFinish())
                dbManager.publishCourseProgress(
                    course?.copy(
                        key = course!!.key,
                        countModulePracticeDone = course!!.countModulePracticeDone!! + 1
                    )!!, onPublishFinish()
                )
            } else {
                Toast.makeText(this, "Неверный ответ", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun onPublishFinish(): DbManager.FinishWorkListener {
        return object : DbManager.FinishWorkListener {
            override fun onFinish() {
                finish()
            }
        }
    }
}