package com.example.sweet.game20.util;

import com.example.sweet.game20.Objects.Bullet;
import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.GunComponent;
import com.example.sweet.game20.Objects.Player;

import java.util.ArrayList;

public class CollisionThread implements Runnable
{
    public Player player1;
    public ArrayList<Enemy> entities;
    public CollisionHandler collisionHandler;


    public volatile boolean running = true;
    public volatile boolean checkCollision = true;

    private final long MILLIS_PER_SECOND = 1000;
    private final long UPS = 60;

    private final long mSPU = MILLIS_PER_SECOND / UPS;

    private double
            globalStartTime,
            pastTime,
            lag = 0.0;

    public CollisionThread()
    {
        //globalStartTime = System.currentTimeMillis();
        entities = new ArrayList<>();
        //player1 = p;
        //entities = e;
        //collisionHandler = c;
    }

    public void run()
    {
        while(running)
        {
            /*double currentTime = System.currentTimeMillis()-globalStartTime;
            double elapsedTime = currentTime - pastTime;
            pastTime = currentTime;
            lag += elapsedTime;

            while( lag >= mSPU)
            {
                //if(!pause)
                update();
                lag -= mSPU;
            }*/
            if(checkCollision)
            {
                update();
                checkCollision = false;
            }
        }
    }

    public void update()
    {
        checkCollisions();
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
        for(Enemy e: entities)
        {
            if (e.getPixelGroup().getCollidableLive() && e.onScreen)
            {
                // Player -> Entity
                collisionHandler.checkCollisions(player1.getPixelGroup(), e.getPixelGroup());

                // Player Gun's Bullets -> Entity
                for(GunComponent gc: player1.getGuns())
                {
                    if (gc != null)
                    {
                        for(Bullet b: gc.gun.getBullets())
                        {
                            if(b.active && b.live)
                                collisionHandler.checkCollisions(b.pixelGroup, e.getPixelGroup());
                        }
                    }
                }

                // Entity Gun's Bullets -> Player
                if(e.getHasGun())
                {
                    for(GunComponent gc: e.getGunComponents())
                    {
                        if (gc != null)
                        {
                            for(Bullet b: gc.gun.getBullets())
                            {
                                if (b.active && b.live)
                                    collisionHandler.checkCollisions(b.pixelGroup, player1.getPixelGroup());
                            }
                        }
                    }
                }
            }

            for(Enemy e2: entities)
            {
                if (e != e2)
                {
                    collisionHandler.preventOverlap(e.getPixelGroup(), e2.getPixelGroup());
                }
            }
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

    public synchronized void setEntities(ArrayList<Enemy> e)
    {
        entities = e;
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
