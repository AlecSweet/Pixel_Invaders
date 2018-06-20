package com.example.sweet.game20.Objects;

import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.DropFactory;

import static android.opengl.GLES20.glUniform1f;

/**
 * Created by Sweet on 6/6/2018.
 */

public class Asteroid extends Enemy
{
    public float
            rotationSpeed,
            distX,
            distY;

    private int offScreenFrames = 0;

    private boolean screenEnterToggle = true;

    private int
            healthDropGoal,
            healthDropRarity;

    public Asteroid(PixelGroup p, ParticleSystem ps, float xb, float yb, DropFactory dF)
    {
        super(p, ps, dF);
        guns = null;
        thrusters = null;
        enemyBody.angle = 3.14;
        enemyBody.rotate(enemyBody.angle);
        baseSpeed = .005f;
        p.speed = baseSpeed;
        hasGun = false;
        xbound = xb + .2f;
        ybound = yb + .2f;
        generatePath();
        healthDropRarity = (int)(Math.random()*140+40);
        healthDropGoal = enemyBody.totalPixels  - (int)(Math.random() * healthDropRarity + 9);
    }

    public void generatePath()
    {
        float travelAngle = 0;
        switch((int)(Math.random()*3.99))
        {
            case 0: enemyBody.setLoc((float)(Math.random()*xbound*2)-xbound, ybound);
                    switch((int)(Math.random()*2.99))
                    {
                        case 0: travelAngle = (float)Math.atan2(enemyBody.centerY - (-ybound), enemyBody.centerX - (Math.random() * xbound * 2 - xbound));
                                break;
                        case 1: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * ybound * 2 - ybound), enemyBody.centerX - xbound);
                                break;
                        case 2: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * ybound * 2 - ybound), enemyBody.centerX - (-xbound));
                                break;
                    }
                    break;
            case 1: enemyBody.setLoc((float)(Math.random()*xbound*2)-xbound, -ybound);
                    switch((int)(Math.random()*2.99))
                    {
                        case 0: travelAngle = (float)Math.atan2(enemyBody.centerY - ybound, enemyBody.centerX - (Math.random() * xbound * 2- xbound));
                                break;
                        case 1: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * ybound * 2 - ybound), enemyBody.centerX - xbound);
                                break;
                        case 2: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * ybound * 2 - ybound), enemyBody.centerX - (-xbound));
                                break;
                    }
                    break;
            case 2: enemyBody.setLoc(xbound, (float)(Math.random()*ybound*2)-ybound);
                    switch((int)(Math.random()*2.99))
                    {
                        case 0: travelAngle = (float)Math.atan2(enemyBody.centerY - (-ybound), enemyBody.centerX - (Math.random() * xbound * 2- xbound));
                                break;
                        case 1: travelAngle = (float)Math.atan2(enemyBody.centerY - ybound, enemyBody.centerX - (Math.random() * xbound * 2- xbound));
                                break;
                        case 2: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * ybound * 2- ybound), enemyBody.centerX - (-xbound));
                                break;
                    }
                    break;
            case 3: enemyBody.setLoc(-xbound, (float)(Math.random()*xbound*2)-xbound);
                    switch((int)(Math.random()*2.99))
                    {
                        case 0: travelAngle = (float)Math.atan2(enemyBody.centerY - (-ybound), enemyBody.centerX - (Math.random() * xbound * 2 - xbound));
                                break;
                        case 1: travelAngle = (float)Math.atan2(enemyBody.centerY - ybound, enemyBody.centerX - (Math.random() * xbound * 2 - xbound));
                                break;
                        case 2: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * ybound * 2- ybound), enemyBody.centerX - xbound);
                                break;
                    }
                    break;
        }
        rotationSpeed = (float)(Math.random()*.06);
        distX = (float)(baseSpeed * Math.cos(travelAngle));
        distY = (float)(baseSpeed * Math.sin(travelAngle));
        x = enemyBody.centerX;
        y = enemyBody.centerY;
        //enemyBody.setLoc(0f,0f);
    }

    @Override
    public void move(float unused, float unused1)
    {
        if(onScreen)
        {
            if(!screenEnterToggle)
            {
                screenEnterToggle = true;
                enemyBody.angle += rotationSpeed * offScreenFrames;
                rotate();
                enemyBody.move(-distX * offScreenFrames, -distY * offScreenFrames);
                offScreenFrames = 0;
            }
            else
            {
                enemyBody.angle += rotationSpeed;
                rotate();
                x += -distX;
                y += -distY;
                enemyBody.move(-distX, -distY);
            }
        }
        else
        {
            if(screenEnterToggle)
            {
                screenEnterToggle = false;
            }
            offScreenFrames++;
            x += -distX;
            y += -distY;
        }

        if (x > xbound + .02f ||
                x < -xbound - .02f ||
                x > ybound + .02f ||
                x < -ybound - .02f)
        {
            enemyBody.collidableLive = false;
            System.out.println("Asteroid Dead");
        }

        if(enemyBody.numLivePixels < healthDropGoal)
        {
            dropsToAdd.add(dropFactory.getNewDrop(Constants.DropType.HEALTH, enemyBody.lastPixelKilled.xDisp + x, enemyBody.lastPixelKilled.yDisp + y));
            healthDropGoal -= (int)(Math.random() * healthDropRarity + 9);
        }
    }

    public void rotate()
    {
        enemyBody.rotate(enemyBody.angle);

        if (enemyBody.angle > Math.PI)
            enemyBody.angle -= Constants.twoPI;
        else if (enemyBody.angle < -Math.PI)
            enemyBody.angle += Constants.twoPI;
    }


    @Override
    public void draw(double interpolation)
    {
        enemyBody.draw();
    }

    public void setBounds(float xb, float yb)
    {
        xbound = xb + enemyBody.halfSquareLength;
        ybound = yb + enemyBody.halfSquareLength;
    }

    @Override
    public Asteroid clone()
    {
        return new Asteroid(enemyBody.clone(), particleSystem, xbound, ybound, dropFactory);
    }
}
