package org.whitneyrobotics.ftc.teamcode.drive.Omni

import com.acmerobotics.roadrunner.drive.Drive
import com.acmerobotics.roadrunner.drive.DriveSignal
import com.acmerobotics.roadrunner.drive.MecanumDrive
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.kinematics.Kinematics
import com.acmerobotics.roadrunner.localization.Localizer
import com.acmerobotics.roadrunner.util.Angle

abstract class OmniDrive @JvmOverloads constructor(
    private val kV: Double,
    private val kA: Double,
    private val kStatic: Double,
    private val trackWidth: Double,
    private val wheelBase: Double = trackWidth,
    private val lateralMultiplier: Double = 1.0
) : Drive() {

    /**
     * Default localizer for mecanum drives based on the drive encoders and (optionally) a heading sensor.
     *
     * @param drive drive
     * @param useExternalHeading use external heading provided by an external sensor (e.g., IMU, gyroscope)
     */
    class OmniLocalizer @JvmOverloads constructor(
        private val drive: OmniDrive,
        private val useExternalHeading: Boolean = true
    ) : Localizer {
        private var _poseEstimate = Pose2d()
        override var poseEstimate: Pose2d
            get() = _poseEstimate
            set(value) {
                lastWheelPositions = emptyList()
                lastExtHeading = Double.NaN
                if (useExternalHeading) drive.externalHeading = value.heading
                _poseEstimate = value
            }
        override var poseVelocity: Pose2d? = null
            private set
        private var lastWheelPositions = emptyList<Double>()
        private var lastExtHeading = Double.NaN

        override fun update() {
            val wheelPositions = drive.getWheelPositions()
            val extHeading = if (useExternalHeading) drive.externalHeading else Double.NaN
            if (lastWheelPositions.isNotEmpty()) {
                val wheelDeltas = wheelPositions
                    .zip(lastWheelPositions)
                    .map { it.first - it.second }
                val robotPoseDelta = OmniKinematics.wheelToRobotVelocities(
                    wheelDeltas,
                    drive.trackWidth,
                    drive.wheelBase,
                    drive.lateralMultiplier
                )
                val finalHeadingDelta = if (useExternalHeading) {
                    Angle.normDelta(extHeading - lastExtHeading)
                } else {
                    robotPoseDelta.heading
                }
                _poseEstimate = Kinematics.relativeOdometryUpdate(
                    _poseEstimate,
                    Pose2d(robotPoseDelta.vec(), finalHeadingDelta)
                )
            }

            val wheelVelocities = drive.getWheelVelocities()
            val extHeadingVel = drive.getExternalHeadingVelocity()
            if (wheelVelocities != null) {
                poseVelocity = OmniKinematics.wheelToRobotVelocities(
                    wheelVelocities,
                    drive.trackWidth,
                    drive.wheelBase,
                    drive.lateralMultiplier
                )
                if (useExternalHeading && extHeadingVel != null) {
                    poseVelocity = Pose2d(poseVelocity!!.vec(), extHeadingVel)
                }
            }

            lastWheelPositions = wheelPositions
            lastExtHeading = extHeading
        }
    }

    override var localizer: Localizer = OmniLocalizer(this)

    override fun setDriveSignal(driveSignal: DriveSignal) {
        val velocities = OmniKinematics.robotToWheelVelocities(
            driveSignal.vel,
            trackWidth,
            wheelBase,
            lateralMultiplier
        )
        val accelerations = OmniKinematics.robotToWheelAccelerations(
            driveSignal.accel,
            trackWidth,
            wheelBase,
            lateralMultiplier
        )
        val powers = Kinematics.calculateMotorFeedforward(velocities, accelerations, kV, kA, kStatic)
        setMotorPowers(powers[0], powers[1], powers[2], powers[3])
    }

    override fun setDrivePower(drivePower: Pose2d) {
        val powers = OmniKinematics.robotToWheelVelocities(
            drivePower,
            1.0,
            1.0,
            lateralMultiplier
        )
        setMotorPowers(powers[0], powers[1], powers[2], powers[3])
    }

    /**
     * Sets the following motor powers (normalized voltages). All arguments are on the interval `[-1.0, 1.0]`.
     */
    abstract fun setMotorPowers(frontLeft: Double, rearLeft: Double, rearRight: Double, frontRight: Double)

    /**
     * Returns the positions of the wheels in linear distance units. Positions should exactly match the ordering in
     * [setMotorPowers].
     */
    abstract fun getWheelPositions(): List<Double>

    /**
     * Returns the velocities of the wheels in linear distance units. Positions should exactly match the ordering in
     * [setMotorPowers].
     */
    open fun getWheelVelocities(): List<Double>? = null
}