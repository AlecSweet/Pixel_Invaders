package com.example.sweet.game20.Objects;

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

    protected float
            spread,
            speed;

    protected float
                x,
                y;

    protected int shaderLocation;

    protected ParticleSystem masterParticleSystem;

    public Gun(float sD, PixelGroup pG, ParticleSystem ps, float spd)
    {
        pixelGroupTemplate = pG;
        bulletPool = new Stack<>();
        shootDelay = sD;
        //textureID = tID;
        //shaderLocation = sL;
        speed = spd;
        int num = (int)Math.ceil((3.4/(speed * 60)) * (1000/sD))+1;
        bullets = new Bullet[num];
        for(int i = 0; i < num; i++)
        {
            bullets[i] = new Bullet(0, 0, 0,
                    speed, 3.4f, shaderLocation, pixelGroupTemplate.clone());
            bulletPool.push(bullets[i]);
        }
        lastShotTime = System.currentTimeMillis();
        masterParticleSystem = ps;
    }

    public void draw(double interpolation)
    {
        for(Bullet b: bullets)
        {
            if (b.live)
            {
                b.draw();
            }
        }
    }

    public void move()
    {
        for(Bullet b: bullets)
        {
            b.move();
        }
    }

    public void applyPauseLength(double p)
    {
        lastShotTime += p;
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
