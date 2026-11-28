package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.BotType
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.prism.Color
import org.firstinspires.ftc.teamcode.helpers.Toggle
import org.firstinspires.ftc.teamcode.helpers.next
import org.firstinspires.ftc.teamcode.opmodes.BaseOpMode
import org.firstinspires.ftc.teamcode.opmodes.teleop.driverProfiles.SoloControlProfile
import org.firstinspires.ftc.teamcode.opmodes.teleop.drivers.TeleopDriver1
import org.firstinspires.ftc.teamcode.opmodes.teleop.drivers.TeleopDriver2

@TeleOp(name = "Teleop")
class Teleop : BaseOpMode(BotType.COMP_BOT) {
    private lateinit var driver1: TeleopDriver1
    private lateinit var driver2: TeleopDriver2
    private val allianceToggle = Toggle(false)
    private var changedAllianceColor = false

    private lateinit var controlProfile: SoloControlProfile

    override fun onInit() {
        controlProfile = SoloControlProfile(gamepad1, gamepad2)
        driver1 = TeleopDriver1(controlProfile, bot, this)
        driver2 = TeleopDriver2(controlProfile, bot, this)
    }

    override fun onInitLoop() {
        allianceToggle.toggle(gamepad1.touchpad)
        if (allianceToggle.justChanged) {
            changedAllianceColor = true
            bot.allianceColor = bot.allianceColor.next()
            bot.led?.setColor(
                when(bot.allianceColor) {
                    AllianceColor.RED -> Color.RED
                    AllianceColor.BLUE -> Color.BLUE
                    AllianceColor.NEUTRAL -> Color.PURPLE
                },
                0,
                23,
            )
        }
        telemetry.addData("Alliance Color", bot.allianceColor)
        telemetry.update()
    }

    override fun onStart() {
        if (!changedAllianceColor) bot.allianceColor = Constants.TransferData.allianceColor
        driver2.onStart()
    }

    override fun onLoop() {
        // Update gamepad inputs
        driver1.update()
        driver2.update()

        handleTransfer()
        handleLED()

        // Add telemetry data
        handleTelemetry()
    }

    private fun handleTransfer(){
        val transferPow = when {
            driver2.stopTransfer and !driver1.reverseTransfer -> 0.0
            driver1.reverseTransfer -> -Constants.Transfer.SHOOT_POWER
            driver2.requestTransferOn -> Constants.Transfer.SHOOT_POWER
            driver1.requestTransferOn and (bot.breakBeam?.state == false) -> Constants.Transfer.LOAD_POWER
            else -> 0.0
        }

        bot.transfer?.setPower(transferPow)
    }

    private  fun handleLED() {
        if (bot.breakBeam?.state == true) {
            bot.led?.setColor(Color.GREEN)
        } else {
            bot.led?.setColor(Color.TRANSPARENT)
        }
    }

    private fun handleTelemetry() {
        addTelemetryData("Drive Power", driver1.drivePower, Verbose.INFO)
        addTelemetryData("Pose", bot.pinpoint!!.pose, Verbose.DEBUG)
        addTelemetryData("Flywheel Velocity", bot.flywheel?.getVelocity(), Verbose.INFO)
        addTelemetryData("Flywheel On", driver2.flywheelOn.state, Verbose.INFO)
        addTelemetryData("Breakbeam", bot.breakBeam?.state, Verbose.INFO)
        addTelemetryData("Turret angle", bot.turret?.getAngle(), Verbose.INFO)
        addTelemetryData("Turret change", controlProfile.incManTrack - controlProfile.decManTrack, Verbose.INFO)

        addTelemetryData("Field Centric", driver1.fieldCentric, Verbose.INFO)
        addTelemetryData("Velocity", "vx: %.2f, vy: %.2f".format(bot.pinpoint?.pose?.vx, bot.pinpoint?.pose?.vy), Verbose.DEBUG)
        addTelemetryData("Voltage", bot.batteryMonitor?.voltage, Verbose.INFO)
        addTelemetryData("Current Draw (mA)", bot.hubPower?.getCurrentDraw(), Verbose.INFO)
        addTelemetryData("Charged Used (mAh)", bot.hubPower?.getChargeUsed(), Verbose.INFO)
        addTelemetryData("Average Current Draw (mA)", bot.hubPower?.getAverageCurrent(), Verbose.DEBUG)
    }
}
