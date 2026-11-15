package org.firstinspires.ftc.teamcode.opmodes.calibration

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DigitalChannel

@TeleOp
class DigitalIOTest : OpMode() {

    lateinit var digit: DigitalChannel

    override fun init() {
        digit = hardwareMap.get(DigitalChannel::class.java, "digital0")
        digit.mode = DigitalChannel.Mode.INPUT
    }

    override fun loop() {
        telemetry.addData("Digital", digit.state)
        telemetry.update()
    }
}