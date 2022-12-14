package com.whitneyrobotics.RoadrunnerPathing;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.ColorScheme;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedLight;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.DriveTrainType;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
import com.noahbres.meepmeep.roadrunner.trajectorysequence.TrajectorySequence;
import com.noahbres.meepmeep.roadrunner.trajectorysequence.TrajectorySequenceBuilder;
import com.noahbres.meepmeep.roadrunner.trajectorysequence.sequencesegment.TurnSegment;

public class Main {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700);

        /*RoadRunnerBotEntity botC = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setColorScheme(new ColorSchemeRedLight())
                .setDimensions(14,14)
                .setConstraints(42, 30, Math.toRadians(90), Math.toRadians(90), 5.77)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(-36, -65, Math.toRadians(90)))
                                .forward(10)
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                                .waitSeconds(1)
                                .lineToLinearHeading(new Pose2d(-36,-12, Math.toRadians(60)))
                                .splineToConstantHeading(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                                .waitSeconds(1)
                                .lineToLinearHeading(new Pose2d(-36,-12, Math.toRadians(60)))
                                .splineToConstantHeading(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                                .waitSeconds(1)
                                .lineToLinearHeading(new Pose2d(-36,-12, Math.toRadians(60)))
                                .splineToConstantHeading(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .build()
                );
*/
         RoadRunnerBotEntity botL = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setColorScheme(new ColorSchemeRedLight())
                .setDimensions(14,14)
                .setConstraints(40, 50, Math.toRadians(90), Math.toRadians(90), 5.77)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(-36, -65, Math.toRadians(90)))
                                .forward(16)
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                                .waitSeconds(1)
                                .lineToLinearHeading(new Pose2d(-35, -12, Math.toRadians(360)))
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                //.splineToLinearHeading(new Pose2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30))), Math.toRadians(60)), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                                .waitSeconds(1)
                                .lineToLinearHeading(new Pose2d(-35, -12, Math.toRadians(360)))
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                //.splineToLinearHeading(new Pose2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30))), Math.toRadians(60)), Math.toRadians(60))

                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                                .waitSeconds(1)
                                .lineToLinearHeading(new Pose2d(-35, -12, Math.toRadians(360)))
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                //.lineToLinearHeading(new Pose2d(-40, -12, Math.toRadians(60)))
                                //.splineToLinearHeading(new Pose2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30))), Math.toRadians(60)), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-60, -12, Math.toRadians(180)))
                                .build()
                );

         /*RoadRunnerBotEntity botR = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setColorScheme(new ColorSchemeRedLight())
                .setDimensions(14,14)
                .setConstraints(42, 30, Math.toRadians(90), Math.toRadians(90), 5.77)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(-36, -65, Math.toRadians(90)))
                                .forward(10)
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                                .waitSeconds(1)
                                .lineToLinearHeading(new Pose2d(-36,-12, Math.toRadians(60)))
                                .splineToConstantHeading(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                                .waitSeconds(1)
                                .lineToLinearHeading(new Pose2d(-36,-12, Math.toRadians(60)))
                                .splineToConstantHeading(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                                .waitSeconds(1)
                                .lineToLinearHeading(new Pose2d(-36,-12, Math.toRadians(60)))
                                .splineToConstantHeading(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-12, -12, Math.toRadians(90)))
                                .build()
                );*/


        meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(botL)
                //.addEntity(botC)
                //.addEntity(botR)
                .start();
    }
}