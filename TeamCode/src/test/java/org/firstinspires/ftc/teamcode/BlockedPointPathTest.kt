package org.firstinspires.ftc.teamcode

import com.pedropathing.api.Paths
import org.firstinspires.ftc.teamcode.helpers.graph.Graph
import org.junit.Test

class BlockedPointPathTest {
    @Test
    fun main() {
        val graph = Graph(50, 50, addField = true)
        graph.setStart(Pair(10.0,10.0))
        graph.setTarget(Pair(350.0,350.0))

        val path = graph.dijkstraPath(aStar = true)
        val bez = graph.pathToBezier(path, 0.25)

        val ptsPrint = mutableListOf<Pair<Double, Double>>()
        for (p in bez) {
            ptsPrint.add(Pair(p.x(), p.y()))
        }

        var pedroPath = graph.toPedroPath(bez)

        val bezPrint = mutableListOf<Pair<Double, Double>>()
        var t = 0.0
        while (t < 1.0) {
            val pose = pedroPath.get(t)
            bezPrint.add(Pair(pose.x(), pose.y()))
            t += 0.01
        }

        println(path)
        println(ptsPrint)
        println(bezPrint)
    }
}