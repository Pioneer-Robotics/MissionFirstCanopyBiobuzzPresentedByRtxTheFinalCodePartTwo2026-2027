package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.algorithm.ForesightConfig
import com.pedropathing.api.Paths.*
import com.pedropathing.api.PoseFactory
import com.pedropathing.follower.Follower
import com.pedropathing.paths.Path
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.commands.Commands.instant
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.BotType
import org.firstinspires.ftc.teamcode.hardware.LED
import org.firstinspires.ftc.teamcode.opmodes.BaseOpMode
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.prism.Color

@Autonomous(name = "Solo Auto", group = "Autonomous")
class SoloAuto : OpMode() {

    private lateinit var follower: Follower

    private val poseFactory = PoseFactory.degrees()

    // Hardware
    lateinit var led: LED

    // Points
    private val start = poseFactory.of(56.0, 8.0, 180.0)
    private val collect = poseFactory.of(7.0, 8.0, 180.0)
    private val shootFar = poseFactory.of(47.0, 117.0, 90.0)
    private val control1 = poseFactory.of(47.0, 22.0, 0.0)
    private val control2 = poseFactory.of(7.0, 117.0, 0.0)

    // Paths
    fun startToCollect(): Path = line(start, collect).constant(collect)
    fun collectToShootFar(): Path = curve(collect, control1, control2, shootFar).linear(collect, shootFar)

    // Autonomous routine
    fun autoRoutine(): Command = sequential(
        instant { Constants.foresightConfig.maxVelocityConstraint.set(25.0) },
        follow(follower, startToCollect()),
        instant { Constants.foresightConfig.maxVelocityConstraint.set(ForesightConfig.Constraint.NONE) },
        follow(follower, collectToShootFar()),
    )

    override fun init() {
        Scheduler.reset()
        follower = Constants.create(hardwareMap)
        follower.setPose(start)
        follower.update()

        // Create hardware objects
        led = LED(hardwareMap).apply { init() }
    }

    override fun start() {
        schedule(autoRoutine())
    }

    override fun loop() {
        follower.update()
        Scheduler.execute()

        telemetry.addData("x", follower.pose().x())
        telemetry.addData("y", follower.pose().y())
        telemetry.addData("heading", follower.pose().heading())

        if (follower.currentPath() != null) {
            telemetry.addData("Current path distance remaining", follower.distanceToEndpoint())
            telemetry.addData("Path number", follower.pathIndex())
        }

        telemetry.update()
    }
}
