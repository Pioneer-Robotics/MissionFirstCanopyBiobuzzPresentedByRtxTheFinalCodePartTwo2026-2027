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

    private lateinit var upToggle: Toggle
    private lateinit var downToggle: Toggle
    private lateinit var onToggle: Toggle
    var vel = 0;

    override fun init() {
        flywheel = DualFlywheel(hardwareMap, "motor1", "motor2").apply{init()}
        upToggle = Toggle(false)
        downToggle = Toggle(false)
        onToggle = Toggle(false)
    }

    override fun loop() {
        flywheel.update()

        upToggle.toggle(gamepad1.right_bumper)
        downToggle.toggle(gamepad1.left_bumper)
        onToggle.toggle(gamepad1.triangle)

        if (upToggle.justChanged) { vel += 100 }
        if (downToggle.justChanged) { vel -= 100 }

        if (onToggle.state) {
            flywheel.setVelocity(vel.toDouble())
        } else {
            flywheel.setVelocity(0.0)
        }

        val currents = flywheel.getCurrents()

        FileLogger.info("Flywheel Velocity", "${flywheel.getVelocity()}")

        telemetry.addData("Target", vel)
        telemetry.addData("Velocity", flywheel.getVelocity())
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