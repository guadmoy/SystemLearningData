package com.guadmoy.systemlearning.model

import java.io.Serializable

data class Feedback(
    val negative:String? = null,
    val positive:String? = null,
    val des:String? = null,
    val rating:Float? = null,

    //Преподаватель
    val uid:String? = null,
    val teacherName:String = "0",
    val teacherPhoto:String? = null,

    val key:String? = null,
    val keyCourse:String? = null,
):Serializable
