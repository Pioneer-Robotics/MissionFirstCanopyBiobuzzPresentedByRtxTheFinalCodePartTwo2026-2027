package org.firstinspires.ftc.teamcode.helpers.graph

import com.pedropathing.math.Pose
import kotlin.math.sqrt

// Kotlin doesn't seem to have a good class for vector operations, so anything I need I'll just update here

// Double

@JvmName("plusDoublePair")
operator fun Pair<Double, Double>.plus(other: Pair<Double, Double>): Pair<Double, Double> {
    return Pair(this.first + other.first, this.second + other.second)
}
@JvmName("minusDoublePair")
operator fun Pair<Double, Double>.minus(other: Pair<Double, Double>): Pair<Double, Double> {
    return Pair(this.first - other.first, this.second - other.second)
}
operator fun Double.times(other: Pair<Double, Double>): Pair<Double, Double> {
    return Pair(this*other.first, this*other.second)
}
fun Pair<Double, Double>.dot(other: Pair<Double, Double>): Double {
    return this.first*other.first + this.second*other.second
}
fun Pair<Double, Double>.abs(): Double {
    return sqrt(this.first*this.first + this.second*this.second)
}
fun Pair<Double, Double>.toInt(): Pair<Int, Int> {
    return Pair(this.first.toInt(), this.second.toInt())
}
fun Pair<Double, Double>.toPose(): Pose {
    return Pose(this.first, this.second)
}

// Int
@JvmName("plusIntPair")
operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first + other.first, this.second + other.second)
}
@JvmName("minusIntPair")
operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first - other.first, this.second - other.second)
}
fun Pair<Int, Int>.toDouble(): Pair<Double, Double> {
    return Pair(this.first.toDouble(), this.second.toDouble())
}