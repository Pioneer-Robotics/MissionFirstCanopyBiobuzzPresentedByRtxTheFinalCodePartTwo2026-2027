package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.api.PoseFactory
import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.pedropathing.paths.Path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.helpers.graph.Graph
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@Autonomous(name = "Obstacle Avoidance Test")
class ObsAvoidTest : OpMode() {
    private lateinit var graph: Graph
    private lateinit var follower: Follower
    private val p = PoseFactory.degrees()

    override fun init() {
        graph = Graph(50, 50, addField = true)
        follower = Constants.create(hardwareMap)
        val path = generatePath(Pair(30.0, 30.0), Pair(330.0, 330.0))
        Scheduler.schedule(follow(follower, path))
    }

    override fun start() {
        follower.setPose(p.of(30.0, 30.0, 90.0))
        follower.update()
    }

    override fun loop() {
        follower.update()
        Scheduler.execute()

        telemetry.addData("Pose", follower.pose())
        telemetry.addData("Target", follower.currentPath()?.endPose())
        telemetry.addData("Follower Busy?", follower.isBusy)
        telemetry.update()
    }

    fun generatePath(start: Pair<Double, Double>, target: Pair<Double, Double>): Path {
        graph.resetGraph()
        graph.setStart(start)
        graph.setTarget(target)

        val path = graph.dijkstraPath(aStar = true)
        val bez = graph.pathToBezier(path, smoothness = 0.25)
        val pedroPath = graph.toPedroPath(bez)

        return pedroPath
    }
}