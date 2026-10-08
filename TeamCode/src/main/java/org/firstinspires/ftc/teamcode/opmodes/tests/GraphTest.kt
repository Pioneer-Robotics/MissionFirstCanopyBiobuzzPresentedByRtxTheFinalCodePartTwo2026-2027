package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.helpers.Toggle
import org.firstinspires.ftc.teamcode.helpers.graph.Graph
import kotlin.time.Duration
import kotlin.time.measureTime

@Autonomous(name = "Graph Test", group = "Test")
class GraphTest : OpMode() {

    enum class State {
        INPUT,
        CALC,
        OUTPUT
    }

    var state = State.INPUT
    var dim = 50
    var aStar = false
    var dur = Duration.ZERO

    val lBumpToggle = Toggle(false)
    val rBumpToggle = Toggle(false)
    val aStarToggle = Toggle(false)

    override fun init() {
        telemetry.addLine("Press Start")
    }

    override fun loop() {
        telemetry.addData("Dimension", dim)
        telemetry.addData("A*", aStar)
        when (state) {
            State.INPUT -> input()
            State.CALC -> calc()
            State.OUTPUT -> output()
        }

        telemetry.update()
    }

    private fun input() {
        val scale = if (gamepad1.triangle) 50 else 5

        lBumpToggle.toggle(gamepad1.left_bumper)
        rBumpToggle.toggle(gamepad1.right_bumper)
        aStarToggle.toggle(gamepad1.cross)

        if (lBumpToggle.justChanged) { dim -= scale }
        if (rBumpToggle.justChanged) { dim += scale }
        aStar = aStarToggle.state

        if (gamepad1.square) {
            state = State.CALC
        }

        telemetry.addLine("Press Right Bumper to Inc Dim")
        telemetry.addLine("Press Left Bumper to Dec Dim")
        telemetry.addLine("Press Triangle to Change Dim Faster")
        telemetry.addLine("Press Cross to Toggle A*")
        telemetry.addLine("Press Square to Calculate")
    }
    private fun calc() {
        telemetry.addLine("Initializing Graph")
        telemetry.update()

        val graph = Graph(dim, dim)
        graph.start = Pair(120.0,120.0)
        graph.target = Pair(230.0,230.0)

        telemetry.addLine("Calculating Dijkstra")
        telemetry.update()

        dur = measureTime {
            graph.dijkstraPath(aStar = aStar)
        }

        state = State.OUTPUT
    }
    private fun output() {
        if (gamepad1.circle) {
            state = State.INPUT
        }
        telemetry.addData("Time Elapsed", dur)
        telemetry.addLine("Press Circle to Reset")
    }

}