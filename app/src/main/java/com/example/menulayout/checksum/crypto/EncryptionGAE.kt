package com.example.menulayout.checksum.crypto

interface EncryptionGAE {
    @Throws(Exception::class)
    fun encryptGAE(var1: String, var2: String): String

    @Throws(Exception::class)
    fun decryptGAE(var1: String, var2: String): String
}
