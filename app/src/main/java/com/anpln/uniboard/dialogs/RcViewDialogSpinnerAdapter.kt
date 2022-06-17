package com.anpln.uniboard.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anpln.uniboard.R
import com.anpln.uniboard.act.EditAdsAct
import kotlin.collections.ArrayList

class RcViewDialogSpinnerAdapter(var tvSelection: TextView, var dialog: AlertDialog) :
    RecyclerView.Adapter<RcViewDialogSpinnerAdapter.SpViewHolder>() {
    private val mainList = ArrayList<String>()//массив для элементов

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sp_list_item, parent, false)
        return SpViewHolder(view, tvSelection, dialog)
    } //метод адаптера, отображение элемента на экране

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainList[position])
    } //метод адаптера, подключение текста к элементам

    override fun getItemCount(): Int {
        return mainList.size
    } //метод адаптера, количество элементов


    class SpViewHolder(itemView: View, var tvSelection: TextView, var dialog: AlertDialog) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var itemText = ""
        fun setData(text: String) {
            val tvSpItem = itemView.findViewById<TextView>(R.id.tvSpItem)
            tvSpItem.text = text
            itemText = text
            itemView.setOnClickListener(this)
        } //обновление и заполенние текстового элемента экрана

        override fun onClick(v: View?) {
            tvSelection.text = itemText
            dialog.dismiss()
        }

    }

    fun updateAdapter(list: ArrayList<String>) {
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }
    //функция для заполнения массива и обновления адаптера
}