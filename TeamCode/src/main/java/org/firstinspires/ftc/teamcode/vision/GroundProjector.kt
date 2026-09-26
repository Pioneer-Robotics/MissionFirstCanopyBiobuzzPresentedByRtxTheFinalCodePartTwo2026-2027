package org.firstinspires.ftc.teamcode.vision

import org.firstinspires.ftc.teamcode.helpers.Pose
import org.opencv.calib3d.Calib3d
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point

/**
 * A class for projecting image coordinates to ground coordinates.
 */
class GroundProjector(
    imagePoints: List<Point>, // List of calibration points in the image
    groundPoints: List<Point> // List of corresponding points on the ground
) {
    private val homography: Mat

    init {
        require(imagePoints.size >= 4) {
            "Need at least 4 calibration points"
        }

        require(imagePoints.size == groundPoints.size) {
            "Image and ground point counts must match"
        }

        val src = MatOfPoint2f()
        val dst = MatOfPoint2f()

        src.fromList(imagePoints)
        dst.fromList(groundPoints)

        homography = Calib3d.findHomography(
            src,
            dst,
            Calib3d.RANSAC,
            3.0
        )

        require(!homography.empty()) {
            "Failed to calculate homography"
        }

        src.release()
        dst.release()
    }

    /**
     * Convert an image pixel to a position on the ground.
     */
    fun project(u: Double, v: Double): Pose {
        val h = DoubleArray(9)
        homography.get(0, 0, h)

        // H * (u,v,1)
        val w = h[6] * u + h[7] * v + h[8]
        val x = (h[0] * u + h[1] * v + h[2]) / w
        val y = (h[3] * u + h[4] * v + h[5]) / w

        return Pose(x, y)
    }

    fun release() {
        homography.release()
    }
}
