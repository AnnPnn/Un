package com.anpln.uniboard.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anpln.uniboard.MainActivity
import com.anpln.uniboard.R
import com.anpln.uniboard.act.EditAdsAct
import com.anpln.uniboard.model.Ad
import com.anpln.uniboard.databinding.AdListItemBinding
import com.google.firebase.auth.FirebaseAuth

class AdsRcAdapter(val act: MainActivity) : RecyclerView.Adapter<AdsRcAdapter.AdHolder>() {
    val adArray = ArrayList<Ad>()//массив для обьявлений


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdHolder(binding, act)
    }//функция для создания view (разметка list_item),шаблон

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adArray[position])
    }//функция выдает ссылку и позицию расположения, заполняет то что было ранее отображено

    override fun getItemCount(): Int {
        return adArray.size
    }//функция следит за размером массива

    fun updateAdapter(newList: List<Ad>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(adArray, newList))
        diffResult.dispatchUpdatesTo(this)//обноление адаптера
        adArray.clear()//отчищаем основной массив
        adArray.addAll(newList)//запись данных в новый лист


    }//функция перезаписывает данные


    class AdHolder(val binding: AdListItemBinding, val act: MainActivity) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(ad: Ad) = with(binding) {
            tvDescription.text = ad.description
            tvPrice.text = ad.price
            tvTitle.text = ad.title
            tvViewCounter.text = ad.viewsCounter//отображение количества просмотров
            tvViewFav.text = ad.favCounter
            if (ad.isFav) {
                ibFav.setImageResource(R.drawable.ic_fav_pressed)
            } else {
                ibFav.setImageResource(R.drawable.ic_fav_normal)
            }
            showEditPanel(isOwner(ad))
            ibFav.setOnClickListener {
               if(act.mAuth.currentUser?.isAnonymous == false) act.onFavClicked(ad)
            }
            itemView.setOnClickListener {
                act.onAdViewed(ad)
            }//весь элемент отображающий объявление на экране
            ibEditAd.setOnClickListener(onClickEdit(ad))
            ibDeleteAd.setOnClickListener {
                act.onDeleteItem(ad)
            }
        }

        private fun onClickEdit(ad: Ad): View.OnClickListener {
            return View.OnClickListener {
                val editIntent = Intent(act, EditAdsAct::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.ADS_DATA, ad)
                }
                act.startActivity(editIntent)
            }
        }//прослушиватель панели редактирования

        private fun isOwner(ad: Ad): Boolean {
            return ad.uid == act.mAuth.uid
        }//функция для сверки uid пользователя и uid объявления

        private fun showEditPanel(isOwner: Boolean) {
            if (isOwner) {
                binding.editPanel.visibility = View.VISIBLE
            } else {
                binding.editPanel.visibility = View.GONE
            }
        }
        //функция для отображения панели редактирования

    }//класс для одинарного объявления


    interface Listener {
        fun onDeleteItem(ad: Ad)//удаление
        fun onAdViewed(ad: Ad)//просмотр объявления
        fun onFavClicked(ad: Ad)//добавление в избранное
    }
}
//заполнение шаблона list_item
//настледуется от RecyclerView класса