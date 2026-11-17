package org.firstinspires.ftc.teamcode.biobuzz

import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection

/** AUDIENCE is the manual's "audience side"; SCORING is the side opposite it. */
enum class Side { AUDIENCE, SCORING }

/** Ordinal is the AprilTag Cluster block: 30-33, 34-37, 38-41, 42-45 (manual 9.9). */
enum class Cell(
    val alliance: AllianceColor,
    val side: Side,
) {
    RED_SCORING(AllianceColor.RED, Side.SCORING),
    RED_AUDIENCE(AllianceColor.RED, Side.AUDIENCE),
    BLUE_AUDIENCE(AllianceColor.BLUE, Side.AUDIENCE),
    BLUE_SCORING(AllianceColor.BLUE, Side.SCORING),
    ;

    /** Matches AprilTagClusterMetadata.name. */
    val label: String get() = "$alliance $side"

    companion object {
        val TAGS = 30..45

        fun of(id: Int): Cell? = if (id in TAGS) entries[(id - TAGS.first) / 4] else null

        /** Null for a single detection: the BioBuzz library defines clusters only. */
        fun of(detection: AprilTagDetection): Cell? =
            (detection as? AprilTagClusterDetection)
                ?.let { c -> entries.firstOrNull { it.label == c.metadata.name } }
    }
}
