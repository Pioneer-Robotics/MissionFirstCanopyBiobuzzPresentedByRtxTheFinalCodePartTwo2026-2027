package org.firstinspires.ftc.teamcode.helpers

import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Uses a Kalman filter to track object positions on the field.
 */
class ObjectTracker(processNoiseStd: Double, measurementNoiseStd: Double) {
    private val filterX = KalmanFilter1D(processNoiseStd = processNoiseStd, measurementNoiseStd = measurementNoiseStd)
    private val filterY = KalmanFilter1D(processNoiseStd = processNoiseStd, measurementNoiseStd = measurementNoiseStd)

    private var lastUpdateTimeMs: Long = 0  // time of the last actual detection
    private var lastPredictTimeMs: Long = 0 // time of the last prediction step
    private var hasEverSeenTarget = false

    private val measurementVar = measurementNoiseStd * measurementNoiseStd

    private fun toField(cam: Pose, robot: Pose): Pair<Double, Double> {
        val c = cos(robot.theta)
        val s = sin(robot.theta)
        return Pair(
            robot.x + cam.x * c - cam.y * s,
            robot.y + cam.x * s + cam.y * c
        )
    }

    /**
     * Picks which candidate (robot-relative poses) is the tracked target.
     * Returns null if none is plausibly the same object.
     */
    fun selectDetection(
        candidates: List<Pose>,
        robotPoseAtCapture: Pose,
        gateChiSq: Double = 9.21,
        lostTimeoutMs: Long = 1500
    ): Pose? {
        if (candidates.isEmpty()) return null

        // Lost the target for too long
        if (hasEverSeenTarget && msSinceLastUpdate() > lostTimeoutMs) reset()

        // No target yet: pick the candidate closest to the robot
        if (!hasEverSeenTarget) {
            return candidates.minByOrNull { hypot(it.x, it.y) }
        }

        val varX = filterX.positionVariance + measurementVar
        val varY = filterY.positionVariance + measurementVar

        // Pick the closest to the predicted position
        var best: Pose? = null
        var bestD2 = Double.MAX_VALUE
        for (cand in candidates) {
            val (fx, fy) = toField(cand, robotPoseAtCapture)
            val dx = fx - filterX.position
            val dy = fy - filterY.position
            val d2 = dx * dx / varX + dy * dy / varY
            if (d2 < bestD2) { bestD2 = d2; best = cand }
        }

        // Decide if the closest detection is good enough
        return if (bestD2 <= gateChiSq) best else null
    }

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

    /** Call only when the vision pipeline produces a valid detection. */
    fun onDetection(cameraEstimatedPose: Pose, robotPose: Pose) {
        update()

        val (fieldX, fieldY) = toField(cameraEstimatedPose, robotPose)

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
