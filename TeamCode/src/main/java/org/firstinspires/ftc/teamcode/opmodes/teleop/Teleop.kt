package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.BotType
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.prism.Color
import org.firstinspires.ftc.teamcode.helpers.Toggle
import org.firstinspires.ftc.teamcode.helpers.next
import org.firstinspires.ftc.teamcode.opmodes.BaseOpMode
import org.firstinspires.ftc.teamcode.opmodes.teleop.drivers.TeleopDriver1
import org.firstinspires.ftc.teamcode.opmodes.teleop.drivers.TeleopDriver2

@TeleOp(name = "Teleop")
class Teleop : BaseOpMode() {
    private lateinit var driver1: TeleopDriver1
    private lateinit var driver2: TeleopDriver2
    private val allianceToggle = Toggle(false)
    private var changedAllianceColor = false

    override fun onInit() {
        bot = Bot.fromType(BotType.COMP_BOT, hardwareMap)

        driver1 = TeleopDriver1(gamepad1, bot)
        driver2 = TeleopDriver2(gamepad2, bot)
    }

    override fun init_loop() {
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

        // Add telemetry data
        addTelemetryData()
    }

    private fun addTelemetryData() {
        addTelemetryData("Alliance Color", bot.allianceColor, Verbose.INFO)
        addTelemetryData("Drive Power", driver1.drivePower, Verbose.INFO)
        addTelemetryData("Pose", bot.pinpoint!!.pose, Verbose.DEBUG)

        addTelemetryData("Field Centric", driver1.fieldCentric, Verbose.INFO)
        addTelemetryData("Velocity", "vx: %.2f, vy: %.2f".format(bot.pinpoint?.pose?.vx, bot.pinpoint?.pose?.vy), Verbose.DEBUG)
        addTelemetryData("Voltage", bot.batteryMonitor?.voltage, Verbose.INFO)
    }
}
