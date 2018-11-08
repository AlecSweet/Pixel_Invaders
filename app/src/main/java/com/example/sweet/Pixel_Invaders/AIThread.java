package com.example.sweet.Pixel_Invaders;

import com.example.sweet.Pixel_Invaders.Util.CollisionHandler;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Enemy;
import com.example.sweet.Pixel_Invaders.Game_Objects.Player;
import com.example.sweet.Pixel_Invaders.Engine_Events.TimeSlowEngine;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

import static com.example.sweet.Pixel_Invaders.Engine_Events.TimeSlowEngine.SlowPattern.POW2_FADEIN_FADEOUT;
import static com.example.sweet.Pixel_Invaders.Engine_Events.TimeSlowEngine.SlowPattern.POW3_FADEOUT;

public class AIThread implements Runnable
{
    private Player player1;

    private TimeSlowEngine timeEngine;

    final Object lock = new Object();

    private boolean saveTime = false;

    private float oldTime = 1;

    volatile long currentFrame = 0;
    volatile long frameRequest = 0;

    volatile float averageFrameTime = 0;

    private volatile GlobalInfo globalInfo;

    public volatile Enemy[] entities;

    volatile boolean
            running = true,
            block = false,
            inBlock = false;

    private boolean levelDone = false;

    private volatile boolean levelClearedConsensus = false;


    AIThread(GlobalInfo gI)
    {
        entities = new Enemy[Constants.ENTITIES_LENGTH];
        globalInfo = gI;
        timeEngine = new TimeSlowEngine(globalInfo, 30);
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
                    globalInfo.setTimeSlow(oldTime);
                    saveTime = true;
                }

                if (currentFrame < frameRequest)
                {
                    currentFrame++;
                    long startTime = System.currentTimeMillis();
                    update();
                    averageFrameTime = System.currentTimeMillis() - startTime;
                }
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
    
    private void update()
    {
        timeEngine.update();
        player1.update();

        float amplitude = player1.rift.checkState(player1, globalInfo);
        if(amplitude > 0)
        {
            timeEngine.addSlow(amplitude, player1.rift.getSlowDuration(), POW2_FADEIN_FADEOUT);
        }

        enemyActions();
    }

    private void enemyActions()
    {
        levelDone = true;
        boolean clearedTemp = true;
        int pxKilledThisFrame = 0;
        for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            if(entities[i] != null && !entities[i].aiRemoveConsensus)
            {
                if(entities[i].spawned)
                {
                    if (entities[i].live)
                    {
                        if (entities[i].checkOverlap)
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

                        entities[i].move(
                                player1.getPixelGroup().getCenterX(),
                                player1.getPixelGroup().getCenterY()
                        );

                        pxKilledThisFrame += entities[i].pixelsKilledSinceLastCheck();
             /*           if(pixKilled > 0)
                        {
                            System.out.println(pixKilled);
                            player1.shakeEngine.addShake(
                                    (float) (.002f * Math.sqrt(pixKilled)),
                                    120,
                                    500
                            );
                        }*/
                        /*if (entities[i].getPixelGroup().readyToScreenShake && entities[i].getPixelGroup().pixelsKilled > 0)
                        {
                            player1.shakeEngine.addShake(
                                    (float) (.004f * Math.sqrt(entities[i].getPixelGroup().pixelsKilled)),
                                    120,
                                    500
                            );
                            entities[i].getPixelGroup().pixelsKilled = 0;
                            entities[i].getPixelGroup().readyToScreenShake = false;
                        }
                        else
                        {
                            entities[i].getPixelGroup().readyToScreenShake = false;
                        }*/
                        for (int d = 0; d < entities[i].consumables.length; d++)
                        {
                            if (entities[i].consumables[d] != null && entities[i].isAsteriod)
                            {
                                player1.addDrop(entities[i].consumables[d]);
                                entities[i].consumables[d] = null;
                            }
                        }
                    }
                    else
                    {
                        pxKilledThisFrame += entities[i].getPixelGroup().totalPixels;
                        /*if(entities[i].pixelsKilledSinceLastCheck() > 0)
                        {
                            player1.shakeEngine.addShake(
                                    (float) (.002f * Math.sqrt(entities[i].getPixelGroup().totalPixels)),
                                    120,
                                    1000
                            );
                        }*/
                        if (globalInfo.gameSettings.slowOnKill)
                        {
                            timeEngine.addSlow(.7f, 400, POW3_FADEOUT);
                        }
                        entities[i].aiRemoveConsensus = true;
                    }
                }
                clearedTemp = false;
            }
            else
            {
                entities[i] = null;
            }
        }

        if(pxKilledThisFrame > 0)
        {
            float temp = (float)Math.sqrt(pxKilledThisFrame);
            player1.shakeEngine.addShake(
                    (.002f * temp),
                    120,
                    300 + (int)(7 * temp)
            );
        }

        if(clearedTemp)
        {
            levelClearedConsensus = true;
        }
        publishLocations();
    }

    private void publishLocations()
    {
        for(Enemy e: entities)
        {
            if(e != null && !e.aiRemoveConsensus) //&& e.aiRecognized)
            {
                e.publishLocation(currentFrame);
            }
        }

        player1.publishLocation(currentFrame);
    }

    void setInfo(Player p)
    {
        player1 = p;
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
                    System.out.println("InterruptedException");
                }
            }
            block = false;
            inBlock = false;
        }
    }

    public boolean getClearedConsensus()
    {
        return levelClearedConsensus;
    }
}
