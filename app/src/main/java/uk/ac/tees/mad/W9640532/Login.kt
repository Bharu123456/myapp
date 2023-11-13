package uk.ac.tees.mad.W9640532

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import uk.ac.tees.mad.W9640532.viewModels.LoginViewModel
import androidx.lifecycle.Observer

class Login : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        val loginButton = findViewById<Button>(R.id.button_login)
        val emailEditText = findViewById<EditText>(R.id.login_email_input)
        val passwordEditText = findViewById<EditText>(R.id.login_pass_input)
        val signup = findViewById<TextView>(R.id.text_signup)


        loginViewModel.loginResult.observe(this, Observer { result ->
            when (result) {
                is LoginViewModel.LoginResult.LoginSuccess -> {
                    Toast.makeText(this,"Login Success",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                is LoginViewModel.LoginResult.LoginError -> {

                    Toast.makeText(this,"Unable to login, please check email/password",Toast.LENGTH_LONG).show()
                }

                else ->{}

            }
        })


        signup.setOnClickListener{
            startActivity(Intent(this, Register::class.java))
        }

        loginButton.setOnClickListener{
            val  email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            loginViewModel.loginUser(email, password)
        }
    }
}