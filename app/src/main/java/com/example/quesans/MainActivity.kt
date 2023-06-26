package com.example.quesans


import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quesans.adapter.UserAdapter
import com.example.quesans.databinding.ActivityMainBinding
import com.example.quesans.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.google.firebase.database.ValueEventListener




class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var signOutBtn: Button
    private lateinit var binding: ActivityMainBinding
    private var database: FirebaseDatabase? = null
    private var users: ArrayList<User>? = null
    private var usersAdapter: UserAdapter? = null
    private var dialog: ProgressDialog? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        // Enable Firebase Realtime Database logging for debugging
//        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        dialog = ProgressDialog(this@MainActivity)
        dialog!!.setMessage("Updating Image...")
        dialog!!.setCancelable(false)
        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance()
        users = ArrayList()
        usersAdapter = UserAdapter(this@MainActivity, users!!)

        val layoutManager = GridLayoutManager(this@MainActivity, 2)
        binding!!.mRec.layoutManager = layoutManager

        binding!!.mRec.adapter = usersAdapter

        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    users!!.clear()
                    for (snapshot1 in snapshot.children) {
                        val uid = snapshot1.child("uid").getValue(String::class.java)
                        val name = snapshot1.child("name").getValue(String::class.java)
                        val phoneNumber = snapshot1.child("phoneNumber").getValue(String::class.java)
                        val profileImage = snapshot1.child("profileImage").getValue(String::class.java)

                        Log.d("TAG", "User Data: uid=$uid, name=$name, phoneNumber=$phoneNumber, profileImage=$profileImage")


                        if (uid != null && uid != FirebaseAuth.getInstance().uid) {
                            val user = User(uid, name, phoneNumber, profileImage)
                            users!!.add(user)
                            Log.d("TAG","User added:$user")
                        }
                        else {
                            Log.d("TAG", "Skipping current user: uid=$uid, currentUid=${FirebaseAuth.getInstance().uid}")
                        }
                    }
                    usersAdapter!!.notifyDataSetChanged()
                    Log.d("TAG", "User List Size: ${users!!.size}")
                    handleEmptyUserList()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("TAG", "Database Error: ${error.message}")
                }
            })
    }private fun handleEmptyUserList() {
        if (users!!.isEmpty()) {
            binding.mRec.visibility = View.VISIBLE
            binding.mRec.visibility = View.GONE
        } else {
            binding.mRec.visibility = View.GONE
            binding.mRec.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!)
            .setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!)
            .setValue("Offline")
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}

