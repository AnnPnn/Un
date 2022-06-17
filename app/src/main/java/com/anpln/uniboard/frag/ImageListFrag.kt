package com.anpln.uniboard.frag

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.anpln.uniboard.R
import com.anpln.uniboard.act.EditAdsAct
import com.anpln.uniboard.databinding.ListImageFragBinding
import com.anpln.uniboard.dialoghelper.ProgressDialog
import com.anpln.uniboard.utils.AdapterCallback
import com.anpln.uniboard.utils.ImageManager
import com.anpln.uniboard.utils.ImagePicker
import com.anpln.uniboard.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFrag(
    private val fragCloseInterface:
    FragmentCloseInterface) : BaseAdsFrag(), AdapterCallback {
    val adapter = SelectImageRvAdapter(this)
    val dragCallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragCallback)
    private var job: Job? = null
    private var addImageItem: MenuItem? = null
    lateinit var binding: ListImageFragBinding//переменная для получения элементов из стороннего класса

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ListImageFragBinding.inflate(layoutInflater)
        adView = binding.adView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        binding.apply {
            touchHelper.attachToRecyclerView(rcViewSelectImage)
            rcViewSelectImage.layoutManager = LinearLayoutManager(activity)
            rcViewSelectImage.adapter = adapter

        }
    }


    override fun onItemDelete() {
        addImageItem?.isVisible = true
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    override fun onDetach() {
        super.onDetach()

    }//отсоединение от Activity

    override fun onClose() {
        super.onClose()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@ImageListFrag)
            ?.commit()//закрытие фрагмента
        fragCloseInterface.onFragClose(adapter.mainArray)
        job?.cancel()//закрытие второстепенного потока

    }

    fun resizeSelectedImages(newList: ArrayList<Uri>, needClear: Boolean, activity: Activity) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity)//запуск значка загрузки
            val bitmapList = ImageManager.imageResize(newList, activity)//сжатие изображений
            dialog.dismiss()//закрытие диалога
            adapter.updateAdapter(bitmapList, needClear) //обновление адаптера
            if (adapter.mainArray.size > 2) addImageItem?.isVisible =
                false//скрывается отображение кнопки
        }//задачи выполняемые отдельно от основоного потока
    }


    private fun setUpToolbar() {

        binding.apply {
            tb.inflateMenu(R.menu.menu_choose_image)//создание разметки для меню
            val deleteItem = tb.menu.findItem(R.id.id_delete_image)
            addImageItem = tb.menu.findItem(R.id.id_add_image)
            if (adapter.mainArray.size > 2) addImageItem?.isVisible =
                false

            tb.setNavigationOnClickListener {

                showInterAd()
            }

            deleteItem.setOnMenuItemClickListener {
                adapter.updateAdapter(ArrayList(), true)//передаем пустой список
                addImageItem?.isVisible = true//отображение кнопки для добавления фото
                true
            }//слушатель нажатий

            addImageItem?.setOnMenuItemClickListener {
                val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
                ImagePicker.addImages(activity as EditAdsAct, imageCount)
                true
            }//слушатель нажатий
        }
    } //функция для инициализации и настройки toolbar

    fun updateAdapter(newList: ArrayList<Uri>, activity: Activity) {
        resizeSelectedImages(newList, false, activity)
    }

    //функция для обновления адаптера

    fun setSingleImage(uri: Uri, pos: Int) {
        val pBar = binding.rcViewSelectImage[pos].findViewById<ProgressBar>(R.id.pBar)
        job = CoroutineScope(Dispatchers.Main).launch {
            pBar.visibility = View.VISIBLE//отображение загрузки на отдельном изображении
            val bitmapList = ImageManager.imageResize(arrayListOf(uri), activity as Activity)
            pBar.visibility = View.GONE
            adapter.mainArray[pos] = bitmapList[0]
            adapter.notifyItemChanged(pos)

        }

    }


}

//отображение изображений на экране