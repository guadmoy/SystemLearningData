package com.guadmoy.systemlearning.adapters
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.act.DescriptionCourse
import com.guadmoy.systemlearning.act.EditKyrsAct
import com.guadmoy.systemlearning.act.Profile
import com.guadmoy.systemlearning.model.Course
import com.guadmoy.systemlearning.databinding.CourseListItemBinding
import com.guadmoy.systemlearning.utils.DiffUtillHelper
import com.squareup.picasso.Picasso

class CoursesRcAdapterProfile(val mainActivity: Profile): RecyclerView.Adapter<CoursesRcAdapterProfile.CourseHolder>() {
    val courseArray = ArrayList<Course>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
        val binding = CourseListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CourseHolder(binding,mainActivity)
    }

    override fun onBindViewHolder(holder: CourseHolder, position: Int) {
        holder.setData(courseArray[position])
    }


    fun updateAdapter(newList: List<Course>){
        val tempArray = ArrayList<Course>()
        tempArray.addAll(courseArray)
        tempArray.addAll(newList)
        val difResult = DiffUtil.calculateDiff(DiffUtillHelper(courseArray,tempArray))
        difResult.dispatchUpdatesTo(this)
        courseArray.clear()
        courseArray.addAll(tempArray)
    }
    fun updateAdapterWithClear(newList: List<Course>){
        val difResult = DiffUtil.calculateDiff(DiffUtillHelper(courseArray,newList))
        difResult.dispatchUpdatesTo(this)
        courseArray.clear()
        courseArray.addAll(newList)
    }

    override fun getItemCount(): Int {
         return courseArray.size
    }


    class CourseHolder(val binding: CourseListItemBinding,val mainActivity: Profile) : RecyclerView.ViewHolder(binding.root) {

        fun setData(course: Course) = with(binding) {
            //tvDescriptionCourse.text = course.description
            tvPriceCourse.text = course.price + " " + "₽"
            tvTitleCourse.text = course.name
          //  tvPeopleCount.text = course.viewsCounter
            tvRaitingCount.text = course.ratingCounter
            tvAvtor.text = course.teacherName
         //   tvLikeCounter.text = course.likeCounter
            Picasso.get().load(course.mainImage).into(ivMainImage)
            if(course.isLike){
                ibLike.setImageResource(R.drawable.ic_like_fill)
            }else ibLike.setImageResource(R.drawable.ic_like_border)
            showPanels(isOwner(course))



            ibLike.setOnClickListener{mainActivity.onCourseLiked(course)}
            ibEditCourse.setOnClickListener(editCourseListener(course))
            tvEditCourse.setOnClickListener(editCourseListener(course))
            ibDeleteCourse.setOnClickListener{ mainActivity.onDeleteOnCourse(course) }
            tvDeleteCourse.setOnClickListener{ mainActivity.onDeleteOnCourse(course) }
            itemView.setOnClickListener{
                mainActivity.onCourseViewed(course)
                val i = Intent(binding.root.context,DescriptionCourse::class.java)
                i.putExtra("Course", course)
                binding.root.context.startActivity(i)
            }


        }

        private fun editCourseListener(course: Course):View.OnClickListener{
            return View.OnClickListener {
                val editIntent = Intent(mainActivity,EditKyrsAct::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE,true)
                    putExtra(MainActivity.COURSE_DATA,course)
                }
                mainActivity.startActivity(editIntent)

            }
        }

        private fun isOwner(course: Course):Boolean{
            return course.uid == mainActivity.mAuth.uid
        }

        private fun showPanels(isOwner:Boolean){
            if(isOwner) {
                binding.ibLike.visibility = View.GONE
                binding.editPanel.visibility  = View.VISIBLE
            }else{
                binding.ibLike.visibility = View.VISIBLE
                binding.editPanel.visibility  = View.GONE
            }
        }
    }
    interface CourseListener{
        fun onDeleteOnCourse(course: Course)
        fun onCourseViewed(course: Course)
        fun onCourseLiked(course: Course)
    }
}