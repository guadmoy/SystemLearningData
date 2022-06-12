package com.guadmoy.systemlearning.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.guadmoy.systemlearning.databinding.ActivityAboutAppBinding

class AboutApp : AppCompatActivity() {
    lateinit var rootElement: ActivityAboutAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
    }

    fun onClickBack(view: View){
        finish()
    }
}