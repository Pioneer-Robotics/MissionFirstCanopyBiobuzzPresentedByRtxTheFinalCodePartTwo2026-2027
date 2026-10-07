package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.helpers.graph.Graph
import org.junit.Test
import kotlin.time.measureTime

class GraphTest {

    @Test
    fun main() {
        val graph = Graph(200, 200)
        graph.start = Pair(140.0,140.0)
        graph.target = Pair(230.0,230.0)

        val dur = measureTime {
            graph.dijkstraPath()
        }
        println(dur)
    }
}