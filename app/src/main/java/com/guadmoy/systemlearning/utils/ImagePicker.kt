package com.guadmoy.systemlearning.utils

import android.app.Activity

import android.graphics.Bitmap
import android.net.Uri
import androidx.fragment.app.Fragment
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.act.EditKyrsAct
import com.guadmoy.systemlearning.dialoghelper.ProgressDialog
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    fun getOption(imageCounter: Int): Options {
        val options = Options().apply {
            ratio = Ratio.RATIO_AUTO
            count = imageCounter
            isFrontFacing = false
            mode = Mode.Picture
        }
        return options
    }

    fun addImage(edAct: EditKyrsAct, imageCounter: Int) {
        val frag = edAct.chooseImageFrag
        edAct.addPixToActivity(R.id.place_holder, getOption(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    edAct.chooseImageFrag = frag
                    openChooseImageFrag(edAct, frag!!)
                    edAct.chooseImageFrag?.updateAdapter(result.data as ArrayList<Uri>, edAct)
                }
            }
        }
    }

    fun getMultiSelectImage(edAct: EditKyrsAct, imageCounter: Int) {
        edAct.addPixToActivity(R.id.place_holder, getOption(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    multiSelectImage(edAct, result.data)
                }
            }
        }
    }

    fun getSingleSelectImage(edAct: EditKyrsAct) {
        val frag = edAct.chooseImageFrag
        edAct.addPixToActivity(R.id.place_holder, getOption(1)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    edAct.chooseImageFrag = frag
                    openChooseImageFrag(edAct, frag!!)
                    singleImage(edAct, result.data[0])
                }
            }
        }
    }

    private fun openChooseImageFrag(edAct: EditKyrsAct, fragment: Fragment) {
        edAct.supportFragmentManager.beginTransaction().replace(R.id.place_holder, fragment)
            .commit()
    }


    private fun closePixFragment(edAct: EditKyrsAct) {
        val fList = edAct.supportFragmentManager.fragments
        fList.forEach {
            if (it.isVisible) edAct.supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    private fun multiSelectImage(edAct: EditKyrsAct, uris: List<Uri>) {

        if (uris.size > 1 && edAct.chooseImageFrag == null) {
            edAct.openChooseFragment(uris as ArrayList<Uri>)
        } else if (uris.size == 1 && edAct.chooseImageFrag == null) {
            CoroutineScope(Dispatchers.Main).launch {
                val dialogProgress = ProgressDialog.createProgressDialog(edAct as Activity)
                val biMapArray = ImageManager.imageResize(uris, edAct) as ArrayList<Bitmap>
                dialogProgress.dismiss()
                edAct.imageAdapter.update(biMapArray)
                closePixFragment(edAct)
            }
        }
    }

    private fun singleImage(edAct: EditKyrsAct, uri: Uri) {
        edAct.chooseImageFrag?.setSingleImage(uri, edAct.editImagePos)
    }

}