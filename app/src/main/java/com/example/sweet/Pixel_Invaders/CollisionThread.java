package com.example.sweet.Pixel_Invaders;

import android.content.Context;

import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Drop;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Enemy;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Bullet;
import com.example.sweet.Pixel_Invaders.Game_Objects.Player;
import com.example.sweet.Pixel_Invaders.Util.CollisionHandler;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.GunComponent;

public class CollisionThread implements Runnable
{
    private Player player1;
    private CollisionHandler collisionHandler;
    public volatile Enemy[] entities;
    volatile float averageFrameTime = 0;
    volatile boolean
            running = true,
            block = false,
            inBlock = false;

    volatile long currentFrame = 0;
    volatile long frameRequest = 0;
    long lowestFrame;

    private volatile boolean levelClearedConsensus = false;

    private volatile GlobalInfo globalInfo;

    final Object lock = new Object();

    CollisionThread(GlobalInfo globalInfo)
    {
        entities = new Enemy[Constants.ENTITIES_LENGTH];
        this.globalInfo = globalInfo;
    }

    public void run()
    {
        while(running)
        {
            checkBlock();
            if (!globalInfo.getPause())
            {
                if (lowestFrame < frameRequest)
                {
                    currentFrame++;
                    long startTime = System.currentTimeMillis();
                    update();
                    averageFrameTime = System.currentTimeMillis() - startTime;
                }
            }
        }
    }

    private void update()
    {
        checkCollisions();
    }

    /*
    *   Handles all collision checking between game objects.
    */
    private void checkCollisions()
    {
        int totalKilled = 0;
        boolean  clearedTemp = true;
        for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            if(entities[i] != null && !entities[i].collisionRemoveConsensus)
            {
                if(!entities[i].collisionRecognized)
                {
                    entities[i].collisionRecognized = true;
                }
                if(entities[i].collisionRecognized)
                {
                    if (entities[i].getPixelGroup().getTotalPixels() * entities[i].getPixelGroup().getLivablePercentage() >
                            entities[i].getPixelGroup().numLivePixels)
                    {
                        entities[i].getPixelGroup().setCollidableLive(false);
                    }

                    if (entities[i].getPixelGroup().getCollidableLive())
                    {
                        // Player -> Entity
                        int numKilled = 0;

                        if (entities[i].inRange)
                        {
                            numKilled += collisionHandler.checkCollisions(player1.getPixelGroup(), entities[i].getPixelGroup());
                        }

                        // Player Gun's Bullets -> Entity
                        for (Drop d : player1.getGuns())
                        {
                            if (d != null && d.component != null)
                            {
                                for (Bullet b : ((GunComponent) d.component).gun.getBullets())
                                {
                                    if (b.getLive())
                                    {
                                        //int t = numKilled;
                                        numKilled += collisionHandler.checkCollisions(b.pixelGroup, entities[i].getPixelGroup());
                                        /*if (numKilled > t)
                                        {
                                            System.out.println(
                                                    " Size: " + entities[i].getPixelGroup().totalPixels +
                                                            " Spawned: " + entities[i].spawned +
                                                            " live: " + entities[i].getPixelGroup().getCollidableLive() +
                                                            " X: " + entities[i].getPixelGroup().getCenterX() +
                                                            " Y: " + entities[i].getPixelGroup().getCenterY() +
                                                            " Asteroid: " + entities[i].isAsteriod +
                                                            " Spawned: " + entities[i].spawned
                                            );
                                            System.out.println(
                                                    " X: " + b.pixelGroup.getCenterX() +
                                                            " Y: " + b.pixelGroup.getCenterY()
                                            );
                                        }*/
                                    }
                                }
                            }
                        }

                        // Entity Gun's Bullets -> Player
                        if (entities[i].getHasGun())
                        {
                            for (GunComponent gc : entities[i].getGunComponents())
                            {
                                if (gc != null)
                                {
                                    for (Bullet b : gc.gun.getBullets())
                                    {
                                        if (b.getLive())
                                        {
                                            numKilled += collisionHandler.checkCollisions(b.pixelGroup, player1.getPixelGroup());
                                        }
                                    }
                                }
                            }
                        }

                        if (numKilled > 0)
                        {
                            totalKilled += numKilled;
                            entities[i].collisionOccured();
                        }
                    }
                    else if (!entities[i].getPixelGroup().getCollidableLive() || entities[i].uiRemoveConsensus)
                    {
                        if (!entities[i].getPixelGroup().getCollidableLive())
                        {
                            collisionHandler.destroyCollidableAnimation(entities[i].getPixelGroup());
                        }
                        entities[i].collisionRemoveConsensus = true;
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
        player1.collisionOccured(totalKilled);
        //player1.consumableCollisionCheck();

        consumeCollisionLocations();
    }

    /*
    *   Conusume current collision location from all game objects with location chaining enabled.
    *   To be done after the current collision locations have been checked for the frame.
    */
    private void consumeCollisionLocations()
    {
        checkLowestHelper(player1.getPixelGroup().consumeCollisionLocation(frameRequest));

        for (Drop d : player1.getGuns())
        {
            if (d != null && d.component != null)
            {
                for (Bullet b : ((GunComponent)d.component).gun.getBullets())
                {
                    if (b.getLive())
                    {
                        checkLowestHelper(b.pixelGroup.consumeCollisionLocation(frameRequest));
                        b.setLive(b.pixelGroup.getCollidableLive());
                    }
                }
            }
        }

        for(Enemy e: entities)
        {
            if(e != null && !e.collisionRemoveConsensus && e.collisionRecognized)
            {
                checkLowestHelper(e.getPixelGroup().consumeCollisionLocation(frameRequest));

                if (e.getHasGun())
                {
                    for (GunComponent gc : e.getGunComponents())
                    {
                        if (gc != null)
                        {
                            for (Bullet b : gc.gun.getBullets())
                            {
                                if (b.getLive())
                                {
                                    checkLowestHelper(b.pixelGroup.consumeCollisionLocation(frameRequest));
                                    b.setLive(b.pixelGroup.getCollidableLive());
                                }
                            }
                        }
                    }
                }
                e.getPixelGroup().readyToKnockback = true;
                e.getPixelGroup().readyToScreenShake = true;
            }
        }
    }

    private void checkLowestHelper(long frame)
    {
        if(frame < lowestFrame)
        {
            lowestFrame = frame;
        }
    }

    synchronized void setCollisionHandler(CollisionHandler c)
    {
        collisionHandler = c;
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
