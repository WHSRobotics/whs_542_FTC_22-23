package org.whitneyrobotics.ftc.teamcode.autoop;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.FollowerConstants;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.swervetotarget.SwervePath;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.swervetotarget.SwervePathGenerationConstants;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;

import java.util.ArrayList;
import java.util.logging.Logger;

import static org.whitneyrobotics.ftc.teamcode.lib.purepursuit.PathGenerator.generateSwervePath;

import com.acmerobotics.dashboard.config.Config;

@Config
public class AutoSwervePositions {

 //carousel path for red top AND bottom
 public final static Position redCarousel1 = new Position(-1219.2,-609.6);
 public final static Position redCarousel2 = new Position(-1219.2,774.7);
 public final static Position redCarousel3 = new Position(-1400.2,1329.2);

 /*
 cursed
 public final static Position blueCarousel1 = new Position(-1219.2,609.6);
 public final static Position blueCarousel2 = new Position(-1219.2,-774.7);
 public final static Position blueCarousel3 = new Position(-1400.2,-1329.2);
 */

 public final static Position redGapApproachToShippingHub1 = new Position(-1703.7,-300);
 public final static Position redGapApproachToShippingHub2 = new Position(-1625.7,0);
 public final static Position redGapApproachToShippingHub3 = new Position(-1219.2,304.8);
 public final static Position redGapApproachToShippingHub4 = new Position(-1079.6,304.8);

 public final static Position redWarehouseApproachToShippingHub1 = new Position(-1663.7,-1300);
 public final static Position redWarehouseApproachToShippingHub2 = new Position(-1683.7,-900);
 public final static Position redWarehouseApproachToShippingHub3 = new Position(-1703.7,-300);
 public final static Position redWarehouseApproachToShippingHub4 = new Position(-1625.7,0);
 public final static Position redWarehouseApproachToShippingHub5 = new Position(-1219.2,304.8);
 public final static Position redWarehouseApproachToShippingHub6 = new Position(-1079.6,304.8);

 public final static Position blueGapApproachToShippingHub1 = new Position(-1703.7,300);
 public final static Position blueGapApproachToShippingHub2 = new Position(-1625.7,0);
 public final static Position blueGapApproachToShippingHub3 = new Position(-1219.2,-304.8);
 public final static Position blueGapApproachToShippingHub4 = new Position(-1079.6,-304.8);

 public final static Position blueWarehouseApproachToShippingHub1 = new Position(-1663.7,1300);
 public final static Position blueWarehouseApproachToShippingHub2 = new Position(-1683.7,900);
 public final static Position blueWarehouseApproachToShippingHub3 = new Position(-1703.7,300);
 public final static Position blueWarehouseApproachToShippingHub4 = new Position(-1625.7,0);
 public final static Position blueWarehouseApproachToShippingHub5 = new Position(-1219.2,-304.8);
 public final static Position blueWarehouseApproachToShippingHub6 = new Position(-1079.6,-304.8);

 //swerve lookahead distances
 public static double redCarouselLookaheadDistance = 100; // in mm
 public static double blueCarouselLookaheadDistance = 100;
 public static double redGapApproachToShippingHubLookaheadDistance = 100;
 public static double redWarehouseToShippingHubLookaheadDistance = 100;
 public static double blueGapApproachToShippingHubLookaheadDistance = 100;
 public static double blueWarehouseToShippingHubLookaheadDistance = 100;
 public static double redShippingHubToGapApproachLookaheadDistance = 100;
 public static double redShippingHubToWarehouseLookaheadDistance = 100;
 public static double blueShippingHubToGapApproachLookaheadDistance = 100;
 public static double blueShippingHubToWarehouseLookaheadDistance = 100;

 //swerve spacing
 public static double redCarouselSpacing = 20; // in mm
 public static double blueCarouselSpacing = 20;
 public static double redGapApproachToShippingHubSpacing = 20;
 public static double redWarehouseToShippingHubSpacing = 20;
 public static double blueGapApproachToShippingHubSpacing = 20;
 public static double blueWarehouseToShippingHubSpacing = 20;
 public static double redShippingHubToGapApproachSpacing = 20;
 public static double redShippingHubToWarehouseSpacing = 20;
 public static double blueShippingHubToGapApproachSpacing = 20;
 public static double blueShippingHubToWarehouseSpacing = 20;

