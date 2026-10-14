package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.helpers.graph.Graph
import org.junit.Test
import kotlin.time.measureTime

class GraphTest {

    @Test
    fun main() {
        val graph = Graph(50, 50)
        graph.setStart(Pair(120.0,120.0))
        graph.setTarget(Pair(230.0,230.0))

        var dur = measureTime {
            graph.dijkstraPath(aStar = false)
        }
        println(dur)
        graph.resetGraph()

        dur = measureTime {
            graph.dijkstraPath(aStar = true)
        }
        println(dur)
    }
}