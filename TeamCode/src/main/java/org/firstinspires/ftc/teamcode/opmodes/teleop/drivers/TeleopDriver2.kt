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
    var autoTrack = Toggle(true)
    var autoFlywheel = Toggle(true)
    var defaultFlyVel = 2150.0

    fun onStart() {
    }

    fun update() {
        handleShoot()
        handleAutoTrack()
        handleManualTrack()
        handleAutoFlywheel()
        handleManualFlywheel()
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

    fun handleAutoTrack() {
        autoTrack.toggle(gamepad.touchpad)

        if (autoTrack.state) {
            bot.turret?.aimAtHive(bot.pinpoint?.pose!!)
        }
    }

    fun handleManualTrack() {
        if (!autoTrack.state) {
            val ang = bot.turret?.getAngle()!!
            val change = gamepad.right_trigger - gamepad.left_trigger
            bot.turret?.gotoAngle(ang + change)
        }
    }

    fun handleAutoFlywheel() {
        autoFlywheel.toggle(gamepad.cross)

        if (autoFlywheel.state) {
            bot.flywheel?.shootAtHive(bot.pinpoint?.pose!!)
        }
    }

    fun handleManualFlywheel() {
        if (!autoFlywheel.state) {
            if (gamepad.dpadUpWasPressed()) {
                defaultFlyVel += 50.0
            } else if (gamepad.dpadDownWasPressed()) {
                defaultFlyVel -= 50.0
            }
            bot.flywheel?.setVelocity(defaultFlyVel)
        }
    }
}
