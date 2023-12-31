package com.example.quesans




import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quesans.adapter.UserAdapter
import com.example.quesans.databinding.ActivityMainBinding
import com.example.quesans.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding?=null
    var database:FirebaseDatabase?=null
    var users:ArrayList<User>?=null
    var usersAdapter:UserAdapter?=null
    var dialog:ProgressDialog?=null
    var user:User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialog= ProgressDialog(this@MainActivity)
        dialog!!.setMessage("Uploading Image...")
        dialog!!.setCancelable(false)
        database= FirebaseDatabase.getInstance()
        users= ArrayList<User>()
        usersAdapter= UserAdapter(this@MainActivity,users!!)
        val layoutManager=GridLayoutManager(this@MainActivity,2)
        binding!!.mRec.layoutManager=layoutManager
        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   user= snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        binding!!.mRec.adapter= usersAdapter
        database!!.reference.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               users!!.clear()
                for(snapshot1 in snapshot.children){
                    val user:User?= snapshot.getValue(User:: class.java)
                    if(!user!!.uid.equals(FirebaseAuth.getInstance().uid)) users!!.add(user)
                }
                usersAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        val currentId= FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId= FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Offline")
    }
}