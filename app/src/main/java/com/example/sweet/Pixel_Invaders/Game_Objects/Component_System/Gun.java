package com.example.sweet.Pixel_Invaders.Game_Objects.Component_System;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.Game_Objects.Pooling.ObjectNode;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

/**
 * Created by Sweet on 3/25/2018.
 */

public abstract class Gun
{
    Bullet[] bullets;

    PixelGroup pixelGroupTemplate;

    public float shakeMod;

    float shootDelay, lastShotTime;

    float
            spread,
            speed,
            x,
            y,
            shockSize;

    ObjectNode[] indexNodes;
    ObjectNode
            openIndexTail,
            openIndexHead;

    int numShots = 1;

    public int totalBullets;

    float fireRateMod = 1;

    ParticleSystem masterParticleSystem;

    public Gun(float sD, PixelGroup pG, ParticleSystem ps, float spd)
    {
        pixelGroupTemplate = pG;
        pixelGroupTemplate.restorable = true;
        shakeMod = (float)Math.sqrt(pixelGroupTemplate.totalPixels) * .001f + .002f;
        shootDelay = sD;
        speed = spd;
        int num = ((int)Math.ceil((4/(speed * 60)) * (1000/shootDelay))+1)*numShots + numShots;
        totalBullets = num;
        bullets = new Bullet[num];
        indexNodes = new ObjectNode[num];

        for(int i = 0; i < num; i++)
        {
            bullets[i] = new Bullet(0, 0, 0,
                    speed, 4f, pixelGroupTemplate.clone());

            indexNodes[i] = new ObjectNode(i, null);
            if(i >= 1)
            {
                openIndexHead.nextObject = indexNodes[i];
                openIndexHead = openIndexHead.nextObject;
            }
            else
            {
                openIndexTail = indexNodes[i];
                openIndexHead = openIndexTail;
            }
        }

        lastShotTime = 0;
        masterParticleSystem = ps;
        shockSize = (float)Math.sqrt(pixelGroupTemplate.totalPixels) * speed * 10;

    }
    
    public Gun(float sD, PixelGroup pG, ParticleSystem ps, float spd, int num)
    {
        pixelGroupTemplate = pG;
        pixelGroupTemplate.restorable = true;
        shakeMod = (float)Math.sqrt(pixelGroupTemplate.totalPixels) * .001f + .002f;
        shootDelay = sD;
        speed = spd;
        bullets = new Bullet[num];
        indexNodes = new ObjectNode[num];
        totalBullets = num;

        for(int i = 0; i < num; i++)
        {
            bullets[i] = new Bullet(0, 0, 0,
                    speed, 4f, pixelGroupTemplate.clone());
            //bullets[i].live = false;
            //bulletPool.add(bullets[i]);

            indexNodes[i] = new ObjectNode(i, null);
            if(i >= 1)
            {
                openIndexHead.nextObject = indexNodes[i];
                openIndexHead = openIndexHead.nextObject;
            }
            else
            {
                openIndexTail = indexNodes[i];
                openIndexHead = openIndexTail;
            }
        }

        lastShotTime = 0;
        masterParticleSystem = ps;
        shockSize = (float)Math.sqrt(pixelGroupTemplate.totalPixels) * speed * 10;
    }
    
    public void draw(double interpolation)
    {
        for(Bullet b: bullets)
        {
            if(b.live && b.onScreen)
            {
                b.draw();
            }
        }
    }

    public void move(GlobalInfo gI)
    {
        for(Bullet b: bullets)
        {
            b.move(gI);
        }
    }

    public void move(GlobalInfo gI, float slow)
    {
        for(Bullet b: bullets)
        {
            b.move(gI, slow);
        }
    }

    void updateBulletPool()
    {
        int num = ((int)Math.ceil((4/(speed * 60)) * (1000 / (shootDelay * fireRateMod)))+1)*numShots + numShots;
        if(num > bullets.length)
        {
            Bullet[] newBullets = new Bullet[num];
            ObjectNode[] newNodes = new ObjectNode[num];

            for (int i = 0; i < num; i++)
            {
                if(i < bullets.length)
                {
                    newBullets[i] = bullets[i];
                    newNodes[i] = indexNodes[i];
                }
                else
                {
                    newBullets[i] = new Bullet(0, 0, 0,
                            speed, 4f, pixelGroupTemplate.clone());
                    newNodes[i] = new ObjectNode(i, null);
                    openIndexHead.nextObject = newNodes[i];
                    openIndexHead = openIndexHead.nextObject;
                }
            }
            bullets = newBullets;
            indexNodes = newNodes;
        }
    }

    public float getPercentTimeRemaining(GlobalInfo gI)
    {
        return ((gI.getAugmentedTimeMillis() - lastShotTime) / (shootDelay * fireRateMod));
    }

    boolean canShoot(GlobalInfo gI)
    {
        return getPercentTimeRemaining(gI) > .94f;
    }

    public void publishLocation(long frame)
    {
       for(Bullet b: bullets)
       {
           b.pixelGroup.publishLocation(frame);
       }
    }

    /*public boolean shoot(float x, float y, float angle, long f, float slow)
    {
        return false;
    }*/

    public boolean shoot(float x, float y, float angle, GlobalInfo gI, float cosA, float sinA)
    {
        return false;
    }

    public boolean shoot(float x, float y, float angle, GlobalInfo gI)
    {
        return false;
    }

    public Bullet[] getBullets()
    {
        return bullets;
    }

    @Override
    public Gun clone(){
        return null;
    }

    public PixelGroup getTemplate()
    {
        return pixelGroupTemplate;
    }

    public double getShotDelay()
    {
        return shootDelay;
    }

    void freeMemory()
    {
        //pixelGroupTemplate.freeMemory();
    }

    void addMaxHealth(int h)
    {
        getTemplate().addMaxHealth(h);
        for(Bullet b: bullets)
        {
            b.pixelGroup.addMaxHealth(h);
        }
    }

    void reduceMaxHealth(int h)
    {
        getTemplate().reduceMaxHealth(h);
        for(Bullet b: bullets)
        {
            b.pixelGroup.reduceMaxHealth(h);
        }
    }

    public void setSpeed(float spd)
    {
        speed = spd;
        for(Bullet b: bullets)
        {
            b.setSpeed(spd);
        }
    }

    public void incSpeed(float mod)
    {
        speed *= mod;
        for(Bullet b: bullets)
        {
            b.setSpeed(speed);
        }
    }

    public void reduceDelay(float mod)
    {
        shootDelay *= mod;
        updateBulletPool();
    }

    public void reduceSpread(float mod)
    {
        spread *= mod;
    }
}
