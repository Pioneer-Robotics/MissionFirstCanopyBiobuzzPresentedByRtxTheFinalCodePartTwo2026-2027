package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.algorithm.Foresight
import com.pedropathing.algorithm.ForesightConfig
import com.pedropathing.config.Configuration
import com.pedropathing.controllers.Controller
import com.pedropathing.follower.Follower
import com.pedropathing.math.Matrix
import com.pedropathing.math.Vector2D
import com.pedropathing.revhub.drivetrains.Mecanum
import com.pedropathing.revhub.drivetrains.MecanumConfig
import com.pedropathing.revhub.localizers.PinpointConfig
import com.pedropathing.revhub.localizers.PinpointLocalizer
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.Constants.HardwareNames as H

object Constants {
    fun create(h: HardwareMap): Follower {
        return Follower(
            PinpointLocalizer(h, localizerConfig),
            Mecanum(h, drivetrainConfig),
            Foresight(foresightConfig)
        )
    }

    var drivetrainConfig: MecanumConfig = MecanumConfig(Configuration { c: MecanumConfig? ->
        c!!.frontLeftName.set(H.DRIVE_LEFT_FRONT)
        c.frontRightName.set(H.DRIVE_RIGHT_FRONT)
        c.backLeftName.set(H.DRIVE_LEFT_BACK)
        c.backRightName.set(H.DRIVE_RIGHT_BACK)
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE)
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD)
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE)
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD)
    })

    var localizerConfig: PinpointConfig = PinpointConfig(Configuration { c: PinpointConfig? ->
        c!!.name.set(H.PINPOINT)
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD)
        c.offsetUnits.set(DistanceUnit.INCH)
        c.xPodOffset.set(2.6607582512802965)
        c.yPodOffset.set(-6.694781686377338)
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD)
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED)
        c.globalDistanceUnit.set(DistanceUnit.INCH)
    })

    var foresightConfig: ForesightConfig = ForesightConfig(
        Configuration { c: ForesightConfig? ->
            val primaryTranslationalForward = Controller.proportional(0.3522695617567914)
            val secondaryTranslationalForward = Controller.proportional(0.13015419025308597)
            val primaryTranslationalLateral = Controller.proportional(0.6699756279452626)
            val secondaryTranslationalLateral = Controller.proportional(0.2475380924472885)

            c!!.forwardTranslational.set(
                Controller.piecewise(secondaryTranslationalForward)
                    .put(2.5, primaryTranslationalForward)
            )
            c.strafeTranslational.set(
                Controller.piecewise(secondaryTranslationalLateral)
                    .put(2.5, primaryTranslationalLateral)
            )

            c.coast.set(Controller.proportionalFeedforward(0.01231297493679838))
            c.brake.set(Controller.proportionalFeedforward(0.010466028696278622))

            c.headingFeedback.set(Controller.proportional(5.537227932061881))
            c.headingBrakeCoefficients.set(
                Vector2D.cartesian(
                    0.045930843361327904,
                    0.010327262684407952
                )
            )

            c.linearBrakeCoefficients.set(Matrix.diag(0.13046804469620646, 0.03997235978107463))
            c.quadraticBrakeCoefficients.set(
                Matrix.diag(
                    9.641547394723007E-4,
                    0.0020489419026312717
                )
            )

            c.maxAchievableForwardVelocity.set(79.34207847251457)
            c.maxAchievableStrafeVelocity.set(59.779192181031554)
            c.naturalForwardDeceleration.set(28.024111218176945)
            c.naturalStrafeDeceleration.set(85.95532065334345)
        }
    )
}