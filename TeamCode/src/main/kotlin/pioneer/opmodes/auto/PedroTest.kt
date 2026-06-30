package pioneer.opmodes.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous

import pioneer.Bot
import pioneer.BotType
import pioneer.opmodes.BaseOpMode

@Autonomous(name = "Pedro Test")
class PedroTest : BaseOpMode() {

    override fun onInit() {
        bot = Bot.fromType(BotType.MECANUM_BOT, hardwareMap)
    }

    override fun onLoop() {
    }
}