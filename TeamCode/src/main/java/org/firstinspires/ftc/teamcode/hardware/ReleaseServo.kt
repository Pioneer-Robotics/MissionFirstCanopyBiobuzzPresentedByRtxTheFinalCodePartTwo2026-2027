package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class ReleaseServo(
    private val hardwareMap: HardwareMap,
    private val servoName: String = "releaseServo"
) : HardwareComponent {

    private lateinit var servo: Servo

    override fun init() {
        servo = hardwareMap.get(Servo::class.java, servoName)
    }

    fun open() {
        servo.position = 1.0
    }

    fun close() {
        servo.position = 0.0
    }

    
}