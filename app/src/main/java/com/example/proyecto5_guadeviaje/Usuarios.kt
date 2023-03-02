package com.example.proyecto5_guadeviaje

import android.graphics.Bitmap

data class Usuarios(
    var id:Int?,
    var nombre:String,
    var asig:String,
    var email:String,
    var imagen: ByteArray
):java.io.Serializable
