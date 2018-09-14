package com.example.lucas.alnOnline.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.example.lucas.alnOnline.Entity.New
import com.example.lucas.alnOnline.R
import com.example.lucas.alnOnline.View.NewsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_news_list.*

class UserNewsListActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var database: FirebaseDatabase? = null
    private var newsDBRef: DatabaseReference? = null
    private var newsForStudent: MutableList<New> = mutableListOf()
    private lateinit var studentNewsTable: RecyclerView
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_news_list)

//        newItem.setOnClickListener { goToNewDetail(new) }

        btn_user_detail.setOnClickListener { goToProfile() }

        val bannerImagesIndex: Array<Int> = arrayOf(R.drawable.iesb, R.drawable.download, R.drawable.visionario)
        banner.photoResourcesIndex = bannerImagesIndex
        banner.transitionTime = 3000

        studentNewsTable = this.findViewById(R.id.rec_view_news)
        studentNewsTable.layoutManager = LinearLayoutManager(this)
        studentNewsTable.itemAnimator = DefaultItemAnimator()
        newsAdapter = NewsAdapter(this)
        studentNewsTable.adapter = newsAdapter
        configFirebase()
        getNewsFromCloud()
    }

    private fun configFirebase() {
        auth = FirebaseAuth.getInstance()
        user = auth?.currentUser
        database = FirebaseDatabase.getInstance()
        newsDBRef = database?.getReference("/news")
    }

    private fun getNewsFromCloud() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.getReference("/news")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserNewsListActivity, "Erro na recuperação da lista de notícias: ${error.toException()}", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(studentNewsSnapshot: DataSnapshot) {
                newsForStudent.clear()
                studentNewsSnapshot.children.forEach {snap ->
                    val newForStudent: New? = snap.getValue(New::class.java)
                    if (newForStudent != null) {
                        newsForStudent.add(newForStudent)
                    }
                }
                newsAdapter.setData(newsForStudent)
            }

        })
    }

    private fun logOut() {
        auth!!.signOut()
        val intent = Intent(this@UserNewsListActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToProfile() {
        val intent = Intent(this@UserNewsListActivity, UserProfileActivity::class.java)
        startActivity(intent)
    }

//    private fun goToNewDetail(new: New) {
//        val intent = Intent(this@UserNewsListActivity, NewDetailActivity::class.java)
//        intent.putExtra("title",new.title)
//        intent.putExtra("description",new.description)
//        intent.putExtra("datePosted",new.datePosted)
//        startActivity(intent)
//    }


}
