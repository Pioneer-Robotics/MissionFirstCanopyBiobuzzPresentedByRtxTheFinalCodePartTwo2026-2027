package org.firstinspires.ftc.teamcode.opmodes.other

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.hardware.Camera
import org.firstinspires.ftc.teamcode.helpers.Chrono
import org.firstinspires.ftc.teamcode.vision.AprilTag
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import kotlin.math.abs

@Autonomous
class HiveTipTest: OpMode() {
    var turretCamera: Camera? = null
    val chrono = Chrono(false)

    // Hive tip tracking
    private val tippedDetections = ArrayDeque<Boolean>()
    private val detectionWindow = 20 // Store last N sightings
    private val minDetections = 5 // Min amount of detections required
    private var lastDetectionTime = System.nanoTime()

    override fun init() {
        turretCamera = Camera(hardwareMap, processors = arrayOf(AprilTag(draw = true).processor)).apply { init() }
    }

    override fun loop() {
        chrono.update()
        turretCamera?.update(chrono.dt)
        updateHiveVision()

        telemetry.addData("Hive Tipped", hiveIsTipped())
        telemetry.update()
    }

    private fun updateHiveVision() {
        val detections = turretCamera?.getProcessor<AprilTagProcessor>()?.detections ?: return
        for (detection in detections) {
            if (detection is AprilTagClusterDetection) {
                telemetry.addData("Cluster Detection", detection.metadata.name)
                if (!detection.metadata.name.contains("AUDIENCE")) break
                telemetry.addData("Roll", detection.ftcPose.roll)
                tippedDetections.addLast(abs(detection.ftcPose.roll) < 90.0)
                lastDetectionTime = System.nanoTime()
                while (tippedDetections.size > detectionWindow) tippedDetections.removeFirst()
                break
            }
        }
    }

    // Is the hive in a scorable position?
    private fun hiveIsTipped(): Boolean {
        if ((System.nanoTime() - lastDetectionTime) > 1e9) return false // too long since last detection
        if (tippedDetections.size < minDetections) return false // not enough data, not tipped
        return tippedDetections.count { it } > tippedDetections.size / 2 // majority wins
    }
}