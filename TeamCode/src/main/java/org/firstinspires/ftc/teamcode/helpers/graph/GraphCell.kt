package org.firstinspires.ftc.teamcode.helpers.graph

import java.lang.Math.floorDiv

// Objects for each unit of the field used for dijkstra's algorithm (or A*, etc)
class GraphCell(
    var value: Double = Double.MAX_VALUE,
    var index: Pair<Int, Int> = Pair(-1, -1)
) {
    val neighbors = MutableList(9) { Pair(-1,-1) }
    var parentIndex: Pair<Int, Int>? = null

    fun setNeighbors() {
        for (i in 0..8) {
            neighbors[i] = index + Pair(-1 + floorDiv(i, 3), -1 + i % 3)
        }
        neighbors.removeAt(4)
    }
}