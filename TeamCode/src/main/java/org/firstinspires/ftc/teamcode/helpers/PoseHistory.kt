package org.firstinspires.ftc.teamcode.helpers

import kotlin.math.abs

class PoseHistory(private val maxAgeMs: Long) {
    private data class Sample(val timeMs: Long, val pose: Pose) {}
    private val history = ArrayDeque<Sample>()

    fun record(pose: Pose) {
        val now = System.currentTimeMillis()
        // Add pose sample to end of history
        history.addLast(Sample(now, pose))
        // Remove old samples based on time
        while (!history.isEmpty() && now - (history.firstOrNull()?.timeMs ?: 0) > maxAgeMs) {
            history.removeFirstOrNull()
        }
    }

    fun getPoseAt(timestampMs: Long): Pose? {
        var best = history.firstOrNull()?.pose
        var bestDiff = Long.MAX_VALUE
        for (s in history) {
            val diff = abs(s.timeMs - timestampMs)
            if (diff < bestDiff) {
                best = s.pose
                bestDiff = diff
            }
        }
        return best
    }
}
