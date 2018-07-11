package com.example.sweet.game20;


import android.graphics.PointF;

import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.Player;
import com.example.sweet.game20.util.CollisionHandler;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.ScreenShake;
import com.example.sweet.game20.util.VectorFunctions;

import java.util.Iterator;


public class AIThread implements Runnable
{
    public Player player1;

    public Enemy[] entities;

    public volatile boolean
            running = true,
            pause = false;

    private boolean saveTime = false;

    private double pauseTime = 0;

    public volatile long frameRequest = 0;
    private long currentFrame = 0;

    /*private PointF panToward = new PointF(0,0);

    public float
            xbound = 0,
            ybound = 0,
            xScreenShift = 0,
            yScreenShift = 0,
            cameraSpeed = .014f,
            cameraPanX = 0f,
            cameraPanY = 0f,
            cameraClamp = .16f,
            screenShakeX = 0,
            screenShakeY = 0;

    public volatile float
            movementOnDownX = 0f,
            movementOnMoveX = 0f,
            shootingOnDownX = 0f,
            shootingOnMoveX = 0f,
            movementOnDownY = 0f,
            movementOnMoveY = 0f,
            shootingOnDownY = 0f,
            shootingOnMoveY = 0f;

    public volatile boolean
            movementDown = false,
            shootingDown = false;*/

    public AIThread(Enemy[] e)
    {
        entities = e;
    }

    public void run()
    {
        while(running)
        {
            /*double currentTime = System.currentTimeMillis() - globalStartTime;
            double elapsedTime = currentTime - pastTime;
            pastTime = currentTime;
            lag += elapsedTime;


            while (lag >= mSPU)
            {
                //if(!pause)
                update();
                lag -= mSPU;
            }*/
            if(!pause)
            {
                if(!saveTime)
                {
                    double pauseLength = System.currentTimeMillis() - pauseTime;
                    player1.applyPauseLength(pauseLength);
                    for(Enemy e: entities)
                    {
                        if(e != null)
                        {
                            e.applyPauseLength(pauseLength);
                        }
                    }
                    saveTime = true;
                }

                if (currentFrame < frameRequest)
                {
                    //System.out.println("FRAME DIFFERENCE: " + (frameRequest - currentFrame));
                    currentFrame++;
                    update();
                }
            }
            else
            {
                if(saveTime)
                {
                    pauseTime = System.currentTimeMillis();
                    saveTime = false;
                }
            }
        }
    }
    
    public void update()
    {
        enemyActions();
    }
    
    public void enemyActions()
    {
        for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            if(entities[i] != null && !entities[i].aiRemoveConsensus)
            {
                if (entities[i].getPixelGroup().getCollidableLive())
                {
                    for (Enemy e2 : entities)
                    {
                        if(e2 != null && !e2.aiRemoveConsensus)
                        {
                            if (e2.getPixelGroup().getCollidableLive() && entities[i] != e2)
                            {
                                CollisionHandler.preventOverlap(entities[i], e2);
                            }
                        }
                    }

                    entities[i].move(player1.getPixelGroup().getCenterX(), player1.getPixelGroup().getCenterY());
                    //entities[i].move(0, 0);
                }
                else
                {
                    entities[i].aiRemoveConsensus = true;
                }
            }
        }
    }

    public synchronized void setPlayer(Player p)
    {
        player1 = p;
    }
}
