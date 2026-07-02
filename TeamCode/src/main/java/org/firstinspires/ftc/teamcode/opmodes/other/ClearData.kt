package org.firstinspires.ftc.teamcode.opmodes.other

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.Constants

@TeleOp(name = "Clear Data", group = "Utils")
class ClearData : OpMode() {
    override fun init() {
        telemetry.addLine("Press start to manually clear all transfer data.")
        telemetry.update()
    }

    override fun loop() {
        Constants.TransferData.reset() // Reset data
        terminateOpModeNow() // Stop the op mode
    }
}
