package com.c23ps021.capstoneprojects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class List (
    var squad: String? = null,
    var bicep: String? = null,
    var deadlift: String? = null,
) : Parcelable