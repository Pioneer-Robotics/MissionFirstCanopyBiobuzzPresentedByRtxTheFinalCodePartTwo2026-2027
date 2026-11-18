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
import com.pedropathing.ivy.groups.Groups.parallel
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.hardware.DualFlywheel
import org.firstinspires.ftc.teamcode.hardware.FlowerRamp
import org.firstinspires.ftc.teamcode.hardware.Intake
import org.firstinspires.ftc.teamcode.hardware.LED
import org.firstinspires.ftc.teamcode.hardware.ReleaseServo
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
    lateinit var releaseServo: ReleaseServo
    lateinit var flowerRamp: FlowerRamp

    // Points
    private val start = poseFactory.of(56.0, 8.0, 180.0)
    private val collect = poseFactory.of(7.0, 8.0, 180.0)
    private val shootFar = poseFactory.of(48.0, 117.0, 90.0)
    private val shootFar2 = poseFactory.of(60.0, 117.0, 90.0)
    private val shootClose = poseFactory.of(60.0, 20.0, 90.0)
    private val flowerFar = poseFactory.of(48.0, 125.0, 90.0)
    private val prepFlowerClose = poseFactory.of(27.0, 48.0, 180.0)
    private val flowerClose = poseFactory.of(19.0,48.0, 180.0)
    private val park1 = poseFactory.of(10.0, 120.0, 0.0)
    private val park2 = poseFactory.of(10.0, 90.0, 0.0)
    private val control1 = poseFactory.of(48.0, 22.0, 0.0)
    private val control2 = poseFactory.of(7.0, 117.0, 0.0)
    private val control3 = poseFactory.of(24.0, 120.0, 0.0)
    private val control4 = poseFactory.of(24.0, 24.0, 0.0)

    // Paths
    fun startToCollect(): Path = line(start, collect).constant(collect)
    fun collectToShootFar(): Path = curve(collect, control1, control2, shootFar).linear(collect, shootFar)
    fun shootFarToFlowerFar(): Path = line(shootFar, flowerFar).constant(shootFar)
    fun flowerFarToPrepFlowerClose(): Path = curve(flowerFar, prepFlowerClose, control3).linear(flowerFar, prepFlowerClose)
    fun prepFlowerCloseToFlowerClose(): Path = line(prepFlowerClose, flowerClose).constant(prepFlowerClose)
    fun flowerFarToFlowerClose(): Path = path(flowerFarToPrepFlowerClose(), prepFlowerCloseToFlowerClose())
    fun flowerCloseToShootClose(): Path = line(flowerClose, shootClose).linear(flowerClose, shootClose)
    fun shootCloseToShootFar(): Path = line(shootClose, shootFar2).constant(shootClose)
    fun shootCloseToPark(): Path = curve(shootClose, control4, park2).linear(shootClose, park2)
    fun shootFarToPark(): Path = line(shootFar, park1).linear(shootFar, park1)

    // Autonomous routine
    fun autoRoutine(): Command = sequential(
        shoot(),
        instant { intake.setPower(1.0) },
        follow(follower, startToCollect()).withMaxVelocity(25.0),
        instant { intake.setPower(0.0) },
        follow(follower, collectToShootFar()),
        instant { flowerRamp.down() },
        shoot(),
        follow(follower, shootFarToFlowerFar()),
        // Might need to wait for intake
        shoot(),
        follow(follower, flowerFarToFlowerClose()),
        waitMs(2000.0), // Wait for flower intake
        parallel(
            follow(follower, flowerCloseToShootClose()),
            sequential(
                waitMs(1000.0),
                instant { flowerRamp.up() }
            )
        ),
        // Is hive tipped?
        if (true) {
            shoot()
            follow(follower, shootCloseToPark())
        } else {
            follow(follower, shootCloseToShootFar())
            shoot()
            follow(follower, shootFarToPark())
        }
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
        releaseServo = ReleaseServo(hardwareMap).apply { init() }
        flowerRamp = FlowerRamp(hardwareMap)
    }

    override fun start() {
        schedule(autoRoutine())
        transfer.setPower(1.0) // Enable transfer
    }

    override fun loop() {
        // Update subsystems
        chrono.update()
        led.update(chrono.dt)
        turret.update(chrono.dt)
        flywheel.update(chrono.dt)
        intake.update(chrono.dt)
        transfer.update(chrono.dt)
        releaseServo.update(chrono.dt)
        flowerRamp.update(chrono.dt)
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
            instant { releaseServo.open() }, // Release shooter servo
            waitMs(2500.0), // Wait for shooter
            instant { releaseServo.close() } // Close shooter servo
        )
    }
}
