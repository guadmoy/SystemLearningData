package com.guadmoy.systemlearning.act


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TabHost
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.adapters.FeedbackRcAdapter
import com.guadmoy.systemlearning.adapters.ModuleViewRcAdapter
import com.guadmoy.systemlearning.adapters.PracticeRcAdapter
import com.guadmoy.systemlearning.adapters.PracticeViewRcAdapter
import com.guadmoy.systemlearning.databinding.ActivityDescriptionCourseBinding
import com.guadmoy.systemlearning.model.Account
import com.guadmoy.systemlearning.model.Course
import com.guadmoy.systemlearning.model.DbManager
import com.guadmoy.systemlearning.model.Feedback
import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_description_course.*


class DescriptionCourse : AppCompatActivity() {
    lateinit var binding: ActivityDescriptionCourseBinding
    private var account: Account? = null
    var coursetemp: Course? = null
    private val dbManager = DbManager()
    var accountArray = ArrayList<Account>()
    var feedbackArray = ArrayList<Feedback>()
    private val fireBaseViewModel: FirebaseViewModel by viewModels()
    private var videoUrl = ""
    lateinit var youTubePlayerView: YouTubePlayerView
    lateinit var youTubePlayerMain: YouTubePlayer
    private val moduleRcAdapter = ModuleViewRcAdapter(this)
    private val feedbackRcAdapter = FeedbackRcAdapter(this)
    private val practiceRcAdapter = PracticeViewRcAdapter(this)
    val mAuth = Firebase.auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initYoutube()
        initTabsHost(0)
        initViewsInfo()
        fireBaseViewModel.loadInfoAccount()
        init()
    }

    override fun onResume() {
        super.onResume()
        coursetemp?.let { fireBaseViewModel.loadFeedback(it) }
        initViewsInfo()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        getIntentFromCourseAct()
        updateAdapter()

    }

    private fun initYoutube() {
        youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youtube_player_view)
        lifecycle.addObserver(youTubePlayerView);

         youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(videoUrl, 0f)
                youTubePlayerMain = youTubePlayer
            }
        })
    }

    fun pauseYoutube() {
        youTubePlayerMain.pause()
    }

    fun initViewsInfo() {
        fireBaseViewModel.liveAccountData.observe(this) {
            accountArray.clear()
            accountArray.addAll(it)
        }
        fireBaseViewModel.liveModuleData.observe(this) {
            moduleRcAdapter.updateAdapterWithClear(it)
        }
        fireBaseViewModel.livePracticeData.observe(this) {
            practiceRcAdapter.updateAdapterWithClear(it)
        }
        fireBaseViewModel.liveFeedbackData.observe(this) {
            feedbackRcAdapter.updateAdapterWithClear(it)
            feedbackArray.clear()
            feedbackArray.addAll(it)

        }

    }

    private fun updateAdapter() = with(binding) {

        recViewDesModule.layoutManager = LinearLayoutManager(this@DescriptionCourse)
        recViewDesModule.adapter = moduleRcAdapter

        recViewFeedback.layoutManager = LinearLayoutManager(this@DescriptionCourse)
        recViewFeedback.adapter = feedbackRcAdapter

        recViewDesPractice.layoutManager = LinearLayoutManager(this@DescriptionCourse)
        recViewDesPractice.adapter = practiceRcAdapter
    }

    private fun initTabsHost(index: Int) {
        binding.tabHostDesCourse.setup()

        var tabSpec: TabHost.TabSpec = binding.tabHostDesCourse.newTabSpec("Des")
        tabSpec.setContent(R.id.Des)
        tabSpec.setIndicator("Курс")
        binding.tabHostDesCourse.addTab(tabSpec)

        tabSpec = binding.tabHostDesCourse.newTabSpec("Mes")
        tabSpec.setContent(R.id.Mes)
        tabSpec.setIndicator("Отзывы")
        binding.tabHostDesCourse.addTab(tabSpec)

        tabSpec = binding.tabHostDesCourse.newTabSpec("Mod")
        tabSpec.setContent(R.id.Mod)
        tabSpec.setIndicator("Модули")
        binding.tabHostDesCourse.addTab(tabSpec)

        tabSpec = binding.tabHostDesCourse.newTabSpec("Practic")
        tabSpec.setContent(R.id.Practic)
        tabSpec.setIndicator("Тесты")
        binding.tabHostDesCourse.addTab(tabSpec)

        binding.tabHostDesCourse.setCurrentTab(index)

        binding.tabHostDesCourse.setOnTabChangedListener {
            if(it.equals("Mes") || it.equals("Mod"))  pauseYoutube()
        }

    }


    private fun getIntentFromCourseAct() {
        val course = intent.getSerializableExtra("Course") as Course
        updateUI(course)
    }

    fun updateUI(course: Course) {
        fillTextCourseDescription(course)
        course.let { fireBaseViewModel.loadModule(it) }
        course.let { fireBaseViewModel.loadModulePractice(it) }
        course.let { fireBaseViewModel.loadFeedback(it) }
        coursetemp = course
    }

    fun onClickAddFeedback(view: View) {
        if (accountArray[0].uid == null) {
            Toast.makeText(this, "Сохраните профиль в настройках!", Toast.LENGTH_LONG).show()
        } else {
            val findFeedback = feedbackArray.find { it -> it.uid == accountArray[0].uid }
            if (findFeedback != null) {
                val editIntent = Intent(this, EditFeedbackActivity::class.java)
                editIntent.putExtra(MainActivity.EDIT_STATE, true)
                editIntent.putExtra("Feedback", findFeedback)
                editIntent.putExtra("Account", accountArray[0])
                this.startActivity(editIntent)
                return
            } else {
                if (coursetemp?.key != null) startActivity(
                    Intent(
                        this,
                        EditFeedbackActivity::class.java
                    ).apply {
                        putExtra("CourseKey", coursetemp)
                        putExtra("Account", accountArray[0])
                    })
            }
        }
    }

    fun onClickBack(view: View) {
        finish()
    }

    fun onclickChat(view: View){
        val i = Intent(this,GroupMessageActivity::class.java)
        i.putExtra("Course",coursetemp)
        this.startActivity(i)
    }

    @SuppressLint("SetTextI18n")
    private fun fillTextCourseDescription(course: Course) = with(binding) {
        tvTitleDCourse.text = course.name
        Picasso.get().load(course.mainImage).into(ivDCourse)
        tvPeopleCount.text = course.viewsCounter
        tvRaitingCount.text = course.ratingCounter
        tvLikeCounter.text = course.likeCounter
        btPrice.text = if (!course.isStudy) "Купить за " + course.price + " ₽" else "Куплено"
        btPrice.setOnClickListener {
            buyCourse(course)
        }
        videoUrl = course.uriVideoCourse!!.split("/").last()
        tvDCourseDescription.text = course.description
        tvDCourseAboutCourse.text = course.about
        tvDCourseRequest.text = course.request
        tvDCourseAudit.text = course.audit
        tvDCourseTime.text = course.time + " часов"
        tvTeacherName.text = course.teacherName
        tvTeacherDescription.text = course.teacherDescription
        Picasso.get().load(course.teacherPhoto).into(ivTeacherAvatar)
        btLangue.text = course.unever!!.split("-")[0]
        btCategory.text = course.category
        btSertificat.visibility = if (course.certificate) View.VISIBLE else View.GONE
        tvDCourseCounter.text = course.countStudent.toString()

    }

    private fun fillProfile(): Account {
        val account: Account
        binding.apply {
            account = Account(
                accountArray[0].name,
                accountArray[0].email,
                accountArray[0].description,
                accountArray[0].avatarImage,
                accountArray[0].coin,
                accountArray[0].status,
                accountArray[0].uid,
            )
        }
        return account
    }

    private fun buyCourse(course: Course) {
        if (course.price?.toInt()!! < accountArray[0].coin.toInt()) {
            btPrice.text = "Куплено"
            fireBaseViewModel.onMyStudyClick(course)
            accountArray[0].coin = (accountArray[0].coin.toInt() - course.price.toInt()).toString()
            account = fillProfile()
            val countStudent = course.countStudent!! + 1
            dbManager.publishProfile(account!!.copy(uid = account?.uid,uidCurrentCourse = course.key), onPublishFinish())
            dbManager.publishCourse(course.copy(uid = course.uid,countStudent = countStudent), onPublishFinish())
            dbManager.publishCourseProgress(course, onPublishFinish())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayerView.release()
    }


    private fun onPublishFinish(): DbManager.FinishWorkListener {
        return object : DbManager.FinishWorkListener {
            override fun onFinish() {
                //  finish()
            }
        }
    }

}

