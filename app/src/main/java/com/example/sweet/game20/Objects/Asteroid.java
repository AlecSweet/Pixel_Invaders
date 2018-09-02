package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.DropFactory;

/**
 * Created by Sweet on 6/6/2018.
 */

public class Asteroid extends Enemy
{
    public float
            rotationSpeed,
            distX,
            distY;

    private boolean
            screenEnterToggle = true,
            inBackground = false;

    private float
            backgroundX,
            backgorundY;

    private int
            healthDropGoal,
            healthDropRarity;

    public Asteroid(PixelGroup p, ParticleSystem ps, float xb, float yb, DropFactory dF, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);
        guns = null;
        thrusters = null;
        checkOverlap = false;
        //enemyBody.angle = 3.14;
        enemyBody.rotate(enemyBody.angle);
        baseSpeed = .005f;
        p.speed = baseSpeed;
        hasGun = false;
        xbound = xb + .2f;
        ybound = yb + .2f;
        generatePath();
        healthDropRarity = (int)(Math.random()*140+40);
        healthDropGoal = enemyBody.totalPixels  - (int)(Math.random() * healthDropRarity + 9);
        enemyBody.setEdgeColor(.3f, .3f, .3f);
    }

    public void generatePath()
    {
        float travelAngle = 0;
        switch((int)(Math.random()*3.99))
        {
            case 0: setLoc((float)(Math.random()*xbound*2)-xbound, ybound);
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
            case 1: setLoc((float)(Math.random()*xbound*2)-xbound, -ybound);
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
            case 2: setLoc(xbound, (float)(Math.random()*ybound*2)-ybound);
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
            case 3: setLoc(-xbound, (float)(Math.random()*xbound*2)-xbound);
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
        rotationSpeed = (float)(Math.random()*.009)-.0045f;

        distX = (float)(baseSpeed * Math.cos(travelAngle));
        distY = (float)(baseSpeed * Math.sin(travelAngle));

        /*enemyBody.setLoc((float)(Math.random()*2-1),(float)(Math.random()*2-1));
        distX = 0;
        distY = 0;*/

        enemyBody.speed = baseSpeed;
        //enemyBody.resetLocationHistory(x, y);
        //enemyBody.setLoc(0f,0f);
    }

    @Override
    //public void move(float unused, float unused1, long curFrame, float slow)
    public void move(float unused, float unused1)
    {
        float tempDistX = distX * globalInfo.timeSlow;
        float tempDistY = distY * globalInfo.timeSlow;
        /*if(onScreen)
        {
            if(!screenEnterToggle)
            {
                screenEnterToggle = true;
                rotate();
                enemyBody.setLoc(x, y);
                enemyBody.resetLocationHistory(x, y);
            }
            else
            {*/
                enemyBody.angle += rotationSpeed * globalInfo.timeSlow;
                //rotate();
                x += -tempDistX;
                y += -tempDistY;
                enemyBody.move(-tempDistX, -tempDistY);
        /*    }
        }
        else
        {
            if(screenEnterToggle)
            {
                screenEnterToggle = false;
            }
            enemyBody.angle += rotationSpeed * gI.timeSlow;
            x += -tempDistX;
            y += -tempDistY;
        }*/

        if (x > xbound + .02f ||
                x < -xbound - .02f ||
                x > ybound + .02f ||
                x < -ybound - .02f)
        {
            aiRemoveConsensus = true;
            //System.out.println("Asteroid Dead");
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
        {
            enemyBody.angle -= Constants.twoPI;
        }
        else if (enemyBody.angle < -Math.PI)
        {
            enemyBody.angle += Constants.twoPI;
        }
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

    public Asteroid clone(float dif, float delay)
    {
        return new Asteroid(
                enemyBody.clone(),
                particleSystem,
                xbound,
                ybound,
                dropFactory,
                globalInfo);
    }
}
