package org.firstinspires.ftc.teamcode.opmodes.calibration

import com.acmerobotics.dashboard.FtcDashboard
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.hardware.DualFlywheel
import org.firstinspires.ftc.teamcode.helpers.FileLogger
import org.firstinspires.ftc.teamcode.helpers.Toggle

@TeleOp
class DoubleFlywheelTest : OpMode() {

    private lateinit var flywheel: DualFlywheel
    val dashboardTelemetry = FtcDashboard.getInstance().telemetry
    var targetVelocity = 0.0

    var flywheelToggle: Toggle = Toggle(false)

    override fun init() {
        flywheel = DualFlywheel(hardwareMap, "motor1", "motor2").apply{init()}
    }

    override fun loop() {

        if (gamepad1.dpadUpWasPressed()){
            targetVelocity += 50
        }

        if (gamepad1.dpadDownWasPressed()){
            targetVelocity -= 50
        }

        flywheelToggle.toggle(gamepad1.square)

        flywheel.update()

        if (flywheelToggle.state){
            flywheel.setVelocity(targetVelocity)
            flywheel.k = 0.3*gamepad1.left_stick_x + 1
        } else {
            flywheel.setVelocity(0.0)
        }


        val currents = flywheel.getCurrents()

        FileLogger.info("Flywheel Velocity", "${flywheel.getVelocity()}")

        telemetry.addData("Velocity", flywheel.getVelocity())
        telemetry.addData("Target Velocity", targetVelocity)
        telemetry.addData("Follow multiplier", flywheel.k)
        telemetry.addData("Current 1", currents.first)
        telemetry.addData("Current 2", currents.second)
        telemetry.update()

        dashboardTelemetry.addData("Velocity", flywheel.getVelocity())
        dashboardTelemetry.addData("Current 1", currents.first)
        dashboardTelemetry.addData("Current 2", currents.second)
        dashboardTelemetry.update()
    }

    override fun stop() {
        FileLogger.flush()
    }
}