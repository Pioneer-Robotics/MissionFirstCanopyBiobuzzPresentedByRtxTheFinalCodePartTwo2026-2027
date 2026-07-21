package org.firstinspires.ftc.teamcode.hardware

import android.util.Size
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.VisionProcessor
import org.firstinspires.ftc.teamcode.Constants
import kotlin.jvm.java

/**
 * Manages the robot webcam and optional [VisionProcessor]s through a [VisionPortal].
 *
 * Call [init] once before use to open the camera and attach processors, then call [close]
 * when the OpMode ends to release resources. Use [getProcessor] to retrieve a configured
 * processor instance by type.
 */
class Camera(
    private val hardwareMap: HardwareMap,
    private val cameraName: String = Constants.HardwareNames.WEBCAM,
    val processors: Array<VisionProcessor> = emptyArray(),
) : HardwareComponent {
    private lateinit var portal: VisionPortal

    override fun init() {
        portal =
            VisionPortal
                .Builder()
                .setCamera(hardwareMap.get(WebcamName::class.java, cameraName))
                .setCameraResolution(Size(1280, 720))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .enableLiveView(true)
                .apply {
                    if (processors.isNotEmpty()) {
                        addProcessors(*processors)
                    }
                }.build()
    }

    // Helper function to get a specific processor by type
    inline fun <reified T : VisionProcessor> getProcessor(): T? = processors.filterIsInstance<T>().firstOrNull()

    fun close() {
        portal.close()
    }
}
