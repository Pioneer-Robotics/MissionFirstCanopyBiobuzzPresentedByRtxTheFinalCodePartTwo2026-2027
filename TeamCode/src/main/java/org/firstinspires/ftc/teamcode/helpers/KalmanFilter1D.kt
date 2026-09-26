package org.firstinspires.ftc.teamcode.helpers

import kotlin.math.pow

/**
 * A 1D Constant Velocity Kalman Filter.
 *
 * State vector x = [position, velocity]
 *
 * Only position is measured directly; velocity is inferred by the filter.
 */
class KalmanFilter1D(
    initialPosition: Double = 0.0,
    initialVelocity: Double = 0.0,
    initialPositionVariance: Double = 1.0,
    initialVelocityVariance: Double = 1.0,
    private val processNoiseStd: Double = 0.1,   // std dev of random acceleration
    private val measurementNoiseStd: Double = 1.0 // std dev of position measurement noise
) {
    // State vector: [position, velocity]
    var x: DoubleArray = doubleArrayOf(initialPosition, initialVelocity)
        private set

    // State covariance matrix (2x2)
    var p: Array<DoubleArray> = arrayOf(
        doubleArrayOf(initialPositionVariance, 0.0),
        doubleArrayOf(0.0, initialVelocityVariance)
    )
        private set

    // Measurement noise variance
    private val r: Double = measurementNoiseStd.pow(2)

    /** Predict the state forward by time step [dt] (seconds). */
    fun predict(dt: Double) {
        // No control input
        // x_new = x + vel * dt
        val pos = x[0] + x[1] * dt
        val vel = x[1]
        x = doubleArrayOf(pos, vel)

        // Process noise covariance Q from a piecewise white-noise
        // acceleration model, scaled by processNoiseStd^2.
        val qScale = processNoiseStd.pow(2)
        val q00 = dt.pow(4) / 4.0 * qScale
        val q01 = dt.pow(3) / 2.0 * qScale
        val q11 = dt.pow(2) * qScale

        val f01 = dt // state transition matrix F = [[1, dt], [0, 1]]
        val p00 = p[0][0]; val p01 = p[0][1]
        val p10 = p[1][0]; val p11 = p[1][1]

        // F * P
        val fp00 = p00 + f01 * p10
        val fp01 = p01 + f01 * p11
        val fp10 = p10
        val fp11 = p11

        // (F * P) * F^T + Q
        val newP00 = fp00 + fp01 * f01 + q00
        val newP01 = fp01 + q01
        val newP10 = fp10 + fp11 * f01 + q01
        val newP11 = fp11 + q11

        // Increases uncertainty
        p = arrayOf(
            doubleArrayOf(newP00, newP01),
            doubleArrayOf(newP10, newP11)
        )
    }

    /** Update the filter with a new position [measurement]. */
    fun update(measurement: Double) {
        // H = [1, 0] since only position is measured.
        val y = measurement - x[0]      // innovation
        val s = p[0][0] + r             // innovation covariance

        // Kalman gain K = P * H^T / S
        val k0 = p[0][0] / s
        val k1 = p[1][0] / s

        // State update: x = x + K * y
        x = doubleArrayOf(x[0] + k0 * y, x[1] + k1 * y)

        // Covariance update: P = (I - K * H) * P
        val newP00 = (1 - k0) * p[0][0]
        val newP01 = (1 - k0) * p[0][1]
        val newP10 = p[1][0] - k1 * p[0][0]
        val newP11 = p[1][1] - k1 * p[0][1]

        // Decreases uncertainty
        p = arrayOf(
            doubleArrayOf(newP00, newP01),
            doubleArrayOf(newP10, newP11)
        )
    }

    val position: Double get() = x[0]
    val velocity: Double get() = x[1]
    val positionVariance: Double get() = p[0][0]
    val velocityVariance: Double get() = p[1][1]
}
