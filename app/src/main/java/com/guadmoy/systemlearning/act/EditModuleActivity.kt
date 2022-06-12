package com.guadmoy.systemlearning.act

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.google.android.gms.tasks.OnCompleteListener
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.databinding.ActivityEditModuleBinding
import com.guadmoy.systemlearning.frag.ImageListFrag
import com.guadmoy.systemlearning.model.Course
import com.guadmoy.systemlearning.model.DbManager
import com.guadmoy.systemlearning.model.Module
import com.guadmoy.systemlearning.utils.ImagePicker
import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_module.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.ByteArrayOutputStream

class EditModuleActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditModuleBinding
    private var module: Module? = null
    private var course: Course? = null
    private val fireBaseViewModel: FirebaseViewModel by viewModels()
    var chooseImageFrag: ImageListFrag? = null
    var imageIndex = 0;
    var imageList = ArrayList<Bitmap>()
    var uriImage: String? = null
    private val dbManager = DbManager()
    private var isEditState = false
    private var courseKey = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditModuleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkEditState()
    }

    private fun isEditState():Boolean{
        return intent.getBooleanExtra(MainActivity.EDIT_STATE,false)
    }

    private fun checkEditState(){

        if(isEditState()){
            module = intent.getSerializableExtra("Module") as Module
            Log.d("MyLog", module.toString())
            isEditState = true
            courseKey = module!!.keyCourse.toString()
            fillViews(module!!)
        }else{
            course = intent.getSerializableExtra("CourseKey") as Course
            courseKey = course?.key.toString()
        }
    }


    fun onClickPublish(view: View){
        Log.d("MyLog", isEditState.toString())
        val tempKey = module?.key

        module = fillModule()
        if(isEditState){
            Log.d("MyLog", module.toString())
            dbManager.publishModuleItem(module!!.copy(key = tempKey),onPublishFinish())
        }else{
            dbManager.publishModuleItem(module!!,onPublishFinish())
            dbManager.publishCourse(course?.copy(uid = course!!.uid)!!, onPublishFinish())
        }
    }
    private fun onPublishFinish() : DbManager.FinishWorkListener{
        return object : DbManager.FinishWorkListener{
            override fun onFinish() {
                finish()
            }
        }
    }

    private fun fillModule():Module{
        val module:Module
        binding.apply {
            module = Module(
                edTitleTheme.text.toString(),
                edTitleDesTheme.text.toString(),
                edUriVideoTheme.text.toString(),
                edDesVideoTheme.text.toString(),

                dbManager.dbCourses.push().key,
                courseKey,
            )
        }
        return module
    }
    private  fun fillViews(module: Module) = with(binding){
        edTitleTheme.setText(module.nameTheme)
        edTitleDesTheme.setText(module.desTheme)

        edUriVideoTheme.setText(module.uriVideo)
        edDesVideoTheme.setText(module.desVideo)
    }

    fun onClickBack(view: View) {
        finish()
    }

}