//package org.firstinspires.ftc.teamcode
//
//import com.pedropathing.control.FilteredPIDFCoefficients
//import com.pedropathing.control.PIDFCoefficients
//import com.pedropathing.follower.Follower
//import com.pedropathing.follower.FollowerConstants
//import com.pedropathing.ftc.FollowerBuilder
//import com.pedropathing.ftc.drivetrains.MecanumConstants
//import com.pedropathing.ftc.localization.constants.PinpointConstants
//import com.pedropathing.paths.PathConstraints
//import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
//import com.qualcomm.robotcore.hardware.DcMotorSimple
//import com.qualcomm.robotcore.hardware.HardwareMap
//
//object PedroConstants {
//    var followerConstants: FollowerConstants? = FollowerConstants()
//        .mass(16.2)
//        .forwardZeroPowerAcceleration(-25.9346931313679598)
//        .lateralZeroPowerAcceleration(-67.342491844080064)
//        .translationalPIDFCoefficients(
//            PIDFCoefficients(
//                0.03,
//                0.0,
//                0.0,
//                0.015
//            )
//        )
//        .translationalPIDFSwitch(4.0)
//        .secondaryTranslationalPIDFCoefficients(
//            PIDFCoefficients(
//                0.4,
//                0.0,
//                0.005,
//                0.0006
//            )
//        )
//        .headingPIDFCoefficients(
//            PIDFCoefficients(
//                0.8,
//                0.0,
//                0.0,
//                0.01
//            )
//        )
//        .secondaryHeadingPIDFCoefficients(
//            PIDFCoefficients(
//                2.5,
//                0.0,
//                0.1,
//                0.0005
//            )
//        )
//        .drivePIDFCoefficients(
//            FilteredPIDFCoefficients(
//                0.1,
//                0.0,
//                0.00035,
//                0.6,
//                0.015
//            )
//        )
//        .secondaryDrivePIDFCoefficients(
//            FilteredPIDFCoefficients(
//                0.02,
//                0.0,
//                0.000005,
//                0.6,
//                0.01
//            )
//        )
//        .drivePIDFSwitch(15.0)
//        .centripetalScaling(0.0005)
//
//    var driveConstants: MecanumConstants? = MecanumConstants()
//        .leftFrontMotorName(Constants.HardwareNames.DRIVE_LEFT_FRONT)
//        .leftRearMotorName(Constants.HardwareNames.DRIVE_LEFT_BACK)
//        .rightFrontMotorName(Constants.HardwareNames.DRIVE_RIGHT_FRONT)
//        .rightRearMotorName(Constants.HardwareNames.DRIVE_RIGHT_BACK)
//
//        .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
//        .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
//        .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
//        .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
//        .xVelocity(78.261926752421046666666666666667)
//        .yVelocity(61.494551922189565)
//
//    var localizerConstants: PinpointConstants? = PinpointConstants()
//        .forwardPodY(0.75)
//        .strafePodX(-6.6)
//        .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
//        .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
//
//    /**
//     * These are the PathConstraints in order:
//     * tValueConstraint, velocityConstraint, translationalConstraint, headingConstraint, timeoutConstraint,
//     * brakingStrength, BEZIER_CURVE_SEARCH_LIMIT, brakingStart
//     *
//     * The BEZIER_CURVE_SEARCH_LIMIT should typically be left at 10 and shouldn't be changed.
//     */
//    var pathConstraints: PathConstraints = PathConstraints(
//        0.995,
//        0.1,
//        0.1,
//        0.009,
//        50.0,
//        1.25,
//        10,
//        1.0
//    )
//
//    //Add custom localizers or drivetrains here
//    fun createFollower(hardwareMap: HardwareMap?): Follower? {
//        return FollowerBuilder(followerConstants, hardwareMap)
//            .mecanumDrivetrain(driveConstants)
//            .pinpointLocalizer(localizerConstants)
//            .pathConstraints(pathConstraints)
//            .build()
//    }
//}