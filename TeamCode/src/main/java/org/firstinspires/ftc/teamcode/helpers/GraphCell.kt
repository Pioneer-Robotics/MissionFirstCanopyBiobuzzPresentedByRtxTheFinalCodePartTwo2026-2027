package org.firstinspires.ftc.teamcode.helpers

import java.lang.Math.floorDiv

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first + other.first, this.second + other.second)
}

class GraphCell(
    var value: Double = Double.MAX_VALUE,
    var index: Pair<Int, Int> = Pair(-1, -1)
) {
    val neighbors = MutableList(8) { Pair(-1,-1) }
    var parent: GraphCell? = null

    init {
        for (i in 0..8) {
            if (i == 4) { continue }
            neighbors[i] = index + Pair(-1 + floorDiv(i, 3), -1 + i % 3)
        }
    }
}