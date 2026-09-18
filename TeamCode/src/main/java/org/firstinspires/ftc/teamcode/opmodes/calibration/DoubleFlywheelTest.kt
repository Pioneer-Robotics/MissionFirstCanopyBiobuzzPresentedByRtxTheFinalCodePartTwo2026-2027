package org.firstinspires.ftc.teamcode.opmodes.calibration

import com.acmerobotics.dashboard.FtcDashboard
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.hardware.DualFlywheel
import org.firstinspires.ftc.teamcode.helpers.FileLogger

@TeleOp
class DoubleFlywheelTest : OpMode() {

    private lateinit var flywheel: DualFlywheel
    val dashboardTelemetry = FtcDashboard.getInstance().telemetry

    override fun init() {
        flywheel = DualFlywheel(hardwareMap, "motor1", "motor2").apply{init()}
    }

    override fun loop() {
        flywheel.update()

        flywheel.setVelocity(gamepad1.right_trigger.toDouble()*2500)
        flywheel.k = 0.3*gamepad1.left_stick_x + 1

        val currents = flywheel.getCurrents()

        FileLogger.info("Flywheel Velocity", "${flywheel.getVelocity()}")

        telemetry.addData("Velocity", flywheel.getVelocity())
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