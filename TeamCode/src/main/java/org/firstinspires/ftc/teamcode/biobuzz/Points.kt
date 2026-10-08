package org.firstinspires.ftc.teamcode.biobuzz

import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.helpers.graph.plus

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

    // For Obstacles
    val flower1DR = Pair(-53.0, 168.0) + Pair(180.0, 180.0)
    val flower1DL = Pair(-67.0, 168.0) + Pair(180.0, 180.0)

    val flower2DR = Pair(168.0, 53.0) + Pair(180.0, 180.0)
    val flower2DL = Pair(168.0, 67.0) + Pair(180.0, 180.0)

    val flower3DR = Pair(53.0, -168.0) + Pair(180.0, 180.0)
    val flower3DL = Pair(67.0, -168.0) + Pair(180.0, 180.0)

    val flower4DR = Pair(-168.0, -53.0) + Pair(180.0, 180.0)
    val flower4DL = Pair(-168.0, -67.0) + Pair(180.0, 180.0)

    val hiveUR = Pair(63.0, 49.0) + Pair(180.0, 180.0)
    val hiveUL = Pair(-63.0, 49.0) + Pair(180.0, 180.0)
    val hiveDR = Pair(63.0, -49.0) + Pair(180.0, 180.0)
    val hiveDL = Pair(-63.0, -49.0) + Pair(180.0, 180.0)

    val fieldUR = Pair(180.0, 180.0) + Pair(180.0, 180.0)
    val fieldUL = Pair(-180.0, 180.0) + Pair(180.0, 180.0)
    val fieldDR = Pair(180.0, -180.0) + Pair(180.0, 180.0)
    val fieldDL = Pair(-180.0, -180.0) + Pair(180.0, 180.0)
}
