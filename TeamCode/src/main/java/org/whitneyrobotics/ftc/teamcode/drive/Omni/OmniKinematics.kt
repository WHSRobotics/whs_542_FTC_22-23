package org.whitneyrobotics.ftc.teamcode.drive.Omni

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.kinematics.Kinematics
import com.acmerobotics.roadrunner.kinematics.MecanumKinematics

object OmniKinematics {
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
                robotVel.x - lateralMultiplier * robotVel.y - k * robotVel.heading, //fr
                robotVel.x + lateralMultiplier * robotVel.y - k * robotVel.heading, //fl
                robotVel.x - lateralMultiplier * robotVel.y + k * robotVel.heading, //bl
                robotVel.x + lateralMultiplier * robotVel.y + k * robotVel.heading //br
        )
    }

    @JvmStatic
    @JvmOverloads
    fun robotToWheelAccelerations(
            robotAccel: Pose2d,
            trackWidth: Double,
            wheelBase: Double = trackWidth,
            lateralMultiplier: Double = 1
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
                (rearLeft + frontRight - frontLeft - rearRight) / lateralMultiplier,
                (rearRight + frontRight - frontLeft - rearLeft) / k,
                wheelVelocities.sum()/k
        ) * 0.25
    }
}