package com.example.sweet.game20.util;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.Objects.Bullet;
import com.example.sweet.game20.Objects.Drop;
import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.GunComponent;
import com.example.sweet.game20.Objects.Player;

public class CollisionThread implements Runnable
{
    public Player player1;
    public CollisionHandler collisionHandler;
    public volatile Enemy[] entities;
    public volatile Enemy[] target;
    public volatile float averageFrameTime = 0;
    public volatile boolean
            running = true,
            //pause = false,
            block = false,
            inBlock = false;

    private final long MILLIS_PER_SECOND = 1000;
    private final long UPS = 120;

    private final long mSPU = MILLIS_PER_SECOND / UPS;

    private double globalStartTime;
            //pastTime,
            //lag = 0.0;

    public volatile long currentFrame = 0;
    public volatile long frameRequest = 0;
    public long lowestFrame;

   // private boolean saveTime = false;

    //private double pauseTime = 0;

    private volatile GlobalInfo globalInfo;

    public final Object lock = new Object();

    public CollisionThread(Enemy[] e, GlobalInfo globalInfo)
    {
        entities = e;
        this.globalInfo = globalInfo;
        globalStartTime = System.currentTimeMillis();
    }

    public void run()
    {
        while(running)
        {
            checkBlock();
            if (!globalInfo.getPause())
            {
                /*if (!saveTime)
                {
                    pastTime = System.currentTimeMillis() - globalStartTime;
                    saveTime = true;
                }*/

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
                if (lowestFrame < frameRequest)
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
            }
            /*else
            {
                if (saveTime)
                {
                    saveTime = false;
                }
            }*/
        }
    }

    public void update()
    {
        /*if(target != null){
            entities = target;
            target = null;
        }*/
        checkCollisions();
    }

    /*
    *   Handles all collision checking between game objects.
    */
    public void checkCollisions()
    {
        int totalKilled = 0;
        for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            if(entities[i] != null && !entities[i].collisionRemoveConsensus)
            {
                if(entities[i].getPixelGroup().getTotalPixels() * entities[i].getPixelGroup().getLivablePercentage()  >
                        entities[i].getPixelGroup().numLivePixels)
                {
                    entities[i].getPixelGroup().setCollidableLive(false);
                }

                if (entities[i].getPixelGroup().getCollidableLive())
                {
                    // Player -> Entity
                    int numKilled = 0;

                    if(entities[i].inRange)
                    {
                        numKilled += collisionHandler.checkCollisions(player1.getPixelGroup(), entities[i].getPixelGroup());
                    }

                    // Player Gun's Bullets -> Entity
                    for (Drop d : player1.getGuns())
                    {
                        if (d != null && d.component != null)
                        {
                            for (Bullet b : ((GunComponent)d.component).gun.getBullets())
                            {
                                if (b.live)
                                {
                                    numKilled += collisionHandler.checkCollisions(b.pixelGroup, entities[i].getPixelGroup());
                                    //collisionHandler.checkCollisions(entities[i].getPixelGroup(), b.pixelGroup);
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
                                    if (b.live)
                                    {
                                        numKilled += collisionHandler.checkCollisions(b.pixelGroup, player1.getPixelGroup());
                                    }
                                }
                            }
                        }
                    }

                    if(numKilled > 0)
                    {
                        totalKilled += numKilled;
                        entities[i].collisionOccured();
                    }
                }
                else if (!entities[i].getPixelGroup().getCollidableLive() || entities[i].aiRemoveConsensus)
                {
                    if(!entities[i].getPixelGroup().getCollidableLive())
                    {
                        collisionHandler.destroyCollidableAnimation(entities[i].getPixelGroup());
                    }
                    entities[i].collisionRemoveConsensus = true;
                }
            }
            /*else if(entities[i] != null && entities[i].uiRemoveConsensus)
            {
                entities[i] = null;
            }*/
        }

        player1.collisionOccured(totalKilled);
        player1.consumableCollisionCheck();

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
                    if (b.live)
                    {
                        checkLowestHelper(b.pixelGroup.consumeCollisionLocation(frameRequest));
                        b.live = b.pixelGroup.getCollidableLive();
                        /*b.pixelGroup.readyToKnockback = true;
                        b.pixelGroup.readyToScreenShake = true;*/
                    }
                }
            }
        }

        for(Enemy e: entities)
        {
            if(e != null && !e.collisionRemoveConsensus)
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
                                if (b.live)
                                {
                                    checkLowestHelper(b.pixelGroup.consumeCollisionLocation(frameRequest));
                                    b.live = b.pixelGroup.getCollidableLive();
                                    /*b.pixelGroup.readyToKnockback = true;
                                    b.pixelGroup.readyToScreenShake = true;*/
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
    public synchronized void setPlayer(Player p)
    {
        player1 = p;
    }

    public synchronized void setCollisionHandler(CollisionHandler c)
    {
        collisionHandler = c;
    }

    public void setInfo(Player p, Enemy[] e)
    {
        player1 = p;
        entities = e;
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
