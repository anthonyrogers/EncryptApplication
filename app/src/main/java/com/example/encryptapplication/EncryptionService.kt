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
    val sharedPref: SharedPreferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    var myKeyPair: ArrayList<KeyPair> = ArrayList()

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): EncryptionService = this@EncryptionService

    }

    override fun onBind(intent: Intent): IBinder {

        return binder
    }

    fun storePublicKey(name: String, key: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(name, key)
        editor.apply()
    }

    fun generateKey(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(1024)
        myKeyPair.add(keyGen.genKeyPair())
        return keyGen.genKeyPair()
    }

    fun getPublicKey(name: String): String?{
        return sharedPref.getString(name, null)
    }

    fun resetMyKeyPair(){
        myKeyPair.clear()
        generateKey()
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
        val publicKeySpec = X509EncodedKeySpec(Base64.decode(key,0))
        return keyFactory.generatePublic(publicKeySpec)
    }

}
