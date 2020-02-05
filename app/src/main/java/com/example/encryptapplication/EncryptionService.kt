package com.example.encryptapplication

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import android.util.Base64
import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class EncryptionService : Service() {

    // Binder given to clients
    private val binder = LocalBinder()
    val PREFS_NAME = "users"
    lateinit var sharedPref: SharedPreferences


    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): EncryptionService = this@EncryptionService

    }

    override fun onBind(intent: Intent): IBinder {
        sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return binder
    }



    fun generateKey(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(1024)
        return keyGen.genKeyPair()
    }

    fun getPublicKey(name: String): String?{
        return sharedPref.getString(name, null)
    }

    fun savePublicKey(name: String, key: PublicKey){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val keyString = Base64.encodeToString(key.encoded, Base64.DEFAULT)
        editor.putString(name, keyString)
        editor.commit()

    }

    fun resetKey(name: String){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(name)
        editor.commit()
    }

    fun encrypt(textToEncrypt: String, publicKey: PublicKey): ByteArray {
        val mCipherEncrypt = Cipher.getInstance("RSA")
        mCipherEncrypt.init(Cipher.ENCRYPT_MODE, publicKey)
        return mCipherEncrypt.doFinal(textToEncrypt.toByteArray())
    }

    fun decrypt(textToDecrypt: ByteArray, privateKey: PrivateKey): String {
        val mCipherDecrypt = Cipher.getInstance("RSA")
        mCipherDecrypt.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = mCipherDecrypt.doFinal(textToDecrypt)
        // Log.e(TAG, "works")
        return String(decryptedBytes)
    }

    fun getPublicKeyFromString(key: String): PublicKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKeySpec = X509EncodedKeySpec(Base64.decode(key, Base64.DEFAULT))
        return keyFactory.generatePublic(publicKeySpec)
    }
}
