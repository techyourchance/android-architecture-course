package com.techyourchance.architecture.common.base64

import java.util.Base64

object Base64EncodeDecode {

    fun String.encodeToBase64(): String {
        return Base64.getUrlEncoder().encodeToString(this.toByteArray())
    }

    fun String.decodeFromBase64(): String {
        return String(Base64.getUrlDecoder().decode(this))
    }
}