package org.firstinspires.ftc.teamcode.helpers.graph

import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

// Class for representing the field as an array of cells (nodes) to solve path optimization problems
// The field is represented as a 1D instead of a 2D array for easier computation
class Graph(
    val dimX: Int,
    val dimY: Int
) {
    // Graph is a collapsed 2D array representing a grid of cells on the field, which algorithms use to find optimal paths
    private val graph = Array(dimX*dimY) { GraphCell() }
    lateinit var start: Pair<Double, Double>
    lateinit var target: Pair<Double, Double>
    var obs1 = GraphObstacle(Pair(130.0, 220.0), Pair(220.0, 130.0), dimX, dimY)

    init {
        setupGraph()
    }

    fun in2D(x: Int, y: Int): Int {
        return dimX*x + y
    }
    fun fieldToIn(p: Pair<Double, Double>): Pair<Int,Int> {
        return Pair(dimX/366.0 * p.first, dimY/366.0 * p.second).toInt()
    }
    fun inToField(p: Pair<Int, Int>): Pair<Double,Double> {
        return Pair(366.0/dimX * p.first, 366.0/dimY * p.second)
    }

    private fun setupGraph() {
        for (i in 0..<dimX) {
            for (j in 0..<dimY) {
                graph[in2D(i,j)].index = Pair(i,j)
                graph[in2D(i,j)].setNeighbors()
                obs1.applyObs(graph[in2D(i,j)])
            }
        }
    }

    fun resetGraph() {
        for (i in 0..<dimX) {
            for (j in 0..<dimY) {
                graph[in2D(i,j)].value = Double.MAX_VALUE
                obs1.applyObs(graph[in2D(i,j)])
                graph[in2D(i,j)].parentIndex = null
            }
        }
    }

    fun dijkstraPath(aStar: Boolean = true, straightCost: Double = 1.0, diagCost: Double = sqrt(2.0)): MutableList<Pair<Double, Double>> {

        val startIn = fieldToIn(start)
        val targetIn = fieldToIn(target)

        val queue = PriorityQueue<GraphCell> {
                c1, c2 -> (c1.value + c1.heuristic).compareTo(c2.value + c2.heuristic) // Min heap comparator
        }

        for (c in graph) {
            if (aStar) {
                val dx = abs(c.index.first - targetIn.first)
                val dy = abs(c.index.second - targetIn.second)
                c.heuristic = dx + dy + (sqrt(2.0) - 2) * min(dx, dy)
            } else {
                c.heuristic = 0.0
            }
        }

        graph[in2D(startIn.first,startIn.second)].value = 0.0
        queue.add(graph[in2D(startIn.first,startIn.second)])
        while (queue.isNotEmpty()) {
            val top = queue.poll()!!
            val index = top.index
            if (aStar and (index == targetIn)) { break } // With A*, break when the current cell we are checking is the end

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
                    nCell.parentIndex = minCell.index
                    queue.add(nCell)
                }
            }
        }

        val path = mutableListOf<Pair<Double, Double>>()
        var lastCell = graph[in2D(targetIn.first, targetIn.second)]
        var lastCellIn = lastCell.index

        path.add(inToField(lastCellIn))
        lastCellIn = lastCell.parentIndex!!
        while (lastCellIn != startIn) {
            path.add(inToField(lastCellIn))
            lastCell = graph[in2D(lastCellIn.first, lastCellIn.second)]
            lastCellIn = lastCell.parentIndex!!
        }
        path.add(inToField(lastCellIn))

        return path
    }
}