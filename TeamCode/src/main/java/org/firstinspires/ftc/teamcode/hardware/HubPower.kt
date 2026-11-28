package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.helpers.PowerMeter

class HubPower (
    private val hardwareMap: HardwareMap
) : HardwareComponent {

    private lateinit var controlHub: LynxModule
    private lateinit var expansionHub: LynxModule
    val controlMeter = PowerMeter()
    val expansionMeter = PowerMeter()

    override fun init() {
        controlHub = hardwareMap.get(LynxModule::class.java, "Control Hub")
        expansionHub = hardwareMap.get(LynxModule::class.java, "Expansion Hub 2")
        controlMeter.reset()
        expansionMeter.reset()
    }

    override fun update(dt: Double) {
        controlMeter.update(controlHub.getCurrent(CurrentUnit.MILLIAMPS))
        expansionMeter.update(expansionHub.getCurrent(CurrentUnit.MILLIAMPS))
    }

    fun getChargeUsed(): Double {
        return controlMeter.getTotalChargeUsed() + expansionMeter.getTotalChargeUsed()
    }

    fun getAverageCurrent(): Double {
        return controlMeter.getAvCurrent() + expansionMeter.getAvCurrent()
    }

    fun getCurrentDraw(): Double {
        return controlHub.getCurrent(CurrentUnit.MILLIAMPS) + expansionHub.getCurrent(CurrentUnit.MILLIAMPS)
    }
}