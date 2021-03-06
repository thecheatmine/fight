package fr.iutlens.mmi.fight;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by dubois on 09/01/2019.
 */

public class History {
    public static final int FRAME_PER_ROUND = 500;
    private final List<Sprite> laserA;
    private final List<Sprite> laserB;
    public Perso persoA,persoB;
    private final ArrayList<Command> commandList;
    private long frame;
    public int round;


    Map<String,Perso> perso;
    private ListIterator<Command> iterator;
    private Command nextCommand;

    public History(List<Sprite> laserA, List<Sprite> laserB) {
        this.laserA = laserA;
        this.laserB = laserB;


        round = 0;

        perso = new HashMap<>();
        commandList = new ArrayList<Command>();
        prepareRound();


    }

    private void prepareRound() {
        frame = 0L;
        iterator = commandList.listIterator();
        if (iterator.hasNext()) nextCommand = iterator.next(); else nextCommand = null;
        createPersoA();
        createPersoB();
        if (round ==0){
            persoA = perso.get("A0");
            persoB = perso.get("B0");
        }
        round += 1;
        perso.put("A"+round,persoA);
        perso.put("B"+round,persoB);
        iterator = commandList.listIterator();
        if (iterator.hasNext()) nextCommand = iterator.next(); else nextCommand = null;

        registerCommand(new MoveToCommand(frame,"A"+round,persoA.x,persoA.y));
        registerCommand(new MoveToCommand(frame,"B"+round,persoB.x,persoB.y));

    }

    private void createPersoA() {
        perso.put("A"+round, new Perso(round == 0 ? R.mipmap.spritepersoblue : R.mipmap.spritefantomeblue,GameView.SIZE_X/4, (GameView.SIZE_Y/2)-32,laserA));
    }

    private void createPersoB() {
        perso.put("B"+round, new Perso(round == 0 ? R.mipmap.spritepersored : R.mipmap.spritefantomered,3*GameView.SIZE_X/4, (GameView.SIZE_Y/2)-32,laserB));
    }


    public void registerCommand(Command command) { // target = "A" ou "B"
        iterator.add(command);
    }


    public void act(){
        while (nextCommand != null && nextCommand.timestamp <= frame){
            nextCommand.apply(this);

            if (iterator.hasNext()) nextCommand = iterator.next(); else nextCommand = null;
        }

        for(Sprite s : perso.values()){
            if (s != persoA && s != persoB)
                s.act(false);
        }
        persoA.act(false);
        persoB.act(false);

        for(int i = 0; i < laserB.size(); i++) {
            if(
                laserB.get(i).x > persoA.x-10
                && laserB.get(i).x < (persoA.x+60)
                && laserB.get(i).y > persoA.y-30
                && laserB.get(i).y < (persoA.y+90)
            ) {
                persoA.vie -= 1;
                laserB.remove(i);
            }
            else if(laserB.get(i).x < -100){
                laserB.remove(i);
            }
        }

        for(int i = 0; i < laserA.size(); i++) {
            if(
                laserA.get(i).x > persoB.x-10
                && laserA.get(i).x < (persoB.x+60)
                && laserA.get(i).y > persoB.y-30
                && laserA.get(i).y < (persoB.y+90)
            ) {
                persoB.vie -= 1;
                laserA.remove(i);
            }
            else if(laserA.get(i).x > GameView.SIZE_X + 100){
                laserA.remove(i);
            }
        }

        frame++;
        if (frame == FRAME_PER_ROUND) prepareRound();
    }

    public void moveA(float speed, float direction) {
        if(persoA.x > GameView.SIZE_X/2){
            final MoveCommand command = new MoveCommand(frame, "A" + round, speed/2, direction);
            registerCommand(command);
            command.apply(this);
        }
        else {
            final MoveCommand command = new MoveCommand(frame, "A" + round, speed*5, direction);
            registerCommand(command);
            command.apply(this);
        }
    }

    public void moveB(float speed, float direction) {
        if(persoB.x < GameView.SIZE_X/2){
            final MoveCommand command = new MoveCommand(frame, "B" + round, speed/2, direction);
            registerCommand(command);
            command.apply(this);
        }
        else {
            final MoveCommand command = new MoveCommand(frame, "B" + round, speed*5, direction);
            registerCommand(command);
            command.apply(this);
        }
    }

    public void fireA() {
        final FireCommand command = new FireCommand(frame, "A" + round);
        registerCommand(command);
        command.apply(this);
    }

    public void fireB() {
        final FireCommand command = new FireCommand(frame, "B" + round);
        registerCommand(command);
        command.apply(this);
    }

    public void paint(Canvas canvas) {
        for(Sprite s : perso.values()){
            if (s != persoA && s != persoB)
            s.paint(canvas);
        }
        persoA.paint(canvas);
        persoB.paint(canvas);
    }
}
