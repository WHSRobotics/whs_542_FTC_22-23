package org.whitneyrobotics.ftc.teamcode.subsys.AutoScoring;

import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;

public class PositionBasedAutoScoring {
    WHSRobotData whsRobotData;

    public powerPlayScoreTarget[] targets;

    public PositionBasedAutoScoring (){
        whsRobotData = new WHSRobotData();

        targets[0] = new powerPlayScoreTarget(600, 600, 2);
        targets[1] = new powerPlayScoreTarget(1800, 600, 2);
        targets[2] = new powerPlayScoreTarget(3000, 600, 2);
        targets[3] = new powerPlayScoreTarget(600, 1800, 2);
        targets[4] = new powerPlayScoreTarget(1800, 1800, 2);
        targets[5] = new powerPlayScoreTarget(3000, 1800, 2);
        targets[6] = new powerPlayScoreTarget(600, 3000, 2);
        targets[7] = new powerPlayScoreTarget(1800, 3000, 2);
        targets[8] = new powerPlayScoreTarget(3000, 3000, 2);

    }

    public class powerPlayScoreTarget {
        public double POSITION_X = 0;
        public double POSITION_Y = 0;

        public double SCORE = 0;

        public powerPlayScoreTarget (double positionX, double positionY, double score){
            POSITION_X = positionX;
            POSITION_Y = positionY;

            SCORE = score;
        }
    }

}
