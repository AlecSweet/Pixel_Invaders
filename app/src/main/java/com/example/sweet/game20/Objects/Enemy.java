package com.example.sweet.game20.Objects;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;

/**
 * Created by Sweet on 2/14/2018.
 */

public class Enemy extends Drawable
{
    protected PixelGroup enemyBody;

    protected ParticleSystem particleSystem;

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

    public Enemy(PixelGroup p, ParticleSystem ps)
    {
        particleSystem = ps;
        x = 0.0f;
        y = 0.0f;
        enemyBody = p;
        enemyBody.setEnableOrphanChunkDeletion(true);
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

    public void rotate(float angleMoving, float ratio)
    {

    }

    public void setLoc(float sX, float sY)
    {
        x+=sX;
        y+=sY;
        enemyBody.move(sX, sY);
    }

    @Override
    public void draw(double interpolation)
    {
        enemyBody.draw();
    }

    public void shoot()
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

    @Override
    public Enemy clone()
    {
        Enemy e = new Enemy(enemyBody.clone(), particleSystem);
        e.setLoc(x, y);
        return e;
    }
}
