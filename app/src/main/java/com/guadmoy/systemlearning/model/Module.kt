package com.guadmoy.systemlearning.model

import java.io.Serializable

data class Module(
    val nameTheme:String? = null,
    val desTheme:String? = null,
    val uriVideo:String? = null,
    val desVideo:String? = null,
    val key:String? = null,
    val keyCourse:String? = null,
    ): Serializable
