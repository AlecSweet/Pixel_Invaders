package com.example.sweet.game20;


import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.sweet.game20.Objects.Bullet;
import com.example.sweet.game20.Objects.Drop;
import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.GunComponent;
import com.example.sweet.game20.Objects.Player;
import com.example.sweet.game20.Objects.TimeSlowEngine;
import com.example.sweet.game20.Objects.TimeSlowEvent;
import com.example.sweet.game20.util.CollisionHandler;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.ScreenShake;
import com.example.sweet.game20.util.VectorFunctions;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL;


public class AIThread implements Runnable
{
    public Player player1;

    public volatile float averageFrameTime = 0;

    private volatile GlobalInfo globalInfo;
    //private ArrayList<TimeSlowEvent> timeSlowEvents;

    public volatile Enemy[] entities;
    public volatile Enemy[] target;
    public volatile boolean
            running = true,
            //pause = false,
            block = false,
            inBlock = false;

    private boolean saveTime = false;
    private float oldTime = 1;

    private CollisionHandler collisionHandler;

    private double pauseTime = 0;

    public volatile long currentFrame = 0;
    public volatile long frameRequest = 0;

    private TimeSlowEngine timeEngine;

    /*private final long MILLIS_PER_SECOND = 1000;
    private final long UPS = 60;
    private final long mSPU = MILLIS_PER_SECOND / UPS;*/

    //private float slowTime = 1;
    private static final int INVALID_POINTER_ID = -1;

    public final Object lock = new Object();

    public AIThread(Enemy[] e, GlobalInfo gI, CollisionHandler cH)
    {
        collisionHandler = cH;
        entities = e;
        globalInfo = gI;
        timeEngine = new TimeSlowEngine(globalInfo, 20);
    }

    public void run()
    {
        while(running)
        {
            checkBlock();
            if(!globalInfo.getPause())
            {
                if (!saveTime)
                {
                    /*double pauseLength = System.currentTimeMillis() - pauseTime;
                    player1.applyPauseLength(pauseLength);
                    for (Enemy e : entities)
                    {
                        if (e != null)
                        {
                            e.applyPauseLength(pauseLength);
                        }
                    }*/
                    globalInfo.setTimeSlow(oldTime);
                    saveTime = true;
                }

                if (currentFrame < frameRequest)
                {
                    currentFrame++;
                    long startTime = System.currentTimeMillis();
                    update();
                    if (averageFrameTime == 0)
                    {
                        averageFrameTime = startTime - System.currentTimeMillis();
                    }
                    else
                    {
                        averageFrameTime = (averageFrameTime + (System.currentTimeMillis() - startTime)) / 2;
                    }
                }

            /*double currentTime = System.currentTimeMillis() - globalStartTime;
            double elapsedTime = currentTime - pastTime;
            pastTime = currentTime;
            lag += elapsedTime;

            while (lag >= mSPU)
            {
                //if(!pause)
                update();
                currentFrame++;
                //glSurfaceView.requestRender();
                lag -= mSPU;
            }*/
            }
            else
            {
                if(saveTime)
                {
                    oldTime = globalInfo.getTimeSlow();
                    globalInfo.setTimeSlow(0);
                    saveTime = false;
                }
            }
        }
    }
    
