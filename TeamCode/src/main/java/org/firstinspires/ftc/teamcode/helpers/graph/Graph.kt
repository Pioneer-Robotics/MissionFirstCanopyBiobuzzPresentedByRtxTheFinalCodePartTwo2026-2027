package org.firstinspires.ftc.teamcode.helpers.graph

import com.pedropathing.api.PoseFactory
import com.pedropathing.math.Pose
import org.firstinspires.ftc.teamcode.biobuzz.Points
import org.firstinspires.ftc.teamcode.general.AllianceColor
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sqrt

// Class for representing the field as an array of cells (nodes) to solve path optimization problems
// The field is represented as a 1D instead of a 2D array for easier computation
class Graph(
    val dimX: Int,
    val dimY: Int,
    val addField: Boolean = false
) {
    // Graph is a collapsed 2D array representing a grid of cells on the field, which algorithms use to find optimal paths
    /*private*/ val graph = Array(dimX*dimY) { GraphCell() }
    lateinit var start: Pair<Double, Double>
    lateinit var target: Pair<Double, Double>
    val obstacles = GraphObstacleConglomerate(dimX, dimY, tol = 36.0)

    init {
        if (addField) { addFieldObstacles() }
        setupGraph()
    }

    fun in2D(x: Int, y: Int): Int {
        return dimX*x + y
    }
    fun fieldToIn(p: Pair<Double, Double>): Pair<Int,Int> {
        val p2 = p // Set bottom left as (0, 0) for internal
        return Pair(dimX/360.0 * p2.first, dimY/360.0 * p2.second).toInt()
    }
    fun inToField(p: Pair<Int, Int>): Pair<Double,Double> {
        return Pair(360.0/dimX * p.first, 360.0/dimY * p.second) // Set center to (0, 0) for output
    }

    fun addFieldObstacles() {
        val pts = Points(AllianceColor.RED)

        // Walls
        obstacles.addObs(pts.fieldUR, pts.fieldDR)
        obstacles.addObs(pts.fieldDR, pts.fieldDL)
        obstacles.addObs(pts.fieldDL, pts.fieldUL)
        obstacles.addObs(pts.fieldUL, pts.fieldUR)

        // Hive
        obstacles.addObs(pts.hiveUR, pts.hiveDR)
        obstacles.addObs(pts.hiveDL, pts.hiveUL)

        // Flowers
        obstacles.addObs(pts.flower1DL, pts.flower1DR)
        obstacles.addObs(pts.flower2DL, pts.flower2DR)
        obstacles.addObs(pts.flower3DL, pts.flower3DR)
        obstacles.addObs(pts.flower4DL, pts.flower4DR)
    }

    private fun setupGraph() {
        for (i in 0..<dimX) {
            for (j in 0..<dimY) {
                graph[in2D(i,j)].index = Pair(i,j)
                graph[in2D(i,j)].setNeighbors()
                obstacles.applyObs(graph[in2D(i,j)])
            }
        }
    }

    fun resetGraph() {
        for (i in 0..<dimX) {
            for (j in 0..<dimY) {
                obstacles.applyObs(graph[in2D(i,j)])
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

    fun pathToBezier(pts: MutableList<Pair<Double, Double>>): MutableList<Pose> {
        val bezier = mutableListOf<Pose>()
        val bezIn = mutableListOf<Int>()
        var d0 = pts[1] - pts[0]

        bezier.add(pts[0].toPose())
        bezIn.add(0)

        for (i in 1..<pts.size-1) {
            val d = pts[i+1] - pts[i]
            if ((d - d0).abs() > 0.0001) { // pts[i+1] is no longer colinear, pts[i] is the last in the line
                                           // Approximate inequality used cuz floating point error
                d0 = pts[i+1] - pts[i]
                if (bezIn.last() + ceil(dimX.toDouble() / 12.0) < i) { // Must be a few points in between every bezier point
                    bezier.add(pts[i].toPose())
                    bezIn.add(i)
                }
            }
        }
        bezier.add(pts[pts.size-1].toPose())
        return bezier
    }
}
