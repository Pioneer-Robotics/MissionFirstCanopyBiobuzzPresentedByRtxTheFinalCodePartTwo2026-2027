package org.firstinspires.ftc.teamcode.opmodes.calibration

import com.acmerobotics.dashboard.FtcDashboard
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.BotType
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.opmodes.BaseOpMode

//@Disabled
@Autonomous(name = "Forward KV Tuner", group = "Calibration")
class ForwardKVTuner : BaseOpMode(BotType.COMP_BOT) {
    override fun onInit() {
        FtcDashboard.getInstance().telemetry.addData("Velocity (cm/s)", 0.0)
        FtcDashboard.getInstance().telemetry.addData("Target (cm/s)", 50.0)
        FtcDashboard.getInstance().telemetry.update()
    }

    override fun onLoop() {
        bot.mecanumBase!!.setDriveVA(
            Pose(vy = 50.0, ay = 0.0), // 50 cm/s forward
        )
        telemetry.addData("Velocity (cm/s)", bot.pinpoint!!.pose.vy)
        telemetry.addData("Position (cm)", bot.pinpoint!!.pose.y)
        FtcDashboard.getInstance().telemetry.addData("Velocity (cm/s)", bot.pinpoint!!.pose.vy)
        FtcDashboard.getInstance().telemetry.addData("Target (cm/s)", 50.0)
        FtcDashboard.getInstance().telemetry.update()
    }
}
