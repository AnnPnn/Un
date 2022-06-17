package com.anpln.uniboard.model

import io.ak1.pix.databinding.MainImageBinding
import java.io.Serializable

data class Ad(
    val faculty: String? = null,
    val discipline: String? = null,
    val tel: String? = null,
    val index: String? = null,
    val withSent: String? = null,
    val title: String? = null,
    val category: String? = null,
    val price: String? = null,
    val description: String? = null,
    val mainImage: String? = null,
    val key: String? = null,
    var favCounter: String = "0",
    val uid: String? = null,//идентификатор, выдаваемый при создании аккаунта
    var isFav: Boolean = false,

    var viewsCounter: String = "0",
    var emailCounter: String = "0",
    var callsCounter: String = "0"

) : Serializable
