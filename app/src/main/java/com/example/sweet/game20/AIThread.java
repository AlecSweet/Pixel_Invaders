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

    private GlobalInfo globalInfo;
    private ArrayList<TimeSlowEvent> timeSlowEvents;

    public volatile Enemy[] entities;

    public volatile boolean
            running = true,
            pause = false;

    private boolean saveTime = false;

    private CollisionHandler collisionHandler;

    private double pauseTime = 0;

    public volatile long currentFrame = 0;
    public volatile long frameRequest = 0;

    private final long MILLIS_PER_SECOND = 1000;
    private final long UPS = 60;
    private final long mSPU = MILLIS_PER_SECOND / UPS;

    private float slowTime = 1;
    private static final int INVALID_POINTER_ID = -1;

    public AIThread(Enemy[] e, GlobalInfo gI, CollisionHandler cH)
    {
        collisionHandler = cH;
        entities = e;
        globalInfo = gI;
    }

    public void run()
    {
        while(running)
        {
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
                    currentFrame++;
                    long startTime = System.currentTimeMillis();
                    update();
                    if(averageFrameTime == 0)
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
                    pauseTime = System.currentTimeMillis();
                    //globalInfo.setTimeSlow(globalInfo.timeSlow*.5f);
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
        globalInfo.timeSlow = 1;
        Iterator<TimeSlowEvent> itrT = player1.timeSlowEvents.iterator();
        while(itrT.hasNext())
        {
            TimeSlowEvent t = itrT.next();
            if(!t.live)
            {
                itrT.remove();
            }
            else
            {
                globalInfo.setTimeSlow(slowTime * t.getSlow());
            }
        }

        player1.shoot(currentFrame, globalInfo);
        player1.moveCamera();
        player1.handleScreenShake();
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

                    entities[i].move(player1.getPixelGroup().getCenterX(),
                            player1.getPixelGroup().getCenterY(),
                            currentFrame,
                            globalInfo.timeSlow
                    );

                    if (entities[i].getPixelGroup().getCollidableLive())
                    {
                        if (Math.abs(entities[i].getX() - player1.xScreenShift) * globalInfo.getScaleX() <=
                                1 + entities[i].getPixelGroup().getHalfSquareLength() * 2 &&
                                Math.abs(entities[i].getY() - player1.yScreenShift) * globalInfo.getScaleY() <=
                                1 + entities[i].getPixelGroup().getHalfSquareLength() * 2)
                        {
                            entities[i].onScreen = true;
                        }
                        else
                        {
                            entities[i].onScreen = false;
                        }
                    }
                    //entities[i].move(0, 0);
                }
                else
                {
                    player1.screenShakeEventsX.add(new ScreenShake(
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
                    ));
                    player1.timeSlowEvents.add(new TimeSlowEvent(.7f, 400));
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
