package com.example.quesans

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.HttpAuthHandler
import com.example.quesans.databinding.ActivitySetUpProfileBinding
import com.example.quesans.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Date

class Set_up_profile : AppCompatActivity() {

    var binding: ActivitySetUpProfileBinding?=null
    var auth:FirebaseAuth?=null
    var database:FirebaseDatabase?=null
    var storage:FirebaseStorage?=null
    var selectedImage: Uri?=null
    var dialog:ProgressDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Updating Profile...")
        dialog!!.setCancelable(false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding!!.imageView11.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 45)
        }
        binding!!.continueBtn02.setOnClickListener {
            val name: String = binding!!.nameBox.text.toString()
            if (name.isEmpty()) {
                binding!!.nameBox.error = ("Please type a name")
            } else {
                dialog!!.show()


                if (selectedImage != null) {
                    val reference = storage!!.reference.child("Profile")
                        .child(auth!!.uid!!)
                    reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            reference.downloadUrl.addOnCompleteListener { uri ->
                                val imageUrl = uri.toString()
                                val uid = auth!!.uid
                                val phone = auth!!.currentUser!!.phoneNumber
                                val name: String = binding!!.nameBox.text.toString()
                                val user = User(uid, name, phone, imageUrl)
                                database!!.reference
                                    .child("users")
                                    .setValue(user)
                                    .addOnCompleteListener {
                                        dialog!!.dismiss()
                                        val intent =
                                            Intent(this@Set_up_profile, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                            }

                        } else {
                            val uid = auth!!.uid
                            val phone = auth!!.currentUser!!.phoneNumber
                            val name: String = binding!!.nameBox.text.toString()
                            val user = User(uid, name, phone, "No Image")
                            dialog!!.dismiss()
                            database!!.reference
                                .child("users")
                                .child(uid!!)
                                .setValue(user)
                                .addOnCanceledListener {
                                    dialog!!.dismiss()
//                                val intent= Intent(this@Set_up_profile,MainActivity::class.java)
//                                startActivity(intent)
                                    startMainActivity()
                                    finish()
                                }

                        }
                    }
                } else {
                    val uid = auth!!.uid
                    val phone = auth!!.currentUser!!.phoneNumber
                    val name: String = binding!!.nameBox.text.toString()
                    val user = User(uid, name, phone, "No Image")
                    database!!.reference
                        .child("users")
                        .child(uid!!)
                        .setValue(user)
                        .addOnCanceledListener {
                            dialog!!.dismiss()
                            startMainActivity()
                        }
                }

            }
        }
    }
            private fun startMainActivity() {
                val intent = Intent(this@Set_up_profile, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data!=null)
        {
            if(data.data!=null)
            {
                val uri=data.data//filepath
                val storage= FirebaseStorage.getInstance()
                val time= Date().time
                val reference= storage.reference
                    .child("Profile")
                    .child(time.toString()+ "")
                reference.putFile(uri!!).addOnCompleteListener{ task->
                    if(task.isSuccessful){
                        reference.downloadUrl.addOnCompleteListener { uri->
                            val filePath= uri!!.toString()
                            val obj = HashMap<String,Any>()
                            obj["image"] = filePath
                            database!!.reference
                                .child("users")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .updateChildren(obj).addOnSuccessListener {  }
                        }
                    }
                }
                binding!!.root.findViewById<CircleImageView>(R.id.imageView11).setImageURI(data.data)
                selectedImage = data.data
            }
        }
    }
}