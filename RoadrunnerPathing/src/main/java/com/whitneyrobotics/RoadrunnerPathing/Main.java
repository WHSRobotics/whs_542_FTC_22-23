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
        Vector2d westHigh = new Vector2d(-24 - (8 * Math.cos(Math.toRadians(60))), 0 - (8 * Math.sin(Math.toRadians(60))));
        Pose2d approachWestHigh = new Pose2d(-24 - (11 * Math.cos(Math.toRadians(60))),0 - (11 * Math.sin(Math.toRadians(60))), Math.toRadians(60));
        RoadRunnerBotEntity botC = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setColorScheme(new ColorSchemeRedLight())
                .setDimensions(14,14)
                .setConstraints(40, 30, Math.toRadians(120), Math.toRadians(150), 5.77)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(-36, -65, Math.toRadians(90)))
                                .forward(72)
                                .back(26)
                                .setReversed(false)
                                //.addTemporalMarker(3.5,()-> robot.linearSlides.setTarget(LinearSlidesMeet3.Target.HIGH.getPosition()+1.5))
                                .splineTo(westHigh, Math.toRadians(60))
                                .waitSeconds(0.25)
                                //.addDisplacementMarker(()->grabberMode = GrabberMode.RELEASE)

                                .back(3)
                                .lineToLinearHeading(new Pose2d(-36,-16,Math.toRadians(160)))
                                /*.addDisplacementMarker(()-> {
                                    grabberMode = GrabberMode.GRAB_ON_DETECT;
                                    robot.linearSlides.setTarget(currentConePrediction+2);
                                })*/
                                .splineTo(new Vector2d(-61,-12.25),Math.toRadians(180))
                                .forward(4)
                                //.addDisplacementMarker(()->robot.linearSlides.setTarget(currentConePrediction))
                                .waitSeconds(0.5)
                                //.addDisplacementMarker(()->robot.linearSlides.setTarget(currentConePrediction+5))
                                .waitSeconds(0.5)
                                .setReversed(true)
                                .back(4)
                                //addDisplacementMarker(() -> robot.linearSlides.setTarget(LinearSlidesMeet3.Target.HIGH))
                                .splineTo(new Vector2d(-36,-16),Math.toRadians(-15))
                                .setReversed(false)
                                .lineToLinearHeading(approachWestHigh)
                                .splineTo(westHigh, Math.toRadians(60))
                                //.addDisplacementMarker(()->grabberMode = GrabberMode.RELEASE)
                                .waitSeconds(0.5)
                                .build()
                );

         RoadRunnerBotEntity botL = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setColorScheme(new ColorSchemeRedLight())
                .setDimensions(16,16)
                .setConstraints(40, 50, Math.toRadians(90), Math.toRadians(90), 5.77)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(-36, -65, Math.toRadians(90)))
                                .forward(16)
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(0.5)

                                .back(3)
                                .lineToLinearHeading(new Pose2d(-36,-16,Math.toRadians(160)))
                                .splineTo(new Vector2d(-60,-12),Math.toRadians(180))
                                .forward(1)
                                .waitSeconds(1)
                                .setReversed(true)
                                .back(1)
                                .splineTo(new Vector2d(-36,-16),Math.toRadians(-15))
                                .setReversed(false)
                                .lineToLinearHeading(new Pose2d(-29,-5*Math.sqrt(3), Math.toRadians(60)))
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(0.5)

                                .back(3)
                                .lineToLinearHeading(new Pose2d(-36,-16,Math.toRadians(160)))
                                .splineTo(new Vector2d(-60,-12),Math.toRadians(180))
                                .forward(1)
                                .waitSeconds(1)
                                .setReversed(true)
                                .back(1)
                                .splineTo(new Vector2d(-36,-16),Math.toRadians(-15))
                                .setReversed(false)
                                .lineToLinearHeading(new Pose2d(-29,-5*Math.sqrt(3), Math.toRadians(60)))
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(0.5)

                                .back(3)
                                .lineToLinearHeading(new Pose2d(-36,-16,Math.toRadians(160)))
                                .splineTo(new Vector2d(-60,-12),Math.toRadians(180))
                                .forward(1)
                                .waitSeconds(1)
                                .setReversed(true)
                                .back(1)
                                .splineTo(new Vector2d(-36,-16),Math.toRadians(-15))
                                .setReversed(false)
                                .lineToLinearHeading(new Pose2d(-29,-5*Math.sqrt(3), Math.toRadians(60)))
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                .waitSeconds(0.5)
                                .back(1)
                                .splineToConstantHeading(new Vector2d(-36,-13),Math.toRadians(-90))

                                .build()
                );

         RoadRunnerBotEntity botR = new DefaultBotBuilder(meepMeep)
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
                                .lineToSplineHeading(new Pose2d(-40, -12, Math.toRadians(360)))
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                //.splineToLinearHeading(new Pose2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30))), Math.toRadians(60)), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                                .waitSeconds(1)
                                .lineToSplineHeading(new Pose2d(-40, -12, Math.toRadians(360)))
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                //.splineToLinearHeading(new Pose2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30))), Math.toRadians(60)), Math.toRadians(60))

                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                                .waitSeconds(1)
                                .lineToSplineHeading(new Pose2d(-40, -12, Math.toRadians(360)))
                                .splineTo(new Vector2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30)))), Math.toRadians(60))
                                //.lineToLinearHeading(new Pose2d(-40, -12, Math.toRadians(60)))
                                //.splineToLinearHeading(new Pose2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30))), Math.toRadians(60)), Math.toRadians(60))
                                .waitSeconds(1)

                                .back(2)
                                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                                .lineToLinearHeading(new Pose2d(-12, -12, Math.toRadians(270)))
                                .build()
                );


        meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(botC)
                //.addEntity(botC)
                //.addEntity(botR)
                .start();
    }
}