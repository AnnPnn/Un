package com.anpln.uniboard.utils



import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build.ID
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anpln.uniboard.R
import com.anpln.uniboard.act.EditAdsAct
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object ImagePicker {
    const val MAX_IMAGE_COUNT = 3//максимальное количество выбранных изображений
    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 998
    private fun getOptions(imageCounter: Int): Options {
        val options = Options().apply {
            count = imageCounter //Количество изображений для восстановления количества выбора
            isFrontFacing = false //Фронтальная камера при запуске
            mode = Mode.Picture //Возможность выбора только изображений или видео или обоих
            path = "/ pix/images"//Пользовательский путь для хранения мультимедиа
        }

        return options

    }

    fun getMultiImages(edAct: EditAdsAct, imageCounter: Int) {
        edAct.addPixToActivity(R.id.place_holder, getOptions(imageCounter)){ result ->
            when (result.status){
                PixEventCallback.Status.SUCCESS -> {
                    getMultiSelectImages(edAct, result.data)

                    }
                }

            }
        }

    fun addImages(edAct: EditAdsAct, imageCounter: Int) {

        edAct.addPixToActivity(R.id.place_holder, getOptions(imageCounter)){ result ->
            when (result.status){
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFrag(edAct)
                   edAct.chooseImageFrag?.updateAdapter(result.data as ArrayList<Uri>,edAct)

                }
            }

        }
    }

    fun getSingleImages(edAct: EditAdsAct) {
        edAct.addPixToActivity(R.id.place_holder, getOptions(1)){ result ->
            when (result.status){
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFrag(edAct)
                    singleImage(edAct, result.data[0])
                }
            }

        }
    }

    private fun openChooseImageFrag(edAct: EditAdsAct){
        edAct.supportFragmentManager.beginTransaction().replace(R.id.place_holder, edAct.chooseImageFrag!!).commit()
    }

    private fun closePixFrag(edAct: EditAdsAct) {
        val fList = edAct.supportFragmentManager.fragments
        fList.forEach {
            if (it.isVisible) edAct.supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    fun getMultiSelectImages(edAct: EditAdsAct, uris: List<Uri>) {

                    if (uris?.size!! > 1 && edAct.chooseImageFrag == null) {
                        edAct.openChooseImageFrag(uris as ArrayList<Uri>)//запуск фрагмента с выбранными изображениями
                    } else if (uris.size == 1 && edAct.chooseImageFrag == null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            edAct.rootElement.pBarLoad.visibility = View.VISIBLE
                            val bitMapArray =
                                ImageManager.imageResize(uris as ArrayList<Uri>, edAct) as ArrayList<Bitmap>
                            edAct.rootElement.pBarLoad.visibility = View.GONE
                            edAct.imageAdapter.update(bitMapArray)
                            closePixFrag(edAct)
                        }
                    }
                }


    //создание Launcher для получения нескольких изображений

    private fun singleImage(edAct: EditAdsAct, uri: Uri){
        edAct.chooseImageFrag?.setSingleImage(uri, edAct.editImagePos)
        }


}


//получение изображений