package org.firstinspires.ftc.teamcode.opmodes.auto

import com.acmerobotics.dashboard.FtcDashboard
import com.pedropathing.api.Paths.curve
import com.pedropathing.api.Paths.line
import com.pedropathing.api.PoseFactory
import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.commands.Commands.instant
import com.pedropathing.ivy.commands.Commands.waitMs
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.pedropathing.paths.Path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.BotType
import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.helpers.Toggle
import org.firstinspires.ftc.teamcode.helpers.next
import org.firstinspires.ftc.teamcode.opmodes.BaseOpMode
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.prism.Color

@Autonomous(name = "Solo Auto", group = "Autonomous")
class SoloAuto : BaseOpMode() {
    private val allianceToggle = Toggle(false)

    // Pedro
    private lateinit var follower: Follower
    private val p = PoseFactory.degrees() // TODO: Mirror

    // Poses
    private val startPose = p.of(-15.25,-63.5,180.0)
    private val collectGardenPose = p.of(-62.0, -62.0, 180.0)
    private val shootFarPose = p.of(-24.0, 52.5, 90.0)

    // Control Poses
    private val control1 = p.of(-24.0, 0.0, 0.0)
    private val control2 = p.of(-62.0, 52.5, 0.0)

    // Paths
    private fun startToCollect(): Path = line(startPose, collectGardenPose).linear(startPose, collectGardenPose)
    private fun collectGardenToShootFar(): Path = curve(collectGardenPose, control1, control2, shootFarPose).linear(collectGardenPose, shootFarPose)

    // Commands
    private fun shoot(): Command = instant { } // TODO: Replace with actions

    private fun autoRoutine() : Command {
        return sequential(
            // Shoot
            waitMs(1000.0),
            // Enable intake
            follow(follower, startToCollect()),
            follow(follower, collectGardenToShootFar())
        )
    }

    override fun onInit() {
        bot = Bot.fromType(BotType.COMP_BOT, hardwareMap)

        follower = Constants.create(hardwareMap)

        telemetry.addData("BEFORE", follower.pose().x())

        follower.setPose(startPose)

        telemetry.addData("AFTER SET", follower.pose().x())

        follower.update()

        telemetry.addData("AFTER UPDATE", follower.pose().x())
        telemetry.update()

        FtcDashboard.getInstance().telemetry.addData("Pose X", follower.pose().x())
        FtcDashboard.getInstance().telemetry.addData("Pose Y", follower.pose().y())
        FtcDashboard.getInstance().telemetry.addData("Pose Heading", follower.pose().heading())
        FtcDashboard.getInstance().telemetry.update()
    }

    override fun init_loop() {
        toggleAlliance(gamepad1.touchpad)
    }
    override fun onStart() {
        schedule(autoRoutine())
    }

    override fun onLoop() {
        follower.update()
        Scheduler.execute()

        telemetry.addData("Pose", follower.pose())
        telemetry.update()

        FtcDashboard.getInstance().telemetry.addData("Pose X", follower.pose().x())
        FtcDashboard.getInstance().telemetry.addData("Pose Y", follower.pose().y())
        FtcDashboard.getInstance().telemetry.addData("Pose Heading", follower.pose().heading())
        FtcDashboard.getInstance().telemetry.update()
    }

    private fun toggleAlliance(toggle: Boolean) {
        allianceToggle.toggle(toggle)
        if (allianceToggle.justChanged) {
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
}