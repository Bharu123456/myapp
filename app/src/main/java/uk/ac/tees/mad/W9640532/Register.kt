package uk.ac.tees.mad.W9640532

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import uk.ac.tees.mad.W9640532.viewModels.LoginViewModel

class Register : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginViewModel.loginResult.observe(this, Observer { result ->
            when (result) {
                is LoginViewModel.LoginResult.RegSuccess -> {
                    Toast.makeText(this,"Registation Success",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this,Login::class.java))
                    finish()
                }
                is LoginViewModel.LoginResult.RegError -> {

                    Toast.makeText(this,"Unable to register, please check email/password",Toast.LENGTH_LONG).show()
                }

                else ->{}

            }
        })

        val name = findViewById<EditText>(R.id.name_input)
        val regButton = findViewById<Button>(R.id.button_Register)
        val emailEditText = findViewById<EditText>(R.id.register_email_input)
        val passwordEditText = findViewById<EditText>(R.id.register_pass_input)

        regButton.setOnClickListener{

            val name = name.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val pass = passwordEditText.text.toString().trim()
            loginViewModel.register(email, pass)

        }
    }
}