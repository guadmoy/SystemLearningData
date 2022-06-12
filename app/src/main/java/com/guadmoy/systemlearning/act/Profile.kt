package com.guadmoy.systemlearning.act


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.adapters.CoursesRcAdapter
import com.guadmoy.systemlearning.adapters.CoursesRcAdapterProfile
import com.guadmoy.systemlearning.databinding.ActivityProfileBinding
import com.guadmoy.systemlearning.model.Account
import com.guadmoy.systemlearning.model.Course

import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.main_content.*


class Profile : AppCompatActivity(),
    CoursesRcAdapterProfile.CourseListener {
    lateinit var binding: ActivityProfileBinding
    private val coursesRcAdapter = CoursesRcAdapterProfile(this)
    private var account: Account? = null
    private val fireBaseViewModel: FirebaseViewModel by viewModels()
    val mAuth = Firebase.auth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setModule()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recViewMyCourse.layoutManager = LinearLayoutManager(this)
        recViewMyCourse.adapter = coursesRcAdapter
    }

    override fun onStart() {
        super.onStart()
        fireBaseViewModel.liveCourseData.observe(this) {
             coursesRcAdapter.updateAdapterWithClear(it)
        }

//        when(accountArray.size){
//            0 -> {  dProgress = ProgressDialog.createProgressDialog(this as Activity)}
//        }
    }



    fun onClickBack(view: View) {
       finish()
    }

    private fun setModule(){
        account = intent.getSerializableExtra("Account") as Account
        fillViews(account!!)
        fireBaseViewModel.loadMyCourse()
    }

    private fun fillViews(account: Account) = with(binding){
        tvNameProfile2.text = account.name
        tvStatusProfile2.text = account.status
        Picasso.get().load(account.avatarImage).into(ivAvatar2)
    }

    override fun onDeleteOnCourse(course: Course) {
        fireBaseViewModel.deleteOneCourse(course)
    }

    override fun onCourseViewed(course: Course) {
        fireBaseViewModel.courseViewed(course)
    }

    override fun onCourseLiked(course: Course) {
        fireBaseViewModel.onLikeClick(course)
    }
}