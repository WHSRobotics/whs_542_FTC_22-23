package org.whitneyrobotics.ftc.teamcode.autoop;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.Queue.QueueItem;
import org.whitneyrobotics.ftc.teamcode.lib.util.Queue.QueueManager;
import org.whitneyrobotics.ftc.teamcode.subsys.FeedForwardCarousel;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;

@Autonomous(name="QueueTest",group="Tests")
@RequiresApi(api = Build.VERSION_CODES.N)
public class QueueManagerTest extends DashboardOpMode {
    WHSRobotImpl robot;
    private int autoState = 0;
    Position carouselPos = new Position(-1000,1000);
    QueueItem driveToCarousel;
    FeedForwardCarousel carousel;

    @Override
    public void init() {
        initializeDashboardTelemetry(25);

        robot = new WHSRobotImpl(hardwareMap);
        carousel = new FeedForwardCarousel(hardwareMap);
        robot.setInitialCoordinate(new Coordinate(-500,500,0));
        //Position carousel = new Position(-1000,1000);
        //QueueItem driveToCarousel = new QueueItem(() -> robot.driveToTarget(carousel,false),() -> robot.driveToTargetInProgress(),false);

    }

    @Override
    public void loop() {
        QueueManager.processQueue();
        switch(autoState){
            case 0:
                driveToCarousel = new QueueItem(() -> robot.driveToTarget(carouselPos,false),() -> robot.driveToTargetInProgress(),true);
                driveToCarousel.setMode(QueueItem.ProcessMode.LINEAR);
                QueueManager.add(driveToCarousel);
                //driveToCarousel.action.invoke();
                QueueManager.add(new QueueItem(() -> carousel.operate(true,true),() -> carousel.carouselInProgress(),false));
                packet.addLine(String.valueOf(driveToCarousel.exitCondition.get()));
                packet.addLine(String.valueOf(driveToCarousel.isAlive()));
                autoState++;
                break;
            case 1:
                if(QueueManager.queueEmpty()){
                    autoState++;
                }
                break;
            case 2:
                telemetry.addLine("Done!");
                packet.addLine("Done");
                break;
        }
        packet.put("Queue Size", QueueManager.queueSize());
        dashboard.sendTelemetryPacket(packet);
    }
}
