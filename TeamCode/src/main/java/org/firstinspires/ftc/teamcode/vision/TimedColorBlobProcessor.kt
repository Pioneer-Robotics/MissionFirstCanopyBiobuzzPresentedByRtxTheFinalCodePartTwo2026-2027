package org.firstinspires.ftc.teamcode.vision

import android.graphics.Canvas
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration
import org.firstinspires.ftc.vision.VisionProcessor
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor
import org.opencv.core.Mat


class TimedColorBlobProcessor(val locator: ColorBlobLocatorProcessor) : VisionProcessor {
    var lastCaptureTimeNanos: Long = 0
        private set

    override fun init(width: Int, height: Int, calibration: CameraCalibration?) {
        locator.init(width, height, calibration)
    }

    override fun processFrame(frame: Mat?, captureTimeNanos: Long): Any? {
        this.lastCaptureTimeNanos = captureTimeNanos
        return locator.processFrame(frame, captureTimeNanos)
    }

    override fun onDrawFrame(
        canvas: Canvas?,
        onscreenWidth: Int,
        onscreenHeight: Int,
        scaleBf: Float,
        scaleBfInv: Float,
        userContext: Any?
    ) {
        locator.onDrawFrame(canvas, onscreenWidth, onscreenHeight, scaleBf, scaleBfInv, userContext)
    }
}
