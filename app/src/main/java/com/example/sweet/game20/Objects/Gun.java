package com.example.sweet.game20.Objects;

import com.example.sweet.game20.util.Constants;

import java.util.Stack;

/**
 * Created by Sweet on 3/25/2018.
 */

public abstract class Gun
{
    protected Stack<Bullet> bulletPool;

    protected Bullet[] bullets;

    protected PixelGroup pixelGroupTemplate;

    public float shakeMod;

    protected double
            shootDelay,
            lastShotTime;

    protected int shotFrameDelay;
    protected long lastShotFrame;

    protected float
            spread,
            speed;

    protected float
                x,
                y;

    protected int numShots = 1;

    protected float fireRateMod = 1;

    protected ParticleSystem masterParticleSystem;

    public Gun(double sD, PixelGroup pG, ParticleSystem ps, float spd)
    {
        pixelGroupTemplate = pG;
        shakeMod = (float)Math.sqrt(pixelGroupTemplate.totalPixels) * .001f;
        bulletPool = new Stack<>();
        shootDelay = sD;
        shotFrameDelay = (int)(shootDelay / Constants.msPerFrame);
        speed = spd;
        int num = ((int)Math.ceil((4/(speed * 60)) * (1000/shootDelay))+1)*numShots;
        bullets = new Bullet[num];
        for(int i = 0; i < num; i++)
        {
            bullets[i] = new Bullet(0, 0, 0,
                    speed, 4f, pixelGroupTemplate.clone());
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

    public void move(float slow)
    {
        for(Bullet b: bullets)
        {
            b.move(slow);
        }
    }

    public void updateBulletPool()
    {
        int num = ((int)Math.ceil((4/(speed * 60)) * (1000/shootDelay))+1)*numShots;
        if(num > bullets.length)
        {
            Bullet[] newBullets = new Bullet[num];
            for (int i = 0; i < num; i++)
            {
                if(i < bullets.length)
                {
                    newBullets[i] = bullets[i];
                }
                else
                {
                    newBullets[i] = new Bullet(0, 0, 0,
                            speed, 4f, pixelGroupTemplate.clone());
                    bulletPool.push(newBullets[i]);
                }
            }
            bullets = newBullets;
        }
    }

    public void applyPauseLength(double p)
    {
        lastShotTime += p;
    }

   /* public boolean shoot(float x, float y, float angle)
    {
        return false;
    }*/

    public boolean shoot(float x, float y, float angle, long f, float slow)
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

    public void freeMemory()
    {
        pixelGroupTemplate.freeMemory();
    }
}
