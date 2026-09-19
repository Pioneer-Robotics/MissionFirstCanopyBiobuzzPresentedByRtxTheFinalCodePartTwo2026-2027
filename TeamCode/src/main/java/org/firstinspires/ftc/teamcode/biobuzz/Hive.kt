package org.firstinspires.ftc.teamcode.biobuzz

import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection

class Hive {
    /** Positive ftcPose.pitch is the upward-facing CELL, the only legal LAUNCH target (G417). */
    fun canShoot(
        detections: List<AprilTagDetection>,
        alliance: AllianceColor,
    ): Boolean =
        alliance != AllianceColor.NEUTRAL &&
            detections.any { d ->
                Cell.of(d)?.alliance == alliance && d.ftcPose?.let { it.pitch > 0 } == true
            }
}
