package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.DropFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;

/**
 * Created by Sweet on 2/14/2018.
 */

public class Enemy extends Drawable
{
    protected PixelGroup enemyBody;

    protected ParticleSystem particleSystem;

    protected DropFactory dropFactory;

    protected GlobalInfo globalInfo;

    protected GunComponent[] guns;

    protected ThrustComponent[] thrusters;

    public ConcurrentLinkedQueue<Drop> dropsToAdd = new ConcurrentLinkedQueue<>();

    protected long lastMoveTime;

    protected float
            spawnDelay,
            creationTime,
            levelStartTime,
            baseSpeed,
            xbound,
            ybound,
            rotateSpeed;

    protected volatile float
            x,
            y;

    protected boolean hasGun = false;

    public volatile boolean
            uiRemoveConsensus = false,
            aiRemoveConsensus = false,
            collisionRemoveConsensus = false,
            inRange = false;

    public boolean
            live = true,
            spawned = false,
            checkOverlap = true;

    public Enemy(PixelGroup p, ParticleSystem ps, DropFactory dF, float xb, float yb, GlobalInfo gI)
    {
        xbound = xb + p.halfSquareLength;
        ybound = yb + p.halfSquareLength;
        lastMoveTime = System.currentTimeMillis();
        particleSystem = ps;
        enemyBody = p;
        enemyBody.setEnableOrphanChunkDeletion(true);
        dropFactory = dF;
        spawnDelay = 0;
        spawned = true;
        globalInfo = gI;
        creationTime = 0;
        generateLocation();
    }

    public Enemy(PixelGroup p, ParticleSystem ps, DropFactory dF, float xb, float yb, float delay, GlobalInfo gI)
    {
        xbound = xb + p.halfSquareLength;
        ybound = yb + p.halfSquareLength;
        lastMoveTime = System.currentTimeMillis();
        particleSystem = ps;
        enemyBody = p;
        enemyBody.setEnableOrphanChunkDeletion(true);
        dropFactory = dF;
        spawnDelay = delay;
        globalInfo = gI;
        creationTime = globalInfo.getAugmentedTimeMillis();
        generateLocation();
    }

    public void move(float mX, float mY)
    {
        System.out.println("IN HERE");
        /*float angle = (float)Math.atan2(mY, mX);
        float tempDistX = -(float)(speed * Math.cos(angle));
        float tempDistY = -(float)(speed * Math.sin(angle));
        x+=tempDistX;
        y+=tempDistY;
        enemyBody.move(tempDistX, tempDistY);
        enemyBody.rotate(-angle);*/
    }

    public void checkSpawn()
    {
        if(globalInfo.getAugmentedTimeMillis() - levelStartTime > spawnDelay)
        {
            spawned = true;
        }
    }

    public void setLevelStartTime(float sT)
    {
        levelStartTime = sT;
    }

    public void rotate(float angleMoving, float ratio)
    {

    }

    public void rotate(float angleMoving, float rotateSpeed, float slow)
    {
        float delta = (float) enemyBody.angle - angleMoving;
        if (delta > rotateSpeed || delta < -rotateSpeed)
        {
            if (delta < -Math.PI || (delta > 0 && delta < Math.PI))
            {
                enemyBody.angle -= rotateSpeed * slow;
            }
            else
            {
                enemyBody.angle += rotateSpeed * slow;
            }
        }
        else
        {
            enemyBody.angle = angleMoving;
        }

        enemyBody.rotate(enemyBody.angle);

        if (enemyBody.angle > Math.PI)
        {
            enemyBody.angle -= Constants.twoPI;
        }
        else if (enemyBody.angle < -Math.PI)
        {
            enemyBody.angle += Constants.twoPI;
        }
    }

    public void setLoc(float sX, float sY)
    {
        x = sX;
        y = sY;
        enemyBody.setLoc(sX, sY);
    }

    public void knockBack(float angle, float extraRatio, float dist)
    {
        float tempDistX = (float) (dist * extraRatio * Math.cos(angle));
        float tempDistY = (float) (dist * extraRatio * Math.sin(angle));
        x += tempDistX;
        y += tempDistY;
        enemyBody.knockBack(tempDistX, tempDistY);
    }

