package org.whitneyrobotics.ftc.teamcode.lib.util;

public class GamepadListener {

    public GamepadListener() {}

    //private boolean firstLongPress = true; //Starts the timer ONCE
    private long longPressStartTime = System.nanoTime(); //Lets you measure when button is starting to be held
    //Stops long press from continuously firing if button is held
    //private boolean longPressFired = false;

    private boolean longPressExpired = true;
    private int longPressState = 0;

    public boolean longPress(boolean button, double minThresholdms){
        switch(longPressState){
            case 0:
                if(button){
                    longPressStartTime = System.nanoTime();
                    longPressExpired = false;
                    longPressState++;
                }
                break;
            case 1:
                if(!button){
                    longPressExpired = true;
                } else if((System.nanoTime()-longPressStartTime)/1E6 >= minThresholdms) {
                    longPressState++;
                    break;
                }
                break;
            case 2:
                longPressState++;
                return true;
            case 3:
                if(!button){
                    longPressExpired = true;
                    longPressState = 0;
                }
                break;
        }
        if(longPressExpired){
            longPressState = 0;
        }
        return false;
    }

    public boolean longPress(boolean button){
        return longPress(button, 1000);
    }

    private boolean shortPressExpired = true;
    private long shortPressStartTime;
    private int shortPressState = 0;

    public boolean shortPress(boolean button, double maxThresholdms) {
       switch(shortPressState){
           case 0:
               if(button){
                   shortPressStartTime = System.nanoTime();
                   shortPressExpired = false;
                   shortPressState++;
               }
               break;
           case 1:
               if(!button){
                   long endTime = System.nanoTime();
                   if((endTime-shortPressStartTime)/1E6 <= maxThresholdms){
                       shortPressState++;
                       break;
                   } else {
                       shortPressExpired=true;
                   }
                   if((System.nanoTime() - shortPressStartTime)/1E6 > maxThresholdms){
                       shortPressExpired = true;
                   }
               }
               break;
           case 2:
               shortPressState++;
               return true;
           case 3:
               if(!button){
                   shortPressExpired = true;
                   shortPressState = 0;
               }
               break;
       }
       if(shortPressExpired){
           shortPressState = 0;
       }
        return false;
    }

    public boolean shortPress(boolean button){
        return shortPress(button, 250);
    }

    private int doublePressState = 0;
    private boolean doublePressExpired = true;
    private long initialPressTime;
    private long firstPressEndTime;

    public boolean doublePress(boolean button, double maxPressIntervalMs){
        switch(doublePressState){
            case 0:
                if(button){
                    initialPressTime = System.nanoTime();
                    doublePressExpired = false;
                    doublePressState++;
                }
                break;
            case 1:
                if ((System.nanoTime()-initialPressTime)/1E6 > (maxPressIntervalMs)){
                    doublePressExpired=true;
                } else if(!button) {
                    firstPressEndTime = System.nanoTime();
                    doublePressState++;
                    break;
                }
                break;
            case 2:
                if(button){
                    if((System.nanoTime()-firstPressEndTime)/1E6 <= maxPressIntervalMs){
                        doublePressState++;
                        break;
                    }
                }  else if ((System.nanoTime()-initialPressTime)/1E6 > (maxPressIntervalMs)){
                    doublePressExpired=true;
                }
                break;
            case 3:
                doublePressExpired=true;
                doublePressState = 0;
                return true;
        }
        if(doublePressExpired){
            doublePressState = 0;
        }
        return false;
    }

    private boolean doublePress(boolean button){
        return doublePress(button, 500);
    }

    public int getDoublePressState(){return doublePressState;}

}
