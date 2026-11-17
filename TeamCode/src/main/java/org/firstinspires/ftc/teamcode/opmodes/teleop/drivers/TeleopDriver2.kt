package org.firstinspires.ftc.teamcode.opmodes.teleop.drivers

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.helpers.Toggle
import org.firstinspires.ftc.teamcode.opmodes.teleop.Teleop

class TeleopDriver2(
    private val gamepad: Gamepad,
    private val bot: Bot,
    private val teleop: Teleop
) {

    var requestTransferOn = false

    fun onStart() {
    }

    fun update() {
        handleShoot()
    }

    fun handleShoot() {
        if (gamepad.right_bumper) {
            bot.releaseServo?.open()
            requestTransferOn = true

        } else {
            bot.releaseServo?.close()
            requestTransferOn = false

        }
    }
}
