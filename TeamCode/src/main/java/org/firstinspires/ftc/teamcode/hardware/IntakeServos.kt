package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.Constants

class IntakeServos (
    private val hardwareMap: HardwareMap,
    private val intakeServoLName: String = Constants.HardwareNames.INTAKE_SERVO_L,
    private val intakeServoRName: String = Constants.HardwareNames.INTAKE_SERVO_R,
    ): HardwareComponent{
    private lateinit var servoL: CRServo
    private lateinit var servoR: CRServo

    private var power = 1.0

    override fun init() {
        servoL = hardwareMap.get(CRServo::class.java, intakeServoLName)
        servoR = hardwareMap.get(CRServo::class.java, intakeServoRName)
    }

    fun turnOn() {
        servoL.power = power
        servoR.power = power
    }

    fun turnOff() {
        servoL.power = 0.0
        servoR.power = 0.0
    }
}