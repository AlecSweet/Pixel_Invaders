package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.util.Constants;

/**
 * Created by Sweet on 3/25/2018.
 */

public abstract class Gun
{
    //protected Stack<Bullet> bulletPool;

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

    /*protected ObjectNode
            activeTail,
            activeHead,
            poolHead,
            poolTail,
            tempObj;*/

    protected ObjectNode[] indexNodes;
    protected ObjectNode
            openIndexTail,
            openIndexHead;

    protected int numShots = 1;

    protected float fireRateMod = 1;

    protected ParticleSystem masterParticleSystem;

    public Gun(double sD, PixelGroup pG, ParticleSystem ps, float spd)
    {
        pixelGroupTemplate = pG;
        pixelGroupTemplate.restorable = true;
        shakeMod = (float)Math.sqrt(pixelGroupTemplate.totalPixels) * .001f + .002f;
        //bulletPool = new Stack<>();
        shootDelay = sD;
        shotFrameDelay = (int)(shootDelay / Constants.msPerFrame);
        speed = spd;
        int num = ((int)Math.ceil((4/(speed * 60)) * (1000/shootDelay))+1)*numShots + numShots;
        bullets = new Bullet[num];
        indexNodes = new ObjectNode[num];

        for(int i = 0; i < num; i++)
        {
            bullets[i] = new Bullet(0, 0, 0,
                    speed, 4f, pixelGroupTemplate.clone());
            //bullets[i].live = false;
            //bulletPool.add(bullets[i]);

            indexNodes[i] = new ObjectNode(new Integer(i), null);
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
        int num = ((int)Math.ceil((4/(speed * 60)) * (1000/shootDelay))+1)*numShots + numShots;
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
                    //newBullets[i].live = false;
                    //bulletPool.add(newBullets[i]);

                    newNodes[i] = new ObjectNode(new Integer(i), null);
                    openIndexHead.nextObject = newNodes[i];
                    openIndexHead = openIndexHead.nextObject;
                }
            }
            bullets = newBullets;
            indexNodes = newNodes;
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

    public void freeMemory()
    {
        pixelGroupTemplate.freeMemory();
    }
}