    public void update()
    {
        /*float s = (float)(Math.cos(tempInc))*.25f + .75f;
        globalInfo.setTimeSlow(s);
        tempInc += .1;*/
        /*if(target != null){
            entities = target;
            target = null;
        }*/
        timeEngine.runEngine();
        /*Iterator<TimeSlowEvent> itrT = player1.timeSlowEvents.iterator();
        while(itrT.hasNext())
        {
            TimeSlowEvent t = itrT.next();
            if(!t.active)
            {
                itrT.remove();
            }
            else
            {
                globalInfo.setTimeSlow(slowTime * t.getSlow());
            }
        }*/

        player1.shoot(currentFrame, globalInfo);
        player1.moveConsumables();
        player1.moveCamera();
        player1.handleScreenShake(globalInfo.gameSettings.screenShakePercent);
        //player1.movePlayer(globalInfo.timeSlow);
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
                    if(entities[i].checkOverlap)
                    {
                        for (Enemy e2 : entities)
                        {
                            if (e2 != null && !e2.aiRemoveConsensus)
                            {
                                if (e2.getPixelGroup().getCollidableLive() && entities[i] != e2)
                                {
                                    CollisionHandler.preventOverlap(entities[i], e2);
                                }
                            }
                        }
                    }

                    /*entities[i].move(player1.getPixelGroup().getCenterX(),
                            player1.getPixelGroup().getCenterY(),
                            currentFrame,
                            globalInfo.timeSlow
                    );*/
                    entities[i].move(player1.getPixelGroup().getCenterX(),
                            player1.getPixelGroup().getCenterY()
                    );

                    if (entities[i].getPixelGroup().getCollidableLive())
                    {
                        float difX = Math.abs(entities[i].getX() - player1.xScreenShift) * globalInfo.getScaleX();
                        float difY = Math.abs(entities[i].getY() - player1.yScreenShift) * globalInfo.getScaleY();
                        if (difX <= (1 + entities[i].getPixelGroup().getHalfSquareLength()) &&
                                difY <= (1 + entities[i].getPixelGroup().getHalfSquareLength()))
                        {
                            entities[i].onScreen = true;
                            if(difX <= 1 && difY <= 1)
                            {
                                entities[i].inRange = true;
                            }
                            else
                            {
                                entities[i].inRange = false;
                            }
                        }
                        else
                        {
                            entities[i].onScreen = false;
                        }
                    }

                    if(entities[i].getPixelGroup().readyToScreenShake && entities[i].getPixelGroup().pixelsKilled > 0)
                    {
                        player1.shakeEngine.addShake(
                                (float)(.002f * Math.sqrt(entities[i].getPixelGroup().pixelsKilled)),
                                120,
                                500
                        );
                        entities[i].getPixelGroup().pixelsKilled = 0;
                        entities[i].getPixelGroup().readyToScreenShake = false;
                    }
                    else
                    {
                        entities[i].getPixelGroup().readyToScreenShake = false;
                    }
                    //entities[i].move(0, 0);
                }
                else
                {
                   /* player1.screenShakeEventsX.add(new ScreenShake(
                            (float)(.002f * Math.sqrt(entities[i].getPixelGroup().totalPixels)),
                            120,
                            1000,
                            globalInfo
                    ));
                    player1.screenShakeEventsY.add(new ScreenShake(
                            (float)(.002f * Math.sqrt(entities[i].getPixelGroup().totalPixels)),
                            120,
                            1000,
                            globalInfo
                    ));*/
                    player1.shakeEngine.addShake(
                            (float)(.002f * Math.sqrt(entities[i].getPixelGroup().totalPixels)),
                            120,
                            1000
                    );
                    //player1.timeSlowEvents.add(new TimeSlowEvent(.7f, 400));
                    if(globalInfo.gameSettings.slowOnKill)
                    {
                        timeEngine.addSlow(.7f, 400);
                    }
                    entities[i].aiRemoveConsensus = true;
                }
            }
        }
        publishLocations();
    }

    public void publishLocations()
    {
        for(Enemy e: entities)
        {
            if(e != null && !e.aiRemoveConsensus)
            {
                e.publishLocation(currentFrame);
            }
        }

        player1.publishLocation(currentFrame);
    }
    public synchronized void setPlayer(Player p)
    {
        player1 = p;
    }

    public void setInfo(Player p, Enemy[] e, GlobalInfo gI)
    {
        player1 = p;
        entities = e;
        globalInfo = gI;
        currentFrame = 0;
        frameRequest = 0;
    }

    private void checkBlock()
    {
        if(block)
        {
            inBlock = true;
            synchronized (lock)
            {
                try
                {
                    lock.wait();
                }
                catch(InterruptedException e)
                {
                }
            }
            block = false;
            inBlock = false;
        }
    }
}
