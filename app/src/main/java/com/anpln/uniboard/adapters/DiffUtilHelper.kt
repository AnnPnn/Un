package com.anpln.uniboard.adapters

import androidx.recyclerview.widget.DiffUtil
import com.anpln.uniboard.model.Ad

class DiffUtilHelper(val oldList: List<Ad>, val newList: List<Ad>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }//размер старого списка

    override fun getNewListSize(): Int {
        return newList.size
    }//размер нового списка

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }//сравнение элементов объявлений по ключу

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }//сравнение всех элементов объявлений
}//класс необходимый для сохранения анимации при удалении