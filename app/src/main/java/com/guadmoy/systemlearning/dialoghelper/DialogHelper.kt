package com.guadmoy.systemlearning.dialoghelper

import android.app.AlertDialog
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.accounthelper.AccountHelper
import com.guadmoy.systemlearning.act.LoginAct
import com.guadmoy.systemlearning.databinding.SignDialogBinding




class DialogHelper(act:LoginAct) {
    private val act = act
    val accHelper = AccountHelper(act)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun createSignDialog(index:Int){
        val builder = AlertDialog.Builder(act)
        val rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)

        //Выбор состояние диалога
        setDialogState(index,rootDialogElement)

        val dialogSignOut = builder.create()

        //Кнопка Войти/Регистриция
        rootDialogElement.btSingUpIn.setOnClickListener{
            setOnClickSignUpIn(dialogSignOut,rootDialogElement,index)
        }
        //Кнопка Вход Гугл
        rootDialogElement.btGoogleSignIn.setOnClickListener{
            accHelper.singInWithGoogle()
            dialogSignOut.dismiss()
        }
        //Кнопка Забыли пароль
        rootDialogElement.btForgetPassword.setOnClickListener{
            setOnClickResetPassword(dialogSignOut,rootDialogElement)
        }


        dialogSignOut.show()
    }

    private fun setOnClickResetPassword(dialogSignOut: AlertDialog?, rootDialogElement: SignDialogBinding) {
        if(rootDialogElement.edSingEmail.text.isNotEmpty()){
            act.mAuth.sendPasswordResetEmail(rootDialogElement.edSingEmail.text.toString()).addOnCompleteListener {task->
            if (task.isSuccessful){

                Toast.makeText(act, R.string.sing_send_suc, Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(act, R.string.sing_send_error, Toast.LENGTH_LONG).show()
                }
            }
            dialogSignOut?.dismiss()
        }else{
            rootDialogElement.tvResetEmailWarning.visibility = View.VISIBLE
        }

    }

    private fun setOnClickSignUpIn(dialogSignOut: AlertDialog?, rootDialogElement: SignDialogBinding, index: Int) {
        dialogSignOut?.dismiss()
        if(index == DialogConst.SING_UP_STATE){
            accHelper.signUpWithEmail(rootDialogElement.edSingEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString())
        }else{
            accHelper.signInWithEmail(rootDialogElement.edSingEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString())
        }
    }

    private fun setDialogState(index: Int, rootDialogElement: SignDialogBinding) {
        if(index == DialogConst.SING_UP_STATE){

            rootDialogElement.btSingUpIn.text = act.resources.getString(R.string.sing_up_action)
        }else if(index == DialogConst.SING_IN_STATE){

            rootDialogElement.btSingUpIn.text = act.resources.getString(R.string.sing_in_action)
            rootDialogElement.btForgetPassword.visibility = View.VISIBLE
        }
    }

}