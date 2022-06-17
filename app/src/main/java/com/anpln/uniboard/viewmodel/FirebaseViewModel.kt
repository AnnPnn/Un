package com.anpln.uniboard.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anpln.uniboard.model.Ad
import com.anpln.uniboard.model.DbManager

class FirebaseViewModel: ViewModel() {
    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()
//обновляет данные view, следит за новыми и доступными обновлениями, хранит список объявлений
    fun loadAllAds(){
    dbManager.getAllAds(object: DbManager.ReadDataCallback{
        override fun readData(list: ArrayList<Ad>) {
            liveAdsData.value = list
        }

    })

    }
    //функция для передачи данных

    fun onFavClick(ad: Ad){
        dbManager.onFavClick(ad, object: DbManager.FinishWorkListener{
            override fun onFinish() {
                val updatedList = liveAdsData.value
                val pos = updatedList?.indexOf(ad)//информация о нажатом объялвении
                if (pos != -1){
                    pos?.let{
                        val favCounter = if(ad.isFav) ad.favCounter.toInt() - 1 else ad.favCounter.toInt() + 1
                        updatedList[pos] = updatedList[pos].copy(isFav = !ad.isFav, favCounter = favCounter.toString())
                    }

                }
                liveAdsData.postValue(updatedList)
            }
        })
    }

    fun adViewed(ad:Ad){
        dbManager.adViewed(ad)
    }

    fun loadMyAds(){
        dbManager.getMyAds(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }

        })
    }
    fun loadMyFavs(){
        dbManager.getMyFavs(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }

        })}
    fun deleteItem(ad: Ad){
        dbManager.deleteAd(ad, object: DbManager.FinishWorkListener{
            override fun onFinish() {
                val updatedList = liveAdsData.value
                updatedList?.remove(ad)
                liveAdsData.postValue(updatedList)
            }
        })
    }
}