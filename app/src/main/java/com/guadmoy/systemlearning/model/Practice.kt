package com.guadmoy.systemlearning.model

import java.io.Serializable

data class Practice(
    val question:String? = null,
    val answer:String? = null,
    val typeQuestion:Int? = null,

    val key:String? = null,
    val keyCourse:String? = null,
):Serializable
