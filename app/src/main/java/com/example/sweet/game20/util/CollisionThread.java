package com.example.sweet.game20.util;

import com.example.sweet.game20.Objects.Bullet;
import com.example.sweet.game20.Objects.Drop;
import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.GunComponent;
import com.example.sweet.game20.Objects.Player;

import java.util.ArrayList;
import java.util.Iterator;

public class CollisionThread implements Runnable
{
    public Player player1;
    public CollisionHandler collisionHandler;
    public Enemy[] entities;

    public volatile boolean
            running = true,
            pause = false;

    private final long MILLIS_PER_SECOND = 1000;
    private final long UPS = 120;

    private final long mSPU = MILLIS_PER_SECOND / UPS;

    private double
            globalStartTime,
            pastTime,
            lag = 0.0;


    private boolean saveTime = false;

    private double pauseTime = 0;

    public CollisionThread(Enemy[] e)
    {
        entities = e;
        globalStartTime = System.currentTimeMillis();
    }

    public void run()
    {
        while(running)
        {
            if(!pause)
            {
                if(!saveTime)
                {
                    pastTime = System.currentTimeMillis() - globalStartTime;
                    saveTime = true;
                }

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
            }
            else
            {
                if(saveTime)
                {
                    saveTime = false;
                }
            }
        }
    }

    public void update()
    {
        checkCollisions();
        player1.consumableCollisionCheck();
    }

    public void checkCollisions()
    {
        for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            if(entities[i] != null && !entities[i].collisionRemoveConsensus)
            {
                if (entities[i].getPixelGroup().getCollidableLive() && entities[i].onScreen)
                {
                    // Player -> Entity
                    collisionHandler.checkCollisions(player1.getPixelGroup(), entities[i].getPixelGroup());

                    // Player Gun's Bullets -> Entity
                    for (Drop d : player1.getGuns())
                    {
                        if (d != null && d.component != null)
                        {
                            for (Bullet b : ((GunComponent)d.component).gun.getBullets())
                            {
                                if (b.live)
                                {
                                    collisionHandler.checkCollisions(b.pixelGroup, entities[i].getPixelGroup());
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
                                        collisionHandler.checkCollisions(b.pixelGroup, player1.getPixelGroup());
                                    }
                                }
                            }
                        }
                    }
                }
                else if (!entities[i].getPixelGroup().getCollidableLive())
                {
                    entities[i].collisionRemoveConsensus = true;
                }
            }
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
}
