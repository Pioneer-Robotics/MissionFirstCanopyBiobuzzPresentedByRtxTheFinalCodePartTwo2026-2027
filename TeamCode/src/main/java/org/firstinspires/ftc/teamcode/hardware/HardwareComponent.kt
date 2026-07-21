package org.firstinspires.ftc.teamcode.hardware

/**
 * Interface for robot hardware modules used by an OpMode.
 *
 * Implement [init] to configure hardware before runtime, and optionally override [update]
 * for per-loop behavior. [name] defaults to the implementing class name.
 */
interface HardwareComponent {
    val name: String
        get() = this::class.simpleName!!

    fun init()

    fun update() {}
}
