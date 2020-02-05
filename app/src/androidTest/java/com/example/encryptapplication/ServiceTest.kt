package com.example.encryptapplication

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ServiceTestRule
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsInstanceOf.any
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import java.security.KeyPair
import java.util.*
import java.util.concurrent.TimeoutException

class ServiceTest {


    lateinit var service: EncryptionService

    @get:Rule
    val serviceRule = ServiceTestRule()

    @Before
    @Throws(TimeoutException::class)
    fun testWithBoundService() {
        val serviceIntent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            EncryptionService::class.java)

        val binder: IBinder = serviceRule.bindService(serviceIntent)
        service = (binder as EncryptionService.LocalBinder).getService()
    }

    @Test
    @Throws(TimeoutException::class)
    fun canGetKeyPair() {

        val service = service
        val keyPair = service.generateKey()

        assertNotNull(keyPair)
    }

    @Test
    @Throws(TimeoutException::class)
    fun canEncrypt() {

        val service = service
        val keyPair = service.generateKey()
        val text = "this text will be encrypted"
        val byteArr = service.encrypt(text, keyPair.public)
        val decrypted = service.decrypt(byteArr, keyPair.private)
        assertEquals(text, decrypted)
    }


    @Test
    @Throws(TimeoutException::class)
    fun canSavePublicKey() {
        val service = service
        val keyPair = service.generateKey()

        service.savePublicKey("Anthony", keyPair.public)
        val pubKey = service.getPublicKey("Anthony")
        val key = service.getPublicKeyFromString(pubKey.toString())
        assertEquals(keyPair.public, key)
    }


    @Test
    @Throws(TimeoutException::class)
    fun resetKey() {
        val service = service
        service.resetKey("Anthony")

       assertNull(service.getPublicKey("Anthony"))
    }
}