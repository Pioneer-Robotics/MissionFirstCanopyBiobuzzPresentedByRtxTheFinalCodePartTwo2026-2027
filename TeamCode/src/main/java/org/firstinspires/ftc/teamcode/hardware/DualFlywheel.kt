package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.helpers.Pose
import kotlin.math.abs

class DualFlywheel(
    private val hardwareMap: HardwareMap,
    private val motor1Name: String = Constants.HardwareNames.FLYWHEEL1,
    private val motor2Name: String = Constants.HardwareNames.FLYWHEEL2,
) : HardwareComponent {
    private lateinit var leader: DcMotorEx
    private lateinit var follower: DcMotorEx

    var useLeader = true
        private set
    private var useVel = true
    private var target = 0.0

    var k = 1.0

    override fun init() {
        leader = hardwareMap.get(DcMotorEx::class.java, motor1Name)
        follower = hardwareMap.get(DcMotorEx::class.java, motor2Name)

        leader.direction = DcMotorSimple.Direction.FORWARD
        follower.direction = DcMotorSimple.Direction.REVERSE

        // Leader: closed-loop velocity control via the hub's internal PIDF.
        leader.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        leader.mode = DcMotor.RunMode.RUN_USING_ENCODER

        // Follower: open-loop. It does NOT run its own encoder-based velocity.
        follower.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        leader.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        follower.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
    }

    /** Commands the flywheel to spin at a target velocity (encoder ticks/sec). */
    fun setVelocity(ticksPerSecond: Double) {
        target = ticksPerSecond
        if (useLeader) {
            leader.velocity = target
        }
        useVel = true
    }

    fun setPower(power: Double) {
        target = power
        if (useLeader) {
            leader.power = power
        }

        useVel = false
    }

    override fun update() {
        if (useLeader) {
            follower.power = leader.power * k
        } else {
            if (useVel) {
                leader.velocity = target
                follower.velocity = target
            } else {
                leader.power = target
                follower.power = target
            }
        }
    }

    fun getVelocity(): Double = leader.velocity

    fun isAtVelocity(target: Double, toleranceTicksPerSec: Double = 50.0): Boolean =
        abs(leader.velocity - target) <= toleranceTicksPerSec

    fun getCurrents(): Pair<Double, Double> {
        val unit = CurrentUnit.AMPS
        return Pair(leader.getCurrent(unit), follower.getCurrent(unit))
    }

    fun useLeaderFollower() {
        useLeader = true

        leader.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        leader.mode = DcMotor.RunMode.RUN_USING_ENCODER
        follower.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        stop()
    }

    fun useIndependent() {
        useLeader = false

        leader.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        leader.mode = DcMotor.RunMode.RUN_USING_ENCODER
        follower.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        follower.mode = DcMotor.RunMode.RUN_USING_ENCODER
        stop()
    }

    fun toggleLeader() {
        if (useLeader) {
            useIndependent()
        } else {
            useLeaderFollower()
        }
    }

    fun stop() {
        leader.power = 0.0
        follower.power = 0.0
    }

    fun distToVel(dist: Double): Double {
        return 2150.0 // TODO: Create regression for velocity
    }

    fun shootAtHive(robotPose: Pose) {
        val target = if (robotPose.y < 180) Pose(140.0, 140.0) else Pose (140.0, 220.0)
        val dist = (target - robotPose).getLength()
        val velocity = distToVel(dist)

        setVelocity(velocity)
    }

    // Very basic, assumes a constant flight time of 0.6 seconds
    // (not unreasonable given farther shot = higher power and faster ball).
    fun shootAtHiveMoving(robotPose: Pose) {
        val target = if (robotPose.y < 180) Pose(140.0, 140.0) else Pose (140.0, 220.0)
        val dx = target.x - robotPose.x
        val dy = target.y - robotPose.y

        val robotVel = Pose(robotPose.vx, robotPose.vy)
        val parDir = Pose(dx, dy).normalize()
        val velPar = robotVel.proj(parDir) // Effective velocity of the robot that matters for flywheel speed

        val flightTime = 0.6

        // After its flight, the robot's velocity will mess up the position of the ball by this much
        val flightDisplacement = velPar * flightTime
        val effTarget = target - flightDisplacement

        val dist = (effTarget - robotPose).getLength()
        val velocity = distToVel(dist)

        setVelocity(velocity)
    }
}
