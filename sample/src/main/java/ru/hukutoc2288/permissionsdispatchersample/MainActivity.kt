package ru.hukutoc2288.permissionsdispatchersample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import ru.hukutoc2288.permissionsdispatcher.SimpleNeverAskDialogFragment
import ru.hukutoc2288.permissionsdispatcher.SimplePermissionsDispatcher
import ru.hukutoc2288.permissionsdispatcher.SimpleRationaleDialogFragment

class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

    private val dispatcher = object : SimplePermissionsDispatcher(Manifest.permission.CAMERA) {
        override fun onPermissionDenied() {
            Toast.makeText(applicationContext, getString(R.string.camera_denied), Toast.LENGTH_SHORT).show()
            finish()
        }

        override fun onNeverAskAgain() {
            SimpleNeverAskDialogFragment(this, getString(R.string.camera_never_again_title),
                    getString(R.string.camera_never_again_message))
                    .show(supportFragmentManager, "cameraRationale")
        }

        override fun onShowRationale() {
            SimpleRationaleDialogFragment(this, getString(R.string.camera_rationale_title),
                    getString(R.string.camera_rationale_message))
                    .show(supportFragmentManager, "cameraNeverAgain")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request camera permissions
        dispatcher.executeWithPermission(this) {
            startCamera()
        }

        // Set up the listener for take photo button
        camera_capture_button.setOnClickListener { takePhoto() }
    }

    private fun takePhoto() {}

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewFinder.surfaceProvider)
                    }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        dispatcher.onRequestPermissionsResult(this,grantResults)
    }
}