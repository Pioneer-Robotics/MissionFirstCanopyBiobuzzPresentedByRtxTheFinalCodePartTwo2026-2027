package org.firstinspires.ftc.teamcode.opmodes.auto

import android.util.Size
import com.acmerobotics.dashboard.FtcDashboard
import com.pedropathing.api.Paths.line
import com.pedropathing.api.PoseFactory
import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.pedropathing.paths.Path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.hardware.Camera
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.vision.ColorBlob
import org.firstinspires.ftc.teamcode.vision.GroundProjector
import org.firstinspires.ftc.vision.opencv.ColorRange
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Autonomous(name = "Intake Tracking")
class SingleCollectTest: OpMode() {
    // Dashboard
    private val dashboardTelemetry = FtcDashboard.getInstance().telemetry
    // Camera
    private lateinit var camera: Camera
    // Processors
    private val pollenColorBlob = ColorBlob(targetColor = ColorRange.YELLOW, draw = true)
    // Projection
    private val projector = GroundProjector(Constants.Camera.IMAGE_POINTS, Constants.Camera.GROUND_POINTS)
    // Pedro pathing
    private val poseFactory = PoseFactory.degrees()
    private lateinit var follower: Follower
    // Detection
    private var target = Pose()
    private var detected = false

    override fun init() {
        camera = Camera(
            hardwareMap,
            processors = arrayOf(pollenColorBlob.processor),
            resolution = Size(640, 480)
        ).apply { init() }

        follower = org.firstinspires.ftc.teamcode.pedroPathing.Constants.create(hardwareMap)
    }

    override fun start() {
        follower.setPose(poseFactory.of(0.0, 0.0, 90.0))
        follower.update()
    }

    override fun loop() {
        if (detected) {
            follower.update()
            Scheduler.execute()
            telemetry.addData("Field Coordinates", target)
            telemetry.update()
        } else {
            val blobs = pollenColorBlob.getBlobs()

            val candidates = blobs.map { detection ->
                val px = detection.circle.x.toDouble()
                val py = (detection.circle.y + detection.circle.radius).toDouble()

                val g = projector.project(px, py)

                Pose(g.x, g.y)
            }

            val robotPose = Pose(
                x = follower.pose().x(),
                y = follower.pose().y(),
                theta = follower.pose().heading()
            )

            val chosen = candidates.minByOrNull {
                hypot(it.x, it.y)
            }

            if (chosen == null) {
                telemetry.addData("ERROR", "No yellow blobs detected")
                telemetry.update()
                return
            }

            target = toField(chosen, robotPose)

            val path = buildPath(robotPose, target)
            schedule(follow(follower, path))

            detected = true
        }
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

    private fun buildPath(start: Pose, target: Pose): Path {
        val endHeading = atan2(target.y - start.y, target.x - start.x)

        val startPoint = poseFactory.of(start.x, start.y, Math.toDegrees(start.theta))
        val endPoint = poseFactory.of(target.x, target.y, Math.toDegrees(endHeading))
        return line(startPoint, endPoint).linear(startPoint, endPoint)
    }
}
