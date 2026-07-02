package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.prism.Color
import org.firstinspires.ftc.teamcode.prism.GoBildaPrismDriver
import org.firstinspires.ftc.teamcode.prism.PrismAnimations

class LED(
    private val hardwareMap: HardwareMap,
    private val driverName: String = Constants.HardwareNames.LED_DRIVER,
) : HardwareComponent {
    private data class SolidState(
        val color: Color,
        val start: Int,
        val end: Int,
        val brightness: Int
    )

    private val layerCache =
        mutableMapOf<GoBildaPrismDriver.LayerHeight, SolidState>()

    private lateinit var driver: GoBildaPrismDriver

    override fun init() {
        driver = hardwareMap.get(
            GoBildaPrismDriver::class.java,
            driverName
        )
    }

    // Only update prism if the state changes
    private fun setSolidIfChanged(
        layer: GoBildaPrismDriver.LayerHeight,
        color: Color,
        start: Int,
        end: Int,
        brightness: Int
    ) {
        val newState = SolidState(color, start, end, brightness)
        val oldState = layerCache[layer]

        if (newState == oldState) return

        layerCache[layer] = newState

        driver.insertAndUpdateAnimation(
            layer,
            PrismAnimations.Solid(color).apply {
                this.startIndex = start
                this.stopIndex = end
                this.brightness = brightness
            }
        )
    }

    fun setColor(color: Color, start: Int = 0, end: Int = 11, brightness: Int = 100) {
        setSolidIfChanged(
            GoBildaPrismDriver.LayerHeight.LAYER_0,
            color,
            start,
            end,
            brightness
        )
    }



    fun setAnimation(animation: PrismAnimations.AnimationBase) {
        driver.insertAndUpdateAnimation(
            GoBildaPrismDriver.LayerHeight.LAYER_0,
            animation,
        )
    }

    fun clear() {
        layerCache.clear()
        driver.clearAllAnimations()
    }
}
