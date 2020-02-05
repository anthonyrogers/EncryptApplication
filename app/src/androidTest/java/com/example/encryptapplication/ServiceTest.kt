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
        // Create the service Intent.
        val serviceIntent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            EncryptionService::class.java
        ).apply {
            // Data can be passed to the service via the Intent.
            // putExtra("test", 42L)
        }

        // Bind the service and grab a reference to the binder.
        val binder: IBinder = serviceRule.bindService(serviceIntent)

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        service = (binder as EncryptionService.LocalBinder).getService()

        // Verify that the service is working correctly.
        //assertThat(service.getRandomInt(), `is`(any(Int::class.java)))
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
}