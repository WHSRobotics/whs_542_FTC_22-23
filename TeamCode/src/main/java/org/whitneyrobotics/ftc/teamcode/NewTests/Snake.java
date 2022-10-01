package org.whitneyrobotics.ftc.teamcode.NewTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions.*;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrain;

import java.util.ArrayList;
import java.util.LinkedList;

@TeleOp(name="Snake",group="z")
@Disabled
public class Snake extends LinearOpMode {
    public String[][] board = new String[13][13];

    public LinkedList<Position> snake = new LinkedList<>();

    public Direction currentDirection = Direction.NONE;

    public enum Direction {
        LEFT, UP, RIGHT, DOWN, NONE
    }

    public boolean checkForCollision(){
        Position snakeHead = snake.get(0);
        if(snakeHead.getX() == 0 || snakeHead.getX() == board[0].length){
            return true;
        }
        if(snakeHead.getY() == 0 || snakeHead.getY() == board.length){
            return true;
        }
        LinkedList<Position> shallowCopy = snake;
        shallowCopy.remove(0);
        for(Position pos : shallowCopy){
            if (pos.getX() == snakeHead.getX() || pos.getY() == pos.getY()){
                return true;
            }
        }

        return false;
    }

    public void growSnake(Direction snakeDir, Position lastSegment){
        switch(snakeDir){
            case UP:
                snake.add(new Position(lastSegment.getX(), lastSegment.getY()-1));
                break;
            case DOWN:
                snake.add(new Position(lastSegment.getX(), lastSegment.getY()+1));
                break;
            case LEFT:
                snake.add(new Position(lastSegment.getX()+1, lastSegment.getY()));
                break;
            case RIGHT:
                snake.add(new Position(lastSegment.getX()-1, lastSegment.getY()));
                break;
        }
    }

    public boolean checkForFruit(){
        for(Position f : fruitList){
            if(snake.getFirst().equals(f)){
                growSnake(currentDirection, new Position(0,0));
                return true;
            }
        }
        return false;
    }

    public int checkForPositionInSnake(Position p){
        for(int i = 0; i<snake.size(); i++){
            if(snake.get(i).getX() == p.getX() && snake.get(i).getY() == p.getY()){
                return i;
            }
        }
        return -1;
    }

    public void moveSnake(){
        Position snakeHead = snake.getFirst();
        Position currentEnd = snake.getLast();
        switch(currentDirection){
            case UP:
                snake.add(new Position(snakeHead.getX(), snakeHead.getY()+1));
                break;
            case DOWN:
                snake.add(new Position(snakeHead.getX(), snakeHead.getY()-1));
                break;
            case LEFT:
                snake.add(new Position(snakeHead.getX()-1, snakeHead.getY()));
                break;
            case RIGHT:
                snake.add(new Position(snakeHead.getX()+1, snakeHead.getY()));
                break;
        }
        if(checkForFruit()){
            snake.push(currentEnd);
        } else {
            snake.remove(snake.size()-1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String renderBoard() {

        for(int y = 0; y< board.length; y++){
            for(int x = 0; x<board[y].length; x++){
                Position currentPos = new Position(x,y);
                if(checkForPositionInSnake(currentPos) != -1){
                    if(snake.getFirst().equals(currentPos)){
                        board[y][x] = "\uD83D\uDC32";
                    } else {
                        board[y][x] = "\uD83D\uDFE9";
                    }
                } else {
                    if(currentPos.getX() == 0 || currentPos.getX() >= board[0].length-1 || currentPos.getY() == 0 || currentPos.getY() >= board.length-1){
                        board[y][x] = "⬛️";
                    } else {
                        board[y][x] = "⬜️";
                    }
                }
            }
        }
        ArrayList<String> outputRows = new ArrayList<>(); //bc im too lazy to do stuff and theres no array.append method
        for(String[] rows : board){
            outputRows.add(String.join("",rows));
        }

        return String.join("\n",outputRows);
    }

    public int maxFruits = (int) Math.ceil(board.length * board[0].length / 33.8);
    public ArrayList<Position> fruitList = new ArrayList<>();
    public void updateFruitList() {
        if (fruitList.size() < maxFruits) {
            fruitList.add(randomizeFruitPosition(board));
        }
    }

    public Position randomizeFruitPosition(String[][] board){
        int height = board.length-2;
        int width = board.length-2;
        int randY;
        int randX;
        do {
            randY = Functions.randInt(0, height);
            randX = Functions.randInt(0, width);
        } while(board[randY][randX] != "⬜️");
        return new Position(randX,randY);
    }

    public void setCurrentDirection(Gamepad g1, Gamepad g2){
        if(g1.dpad_left || g2.dpad_left || g1.left_stick_x < -0.5 || g2.left_stick_x < -0.5) currentDirection = Direction.LEFT;
        if(g1.dpad_right || g2.dpad_right || g1.left_stick_x > 0.5 || g2.left_stick_x > 0.5) currentDirection = Direction.RIGHT;
        if(g1.dpad_down || g2.dpad_down || g1.left_stick_y > 0.5 || g2.left_stick_y > 0.5) currentDirection = Direction.DOWN;
        if(g1.dpad_up || g2.dpad_up || g1.left_stick_y < -0.5 || g2.left_stick_y < -0.5) currentDirection = Direction.UP;
    }

    private boolean gameOver;
    Drivetrain drivetrain;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void runOpMode() throws InterruptedException {
        //drivetrain= new Drivetrain(hardwareMap);
        snake.push(new Position(Math.floor(board[0].length/2), Math.floor(board.length)/2));
        telemetry.addLine("CONTROLS: Dpad/Stick to Move");
        telemetry.addLine(renderBoard());
        telemetry.update();
        waitForStart();
        while(!isStopRequested()){
            if(gameOver){
                throw new RuntimeException("SUSSY BUSSY");
                //drivetrain.operate(-1,1);
            } else {
                updateFruitList();
                //checkForCollision();
                setCurrentDirection(gamepad1,gamepad2);
                moveSnake();
                sleep(500);
            }
            telemetry.addLine(renderBoard());
            telemetry.addData("Snake length", snake.size());
            telemetry.addData("Max Fruits",maxFruits);
            telemetry.addData("Fruits Size", fruitList.size());
            telemetry.update();
        }
    }


}
