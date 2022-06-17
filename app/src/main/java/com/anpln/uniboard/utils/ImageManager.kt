package com.anpln.uniboard.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

import java.io.File
import java.io.InputStream
import java.lang.Error

object ImageManager {

    private const val MAX_IMAGE_SIZE = 1000 //максимально допустимый размер изображения
    private const val WIDTH = 0
    private const val HEIGHT = 1

    fun getImageSize(uri: Uri, act: Activity): List<Int> {
        val inStream = act.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true // указываем края изображения
        }
        BitmapFactory.decodeStream(inStream, null, options)
        return listOf(options.outWidth, options.outHeight)
    }//проверка по высоте и ширине


    fun chooseScaleType(im: ImageView, bitMap: Bitmap) {
        if (bitMap.width > bitMap.height) {
            im.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            im.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }//размещение изображения на весь фрагмент


    suspend fun imageResize(uris: ArrayList<Uri>, act: Activity): List<Bitmap> = withContext(Dispatchers.IO) {
        val tempList = ArrayList<List<Int>>()//массив для храниения высоты и ширины изображения
        val bitmapList = ArrayList<Bitmap>()
        for (n in uris.indices) {
            val size = getImageSize(uris[n], act)
            val imageRatio =
                size[WIDTH].toFloat() / size[HEIGHT].toFloat()//вычисление пропорции изображения
            if (imageRatio > 1) {
                // > 1 - горизонтальная
                if (size[WIDTH] > MAX_IMAGE_SIZE) {
                    tempList.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / imageRatio).toInt()))
                } else {
                    tempList.add(listOf(size[WIDTH], size[HEIGHT]))
                }
            } else {
                if (size[HEIGHT] > MAX_IMAGE_SIZE) {
                    tempList.add(listOf((MAX_IMAGE_SIZE * imageRatio).toInt(), MAX_IMAGE_SIZE))
                } else {
                    tempList.add(listOf(size[WIDTH], size[HEIGHT]))
                }
            }

        }
        for (i in uris.indices) {
            val e = kotlin.runCatching {
                bitmapList.add(
                    Picasso.get().load(uris[i])
                        .resize(tempList[i][WIDTH], tempList[i][HEIGHT]).get()
                )
                //подключение библиотеки Picasso
            }
            Log.d("MyLog", "Bitmap load done: ${e.isSuccess}")//предупреждение о сбоях в работе
        }
        return@withContext bitmapList
    }//функция сжатия изоражения


}