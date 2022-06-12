package com.guadmoy.systemlearning.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


import java.io.File
import java.io.InputStream

object ImageManager {
   private const val MAX_IMAGE_SIZE = 1000
    const val WIDTH =0
    const val HEIGHT =1

    fun getImageSize(uri:Uri,act:Activity) : List<Int>{
        val inStream = act.contentResolver.openInputStream(uri)

        val option = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inStream,null,option)
        return listOf(option.outWidth,option.outHeight)
    }




  suspend  fun imageResize(uris: List<Uri>,act: Activity): List<Bitmap> =  withContext(Dispatchers.IO){
        val tempList = ArrayList<List<Int>>()
        val bitMapList = ArrayList<Bitmap>()
        for (n in uris.indices){
            val size = getImageSize(uris[n], act)
            val imageRatio = size[WIDTH].toFloat() / size[HEIGHT].toFloat()
            if(imageRatio > 1){

                if(size[WIDTH] > MAX_IMAGE_SIZE){
                    tempList.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / imageRatio).toInt()))
                }else tempList.add(listOf(size[WIDTH], size[HEIGHT]))
            }else{
                if(size[HEIGHT] > MAX_IMAGE_SIZE){
                    tempList.add(listOf((MAX_IMAGE_SIZE * imageRatio).toInt(), MAX_IMAGE_SIZE))
                }else tempList.add(listOf(size[WIDTH], size[HEIGHT]))
            }
        }

      for(i in uris.indices){
          bitMapList.add(Picasso.get().load(uris[i]).resize(tempList[i][WIDTH],tempList[i][HEIGHT]).get())
      }
      return@withContext bitMapList
    }


}