    @Override
    public void draw(double interpolation)
    {
        if (onScreen && getPixelGroup().getCollidableLive())
        {
            enemyBody.draw();
        }

        if (getHasGun())
        {
            for (GunComponent gC : getGunComponents())
            {
                if (gC != null)
                {
                    gC.gun.draw(0);
                }
            }
        }
    }

    public void addThrustParticles(Pixel[] pixels, float ratio, float dist, Collidable c)
    {
        for (Pixel p : pixels)
        {
            //if(p.live)
            if (p != null && p.state >= 1)
            {
                float xDisp = c.infoMap[p.row][p.col].xOriginal * enemyBody.cosA +
                        c.infoMap[p.row][p.col].yOriginal * enemyBody.sinA;
                float yDisp = c.infoMap[p.row][p.col].yOriginal * enemyBody.cosA -
                        c.infoMap[p.row][p.col].xOriginal * enemyBody.sinA;
                for (int t = 0; t < 2; t++)
                {
                    particleSystem.addParticle(xDisp + enemyBody.centerX,
                            yDisp + enemyBody.centerY,
                            //-enemyBody.cosA, enemyBody.sinA,
                            (float) -enemyBody.angle + (float) Math.PI,
                            c.infoMap[p.row][p.col].r,
                            c.infoMap[p.row][p.col].g,
                            c.infoMap[p.row][p.col].b,
                            .7f,
                            (baseSpeed * thrusters[0].getThrustPower() * (float) (Math.random() * 70 + 20)) * ratio,
                            dist * ratio * (float) Math.random() * 2,
                            (float) (Math.random() * 20)
                    );
                }
            }
        }
    }

    public void collisionOccured()
    {
        if(guns != null)
        {
            for (GunComponent gC : guns)
            {
                if (gC != null)
                {
                    gC.checkAlive();
                }
            }
        }

        if(thrusters != null)
        {
            for (ThrustComponent tC : thrusters)
            {
                if (tC != null)
                {
                    tC.checkAlive();
                }
            }
        }
    }

    public void shoot()
    {

    }

    public void applyPauseLength(double p)
    {

    }

    public void generateLocation()
    {
        switch((int)(Math.random()*3.99))
        {
            case 0:
                setLoc((float) (Math.random() * xbound * 2) - xbound, ybound);
                break;
            case 1:
                setLoc((float) (Math.random() * xbound * 2) - xbound, -ybound);
                break;
            case 2:
                setLoc(xbound, (float) (Math.random() * ybound * 2) - ybound);
                break;
            case 3:
                setLoc(-xbound, (float) (Math.random() * xbound * 2) - xbound);
                break;
        }
    }

    public PixelGroup getPixelGroup()
    {
        return enemyBody;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public boolean getHasGun()
    {
        return hasGun;
    }

    public GunComponent[] getGunComponents()
    {
        return guns;
    }

    public void setBounds(float xb, float yb)
    {
        xbound = xb;
        ybound = yb;
    }

    public Enemy clone(float difficulty, float delay)
    {
        System.out.println("SHOULDNT BE IN HERE _____________________________________________________________");
        Enemy e = new Enemy(enemyBody.clone(), particleSystem, dropFactory, xbound, ybound, globalInfo);
        e.setLoc(x, y);
        return e;
    }

    public void publishLocation(long frame)
    {
        if(spawned)
        {
            if (enemyBody.enableLocationChain)
            {
                enemyBody.publishLocation(frame);
            }
            if (getHasGun())
            {
                for (GunComponent gC : getGunComponents())
                {
                    if (gC != null)
                    {
                        gC.gun.publishLocation(frame);
                    }
                }
            }
        }
    }

    public void freeMemory()
    {
        if (getHasGun())
        {
            for (GunComponent gC : getGunComponents())
            {
                if (gC != null)
                {
                    for (Bullet b : gC.gun.getBullets())
                    {
                        b.freeResources();
                    }
                }
            }
        }
        getPixelGroup().freeMemory();
        enemyBody = null;
        particleSystem = null;
        dropsToAdd = null;
        guns = null;
        thrusters = null;
    }
}
