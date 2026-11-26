package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.Constants

class BreakBeamPair(
    private val hardwareMap: HardwareMap,
    private val beam1Name: String = Constants.HardwareNames.BREAK_BEAM_1,
    private val beam2Name: String = Constants.HardwareNames.BREAK_BEAM_2
) : HardwareComponent {

    private lateinit var beam1: DigitalChannel
    private lateinit var beam2: DigitalChannel

    private var beam1Time = ElapsedTime()
    private var beam2Time = ElapsedTime()

    private var beam1WasBlocked = false
    private var beam2WasBlocked = false
    var state = false

    private val sensorDelay = Constants.Transfer.BREAK_BEAM_TIME_MS

    override fun init() {
        beam1 = hardwareMap.get(DigitalChannel::class.java, beam1Name)
        beam2 = hardwareMap.get(DigitalChannel::class.java, beam2Name)

        beam1.mode = DigitalChannel.Mode.INPUT
        beam2.mode = DigitalChannel.Mode.INPUT
    }

    override fun update(dt: Double) {
        val beam1IsBlocked = !beam1.state
        val beam2IsBlocked = !beam2.state

        if (!beam1WasBlocked and beam1IsBlocked) { // Beam 1 became active
            beam1Time.reset()
        }
        if (!beam2WasBlocked and beam2IsBlocked) { // Beam 2 became active
            beam2Time.reset()
        }

        // If at least one beam has been broken for long enough, report a detection
        state = (beam1IsBlocked and (beam1Time.milliseconds() > sensorDelay)) or (beam2IsBlocked and (beam2Time.milliseconds() > sensorDelay))

        beam1WasBlocked = !beam1.state
        beam2WasBlocked = !beam2.state
    }
}