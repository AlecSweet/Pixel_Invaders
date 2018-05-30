package com.example.sweet.game20.Objects;

import android.content.Context;

import com.example.sweet.game20.R;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.ImageParser;
import com.example.sweet.game20.util.PixelShapes;
import com.example.sweet.game20.util.Shapes;
import com.example.sweet.game20.util.VectorFunctions;

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

    protected int
            xDispLoc,
            yDispLoc;

    private String
            X_DISP = "x_displacement",
            Y_DISP = "y_displacement";

    protected boolean hasGun = false;

    protected GunComponent[] guns;

    protected ThrustComponent[] thrusters;

    public boolean live = true;

    public Enemy(PixelGroup p, int sL, ParticleSystem ps)
    {
        particleSystem = ps;
        x = 0.0f;
        y = 0.0f;
        enemyBody = p;
        baseSpeed = .003f;
        enemyBody.setEnableOrphanChunkDeletion(true);
        xDispLoc = glGetUniformLocation(sL,X_DISP);
        yDispLoc = glGetUniformLocation(sL,Y_DISP);
    }

    public void move(float mX, float mY)
    {
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

    public void setLoc(float x1, float x2)
    {
        x+=x1;
        y+=x2;
        enemyBody.move(x1, x2);
    }

    @Override
    public void draw(double interpolation)
    {
        glUniform1f(xDispLoc, enemyBody.getCenterX());
        glUniform1f(yDispLoc, enemyBody.getCenterY());
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

    public void setUniformLocs(int xLoc, int yLoc)
    {
        xDispLoc = xLoc;
        yDispLoc = yLoc;
    }

    @Override
    public Enemy clone()
    {
        Enemy e = new Enemy(enemyBody.clone(),0, particleSystem);
        e.setUniformLocs(xDispLoc, yDispLoc);
        e.setLoc(x, y);

        return e;
    }
}