 //Weight Smooth
 public static double redCarouselWeightSmooth = 0.5; // in mm
 public static double blueCarouselWeightSmooth = 0.5;
 public static double redGapApproachToShippingHubWeightSmooth = 0.7;
 public static double redWarehouseToShippingHubWeightSmooth = 0.7;
 public static double blueGapApproachToShippingHubWeightSmooth = 0.7;
 public static double blueWarehouseToShippingHubWeightSmooth = 0.7;
 public static double redShippingHubToGapApproachWeightSmooth = 0.7;
 public static double redShippingHubToWarehouseWeightSmooth = 0.7;
 public static double blueShippingHubToGapApproachWeightSmooth = 0.7;
 public static double blueShippingHubToWarehouseWeightSmooth = 0.7;

 //swerve turn speed (1-5)
 public static double redCarouselTurnSpeed = 2.5; // in mm
 public static double blueCarouselTurnSpeed = 2.5;
 public static double redGapApproachToShippingHubTurnSpeed = 2.5;
 public static double redWarehouseToShippingHubTurnSpeed = 2.5;
 public static double blueGapApproachToShippingHubTurnSpeed = 2.5;
 public static double blueWarehouseToShippingHubTurnSpeed = 2.5;
 public static double redShippingHubToGapApproachTurnSpeed = 2.5;
 public static double redShippingHubToWarehouseTurnSpeed = 2.5;
 public static double blueShippingHubToGapApproachTurnSpeed = 2.5;
 public static double blueShippingHubToWarehouseTurnSpeed = 2.5;


 //swerve max velocity
 public static double redCarouselMaxVelocity = 400; // in mm
 public static double blueCarouselMaxVelocity = 400;
 public static double redGapApproachToShippingHubMaxVelocity = 400;
 public static double redWarehouseToShippingHubMaxVelocity = 400;
 public static double blueGapApproachToShippingHubMaxVelocity = 400;
 public static double blueWarehouseToShippingHubMaxVelocity = 400;
 public static double redShippingHubToGapApproachMaxVelocity = 400;
 public static double redShippingHubToWarehouseMaxVelocity = 400;
 public static double blueShippingHubToGapApproachMaxVelocity = 400;
 public static double blueShippingHubToWarehouseMaxVelocity = 400;

 //ArrayLists to call in getPath
 public static ArrayList<Position> redCarousel = new ArrayList<Position>();
 public static ArrayList<Position> blueCarousel = new ArrayList<Position>();
 public static ArrayList<Position> redGapApproachToShippingHub = new ArrayList<Position>();
 public static ArrayList<Position> redWarehouseToShippingHub = new ArrayList<Position>();
 public static ArrayList<Position> blueGapApproachToShippingHub = new ArrayList<Position>();
 public static ArrayList<Position> blueWarehouseToShippingHub = new ArrayList<Position>();
 public static ArrayList<Position> redShippingHubToGapApproach = new ArrayList<Position>();
 public static ArrayList<Position> redShippingHubToWarehouse = new ArrayList<Position>();
 public static ArrayList<Position> blueShippingHubToGapApproach = new ArrayList<Position>();
 public static ArrayList<Position> blueShippingHubToWarehouse = new ArrayList<Position>();

 //Initialize Follower Constants
 public static FollowerConstants redCarouselFollowerConstants = new FollowerConstants(redCarouselLookaheadDistance,true);
 public static FollowerConstants blueCarouselFollowerConstants = new FollowerConstants(blueCarouselLookaheadDistance,true);
 public static FollowerConstants redGapApproachToShippingHubFollowerConstants = new FollowerConstants(redGapApproachToShippingHubLookaheadDistance,true);
 public static FollowerConstants redWarehouseToShippingHubFollowerConstants = new FollowerConstants(redWarehouseToShippingHubLookaheadDistance,true);
 public static FollowerConstants blueGapApproachToShippingHubFollowerConstants = new FollowerConstants(blueGapApproachToShippingHubLookaheadDistance,true);
 public static FollowerConstants blueWarehouseToShippingHubFollowerConstants = new FollowerConstants(blueWarehouseToShippingHubLookaheadDistance,true);
 public static FollowerConstants redShippingHubToGapApproachFollowerConstants = new FollowerConstants(redShippingHubToGapApproachLookaheadDistance,false);
 public static FollowerConstants redShippingHubToWarehouseFollowerConstants = new FollowerConstants(redShippingHubToWarehouseLookaheadDistance,false);
 public static FollowerConstants blueShippingHubToGapApproachFollowerConstants = new FollowerConstants(blueShippingHubToGapApproachLookaheadDistance,false);
 public static FollowerConstants blueShippingHubToWarehouseFollowerConstants = new FollowerConstants(blueShippingHubToWarehouseLookaheadDistance,false);

