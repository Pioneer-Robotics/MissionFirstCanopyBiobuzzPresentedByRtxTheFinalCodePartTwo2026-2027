package org.firstinspires.ftc.teamcode.opmodes.other

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.Bot
import org.firstinspires.ftc.teamcode.BotType
import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.hardware.Camera
import org.firstinspires.ftc.teamcode.opmodes.BaseOpMode
import org.firstinspires.ftc.teamcode.vision.AprilTag
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import kotlin.math.abs


@TeleOp(name="Biobuzz AprilTag Test")
class BiobuzzAprilTagsTest: BaseOpMode(botType = BotType.CUSTOM) {
    private val processor: AprilTagProcessor = AprilTag(draw = true).processor
    var errorDegrees: Double? = 0.0
    val allianceColor = AllianceColor.RED
    override fun onInit() {
        bot = Bot
                .builder()
                .add(Camera(hardwareMap, processors = arrayOf()))
                .build()
    }

    override fun onLoop(){
        var detections = processor.detections

//        var filteredDetections = detections?.filter {
//            it.id
//        }


        val scorableClusters = detections
            .filterIsInstance<AprilTagClusterDetection>()
            .filter { cluster ->
                cluster.metadata.name.startsWith(allianceColor.name.take(1), true) &&
                        abs(cluster.ftcPose.roll) < 90.0
        }

        var targetCluster = scorableClusters.get(0)

//        targetCluster.

        errorDegrees = targetCluster.ftcPose?.bearing

    }




}