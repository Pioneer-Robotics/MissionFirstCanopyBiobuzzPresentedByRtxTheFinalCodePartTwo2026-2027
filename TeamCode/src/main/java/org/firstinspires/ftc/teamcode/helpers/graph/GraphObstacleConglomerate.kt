package org.firstinspires.ftc.teamcode.helpers.graph

class GraphObstacleConglomerate(val dimX: Int, val dimY: Int, val tol: Double) {
    val obstacles = mutableListOf<GraphObstacle>()

    fun addObs(p1: Pair<Double, Double>, p2: Pair<Double, Double>) {
        obstacles.add(GraphObstacle(p1, p2, dimX, dimY, tol))
    }

    fun applyObs(cell: GraphCell) {
        val p = cell.index.toDouble()
        var apply = false
        for (ob in obstacles) {
            if (ob.shouldApply(p)) {
                apply = true
                break // No need to apply an obstacle more than once
            }
        }
        if (apply) cell.value = -1.0 else cell.value = Double.MAX_VALUE
    }
}