package com.example.sweet.game20.Objects;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Sweet on 3/25/2018.
 */

public abstract class Gun
{
    protected Stack<Bullet> bulletPool;

    protected Bullet[] bullets;

    protected PixelGroup pixelGroupTemplate;

    protected double
            shootDelay,
            lastShotTime;

    protected double globalStartTime;

    protected float
            spread,
            speed;

    protected int
            textureID,
            shaderLocation;

    protected ParticleSystem masterParticleSystem;

    public Gun(float sD, double gst, int tID, int sL, int pSL, PixelGroup pG, ParticleSystem ps, float spd)
    {
        pixelGroupTemplate = pG;
        bulletPool = new Stack<>();
        shootDelay = sD;
        globalStartTime = gst;
        textureID = tID;
        shaderLocation = sL;
        speed = spd;
        int num = (int)Math.ceil((3.4/(speed * 60)) * (1000/sD))+1;
        bullets = new Bullet[num];
        for(int i = 0; i < num; i++)
        {
            bullets[i] = new Bullet(0, 0, 0,
                    speed, 3.4f, shaderLocation, pixelGroupTemplate.clone());
            bulletPool.push(bullets[i]);
        }
        lastShotTime = System.currentTimeMillis()-globalStartTime;
        masterParticleSystem = ps;
    }

    public void draw(double interpolation)
    {
        /*for (Iterator<Bullet> i = bullets.iterator(); i.hasNext(); )
        {
            Bullet t = i.next();
            if (t.live)
                t.draw();
            else {
                t.freeResources();
                i.remove();
            }
        }*/
        int len = bullets.length;
        for(int i = 0; i < len; i++)
        {
            if (bullets[i].live && bullets[i].active)
                bullets[i].draw();
        }
    }

    public void move()
    {
        for(Bullet b: bullets)
            b.move();
    }

    public boolean shoot(float x, float y, float angle)
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
}
