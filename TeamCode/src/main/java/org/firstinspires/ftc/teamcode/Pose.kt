package org.firstinspires.ftc.teamcode

import kotlin.math.hypot

class Pose(var x: Double, var y: Double) {
    fun distanceTo(other: Pose): Double {
        return hypot(other.x-this.x, other.y-this.y)
    }
}
