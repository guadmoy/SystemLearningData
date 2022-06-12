package com.guadmoy.systemlearning.act

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TabHost.TabSpec
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.adapters.ImageAdapter
import com.guadmoy.systemlearning.adapters.ModuleEditRcAdapter
import com.guadmoy.systemlearning.adapters.PracticeRcAdapter
import com.guadmoy.systemlearning.databinding.ActivityEditKyrsBinding
import com.guadmoy.systemlearning.dialoghelper.DialogSpinnerHelper
import com.guadmoy.systemlearning.frag.FragmentCloseInterface
import com.guadmoy.systemlearning.frag.ImageListFrag
import com.guadmoy.systemlearning.model.Account
import com.guadmoy.systemlearning.model.Course
import com.guadmoy.systemlearning.model.DbManager
import com.guadmoy.systemlearning.model.Module
import com.guadmoy.systemlearning.utils.ImagePicker
import com.guadmoy.systemlearning.utils.UneverHelper
import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel
import java.io.ByteArrayOutputStream


class EditKyrsAct : AppCompatActivity(),FragmentCloseInterface {
    lateinit var rootElement: ActivityEditKyrsBinding
    lateinit var imageAdapter: ImageAdapter
    private val dialog = DialogSpinnerHelper()
    private val dbManager = DbManager()
    var chooseImageFrag: ImageListFrag? = null
    var editImagePos = 0;
    private var imageIndex = 0;
    private var isEditState = false
    private var course: Course? = null
    private var account: Account? = null
    var accountArray = ArrayList<Account>()
    var moduleArray = ArrayList<Module>()
    private val fireBaseViewModel: FirebaseViewModel by viewModels()
    val mAuth = Firebase.auth
    private val moduleRcAdapter = ModuleEditRcAdapter(this)
    private val practiceRcAdapter = PracticeRcAdapter(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditKyrsBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        initTabsHost(0)

        initViewsInfo()
        fireBaseViewModel.loadInfoAccount()
        course?.let { fireBaseViewModel.loadModule(it) }
        course?.let { fireBaseViewModel.loadModulePractice(it) }

        init()
        checkEditState()
    }
    override fun onResume() {
        super.onResume()
        course?.let { fireBaseViewModel.loadModule(it) }
        course?.let { fireBaseViewModel.loadModulePractice(it) }
        course?.let { fireBaseViewModel.loadFeedback(it) }
        initViewsInfo()
    }

    private fun checkEditState(){
        if(isEditState()){
            course = intent.getSerializableExtra(MainActivity.COURSE_DATA) as Course
            isEditState = true
            fillViews(course!!)
        }
    }
    private fun isEditState():Boolean{
        return intent.getBooleanExtra(MainActivity.EDIT_STATE,false)
    }


    private fun init(){
        imageAdapter = ImageAdapter()
        rootElement.vpImages.adapter = imageAdapter
        initRecViewAdapter()
    }

    private fun initTabsHost(index:Int){
        rootElement.tabHost.setup()
        var tabSpec: TabSpec = rootElement.tabHost.newTabSpec("tag1")

        tabSpec.setContent(R.id.tab1)
        tabSpec.setIndicator("Основные")
        rootElement.tabHost.addTab(tabSpec)

        tabSpec = rootElement.tabHost.newTabSpec("tag2")
        tabSpec.setContent(R.id.tab2)
        tabSpec.setIndicator("Лекции")
        rootElement.tabHost.addTab(tabSpec)


        tabSpec = rootElement.tabHost.newTabSpec("tag3")
        tabSpec.setContent(R.id.tab3)
        tabSpec.setIndicator("Практика")
        rootElement.tabHost.addTab(tabSpec)

        rootElement.tabHost.setCurrentTab(index)

    }
    private fun initRecViewAdapter() = with(rootElement){
        recModule.layoutManager = LinearLayoutManager(this@EditKyrsAct)
        recModule.adapter = moduleRcAdapter
        recModulePractice.layoutManager = LinearLayoutManager(this@EditKyrsAct)
        recModulePractice.adapter = practiceRcAdapter

    }


    fun initViewsInfo(){
        fireBaseViewModel.liveAccountData.observe(this) {
            accountArray.clear()
            accountArray.addAll(it)
        }
        fireBaseViewModel.liveModuleData.observe(this){
            moduleRcAdapter.updateAdapterWithClear(it)
        }
        fireBaseViewModel.livePracticeData.observe(this){
            practiceRcAdapter.updateAdapterWithClear(it)
        }
    }


