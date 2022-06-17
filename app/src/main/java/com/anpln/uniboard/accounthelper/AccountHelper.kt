package com.anpln.uniboard.accounthelper

import android.util.Log
import android.widget.Toast
import com.anpln.uniboard.MainActivity
import com.anpln.uniboard.R
import com.anpln.uniboard.constants.FirebaseAuthConstants
import com.anpln.uniboard.dialoghelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*


class AccountHelper(act: MainActivity) {
    private val act = act //переменная для подсоединения Activity

    private lateinit var signInClient: GoogleSignInClient
    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.currentUser?.delete()?.addOnCompleteListener {
                task ->
                if(task.isSuccessful){
                    act.mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signUpWithEmailSuccessful(task.result.user!!)

                            } else {
                                signUpWithEmailException(task.exception!!, email, password)
                            }
                            //проверка на успешную регистрацию
                        }
                }
            }


        }
        //функция для регистрации по email-адресу

    }
    private fun signUpWithEmailSuccessful(user: FirebaseUser){
        sendEmailVerification(user)
        act.uiUpdate(user)
    }

    private fun signUpWithEmailException(e: Exception, email: String, password: String){

        if (e is FirebaseAuthUserCollisionException) {
            val exception = e as FirebaseAuthUserCollisionException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {


                linkEmailToG(email, password)
            }


        } else if (e is FirebaseAuthInvalidCredentialsException) {

            if (e.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {

                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG
                ).show()
            }

        }
        if (e is FirebaseAuthWeakPasswordException) {

            //Log.d("MyLog", "Exception: ${e.errorCode}")
            if (e.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {

                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_WEAK_PASSWORD,
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }


    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.currentUser?.delete()?.addOnCompleteListener {
                task ->
                if(task.isSuccessful){
                    act.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            act.uiUpdate(task.result?.user)

                        } else {
                            signUpWithEmailException(task.exception!!, email, password)

                        }
                    }
                }
            }
        }
        //вход по email-адрес
    }

    private fun signInWithEmailException(e: Exception, email: String, password: String){
       // Log.d("MyLog", "Exception: ${e}")

        if (e is FirebaseAuthInvalidCredentialsException) {
            //Log.d("MyLog", "Exception: ${task.exception}")

            //Log.d("MyLog", "Exception 2: ${exception.errorCode}")

            if (e.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {

                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_WRONG_PASSWORD,
                    Toast.LENGTH_LONG
                ).show()
            } else if (e.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {

                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG
                ).show()
            }

        } else if (e is FirebaseAuthInvalidUserException) {

            if (e.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_USER_NOT_FOUND,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun linkEmailToG(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (act.mAuth.currentUser != null) {
            act.mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //проверка на успешное выполнение
                    Toast.makeText(
                        act,
                        act.resources.getString(R.string.link_done),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        } else {
            Toast.makeText(
                act,
                act.resources.getString(R.string.enter_to_g),
                Toast.LENGTH_LONG
            ).show()
        }
        //подсоединяем email к google аккаунту
    }

    private fun getSignInClient(): GoogleSignInClient {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(act, gso)
//настраиваем клиента, чтобы в дальнейшем сделать запрос со смартфона
// для получения google аккаунта
// берем токен для регистрации и запрашиваем email

    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startActivityForResult(intent, GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    fun signOutG() {
        getSignInClient().signOut()
    }

    fun signInFirebaseWithGoogle(token: String) {
        //вход по google-аккаунту
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.mAuth.currentUser?.delete()?.addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                act.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(act, "Sign in done", Toast.LENGTH_LONG).show()
                        act.uiUpdate(task.result?.user) //обновление user-интерфейса, добавление результата входа
                    } else {
                        Log.d("MyLog", "Google Sign In Exception: ${task.exception}")
                    }
            }
        }

        }

    }


    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verification_done),
                    Toast.LENGTH_LONG
                ).show()

            } else {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verification_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    fun signInAnonymously(listener: Listener){
        act.mAuth.signInAnonymously().addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                listener.onComplete()
                Toast.makeText(act, "Вы вошли как гость", Toast.LENGTH_LONG).show()
            }else {
                Toast.makeText(act, "Не удалось войти как гость", Toast.LENGTH_LONG).show()
            }
        }
    }
    interface Listener{
      fun  onComplete()
    }
}//все функции для регистрации