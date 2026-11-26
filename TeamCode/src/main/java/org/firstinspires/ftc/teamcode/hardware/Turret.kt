package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.biobuzz.Points
import org.firstinspires.ftc.teamcode.helpers.MathUtils
import org.firstinspires.ftc.teamcode.helpers.Pose
import kotlin.math.PI
import kotlin.math.atan2

class Turret(
    private val hardwareMap: HardwareMap,
    private val servo1Name: String = Constants.HardwareNames.TURRET_SERVO_1,
    private val servo2Name: String = Constants.HardwareNames.TURRET_SERVO_2,
    private val encoderName: String = Constants.HardwareNames.TRANSFER
) : HardwareComponent {

    private lateinit var servoPair: CRServoPair

    val T = Constants.Turret

    private val minAngleDeg = T.MIN_ANGLE_DEG
    private val maxAngleDeg = T.MAX_ANGLE_DEG

    override fun init() {
        servoPair = CRServoPair(hardwareMap, servo1Name, servo2Name, encoderName, true,
            pidConstants = arrayOf(T.kP, T.kI, T.kD)).apply { init() }
    }

    override fun update(dt: Double) {
        servoPair.update(dt)
    }

    fun gotoAngle(angleDeg: Double) {
        servoPair.setAngle(MathUtils.wrap(angleDeg, Pair(minAngleDeg, maxAngleDeg)))
    }

    fun getAngle(): Double {
        return servoPair.angle
    }

    fun aimAtPos(robotPose: Pose, goalPose: Pose) {
        val dx = goalPose.x - robotPose.x
        val dy = goalPose.y - robotPose.y

        val turretAngle = (atan2(dy, dx) - robotPose.theta)*360.0/2.0/PI
        gotoAngle(turretAngle)
    }

    fun aimAtHive(robotPose: Pose) {
        val target = if (robotPose.y < 180) Points.LOW_HIVE else Points.HIGH_HIVE
        aimAtPos(robotPose, target)
    }

    // Very basic, assumes a constant flight time of 0.6 seconds
    // (not unreasonable given farther shot = higher power and faster ball).
    fun aimAtHiveMoving(robotPose: Pose) {
        val target = if (robotPose.y < 180) Points.LOW_HIVE else Points.HIGH_HIVE
        val dx = target.x - robotPose.x
        val dy = target.y - robotPose.y

        val robotVel = Pose(robotPose.vx, robotPose.vy)

        val perpDir = Pose(-dy, dx).normalize() // Direction of bot necessitating an angle change
        val velPerp = robotVel.proj(perpDir) // Effective velocity of the robot that matters for turret angle

        val flightTime = 0.6

        // After its flight, the robot's velocity will mess up the position of the ball by this much
        val flightDisplacement = velPerp * flightTime
        val effTarget = target - flightDisplacement

        aimAtPos(effTarget, robotPose)
    }
}