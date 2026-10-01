package org.firstinspires.ftc.teamcode.vision

import android.graphics.Color
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor.Blob
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor.BlobCriteria
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor.ContourMode
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor.MorphOperationType
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor.Util
import org.firstinspires.ftc.vision.opencv.ColorRange
import org.firstinspires.ftc.vision.opencv.ImageRegion

class ColorBlob(
    targetColor: ColorRange? = null, // null = detects ALL blobs
    draw: Boolean = false,
) : Processor {
    override val processor: TimedColorBlobProcessor = TimedColorBlobProcessor(
            ColorBlobLocatorProcessor
            .Builder()
            .apply { targetColor?.let { setTargetColorRange(it) } } // Only set if provided
            .setContourMode(ContourMode.EXTERNAL_ONLY)
            .setBlurSize(3)
            .setDilateSize(10)
            .setErodeSize(15)
            .setMorphOperationType(MorphOperationType.OPENING)
            .setRoi(ImageRegion.asUnityCenterCoordinates(-0.9, 0.9, 0.9, -0.9)) // Eliminate detection near edges
            .setDrawContours(draw)
            .setBoxFitColor(0) // Disable the drawing of rectangles
            .setCircleFitColor(Color.rgb(255, 255, 0)) // Draw a circle
            .build()
    )

    /**
     * Get all detected blobs.
     */
    fun getBlobs(): List<Blob> = processor.locator.blobs

    /**
     * Get blobs filtered by specific criteria.
     */
    fun getBlobsByCriteria(
        criteria: BlobCriteria,
        minValue: Double,
        maxValue: Double,
    ): List<Blob> {
        val blobs = getBlobs().toMutableList()
        // In-place, post-process mutation of blobs object
        Util.filterByCriteria(criteria, minValue, maxValue, blobs)
        return blobs
    }

    /**
     * Get the largest blob by contour area.
     */
    fun getLargestBlob(): Blob? = getBlobs().maxByOrNull { it.contourArea }

    /**
     * Get the most circular blob.
     */
    fun getMostCircular(): Blob? = getBlobs().maxByOrNull { it.circularity }

    /**
     * Get the densest blob (highest fill ratio).
     */
    fun getDensest(): Blob? = getBlobs().maxByOrNull { it.density }
}
