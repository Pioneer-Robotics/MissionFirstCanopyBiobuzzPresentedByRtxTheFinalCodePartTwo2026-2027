package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import kotlin.math.abs

class DualFlywheel(
    private val hardwareMap: HardwareMap,
    private val motor1Name: String,
    private val motor2Name: String,
) : HardwareComponent {
    private lateinit var leader: DcMotorEx
    private lateinit var follower: DcMotorEx

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
        leader.velocity = ticksPerSecond
    }

    override fun update() {
        follower.power = leader.power
    }

    fun getVelocity(): Double = leader.velocity

    fun isAtVelocity(target: Double, toleranceTicksPerSec: Double = 50.0): Boolean =
        abs(leader.velocity - target) <= toleranceTicksPerSec

    fun getCurrents(): Pair<Double, Double> {
        val unit = CurrentUnit.AMPS
        return Pair(leader.getCurrent(unit), follower.getCurrent(unit))
    }

    fun stop() {
        leader.power = 0.0
        follower.power = 0.0
    }
}
