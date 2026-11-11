package org.firstinspires.ftc.teamcode.opmodes.calibration

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.hardware.Turret
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.helpers.Toggle
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

@TeleOp
class TurretTest : OpMode() {
    val FIELD_SIZE = 144.0 // inches
    val HALF_FIELD = FIELD_SIZE / 2
    val POINT_RADIUS = 3.0
    val TOUCHPAD_SENSITIVITY = 72.0

    fun fieldToDashboard(pose: Pose): Pose {
        return Pose(pose.y - HALF_FIELD, -(pose.x - HALF_FIELD))
    }

    val dashboard: FtcDashboard = FtcDashboard.getInstance()

    var fingerWasDown = false
    var lastTouchX = 0.0
    var lastTouchY = 0.0
    val poseToggle = Toggle(true)

    private lateinit var turret: Turret

    var robotPose = Pose(HALF_FIELD, HALF_FIELD, theta=0.0)
    var goalPose = Pose(HALF_FIELD, HALF_FIELD, theta=0.0)

    override fun init() {
        turret = Turret(hardwareMap).apply { init() }
        turret.gotoPosition(0.0)
    }

    override fun loop() {
        poseToggle.toggle(gamepad1.circle)
        updatePointFromTouchpad()

        val (plotXGoal, plotYGoal) = fieldToDashboard(goalPose)
        val (plotXRobot, plotYRobot) = fieldToDashboard(robotPose)

        val packet = TelemetryPacket()
        val field = packet.fieldOverlay()

        field.setStroke("#00FF00")
        field.setFill("#00FF00")
        field.fillCircle(plotXGoal, plotYGoal, POINT_RADIUS)
        field.setStroke("#FF0000")
        field.setFill("#FF0000")
        field.fillCircle(plotXRobot, plotYRobot, POINT_RADIUS)

        dashboard.sendTelemetryPacket(packet)

        if (abs(gamepad1.right_stick_x) > 0.02) {
            robotPose += Pose(theta=0.01*gamepad1.right_stick_x)
        }

        turret.aimAtPos(robotPose, goalPose)
        val dx = goalPose.x - robotPose.x
        val dy = goalPose.y - robotPose.y

        telemetry.addData("Robot Pose", robotPose)
        telemetry.addData("Goal Pose", goalPose)
        telemetry.addData("Turret Angle", turret.getAngle())
        telemetry.addData("Turret Position", turret.getPosition())
        telemetry.addData("Controlling Pose", if (poseToggle.state) "Goal Pose" else "Robot Pose")
        telemetry.update()
    }


    private fun updatePointFromTouchpad() {
        val fingerDown = gamepad1.touchpad_finger_1

        if (fingerDown) {
            val touchX = gamepad1.touchpad_finger_1_x.toDouble()
            val touchY = gamepad1.touchpad_finger_1_y.toDouble()

            if (fingerWasDown) {
                val dx = touchX - lastTouchX
                val dy = touchY - lastTouchY

                if (poseToggle.state) {
                    val goalPoseX = (goalPose.x + (dx * TOUCHPAD_SENSITIVITY / 2.0)).coerceIn(0.0, FIELD_SIZE)
                    val goalPoseY = (goalPose.y + (dy * TOUCHPAD_SENSITIVITY / 2.0)).coerceIn(0.0, FIELD_SIZE)
                    goalPose = Pose(goalPoseX, goalPoseY)
                } else {
                    val robotPoseX = (robotPose.x + (dx * TOUCHPAD_SENSITIVITY / 2.0)).coerceIn(0.0, FIELD_SIZE)
                    val robotPoseY = (robotPose.y + (dy * TOUCHPAD_SENSITIVITY / 2.0)).coerceIn(0.0, FIELD_SIZE)
                    robotPose = Pose(robotPoseX, robotPoseY, robotPose.theta)
                }
            }

            lastTouchX = touchX
            lastTouchY = touchY
        }

        fingerWasDown = fingerDown
    }
}