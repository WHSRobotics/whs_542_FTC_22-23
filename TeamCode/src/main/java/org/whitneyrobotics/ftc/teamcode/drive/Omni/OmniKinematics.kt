package org.whitneyrobotics.ftc.teamcode.drive.Omni

import com.acmerobotics.roadrunner.geometry.Pose2d
import kotlin.math.sqrt

object OmniKinematics {
    @JvmStatic
    val RAD_2_OVER_2 = sqrt(2.0) /2

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
<<<<<<< Updated upstream
                robotVel.x + lateralMultiplier * robotVel.y + k * robotVel.heading, //fr
                robotVel.x - lateralMultiplier * robotVel.y + k * robotVel.heading, //fl
=======
                robotVel.x - lateralMultiplier * robotVel.y - k * robotVel.heading, //fr
                robotVel.x + lateralMultiplier * robotVel.y + k * robotVel.heading, //fl
>>>>>>> Stashed changes
                robotVel.x - lateralMultiplier * robotVel.y + k * robotVel.heading, //bl
                robotVel.x + lateralMultiplier * robotVel.y - k * robotVel.heading //br
        )
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
                (rearLeft + rearRight - frontLeft - frontRight) / lateralMultiplier * RAD_2_OVER_2,
                (rearRight + frontRight - frontLeft - rearLeft) / k * RAD_2_OVER_2,
                wheelVelocities.sum()/k
        ) * 0.25
    }
}