package org.firstinspires.ftc.teamcode.opmodes.calibration

import com.acmerobotics.dashboard.FtcDashboard
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.hardware.DualFlywheel
import org.firstinspires.ftc.teamcode.helpers.FileLogger
import org.firstinspires.ftc.teamcode.helpers.Toggle
import org.firstinspires.ftc.teamcode.helpers.next

@TeleOp
class DoubleFlywheelTest : OpMode() {

    private lateinit var flywheel: DualFlywheel
    val dashboardTelemetry = FtcDashboard.getInstance().telemetry
    var targetVelocity = 0.0
    var power = 0.0

    var flywheelToggle: Toggle = Toggle(false)
    var velToggle: Toggle = Toggle(true)

    enum class TelemetryPage {
        BASIC,
        CONTROLS,
        INFO,
        ALL
    }

    var telemetryPage = TelemetryPage.BASIC

    val timer = ElapsedTime()
    var spinupTime = 0.0
    var launched = false

    override fun init() {
        flywheel = DualFlywheel(hardwareMap, "motor1", "motor2").apply{init()}
    }

    override fun loop() {

        val t = System.nanoTime()

        val speedMulti = if (gamepad1.right_bumper) 5.0 else if (gamepad1.left_bumper) 0.2 else 1.0

        if (gamepad1.dpadUpWasPressed()){
            targetVelocity += 50 * speedMulti
            power += 0.05 * speedMulti
        }

        if (gamepad1.dpadDownWasPressed()){
            targetVelocity -= 50 * speedMulti
            power -= 0.05 * speedMulti
        }

        if (gamepad1.crossWasPressed()) {
            power = 0.0
            targetVelocity = 0.0
            flywheel.setPower(0.0)
            flywheel.setVelocity(0.0)
        }

        velToggle.toggle(gamepad1.cross)
        flywheelToggle.toggle(gamepad1.square)

        if (gamepad1.triangleWasPressed()) {
            flywheel.toggleLeader()
        }

        flywheel.update()

        if (flywheelToggle.state){
            if (velToggle.state) {
                flywheel.setVelocity(targetVelocity)
            } else {
                flywheel.setPower(power)
            }
            flywheel.k = 0.3*gamepad1.left_stick_x + 1
        } else {
            flywheel.setVelocity(0.0)
        }


        val currents = flywheel.getCurrents()

        FileLogger.info("Flywheel Velocity", "${flywheel.getVelocity()}")

        if (gamepad1.circleWasPressed()) {
            telemetryPage = telemetryPage.next()
        }

        val runtime = Runtime.getRuntime()
        val memTot = runtime.totalMemory()
        val memUsed = memTot - runtime.freeMemory()

        val dt = (System.nanoTime() - t).toDouble() / 1000000.0

        if (flywheel.getVelocity() < targetVelocity - 70) {
            launched = true
            timer.reset()
        }

        if (flywheel.getVelocity() > targetVelocity && launched) {
            spinupTime = timer.milliseconds()
            launched = false
        }

        when (telemetryPage) {
            TelemetryPage.BASIC -> {
                telemetry.addData("Running", flywheelToggle.state)
                telemetry.addData("Velocity", flywheel.getVelocity())
                if (velToggle.state) {
                    telemetry.addData("Target Velocity", targetVelocity)
                } else {
                    telemetry.addData("Power", power)
                }
                telemetry.addLine("Press CIRCLE (Page 1/4)")
            }

            TelemetryPage.CONTROLS -> {
                telemetry.addLine("Press DPAD_UP to increase target velocity/power")
                telemetry.addLine("Press DPAD_DOWN to decrease target velocity/power")
                telemetry.addLine("Press SQUARE to start/stop flywheel")
                telemetry.addLine("Press CROSS to switch velocity/power")
                telemetry.addLine("Press TRIANGLE to toggle leader/follower system")
                telemetry.addLine("Press CIRCLE (Page 2/4)")
            }

            TelemetryPage.INFO -> {
                telemetry.addData("Running", flywheelToggle.state)
                telemetry.addData("Velocity", flywheel.getVelocity())
                telemetry.addData("Spinup Time", spinupTime)
                if (velToggle.state) {
                    telemetry.addData("Target Velocity", targetVelocity)
                } else {
                    telemetry.addData("Power", power)
                }
                if (flywheel.useLeader) {
                    telemetry.addLine("Dual Motor System: Leader/Follower")
                } else {
                    telemetry.addLine("Dual Motor System: Independent")
                }
                telemetry.addData("Follow multiplier", flywheel.k)
                telemetry.addData("Current 1", currents.first)
                telemetry.addData("Current 2", currents.second)

                telemetry.addData("Loop dt", dt)
                telemetry.addData("Used memory", memUsed)
                telemetry.addData("Total memory", memTot)

                telemetry.addLine("Press CIRCLE (Page 3/4)")
            }

            TelemetryPage.ALL -> {
                telemetry.addData("Running", flywheelToggle.state)
                telemetry.addData("Velocity", flywheel.getVelocity())
                telemetry.addData("Spinup Time", spinupTime)
                if (velToggle.state) {
                    telemetry.addData("Target Velocity", targetVelocity)
                } else {
                    telemetry.addData("Power", power)
                }
                if (flywheel.useLeader) {
                    telemetry.addLine("Dual Motor System: Leader/Follower")
                } else {
                    telemetry.addLine("Dual Motor System: Independent")
                }
                telemetry.addData("Follow multiplier", flywheel.k)
                telemetry.addData("Current 1", currents.first)
                telemetry.addData("Current 2", currents.second)

                telemetry.addData("Loop dt", dt)
                telemetry.addData("Used memory", memUsed)
                telemetry.addData("Total memory", memTot)

                telemetry.addLine("Press DPAD_UP to increase target velocity/power")
                telemetry.addLine("Press DPAD_DOWN to decrease target velocity/power")
                telemetry.addLine("Press SQUARE to start/stop flywheel")
                telemetry.addLine("Press CROSS to switch velocity/power")
                telemetry.addLine("Press TRIANGLE to toggle leader/follower system")

                telemetry.addLine("Press CIRCLE (Page 4/4)")
            }
        }

        telemetry.update()

        dashboardTelemetry.addData("Velocity", flywheel.getVelocity())
        dashboardTelemetry.addData("Current 1", currents.first)
        dashboardTelemetry.addData("Current 2", currents.second)
        dashboardTelemetry.addData("Loop dt", dt)
        dashboardTelemetry.addData("Used memory", memUsed)
        dashboardTelemetry.update()
    }

    override fun stop() {
        FileLogger.flush()
    }
}