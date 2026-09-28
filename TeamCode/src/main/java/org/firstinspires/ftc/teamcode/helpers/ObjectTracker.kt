package org.firstinspires.ftc.teamcode.helpers

import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Uses a Kalman filter to track object positions on the field.
 */
class ObjectTracker(
    processNoiseStd: Double,
    measurementNoiseStd: Double,
    private val historyNs: Long = 1_000_000_000L // How long to keep track of Kalman filter history
) {
    private val filterX = KalmanFilter1D(processNoiseStd = processNoiseStd, measurementNoiseStd = measurementNoiseStd)
    private val filterY = KalmanFilter1D(processNoiseStd = processNoiseStd, measurementNoiseStd = measurementNoiseStd)

    private class Snapshot(val timeNs: Long, val x: KalmanFilter1D.State, val y: KalmanFilter1D.State)
    private val history = ArrayDeque<Snapshot>()

    private var filterTimeNs = 0L // the moment the filter state currently represents
    private var lastUpdateTimeNs = 0L // capture time of the last real detection
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

    private fun record(t: Long) {
        history.addLast(Snapshot(t, filterX.getState(), filterY.getState()))
        while (history.isNotEmpty() && t - history.first().timeNs > historyNs) history.removeFirst()
    }

    private fun predictTo(tNs: Long) {
        val dt = (tNs - filterTimeNs) / 1_000_000_000.0
        if (dt <= 0.0) return
        filterX.predict(dt)
        filterY.predict(dt)
        filterTimeNs = tNs
        record(tNs)
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
        if (hasEverSeenTarget) predictTo(System.nanoTime())
    }

    /** Call only when the vision pipeline produces a valid detection.
     *  robotPoseAtCapture should be the robot's field pose AT THE FRAME'S CAPTURE
     *  TIME, not necessarily the current pose due to latency. */
    fun onDetection(cameraEstimatedPose: Pose, robotPoseAtCapture: Pose, captureTimeNs: Long) {
        val nowNs = System.nanoTime()
        val capNs = minOf(captureTimeNs, nowNs)

        // Convert robot-centric coordinates to field coordinates
        val (fieldX, fieldY) = toField(cameraEstimatedPose, robotPoseAtCapture)

        // Get the last snapshot before the capture time
        val snap = history.lastOrNull { it.timeNs <= capNs }

        if (!hasEverSeenTarget || snap == null) {
            // First detection, or frame older than history: apply at capture time, then catch up.
            filterX.initialize(fieldX)
            filterY.initialize(fieldY)
            filterTimeNs = capNs
            record(capNs)
            hasEverSeenTarget = true
        } else {
            // Times of the predict steps being removed
            val replayTimes = history.filter { it.timeNs > capNs }.map { it.timeNs }
            history.removeAll { it.timeNs > capNs }

            // Roll back, predict to the capture instant, apply the measurement there
            filterX.setState(snap.x)
            filterY.setState(snap.y)
            filterTimeNs = snap.timeNs
            predictTo(capNs)
            filterX.update(fieldX)
            filterY.update(fieldY)
            history.removeLast() // drop the pre-update snapshot at capNs
            record(capNs) // keep the post-update one

            // Replay forward
            for (t in replayTimes) predictTo(t)
        }

        predictTo(nowNs)
        lastUpdateTimeNs = capNs
    }

    fun getX(): Double = filterX.position
    fun getY(): Double = filterY.position

    /** Estimated field-frame velocity of the target (units/sec), inferred by the filter. */
    fun getVelocityX(): Double = filterX.velocity
    fun getVelocityY(): Double = filterY.velocity

    /** Grows the longer the target goes unseen. */
    fun getPositionVarianceX(): Double = filterX.positionVariance
    fun getPositionVarianceY(): Double = filterY.positionVariance

    fun msSinceLastUpdate(): Long = if (hasEverSeenTarget) (System.nanoTime() - lastUpdateTimeNs) / 1_000_000 else Long.MAX_VALUE
    fun hasTarget(): Boolean = hasEverSeenTarget

    fun reset() {
        hasEverSeenTarget = false
        lastUpdateTimeNs = 0
        filterTimeNs = 0
        history.clear()
    }
}