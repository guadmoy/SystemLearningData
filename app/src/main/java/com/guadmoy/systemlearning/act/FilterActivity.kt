package com.guadmoy.systemlearning.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.databinding.ActivityFilterBinding
import com.guadmoy.systemlearning.dialoghelper.DialogSpinnerHelper
import com.guadmoy.systemlearning.utils.UneverHelper
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity : AppCompatActivity() {
    lateinit var binding: ActivityFilterBinding
    private val dialog = DialogSpinnerHelper()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }




    fun onClickSelectCity(view: View) = with(binding) {
        val listCity = UneverHelper.getAllCity(this@FilterActivity)
        dialog.showSpinnerDialog(this@FilterActivity,listCity,tvCity)
        if(tvUnever.text.toString() != getString(R.string.select_unever)){
            tvUnever.text = getString(R.string.select_unever)
        }
    }
    fun onClickSelectUnever(view: View) = with(binding){
        val selectCity = tvCity.text.toString()
        if(selectCity == getString(R.string.select_city)){
            Toast.makeText(this@FilterActivity, "Сначала выберите город!", Toast.LENGTH_LONG).show()
        }else{
            val listUnever = UneverHelper.getAllUnever(selectCity,this@FilterActivity)
            dialog.showSpinnerDialog(this@FilterActivity,listUnever,tvUnever)
        }
    }
    fun onClickSelectCategory(view: View)= with(binding){
        val listCity = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialog.showSpinnerDialog(this@FilterActivity,listCity,tvCat)
    }

    fun onClickDone(view: View) = with(binding){
        createFilter()
        finish()

    }

    private fun createFilter():String {
        val sBuilder = StringBuilder()
        val arrayTempFilter = listOf(
            tvCity.text,
            tvUnever.text,
            tvCat.text,
            switchCertiwicat.isChecked.toString(),
            switchCertiwicat2.isChecked.toString()
        )
        for((i,s) in arrayTempFilter.withIndex()){
            if(s != getString(R.string.select_city)
                && s != getString(R.string.select_unever)
                && s != getString(R.string.select_category)){
                sBuilder.append(s)
               if(i != arrayTempFilter.size -1) sBuilder.append("_")

            }
        }
        Toast.makeText(this@FilterActivity, "${sBuilder.toString()}", Toast.LENGTH_LONG).show()
        return sBuilder.toString()

    }
}