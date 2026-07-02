package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.general.AllianceColor
import org.firstinspires.ftc.teamcode.hardware.BatteryMonitor
import org.firstinspires.ftc.teamcode.hardware.Camera
import org.firstinspires.ftc.teamcode.hardware.HardwareComponent
import org.firstinspires.ftc.teamcode.hardware.LED
import org.firstinspires.ftc.teamcode.hardware.MecanumBase
import org.firstinspires.ftc.teamcode.localization.localizers.Pinpoint
import org.firstinspires.ftc.teamcode.pathing.follower.Follower
import org.firstinspires.ftc.teamcode.vision.AprilTag

enum class BotType {
    MECANUM_BOT,
    COMP_BOT,
    CUSTOM,
}

class Bot private constructor(
    val type: BotType,
    private val hardwareComponents: Map<Class<out HardwareComponent>, HardwareComponent>,
) {
    // Type-safe access
    internal inline fun <reified T : HardwareComponent> get(): T? = hardwareComponents[T::class.java] as? T

    // Check if a component is present
    internal inline fun <reified T : HardwareComponent> has(): Boolean = hardwareComponents.containsKey(T::class.java)

    fun initAll() {
        hardwareComponents.values.forEach { it.init() }
    }

    var allianceColor = AllianceColor.RED

    // Property-style access for known components
    val mecanumBase get() = get<MecanumBase>()
    val pinpoint get() = get<Pinpoint>()
    val camera get() = get<Camera>()
    val batteryMonitor get() = get<BatteryMonitor>()
    val led get() = get<LED>()

    // Follower is lazily initialized (only if accessed)
    // and will error if localizer or mecanumBase is missing
    val follower: Follower by lazy {
        Follower(
            localizer = pinpoint!!,
            drive = mecanumBase!!,
        )
    }

    fun updateAll() {
        hardwareComponents.values.forEach { it.update() }
    }

    // Companion for builder and fromType
    companion object {
        fun builder() = Builder()

        fun fromType(
            type: BotType,
            hardwareMap: HardwareMap,
        ): Bot =
            when (type) {
                BotType.MECANUM_BOT ->
                    builder()
                        .add(MecanumBase(hardwareMap))
                        .add(Pinpoint(hardwareMap))
                        .add(BatteryMonitor(hardwareMap))
                        .build()
                BotType.COMP_BOT ->
                    builder()
                        .add(MecanumBase(hardwareMap))
                        .add(Pinpoint(hardwareMap))
                        .add(LED(hardwareMap))
                        .add(Camera(hardwareMap, processors = arrayOf(AprilTag().processor)))
                        .add(BatteryMonitor(hardwareMap))
                        .build()
                BotType.CUSTOM -> throw IllegalArgumentException("Use Bot.builder() to create a custom bot")
            }
    }

    class Builder {
        private val components = mutableMapOf<Class<out HardwareComponent>, HardwareComponent>()

        fun <T : HardwareComponent> add(component: T): Builder {
            // TODO: Possibly allow interfaces such as Localizer vs Pinpoint
            components[component::class.java] = component
            return this
        }

        fun build(): Bot = Bot(BotType.CUSTOM, components.toMap())
    }
}
