package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.helpers.graph.Graph
import org.junit.Test

class PathToBezTest {

    @Test
    fun main() {
        val graph = Graph(50, 50, addField = true)
        graph.start = Pair(45.0,45.0)
        graph.target = Pair(300.0,300.0)

        val path = graph.dijkstraPath(aStar = true)
        val bez = graph.pathToBezier(path)

        val bezPrint = mutableListOf<Pair<Double, Double>>()
        for (p in bez) {
            bezPrint.add(Pair(p.x(), p.y()))
        }

        println(path)
        println(bezPrint)
    }
}