 public static SwervePathGenerationConstants redCarouselSwervePathGenerationConstants = new SwervePathGenerationConstants(redCarouselSpacing,redCarouselWeightSmooth,redCarouselTurnSpeed,redCarouselMaxVelocity);
 public static SwervePathGenerationConstants blueCarouselSwervePathGenerationConstants = new SwervePathGenerationConstants(blueCarouselSpacing,blueCarouselWeightSmooth,blueCarouselTurnSpeed,blueCarouselMaxVelocity);
 public static SwervePathGenerationConstants redGapApproachToShippingHubSwervePathGenerationConstants = new SwervePathGenerationConstants(redGapApproachToShippingHubSpacing,redGapApproachToShippingHubWeightSmooth,redGapApproachToShippingHubTurnSpeed,redGapApproachToShippingHubMaxVelocity);
 public static SwervePathGenerationConstants redWarehouseToShippingHubSwervePathGenerationConstants = new SwervePathGenerationConstants(redWarehouseToShippingHubSpacing,redWarehouseToShippingHubWeightSmooth,redWarehouseToShippingHubTurnSpeed,redWarehouseToShippingHubMaxVelocity);
 public static SwervePathGenerationConstants blueGapApproachToShippingHubSwervePathGenerationConstants = new SwervePathGenerationConstants(blueGapApproachToShippingHubSpacing,blueGapApproachToShippingHubWeightSmooth,blueGapApproachToShippingHubTurnSpeed,blueGapApproachToShippingHubMaxVelocity);
 public static SwervePathGenerationConstants blueWarehouseToShippingHubSwervePathGenerationConstants = new SwervePathGenerationConstants(blueWarehouseToShippingHubSpacing,blueWarehouseToShippingHubWeightSmooth,blueWarehouseToShippingHubTurnSpeed,blueWarehouseToShippingHubMaxVelocity);
 public static SwervePathGenerationConstants redShippingHubToGapApproachSwervePathGenerationConstants = new SwervePathGenerationConstants(redShippingHubToGapApproachSpacing,redShippingHubToGapApproachWeightSmooth,redShippingHubToWarehouseTurnSpeed,redShippingHubToGapApproachMaxVelocity);
 public static SwervePathGenerationConstants redShippingHubToWarehouseSwervePathGenerationConstants = new SwervePathGenerationConstants(redShippingHubToWarehouseSpacing,redShippingHubToWarehouseWeightSmooth,redShippingHubToWarehouseTurnSpeed,redShippingHubToWarehouseMaxVelocity);
 public static SwervePathGenerationConstants blueShippingHubToGapApproachSwervePathGenerationConstants = new SwervePathGenerationConstants(blueShippingHubToWarehouseSpacing,blueShippingHubToWarehouseWeightSmooth,blueShippingHubToWarehouseTurnSpeed,blueShippingHubToWarehouseMaxVelocity);
 public static SwervePathGenerationConstants blueShippingHubToWarehouseSwervePathGenerationConstants = new SwervePathGenerationConstants(blueShippingHubToWarehouseSpacing,blueShippingHubToWarehouseWeightSmooth,blueShippingHubToWarehouseTurnSpeed,blueShippingHubToWarehouseMaxVelocity);

 public static SwervePath redCarouselSwervePath;
 public static SwervePath blueCarouselSwervePath;
 public static SwervePath redGapApproachToShippingHubSwervePath;
 public static SwervePath redWarehouseToShippingHubSwervePath;
 public static SwervePath blueGapApproachToShippingHubSwervePath;
 public static SwervePath blueWarehouseToShippingHubSwervePath;
 public static SwervePath redShippingHubToGapApproachSwervePath;
 public static SwervePath redShippingHubToWarehouseSwervePath;
 public static SwervePath blueShippingHubToGapApproachSwervePath;
 public static SwervePath blueShippingHubToWarehouseSwervePath;

