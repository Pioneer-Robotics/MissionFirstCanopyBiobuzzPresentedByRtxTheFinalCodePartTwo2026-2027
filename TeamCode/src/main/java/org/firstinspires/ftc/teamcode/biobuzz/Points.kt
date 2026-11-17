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
BLUE ALLIANCE   |                ----> +X       |    RED ALLIANCE
                |                               |
                |                               |
                |                               |
                |                               |
                |-------------------------------|

                            AUDIENCE SIDE

    THETA = 0 FACING RIGHT, BOTTOM LEFT IS (0,0)
*/

// This game is symmetrical, the points are from the current alliance's perspective
object Points {

    // Key positions on the field
    val ORIGIN = Pose(0.0, 0.0)
    val LOW_HIVE = Pose(140.0, 140.0)
    val HIGH_HIVE = Pose(140.0, 220.0)

    val BOTTOM_LEFT_REACHABLE = Pose(30.0, 30.0)
}
