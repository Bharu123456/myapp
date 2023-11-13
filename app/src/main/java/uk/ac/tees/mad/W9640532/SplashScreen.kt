package uk.ac.tees.mad.W9640532

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()


        if (auth.currentUser != null) {
            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))

            }, 2000)
        }else{

            Handler().postDelayed({
                startActivity(Intent(this, Login::class.java))
            }, 2000)
        }





    }
}