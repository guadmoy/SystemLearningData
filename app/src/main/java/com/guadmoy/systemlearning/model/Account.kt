package com.guadmoy.systemlearning.model
import java.io.Serializable

data class Account(
    val name:String? = null,
    val email:String? = null,
    val description:String? = null,
    var avatarImage:String? = null,
    var coin:String = "0",
    var status:String? = null,
    val uid:String? = null,
    val uidCurrentCourse:String? = null,
):Serializable
