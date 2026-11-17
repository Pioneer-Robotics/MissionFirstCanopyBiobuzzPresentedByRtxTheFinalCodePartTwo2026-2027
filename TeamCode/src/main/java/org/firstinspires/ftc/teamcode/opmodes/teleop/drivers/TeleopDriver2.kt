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

    fun onStart() {
    }

    fun update() {
    }

    fun handleShoot() {
        if (gamepad.right_bumper) {
            bot.releaseServo?.open()
            bot.transfer?.setPower(1.0) // Always move transfer when shooting
            teleop.shooting = true
        } else {
            bot.releaseServo?.close()
            teleop.shooting = false

            if (!teleop.intaking) { // When we aren't shooting, only stop transfer if we aren't intaking
                bot.transfer?.setPower(0.0)
            }
        }
    }
}
