package org.firstinspires.ftc.teamcode.helpers.graph

import com.pedropathing.api.Paths
import com.pedropathing.math.Pose
import com.pedropathing.paths.Path
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
    private lateinit var start: Pair<Double, Double>
    private lateinit var target: Pair<Double, Double>
    private var startBlocked = true
    private var targetBlocked = true

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

    fun setStart(start: Pair<Double, Double>) {
        this.start = start
        val startIn = fieldToIn(start)
        startBlocked = (graph[in2D(startIn.first, startIn.second)].value == -1.0)
    }

    fun setTarget(target: Pair<Double, Double>) {
        this.target = target
        val targetIn = fieldToIn(target)
        targetBlocked = (graph[in2D(targetIn.first, targetIn.second)].value == -1.0)
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

        var startIn = fieldToIn(start)
        var targetIn = fieldToIn(target)

        // If start or target are outside of path, rectify them
        if (startBlocked) { startIn = findCloseReachable(startIn) }
        if (targetBlocked) { targetIn = findCloseReachable(targetIn) }

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

        return path.reversed().toMutableList()
    }

    fun pathToBezier(pts: MutableList<Pair<Double, Double>>, smoothness: Double = 0.25): MutableList<Pose> {
        var k = smoothness
        if (smoothness == 0.0) {
            k = 1e-6 // It breaks if smoothness is 0.0 ¯\_(ツ)_/¯
        }

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

        // If path started/ended in a boundary and was rectified, add back the start and target
        if (startBlocked) { bezier.add(start.toPose()) }
        if (targetBlocked) { bezier.add(0, target.toPose()) }

        val tangents = calculateTangents(bezier)
        val pts = mutableListOf<Pose>() // Every 4 points makes up a cubic Bezier

        for (i in 0..< bezier.size-1) {
            val cur = bezier[i]
            val next = bezier[i+1]
            val dist = cur.distance(next)
            pts.add(cur) // First point
            pts.add(cur + tangents[i] * k * dist) // Control point 1
            pts.add(next - tangents[i + 1] * k * dist) // Control point 2
            pts.add(next) // Last point
        }

        return pts
    }

    fun findCloseReachable(p: Pair<Int, Int>): Pair<Int, Int> {
        val directions = mutableListOf(Pair(1,0), Pair(1,1), Pair(0,1), Pair(-1,1), Pair(-1,0), Pair(-1,-1), Pair(0,-1), Pair(1,-1)) // 8 Diagonals to explore
        var i = 1
        while (i < dimX/2) { // Exit if an unreasonable amount of time has passed
            for (dir in directions) {
                val pNew = p + i * dir
                val pFlatIn = in2D(pNew.first, pNew.second)

                if ((pFlatIn < 0) or (pFlatIn >= dimX*dimY)) { // Index out of range, I don't feel like handling this properly
                    continue
                }

                if (graph[pFlatIn].value != -1.0) { // Not on obstacle
                    return pNew
                }
            }
            i++
        }
        return Pair(-1, -1)
    }

    /**
     * Takes a list of cubic bezier control points and returns a Pedro compound path
     * Every 4 points makes up a Bézier curve
     */
    fun toPedroPath(path: MutableList<Pose>): Path {
        var pedroPath = Paths.curve(path[0], path[1], path[2], path[3]).tangent()
        for (i in 4..path.size-4 step 4) {
            pedroPath = Paths.path(
                pedroPath,
                Paths.curve(path[i], path[i+1], path[i+2], path[i+3]).tangent()
            )
        }
        return pedroPath
    }

    private fun calculateTangents(pts: MutableList<Pose>): MutableList<Pose> {
        val tangents = mutableListOf<Pose>()
        val numPoints = pts.size
        tangents.add((pts[1] - pts[0]).unit())
        for (i in 1..< numPoints-1) {
            val incoming = (pts[i] - pts[i-1]).unit()
            val outgoing = (pts[i+1] - pts[i]).unit()
            val avg = (incoming + outgoing).unit()
            tangents.add(avg)
        }
        tangents.add((pts[numPoints-1] - pts[numPoints-2]).unit())
        return tangents
    }

    private fun Pose.unit() : Pose {
        val length = sqrt(this.x() * this.x() + this.y() * this.y())
        return if (length != 0.0) this / length else Pose(0.0, 0.0)
    }
}
