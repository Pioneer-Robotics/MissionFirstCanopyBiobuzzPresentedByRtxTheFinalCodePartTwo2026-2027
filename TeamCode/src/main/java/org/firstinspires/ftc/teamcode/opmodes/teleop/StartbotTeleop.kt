package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.BotType
import org.firstinspires.ftc.teamcode.Constants
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.helpers.Toggle
import org.firstinspires.ftc.teamcode.opmodes.BaseOpMode

@TeleOp(name = "Starter Bot Teleop")
class StartbotTeleop: BaseOpMode(BotType.GOBILDA_STARTER_BOT) {

    private var drivePower = Constants.Drive.DEFAULT_POWER
    private var incDrivePower: Toggle = Toggle(false)
    private var decDrivePower: Toggle = Toggle(false)
    private var intakeToggle: Toggle = Toggle(false)
    private var transferToggle: Toggle = Toggle(false)
    private var flywheelToggle: Toggle = Toggle(false)

    override fun onInit() {
        bot = Bot.fromType(BotType.GOBILDA_STARTER_BOT, hardwareMap)
    }

    override fun onLoop() {
        drive()
        updateDrivePower()
        handleIntake()
        handleTransfer()
        handleFlywheel()
    }

    private fun drive() {
        val direction = Pose(gamepad1.left_stick_x.toDouble(), -gamepad1.left_stick_y.toDouble())
        bot.mecanumBase?.setDrivePower(
            Pose(
                vx = direction.x,
                vy = direction.y,
                omega = gamepad1.right_stick_x.toDouble(),
            ),
            drivePower,
            Constants.Drive.MAX_MOTOR_VELOCITY_TPS
        )
    }

    private fun updateDrivePower() {
        incDrivePower.toggle(gamepad1.right_bumper)
        decDrivePower.toggle(gamepad1.left_bumper)
        if (incDrivePower.justChanged) {
            drivePower += 0.1
        }
        if (decDrivePower.justChanged) {
            drivePower -= 0.1
        }
        drivePower = drivePower.coerceIn(0.1, 1.0)
    }

    private fun handleIntake(){
//        intakeToggle.toggle(gamepad1.square)
//        if (intakeToggle.state) {
//            bot.starterIntake?.forward()
//            bot.intakeServos?.turnOn()
//        } else {
//            bot.starterIntake?.stop()
//            bot.intakeServos?.turnOff()
//        }
    }

    private fun handleTransfer(){
        transferToggle.toggle(gamepad1.circle)
        if (transferToggle.state){
            bot.transferServo?.turnOn()
        } else {
            bot.transferServo?.turnOff()
        }
    }

    private fun handleFlywheel(){
        flywheelToggle.toggle(gamepad1.dpad_right)
        if (flywheelToggle.state){
            bot.flywheel?.targetVelocity = 0.8
        } else {
            bot.flywheel?.targetVelocity = 0.0
        }
    }


}