package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Constants

class FlowerRamp(
    private val hardwareMap: HardwareMap,
    private val servoName1: String = Constants.HardwareNames.FLOWER_SERVO_1,
    private val servoName2: String = Constants.HardwareNames.FLOWER_SERVO_2
) : HardwareComponent {

    private lateinit var servoPair: ServoPair

    override fun init() {
        servoPair = ServoPair(hardwareMap,servoName1,servoName2)
    }

    fun down() {
        servoPair.position = Constants.ServoPositions.FLOWER_DOWN
    }

    fun up() {
        servoPair.position = Constants.ServoPositions.FLOWER_UP
    }
}