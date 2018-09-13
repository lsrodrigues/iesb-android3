package com.example.lucas.alnOnline.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.lucas.alnOnline.R
import com.example.lucas.alnOnline.Validator
import kotlinx.android.synthetic.main.activity_new_user.*
import com.example.lucas.alnOnline.Entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class NewUserActivity : AppCompatActivity() {

    var mAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        mAuth = FirebaseAuth.getInstance()

        createAccount.setOnClickListener {
            var validator = Validator()

            if(txt_create_email.text.toString().isEmpty() || txt_create_senha.text.toString().isEmpty() || txt_confirma_senha.text.toString().isEmpty()){
                Toast.makeText(this, "Preencha os campos obrigatórios", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!validator.validateConfirmPassword(txt_create_senha.text.toString(), txt_confirma_senha.text.toString())){
                Toast.makeText(this, "Senhas são diferentes", Toast.LENGTH_LONG).show()
                txt_create_senha.setText("")
                txt_confirma_senha.setText("")
                return@setOnClickListener
            }

            if(!validator.validatePassword(txt_create_senha.text.toString())){
                Toast.makeText(this, "A senha deve conter 6 dígitos, e conter pelo menos um caractere maiúsculo, um caractere especial e um número", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!validator.validateEmail(txt_create_email.text.toString())){
                Toast.makeText(this, "Email inválido", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var u = User(txt_create_email.text.toString(), txt_confirma_senha.text.toString())

            mAuth?.let { m ->
                m.createUserWithEmailAndPassword(u.mail.toString(),u.password.toString()).addOnCompleteListener({ task ->
                    if(task.isSuccessful){
                        Log.i("Novousuario", mAuth?.currentUser?.uid)
                        Log.i("Novousuario", mAuth?.currentUser?.isEmailVerified.toString())

                        if(mAuth?.currentUser != null && mAuth?.currentUser?.isEmailVerified == false){
                            mAuth?.currentUser?.sendEmailVerification()
                        }

                        startActivity(Intent(this@NewUserActivity, LoginActivity::class.java))
                        finish()
                    }else if(task.exception is FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(this, "E-mail inválido, digite um novo email", Toast.LENGTH_LONG).show()
                    }else if(task.exception is FirebaseAuthUserCollisionException){
                        Toast.makeText(this, "E-mail já cadastrado", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }
}
