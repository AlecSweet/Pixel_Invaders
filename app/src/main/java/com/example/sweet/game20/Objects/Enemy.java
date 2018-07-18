package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
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

    protected long lastMoveTime;

    public ConcurrentLinkedQueue<Drop> dropsToAdd = new ConcurrentLinkedQueue<>();

    protected volatile float
            x,
            y;

    protected float
            baseSpeed;

    protected boolean hasGun = false;

    public volatile boolean
            aiRemoveConsensus = false,
            collisionRemoveConsensus = false;

    protected GunComponent[] guns;

    protected ThrustComponent[] thrusters;

    public boolean live = true;

    protected float xbound, ybound;

    public Enemy(PixelGroup p, ParticleSystem ps, DropFactory dF)
    {
        lastMoveTime = System.currentTimeMillis();
        particleSystem = ps;
        x = 0.0f;
        y = 0.0f;
        enemyBody = p;
        enemyBody.setEnableOrphanChunkDeletion(true);
        dropFactory = dF;
    }

    public void move(float mX, float mY, long curFrame, float slow)
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

    public void rotate(float angleMoving, float ratio)
    {

    }

    public void setLoc(float sX, float sY)
    {
        x+=sX;
        y+=sY;
        enemyBody.move(sX, sY);
    }

    public void knockBack(float angle, float extraRatio, float dist)
    {
        float tempDistX = (float)(dist * extraRatio * Math.cos(angle));
        float tempDistY = (float)(dist * extraRatio * Math.sin(angle));
        x += tempDistX;
        y += tempDistY;
        enemyBody.knockBack(tempDistX, tempDistY);
    }

    @Override
    public void draw(double interpolation)
    {
        enemyBody.draw();
    }

    public void shoot()
    {

    }

    public void applyPauseLength(double p)
    {

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

    @Override
    public Enemy clone()
    {
        System.out.println("SHOULDNT BE IN HERE _____________________________________________________________");
        Enemy e = new Enemy(enemyBody.clone(), particleSystem, dropFactory);
        e.setLoc(x, y);
        return e;
    }
}
