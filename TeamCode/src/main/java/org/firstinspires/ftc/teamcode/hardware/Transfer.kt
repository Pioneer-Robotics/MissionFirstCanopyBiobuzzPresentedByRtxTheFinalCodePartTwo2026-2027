package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.Constants

class Transfer(
    private val hardwareMap: HardwareMap,
    private val motorName: String = Constants.HardwareNames.TRANSFER
) : HardwareComponent {
    private lateinit var motor: DcMotorEx

    override fun init() {
        motor = hardwareMap.get(DcMotorEx::class.java, motorName)

        motor.direction = DcMotorSimple.Direction.FORWARD
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    fun setPower(power: Double) {
        motor.power = power
    }
}