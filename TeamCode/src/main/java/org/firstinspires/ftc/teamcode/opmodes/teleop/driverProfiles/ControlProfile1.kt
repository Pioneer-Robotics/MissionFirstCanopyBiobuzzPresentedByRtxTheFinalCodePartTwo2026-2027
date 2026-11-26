package org.firstinspires.ftc.teamcode.opmodes.teleop.driverProfiles

import com.qualcomm.robotcore.hardware.Gamepad

class ControlProfile1(private val gamepad1: Gamepad, private val gamepad2: Gamepad) : ControlProfile {

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

    override val resetOdo1 get() = gamepad1.right_trigger
    override val resetOdo2 get() = gamepad1.left_trigger

    override val flowerRamp get() = gamepad1.cross

    // Driver 2

    override val shoot get() = gamepad2.right_bumper
    override val shootTriggered get() = gamepad2.rightBumperWasPressed()

    override val toggleAutoTrack get() = gamepad2.touchpad
    override val incManTrack get() = gamepad2.right_trigger
    override val decManTrack get() = gamepad2.left_trigger

    override val toggleAutoFlywheel get() = gamepad2.cross
    override val toggleFlywheel get() = gamepad2.circle
    override val incManFlywheel get() = gamepad2.dpadUpWasPressed()
    override val decManFlywheel get() = gamepad2.dpadDownWasPressed()
}