package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.BotType
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.hardware.MecanumBase
import org.firstinspires.ftc.teamcode.helpers.FileLogger
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.helpers.Toggle
import org.firstinspires.ftc.teamcode.helpers.next
import org.firstinspires.ftc.teamcode.localization.localizers.Pinpoint
import org.firstinspires.ftc.teamcode.prism.Color

/**
 * Shared OpMode base that wires bot lifecycle, bulk reads, follower updates, and telemetry.
 *
 * Subclasses should assign [bot] in [onInit], then implement [onStart], [onLoop], and [onStop]
 * for behavior. The framework handles [bot.initAll], [bot.updateAll], optional follower updates,
 * dashboard packet sending, and stop-time cleanup automatically.
 */
abstract class BaseOpMode(val botType: BotType) : OpMode() {
    // Bot instance to be defined in subclasses
    protected lateinit var bot: Bot

    // Telemetry packet for dashboard
    protected var telemetryPacket = TelemetryPacket()

    // Dashboard instance
    private val dashboard =
        FtcDashboard
            .getInstance()

    val runTimer = ElapsedTime()

    val elapsedTime: Double
        get() = runTimer.seconds()

    val allHubs: List<LynxModule> by lazy {
        hardwareMap.getAll(LynxModule::class.java)
    }

    final override fun init() {
        for (hub in allHubs) {
            hub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
        }
        bot = Bot.fromType(botType, hardwareMap)
        bot.initAll() // Initialize bot hardware
        onInit() // Call user-defined init method
        updateTelemetry()

        // Transfer data
        bot.allianceColor = Constants.TransferData.allianceColor
//        bot.pinpoint?.reset(Constants.TransferData.pose)
    }

    final override fun init_loop() {
        onInitLoop()
    }

    final override fun start() {
        onStart()
        runTimer.reset()
    }

    final override fun loop() {
        for (hub in allHubs) {
            hub.clearBulkCache()
        }
        // Update bot systems
        bot.updateAll()

        // Call user-defined loop logic
        onLoop()

        // Update path follower
        if (bot.usePioneerFollower && bot.has<Pinpoint>() && bot.has<MecanumBase>()) {
            bot.follower.update()
        }

        // Automatically handle telemetry updates
        updateTelemetry()
    }

    final override fun stop() {
        // Transfer data
        Constants.TransferData.allianceColor = bot.allianceColor
        Constants.TransferData.pose = bot.pinpoint?.pose ?: Pose()

        bot.led?.clear()
        bot.mecanumBase?.stop() // Ensure motors are stopped
        FileLogger.flush() // Flush any logged data
        onStop() // Call user-defined stop method
    }

    enum class Verbose {
        DEBUG,
        INFO,
        FATAL
    }

    fun addTelemetryData(caption: String, value: Any ?= null, verbose: Verbose) {
        if (verbose.ordinal >= Constants.Misc.VERBOSE_LEVEL.ordinal) {
            telemetry.addData(caption, value)
        }
    }

    private fun updateTelemetry() {
        telemetry.update()
        dashboard.sendTelemetryPacket(telemetryPacket)
        telemetryPacket = TelemetryPacket() // Reset packet for next loop
    }

    // These functions are meant to be overridden in subclasses
    protected open fun onInit() {}

    protected open fun onInitLoop() {}

    protected open fun onStart() {}

    protected open fun onLoop() {}

    protected open fun onStop() {}
}
