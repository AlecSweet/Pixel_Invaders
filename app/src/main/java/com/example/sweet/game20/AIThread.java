package com.example.sweet.game20;


import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.Player;

import java.util.ArrayList;

public class AIThread implements Runnable
{
    public Player player1;

    public ArrayList<Enemy> entities;

    public volatile boolean running = true;
    public volatile boolean aImove = true;
    
    private final long MILLIS_PER_SECOND = 1000;
    private final long UPS = 120;

    private final long mSPU = MILLIS_PER_SECOND / UPS;
    
    private double
            globalStartTime,
            pastTime,
            lag = 0.0;
    
    public AIThread()
    {
        //player1 = p1;
        //entities = e;
        entities = new ArrayList<>();
        //globalStartTime = System.currentTimeMillis();
    }

    public void run()
    {
        while(running)
        {
            //double currentTime = System.currentTimeMillis()-globalStartTime;
            //double elapsedTime = currentTime - pastTime;
            //pastTime = currentTime;
            //lag += elapsedTime;
            if(aImove)
            {
                update();
                aImove = false;
            }
        }
    }
    
    public void update()
    {
        enemyActions();
    }
    
    public void enemyActions()
    {
        int eS = entities.size();
        for(int i = 0; i < eS; i++)
        {
            /*  Move all entities;
             */
            if(entities.get(i).getPixelGroup().getCollidableLive())
            {
                entities.get(i).move(player1.getPixelGroup().getCenterX(), player1.getPixelGroup().getCenterY());
            }
        }
    }

    public synchronized void setEntities(ArrayList<Enemy> e)
    {
        entities = e;
    }

    public synchronized void setPlayer(Player p)
    {
        player1 = p;
    }
}
