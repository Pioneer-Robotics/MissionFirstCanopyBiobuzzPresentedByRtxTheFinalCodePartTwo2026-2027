package org.firstinspires.ftc.teamcode.biobuzz

import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.helpers.Pose

/*
                            GOAL SIDE
                |-------------------------------|
                |               +Y              |
                |               ^               |
                |               |               |
                |               |               |
BLUE ALLIANCE   |               0----> +X       |    RED ALLIANCE
                |                               |
                |                               |
                |                               |
                |                               |
                |-------------------------------|

                            AUDIENCE SIDE

    THETA = 0 FACING FORWARD
*/

// All points are defined from the RED ALLIANCE perspective
class Points(
    val color: AllianceColor,
) {
    // Function to transform a point based on alliance color
    fun Pose.T(c: AllianceColor): Pose =
        when (c) {
            AllianceColor.RED -> this
            AllianceColor.BLUE -> Pose(-this.x, this.y, theta=-this.theta)
            AllianceColor.NEUTRAL -> this
        }

    // Key positions on the field
    val ORIGIN = Pose(0.0, 0.0, 0.0)
}
