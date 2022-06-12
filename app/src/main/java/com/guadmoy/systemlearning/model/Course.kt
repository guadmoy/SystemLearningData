package com.guadmoy.systemlearning.model

import java.io.Serializable

data class Course(
    val city:String? = null,
    val unever:String? = null,
    val langue:String? = null,
    val name:String? = null,
    val category:String? = null,
    val description:String? = null,
    val uriVideoCourse:String? = null,
    val request:String? = null,
    val audit:String? = null,
    val time:String? = null,
    val about:String? = null,
    val certificate: Boolean = false,
    val typeCourse: Boolean = false,
    val price:String? = null,
    var key:String? = null,
    val uid:String? = null,
    val timeCreate:String = "0",
    var isLike:Boolean = false,
    var isStudy:Boolean = false,

    //Статистика
    var countStudent :Int? = 0,

    var countModuleLecture :Int? = 0,
    var countModuleLectureDone :Double? = 0.0,
    var countModulePractice :Int? = 0,
    var countModulePracticeDone :Double? = 0.0,


    //Преподаватель
    val teacherName:String = "0",
    val teacherDescription:String = "0",
    val teacherPhoto:String? = null,

    //Фото ссылки
    var mainImage:String? = null,
    var firstImage:String? = null,
    var secondImage:String? = null,

    //Только внутри приложения
    var viewsCounter:String = "0",
    var ratingCounter:String = "0",
    var ratingCounterPeople:Int = 0,
    var likeCounter:String = "0",
):Serializable
