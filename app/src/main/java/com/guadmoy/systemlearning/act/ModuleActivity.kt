package com.guadmoy.systemlearning.act

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.databinding.ActivityModuleBinding
import com.guadmoy.systemlearning.model.Account
import com.guadmoy.systemlearning.model.Course
import com.guadmoy.systemlearning.model.DbManager
import com.guadmoy.systemlearning.model.Module
import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.awaitAll


class ModuleActivity : AppCompatActivity() {
    lateinit var rootElement: ActivityModuleBinding
    private var module: Module? = null
    private var course: Course? = null
    private var videoUrl = ""
    lateinit var youTubePlayerView: YouTubePlayerView
    private val dbManager = DbManager()
    private val fireBaseViewModel: FirebaseViewModel by viewModels()
    var isDone = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityModuleBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        setModule()
        initAccountModule()
    }

   private fun initAccountModule(){
       fireBaseViewModel.loadCurrentAllModule()
       fireBaseViewModel.liveModuleData.observe(this) {
           isFindModule(it)
       }
    }

    fun isFindModule(moduleArray: ArrayList<Module>) {
        module = intent.getSerializableExtra("MODULE_DATA") as Module
        Log.d("MyLog","Find: ${moduleArray.find {
            it.key == module!!.key
        }}")
        if(moduleArray.find {
            it.key == module!!.key
        } != null) isDone = true
        Log.d("MyLog","isDone: $isDone")


        course = intent.getSerializableExtra("course") as Course
        fillViews(module!!)
    }

    private fun setModule(){

    }

    private fun fillViews(module: Module) = with(rootElement){
        tvTitleThemeModule.text = module.nameTheme
        tvTitleModuleNameMain.text = module.nameTheme
        tvDescriptionVideo.text = module.desVideo
        tvDescriptionTheme.text = module.desTheme
        videoUrl = module.uriVideo!!.split("/").last()
        btDoneModule.visibility = if (isDone) View.GONE else View.VISIBLE
        initYoutube()
    }

    private fun initYoutube(){
        youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youtube_player_view)
        lifecycle.addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(videoUrl, 0f)
            }
        })
    }

    fun onClickBack(view: View) {
        finish()
    }

    fun onClickDownload(view: View){

    }


    fun onCLickDone(view: View){
        dbManager.publishCourseModuleProgress(module!!, onPublishFinish())
        dbManager.publishCourseProgress(course?.copy(key = course!!.key, countModuleLectureDone = course!!.countModuleLectureDone!! +1)!!, onPublishFinish())
    }

    private fun onPublishFinish() : DbManager.FinishWorkListener{
        return object : DbManager.FinishWorkListener{
            override fun onFinish() {
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayerView.release()
    }
}