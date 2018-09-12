package com.example.sweet.Pixel_Invaders;

import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Drop;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Asteroid;
import com.example.sweet.Pixel_Invaders.Util.CollisionHandler;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Enemy;
import com.example.sweet.Pixel_Invaders.Game_Objects.Player;
import com.example.sweet.Pixel_Invaders.Engine_Events.TimeSlowEngine;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

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
        timeEngine.runEngine();
        //player1.shoot(currentFrame, globalInfo);
        player1.handleDrops();
        player1.shoot();
        //player1.consumableCollisionCheck();
        //player1.moveConsumables();
        //player1.moveCamera();
        player1.handleScreenShake(globalInfo.gameSettings.screenShakePercent);
        player1.checkRegen();
        //globalInfo.publishScreenShift(player1.xScreenShift - player1.screenShakeX, player1.yScreenShift - player1.screenShakeY);
        globalInfo.screenShiftX = player1.xScreenShift - player1.screenShakeX;
        globalInfo.screenShiftY = player1.yScreenShift - player1.screenShakeY;
        //player1.checkDropsOnScreen();

        enemyActions();
    }

    private void enemyActions()
    {
        levelDone = true;
        boolean clearedTemp = true;
        for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            if(entities[i] != null && !entities[i].aiRemoveConsensus)
            {
                if(!entities[i].aiRecognized)
                {
                    entities[i].aiRecognized = true;
                }
                if(entities[i].aiRecognized)
                {
                    if (entities[i].getPixelGroup().getCollidableLive())
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

                        if (entities[i].getPixelGroup().getCollidableLive())
                        {
                            float difX = Math.abs(entities[i].getX() - player1.xScreenShift) * globalInfo.getScaleX();
                            float difY = Math.abs(entities[i].getY() - player1.yScreenShift) * globalInfo.getScaleY();
                            if (difX <= (1 + entities[i].getPixelGroup().getHalfSquareLength()) &&
                                    difY <= (1 + entities[i].getPixelGroup().getHalfSquareLength()))
                            {
                                entities[i].onScreen = true;
                                if (difX <= 1 && difY <= 1)
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

                        if (entities[i].getPixelGroup().readyToScreenShake && entities[i].getPixelGroup().pixelsKilled > 0)
                        {
                            player1.shakeEngine.addShake(
                                    (float) (.002f * Math.sqrt(entities[i].getPixelGroup().pixelsKilled)),
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
                        player1.shakeEngine.addShake(
                                (float) (.002f * Math.sqrt(entities[i].getPixelGroup().totalPixels)),
                                120,
                                1000
                        );
                        if (globalInfo.gameSettings.slowOnKill)
                        {
                            timeEngine.addSlow(.7f, 400);
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
            if(e != null && !e.aiRemoveConsensus && e.aiRecognized)
            {
                e.publishLocation(currentFrame);
            }
        }

        player1.publishLocation(currentFrame);
    }

    void setInfo(Player p, GlobalInfo gI)
    {
        player1 = p;
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
