package ru.hukutoc2288.permissionsdispatcher.sample

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import ru.hukutoc2288.permissionsdispatcher.R


class MainActivity : AppCompatActivity() {
    private val LOG_TAG = "CAMERA"
    lateinit var cameras: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // Получение списка камер с устройства
            cameras = cameraManager.cameraIdList

            // выводим информацию по камере
            for (cameraID in cameras) {
                Log.i(LOG_TAG, "cameraID: $cameraID")
                val id = cameraID.toInt()

                // Получениe характеристик камеры
                val cc: CameraCharacteristics = cameraManager.getCameraCharacteristics(cameraID)
                // Получения списка выходного формата, который поддерживает камера
                val configurationMap = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                //  Определение какая камера куда смотрит
                val Faceing = cc.get(CameraCharacteristics.LENS_FACING)
                if (Faceing == CameraCharacteristics.LENS_FACING_FRONT) {
                    Log.i(LOG_TAG, "Camera with ID: $cameraID  is FRONT CAMERA  ")
                }
                if (Faceing == CameraCharacteristics.LENS_FACING_BACK) {
                    Log.i(LOG_TAG, "Camera with: ID $cameraID is BACK CAMERA  ")
                }


                // Получения списка разрешений которые поддерживаются для формата jpeg
                val sizesJPEG: Array<Size>? = configurationMap!!.getOutputSizes(ImageFormat.JPEG)
                if (sizesJPEG != null) {
                    for (item in sizesJPEG) {
                        Log.i(LOG_TAG, "w:" + item.getWidth().toString() + " h:" + item.getHeight())
                    }
                } else {
                    Log.i(LOG_TAG, "camera don`t support JPEG")
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
}