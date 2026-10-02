package org.firstinspires.ftc.teamcode.helpers

import com.pedropathing.algorithm.ForesightConfig
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.commands.Commands.instant
import com.pedropathing.ivy.groups.Groups.sequential
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.enums.enumEntries

/** Returns the next enum entry in a cyclic manner. */
inline fun <reified T : Enum<T>> T.next(step: Int = 1): T {
    val entries = enumEntries<T>()
    val nextOrdinal = (this.ordinal + step) % entries.size
    return entries[nextOrdinal]
}

/** Sets the maximum velocity for the command. */
fun Command.withMaxVelocity(velocity: Double): Command =
    sequential(
        instant {
            Constants.foresightConfig.maxVelocityConstraint.set(velocity)
        },
        this,
        instant {
            Constants.foresightConfig.maxVelocityConstraint.set(
                ForesightConfig.Constraint.NONE
            )
        }
    )
