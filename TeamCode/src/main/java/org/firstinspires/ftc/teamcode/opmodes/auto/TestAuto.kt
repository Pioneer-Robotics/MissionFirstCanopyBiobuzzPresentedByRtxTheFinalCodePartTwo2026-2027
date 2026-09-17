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
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@Autonomous(name = "AutoPath", group = "Autonomous")
class AutoPath : LinearOpMode() {

    private lateinit var follower: Follower

    private val poseFactory = PoseFactory.degrees()

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
        follow(follower, collectToShootFar())
    )

    override fun runOpMode() {
        Scheduler.reset()
        follower = Constants.create(hardwareMap)
        follower.setPose(start)
        follower.update()

        waitForStart()
        schedule(autoRoutine())

        while (opModeIsActive()) {
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
}
