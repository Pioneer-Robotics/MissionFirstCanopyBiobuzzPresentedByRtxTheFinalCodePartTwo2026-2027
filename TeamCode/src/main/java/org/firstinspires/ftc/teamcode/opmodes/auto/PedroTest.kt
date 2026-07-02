//package org.firstinspires.ftc.teamcode.opmodes.auto
//
//import com.pedropathing.ivy.Scheduler.*
//import com.pedropathing.ivy.pedro.PedroCommands.*
//import com.pedropathing.ivy.groups.Groups.*
//
//import com.pedropathing.follower.Follower
//import com.pedropathing.geometry.BezierLine
//import com.pedropathing.geometry.Pose
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous
//import org.firstinspires.ftc.teamcode.Bot
//import org.firstinspires.ftc.teamcode.BotType
//import org.firstinspires.ftc.teamcode.PedroConstants
//import org.firstinspires.ftc.teamcode.opmodes.BaseOpMode
//
//@Autonomous(name = "Pedro Test")
//class PedroTest : BaseOpMode() {
//    lateinit var pFollower: Follower
//
//    private val pose1 = Pose(0.0, 0.0, Math.toRadians(0.0))
//    private val pose2 = Pose(20.0, 0.0, Math.toRadians(0.0))
//    private val pose3 = Pose(20.0, 20.0, Math.toRadians(0.0))
//    private val pose4 = Pose(0.0, 0.0, Math.toRadians(180.0))
//
//    private val path1 = pFollower.pathBuilder()
//        .addPath(BezierLine(pose1, pose2))
//        .setLinearHeadingInterpolation(pose1.heading, pose2.heading)
//        .build()
//    private val path2 = pFollower.pathBuilder()
//        .addPath(BezierLine(pose2, pose3))
//        .setLinearHeadingInterpolation(pose2.heading, pose3.heading)
//        .build()
//    private val path3 = pFollower.pathBuilder()
//        .addPath(BezierLine(pose3, pose4))
//        .setLinearHeadingInterpolation(pose3.heading, pose4.heading)
//        .build()
//
//    override fun onInit() {
//        bot = Bot.fromType(BotType.MECANUM_BOT, hardwareMap)
//        reset()
//        pFollower = PedroConstants.createFollower(hardwareMap)!!;
//        pFollower.setStartingPose(pose1);
//    }
//
//    override fun init_loop() {
//        schedule(sequential(
//            follow(pFollower, path1),
//            follow(pFollower, path2, true),
//            follow(pFollower, path3, true),
//        ));
//    }
//
//    override fun onLoop() {
//        pFollower.update();
//        execute();
//
//        telemetry.addData("x", pFollower.pose.x);
//        telemetry.addData("y", pFollower.pose.y);
//        telemetry.addData("heading", pFollower.pose.heading);
//        telemetry.update();
//    }
//}