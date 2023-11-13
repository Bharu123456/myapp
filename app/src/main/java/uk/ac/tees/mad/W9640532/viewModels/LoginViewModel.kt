package uk.ac.tees.mad.W9640532.viewModels

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import uk.ac.tees.mad.W9640532.Login

class LoginViewModel : ViewModel()  {

    private val loginResultMutable = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = loginResultMutable

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    loginResultMutable.value = LoginResult.LoginSuccess(user)
                } else {
                    loginResultMutable.value = LoginResult.LoginError(task.exception?.message ?: "Authentication failed.")
                }
            }
    }

    fun register(email: String, pass: String){

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    loginResultMutable.value = LoginResult.RegSuccess(user)
                } else {
                    loginResultMutable.value = LoginResult.RegError("Authentication failed user Already exist")
                }
            }
    }

    sealed class LoginResult {
        data class LoginSuccess(val user: FirebaseUser?) : LoginResult()
        data class LoginError(val errorMessage: String) : LoginResult()

        data class RegSuccess(val user: FirebaseUser?) : LoginResult()
        data class RegError(val errorMessage: String) : LoginResult()

    }
}