package org.firstinspires.ftc.teamcode.opmodes.teleop.driverProfiles

import com.qualcomm.robotcore.hardware.Gamepad

class SoloControlProfile(private val gamepad1: Gamepad, private val gamepad2: Gamepad) : ControlProfile {

    // Driver 1

    override val drive get() = gamepad1.left_stick_y
    override val strafe get() = gamepad1.left_stick_x
    override val turn get() = gamepad1.right_stick_x

    override val incDrivePower get() = gamepad1.dpad_right
    override val decDrivePower get() = gamepad1.dpad_left

    override val toggleFieldCentric get() = gamepad1.touchpad

    override val intake get() = gamepad1.left_bumper
    override val endAutoStopIntake get() = gamepad1.leftBumperWasReleased()
    override val reverseAll get() = gamepad1.dpad_up
    override val reverseIntake get() = gamepad1.dpad_down

//    override val resetOdo1 get() = gamepad1.right_trigger
//    override val resetOdo2 get() = gamepad1.left_trigger
    override val resetOdo get() = gamepad1.right_trigger > 0.5 && gamepad1.left_trigger > 0.5

    override val flowerRamp get() = gamepad1.cross

    override val shoot get() = gamepad1.right_bumper
    override val shootTriggered get() = gamepad1.rightBumperWasPressed()

    override val toggleAutoTrack get() = gamepad1.square
    override val incManTrack get() = gamepad1.right_trigger
    override val decManTrack get() = gamepad1.left_trigger

    override val toggleAutoFlywheel get() = gamepad1.triangle
    override val toggleFlywheel get() = gamepad1.circle
    override val incManFlywheel get() = gamepad1.options
    override val decManFlywheel get() = gamepad1.share
}