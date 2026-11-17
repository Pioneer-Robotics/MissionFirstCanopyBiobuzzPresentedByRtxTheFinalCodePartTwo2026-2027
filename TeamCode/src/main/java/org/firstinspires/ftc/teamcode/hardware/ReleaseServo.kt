package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Constants

class ReleaseServo(
    private val hardwareMap: HardwareMap,
    private val servoName: String = Constants.HardwareNames.RELEASE_SERVO
) : HardwareComponent {

    private lateinit var servo: Servo

    override fun init() {
        servo = hardwareMap.get(Servo::class.java, servoName)
    }

    fun open() {
        servo.position = Constants.ServoPositions.RELEASE_OPEN
    }

    fun close() {
        servo.position = Constants.ServoPositions.RELEASE_CLOSE
    }

    
}