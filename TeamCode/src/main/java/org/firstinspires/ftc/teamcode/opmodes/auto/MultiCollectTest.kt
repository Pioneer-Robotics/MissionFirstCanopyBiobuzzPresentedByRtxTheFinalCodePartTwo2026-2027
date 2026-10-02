package org.firstinspires.ftc.teamcode.opmodes.auto

import android.util.Size
import com.acmerobotics.dashboard.FtcDashboard
import com.pedropathing.api.Paths.through
import com.pedropathing.api.PoseFactory
import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.hardware.Camera
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.helpers.withMaxVelocity
import org.firstinspires.ftc.teamcode.vision.ColorBlob
import org.firstinspires.ftc.teamcode.vision.GroundProjector
import org.firstinspires.ftc.vision.opencv.ColorRange
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Autonomous(name = "Multi-Collect")
class MultiCollectTest: OpMode() {
    private enum class State { SEARCH, COLLECT, DONE }

    // Dashboard
    private val dashboardTelemetry = FtcDashboard.getInstance().telemetry
    // Camera
    private lateinit var camera: Camera
    // Processors
    private val pollenColorBlob = ColorBlob(targetColor = ColorRange.YELLOW, draw = true)
    // Projection
    private val projector = GroundProjector(Constants.Camera.IMAGE_POINTS, Constants.Camera.GROUND_POINTS)
    // Pedro pathing
    private val p = PoseFactory.degrees()
    private lateinit var follower: Follower
    // State
    private var state = State.SEARCH

    override fun init() {
        camera = Camera(
            hardwareMap,
            processors = arrayOf(pollenColorBlob.processor),
            resolution = Size(640, 480)
        ).apply { init() }

        follower = org.firstinspires.ftc.teamcode.pedroPathing.Constants.create(hardwareMap)
    }

    override fun start() {
        follower.setPose(p.of(0.0, 0.0, 90.0))
        follower.update()
    }

    override fun loop() {
        follower.update()
        Scheduler.execute()

        when (state) {
            State.SEARCH -> planRoute(4)
            State.COLLECT -> if (!follower.isBusy) { state = State.DONE }
            State.DONE -> {}
        }
    }

    private fun planRoute(numObjects: Int) {
        if (follower.isBusy) return

        val robot = Pose.fromPedro(follower.pose())

        val objects = pollenColorBlob.getBlobs().map { d ->
            val g = projector.project(d.circle.x.toDouble(), (d.circle.y + d.circle.radius).toDouble())
            toField(Pose(g.x, g.y), robot)
        }

        if (objects.isEmpty()) { telemetry.addData("ERROR", "No blobs"); return }

        val route = orderByNearest(robot, objects).take(numObjects)

        val points = listOf(robot) + route
        val poses = points.map { p.of(it.x, it.y, it.theta) }
//        val poses = points.mapIndexed { i, pt ->
//            val next = points.getOrNull(i + 1)
//            val heading = if (next != null) atan2(next.y - pt.y, next.x - pt.x) else robot.theta
//            p.of(pt.x, pt.y, Math.toDegrees(heading))
//        }

        // One continuous Bézier curve passing through every pose
        val path = through(*poses.toTypedArray()).tangent()

        schedule(follow(follower, path).withMaxVelocity(50.0))
        state = State.COLLECT
    }

    private fun orderByNearest(start: Pose, pts: List<Pose>): List<Pose> {
        val remaining = pts.toMutableList()
        val ordered = mutableListOf<Pose>()
        var cur = start
        while (remaining.isNotEmpty()) {
            val next = remaining.minByOrNull { hypot(it.x - cur.x, it.y - cur.y) }!!
            ordered += next
            remaining -= next
            cur = next
        }
        return ordered
    }

    private fun toField(cam: Pose, robot: Pose): Pose {
        val c = cos(robot.theta)
        val s = sin(robot.theta)

        return Pose(
            x = -(robot.x + cam.y * c - cam.x * s),
            y = robot.y + cam.y * s + cam.x * c,
            theta = robot.theta
        )
    }
}
