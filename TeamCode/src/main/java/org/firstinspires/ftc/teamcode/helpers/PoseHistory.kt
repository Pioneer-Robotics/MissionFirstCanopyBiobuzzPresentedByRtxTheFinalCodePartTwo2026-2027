package org.firstinspires.ftc.teamcode.helpers

import kotlin.math.abs

class PoseHistory(private val maxAgeMs: Long) {
    private data class Sample(val timeNs: Long, val pose: Pose) {}
    private val history = ArrayDeque<Sample>()

    fun record(pose: Pose) {
        val now = System.nanoTime()
        // Add pose sample to end of history
        history.addLast(Sample(now, pose))
        // Remove old samples based on time
        while (!history.isEmpty() && now - (history.firstOrNull()?.timeNs ?: 0) > maxAgeMs * 1_000_000) {
            history.removeFirstOrNull()
        }
    }

    fun getPoseAt(timestampNs: Long): Pose? {
        var best = history.firstOrNull()?.pose
        var bestDiff = Long.MAX_VALUE
        for (s in history) {
            val diff = abs(s.timeNs - timestampNs)
            if (diff < bestDiff) {
                best = s.pose
                bestDiff = diff
            }
        }
        return best
    }
}
