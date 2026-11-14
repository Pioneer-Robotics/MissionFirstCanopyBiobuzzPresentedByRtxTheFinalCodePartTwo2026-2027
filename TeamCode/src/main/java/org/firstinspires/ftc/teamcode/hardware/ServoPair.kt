package org.firstinspires.ftc.teamcode.hardware

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.helpers.PIDController

/**
 * Controls two linked servos as a single mechanism position.
 *
 * Call [init] before use, then set [position] to move both servos together. When [reversed]
 * is true, the second servo is driven with mirrored output for opposing linkage motion.
 */
class ServoPair(
    private val hardwareMap: HardwareMap,
    private val servo1Name: String,
    private val servo2Name: String,
    private val encoderName: String,
    private val reverseServo: Boolean = true,
    private val encoderResolution: Int = 20234, // Counts per revolution
    // TODO tune encoder resolution and pid
    private val pidConstants: Array<Double> = arrayOf(0.0001, 0.0, 0.0)
) : HardwareComponent {
    var pid = PIDController(pidConstants[0], pidConstants[1], pidConstants[2])

    private lateinit var servo1: CRServo
    private lateinit var servo2: CRServo
    private lateinit var encoder: DcMotorEx
    private var targetTicks = 0

    val position: Double
        get() = encoder.currentPosition / encoderResolution.toDouble()

    override fun init() {
        servo1 = hardwareMap.get(CRServo::class.java, servo1Name)
        servo2 = hardwareMap.get(CRServo::class.java, servo2Name)
        encoder = hardwareMap.get(DcMotorEx::class.java, encoderName)

        if (reverseServo) {
            servo1.direction = DcMotorSimple.Direction.REVERSE
            servo1.direction = DcMotorSimple.Direction.FORWARD
        } else {
            servo1.direction = DcMotorSimple.Direction.FORWARD
            servo1.direction = DcMotorSimple.Direction.FORWARD
        }
    }

    /**
     * Set a target position to run to using PID from 0-1
     */
    fun setPosition(position: Double) {
        targetTicks = (position * encoderResolution).toInt()
    }

    override fun update(dt: Double) {
        val error = targetTicks - encoder.currentPosition
        val correction = pid.update(error.toDouble(), dt)
        setPower(correction)
    }

    private fun setPower(power: Double) {
        servo1.power = power
        servo2.power = power
    }
}
