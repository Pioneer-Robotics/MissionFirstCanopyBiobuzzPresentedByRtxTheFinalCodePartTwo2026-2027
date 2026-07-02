package org.firstinspires.ftc.teamcode.opmodes.teleop.drivers

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.helpers.Toggle
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class TeleopDriver1(
    var gamepad: Gamepad,
    val bot: Bot,
) {
    var drivePower = Constants.Drive.DEFAULT_POWER
    val fieldCentric: Boolean
        get() = fieldCentricToggle.state

    // Toggles
    private var incDrivePower: Toggle = Toggle(false)
    private var decDrivePower: Toggle = Toggle(false)
    private var fieldCentricToggle: Toggle = Toggle(false)
    private var intakeToggle: Toggle = Toggle(false)

    fun update() {
        drive()
        updateDrivePower()
        updateFieldCentric()
    }

    private fun drive() {
        var direction = Pose(gamepad.left_stick_x.toDouble(), -gamepad.left_stick_y.toDouble())
        if (fieldCentric) {
            var angle = atan2(direction.y, direction.x) - bot.pinpoint?.pose!!.theta
            angle += when (bot.allianceColor) {
                AllianceColor.BLUE -> PI/2
                AllianceColor.RED -> -PI/2
                AllianceColor.NEUTRAL -> 0.0
            }
            val mag = direction.getLength()
            direction = Pose(mag * cos(angle), mag * sin(angle))
        }
        bot.mecanumBase?.setDrivePower(
            Pose(
                vx = direction.x,
                vy = direction.y,
                omega = gamepad.right_stick_x.toDouble(),
            ),
            drivePower,
            Constants.Drive.MAX_MOTOR_VELOCITY_TPS
        )
    }

    private fun updateDrivePower() {
        incDrivePower.toggle(gamepad.right_bumper)
        decDrivePower.toggle(gamepad.left_bumper)
        if (incDrivePower.justChanged) {
            drivePower += 0.1
        }
        if (decDrivePower.justChanged) {
            drivePower -= 0.1
        }
        drivePower = drivePower.coerceIn(0.1, 1.0)
    }

    private fun updateFieldCentric() {
        fieldCentricToggle.toggle(gamepad.touchpad)
    }
}
