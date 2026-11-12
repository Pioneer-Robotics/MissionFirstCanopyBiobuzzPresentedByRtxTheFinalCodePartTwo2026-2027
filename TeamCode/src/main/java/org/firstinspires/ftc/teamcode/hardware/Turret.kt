package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.helpers.Pose
import kotlin.math.PI
import kotlin.math.atan2

class Turret(
    private val hardwareMap: HardwareMap,
    private val servo1Name: String = "servo1",
    private val servo2Name: String = "servo2"
) : HardwareComponent {

    private lateinit var servoPair: ServoPair

    private val minAngleDeg = 0.0
    private val maxAngleDeg = 320.0

    override fun init() {
        servoPair = ServoPair(hardwareMap, servo1Name, servo2Name, true).apply { init() }
    }

    fun gotoPosition(position: Double) {
        servoPair.position = position
    }

    fun gotoAngle(angleDeg: Double) {

        val modAngle = (angleDeg.mod(360.0)).coerceIn(minAngleDeg, maxAngleDeg)

        // Convert to servo position 0 to 1
        val pos = (modAngle - minAngleDeg) / (maxAngleDeg - minAngleDeg)

        gotoPosition(pos)
    }

    fun getPosition(): Double {
        return servoPair.position
    }

    fun getAngle(): Double {
        return minAngleDeg + getPosition() * (maxAngleDeg - minAngleDeg)
    }

    fun aimAtPos(robotPose: Pose, goalPose: Pose) {
        val dx = goalPose.x - robotPose.x
        val dy = goalPose.y - robotPose.y

        val turretAngle = (atan2(dy, dx) - robotPose.theta)*360.0/2.0/PI
        gotoAngle(turretAngle)
    }

    fun aimAtHive(robotPose: Pose) {
        val target = if (robotPose.y < 180) Pose(140.0, 140.0) else Pose (140.0, 220.0)
        aimAtPos(robotPose, target)
    }

    // Very basic, assumes a constant flight time of 0.6 seconds
    // (not unreasonable given farther shot = higher power and faster ball).
    fun aimAtHiveMoving(robotPose: Pose) {
        val target = if (robotPose.y < 180) Pose(140.0, 140.0) else Pose (140.0, 220.0)
        val dx = target.x - robotPose.x
        val dy = target.y - robotPose.y

        val vel = Pose(robotPose.vx, robotPose.vy)

        val tangent = Pose(-dy, dx).normalize() // Direction of bot necessitating an angle change
        val velProj = vel.proj(tangent) // Effective velocity of the robot that matters for turret angle

        val flightTime = 0.6

        // After its flight, the robot's velocity will mess up the position of the ball by this much
        val flightDisplacement = velProj * flightTime
        val effTarget = target - flightDisplacement

        aimAtPos(effTarget, robotPose)
    }
}