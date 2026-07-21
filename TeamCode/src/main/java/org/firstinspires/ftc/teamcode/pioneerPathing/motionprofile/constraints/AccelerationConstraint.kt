package org.firstinspires.ftc.teamcode.pioneerPathing.motionprofile.constraints

/**
 * Motion profile acceleration constraint.
 */
fun interface AccelerationConstraint {
    /**
     * Returns the maximum profile acceleration at displacement [s].
     */
    operator fun get(s: Double): Double
}
