package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.api.Paths.*
import com.pedropathing.api.PoseFactory
import com.pedropathing.follower.Follower
import com.pedropathing.paths.Path
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.commands.Commands.instant
import com.pedropathing.ivy.commands.Commands.waitMs
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.hardware.DualFlywheel
import org.firstinspires.ftc.teamcode.hardware.Intake
import org.firstinspires.ftc.teamcode.hardware.LED
import org.firstinspires.ftc.teamcode.hardware.Transfer
import org.firstinspires.ftc.teamcode.hardware.Turret
import org.firstinspires.ftc.teamcode.helpers.Chrono
import org.firstinspires.ftc.teamcode.helpers.toPioneerPose
import org.firstinspires.ftc.teamcode.helpers.withMaxVelocity
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@Autonomous(name = "Solo Auto", group = "Autonomous")
class SoloAuto : OpMode() {

    private lateinit var follower: Follower
    private val poseFactory = PoseFactory.degrees()
    private val chrono = Chrono(false)

    // Hardware
    lateinit var led: LED
    lateinit var turret: Turret
    lateinit var flywheel: DualFlywheel
    lateinit var intake: Intake
    lateinit var transfer: Transfer

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
        shoot(),
        instant { intake.setPower(1.0) },
        follow(follower, startToCollect()).withMaxVelocity(25.0),
        instant { intake.setPower(0.0) },
        follow(follower, collectToShootFar()),
        shoot(),
        // Intake from flower
        // Go to close flower
        // Is hive tipped?
        // Yes --> Shoot close
        // No  --> Shoot far
        // Park
    )

    override fun init() {
        Scheduler.reset()
        follower = Constants.create(hardwareMap)
        follower.setPose(start)
        follower.update()

        // Create hardware objects
        led = LED(hardwareMap).apply { init() }
        turret = Turret(hardwareMap).apply { init() }
        flywheel = DualFlywheel(hardwareMap).apply {
            init()
            useIndependent()
        }
        intake = Intake(hardwareMap).apply { init() }
        transfer = Transfer(hardwareMap).apply { init() }
    }

    override fun start() {
        schedule(autoRoutine())
    }

    override fun loop() {
        // Update subsystems
        chrono.update()
        led.update(chrono.dt)
        turret.update(chrono.dt)
        flywheel.update(chrono.dt)
        intake.update(chrono.dt)
        transfer.update(chrono.dt)
        follower.update()

        // Aim at hive
        turret.aimAtHive(follower.pose().toPioneerPose())
        flywheel.shootAtHive(follower.pose().toPioneerPose())

        // Execute scheduler routine
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

    // Pedro ivy commands
    fun shoot(): Command {
        return sequential(
            instant {  }, // Release shooter servo
            waitMs(500.0) // Wait for shooter
        )
    }
}
