package org.firstinspires.ftc.teamcode.opmodes.auto

import android.util.Size
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.hardware.Camera
import org.firstinspires.ftc.teamcode.helpers.ObjectTracker
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.helpers.PoseHistory
import org.firstinspires.ftc.teamcode.localization.localizers.Pinpoint
import org.firstinspires.ftc.teamcode.vision.ColorBlob
import org.firstinspires.ftc.teamcode.vision.GroundProjector
import org.firstinspires.ftc.vision.opencv.ColorRange

@Autonomous(name = "Object Tracking")
class ObjectTracking: OpMode() {
    // Odometry
    private lateinit var pinpoint: Pinpoint
    // Camera
    private lateinit var camera: Camera
    // Processors
    private val pollenColorBlob = ColorBlob(targetColor = ColorRange.YELLOW, draw = true)
    private val nectarColorBlob = ColorBlob(targetColor = ColorRange.RED, draw = true)
    // Projection
    private val projector = GroundProjector(Constants.Camera.IMAGE_POINTS, Constants.Camera.GROUND_POINTS)
    // Tracking
    private val tracker = ObjectTracker(processNoiseStd = 0.1, measurementNoiseStd = 1.0)
    // Pose history
    private val poseHistory = PoseHistory(250)
    private var lastFrameNs = 0L

    override fun init() {
        camera = Camera(
            hardwareMap,
            processors = arrayOf(pollenColorBlob.processor, nectarColorBlob.processor),
            resolution = Size(320, 240)
        ).apply { init() }

        pinpoint = Pinpoint(hardwareMap)
    }

    override fun loop() {
        tracker.update()
        poseHistory.record(pinpoint.pose)

        val frameCaptureNs = pollenColorBlob.processor.lastCaptureTimeNanos
        if (frameCaptureNs == lastFrameNs) return // already handled this frame
        lastFrameNs = frameCaptureNs

        val robotPoseAtCapture = poseHistory.getPoseAt(frameCaptureNs) ?: return

        // Project every blob in the frame
        val candidates = pollenColorBlob.getBlobs().map { detection ->
            val px = detection.circle.x.toDouble()
            val py = (detection.circle.y + detection.circle.radius).toDouble()
            val g = projector.project(px, py)
            Pose(g.x, g.y)
        }

        val chosen = tracker.selectDetection(candidates, robotPoseAtCapture) ?: return
        tracker.onDetection(chosen, robotPoseAtCapture, frameCaptureNs)
    }
}
