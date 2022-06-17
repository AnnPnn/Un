package com.anpln.uniboard.frag

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.anpln.uniboard.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

open class BaseAdsFrag : Fragment(), InterAdsClose {
    lateinit var adView: AdView
    var interAd: InterstitialAd? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAds()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadInterAd()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }//перезапуск рекламы

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    //остановка рекламы
    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }//прерывание рекламы
    //жизненный цикл Activity

    private fun initAds() {
        MobileAds.initialize(activity as Activity)
        val adRequest = AdRequest.Builder().build()//запрос на размешение рекламы
        adView.loadAd(adRequest)//загрузка рекламы

    }


    private fun loadInterAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context as Activity,
            getString(R.string.ad_inter_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {

                    interAd = ad

                }
            })


    }

    fun showInterAd() {

        if (interAd != null) {
            interAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onClose()

                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    onClose()
                }

            }//показ рекламы во весь экран

            interAd?.show(activity as Activity)
        } else {
            onClose()
        }
    }

    override fun onClose() {

    }
}


//размешение рекламы
