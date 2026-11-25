package org.firstinspires.ftc.teamcode.opmodes.teleop.driverProfiles

import com.qualcomm.robotcore.hardware.Gamepad

class ControlProfile1(private val gamepad1: Gamepad, private val gamepad2: Gamepad) : ControlProfile {

    // Driver 1

    override val drive = gamepad1.left_stick_y
    override val strafe = gamepad1.left_stick_x
    override val turn = gamepad1.right_stick_x

    override val incDrivePower = gamepad1.dpad_right
    override val decDrivePower = gamepad1.dpad_left

    override val toggleFieldCentric = gamepad1.touchpad

    override val intake = gamepad1.left_bumper
    override val endAutoStopIntake = gamepad1.leftBumperWasReleased()
    override val reverseAll = gamepad1.dpad_up
    override val reverseIntake = gamepad1.dpad_down

    override val resetOdo1 = gamepad1.right_trigger
    override val resetOdo2 = gamepad1.left_trigger

    override val flowerRamp = gamepad1.cross

    // Driver 2

    override val shoot = gamepad2.right_bumper
    override val shootTriggered = gamepad2.rightBumperWasPressed()

    override val toggleAutoTrack = gamepad2.touchpad
    override val incManTrack = gamepad2.right_trigger
    override val decManTrack = gamepad2.left_trigger

    override val toggleAutoFlywheel = gamepad2.cross
    override val incManFlywheel = gamepad2.dpadUpWasPressed()
    override val decManFlywheel = gamepad2.dpadDownWasPressed()
}