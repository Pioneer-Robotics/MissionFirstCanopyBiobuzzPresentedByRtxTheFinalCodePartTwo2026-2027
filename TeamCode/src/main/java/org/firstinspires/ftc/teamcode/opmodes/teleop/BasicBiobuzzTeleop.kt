package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.IMU
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.hardware.DualFlywheel
import org.firstinspires.ftc.teamcode.hardware.Intake
import org.firstinspires.ftc.teamcode.hardware.MecanumBase
import org.firstinspires.ftc.teamcode.hardware.Transfer
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.helpers.Toggle
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@TeleOp(name = "Basic Biobuzz Teleop")
class BasicBioBuzzTeleop : OpMode() {
    private var drivePower = Constants.Drive.DEFAULT_POWER
    private var incDrivePower: Toggle = Toggle(false)
    private var decDrivePower: Toggle = Toggle(false)
    private var transferShootToggle: Toggle = Toggle(false)
    private var flywheelToggle: Toggle = Toggle(false)

    private lateinit var mecanumBase: MecanumBase
    private lateinit var intake: Intake
    private lateinit var transfer: Transfer
    private lateinit var flywheel: DualFlywheel
    private lateinit var imu: IMU

    override fun init() {
        mecanumBase = MecanumBase(hardwareMap).apply { init() }
        intake = Intake(hardwareMap).apply { init() }

        imu = hardwareMap.get(IMU::class.java, "imu")
        val logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
        val usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.UP;
        val orientationOnRobot = RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(IMU.Parameters(orientationOnRobot));
        imu.resetYaw()
//        transfer = Transfer(hardwareMap)
//        flywheel = DualFlywheel(hardwareMap)
    }

    override fun loop() {
        drive()
        updateDrivePower()
        handleIntake()
        handleResetYaw()
//        handleTransferToShoot()
//        handleFlywheel()

        handleTelemetry()
    }

    private fun drive() {
        var direction = Pose(gamepad1.left_stick_x.toDouble(), -gamepad1.left_stick_y.toDouble())
        var angle = atan2(direction.y, direction.x) - imu.robotYawPitchRollAngles.getYaw(AngleUnit.RADIANS)
        val mag = direction.getLength()
        direction = Pose(mag * cos(angle), mag * sin(angle))
        mecanumBase.setDrivePower(
            Pose(
                vx = direction.x,
                vy = direction.y,
                omega = gamepad1.right_stick_x.toDouble(),
            ),
            drivePower,
            Constants.Drive.MAX_MOTOR_VELOCITY_TPS
        )
    }

    private fun updateDrivePower() {
        incDrivePower.toggle(gamepad1.dpad_up)
        decDrivePower.toggle(gamepad1.dpad_down)
        if (incDrivePower.justChanged) {
            drivePower += 0.1
        }
        if (decDrivePower.justChanged) {
            drivePower -= 0.1
        }
        drivePower = drivePower.coerceIn(0.1, 1.0)
    }

    private fun handleResetYaw() {
        if ((gamepad1.left_trigger > 0.8) and (gamepad1.right_trigger > 0.8)) {
            imu.resetYaw()
        }
    }

    private fun handleIntake() {
        if (gamepad1.left_bumper) {
            intake.setPower(1.0)
        } else if (gamepad1.square) {
            intake.setPower(-1.0)
        } else {
            intake.setPower(0.0)
        }
    }

    private fun handleTransferToShoot() {
        transferShootToggle.toggle(gamepad1.right_bumper)
        if (transferShootToggle.state) {
            transfer.setPower(1.0)
        } else {
            transfer.setPower(0.0)
        }
    }

    private fun handleFlywheel() {
        flywheelToggle.toggle(gamepad1.circle)
        if (flywheelToggle.state) {
            flywheel.setVelocity(2000.0) // TODO: Get accurate velocities based on distance
        }
    }

    private fun handleTelemetry() {
        telemetry.addData("Drive Power Mult", drivePower)
        telemetry.update()
    }
}