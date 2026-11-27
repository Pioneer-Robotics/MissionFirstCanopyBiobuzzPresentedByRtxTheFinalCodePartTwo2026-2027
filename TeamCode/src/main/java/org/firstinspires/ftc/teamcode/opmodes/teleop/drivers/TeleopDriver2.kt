package org.firstinspires.ftc.teamcode.opmodes.teleop.drivers

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.helpers.Toggle
import org.firstinspires.ftc.teamcode.opmodes.teleop.Teleop
import org.firstinspires.ftc.teamcode.opmodes.teleop.driverProfiles.ControlProfile

class TeleopDriver2(
    private val controls: ControlProfile,
    private val bot: Bot,
    private val teleop: Teleop
) {

    var requestTransferOn = false
    var stopTransfer = false
    var autoTrack = Toggle(false)
    var autoFlywheel = Toggle(false)
    var flywheelOn = Toggle(true)
    var defaultFlyVel = 2150.0
    var preShootTimeout = ElapsedTime()
    var manualTurretTarget = 0.0

    fun onStart() {
    }

    fun update() {
        handleFlywheelToggle()
        handleShoot()
        handleAutoTrack()
        handleManualTrack()
        handleAutoFlywheel()
        handleManualFlywheel()
    }

    private fun handleShoot() {

        // Shoot was triggered
        if (controls.shootTriggered) {
            preShootTimeout.reset()
        }

        if (controls.shoot) {
            bot.releaseServo?.open()

            if (preShootTimeout.milliseconds() > Constants.Turret.RELEASE_TIME_TORQUE) {
                stopTransfer = false
                requestTransferOn = true // Wait until servo has moved to push out
            } else {
                stopTransfer = true
                requestTransferOn = false
            }

        } else {
            bot.releaseServo?.close()
            requestTransferOn = false

        }
    }

    private fun handleAutoTrack() {
        autoTrack.toggle(controls.toggleAutoTrack)

        if (autoTrack.state) {
            bot.turret?.aimAtHive(bot.pinpoint?.pose!!)
        }
    }

    private fun handleManualTrack() {
        if (!autoTrack.state) {
            if (autoTrack.justChanged) {
                manualTurretTarget = bot.turret?.getAngle() ?: 0.0
            }
            val change = controls.incManTrack - controls.decManTrack
            manualTurretTarget += change * 3
            bot.turret?.gotoAngle(manualTurretTarget)
        }
    }

    private fun handleFlywheelToggle() {
        flywheelOn.toggle(controls.toggleFlywheel)
    }

    private fun handleAutoFlywheel() {
        autoFlywheel.toggle(controls.toggleAutoFlywheel)

        if (autoFlywheel.state) {
            if (flywheelOn.state) {
                bot.flywheel?.shootAtHive(bot.pinpoint?.pose!!)
            } else {
                bot.flywheel?.setPower(0.0)
            }
        }
    }

    private fun handleManualFlywheel() {
        if (!autoFlywheel.state) {
            if (flywheelOn.state) {
                if (controls.incManFlywheel) {
                    defaultFlyVel += 50.0
                } else if (controls.decManFlywheel) {
                    defaultFlyVel -= 50.0
                }
                bot.flywheel?.setVelocity(defaultFlyVel)
            } else {
                bot.flywheel?.setPower(0.0)
            }
        }
    }
}
