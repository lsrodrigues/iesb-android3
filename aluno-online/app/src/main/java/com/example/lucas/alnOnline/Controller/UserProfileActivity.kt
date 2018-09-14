package com.example.lucas.alnOnline.Controller

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.lucas.alnOnline.Entity.User
import com.example.lucas.alnOnline.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.ByteArrayOutputStream

class UserProfileActivity : AppCompatActivity() {

    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    private val PHOTO_MAX_SIZE: Long = 100 * 1024
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var student: User = User()
    private var userDBRef: DatabaseReference? = null
    private var userStoreRef: StorageReference? = null
    private var photoContent: ByteArray? = null
    private val PHOTO_NAME: String = "profile_photo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        save.setOnClickListener { saveStudent() }
        picture.setOnClickListener { selectImageInAlbum() }
        configFirebase()
        retrieveStudentProfileFromCloud()
    }

    private fun configFirebase() {
        auth = FirebaseAuth.getInstance()
        user = auth?.currentUser
        database = FirebaseDatabase.getInstance()
        userDBRef = database?.getReference("user")
        storage = FirebaseStorage.getInstance()
        userStoreRef = storage?.getReference("user")
    }

    private fun saveStudent() {
        saveStudentPhoto() {
            saveStudentInfo() {
                val intent = Intent(this@UserProfileActivity, UserNewsListActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun retrieveStudentProfileFromCloud() {
        val studentListener = object: ValueEventListener {
            override fun onDataChange(studentSnapshot: DataSnapshot) {
                if (studentSnapshot.exists()) {
                    student = studentSnapshot.getValue(User::class.java)!!
                    name.setText(student.name)
                    mail.setText(student.mail)
                    code.setText(student.enrollment)
                    phone_txt.setText(student.phone)
                    if (student.image.trim().isNotEmpty()) {
                        retrieveStudentPhotoFromCloudWith(student.image)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserProfileActivity, "Ops, houve um erro na recuperação do usuário: ${error.toException()}", Toast.LENGTH_LONG).show()
            }
        }
        userDBRef!!.child(user!!.uid).addListenerForSingleValueEvent(studentListener)
    }

    private fun retrieveStudentPhotoFromCloudWith(photoStoreRef: String) {
        val storageRef: StorageReference? = storage?.getReference(photoStoreRef)
        if (storageRef == null) {
            Toast.makeText(this@UserProfileActivity, "Erro ao obter referência ao armazenamento!", Toast.LENGTH_LONG).show()
            return
        }
        storageRef!!.getBytes(PHOTO_MAX_SIZE)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val photoByteArray = task.result
                        photoContent = photoByteArray
                        val photoBMP = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.size)
                        photoBMP.let {
                            picture.setImageBitmap(photoBMP)
                        }
                    } else {
                        Toast.makeText(this@UserProfileActivity, "Erro no download da foto de perfil: ${task.exception.toString()}", Toast.LENGTH_LONG).show()
                    }
                }
    }


    private fun saveStudentInfo(completion: () -> Unit) {
        if (user == null) {
            Toast.makeText(this@UserProfileActivity, "Erro obtendo usuário corrente!", Toast.LENGTH_LONG).show()
//            logOut()
            return
        }
        if (userDBRef == null) {
            Toast.makeText(this@UserProfileActivity, "Erro ao obter referência do banco de dados!", Toast.LENGTH_LONG).show()
            return
        }
        student.name = name.text.toString()
        student.enrollment = code.text.toString()
        student.phone = phone_txt.text.toString()
        student.id = user!!.uid
        student.mail = mail.text.toString()
        userDBRef!!.child(user!!.uid).setValue(student).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@UserProfileActivity, "Informações salvas com sucesso!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@UserProfileActivity, "Erro ao salvar informações: ${task.exception.toString()}", Toast.LENGTH_LONG).show()
            }
            completion()
        }
    }

    private fun saveStudentPhoto(completion: () -> Unit) {
        val photoData = photoContent
        if (photoData == null) { return } else
            if (user == null) {
                Toast.makeText(this@UserProfileActivity, "Erro obtendo usuário corrente!", Toast.LENGTH_LONG).show()
                return
            }
        if (userDBRef == null) {
            Toast.makeText(this@UserProfileActivity, "Erro ao obter referência ao armazenamento!", Toast.LENGTH_LONG).show()
            return
        }
        userStoreRef!!.child(user!!.uid).child(PHOTO_NAME).putBytes(photoData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        student.image = task.result.metadata!!.path
                    } else {
                        student.image = ""
                        Toast.makeText(this@UserProfileActivity, "Erro ao salvar photo: ${task.exception.toString()}", Toast.LENGTH_LONG).show()
                    }
                    completion()
                }

    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageStream = ByteArrayOutputStream()
        val selectedImagePath = data!!.data
        val imageData = MediaStore.Images.Media
                .getBitmap(contentResolver, selectedImagePath)

        picture.setImageBitmap(imageData)
        imageData.compress(Bitmap.CompressFormat.PNG, 100, imageStream)
        photoContent = imageStream.toByteArray()
    }

}
