package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.helpers.graph.Graph
import org.junit.Test
import kotlin.time.measureTime

class GraphTest {

    @Test
    fun main() {
        val graph = Graph(24, 24)
        graph.start = Pair(9,9)
        graph.target = Pair(15,15)
        println(graph.target)

        val dur = measureTime {
            graph.dijkstraPath()
        }
        println(dur.inWholeMicroseconds)
    }
}