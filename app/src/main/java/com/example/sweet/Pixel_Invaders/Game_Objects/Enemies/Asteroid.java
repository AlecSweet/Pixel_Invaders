package com.example.sweet.Pixel_Invaders.Game_Objects.Enemies;

import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Drop;
import com.example.sweet.Pixel_Invaders.Util.Static.VectorFunctions;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;

import static android.opengl.GLES20.glUniform1f;

/**
 * Created by Sweet on 6/6/2018.
 */

public class Asteroid extends Enemy
{
    private float
            rotationSpeed,
            distX,
            distY;

    private float travelDistance, mod;

    private int
            healthDropGoal,
            healthDropRarity;
    public boolean levelDone = false;

    public Asteroid(PixelGroup p, ParticleSystem ps, float xb, float yb, DropFactory dF, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);
        consumables = new Drop[5];
        isAsteriod = true;
        guns = null;
        thrusters = null;
        checkOverlap = false;
        enemyBody.rotate(enemyBody.angle);
        baseSpeed = .005f;
        enemyBody.speed = baseSpeed;
        hasGun = false;
        xbound = xb + .2f;
        ybound = yb + .2f;
        generatePath();
        healthDropRarity = (int)(Math.random()*140+40);
        healthDropGoal = enemyBody.totalPixels  - (int)(Math.random() * healthDropRarity + 9);
    }

    private void generatePath()
    {
        float travelAngle = 0;
        float num;
        switch((int)(Math.random()*3.99))
        {
            case 0:
                setLoc((float)(Math.random()*xbound*2)-xbound, ybound);
                num = (float)Math.random() * xbound * 2;
                travelDistance = VectorFunctions.getMagnitude(enemyBody.centerY - (-ybound), enemyBody.centerX - (num - xbound));
                travelAngle = (float)Math.atan2(enemyBody.centerY - (-ybound), enemyBody.centerX - (num - xbound));
                break;
            case 1:
                setLoc((float)(Math.random()*xbound*2)-xbound, -ybound);
                num = (float)Math.random() * xbound * 2;
                travelDistance = VectorFunctions.getMagnitude(enemyBody.centerY - ybound, enemyBody.centerX - (num - xbound));
                travelAngle = (float)Math.atan2(enemyBody.centerY - ybound, enemyBody.centerX - (num - xbound));
                break;
            case 2:
                setLoc(xbound, (float)(Math.random()*ybound*2)-ybound);
                num = (float)Math.random() * ybound * 2;
                travelDistance = VectorFunctions.getMagnitude(enemyBody.centerY - (num - ybound), enemyBody.centerX - (-xbound));
                travelAngle = (float)Math.atan2(enemyBody.centerY - (num - ybound), enemyBody.centerX - (-xbound));
                break;
            case 3:
                setLoc(-xbound, (float)(Math.random()*xbound*2)-xbound);
                num = (float)Math.random() * ybound * 2;
                travelDistance = VectorFunctions.getMagnitude(enemyBody.centerY - (num - ybound), enemyBody.centerX - xbound);
                travelAngle = (float)Math.atan2(enemyBody.centerY - (num - ybound), enemyBody.centerX - xbound);
                break;
        }
        /*switch((int)(Math.random()*3.99))
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
        }*/
        rotationSpeed = (float)(Math.random()*.009)-.0045f;

        distX = (float)(baseSpeed * Math.cos(travelAngle));
        distY = (float)(baseSpeed * Math.sin(travelAngle));

        enemyBody.speed = baseSpeed;
    }

    @Override
    public void move(float unused, float unused1)
    {
        if(!inBackground)
        {
            float tempDistX = distX * globalInfo.timeSlow;
            float tempDistY = distY * globalInfo.timeSlow;

            enemyBody.angle += rotationSpeed * globalInfo.timeSlow;
            x += -tempDistX;
            y += -tempDistY;
            enemyBody.move(-tempDistX, -tempDistY);

            if (x > xbound + .02f ||
                    x < -xbound - .02f ||
                    y > ybound + .02f ||
                    y < -ybound - .02f)
            {
                if(!levelDone)
                {
                    inBackground = true;
                }
                else
                {
                    aiRemoveConsensus = true;
                    collisionRemoveConsensus = true;
                }
                backgroundX = x + tempDistX;
                backgroundY = y + tempDistY;
                enemyBody.move(-10, -10);
                //aiRemoveConsensus = true;
            }

            if (enemyBody.numLivePixels < healthDropGoal)
            {
                //cdropsToAdd.add(dropFactory.getNewDrop(Constants.DropType.HEALTH, enemyBody.lastPixelKilled.xDisp + x, enemyBody.lastPixelKilled.yDisp + y));
                consumables[consumIndex] = dropFactory.getNewDrop(Constants.DropType.HEALTH, enemyBody.lastPixelKilled.xDisp + x, enemyBody.lastPixelKilled.yDisp + y);
                consumIndex++;
                if(consumIndex > 4)
                {
                    consumIndex = 0;
                }
                healthDropGoal -= (int) (Math.random() * healthDropRarity + 9);
            }
        }
        else
        {
            if(!levelDone)
            {
                float tempDistX = -distX * globalInfo.timeSlow;
                float tempDistY = -distY * globalInfo.timeSlow;

                /*float dist = VectorFunctions.getMagnitude(x - backgroundX, y - backgroundY);
                float travelRatio = dist / travelDistance;
                mod = (float) Math.sin(travelRatio * Math.PI) * .6f + .3f;
                backgroundX += -tempDistX * mod;
                backgroundY += -tempDistY * mod;*/
                backgroundX += -tempDistX;
                backgroundY += -tempDistY;

                enemyBody.angle += rotationSpeed * globalInfo.timeSlow;

                if (backgroundX > xbound + .02f ||
                        backgroundX < -xbound - .02f ||
                        backgroundY > ybound + .02f ||
                        backgroundY < -ybound - .02f)
                {
                    inBackground = false;
                    setLoc(backgroundX + tempDistX, backgroundY + tempDistY);
                    //aiRemoveConsensus = true;
                }
            }
            else
            {
                aiRemoveConsensus = true;
                collisionRemoveConsensus = true;
            }
        }
    }
    @Override
    public void drawInBackground()
    {
        enemyBody.softDraw(backgroundX, backgroundY, enemyBody.cosA, enemyBody.sinA, 0f, 1 - mod, globalInfo.pointSize * (1 - mod));
    }

    @Override
    public void setShift(int locX, int locY)
    {
        glUniform1f(locX, 0);
        glUniform1f(locY, 0);
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
