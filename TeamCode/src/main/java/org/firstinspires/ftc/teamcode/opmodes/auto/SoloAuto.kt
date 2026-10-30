package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.api.Paths.*
import com.pedropathing.api.PoseFactory
import com.pedropathing.follower.Follower
import com.pedropathing.paths.Path
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.commands.Commands.waitMs
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.hardware.LED
import org.firstinspires.ftc.teamcode.helpers.withMaxVelocity
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@Autonomous(name = "Solo Auto", group = "Autonomous")
class SoloAuto : OpMode() {

    private lateinit var follower: Follower

    private val poseFactory = PoseFactory.degrees()

    // Hardware
    lateinit var led: LED

    // Points
    private val start = poseFactory.of(56.0, 8.0, 180.0)
    private val collectGarden = poseFactory.of(7.0, 8.0, 180.0)
    private val collectFlower1 = poseFactory.of(48.0, 120.0, 90.0)
    private val collectFlower2 = poseFactory.of(48.0, 130.0, 90.0)
    private val park = poseFactory.of(12.0, 120.0, 90.0)
    private val control1 = poseFactory.of(47.0, 22.0, 0.0)
    private val control2 = poseFactory.of(7.0, 117.0, 0.0)

    // Paths
    fun startToCollectGarden(): Path = line(start, collectGarden).constant(collectGarden)
    fun collectGardenToCollectFlower(): Path = curve(collectGarden, control1, control2, collectFlower1).linear(collectGarden, collectFlower1)
    fun collectFlower1ToCollectFlower2(): Path = line(collectFlower1, collectFlower2).constant(collectFlower1)
    fun collectFlowerToPark(): Path = line(collectFlower2, park).constant(collectFlower2)

    // Autonomous routine
    fun autoRoutine(): Command = sequential(
        waitMs(2000.0), // Shoot
        follow(follower, startToCollectGarden()).withMaxVelocity(25.0),
        follow(follower, collectGardenToCollectFlower()),
        waitMs(1000.0), // Shoot
        follow(follower, collectFlower1ToCollectFlower2()).withMaxVelocity(25.0),
        waitMs(1000.0), // Finish shooting
        follow(follower, collectFlowerToPark())
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
