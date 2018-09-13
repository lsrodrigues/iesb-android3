package com.example.lucas.alnOnline.Controller

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.lucas.alnOnline.Entity.User
import com.example.lucas.alnOnline.R
import com.example.lucas.alnOnline.Validator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()

        createBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity, NewUserActivity::class.java))
        }

        if (mAuth?.currentUser != null) {
            val screen = Intent(this@LoginActivity, UserNewsListActivity::class.java)
            screen.putExtra("email", mAuth?.currentUser?.email)
            startActivity(screen)
            finish()
        } else {
            login.setOnClickListener {

                var u: User = User()
                u.mail = txt_email.text.toString()
                u.password = txt_senha.text.toString()

                if (u.mail.toString().isEmpty() || u.password.toString().isEmpty()) {
                    Toast.makeText(this, "Preencha o campo Email e/ou Senha", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                var validator: Validator = Validator()
                if (!validator.validateEmail(u.mail.toString())) {
                    Toast.makeText(this, "Email inválido", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                mAuth?.let { m ->

                    m.signInWithEmailAndPassword(u.mail.toString(), u.password.toString()).addOnCompleteListener({ task ->

                        if (task.isSuccessful) {

                            Log.i("New", mAuth?.currentUser?.uid)

                            val it = Intent(this@LoginActivity, UserNewsListActivity::class.java)
                            it.putExtra("email", u.mail)
                            it.putExtra("senha", u.password)
                            startActivity(it)
                            finish()
                        } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "Senha inválida ou conta não cadastrada para esse e-mail", Toast.LENGTH_LONG).show()
                        } else if (task.exception is FirebaseAuthInvalidUserException) {
                            Toast.makeText(this, "Verifique o e-mail ou a senha", Toast.LENGTH_LONG).show()
                        } else {
                            Log.i("ERR", task.exception.toString() + " " + task.exception)
                            Toast.makeText(this, "Verifique o e-mail ou a senha", Toast.LENGTH_LONG).show()
                        }

                    })

                }
            }

        }
    }
}
