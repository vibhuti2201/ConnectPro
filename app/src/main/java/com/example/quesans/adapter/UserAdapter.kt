package com.example.quesans.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quesans.R
import com.example.quesans.databinding.ItemProfileBinding
import com.example.quesans.model.User


class UserAdapter(var context:Context,var userList: ArrayList<User>):
        RecyclerView.Adapter<UserAdapter.UserViewHolder>()
{

    inner class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding: ItemProfileBinding = ItemProfileBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//        var v=LayoutInflater.from(context).inflate(R.layout.item_profile,
//        parent,true)
//        return UserViewHolder(v)
        val inflater=LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_profile,parent,false)
        return  UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.username.text=user.name
        Glide.with(context)
            .load(user.profileImage)
            .placeholder(R.drawable.user)
            .into(holder.binding.profile)
    }

    override fun getItemCount():Int =userList.size


}