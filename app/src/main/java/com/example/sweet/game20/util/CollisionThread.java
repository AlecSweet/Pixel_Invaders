package com.example.sweet.game20.util;

import com.example.sweet.game20.Objects.Bullet;
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
        player1.type0DropCollisionCheck();
    }

    public void checkCollisions()
    {
        /*int eS = entities.size();
        for(int i = 0; i < eS; i++)
        {

            if (entities.get(i).getPixelGroup().getCollidableLive() && entities.get(i).onScreen)
            {
                // Player -> Entity
                collisionHandler.checkCollisions(player1.getPixelGroup(), entities.get(i).getPixelGroup());

                // Player Gun's Bullets -> Entity
                int gS = player1.getGuns().length;
                for(int gI = 0; gI < gS; gI++)
                {
                    if (player1.getGuns()[gI] != null)
                    {
                        int bS = player1.getGuns()[gI].gun.getBullets().length;
                        for(int bI = 0; bI < bS; bI++)
                        {
                            if(player1.getGuns()[gI].gun.getBullets()[bI].active && player1.getGuns()[gI].gun.getBullets()[bI].live)
                                collisionHandler.checkCollisions(player1.getGuns()[gI].gun.getBullets()[bI].pixelGroup, entities.get(i).getPixelGroup());
                        }
                    }
                }

                // Entity Gun's Bullets -> Player
                if(entities.get(i).getHasGun())
                {
                    int g2S = entities.get(i).getGunComponents().length;
                    for (int gI = 0; gI < g2S; gI++)
                    {
                        if (entities.get(i).getGunComponents()[gI] != null)
                        {
                            int bS = entities.get(i).getGunComponents()[gI].gun.getBullets().length;
                            for (int bI = 0; bI < bS; bI++)
                            {
                                if (entities.get(i).getGunComponents()[gI].gun.getBullets()[bI].active && entities.get(i).getGunComponents()[gI].gun.getBullets()[bI].live)
                                    collisionHandler.checkCollisions(entities.get(i).getGunComponents()[gI].gun.getBullets()[bI].pixelGroup, player1.getPixelGroup());
                            }
                        }
                    }
                }
            }

            // Entity -> Other Entity
            int eSize = entities.size();
            for(int i2 = 0; i2 < eSize; i2++)
            {
                if (entities.get(i) != entities.get(i2))
                {
                    collisionHandler.preventOverlap(entities.get(i).getPixelGroup(), entities.get(i2).getPixelGroup());
                }
            }
        }*/
        //Iterator<Enemy> itr = entities.iterator();
        //while(itr.hasNext())
        //for(Enemy e: entities)
        for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            if(entities[i] != null && !entities[i].collisionRemoveConsensus)
            {
                if (entities[i].getPixelGroup().getCollidableLive() && entities[i].onScreen)
                {
                    // Player -> Entity
                    collisionHandler.checkCollisions(player1.getPixelGroup(), entities[i].getPixelGroup());

                    // Player Gun's Bullets -> Entity
                    for (GunComponent gc : player1.getGuns())
                    {
                        if (gc != null)
                        {
                            for (Bullet b : gc.gun.getBullets())
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
            /*for(Enemy e2: entities)
            {
                if (e != e2)
                {
                    collisionHandler.preventOverlap(e.getPixelGroup(), e2.getPixelGroup());
                }
            }*/
        }
    }

    /*public void enemyActions()
    {
        int eS = entities.size();
        for(int i = 0; i < eS; i++)
        {

            if (entities.get(i).getPixelGroup().getCollidableLive() && entities.get(i).onScreen)
            {
                // Player -> Entity
                collisionHandler.checkCollisions(player1.getPixelGroup(), entities.get(i).getPixelGroup());

                // Player Gun's Bullets -> Entity
                int gS = player1.getGuns().length;
                for(int gI = 0; gI < gS; gI++)
                {
                    if (player1.getGuns()[gI] != null)
                    {
                        int bS = player1.getGuns()[gI].gun.getBullets().length;
                        for(int bI = 0; bI < bS; bI++)
                        {
                            if(player1.getGuns()[gI].gun.getBullets()[bI].active)
                                collisionHandler.checkCollisions(player1.getGuns()[gI].gun.getBullets()[bI].pixelGroup, entities.get(i).getPixelGroup());
                        }
                    }
                }

                // Entity Gun's Bullets -> Player
                if(entities.get(i).getHasGun())
                {
                    int g2S = entities.get(i).getGunComponents().length;
                    for (int gI = 0; gI < g2S; gI++)
                    {
                        if (entities.get(i).getGunComponents()[gI] != null)
                        {
                            int bS = entities.get(i).getGunComponents()[gI].gun.getBullets().length;
                            for (int bI = 0; bI < bS; bI++)
                            {
                                if (entities.get(i).getGunComponents()[gI].gun.getBullets()[bI].active)
                                    collisionHandler.checkCollisions(entities.get(i).getGunComponents()[gI].gun.getBullets()[bI].pixelGroup, player1.getPixelGroup());
                            }
                        }
                    }
                }
            }

            // Entity -> Other Entity
            int eSize = entities.size();
            for(int i2 = 0; i2 < eSize; i2++)
            {
                if (entities.get(i) != entities.get(i2))
                {
                    collisionHandler.preventOverlap(entities.get(i).getPixelGroup(), entities.get(i2).getPixelGroup());
                }
            }
        }
    }*/

    /*public synchronized void setEntities(ArrayList<Enemy> e)
    {
        entities = e;
        checkCollision = true;
    }

    public synchronized void addEntity(Enemy e)
    {
        entities.add(e);
    }
    */
    public synchronized void setPlayer(Player p)
    {
        player1 = p;
    }

    public synchronized void setCollisionHandler(CollisionHandler c)
    {
        collisionHandler = c;
    }
}