    fun onClickSelectCity(view:View){
        val listCity = UneverHelper.getAllCity(this)
        dialog.showSpinnerDialog(this,listCity,rootElement.tvCity)
        if(rootElement.tvUnever.text.toString() != getString(R.string.select_unever)){
            rootElement.tvUnever.text = getString(R.string.select_unever)
        }
    }
    fun onClickSelectUnever(view:View){
        val selectCity = rootElement.tvCity.text.toString()
        if(selectCity == getString(R.string.select_city)){
            Toast.makeText(this, "Сначала выберите город!", Toast.LENGTH_LONG).show()
        }else{
            val listUnever = UneverHelper.getAllUnever(selectCity,this)
            dialog.showSpinnerDialog(this,listUnever,rootElement.tvUnever)
        }
    }
    fun onClickSelectCategory(view:View){
        val listCity = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialog.showSpinnerDialog(this,listCity,rootElement.tvCat)
    }
    fun onClickGetImage(view:View){

        if(imageAdapter.mainArray.size == 0){
            ImagePicker.getMultiSelectImage(this,3)
        }else{
            openChooseFragment(null)
            chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
        }
    }
    fun onClickBack(view: View){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    fun onClickAddModule(view:View){
        if(course?.key != null) startActivity(Intent(this, EditModuleActivity::class.java).apply {
            putExtra("CourseKey", course)
        })
        else Toast.makeText(
            this,
            "Сначала сохраните курс! Затем откройте его, для дальнейшего заполнения!",
            Toast.LENGTH_LONG
        ).show()
    }

    fun onClickAddPractice(view:View){
        if(course?.key != null) startActivity(Intent(this, EditPracticeActivity::class.java).apply {
            putExtra("CourseKey", course)
        })
        else Toast.makeText(
            this,
            "Сначала сохраните курс! Затем откройте его, для дальнейшего заполнения!",
            Toast.LENGTH_LONG
        ).show()
    }

    //Функции публикации
    fun onClickPublish(view: View){
        val tempKey = course?.key
        course = fillCourse()
        if(isEditState){
            course!!.key = tempKey
            uploadImages()
        }else{
            uploadImages()
        }
    }
    private fun onPublishFinish() : DbManager.FinishWorkListener{
            return object : DbManager.FinishWorkListener{
                override fun onFinish() {
                    finish()
                }
            }
    }

    //Функции заполнения БД и считывания с БД
    private fun fillCourse():Course{
        val course:Course
        rootElement.apply {
            course = Course(
                tvCity.text.toString(),
                tvUnever.text.toString(),
                edLangue.text.toString(),
                edName.text.toString(),
                tvCat.text.toString(),
                edDescription.text.toString(),
                edUriVideo.text.toString(),
                edRequest.text.toString(),
                edAudit.text.toString(),
                edTime.text.toString(),
                edAboutCourse.text.toString(),
                switchCertiwicat.isChecked(),
                switchTypeCourse.isChecked(),
                edPrice.text.toString(),
                dbManager.dbCourses.push().key,
                dbManager.auth.uid,
                System.currentTimeMillis().toString(),
                false,
                false,
                0,
                recModule.adapter?.itemCount,
                0.0,
                recModulePractice.adapter?.itemCount,
                0.0,

                //Блок учителя
                accountArray[0].name.toString(),
                accountArray[0].description.toString(),
                accountArray[0].avatarImage.toString(),
            )
        }
        return course
    }
    private fun fillViews(course: Course) = with(rootElement){
        tvCity.text = course.city
        tvUnever.text = course.unever
        edLangue.setText(course.langue)
        edName.setText(course.name)
        tvCat.text = course.category
        edDescription.setText(course.description)
        edUriVideo.setText(course.uriVideoCourse)
        edRequest.setText(course.request)
        edAudit.setText(course.audit)
        edTime.setText(course.time)
        edAboutCourse.setText(course.about)
        switchCertiwicat.isChecked = course.certificate
        switchTypeCourse.isChecked = course.typeCourse
        edPrice.setText(course.price)
    }

    // Функции с открытием камеры
    override fun onFragClose(list: ArrayList<Bitmap>) {
        rootElement.svMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFrag = null
    }
    fun openChooseFragment(newList: ArrayList<Uri>?){
        chooseImageFrag = ImageListFrag(this)
        if (newList != null) chooseImageFrag?.resizeSelectImage(newList,true,this)

        rootElement.svMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFrag!!)
        fm.commit()
    }


    //Функции с картинками
    private fun uploadImages(){
        if(imageAdapter.mainArray.size == imageIndex){
            dbManager.publishCourse(course!!,onPublishFinish())
            return
        }
       val byteArray =  prepareImageByArray(imageAdapter.mainArray[imageIndex])
        uploadImage(byteArray){
          //  dbManager.publishCourse(course!!,onPublishFinish())
            nextImage(it.result.toString())
        }
    }
    private fun nextImage(uri: String){
        setImageUri(uri)
        imageIndex++
        uploadImages()
    }
    private fun setImageUri(uri: String){
            when(imageIndex){
                0-> course = course?.copy(mainImage = uri)
                1-> course = course?.copy(firstImage = uri)
                2-> course = course?.copy(secondImage = uri)
            }
    }
    private fun prepareImageByArray(bitmap: Bitmap):ByteArray{
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,20,outStream)
        return outStream.toByteArray()
    }
    private fun uploadImage(byteArray: ByteArray,listener:OnCompleteListener<Uri>) {
        val itemStorageReference = dbManager.dbStorage.child(dbManager.auth.uid!!)
            .child("image_${System.currentTimeMillis()}")
        val upTask = itemStorageReference.putBytes(byteArray)
        upTask.continueWithTask{task ->
            itemStorageReference.downloadUrl
        }.addOnCompleteListener(listener)

    }




}