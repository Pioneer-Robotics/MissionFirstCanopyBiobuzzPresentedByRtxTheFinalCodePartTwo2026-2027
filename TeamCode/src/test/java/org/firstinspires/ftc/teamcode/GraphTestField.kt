package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.helpers.graph.Graph
import org.junit.Test
import kotlin.time.measureTime

class GraphTestField {
    @Test
    fun main() {
        val graph = Graph(48, 48, addField = true)

        val values = mutableListOf<Double>()
        val points = mutableListOf<Pair<Double, Double>>()
        for (cell in graph.graph) {
            values.add(cell.value.coerceIn(-100.0, 100.0))
            points.add(graph.inToField(cell.index))
        }
        println(values)
        println(points)
//        graph.start = Pair(120.0,120.0)
//        graph.target = Pair(230.0,230.0)
//
//        graph.dijkstraPath(aStar = false)

    }
}