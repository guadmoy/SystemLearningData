package com.guadmoy.systemlearning.act

import android.content.Intent
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.databinding.ActivityLoginBinding
import com.guadmoy.systemlearning.dialoghelper.DialogConst
import com.guadmoy.systemlearning.dialoghelper.DialogHelper

class LoginAct : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private val dialogHelper = DialogHelper(this)
    lateinit var googleSignInLan: ActivityResultLauncher<Intent>
    val mAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init(){
        onActivityResult()
        checkStatusSign()
    }

    private fun onActivityResult() {
        googleSignInLan = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null){
                    dialogHelper.accHelper.signFirebaseWithGoogle(account.idToken!!)
                }

            }catch (e: ApiException){
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun checkStatusSign(){
        //dialogHelper.accHelper.singOutWithGoogle()
       // Toast.makeText(this,"${mAuth.currentUser}",Toast.LENGTH_LONG).show()
        if(mAuth.currentUser != null){
            var i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }

     fun onClickRegisterProfile(view: View){
        dialogHelper.createSignDialog(DialogConst.SING_UP_STATE)
    }
     fun onClickSignProfile(view: View){
        dialogHelper.createSignDialog(DialogConst.SING_IN_STATE)
    }

    var doubleBackToExitPressed = 1

    override fun onBackPressed() {
        if (doubleBackToExitPressed == 2) {
            finishAffinity();
            System.exit(0);
        }
        else {
            doubleBackToExitPressed++;
            Toast.makeText(this, "Please press Back again to exit", Toast.LENGTH_SHORT).show();
        }

        Handler().postDelayed(Runnable() {
            @Override
            fun run() {
                doubleBackToExitPressed=1;
            }
        }, 2000);
    }


}