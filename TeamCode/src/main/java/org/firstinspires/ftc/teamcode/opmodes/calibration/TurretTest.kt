package org.firstinspires.ftc.teamcode.opmodes.calibration

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.hardware.Turret

@TeleOp
class TurretTest : OpMode() {

    private lateinit var turret: Turret

    override fun init() {
        turret = Turret(hardwareMap).apply { init() }
        turret.gotoPosition(0.0)
    }

    override fun loop() {
        if (gamepad1.right_trigger > 0.02) {
            turret.gotoPosition(gamepad1.right_trigger.toDouble())
        } else if (gamepad1.left_trigger > 0.02) {
            turret.gotoAngle(gamepad1.left_trigger.toDouble()*360)
        } else {
            turret.gotoPosition(0.0)
        }

        telemetry.addData("Turret Position", turret.getPosition())
        telemetry.addData("Turret Angle", turret.getAngle())
    }

}