package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.hardware.DualFlywheel
import org.firstinspires.ftc.teamcode.hardware.Intake
import org.firstinspires.ftc.teamcode.hardware.MecanumBase
import org.firstinspires.ftc.teamcode.hardware.Transfer
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.helpers.Toggle

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

    override fun init() {
        mecanumBase = MecanumBase(hardwareMap)
        intake = Intake(hardwareMap)
        transfer = Transfer(hardwareMap)
        flywheel = DualFlywheel(hardwareMap)
    }

    override fun loop() {
        drive()
        updateDrivePower()
        handleIntake()
        handleTransferToShoot()
        handleFlywheel()
    }

    private fun drive() {
        val direction = Pose(gamepad1.left_stick_x.toDouble(), -gamepad1.left_stick_y.toDouble())
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
        incDrivePower.toggle(gamepad1.right_bumper)
        decDrivePower.toggle(gamepad1.left_bumper)
        if (incDrivePower.justChanged) {
            drivePower += 0.1
        }
        if (decDrivePower.justChanged) {
            drivePower -= 0.1
        }
        drivePower = drivePower.coerceIn(0.1, 1.0)
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
}