package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import kotlin.math.PI

class Turret(
    private val hardwareMap: HardwareMap,
    private val servo1Name: String = "servo1",
    private val servo2Name: String = "servo2"
) : HardwareComponent {

    private lateinit var servoPair: ServoPair

    private val minAngleDeg = 0.0
    private val maxAngleDeg = 360.0

    override fun init() {
        servoPair = ServoPair(hardwareMap, servo1Name, servo2Name, true).apply { init() }
    }

    fun gotoPosition(position: Double) {
        servoPair.position = position
    }

    fun gotoAngle(angleDeg: Double) {

        val modAngle = (angleDeg % 360.0).coerceIn(minAngleDeg, maxAngleDeg)

        // Convert to servo position 0 to 1
        val pos = (modAngle - minAngleDeg) / (maxAngleDeg - minAngleDeg)

        gotoPosition(pos)
    }

    fun getPosition(): Double {
        return servoPair.position
    }

    fun getAngle(): Double {
        return minAngleDeg + getPosition() * (maxAngleDeg - minAngleDeg)
    }

}