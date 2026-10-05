package org.firstinspires.ftc.teamcode.helpers

class Graph(
    val dimX: Int,
    val dimY: Int
) {
    private val graph = Array(dimX) { Array(dimY) { GraphCell() } }
    private lateinit var start: Pair<Int, Int>
    private lateinit var target: Pair<Int, Int>

    init {
        indexGraph()
        addObstacles()
    }

    private fun indexGraph() {
        for (i in 0..<dimX) {
            for (j in 0..<dimY) {
                graph[i][j].index = Pair(i,j)
            }
        }
    }

    private fun addObstacles() {
        // TODO: Make work
    }

    private fun resetGraph() {
        for (i in 0..<dimX) {
            for (j in 0..<dimY) {
                graph[i][j].value = Double.MAX_VALUE
                // TODO: might have to reset parents but idk how
            }
        }
    }

    fun setStart(start: Pair<Int, Int>) {
        this.start = start
    }

    fun setTarget(target: Pair<Int, Int>) {
        this.target = target
    }

    fun dijkstraPath(straightCost: Double = 1.0, diagCost: Double = 1.41421): MutableList<Pair<Int, Int>> {

        val uncheckedList = graph.flatten().toMutableList()
        lateinit var lastCell: GraphCell

        while (uncheckedList.isNotEmpty()) {
            val minCell = (uncheckedList.minBy { it.value })

            for (n in minCell.neighbors) {
                val straight = (minCell.index.first == n.first) or (minCell.index.second == n.second)

                val newVal = minCell.value + (if (straight) straightCost else diagCost)
                if (newVal < graph[n.first][n.second].value) {
                    graph[n.first][n.second].value = newVal
                    graph[n.first][n.second].parent = minCell
                }

                if (minCell.index == target) {
                    lastCell = minCell
                    break
                }

                uncheckedList.remove(minCell)
            }
        }
        val path = mutableListOf<Pair<Int, Int>>()
        path.add(lastCell.index)
        while (lastCell.index != start) {
            lastCell = lastCell.parent!!
            path.add(lastCell.index)
        }
        path.add(start)

        return path
    }

    fun indexFlatList(x: Int, y: Int): Int {
        return dimX*x + y
    }

}