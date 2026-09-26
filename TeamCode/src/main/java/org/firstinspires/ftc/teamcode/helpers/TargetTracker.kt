package org.firstinspires.ftc.teamcode.helpers

import kotlin.math.cos
import kotlin.math.sin

/**
 * Uses a Kalman filter to track object positions on the field.
 */
class TargetTracker(processNoiseStd: Double, measurementNoiseStd: Double) {
    private val filterX = KalmanFilter1D(processNoiseStd = processNoiseStd, measurementNoiseStd = measurementNoiseStd)
    private val filterY = KalmanFilter1D(processNoiseStd = processNoiseStd, measurementNoiseStd = measurementNoiseStd)

    private var lastUpdateTimeMs: Long = 0  // time of the last actual detection
    private var lastPredictTimeMs: Long = 0 // time of the last prediction step
    private var hasEverSeenTarget = false

    /**
     * Call once per loop iteration
     */
    fun update() {
        if (!hasEverSeenTarget) return
        val nowMs = System.currentTimeMillis()
        val dtSeconds = (nowMs - lastPredictTimeMs) / 1000.0
        if (dtSeconds <= 0.0) return
        filterX.predict(dtSeconds)
        filterY.predict(dtSeconds)
        lastPredictTimeMs = nowMs
    }

    /** Call only when the vision pipeline produces a valid detection.
     *  robotPoseAtCapture should be the robot's field pose AT THE FRAME'S CAPTURE
     *  TIME, not necessarily the current pose due to latency. */
    fun onDetection(cameraEstimatedPose: Pose, robotPoseAtCapture: Pose) {
        update()

        val fieldX = robotPoseAtCapture.x + cameraEstimatedPose.x
        val fieldY = robotPoseAtCapture.y + cameraEstimatedPose.y

        filterX.update(fieldX)
        filterY.update(fieldY)

        val nowMs = System.currentTimeMillis()
        lastUpdateTimeMs = nowMs
        lastPredictTimeMs = nowMs
        hasEverSeenTarget = true
    }

    fun getX(): Double = filterX.position
    fun getY(): Double = filterY.position

    /** Estimated field-frame velocity of the target (units/sec), inferred by the filter. */
    fun getVelocityX(): Double = filterX.velocity
    fun getVelocityY(): Double = filterY.velocity

    /** Grows the longer the target goes unseen. */
    fun getPositionVarianceX(): Double = filterX.positionVariance
    fun getPositionVarianceY(): Double = filterY.positionVariance

    fun msSinceLastUpdate(): Long = if (hasEverSeenTarget) System.currentTimeMillis() - lastUpdateTimeMs else Long.MAX_VALUE
    fun hasTarget(): Boolean = hasEverSeenTarget

    fun reset() {
        hasEverSeenTarget = false
        lastUpdateTimeMs = 0
        lastPredictTimeMs = 0
    }
}