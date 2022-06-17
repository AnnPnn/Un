package com.anpln.uniboard.act


import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anpln.uniboard.MainActivity
import com.anpln.uniboard.R
import com.anpln.uniboard.adapters.ImageAdapter
import com.anpln.uniboard.model.Ad
import com.anpln.uniboard.model.DbManager
import com.anpln.uniboard.databinding.ActivityEditAdsBinding
import com.anpln.uniboard.dialogs.DialogSpinnerHelper
import com.anpln.uniboard.frag.FragmentCloseInterface
import com.anpln.uniboard.frag.ImageListFrag
import com.anpln.uniboard.utils.DisciplineHelper
import com.anpln.uniboard.utils.ImagePicker
import com.google.android.gms.tasks.OnCompleteListener
import java.io.ByteArrayOutputStream


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    var chooseImageFrag: ImageListFrag? = null
    lateinit var rootElement: ActivityEditAdsBinding
    private var dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    private val dbManager = DbManager()
    var editImagePos = 0
    private var isEditState = false
    private var ad: Ad? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        init()
        checkEditState()
    }

    private fun checkEditState() {
        isEditState = isEditState()
        if (isEditState) {
            ad = intent.getSerializableExtra(MainActivity.ADS_DATA) as Ad
            if (ad != null) fillViews(ad!!)
        }
    }

    private fun isEditState(): Boolean {
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    //функция для проверки состояния (проверяет создание нового или редактирование уже готового объявления)
    private fun fillViews(ad: Ad) = with(rootElement) {
        tvFaculty.text = ad.faculty
        tvDiscipline.text = ad.discipline
        edTel.setText(ad.tel)
        edInd.setText(ad.index)
        checkBox.isChecked = ad.withSent.toBoolean()
        tvCat.text = ad.category
        edTitle.setText(ad.title)
        edPrice.setText(ad.price)
        edDescription.setText(ad.description)

    }//редактирование уже имеющихся объявлений



    private fun init() {
        imageAdapter = ImageAdapter()
        rootElement.vpImages.adapter = imageAdapter


    }

    //OnClicks
    fun onClickSelectFaculty(view: View) {
        val listFaculty = DisciplineHelper.getAllFaculty(this)
        dialog.showSpinnerDialog(this, listFaculty, rootElement.tvFaculty)
        //слушатель нажатий
        if (rootElement.tvDiscipline.text.toString() != getString(R.string.select_discipline)) {
            rootElement.tvDiscipline.text = getString(R.string.select_discipline)
            //возвращаем значение поля по умолчанию
        }
    }

    fun onClickSelectDiscipline(view: View) {
        val selectedFaculty = rootElement.tvFaculty.text.toString()
        if (selectedFaculty != getString(R.string.select_faculty)) {
            val listDiscipline = DisciplineHelper.getAllDiscipline(selectedFaculty, this)
            dialog.showSpinnerDialog(this, listDiscipline, rootElement.tvDiscipline)
            //слушатель нажатий
        } else {
            Toast.makeText(this, "No Faculty selected", Toast.LENGTH_LONG).show()
        }
    }

    fun onClickSelectCat(view: View) {

        val listCategory = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialog.showSpinnerDialog(this, listCategory, rootElement.tvCat)
        //слушатель нажатий

    }

    fun onClickGetImages(view: View) {

        if (imageAdapter.mainArray.size == 0) {
            ImagePicker.getMultiImages(this, 3)
        } else {
            openChooseImageFrag(null)
            chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
        }

    }//слушатель для открытия фрагмента с изображениями


    fun onClickPublish(view: View) {
        val adTemp = fillAd()
        if (isEditState) {
            dbManager.publishAd(adTemp.copy(key = ad?.key),
               onPublishFinish()
            )//копируем все значения, внося изменения в key и uid
        } else {
            dbManager.publishAd(adTemp, onPublishFinish())
                //uploadImages(adTemp)
        }

    }//слушатель для кнопки публикации


    private fun onPublishFinish(): DbManager.FinishWorkListener {
        return object : DbManager.FinishWorkListener {
            override fun onFinish() {
                finish()
            }

        }
    }



    private fun fillAd(): Ad {
        val ad: Ad
        rootElement.apply {
            ad = Ad(
                tvFaculty.text.toString(),
                tvDiscipline.text.toString(),
                edTel.text.toString(),
                edInd.text.toString(),
                checkBox.isChecked.toString(),
                edTitle.text.toString(),
                tvCat.text.toString(),
                edPrice.text.toString(),
                edDescription.text.toString(),
                "empty",
                dbManager.db.push().key,
                "0",
                dbManager.auth.uid,


            )
        }
        return ad
    }//функция для заполнения класса Ad, возвращает уже заполененное объявление


    override fun onFragClose(list: ArrayList<Bitmap>) {
        rootElement.scroolViewMain.visibility =
            View.VISIBLE//повторное отображение элементов после закрытия фрагмента
        imageAdapter.update(list)
        chooseImageFrag = null

    }

    fun openChooseImageFrag(newList: ArrayList<Uri>?) {
        chooseImageFrag = ImageListFrag(this)
        if (newList != null) chooseImageFrag?.resizeSelectedImages(newList, true, this)
        rootElement.scroolViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFrag!!)
        fm.commit()
    }

    private fun uploadImages(adTemp: Ad){
      val byteArray =  prepareImageByteArray(imageAdapter.mainArray[0])
        uploadImage(byteArray){

            dbManager.publishAd(adTemp.copy(mainImage = it.result.toString()), onPublishFinish())

        }
    }//интерфейс
    private fun prepareImageByteArray(bitMap: Bitmap): ByteArray{
        val outStream = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.JPEG, 20, outStream)
        return outStream.toByteArray()

    }//превращает изображение в массив байтов
    private fun uploadImage(byteArray: ByteArray, listener: OnCompleteListener<Uri>){
        val imStorageRef = dbManager.dbStorage
            .child(dbManager.auth.uid!!)
            .child("image_${System.currentTimeMillis()}")
        val upTask = imStorageRef.putBytes(byteArray)
        upTask.continueWithTask{
            task -> imStorageRef.downloadUrl
        }.addOnCompleteListener(listener)
    }//загрузка, выдача ссылки и показ интерфейса

}
