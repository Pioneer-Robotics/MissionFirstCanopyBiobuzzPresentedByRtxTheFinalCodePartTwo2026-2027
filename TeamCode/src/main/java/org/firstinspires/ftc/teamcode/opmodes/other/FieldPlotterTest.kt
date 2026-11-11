package org.firstinspires.ftc.teamcode.opmodes.other

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Field Plot Test", group = "Dashboard")
class FieldPlotterTest : OpMode() {
    val FIELD_SIZE = 144.0 // inches
    val HALF_FIELD = FIELD_SIZE / 2
    val POINT_RADIUS = 3.0
    val TOUCHPAD_SENSITIVITY = 72.0

    fun fieldToDashboard(fieldX: Double, fieldY: Double): Pair<Double, Double> {
        return Pair(fieldY - HALF_FIELD, -(fieldX - HALF_FIELD))
    }

    val dashboard: FtcDashboard = FtcDashboard.getInstance()

    var fieldX = HALF_FIELD
    var fieldY = HALF_FIELD

    var fingerWasDown = false
    var lastTouchX = 0.0
    var lastTouchY = 0.0

    override fun init() {

    }

    override fun loop() {
        updatePointFromTouchpad()

        val (plotX, plotY) = fieldToDashboard(fieldX, fieldY)

        val packet = TelemetryPacket()
        val field = packet.fieldOverlay()

        field.setStroke("#3F51B5")
        field.setFill("#3F51B5")
        field.fillCircle(plotX, plotY, POINT_RADIUS)

        packet.put("Field X (in)", fieldX)
        packet.put("Field Y (in)", fieldY)
        packet.put("Finger X", gamepad1.touchpad_finger_1_x)
        packet.put("Finger Y", gamepad1.touchpad_finger_2_x)

        dashboard.sendTelemetryPacket(packet)
    }

    private fun updatePointFromTouchpad() {
        val fingerDown = gamepad1.touchpad_finger_1

        if (fingerDown) {
            val touchX = gamepad1.touchpad_finger_1_x.toDouble()
            val touchY = gamepad1.touchpad_finger_1_y.toDouble()

            if (fingerWasDown) {
                val dx = touchX - lastTouchX
                val dy = touchY - lastTouchY

                fieldX += dx * TOUCHPAD_SENSITIVITY / 2.0
                fieldY += dy * TOUCHPAD_SENSITIVITY / 2.0

                fieldX = fieldX.coerceIn(0.0, FIELD_SIZE)
                fieldY = fieldY.coerceIn(0.0, FIELD_SIZE)
            }

            lastTouchX = touchX
            lastTouchY = touchY
        }

        fingerWasDown = fingerDown
    }
}