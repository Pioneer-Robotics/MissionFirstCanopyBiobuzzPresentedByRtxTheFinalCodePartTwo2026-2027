package org.firstinspires.ftc.teamcode.helpers

import java.util.PriorityQueue

class Graph(
    val dimX: Int,
    val dimY: Int
) {
    // Graph is a collapsed 2D array representing a grid of cells on the field, which algorithms use to find optimal paths
    private val graph = Array(dimX*dimY) { GraphCell() }
    lateinit var start: Pair<Int, Int>
    lateinit var target: Pair<Int, Int>

    init {
        setGraphIndices()
        addObstacles()
    }

    fun in2D(x: Int, y: Int): Int {
        return dimX*x + y
    }

    private fun setGraphIndices() {
        for (i in 0..<dimX) {
            for (j in 0..<dimY) {
                graph[in2D(i,j)].index = Pair(i,j)
                graph[in2D(i,j)].setNeighbors()
            }
        }
    }

    private fun addObstacles() {
        // TODO: Make work
    }

    private fun resetGraph() {
        for (i in 0..<dimX) {
            for (j in 0..<dimY) {
                graph[in2D(i,j)].value = Double.MAX_VALUE
                graph[in2D(i,j)].parent = null
            }
        }
    }

    fun dijkstraPath(straightCost: Double = 1.0, diagCost: Double = 1.41421): MutableList<Pair<Int, Int>> {

        val queue = PriorityQueue<GraphCell> {
                c1, c2 -> c1.value.compareTo(c2.value) // Min heap comparator
        }

        graph[in2D(start.first,start.second)].value = 0.0
        queue.add(graph[in2D(start.first,start.second)])

        while (queue.isNotEmpty()) {
            val top = queue.poll()!!
            val index = top.index
            val minCell = graph[in2D(index.first,index.second)]

            if (top.value > minCell.value) {
                continue
            }

            for (n in top.neighbors) {
                val i = in2D(n.first, n.second)
                if ((n.first < 0) or (n.first >= dimX) or (n.second < 0) or (n.second >= dimY)) {
                    continue
                }
                val nCell = graph[i]
                if (nCell.value == -1.0) { continue } // Cell is an obstacle

                val straight = (minCell.index.first == n.first) or (minCell.index.second == n.second)

                val newVal = minCell.value + (if (straight) straightCost else diagCost)
                if (newVal < nCell.value) {
                    nCell.value = newVal
                    nCell.parent = minCell
                    queue.add(nCell)
                }
            }
        }

        val path = mutableListOf<Pair<Int, Int>>()
        var lastCell = graph[in2D(target.first, target.second)]
        path.add(lastCell.index)
        while (lastCell.index != start) {
            lastCell = lastCell.parent!!
            path.add(lastCell.index)
        }

        return path
    }
}