package com.guadmoy.systemlearning

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.guadmoy.systemlearning.act.*
import com.guadmoy.systemlearning.adapters.CategoryRcAdapter
import com.guadmoy.systemlearning.adapters.CoursesRcAdapter
import com.guadmoy.systemlearning.dialoghelper.DialogSpinnerHelper
import com.guadmoy.systemlearning.dialoghelper.ProgressDialog
import com.guadmoy.systemlearning.model.Account
import com.guadmoy.systemlearning.model.Course
import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.main_content.*


class MainActivity : AppCompatActivity(),
    CoursesRcAdapter.CourseListener {
    lateinit var googleSignInLan: ActivityResultLauncher<Intent>
    private val dialog = DialogSpinnerHelper()
    private val categoryRcAdapter = CategoryRcAdapter(this)
    var accountArray = ArrayList<Account>()

    val mAuth = Firebase.auth
    private val coursesRcAdapter = CoursesRcAdapter(this)
    private val fireBaseViewModel: FirebaseViewModel by viewModels()
    private var clearUpdate: Boolean = true
    var currentCategory: String? = null
    var nameCourse: String? = null
    var dProgress: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initRecyclerView()
        initViewModel()
        bottomMenuOnClick()
        fireBaseViewModel.loadAllCourseFirstPage()
        scrollListener()

    }

    override fun onResume() {
        super.onResume()
        bNavView.selectedItemId = R.id.id_recomendation
    }

    override fun onStart() {
        super.onStart()
        initViewsInfo()
        CardViewCategory.visibility = View.GONE
        fireBaseViewModel.loadInfoAccount()

        when (accountArray.size) {
            0 -> {
                dProgress = ProgressDialog.createProgressDialog(this as Activity)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("SetTextI18n")
    private fun initCurrentCourse(account: Account) {
        if (account.uidCurrentCourse != null) {
            fireBaseViewModel.loadCurrentCourse(account.uidCurrentCourse!!)
            fireBaseViewModel.liveCurrentCourseData.observe(this) {
                if (it.size != 0) {
                    val rnds = (0 until it.size).random() // generated random from 0 to 10 included
                    val courseCurrent = it[rnds]
                    if (courseCurrent.countModulePractice!! > 0 && courseCurrent.countModuleLecture!! > 0) {
                        textView19.text =
                            "${courseCurrent.countModuleLectureDone!! / courseCurrent.countModuleLecture!! * 100} " + "%"
                        textView21.text =
                            "${courseCurrent.countModulePracticeDone!! / courseCurrent.countModulePractice!! * 100} " + "%"
                        progressBar2.progress =
                            (((courseCurrent.countModuleLectureDone!! / courseCurrent.countModuleLecture!! * 100)
                                    + (courseCurrent.countModulePracticeDone!! / courseCurrent.countModulePractice!! * 100)) / 2).toInt()
                    }
                    textView16.text = courseCurrent.name
                    if (courseCurrent.mainImage != null)
                        Picasso.get().load(courseCurrent.mainImage).into(imageView2)
                    cardCurrentCourses.setOnClickListener {
                        val i = Intent(this, DescriptionCourse::class.java)
                        i.putExtra("Course", courseCurrent)
                        this.startActivity(i)
                    }
                }
            }
        }

    }


    private fun initViewModel() {
        fireBaseViewModel.liveCourseData.observe(this) {
            val list = courseByCategory(it)
            if (!clearUpdate) {
                coursesRcAdapter.updateAdapter(list)
            } else coursesRcAdapter.updateAdapterWithClear(list)
            tvEmpety.visibility = if (coursesRcAdapter.itemCount == 0) View.VISIBLE else View.GONE
        }
    }

    fun initViewsInfo() {
        fireBaseViewModel.liveAccountData.observe(this) {
            accountArray.clear()
            accountArray.addAll(it)
            if (accountArray.isNotEmpty()) {
                uiUpdate(accountArray[0])
                initCurrentCourse(accountArray[0])
                dProgress?.dismiss()
            } else uiUpdateFirst()
        }
    }

    private fun courseByCategory(list: ArrayList<Course>): ArrayList<Course> {
        val tempList = ArrayList<Course>()
        if (currentCategory != getString(R.string.recomended)) {
            list.forEach {
                if (currentCategory == it.category) tempList.add(it)
            }
        } else {
            tempList.addAll(list)
        }
        tempList.reverse()
        return tempList
    }

    private fun init() {
        getCategory()
        currentCategory = getString(R.string.recomended)
    }

    private fun initRecyclerView() {
        recViewMain.layoutManager = LinearLayoutManager(this@MainActivity)
        recViewMain.adapter = coursesRcAdapter
        recViewCategory.layoutManager = LinearLayoutManager(this@MainActivity)
        recViewCategory.adapter = categoryRcAdapter
    }

    private fun bottomMenuOnClick() {
        bNavView.setOnNavigationItemSelectedListener() { item ->
            clearUpdate = true
            when (item.itemId) {
                R.id.id_recomendation -> {

                    //блок автара
                    ivAvatar.visibility = View.VISIBLE
                    tvNameProfile.visibility = View.VISIBLE
                    tvWelcome.visibility = View.VISIBLE
                    ibBell.visibility = View.VISIBLE

                    //блок профиля
                    ibAvatarProfile.visibility = View.GONE
                    tvProfileSurname.visibility = View.GONE
                    tvEmailProfile.visibility = View.GONE
                    settingsProfile.visibility = View.GONE


                    setMargins(mainCard, 0, 500, 0, 0)
                    currentCategory = getString(R.string.recomended)
                    cardSearch.visibility = View.GONE
                    cardFilter.visibility = View.GONE
                    cardCurrentCourses.visibility = View.VISIBLE
                    tvTitile.visibility = View.VISIBLE
                    tvTitile.text = "Рекомендации"
                    fireBaseViewModel.loadAllCourseFirstPage()
                    recViewMain.visibility = View.VISIBLE

                }
                R.id.id_search_course -> {

                    setMargins(mainCard, 0, 300, 0, 0)

                    cardSearch.visibility = View.VISIBLE
                    cardFilter.visibility = View.VISIBLE  //Кнопка Множество фильтров
                    tvTitile.visibility = View.GONE
                    cardCurrentCourses.visibility = View.GONE
                    recViewMain.visibility = View.VISIBLE


                    //блок автара
                    ivAvatar.visibility = View.GONE
                    tvNameProfile.visibility = View.GONE
                    tvWelcome.visibility = View.GONE
                    ibBell.visibility = View.GONE

                    //блок профиля
                    ibAvatarProfile.visibility = View.GONE
                    tvProfileSurname.visibility = View.GONE
                    tvEmailProfile.visibility = View.GONE
                    settingsProfile.visibility = View.GONE

                    ibSearcLocal.setOnClickListener {
                        if (tvSearchCourse.text.isEmpty()) {
                            fireBaseViewModel.loadAllCourseFirstPage()
                            cardFilter.visibility = View.GONE
                        } else {
                            onClickNameCourse()
                            cardFilter.visibility = View.GONE
                        }
                    }
                    btFilter.setOnClickListener {
                        startActivity(Intent(this@MainActivity, FilterActivity::class.java))
                    }
                }
                R.id.id_my_user -> {


                    //блок автара
                    ivAvatar.visibility = View.GONE
                    tvNameProfile.visibility = View.GONE
                    tvWelcome.visibility = View.GONE
                    ibBell.visibility = View.GONE

                    //блок профиля
                    ibAvatarProfile.visibility = View.VISIBLE
                    tvProfileSurname.visibility = View.VISIBLE
                    tvEmailProfile.visibility = View.VISIBLE
                    settingsProfile.visibility = View.VISIBLE

                    ibExitProfile.setOnClickListener {
                        mAuth.signOut()
                        val i = Intent(this, LoginAct::class.java)
                        startActivity(i)
                    }

                    editAccount.setOnClickListener {
                        val i = Intent(this, MyProfile::class.java)
                        startActivity(i)
                    }

                    createCourse.setOnClickListener {
                        if (accountArray[0].status == "teacher") {
                            val i = Intent(this, EditKyrsAct::class.java)
                            startActivity(i)
                        } else {
                            Toast.makeText(
                                this,
                                this.resources.getString(R.string.not_teacher),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    aboutApp.setOnClickListener {
                        val i = Intent(this, AboutApp::class.java)
                        startActivity(i)
                    }
                    supportChat.setOnClickListener {
                        val intent= Intent()
                        intent.action=Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_TEXT,"Написать письмо в поддержку:")
                        intent.type="text/plain"
                        startActivity(Intent.createChooser(intent,"Откройте приложении почты:"))
                    }
                    shareApp.setOnClickListener {
                        val intent= Intent()
                        intent.action=Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_TEXT,"Спасибо, что хотите поделится этим прекрсным приложением:")
                        intent.type="text/plain"
                        startActivity(Intent.createChooser(intent,"Отправить как:"))
                    }


                    recViewMain.visibility = View.GONE
                    tvTitile.visibility = View.GONE
                    cardSearch.visibility = View.GONE
                    cardFilter.visibility = View.GONE
                    cardCurrentCourses.visibility = View.GONE
                    setMargins(mainCard, 0, 800, 0, 0)

                }
                R.id.id_my_favorite_course -> {
                    cardSearch.visibility = View.GONE
                    cardFilter.visibility = View.GONE
                    recViewMain.visibility = View.VISIBLE

                    //блок автара
                    ivAvatar.visibility = View.VISIBLE
                    tvNameProfile.visibility = View.VISIBLE
                    tvWelcome.visibility = View.VISIBLE
                    ibBell.visibility = View.VISIBLE
                    cardCurrentCourses.visibility = View.GONE
                    tvTitile.visibility = View.VISIBLE
                    tvTitile.text = "Избранные курсы"

                    //блок профиля
                    ibAvatarProfile.visibility = View.GONE
                    tvProfileSurname.visibility = View.GONE
                    tvEmailProfile.visibility = View.GONE
                    settingsProfile.visibility = View.GONE


                    setMargins(mainCard, 0, 300, 0, 0)
                    fireBaseViewModel.loadLikeCourse()
                }
            }
            true
        }
    }

    private fun getCategory() {
        val listCategory = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        categoryRcAdapter.updateAdapter(listCategory)
    }

    fun onClickCat() {
        // currentCategory = tvSearchCourse.text.toString()
        fireBaseViewModel.loadAllCourseFromCat(currentCategory!!)
    }

    fun onClickNameCourse() {
        currentCategory = getString(R.string.recomended)
        nameCourse = tvSearchCourse.text.toString()
        fireBaseViewModel.loadNameCourseFirstPage(nameCourse!!)
    }

    fun onClickSelectCategory(view: View) {
        val listCity = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialog.showSpinnerDialog(this, listCity, tvSearchCourse)
    }

    fun uiUpdate(account: Account) {
        tvNameProfile.text = accountArray[0].name
        tvProfileSurname.text = accountArray[0].name
        tvEmailProfile.text = accountArray[0].email
        Picasso.get().load(accountArray[0].avatarImage).into(ivAvatar)
        Picasso.get().load(accountArray[0].avatarImage).into(ibAvatarProfile)

        ibBell.setOnClickListener {
            if (account.uidCurrentCourse != null) {
                fireBaseViewModel.loadCurrentCourse(account.uidCurrentCourse!!)
                fireBaseViewModel.liveCurrentCourseData.observe(this) {
                    coursesRcAdapter.updateAdapterWithClear(it)
                    setMargins(mainCard, 0, 300, 0, 0)
                    cardCurrentCourses.visibility = View.GONE
                    tvTitile.text = "Купленные курсы"
                }
            }
        }

    ivAvatar.setOnClickListener{
        val i = Intent(this, Profile::class.java)
        i.putExtra("Account", accountArray[0])
        startActivity(i)
    }
    tvNameProfile.setOnClickListener{
        val i = Intent(this, Profile::class.java)
        i.putExtra("Account", accountArray[0])
        startActivity(i)
    }
}

fun uiUpdateFirst() {
    tvNameProfile.text = "Заполните профиль"
}


companion object {
    const val EDIT_STATE = "edit_state"
    const val COURSE_DATA = "course_data"
    const val SCROLL_DOWN = 1
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

var doubleBackToExitPressed = 1

override fun onBackPressed() {
    if (doubleBackToExitPressed == 2) {
        finishAffinity();
        System.exit(0);
    } else {
        doubleBackToExitPressed++;
        Toast.makeText(this, "Please press Back again to exit", Toast.LENGTH_SHORT).show();
    }

    Handler().postDelayed(Runnable() {
        @Override
        fun run() {
            doubleBackToExitPressed = 1;
        }
    }, 2000);
}

private fun scrollListener() {
    recViewMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(SCROLL_DOWN) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                clearUpdate = false
                val courseList = fireBaseViewModel.liveCourseData.value
                if (courseList != null) {
                    if (nameCourse == null) {
                        if (courseList.isNotEmpty()) getCourseFromCat(courseList)
                    } else {
                        getCourseFromName(courseList)
                    }
                }
            }
        }
    })
}

private fun getCourseFromCat(courseList: ArrayList<Course>) {
    courseList[0].let {
        if (currentCategory == getString(R.string.recomended)) {
            fireBaseViewModel.loadAllCourseNextPage(it.timeCreate)
        } else {
            val catTime = "${it.category}_${it.timeCreate}"
            fireBaseViewModel.loadAllCourseFromCatNextPage(catTime)
        }
    }
}

private fun getCourseFromName(courseList: ArrayList<Course>) {
    if (courseList.isNotEmpty())
        courseList[0].let {
            fireBaseViewModel.loadNameCourseNextPage(it.timeCreate)
        }
}

private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
    if (view.layoutParams is MarginLayoutParams) {
        val p = view.layoutParams as MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        view.requestLayout()
    }
}


}
