package com.example.sweet.Pixel_Invaders.Game_Objects.Enemies;

import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.GunComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Collidable;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Gun;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;

/**
 * Created by Sweet on 8/23/2018.
 */

public class Pulser extends Enemy
{
    public Pixel[][]
            gunsPixels = new Pixel[Constants.pulseGunCoors.length][Constants.pulseGunCoors[0].length/2],
            thrustersPixels = new Pixel[Constants.pulseThrustCoors.length][Constants.pulseThrustCoors[0].length/2];

    private float
            targetX = 0,
            targetY = 0,
            distX = 0,
            distY = 0,
            rotate = 0;

    private boolean inPosition = false;

    public Pulser(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);

        for(int r = 0; r < gunsPixels.length; r++)
        {
            for(int c = 0; c < Constants.pulseGunCoors[0].length; c+=2)
            {
                gunsPixels[r][c / 2] = enemyBody.getpMap()[Constants.pulseGunCoors[r][c + 1] + 1][Constants.pulseGunCoors[r][c] + 1];
            }
        }
        
        for(int r = 0; r < thrustersPixels.length; r++)
        {
            for(int c = 0; c < thrustersPixels[0].length; c+=2)
            {
                thrustersPixels[r][c / 2] = enemyBody.getpMap()[Constants.pulseThrustCoors[r][c + 1] + 1][Constants.pulseThrustCoors[r][c] + 1];
            }
        }

        guns = new GunComponent[4];
        guns[2] = new GunComponent(gunsPixels[0], Constants.pulseGunOffsets[2][0], Constants.pulseGunOffsets[2][1], 0, g);
        guns[1] = new GunComponent(gunsPixels[1], Constants.pulseGunOffsets[1][0], Constants.pulseGunOffsets[1][1], 0, g.clone());
        guns[0] = new GunComponent(gunsPixels[2], Constants.pulseGunOffsets[0][0], Constants.pulseGunOffsets[0][1], 0, g.clone());
        guns[3] = new GunComponent(gunsPixels[3], Constants.pulseGunOffsets[3][0], Constants.pulseGunOffsets[3][1], 0, g.clone());
        hasGun = true;

        thrusters = new ThrustComponent[8];
        thrusters[0] = new ThrustComponent(thrustersPixels[0], 0, 0, 0, 2);
        thrusters[1] = new ThrustComponent(thrustersPixels[1], 0, 0, 0, 2);
        thrusters[2] = new ThrustComponent(thrustersPixels[2], 0, 0, 0, 2);
        thrusters[3] = new ThrustComponent(thrustersPixels[3], 0, 0, 0, 2);
        thrusters[4] = new ThrustComponent(thrustersPixels[4], 0, 0, 0, 2);
        thrusters[5] = new ThrustComponent(thrustersPixels[5], 0, 0, 0, 2);
        thrusters[6] = new ThrustComponent(thrustersPixels[6], 0, 0, 0, 2);
        thrusters[7] = new ThrustComponent(thrustersPixels[7], 0, 0, 0, 2);

        checkOverlap = false;
        targetX = (float)Math.random() * 1.2f * (x/Math.abs(x));
        targetY = (float)Math.random() * 1.2f * (y/Math.abs(y));
        baseSpeed = .004f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;

