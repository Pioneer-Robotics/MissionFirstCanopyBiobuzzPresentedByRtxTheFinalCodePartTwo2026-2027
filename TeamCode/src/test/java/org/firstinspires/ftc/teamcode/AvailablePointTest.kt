package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.helpers.graph.Graph
import org.junit.Test

class AvailablePointTest {
    @Test
    fun main() {
        val graph = Graph(50, 50, addField = true)

        val out = graph.findCloseReachable(Pair(15,15))
        println(out)
    }
}