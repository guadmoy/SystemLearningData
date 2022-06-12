package com.guadmoy.systemlearning.act


import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.databinding.ActivityMyProfileBinding
import com.guadmoy.systemlearning.model.Account
import com.guadmoy.systemlearning.model.DbManager
import com.guadmoy.systemlearning.viewmodel.FirebaseViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfile : AppCompatActivity() {
    lateinit var binding: ActivityMyProfileBinding
    private val fireBaseViewModel: FirebaseViewModel by viewModels()
    val mAuth = Firebase.auth
    var loginAct = LoginAct()
    var imageIndex = 0;
    var mainActivity = MainActivity()
    private val dbManager = DbManager()
    private var account: Account? = null
    var accountArray = ArrayList<Account>()
    var imageList = ArrayList<Bitmap>()
    var uriImage: String? = null
    var statusAccount = "student"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewsInfo()
        fireBaseViewModel.loadInfoAccount()
        init()

    }
    fun init() {
        editOn()
    }

    fun initViewsInfo(){
        fireBaseViewModel.liveAccountData.observe(this) {
            accountArray.clear()
            accountArray.addAll(it)
            if(accountArray.isEmpty()){
                updateUiWithGoogle(mAuth.currentUser)
            }else{
                fillProfileViews(accountArray[0])
            }
        }
    }

    fun updateUiWithGoogle(user:FirebaseUser?){
        if(user != null){
            binding.tvProfileName.setText ( if(user.displayName != null) user.displayName else "")
            binding.tvProfileEmail.text = if(user.email != null) user.email else ""
            Picasso.get().load(user.photoUrl).into(binding.ibAvatar)
            uriImage = user.photoUrl.toString()
        }
    }


    fun onClickBack(view: View){
        finish()
    }
    fun onClickStatus(view: View){
        statusAccount = "teacher"
        btGiveTeacher.visibility = View.GONE
        Toast.makeText(
            this,
            "Заявка подана, сохраните изменения!",
            Toast.LENGTH_LONG
        ).show()
    }

    fun onClickPublishAccount(view: View){
        if(tvProfileName.text.toString() == ""){
            tvNotifyErrorEdit.visibility = View.VISIBLE
        }else{
            account = fillProfile()
            dbManager.publishProfile(account!!.copy(uid = account?.uid),onPublishFinish())
        }

    }
    private fun onPublishFinish() : DbManager.FinishWorkListener{
        return object : DbManager.FinishWorkListener{
            override fun onFinish() {
                finish()
            }
        }
    }


    private fun fillProfile(): Account {
        val account: Account
        binding.apply {
            account = Account(
                tvProfileName.text.toString(),
                tvProfileEmail.text.toString(),
                tvDescriptionProfile.text.toString(),
                uriImage,
                tvCoin.text.toString(),
                statusAccount,
                dbManager.auth.uid,
            )
        }
        return account
    }
    private fun fillProfileViews(account: Account) = with(binding){
        statusAccount = account.status!!
        uriImage = account.avatarImage
        Picasso.get().load(account.avatarImage).into(ibAvatar)
        tvProfileName.setText (account.name)
        tvProfileEmail.text = account.email
        tvDescriptionProfile.setText(account.description)
        tvCoin.text = (account.coin)
        tvStatusProfile.text = account.status
        if(account.status == "teacher") btGiveTeacher.visibility = View.GONE
    }

    private fun editOn(){
        binding.tvProfileName.setInputType(InputType.TYPE_CLASS_TEXT)
        binding.tvDescriptionProfile.setInputType(InputType.TYPE_CLASS_TEXT)

        binding.btGiveTeacher.visibility = View.VISIBLE
        binding.bSaveProfile.visibility = View.VISIBLE
    }

}