package com.c23ps021.capstoneprojects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    var name: String? = null,
    var photo: Int? = null,
) : Parcelable
