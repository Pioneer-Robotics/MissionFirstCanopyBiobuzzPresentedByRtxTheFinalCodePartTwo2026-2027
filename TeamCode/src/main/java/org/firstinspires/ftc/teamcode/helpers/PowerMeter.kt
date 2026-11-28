package org.firstinspires.ftc.teamcode.helpers

import com.qualcomm.robotcore.util.ElapsedTime
import kotlin.time.DurationUnit

class PowerMeter() {
    private val timer = Chrono(autoUpdate = false, units = DurationUnit.HOURS)
    private var totChargeUsed = 0.0 // Q = I*t. Battery gives capacity in mAh, so this is appropriate
    private var n = 0.0
    private var totT = 0.0

    init {
        timer.reset()
    }

    fun update(current: Double) {
        n++
        totChargeUsed += timer.dt * current
        totT += timer.dt
        timer.reset()
    }

    fun reset() {
        totT = 0.0
        n = 0.0
        timer.reset()
    }

    fun getTotalChargeUsed(): Double {
        return totChargeUsed
    }
    fun getAvCurrent(): Double {
        return totChargeUsed / totT
    }
}