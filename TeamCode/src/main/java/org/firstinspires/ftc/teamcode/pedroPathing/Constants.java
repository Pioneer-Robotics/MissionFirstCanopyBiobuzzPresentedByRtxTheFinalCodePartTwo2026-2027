package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PredictiveBrakingCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .headingPIDFCoefficients(new PIDFCoefficients(1.5, 0.00, 0.05, 0.01))
            .forwardZeroPowerAcceleration(-32.54)
            .lateralZeroPowerAcceleration(-58.0)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.125, 0.0, 0.01, 0.0))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.015, 0.0, 0.0001, 0.0, 0.0))
            .predictiveBrakingCoefficients(new PredictiveBrakingCoefficients(0.1,0.065619,0.0023489))
            .centripetalScaling(0.0)
            //.centripetalScaling(0.00015)
            .mass(13.61);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName(org.firstinspires.ftc.teamcode.Constants.HardwareNames.DRIVE_RIGHT_FRONT)
            .rightRearMotorName(org.firstinspires.ftc.teamcode.Constants.HardwareNames.DRIVE_RIGHT_BACK)
            .leftRearMotorName(org.firstinspires.ftc.teamcode.Constants.HardwareNames.DRIVE_LEFT_BACK)
            .leftFrontMotorName(org.firstinspires.ftc.teamcode.Constants.HardwareNames.DRIVE_LEFT_FRONT)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(68.0)
            .yVelocity(57.0);

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(76.0)
            .strafePodX(-167.0)
            .distanceUnit(DistanceUnit.MM)
            .hardwareMapName("pinpoint")
            .encoderResolution(org.firstinspires.ftc.teamcode.Constants.Pinpoint.ENCODER_RESOLUTION)
            .forwardEncoderDirection(org.firstinspires.ftc.teamcode.Constants.Pinpoint.X_ENCODER_DIRECTION)
            .strafeEncoderDirection(org.firstinspires.ftc.teamcode.Constants.Pinpoint.Y_ENCODER_DIRECTION);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pinpointLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}