        float travelAngle = -(float)(Math.atan2(targetY - enemyBody.centerY, targetX - enemyBody.centerX));
        distX = -(float)(baseSpeed * Math.cos(travelAngle));
        distY = (float)(baseSpeed * Math.sin(travelAngle));
        rotate = (float)(Math.random()*.06 - .03);
    }

    private Pulser(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, float difficulty, float delay, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, delay, gI);

        for(int r = 0; r < gunsPixels.length; r++)
        {
            for(int c = 0; c < Constants.pulseGunCoors[0].length; c+=2)
            {
                gunsPixels[r][c / 2] = enemyBody.getpMap()[Constants.pulseGunCoors[r][c + 1] + 1][Constants.pulseGunCoors[r][c] + 1];
            }
        }

        for(int r = 0; r < thrustersPixels.length; r++)
        {
            for(int c = 0; c < thrustersPixels[0].length; c+=2)
            {
                thrustersPixels[r][c / 2] = enemyBody.getpMap()[Constants.pulseThrustCoors[r][c + 1] + 1][Constants.pulseThrustCoors[r][c] + 1];
            }
        }

        float power = 1;
        float del = 1;
        float shotspd = 1;
        float sprd;
        if(difficulty <= 25f)
        {
            float per = difficulty / 25f;
            power += 2f * per;
            del -= per / 2f;
            shotspd += per;
            sprd = 1f - per;
        }
        else
        {
            power = 3f;
            del = .5f;
            shotspd = 2f;
            sprd = 0f;
        }

        guns = new GunComponent[4];
        guns[0] = new GunComponent(gunsPixels[0], Constants.pulseGunOffsets[2][0], Constants.pulseGunOffsets[2][1], 0, g);
        guns[1] = new GunComponent(gunsPixels[1], Constants.pulseGunOffsets[1][0], Constants.pulseGunOffsets[1][1], 0, g.clone());
        guns[2] = new GunComponent(gunsPixels[2], Constants.pulseGunOffsets[0][0], Constants.pulseGunOffsets[0][1], 0, g.clone());
        guns[3] = new GunComponent(gunsPixels[3], Constants.pulseGunOffsets[3][0], Constants.pulseGunOffsets[3][1], 0, g.clone());
        guns[0].gun.reduceSpread(sprd);
        guns[0].gun.incSpeed(shotspd);
        guns[0].gun.reduceDelay(del);
        guns[1].gun.reduceSpread(sprd);
        guns[1].gun.incSpeed(shotspd);
        guns[1].gun.reduceDelay(del);
        guns[2].gun.reduceSpread(sprd);
        guns[2].gun.incSpeed(shotspd);
        guns[2].gun.reduceDelay(del);
        guns[3].gun.reduceSpread(sprd);
        guns[3].gun.incSpeed(shotspd);
        guns[3].gun.reduceDelay(del);
        hasGun = true;

        thrusters = new ThrustComponent[8];
        thrusters[0] = new ThrustComponent(thrustersPixels[0], 0, 0, 0,2);
        thrusters[1] = new ThrustComponent(thrustersPixels[1], 0, 0, 0,2);
        thrusters[2] = new ThrustComponent(thrustersPixels[2], 0, 0, 0,2);
        thrusters[3] = new ThrustComponent(thrustersPixels[3], 0, 0, 0,2);
        thrusters[4] = new ThrustComponent(thrustersPixels[4], 0, 0, 0,2);
        thrusters[5] = new ThrustComponent(thrustersPixels[5], 0, 0, 0,2);
        thrusters[6] = new ThrustComponent(thrustersPixels[6], 0, 0, 0,2);
        thrusters[7] = new ThrustComponent(thrustersPixels[7], 0, 0, 0,2);

        checkOverlap = false;
        targetX = (float)Math.random() * 1.2f * (x/Math.abs(x));
        targetY = (float)Math.random() * 1.2f * (y/Math.abs(y));
        baseSpeed = .004f * power;
        enemyBody.speed = baseSpeed;

        float travelAngle = -(float)(Math.atan2(targetY - enemyBody.centerY, targetX - enemyBody.centerX));
        distX = -(float)(baseSpeed * Math.cos(travelAngle));
        distY = (float)(baseSpeed * Math.sin(travelAngle));
        rotate = (float)(Math.random()*.06 - .03);
        generateDrops(guns[0], thrusters[0], difficulty, 1);
    }

    @Override
    public void move(float pX, float pY)
    {
        if(!inPosition)
        {
            if (Math.abs(enemyBody.getCenterX() - targetX) <= enemyBody.halfSquareLength &&
                    Math.abs(enemyBody.getCenterY() - targetY) <= enemyBody.halfSquareLength)
            {
                inPosition = true;
            }
            else
            {
                x += -distX;
                y += -distY;
                enemyBody.move(-distX, -distY);
            }
        }
        else
        {
            enemyBody.move(0, 0);
            float tRem = 0;
            for(GunComponent gC: guns)
            {
                if(gC.live)
                {
                    tRem = gC.gun.getPercentTimeRemaining(globalInfo);
                    break;
                }
            }

            if(tRem < .9f && tRem > .1f)
            {
                rotate(enemyBody.angle + rotate, .01f, globalInfo.timeSlow);
            }
            else if(tRem > .84f && tRem < .94f)
            {
                if(rotate < 0)
                {
                    addThrustParticles(thrustersPixels[3], 1, .05f, enemyBody, -(float)(enemyBody.angle - Math.PI));
                    addThrustParticles(thrustersPixels[1], 1, .05f, enemyBody, -(float)(enemyBody.angle - Math.PI/2));
                    addThrustParticles(thrustersPixels[7], 1, .05f, enemyBody, -(enemyBody.angle));
                    addThrustParticles(thrustersPixels[5], 1, .05f, enemyBody, -(float)(enemyBody.angle - (Math.PI/ 2) * 3));
                }
                else
                {
                    addThrustParticles(thrustersPixels[6], 1, .05f, enemyBody, -(float)(enemyBody.angle - Math.PI/2));
                    addThrustParticles(thrustersPixels[4], 1, .05f, enemyBody, -(enemyBody.angle));
                    addThrustParticles(thrustersPixels[2], 1, .05f, enemyBody, -(float)(enemyBody.angle - (Math.PI/ 2) * 3));
                    addThrustParticles(thrustersPixels[0], 1, .05f, enemyBody, -(float)(enemyBody.angle - Math.PI));
                }
            }

            for(int i = 0; i < guns.length; i++)
            {
                if(guns[i] != null)
                {
                    if(guns[i].canShoot(globalInfo))
                    {
                        float ang = enemyBody.angle + ((float) Math.PI / 2) * i;
                        guns[i].shoot(
                                enemyBody.centerX,
                                enemyBody.centerY,
                                ang,
                                globalInfo,
                                enemyBody.cosA,
                                enemyBody.sinA,
                                0f
                        );
                    }
                    guns[i].move(globalInfo);
                }
            }
        }
    }

    public void rotate(float angleMoving, float rotateSpeed, float slow)
    {
        float delta = enemyBody.angle - angleMoving;
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

        if (delta > .001 || delta < -.001)
        {
            if (delta < -Math.PI || (delta > 0 && delta < Math.PI))
            {
                addThrustParticles(thrustersPixels[6], 1, .05f, enemyBody, -(enemyBody.angle - (float)Math.PI/2));
                addThrustParticles(thrustersPixels[4], 1, .05f, enemyBody, -(enemyBody.angle));
                addThrustParticles(thrustersPixels[2], 1, .05f, enemyBody, -(enemyBody.angle - (float)(Math.PI/ 2) * 3));
                addThrustParticles(thrustersPixels[0], 1, .05f, enemyBody, -(enemyBody.angle - (float)Math.PI));
            }
            else
            {
                addThrustParticles(thrustersPixels[3], 1, .05f, enemyBody, -(enemyBody.angle - (float)Math.PI));
                addThrustParticles(thrustersPixels[1], 1, .05f, enemyBody, -(enemyBody.angle - (float)Math.PI/2));
                addThrustParticles(thrustersPixels[7], 1, .05f, enemyBody, -(enemyBody.angle));
                addThrustParticles(thrustersPixels[5], 1, .05f, enemyBody, -(enemyBody.angle - (float)(Math.PI/ 2) * 3));
            }
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

    public void addThrustParticles(Pixel[] pixels, float ratio, float dist, Collidable c)
    {
        for(Pixel p: pixels)
        {
            if(p != null && p.state >= 1)
            {
                float xDisp = c.infoMap[p.row][p.col].xOriginal * enemyBody.cosA +
                        c.infoMap[p.row][p.col].yOriginal * enemyBody.sinA;
                float yDisp = c.infoMap[p.row][p.col].yOriginal * enemyBody.cosA -
                        c.infoMap[p.row][p.col].xOriginal * enemyBody.sinA;
                for (int t = 0; t < 2; t++)
                {
                    particleSystem.addParticle(xDisp + enemyBody.centerX,
                            yDisp + enemyBody.centerY,
                            -enemyBody.angle + (float)Math.PI,
                            c.infoMap[p.row][p.col].r,
                            c.infoMap[p.row][p.col].g,
                            c.infoMap[p.row][p.col].b,
                            .7f,
                            (baseSpeed * thrusters[0].getThrustPower() * (float)(Math.random()*70+20)) * ratio,
                            dist * ratio * (float)Math.random()*2,
                            (float)(Math.random()*20)
                    );
                }
            }
        }
    }

    private void addThrustParticles(Pixel[] pixels, float ratio, float dist, Collidable c, float ang)
    {
        for(Pixel p: pixels)
        {
            if(p != null && p.state >= 1)
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
                            ang,
                            c.infoMap[p.row][p.col].r,
                            c.infoMap[p.row][p.col].g,
                            c.infoMap[p.row][p.col].b,
                            .7f,
                            (baseSpeed * thrusters[0].getThrustPower() * (float)(Math.random()*140+40)) * ratio,
                            dist * ratio * (float)Math.random()*2,
                            (float)(Math.random()*20)
                    );
                }
            }
        }
    }

    public Pulser clone(float difficulty, float delay)
    {
        return new Pulser(
                enemyBody.clone(),
                guns[0].gun.clone(),
                particleSystem,
                dropFactory,
                xbound,
                ybound,
                difficulty,
                delay,
                globalInfo
        );
    }
}
