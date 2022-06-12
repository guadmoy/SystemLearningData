package com.guadmoy.systemlearning.model

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DbManager {
    val dbCourses = Firebase.database.getReference(MAIN_COURSE_NODE)
    val dbProfile = Firebase.database.getReference(MAIN_PROFILE_NODE)
    val dbStorage = Firebase.storage.getReference(MAIN_COURSE_NODE)
    val auth = Firebase.auth

    //Публикация
    fun publishCourse(course: Course, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null)
            dbCourses.child(course.key!!).child(auth.uid!!)
                .child(COURSE_NODE)
                .setValue(course).addOnCompleteListener {
                    val courseFilter = CourseFilter(
                        course.timeCreate,
                        "${course.category}_${course.timeCreate}",
                    course.name)
                    dbCourses.child(course.key!!).child(COURSE_FILTER)
                        .setValue(courseFilter).addOnCompleteListener {
                            finishWorkListener.onFinish()
                        }
                }
    }
    //Публикация модуля лекций
    fun publishModuleItem(module: Module, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null)
            dbCourses.child(module.keyCourse!!)
                .child(MODULE_LIST)
                .child(MODULE_LIST_LECTURES)
                .child(module.key!!)
                .setValue(module).addOnCompleteListener {
                    finishWorkListener.onFinish()
                }
    }
    //Показать все модули лекции только данного курса
    fun getModule(course: Course,readDataCallback: ReadDataModuleCallback?) {
        val query = dbCourses.child(course.key!!).child(MODULE_LIST).child(MODULE_LIST_LECTURES)
        readDataFromDbModule(query,readDataCallback)
    }
    //Показать все модули практики только данного курса
    fun getPractice(course: Course,readDataCallback: ReadDataPracticeCallback?) {
        val query = dbCourses.child(course.key!!).child(MODULE_LIST).child(MODULE_LIST_PRACTICE)
        readDataFromDbPractice(query,readDataCallback)
    }

    //Публикация модуля практика
    fun publishModulePractice(practice: Practice, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null)
            dbCourses.child(practice.keyCourse!!)
                .child(MODULE_LIST )
                .child(MODULE_LIST_PRACTICE)
                .child(practice.key!!)
                .setValue(practice).addOnCompleteListener {
                    finishWorkListener.onFinish()
                }
    }

    //Публикация модуля отзывы
    fun publishModuleFeedback(feedback: Feedback, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null)
            dbCourses.child(feedback.keyCourse!!)
                .child(COURSE_FEEDBACK )
                .child(feedback.key!!)
                .setValue(feedback).addOnCompleteListener {
                    finishWorkListener.onFinish()
                }
    }
    //Показать все отзывы данного курса
    fun getFeedback(course: Course,readDataCallback: ReadDataFeedbackCallback?) {
        val query = dbCourses.child(course.key!!).child(COURSE_FEEDBACK)
        readDataFromDbFeedback(query,readDataCallback)
    }
    //Публикация профиля
    fun publishProfile(account: Account, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null)
            dbProfile.child(auth.uid!!)
                .setValue(account).addOnCompleteListener {
                    finishWorkListener.onFinish()
                }
    }

    //Добавление к профилю курс при покупке
    fun publishCourseProgress(course: Course, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null)
            dbProfile.child(auth.uid!!).child(PROFILE_COURSE_NODE).child(course.key!!)
                .setValue(course).addOnCompleteListener {
                    finishWorkListener.onFinish()
                }
    }
    //Добавление к профилю курс модуля ghb pfdthitybt
    fun publishCourseModuleProgress(module: Module, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null)
            dbProfile.child(auth.uid!!).child(MODULE_LIST).child(
                module.key!!)
                .setValue(module).addOnCompleteListener {
                    finishWorkListener.onFinish()
                }
    }
    //Добавление к профилю курс модуля ghb pfdthitybt
    fun publishCoursePracticeProgress(practice: Practice, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null)
            dbProfile.child(auth.uid!!).child(MODULE_LIST_PRACTICE).child(
                practice.key!!)
                .setValue(practice).addOnCompleteListener {
                    finishWorkListener.onFinish()
                }
    }

    //Показать только мои курсы
    fun getMyCourse(readDataCallback: ReadDataCallback?) {
        val query = dbCourses.orderByChild(auth.uid + "/content/uid").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }
    //Показать только избранные курсы
    fun getLikeCourse(readDataCallback: ReadDataCallback?) {
        val query = dbCourses.orderByChild("/like/${auth.uid}").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }
    //Показать только Мое обучение
    fun getMyStudyCourse(readDataCallback: ReadDataCallback?) {
        val query = dbCourses.orderByChild("/student/${auth.uid}").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }
    //Показать все курсы первая страница
    fun getAllCourseFirstPage(readDataCallback: ReadDataCallback?) {
        val query = dbCourses.orderByChild( "/$COURSE_FILTER/time")
            .limitToLast(
                COURSE_LIMIT
            )
        readDataFromDb(query, readDataCallback)
    }
    //Показать текущий курс
    fun loadCurrentCourse(uuid:String,readDataCallback: ReadDataCallback?) {
        val query = dbProfile.orderByChild("uid").equalTo(auth.uid)
        readDataFromDbProfileCourse(query, readDataCallback)
    }
    //Показать текущий модули
    fun loadCurrentAllModule(readDataCallback: ReadDataModuleCallback?) {
        val query = dbProfile.child(auth.uid!!).child(MODULE_LIST)
        readDataFromDbModule(query, readDataCallback)
    }

    //Показать все курсы остальные
    fun getAllCourseNextPage(time:String,readDataCallback: ReadDataCallback?) {
        val query = dbCourses.orderByChild( "/$COURSE_FILTER/time").endBefore(time)
            .limitToLast(
                COURSE_LIMIT
            )
        readDataFromDb(query, readDataCallback)
    }
    //Показать курсы по имени
    fun getNameCourseFirstPage(name:String,readDataCallback: ReadDataCallback?) {
        val query = dbCourses.orderByChild( "/$COURSE_FILTER/name")
            .startAt(name).endAt(name +"\uf8ff")
            .limitToLast(
                COURSE_LIMIT
            )
        readDataFromDb(query, readDataCallback)
    }
    //Показать все курсы по имени остальные
    fun getNameCourseNextPage(nameTime:String,readDataCallback: ReadDataCallback?) {
        val query = dbCourses.orderByChild( "/$COURSE_FILTER/name")
            .endBefore(nameTime)
            .limitToLast(
                COURSE_LIMIT
            )
        readDataFromDb(query, readDataCallback)
    }
    //Показать курсы по категории первая страница
    fun getAllCourseFromCatFirstPage(cat: String, readDataCallback: ReadDataCallback?) {
        val query = dbCourses.orderByChild( "/$COURSE_FILTER/catTime")
            .startAt(cat).endAt(cat + "_\uf8ff").limitToLast(
                COURSE_LIMIT
            )
        readDataFromDb(query, readDataCallback)
    }
    //Показать курсы по категории оставшиеся
    fun getAllCourseFromCatNextPage(catTime: String, readDataCallback: ReadDataCallback?) {
        val query = dbCourses.orderByChild( "/$COURSE_FILTER/catTime")
            .endBefore(catTime)
            .limitToLast(
                COURSE_LIMIT
            )
        readDataFromDb(query, readDataCallback)
    }
    //Удалить один мой курс
    fun deleteOneCourse(course: Course, finishWorkListener: FinishWorkListener) {
        if (course.key == null || course.uid == null) return
        dbCourses.child(course.key!!).removeValue().addOnCompleteListener {
            finishWorkListener.onFinish()
        }
    }
    //Счетчик просмотров
    fun courseViews(course: Course) {
        var counter = course.viewsCounter.toInt()
        counter++
        if (auth.uid != null)
            dbCourses.child(course.key!!)
                .child(COURSE_INFO_NODE)
                .setValue(InfoCourse(counter.toString()))
    }
    //Добавить в избранное
    fun addToLike(course: Course, finishWorkListener: FinishWorkListener) {
        course.key?.let {
            auth.uid?.let { uid ->
                dbCourses.child(it).child(COURSE_LIKE_NODE).child(uid).setValue(uid)
                    .addOnCompleteListener {
                        if (it.isSuccessful) finishWorkListener.onFinish()
                    }
            }
        }
    }
    //Удалить из избранное
    private fun removeToLike(course: Course, finishWorkListener: FinishWorkListener) {
        course.key?.let {
            auth.uid?.let { uid ->
                dbCourses.child(it).child(COURSE_LIKE_NODE).child(uid).removeValue()
                    .addOnCompleteListener {
                        if (it.isSuccessful) finishWorkListener.onFinish()

                    }
            }
        }
    }
    //Функция добавления в избранное
    fun onLikeClick(course: Course, finishWorkListener: FinishWorkListener) {
        if (course.isLike) {
            removeToLike(course, finishWorkListener)
        } else {
            addToLike(course, finishWorkListener)
        }
    }
    //Добавить в Мое обучение
    fun addToMyStudy(course: Course, finishWorkListener: FinishWorkListener) {
        course.key?.let {
            auth.uid?.let { uid ->
                dbCourses.child(it).child(COURSE_STUDENT_NODE).child(uid).setValue(uid)
                    .addOnCompleteListener {
                       // if (it.isSuccessful) finishWorkListener.onFinish()
                    }
            }
        }
    }
    //Удалить из Мое обучение
    private fun removeToMyStudy(course: Course, finishWorkListener: FinishWorkListener) {
        course.key?.let {
            auth.uid?.let { uid ->
                dbCourses.child(it).child(COURSE_STUDENT_NODE).child(uid).removeValue()
                    .addOnCompleteListener {
                       // if (it.isSuccessful) finishWorkListener.onFinish()
                    }
            }
        }
    }
    //Функция добавления в Мое обучение
    fun onMyStudyClick(course: Course, finishWorkListener: FinishWorkListener) {
        if (course.isStudy) {
            removeToMyStudy(course, finishWorkListener)
        } else {
            addToMyStudy(course, finishWorkListener)
        }

    }
    //Показать информацию аккаунта
    fun getMyAccount(readDataCallback: ReadDataAccountCallback?) {
        val query = dbProfile.orderByChild("uid").equalTo(auth.uid)
        readDataFromDbProfile(query, readDataCallback)
    }


    private fun readDataFromDb(query: Query, readDataCallback: ReadDataCallback?) {

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courseArray = ArrayList<Course>()

                for (item in snapshot.children) {
                    var content: Course? = null
                    item.children.forEach {
                        if (content == null) content =
                            it.child(COURSE_NODE).getValue(Course::class.java)
                    }
                    val infoCourse = item.child(COURSE_INFO_NODE).getValue(InfoCourse::class.java)

                    val likeCounter = item.child(COURSE_LIKE_NODE).childrenCount
                    val isLike = auth.uid?.let {
                        item.child(COURSE_LIKE_NODE).child(it).getValue(
                            String::class.java
                        )
                    }
                    val isStudy = auth.uid?.let {
                        item.child(COURSE_STUDENT_NODE).child(it).getValue(
                            String::class.java
                        )
                    }
                    content?.isLike = isLike != null
                    content?.isStudy = isStudy != null
                    content?.likeCounter = likeCounter.toString()


                    content?.viewsCounter = infoCourse?.viewsCounter ?: "0"
                    content?.ratingCounter = infoCourse?.ratingCounter ?: "0"
                    if (content != null) courseArray.add(content!!)

                }
                readDataCallback?.readData(courseArray)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    private fun readDataFromDbProfile(query: Query, readDataCallback: ReadDataAccountCallback?) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accountArray = ArrayList<Account>()
                for (item in snapshot.children) {
                    var account: Account? = null
                    if (account == null) account = item.getValue(Account::class.java)
                    if (account != null) accountArray.add(account)
                }
                readDataCallback?.readDataAccount(accountArray)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun readDataFromDbProfileCourse(query: Query, readDataCallback: ReadDataCallback?) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courseArray = ArrayList<Course>()
                for (item in snapshot.children) {
                    item.child("course").children.forEach { courses->
                        var content: Course? = null
                        if (content == null) content = courses.getValue(Course::class.java)
                        if (content != null) courseArray.add(content)
                    }
                }
                readDataCallback?.readData(courseArray)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun readDataFromDbModule(query: Query, readDataCallback: ReadDataModuleCallback?) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val moduleArray = ArrayList<Module>()
                for (item in snapshot.children) {
                    var module: Module? = null
                            module = item.getValue(Module::class.java)
                            moduleArray.add(module!!)
                }
                readDataCallback?.readDataModule(moduleArray)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun readDataFromDbPractice(query: Query, readDataCallback: ReadDataPracticeCallback?) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val practiceArray = ArrayList<Practice>()
                for (item in snapshot.children) {
                    var practice: Practice? = null
                        practice = item.getValue(Practice::class.java)
                        practiceArray.add(practice!!)
                }
                readDataCallback?.readDataPractice(practiceArray)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun readDataFromDbFeedback(query: Query, readDataCallback: ReadDataFeedbackCallback?) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val feedbackArray = ArrayList<Feedback>()
                for (item in snapshot.children) {
                    var feedback: Feedback? = null
                    feedback = item.getValue(Feedback::class.java)
                    feedbackArray.add(feedback!!)
                }
                readDataCallback?.readDataFeedback(feedbackArray)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    interface ReadDataCallback {
        fun readData(list: ArrayList<Course>)
    }

    interface ReadDataAccountCallback {
        fun readDataAccount(list: ArrayList<Account>)
    }

    interface ReadDataModuleCallback {
        fun readDataModule(list: ArrayList<Module>)
    }
    interface ReadDataPracticeCallback {
        fun readDataPractice(list: ArrayList<Practice>)
    }
    interface ReadDataFeedbackCallback {
        fun readDataFeedback(list: ArrayList<Feedback>)
    }

    interface FinishWorkListener {
        fun onFinish()
    }

    companion object {
        const val COURSE_NODE = "content"
        const val COURSE_FILTER = "filter"
        const val MODULE_LIST = "module"
        const val COURSE_FEEDBACK = "feedback"
        const val MODULE_LIST_LECTURES = "lectures"
        const val MODULE_LIST_PRACTICE = "practice"
        const val MAIN_COURSE_NODE = "course"
        const val PROFILE_COURSE_NODE = "course"
        const val MAIN_PROFILE_NODE = "profile"
        const val COURSE_INFO_NODE = "info"
        const val COURSE_LIKE_NODE = "like"
        const val COURSE_STUDENT_NODE = "student"
        const val COURSE_LIMIT = 4
    }
}