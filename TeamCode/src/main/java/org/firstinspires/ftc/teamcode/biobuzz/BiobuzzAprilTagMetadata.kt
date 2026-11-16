package org.firstinspires.ftc.teamcode.biobuzz

import org.firstinspires.ftc.robotcore.external.navigation.Quaternion
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles
import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.helpers.MathUtils
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase

private val tagLibrary by lazy { AprilTagGameDatabase.getBioBuzzTagLibrary() }


enum class BiobuzzAprilTagMetadata (
    val id: Int,
    val alliance: AllianceColor
){
    BLUE_R(42, AllianceColor.BLUE),
    BLUE_L(38, AllianceColor.BLUE),
    RED_R(34, AllianceColor.RED),
    RED_L(30, AllianceColor.RED),
    ;

    val tagOfficialName: String
        get() = tagLibrary.lookupTag(id).name

    private val orientationQ: Quaternion by lazy {
        tagLibrary.lookupTag(id).fieldOrientation
    }

    private val orientationYPR: YawPitchRollAngles = MathUtils.quaternionToEuler(orientationQ)

}