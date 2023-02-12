package org.whitneyrobotics.ftc.teamcode.drive.Omni

import com.acmerobotics.roadrunner.geometry.Pose2d
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object OmniKinematics {
    @JvmStatic
    val RAD_2 = sqrt(2.0)


    @JvmStatic
    @JvmOverloads
    fun robotToWheelVelocities(
            robotVel: Pose2d,
            trackWidth: Double,
            wheelBase: Double = trackWidth,
            lateralMultiplier: Double = 1.0
    ): List<Double> {
        val k = (trackWidth + wheelBase)/2
        return listOf(
                RAD_2/2 * robotVel.x - RAD_2/2 * lateralMultiplier * robotVel.y - k * RAD_2 * robotVel.heading, //fl
                RAD_2/2 * robotVel.x + RAD_2/2 * lateralMultiplier * robotVel.y - k * RAD_2 * robotVel.heading, //fr
                RAD_2/2 * robotVel.x - RAD_2/2 * lateralMultiplier * robotVel.y + k * RAD_2 * robotVel.heading, //br
                RAD_2/2 * robotVel.x + RAD_2/2 * lateralMultiplier * robotVel.y + k * RAD_2 * robotVel.heading, //bl
        )
        /*
        return listOf(
            RAD_2/2 * robotVel.x + RAD_2/2 * lateralMultiplier * robotVel.y - , //fr
            RAD_2/2 * robotVel.x - RAD_2/2 * lateralMultiplier * robotVel.y + k * RAD_2 * robotVel.heading, //fl
            RAD_2/2 * robotVel.x - RAD_2/2 * lateralMultiplier * robotVel.y + k * RAD_2 * robotVel.heading, //bl
            RAD_2/2 * robotVel.x + RAD_2/2 * lateralMultiplier * robotVel.y - k * RAD_2 * robotVel.heading //br
        )
         */
    }

    @JvmStatic
    @JvmOverloads
    fun robotToWheelAccelerations(
            robotAccel: Pose2d,
            trackWidth: Double,
            wheelBase: Double = trackWidth,
            lateralMultiplier: Double = 1.0
    ) = robotToWheelVelocities(
            robotAccel,
            trackWidth,
            wheelBase, lateralMultiplier
    )

    @JvmStatic
    @JvmOverloads
    fun wheelToRobotVelocities(
            wheelVelocities: List<Double>,
            trackWidth: Double,
            wheelBase: Double = trackWidth,
            lateralMultiplier: Double = 1.0
    ): Pose2d {
        val k = (trackWidth + wheelBase) / 2.0
        val (frontLeft, rearLeft, rearRight, frontRight) = wheelVelocities
        return Pose2d(
                (frontLeft + frontRight - rearLeft - rearRight) / lateralMultiplier * RAD_2,
                (frontLeft + rearLeft - frontRight - rearRight) / k * RAD_2,
                wheelVelocities.sum()/k
        ) * 0.25
    }

    @JvmStatic
    fun normalize(velocities: List<Double>): List<Double> {
        var divisor = 1.0
        divisor = velocities.maxOrNull()?.let { min(it,1.0) }!!
        return velocities.map { it/divisor }
    }
}