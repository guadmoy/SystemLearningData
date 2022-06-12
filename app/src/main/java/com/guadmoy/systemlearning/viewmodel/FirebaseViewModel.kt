package com.guadmoy.systemlearning.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guadmoy.systemlearning.model.*

class FirebaseViewModel:ViewModel() {
    private val dbManager = DbManager()
    val liveCourseData = MutableLiveData<ArrayList<Course>>()
    val liveCurrentCourseData = MutableLiveData<ArrayList<Course>>()
    val liveAccountData = MutableLiveData<ArrayList<Account>>()
    val liveModuleData = MutableLiveData<ArrayList<Module>>()
    val livePracticeData = MutableLiveData<ArrayList<Practice>>()
    val liveFeedbackData = MutableLiveData<ArrayList<Feedback>>()

    fun loadAllCourseFirstPage(){
        dbManager.getAllCourseFirstPage(object : DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Course>) {
                liveCourseData.value?.clear()
                liveCourseData.value = list
            }
        })
    }
    fun loadCurrentCourse(uuid:String){
        dbManager.loadCurrentCourse(uuid,object : DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Course>) {
                liveCurrentCourseData.value?.clear()
                liveCurrentCourseData.value = list
            }
        })
    }
    fun loadAllCourseNextPage(time:String){
        dbManager.getAllCourseNextPage(time,object : DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Course>) {
                liveCourseData.value = list
            }
        })
    }


    fun loadNameCourseFirstPage(name:String){
        dbManager.getNameCourseFirstPage(name,object : DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Course>) {
                liveCourseData.value?.clear()
                liveCourseData.value = list
            }
        })
    }
    fun loadNameCourseNextPage(time:String){
        dbManager.getNameCourseNextPage(time,object : DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Course>) {
                liveCourseData.value = list
            }
        })
    }

    fun loadAllCourseFromCat(cat:String){
        dbManager.getAllCourseFromCatFirstPage(cat,object : DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Course>) {
                liveCourseData.value?.clear()
                liveCourseData.value = list
            }
        })
    }
    fun loadAllCourseFromCatNextPage(lastCatTime:String){
        dbManager.getAllCourseFromCatNextPage(lastCatTime,object : DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Course>) {
                liveCourseData.value = list
            }
        })
    }

    fun loadMyCourse(){
        dbManager.getMyCourse(object : DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Course>) {
                liveCourseData.value = list
            }
        })
    }

    fun loadModule(course: Course){
        dbManager.getModule(course,object :DbManager.ReadDataModuleCallback{
            override fun readDataModule(list: ArrayList<Module>) {
                liveModuleData.value = list
            }
        })
    }
    fun loadCurrentAllModule(){
        dbManager.loadCurrentAllModule(object :DbManager.ReadDataModuleCallback{
            override fun readDataModule(list: ArrayList<Module>) {
                liveModuleData.value = list
            }
        })
    }
    fun loadModulePractice(course: Course){
        dbManager.getPractice(course,object :DbManager.ReadDataPracticeCallback{
            override fun readDataPractice(list: ArrayList<Practice>) {
                livePracticeData.value = list
            }

        })
    }
    fun loadFeedback(course: Course){
        dbManager.getFeedback(course,object :DbManager.ReadDataFeedbackCallback{
            override fun readDataFeedback(list: ArrayList<Feedback>) {
                liveFeedbackData.value = list
            }


        })
    }

    fun loadLikeCourse(){
        dbManager.getLikeCourse(object : DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Course>) {
                liveCourseData.value = list
            }
        })
    }
    fun loadMyStudyCourse(){
        dbManager.getMyStudyCourse(object : DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Course>) {
                liveCourseData.value = list
            }
        })
    }

    fun deleteOneCourse(course: Course){
        dbManager.deleteOneCourse(course,object :DbManager.FinishWorkListener{
            override fun onFinish() {
                val updatedList = liveCourseData.value
                updatedList?.remove(course)
                liveCourseData.postValue(updatedList!!)
            }

        })
    }

    fun courseViewed(course: Course){
        dbManager.courseViews(course)
    }
    fun onLikeClick(course: Course){
        dbManager.onLikeClick(course,object :DbManager.FinishWorkListener{
            override fun onFinish() {
                val updatedList = liveCourseData.value
                val pos = updatedList?.indexOf(course)
                if(pos != -1){
                    pos?.let{
                        val likeCounter = if(course.isLike) course.likeCounter.toInt() -1 else course.likeCounter.toInt() +1
                        updatedList[pos] = updatedList[pos].copy(isLike = !course.isLike, likeCounter = likeCounter.toString())
                    }
                }
                liveCourseData.postValue(updatedList!!)
            }

        })
    }
    fun onMyStudyClick(course: Course){
        dbManager.onMyStudyClick(course,object :DbManager.FinishWorkListener{
            override fun onFinish() {
                val updatedList = liveCourseData.value
                val pos = updatedList?.indexOf(course)
                if(pos != -1){
                    pos?.let{
                        updatedList[pos] = updatedList[pos].copy(isStudy = !course.isStudy)
                    }
                }
                liveCourseData.postValue(updatedList!!)
            }

        })
    }


    fun loadInfoAccount(){
        dbManager.getMyAccount(object : DbManager.ReadDataAccountCallback{
            override fun readDataAccount(list: ArrayList<Account>) {
                liveAccountData.value = list

            }
        })
    }
}