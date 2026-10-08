package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.Constants

class TransferServo (
    private val hardwareMap: HardwareMap,
    private val transferServoName: String = Constants.HardwareNames.TRANSFER_SERVO
): HardwareComponent{
    private lateinit var transferServo: CRServo

    private var power = 1.0

    override fun init() {
        transferServo = hardwareMap.get(CRServo::class.java, transferServoName)
    }

    fun turnOn() {
        transferServo.power = power
    }

    fun turnOff() {
        transferServo.power = 0.0
    }

}