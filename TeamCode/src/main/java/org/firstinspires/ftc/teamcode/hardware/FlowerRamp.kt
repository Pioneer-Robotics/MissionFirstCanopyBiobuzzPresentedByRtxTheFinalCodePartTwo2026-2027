package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Constants

class FlowerRamp(
    private val hardwareMap: HardwareMap,
    private val servoName: String = Constants.HardwareNames.FLOWER_SERVO
) : HardwareComponent {

    private lateinit var servo: Servo

    override fun init() {
        servo = hardwareMap.get(Servo::class.java, servoName)
    }

    fun down() {
        servo.position = Constants.ServoPositions.FLOWER_DOWN
    }

    fun up() {
        servo.position = Constants.ServoPositions.FLOWER_UP
    }
}