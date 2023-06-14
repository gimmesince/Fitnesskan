package com.c23ps021.capstoneprojects.config

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ApiResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val result: LoginResult,

    @field:SerializedName("listStory")
    val story: List<ListStory>,

    )

data class LoginResult(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("token")
    val token: String
)

@Parcelize
data class ListStory(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("createdAt")
    val createAt: String,

    @field:SerializedName("description")
    val description: String ?= null ,

    @field:SerializedName("id")
    val id:String ?= null
): Parcelable