 public static void instantiatePaths(){
  redCarousel = Functions.instantiatePath(redCarousel1,redCarousel2,redCarousel3);
  //blueCarousel = Functions.instantiatePath();
  redGapApproachToShippingHub = Functions.instantiatePath(redGapApproachToShippingHub1,redGapApproachToShippingHub2,redGapApproachToShippingHub3,redGapApproachToShippingHub4);
  redWarehouseToShippingHub = Functions.instantiatePath(redWarehouseApproachToShippingHub1,redWarehouseApproachToShippingHub2,redWarehouseApproachToShippingHub3,redGapApproachToShippingHub4,redWarehouseApproachToShippingHub5,redWarehouseApproachToShippingHub6);
  blueGapApproachToShippingHub = Functions.instantiatePath(blueGapApproachToShippingHub1,blueGapApproachToShippingHub2,blueGapApproachToShippingHub3,blueGapApproachToShippingHub4);
  blueWarehouseToShippingHub = Functions.instantiatePath(blueWarehouseApproachToShippingHub1,blueWarehouseApproachToShippingHub2,blueWarehouseApproachToShippingHub3,blueWarehouseApproachToShippingHub4,blueWarehouseApproachToShippingHub4,blueWarehouseApproachToShippingHub5,blueWarehouseApproachToShippingHub6);
  redShippingHubToGapApproach = Functions.reversePath(redGapApproachToShippingHub);
  redShippingHubToWarehouse = Functions.reversePath(redWarehouseToShippingHub);
  blueShippingHubToGapApproach = Functions.reversePath(blueGapApproachToShippingHub);
  blueShippingHubToWarehouse = Functions.reversePath(blueWarehouseToShippingHub);
 }

 public static ArrayList<Position> getPath(ArrayList<Position> pathArray){
  instantiatePaths();
  return pathArray;
 }

 public static void generateAutoPaths(){
  instantiatePaths();
  redCarouselSwervePath = generateSwervePath(redCarousel, redCarouselFollowerConstants, redCarouselSwervePathGenerationConstants);
  //blueCarouselSwervePath = generateSwervePath(blueCarousel, blueCarouselFollowerConstants, blueCarouselSwervePathGenerationConstants);
  redGapApproachToShippingHubSwervePath = generateSwervePath(redGapApproachToShippingHub, redGapApproachToShippingHubFollowerConstants, redGapApproachToShippingHubSwervePathGenerationConstants);
  redWarehouseToShippingHubSwervePath = generateSwervePath(redWarehouseToShippingHub, redWarehouseToShippingHubFollowerConstants, redWarehouseToShippingHubSwervePathGenerationConstants);
  //blueGapApproachToShippingHubSwervePath = generateSwervePath(blueGapApproachToShippingHub, blueGapApproachToShippingHubFollowerConstants, blueGapApproachToShippingHubSwervePathGenerationConstants);
  //blueWarehouseToShippingHubSwervePath = generateSwervePath(blueWarehouseToShippingHub, blueWarehouseToShippingHubFollowerConstants, blueWarehouseToShippingHubSwervePathGenerationConstants);
  redShippingHubToGapApproachSwervePath = generateSwervePath(redShippingHubToGapApproach, redShippingHubToGapApproachFollowerConstants, redShippingHubToGapApproachSwervePathGenerationConstants);
  redShippingHubToWarehouseSwervePath = generateSwervePath(redShippingHubToWarehouse, redShippingHubToWarehouseFollowerConstants, redShippingHubToWarehouseSwervePathGenerationConstants);
  //blueShippingHubToGapApproachSwervePath = generateSwervePath(blueShippingHubToGapApproach, blueShippingHubToGapApproachFollowerConstants, blueShippingHubToGapApproachSwervePathGenerationConstants);
  //blueShippingHubToWarehouseSwervePath = generateSwervePath(blueShippingHubToWarehouse, blueShippingHubToWarehouseFollowerConstants, blueShippingHubToWarehouseSwervePathGenerationConstants);

 }
}


