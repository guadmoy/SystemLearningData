package com.guadmoy.systemlearning.accounthelper

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.guadmoy.systemlearning.MainActivity
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.act.LoginAct
import com.guadmoy.systemlearning.model.Account
import com.guadmoy.systemlearning.model.Course
import kotlinx.android.synthetic.main.sign_dialog.*

class AccountHelper(act: LoginAct) {
    private val act = act

    private lateinit var googleSignInClient: GoogleSignInClient

    //Регистрация с помощью Емаил
    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendEmailVerification(task.result?.user!!)
                        val i = Intent(act, MainActivity::class.java)
                        act.startActivity(i)

                    } else {
                        Toast.makeText(
                            act,
                            act.resources.getString(R.string.sing_up_error),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("MyLog", "Exception: " + task.exception)

                        //Блок ошибок
                        //Ошибка связанные с аккаунтом в БД
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            val exception = task.exception as FirebaseAuthUserCollisionException
                            //Ошибка "Этот емаил уже используется"
                            if (exception.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                                linkEmailToGoogle(email, password)
                            }
                        }
                        //Ошибка связанная с полем емаил
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exception =
                                task.exception as FirebaseAuthInvalidCredentialsException
                            //Ошибка "Неправильный формат Емаил"
                            if (exception.errorCode == "ERROR_INVALID_EMAIL") {
                                Toast.makeText(act, "Неправильный формат Емаил", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        //Ошибка связанная с полем пароля (меньше 6 символов)
                        if (task.exception is FirebaseAuthWeakPasswordException) {
                            val exception = task.exception as FirebaseAuthWeakPasswordException
                            //Ошибка "Пароль меньше 6 символов"
                            if (exception.errorCode == "ERROR_WEAK_PASSWORD") {
                                Toast.makeText(act, "Пароль меньше 6 символов", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                }

        }
    }


    //Вход с помощью Емаил
    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val i = Intent(act, MainActivity::class.java)
                    act.startActivity(i)

                    Toast.makeText(
                        act,
                        act.resources.getString(R.string.sing_in_suc),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    //Ошибка связанная с полем емаил
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        val exception = task.exception as FirebaseAuthInvalidCredentialsException
                        //Ошибка "Неправильный формат Емаил"
                        if (exception.errorCode == "ERROR_INVALID_EMAIL") {
                            Toast.makeText(act, "Неправильный формат Емаил", Toast.LENGTH_LONG)
                                .show()
                        }
                        //Ошибка "Неправильный емаил или пароль"
                        if (exception.errorCode == "ERROR_WRONG_PASSWORD") {
                            Toast.makeText(act, "Неправильный емаил или пароль", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }

        }
    }

    //Отпрака проверочного письма на почту
    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.sing_send_suc),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.sing_send_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    //Соединение Емаил с Гугл аккаунтом
    private fun linkEmailToGoogle(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (act.mAuth.currentUser != null) {
            act.mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful)
                    Toast.makeText(act, "Аккаунт Емаил и ГУгл объедены", Toast.LENGTH_LONG).show()
            }
        } else Toast.makeText(
            act,
            "Войдите в Гугл Аккаунт для обединения с почтой",
            Toast.LENGTH_LONG
        ).show()
    }

    //Вход с помощью Google
    private fun getSingInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("825266917855-ri8gof3v093ack7eli2ijsamnasq7cqg.apps.googleusercontent.com")
            .requestEmail().build()
        return GoogleSignIn.getClient(act, gso)
    }

    //Вход с помощью Google(кнопка)
    fun singInWithGoogle() {
        googleSignInClient = getSingInClient()
        val intent = googleSignInClient.signInIntent
        act.googleSignInLan.launch(intent)
    }

    //выход с помощью Google(кнопка)
    fun singOutWithGoogle() {
        getSingInClient().signOut()
    }


    fun signFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, "Успешно", Toast.LENGTH_SHORT).show()
                var i = Intent(act, MainActivity::class.java)
                act.startActivity(i)
            }
        }
    }
}