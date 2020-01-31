package com.example.encryptapplication

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ServiceTestRule
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeoutException

class ServiceTest {
    @get:Rule
    val serviceRule = ServiceTestRule()

    @Test
    @Throws(TimeoutException::class)
    fun testWithBoundService() {
        // Create the service Intent.
        val serviceIntent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            EncryptionService::class.java
        ).apply {
            // Data can be passed to the service via the Intent.
            //putExtra(SEED_KEY, 42L)
        }

        // Bind the service and grab a reference to the binder.
        val binder: IBinder = serviceRule.bindService(serviceIntent)

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        val service: EncryptionService = (binder as EncryptionService.LocalBinder).getService()

        // Verify that the service is working correctly.
        //assertThat(service.getRandomInt(), `is`(any(Int::class.java)))

        service.storePublicKey("Anthonyy", "Rogers")
        //Log.e(TAG, service.getPublicKey("Anthonyy"))
    }
}