package com.example.sweet.game20;


import android.graphics.PointF;

import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.GunComponent;
import com.example.sweet.game20.Objects.Player;
import com.example.sweet.game20.util.CollisionHandler;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.VectorFunctions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AIThread implements Runnable
{
    public Player player1;

    public Enemy[] entities;
    public volatile boolean running = true;

    public volatile long frameRequest = 0;

    private long currentFrame = 0;
    private final long MILLIS_PER_SECOND = 1000;
    private final long UPS = 60;

    private final long mSPU = MILLIS_PER_SECOND / UPS;

    private double
            globalStartTime,
            pastTime,
            lag = 0.0;

    private PointF panToward = new PointF(0,0);

    public volatile float
            xbound = 0,
            ybound = 0,
            xScreenShift = 0,
            yScreenShift = 0,
            cameraSpeed = .014f,
            cameraPanX = 0f,
            cameraPanY = 0f,
            cameraClamp = .16f;

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
            shootingDown = false;
    
    public AIThread(Enemy[] e)
    {
        globalStartTime = System.currentTimeMillis();
        entities = e;
    }

    public void run()
    {
        while(running)
        {
            double currentTime = System.currentTimeMillis() - globalStartTime;
            double elapsedTime = currentTime - pastTime;
            pastTime = currentTime;
            lag += elapsedTime;


            while (lag >= mSPU)
            {
                //if(!pause)
                update();
                lag -= mSPU;
            }

            /*if(currentFrame < frameRequest)
            {
                //System.out.println("FRAME DIFFERENCE: " + (frameRequest - currentFrame));
                currentFrame++;
                update();
            }*/
        }
    }
    
    public void update()
    {
        enemyActions();
        movePlayer();
        moveCamera();
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

    public void movePlayer()
    {
        if(movementOnDownX > .8f)
        {
            player1.getPixelGroup().resetPixels();
        }

        if(movementDown)
        {
            player1.move(movementOnMoveX - movementOnDownX, movementOnMoveY - movementOnDownY);
        }

        if(shootingDown)
        {
            float diffX = shootingOnMoveX - shootingOnDownX;
            float diffY = shootingOnMoveY - shootingOnDownY;
            float tempMagnitude = VectorFunctions.getMagnitude(diffX, diffY);

            if (tempMagnitude > .1)
            {
                player1.shoot(diffX, diffY);
            }
        }

        if(player1.getGuns()!= null)
        {
            for (GunComponent gC : player1.getGuns())
            {
                if (gC != null)
                {
                    gC.gun.move();
                }
            }
        }
    }

    public void moveCamera()
    {
        if (shootingDown)
        {
            float diffX = shootingOnMoveX - shootingOnDownX;
            float diffY = shootingOnMoveY - shootingOnDownY;
            float tempMagnitude = VectorFunctions.getMagnitude(diffX, diffY);
            if (tempMagnitude > .1)
            {
                panToward.set(cameraClamp * diffX / tempMagnitude, cameraClamp * diffY / tempMagnitude);
            }
            else
            {
                panToward.set(0, 0);
            }
        }
        else
        {
            panToward.set(0, 0);
        }

        float panAngle;
        float panDiffX = panToward.x - cameraPanX;
        float panDiffY = panToward.y - cameraPanY;
        float panMag = VectorFunctions.getMagnitude(panDiffX, panDiffY);

        if (panMag > .01)
        {
            panAngle = (float) Math.atan2(panToward.y - cameraPanY, panToward.x - cameraPanX);
            cameraPanX += cameraSpeed * Math.cos(panAngle);
            cameraPanY += cameraSpeed * Math.sin(panAngle);
        }
        else
        {
            cameraPanX = panToward.x;
            cameraPanY = panToward.y;
        }

        if (player1.getPixelGroup().getCenterX() + cameraPanX < xbound && player1.getPixelGroup().getCenterX() + cameraPanX > -xbound)
        {
            xScreenShift = player1.getPixelGroup().getCenterX() + cameraPanX;
        }
        if (player1.getPixelGroup().getCenterY() - cameraPanY < ybound && player1.getPixelGroup().getCenterY() - cameraPanY > -ybound)
        {
            yScreenShift = player1.getPixelGroup().getCenterY() - cameraPanY;
        }
    }

    /*public synchronized void setEntities(ArrayList<Enemy> e)
    {
        //entities = e;
        aImove = true;
    }

    public void addEntity(Enemy e)
    {
        entities.add(e);
    }
       */
    public synchronized void setPlayer(Player p)
    {
        player1 = p;
    }
}
