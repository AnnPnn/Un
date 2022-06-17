package com.anpln.uniboard.frag


import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anpln.uniboard.R
import com.anpln.uniboard.act.EditAdsAct
import com.anpln.uniboard.databinding.SelectImageFragItemBinding
import com.anpln.uniboard.utils.AdapterCallback
import com.anpln.uniboard.utils.ImageManager
import com.anpln.uniboard.utils.ImagePicker
import com.anpln.uniboard.utils.ItemTouchMoveCallback


class SelectImageRvAdapter(val adapterCallback: AdapterCallback) :
    RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(),
    ItemTouchMoveCallback.ItemTouchAdapter {
    val mainArray = ArrayList<Bitmap>()//список для харанения изображений
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val viewBinding =
            SelectImageFragItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ImageHolder(viewBinding, parent.context, this)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
        //получение массива из которого берется разметка для адаптера
    }

    override fun onMove(startPos: Int, targetPos: Int) {
        val targetItem = mainArray[targetPos]
        mainArray[targetPos] = mainArray[startPos]//изменение позиции изображения
        mainArray[startPos] = targetItem//изменение заголовка изображения
        notifyItemMoved(startPos, targetPos)
        //изменение позиции элемента
    }

    override fun onClear() {
        notifyDataSetChanged()
    }

    class ImageHolder(
        private val viewBinding: SelectImageFragItemBinding,
        val context: Context,
        val adapter: SelectImageRvAdapter
    ) : RecyclerView.ViewHolder(viewBinding.root) {


        fun setData(bitMap: Bitmap) {


            viewBinding.imEditImage.setOnClickListener {

                ImagePicker.getSingleImages(context as EditAdsAct)
                //редактирование одного изображения
                context.editImagePos = adapterPosition

            }

            viewBinding.imDelete.setOnClickListener {
                adapter.mainArray.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
                for (n in 0 until adapter.mainArray.size) adapter.notifyItemChanged(n)
                adapter.adapterCallback.onItemDelete()
            }
            viewBinding.tvTitle.text =
                context.resources.getStringArray(R.array.title_array)[adapterPosition]
            ImageManager.chooseScaleType(viewBinding.imageContent, bitMap)
            viewBinding.imageContent.setImageBitmap(bitMap)//заполнение элемента получнными данными
        }

    }

    fun updateAdapter(newList: List<Bitmap>, needClear: Boolean) {
        if (needClear) mainArray.clear()//отчищение массива
        mainArray.addAll(newList)//заполнение массива
        notifyDataSetChanged()
    }//функция для обновления адаптера


}
//адаптер для отображения выбранных изображений на фрагменте для показа списка