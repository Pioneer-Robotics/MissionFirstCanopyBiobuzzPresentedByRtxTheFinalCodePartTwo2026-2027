package org.firstinspires.ftc.teamcode.opmodes.teleop.drivers

import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.helpers.Toggle
import org.firstinspires.ftc.teamcode.opmodes.teleop.Teleop
import org.firstinspires.ftc.teamcode.opmodes.teleop.driverProfiles.ControlProfile
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class TeleopDriver1(
    private val controls: ControlProfile,
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
    private var fieldCentricToggle: Toggle = Toggle(true)
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
        var direction = Pose(controls.strafe.toDouble(), -controls.drive.toDouble())
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
                vx = if (abs(direction.x) < 0.05) 0.0 else direction.x,
                vy = if (abs(direction.y) < 0.05) 0.0 else direction.y,
                omega = if (abs(controls.turn.toDouble()) < 0.05) 0.0 else controls.turn.toDouble(),
            ),
            drivePower,
            Constants.Drive.MAX_MOTOR_VELOCITY_TPS
        )
    }

    private fun updateDrivePower() {
        incDrivePower.toggle(controls.incDrivePower)
        decDrivePower.toggle(controls.decDrivePower)
        if (incDrivePower.justChanged) {
            drivePower += 0.1
        }
        if (decDrivePower.justChanged) {
            drivePower -= 0.1
        }
        drivePower = drivePower.coerceIn(0.1, 1.0)
    }

    private fun updateFieldCentric() {
        fieldCentricToggle.toggle(controls.toggleFieldCentric)
    }

    private fun handleIntakeTransfer() {
        //Basic on/off
        if (controls.intake) {
            intakeState = true
            requestTransferOn = true
        } else {
            intakeState = false
            requestTransferOn = false
        }

        //Auto stop intake
        autoStopIntake = bot.breakBeam?.state!!

        if (controls.endAutoStopIntake) {
            autoStopIntake = false
        }
        if (autoStopIntake) {
            intakeState = false
        }

        //All reverse controls
        if (controls.reverseAll) {
            reverseTransfer = true
            reverseIntake = true
            requestTransferOn = true
            intakeState = true
        } else if (controls.reverseIntake) {
            reverseIntake = true
        } else {
            reverseIntake = false
            reverseTransfer = false
        }

        //Talk to intake hardware
        var intakePow = 0.0
        if (reverseIntake) {
            intakePow = -1.0
        } else if (intakeState) {
            intakePow = 1.0
        }

        bot.intake?.setPower(intakePow)
    }

    private fun handleResetOdo() {
        if (controls.resetOdo) {
            bot.pinpoint?.reset(Constants.Misc.RESET_POSE)
        }
    }

    private fun handleFlowerRamp() {
        flowerToggle.toggle(controls.flowerRamp)

        if (flowerToggle.state) {
            bot.flowerRamp?.down()
        } else {
            bot.flowerRamp?.up()
        }
    }
}
