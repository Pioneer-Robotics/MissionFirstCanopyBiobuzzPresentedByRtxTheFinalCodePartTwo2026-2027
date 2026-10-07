package org.firstinspires.ftc.teamcode.helpers.graph

/**
 * Creates lines that are interpreted as obstacles for path optimization problems
 * @param p1 Field coordinate of start of obstacle (cm)
 * @param p2 Field coordinate of end of obstacle (cm)
 * @param dimX Number of nodes/cells in the X direction of the graph
 * @param dimY Number of nodes/cells in the Y direction of the graph
 * @param tol Distance around the obstacle to avoid (cm)
 */
class GraphObstacle(val p1: Pair<Double, Double>,
                    val p2: Pair<Double, Double>,
                    val dimX: Int, val dimY: Int,
                    val tol: Double = 30.0) {

    val graphP1 = toGraphCoord(p1, dimX.toDouble(), dimY.toDouble())
    val graphP2 = toGraphCoord(p2, dimX.toDouble(), dimY.toDouble())
    val graphTol = dimX * tol / 366.0 // xDim and yDim should be the same

    private fun toGraphCoord(p: Pair<Double, Double>, xDim: Double, yDim: Double): Pair<Double, Double> {
        return Pair(xDim * p.first/366, yDim * p.second/366)
    }

    private fun distToLineSeg(s1: Pair<Double, Double>, s2: Pair<Double, Double>, p: Pair<Double, Double>): Double {
        val t = (p-s1).dot(s2-s1) / (s2-s1).dot((s2-s1)) // Closest t on infinite line
        val tBound = t.coerceIn(0.0, 1.0)                               // Bind t from 0 to 1 (the whole line)
        return (s1 + tBound*(s2 - s1) - p).abs()                        // Return distance between p and the point on our line
    }

    fun applyObs(cell: GraphCell) {
        val p = cell.index.toDouble()
        val dist = distToLineSeg(graphP1, graphP2, p)
        if (dist <= graphTol) {
            cell.value = -1.0
        } else {
            cell.value = Double.MAX_VALUE
        }
    }
}