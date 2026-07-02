package org.firstinspires.ftc.teamcode.hardware

interface HardwareComponent {
    val name: String
        get() = this::class.simpleName!!

    fun init()

    fun update() {}
}
