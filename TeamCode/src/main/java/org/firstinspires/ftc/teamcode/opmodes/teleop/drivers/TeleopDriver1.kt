package org.firstinspires.ftc.teamcode.opmodes.teleop.drivers

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.helpers.Toggle
import org.firstinspires.ftc.teamcode.opmodes.teleop.Teleop
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class TeleopDriver1(
    private val gamepad: Gamepad,
    private val bot: Bot,
    private val teleop: Teleop
) {
    var drivePower = Constants.Drive.DEFAULT_POWER
    val fieldCentric: Boolean
        get() = fieldCentricToggle.state
    var reverseIntake = false
    var reverseTransfer = false
    var requestTransferOn = false
    var autoStopIntake = false
    var intakeState = false

    // Toggles
    private var incDrivePower: Toggle = Toggle(false)
    private var decDrivePower: Toggle = Toggle(false)
    private var fieldCentricToggle: Toggle = Toggle(false)
    private var intakeToggle: Toggle = Toggle(false)
    private var flowerToggle: Toggle = Toggle(false)

    fun update() {
        drive()
        updateDrivePower()
        updateFieldCentric()
        handleIntakeTransfer()
        handleResetOdo()
        handleFlowerRamp()
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

    private fun handleIntakeTransfer() {
        //Basic on/off
        if (gamepad.left_bumper){
            intakeState = true
            requestTransferOn = true
        } else {
            intakeState = false
            requestTransferOn = false
        }

        //Auto stop intake
        if (teleop.storageFull.justChanged){
            autoStopIntake = teleop.storageFull.state
        }
        if (autoStopIntake){
            intakeState = false
            if (gamepad.leftBumperWasReleased()){
                autoStopIntake = false
            }
        }

        //All reverse controls
        if (gamepad.dpad_up){
            reverseTransfer = true
            reverseIntake = true
        } else if (gamepad.dpad_down){
            reverseIntake = true
        } else {
            reverseIntake = false
            reverseTransfer = false
        }

        //Talk to intake hardware
        var intakePow = if (reverseIntake) -1.0 else 1.0
        intakePow *= if (intakeState) 1.0 else 0.0

        bot.intake?.setPower(intakePow)

    }

    private fun handleResetOdo() {
        if (gamepad.right_trigger > 0.5 && gamepad.left_trigger > 0.5) {
            bot.pinpoint?.reset(Constants.Misc.RESET_POSE)
        }
    }

    private fun handleFlowerRamp() {
        flowerToggle.toggle(gamepad.cross)

        if (flowerToggle.state) {
            bot.flowerRamp?.down()
        } else {
            bot.flowerRamp?.up()
        }
    }
}
