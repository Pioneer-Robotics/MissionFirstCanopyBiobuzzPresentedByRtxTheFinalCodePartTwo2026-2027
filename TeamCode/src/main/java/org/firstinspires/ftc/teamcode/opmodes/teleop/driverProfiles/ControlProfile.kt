package org.firstinspires.ftc.teamcode.opmodes.teleop.driverProfiles

interface ControlProfile {

    // Driver 1

    val drive: Float
    val strafe: Float
    val turn: Float

    val incDrivePower: Boolean
    val decDrivePower: Boolean

    val toggleFieldCentric: Boolean

    val intake: Boolean
    val endAutoStopIntake: Boolean
    val reverseAll: Boolean
    val reverseIntake: Boolean

    val resetOdo: Boolean

    val flowerRamp: Boolean

    // Driver 2

    val shoot: Boolean
    val shootTriggered: Boolean

    val toggleAutoTrack: Boolean
    val incManTrack: Float
    val decManTrack: Float

    val toggleFlywheel: Boolean
    val toggleAutoFlywheel: Boolean
    val incManFlywheel: Boolean
    val decManFlywheel: Boolean
}