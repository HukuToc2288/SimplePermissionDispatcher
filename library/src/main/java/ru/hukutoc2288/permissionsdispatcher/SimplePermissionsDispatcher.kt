package ru.hukutoc2288.permissionsdispatcher

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private var currentRequestCode = 0

abstract class SimplePermissionsDispatcher(internal val permission: String) {

    private val requestCode = currentRequestCode++
    internal lateinit var currentRunnable: () -> Unit

    fun executeWithPermission(context: Activity, runnable: () -> Unit) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            runnable()
        } else {
            currentRunnable = runnable
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                onShowRationale()
            } else {
                ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)
            }
        }
    }

    fun onRequestPermissionsResult(context: Activity, grantResults: IntArray) {
        if (grantResults.isEmpty()) {
            // something seems to be broken, don't process this
            return
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            currentRunnable()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                onPermissionDenied()
            } else {
                onNeverAskAgain()
            }
        }
    }

    fun proceedAfterRationale(context: Activity) {
        ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)
    }

    abstract fun onPermissionDenied()

    abstract fun onNeverAskAgain()

    abstract fun onShowRationale()
}