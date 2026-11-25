package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.biobuzz.Points
import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.helpers.Pose
import org.firstinspires.ftc.teamcode.opmodes.BaseOpMode.Verbose
import kotlin.math.PI

object Constants {
    object HardwareNames {
        // Drive motors
        const val DRIVE_LEFT_FRONT = "driveLF"
        const val DRIVE_LEFT_BACK = "driveLB"
        const val DRIVE_RIGHT_FRONT = "driveRF"
        const val DRIVE_RIGHT_BACK = "driveRB"

        // Other motors
        const val FLYWHEEL1 = "flywheelMotor1"
        const val FLYWHEEL2 = "flywheelMotor2"
        const val INTAKE = "intake"
        const val TRANSFER = "transfer"

        const val TURRET_ENCODER = "turretEncoder"

        // Odometry
        const val ODO_LEFT = "odoLeft"
        const val ODO_RIGHT = "odoRight"
        const val ODO_CENTER = "odoCenter"

        // Pinpoint
        const val PINPOINT = "pinpoint"

        // Servos
        const val RELEASE_SERVO = "releaseServo"
        const val TURRET_SERVO_1 = "turretServo1"
        const val TURRET_SERVO_2 = "turretServo2"
        const val FLOWER_SERVO_1 = "flowerServo1"
        const val FLOWER_SERVO_2 = "flowerServo2"

        // Other
        const val WEBCAM = "Webcam 1"
        const val LED_DRIVER = "prism"
        const val BREAK_BEAM_1 = "breakBeam1"
        const val BREAK_BEAM_2 = "breakBeam2"
    }

    // -------- Odometry (3-wheel) --------
    object Odometry {
        // geometry (cm)
        const val TRACK_WIDTH_CM = 26.5
        const val FORWARD_OFFSET_CM = 15.1
        const val WHEEL_DIAMETER_CM = 4.8

        // encoder
        const val TICKS_PER_REV = 2000.0
        const val TICKS_TO_CM = (WHEEL_DIAMETER_CM * PI) / TICKS_PER_REV
    }

    // -------- Drivebase (mecanum) --------
    object Drive {
        // geometry (cm)
        const val TRACK_WIDTH_CM = 0.0
        const val WHEEL_BASE_CM = 0.0

        // limits
        const val MAX_MOTOR_VELOCITY_TPS = 2500.0
        const val DEFAULT_POWER = 1.0 // Updated from 0.7

        // Feedforward gains using Pose(x,y,theta)
//        @JvmField var kVX = 0.0
//        @JvmField var kVY = 0.0
//        @JvmField var kVT = 0.0
//        @JvmField var kAX = 0.0
//        @JvmField var kAY = 0.0
//        @JvmField var kAT = 0.0

        val kV = Pose(x = 0.0063, y = 0.0055, theta = -0.147)
        val kA = Pose(x = 0.00125, y = 0.001, theta = 0.0)
        val kS = Pose(x = 0.11, y = 0.06, theta = 0.0)

        val MOTOR_CONFIG =
            mapOf(
                HardwareNames.DRIVE_LEFT_FRONT to DcMotorSimple.Direction.REVERSE,
                HardwareNames.DRIVE_LEFT_BACK to DcMotorSimple.Direction.REVERSE,
                HardwareNames.DRIVE_RIGHT_FRONT to DcMotorSimple.Direction.FORWARD,
                HardwareNames.DRIVE_RIGHT_BACK to DcMotorSimple.Direction.FORWARD,
            )
    }

    // -------- Pinpoint (odometry pods) --------
    object Pinpoint {
        // offsets from tracking point (mm): +forward, +left
        const val Y_POD_OFFSET_MM = -156.7
        const val X_POD_OFFSET_MM = 67.0

        // encoder configuration. Y should increase left, X should increase forward
        @JvmField
        val Y_ENCODER_DIRECTION = GoBildaPinpointDriver.EncoderDirection.REVERSED
        @JvmField
        val X_ENCODER_DIRECTION = GoBildaPinpointDriver.EncoderDirection.FORWARD

        @JvmField
        val ENCODER_RESOLUTION = GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD
    }

    // -------- Follower (path following) --------
    @Config
    object Follower {
        /** The threshold in cm to consider the target reached. */
        const val POSITION_THRESHOLD = 4.0

        /** The threshold in radians to consider the target heading reached. */
        const val ROTATION_THRESHOLD = 0.1

        /** The maximum drive velocity in cm per second. */
        const val MAX_DRIVE_VELOCITY = 150.0

        /** The maximum drive acceleration in cm per second squared. */
        const val MAX_DRIVE_ACCELERATION = 120.0

        /** The maximum centripetal acceleration that the robot can handle in cm/s^2. */
        const val MAX_CENTRIPETAL_ACCELERATION = (70.0 * 70.0) / 25.0

        /** The maximum angular velocity in rad per second. */
        const val MAX_ANGULAR_VELOCITY = 1.0

        /** The maximum angular acceleration in rad per second squared. */
        const val MAX_ANGULAR_ACCELERATION = 10.0

        // --- Standard Follower PID Tuned to Stay on the Path ---
        // X-axis PID coefficients for the trajectory follower
        @JvmField var X_KP = 2.67 // was 7.0
        @JvmField var X_KI = 0.0
        @JvmField var X_KD = 0.1

        // Y-axis PID coefficients for the trajectory follower
        @JvmField var Y_KP = 2.67 // was 7.0
        @JvmField var Y_KI = 0.0
        @JvmField var Y_KD = 0.1

        // Theta PID coefficients for heading interpolation
        @JvmField var THETA_KP = 3.067 // was 5.0
        @JvmField var THETA_KI = 0.0
        @JvmField var THETA_KD = 0.0

        // --- Position PID coefficients tuned for final pose correction ---
        @JvmField var POS_X_KP = 0.0
        @JvmField var POS_X_KI = 0.0
        @JvmField var POS_X_KD = 0.0

        @JvmField var POS_Y_KP = 0.0
        @JvmField var POS_Y_KI = 0.0
        @JvmField var POS_Y_KD = 0.0

        @JvmField var POS_THETA_KP = 0.0
        @JvmField var POS_THETA_KI = 0.0
        @JvmField var POS_THETA_KD = 0.0

    }

    object Camera {
        // Camera position constants (cm)
        val XYZ_UNITS = DistanceUnit.CM
        val XYZ_OFFSET: List<Double> = listOf(17.5, 24.0, 30.5)

        // Camera orientation constants (degrees)
        val RPY_UNITS = AngleUnit.DEGREES
        val RPY_OFFSET: List<Double> = listOf(0.0, -90.0, 0.0) // Pitch=-90 to face forward

         //Lens Intrinsics
         const val FX = 915.97454765
         const val FY = 915.69375972
         const val CX = 634.56292469
         const val CY = 370.77742622

        // val distortionCoefficients = floatArrayOf(0.0573F, 2.0205F, -0.0331F, 0.0021F, -14.6155F, 0F, 0F, 0F)
    }


    @Config
    object Turret {
        // Turret PID
        const val kP = 0.0001
        const val kI = 0.0
        const val kD = 0.0

        const val MIN_ANGLE_DEG = 0.0
        const val MAX_ANGLE_DEG = 360.0

        const val RELEASE_TIME_TORQUE = 350 // Time for release servo to open if it is a torque servo
        const val RELEASE_TIME_SPEED = 150 // If it is a speed servo
    }

    object ServoPositions {
        const val RELEASE_OPEN = 1.0
        const val RELEASE_CLOSE = 0.0

        const val FLOWER_DOWN = 0.0
        const val FLOWER_UP = 1.0
    }

    object TransferData {
        fun reset() {
            allianceColor = AllianceColor.NEUTRAL
            pose = Pose()
            turretMotorTicks = 0
            spindexerMotorTicks = 0
        }

        var allianceColor = AllianceColor.NEUTRAL
        var pose = Pose()
        var turretMotorTicks = 0
        var spindexerMotorTicks = 0
    }

    @Config
    object Flywheel {

    }

    object Transfer {
        const val LOAD_POWER = 0.6
        const val SHOOT_POWER = 1.0

        const val BREAK_BEAM_TIME_MS = 250.0
    }

    object Misc {
        val VERBOSE_LEVEL = Verbose.DEBUG
        val RESET_POSE = Points.BOTTOM_LEFT_REACHABLE
    }
}
