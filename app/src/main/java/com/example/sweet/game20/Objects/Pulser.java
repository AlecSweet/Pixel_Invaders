package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.DropFactory;
import com.example.sweet.game20.util.VectorFunctions;

/**
 * Created by Sweet on 8/23/2018.
 */

public class Pulser extends Enemy
{
    /*private Pixel[][]
            gun1Pixels = new Pixel[Constants.pulseGunCoor1.length/2],
            gun2Pixels = new Pixel[Constants.pulseGunCoor2.length/2],
            gun3Pixels = new Pixel[Constants.pulseGunCoor3.length/2],
            gun4Pixels = new Pixel[Constants.pulseGunCoor4.length/2],
            thruster1Pixels = new Pixel[Constants.pulseThrustCoor1.length/2],
            thruster2Pixels = new Pixel[Constants.pulseThrustCoor2.length/2],
            thruster3Pixels = new Pixel[Constants.pulseThrustCoor3.length/2],
            thruster4Pixels = new Pixel[Constants.pulseThrustCoor4.length/2],
            thruster5Pixels = new Pixel[Constants.pulseThrustCoor5.length/2],
            thruster6Pixels = new Pixel[Constants.pulseThrustCoor6.length/2],
            thruster7Pixels = new Pixel[Constants.pulseThrustCoor7.length/2],
            thruster8Pixels = new Pixel[Constants.pulseThrustCoor8.length/2];*/
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
        guns[2] = new GunComponent(gunsPixels[0], Constants.pulseGunOffsets[2][0], Constants.pulseGunOffsets[2][1], 0, g, ps);
        guns[1] = new GunComponent(gunsPixels[1], Constants.pulseGunOffsets[1][0], Constants.pulseGunOffsets[1][1], 0, g.clone(), ps);
        guns[0] = new GunComponent(gunsPixels[2], Constants.pulseGunOffsets[0][0], Constants.pulseGunOffsets[0][1], 0, g.clone(), ps);
        guns[3] = new GunComponent(gunsPixels[3], Constants.pulseGunOffsets[3][0], Constants.pulseGunOffsets[3][1], 0, g.clone(), ps);
        hasGun = true;

        thrusters = new ThrustComponent[8];
        thrusters[0] = new ThrustComponent(thrustersPixels[0], 0, 0, 0,2, 2, ps);
        thrusters[1] = new ThrustComponent(thrustersPixels[1], 0, 0, 0,1, 2, ps);
        thrusters[2] = new ThrustComponent(thrustersPixels[2], 0, 0, 0,2, 2, ps);
        thrusters[3] = new ThrustComponent(thrustersPixels[3], 0, 0, 0,1, 2, ps);
        thrusters[4] = new ThrustComponent(thrustersPixels[4], 0, 0, 0,2, 2, ps);
        thrusters[5] = new ThrustComponent(thrustersPixels[5], 0, 0, 0,1, 2, ps);
        thrusters[6] = new ThrustComponent(thrustersPixels[6], 0, 0, 0,2, 2, ps);
        thrusters[7] = new ThrustComponent(thrustersPixels[7], 0, 0, 0,1, 2, ps);

        checkOverlap = false;
        targetX = (float)Math.random() * 1.2f * (x/Math.abs(x));
        targetY = (float)Math.random() * 1.2f * (y/Math.abs(y));
        distX =
        baseSpeed = .004f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;

        float travelAngle = -(float)(Math.atan2(targetY - enemyBody.centerY, targetX - enemyBody.centerX));
        distX = -(float)(baseSpeed * Math.cos(travelAngle));
        distY = (float)(baseSpeed * Math.sin(travelAngle));
        rotate = (float)(Math.random()*.06 - .03);
        enemyBody.setEdgeColor(.9f, 0f, .9f);
    }

    public Pulser(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, float difficulty, float delay, GlobalInfo gI)
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

        guns = new GunComponent[4];
        guns[0] = new GunComponent(gunsPixels[0], Constants.pulseGunOffsets[2][0], Constants.pulseGunOffsets[2][1], 0, g, ps);
        guns[1] = new GunComponent(gunsPixels[1], Constants.pulseGunOffsets[1][0], Constants.pulseGunOffsets[1][1], 0, g.clone(), ps);
        guns[2] = new GunComponent(gunsPixels[2], Constants.pulseGunOffsets[0][0], Constants.pulseGunOffsets[0][1], 0, g.clone(), ps);
        guns[3] = new GunComponent(gunsPixels[3], Constants.pulseGunOffsets[3][0], Constants.pulseGunOffsets[3][1], 0, g.clone(), ps);
        hasGun = true;

        thrusters = new ThrustComponent[8];
        thrusters[0] = new ThrustComponent(thrustersPixels[0], 0, 0, 0,2, 2, ps);
        thrusters[1] = new ThrustComponent(thrustersPixels[1], 0, 0, 0,1, 2, ps);
        thrusters[2] = new ThrustComponent(thrustersPixels[2], 0, 0, 0,2, 2, ps);
        thrusters[3] = new ThrustComponent(thrustersPixels[3], 0, 0, 0,1, 2, ps);
        thrusters[4] = new ThrustComponent(thrustersPixels[4], 0, 0, 0,2, 2, ps);
        thrusters[5] = new ThrustComponent(thrustersPixels[5], 0, 0, 0,1, 2, ps);
        thrusters[6] = new ThrustComponent(thrustersPixels[6], 0, 0, 0,2, 2, ps);
        thrusters[7] = new ThrustComponent(thrustersPixels[7], 0, 0, 0,1, 2, ps);

        checkOverlap = false;
        targetX = (float)Math.random() * 1.2f * (x/Math.abs(x));
        targetY = (float)Math.random() * 1.2f * (y/Math.abs(y));
        distX =
                baseSpeed = .004f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;

        float travelAngle = -(float)(Math.atan2(targetY - enemyBody.centerY, targetX - enemyBody.centerX));
        distX = -(float)(baseSpeed * Math.cos(travelAngle));
        distY = (float)(baseSpeed * Math.sin(travelAngle));
        rotate = (float)(Math.random()*.06 - .03);
        enemyBody.setEdgeColor(.9f, 0f, .9f);
    }

    //public void move(float pX, float pY, long curFrame, float slow)
    @Override
    public void move(float pX, float pY)
    {
        if(spawned)
        {
            if(!inPosition)
            {
                if (Math.abs(enemyBody.centerX - targetX) <= baseSpeed &&
                        Math.abs(enemyBody.centerY - targetY) <= baseSpeed)
                {
                    inPosition = true;
                }

                x += -distX;
                y += -distY;
                enemyBody.move(-distX, -distY);
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

                //System.out.println(tRem);
                if(tRem < .9f && tRem > .1f)
                {
                    rotate((float) enemyBody.angle + rotate, .01f, globalInfo.timeSlow);
                }
                else if(tRem > .84f && tRem < .94f)
                {
                    if(rotate < 0)
                    {
                        addThrustParticles(thrustersPixels[3], 1, .05f, enemyBody, -(float)(enemyBody.angle - Math.PI));
                        addThrustParticles(thrustersPixels[1], 1, .05f, enemyBody, -(float)(enemyBody.angle - Math.PI/2));
                        addThrustParticles(thrustersPixels[7], 1, .05f, enemyBody, -(float)(enemyBody.angle));
                        addThrustParticles(thrustersPixels[5], 1, .05f, enemyBody, -(float)(enemyBody.angle - (Math.PI/ 2) * 3));
                    }
                    else
                    {
                        addThrustParticles(thrustersPixels[6], 1, .05f, enemyBody, -(float)(enemyBody.angle - Math.PI/2));
                        addThrustParticles(thrustersPixels[4], 1, .05f, enemyBody, -(float)(enemyBody.angle));
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
                            float ang = (float) enemyBody.angle + ((float) Math.PI / 2) * i;
                            guns[i].shoot(
                                    enemyBody.centerX,
                                    enemyBody.centerY,
                                    ang,
                                    globalInfo,
                                    enemyBody.cosA,
                                    enemyBody.sinA
                            );
                        }
                        guns[i].move(globalInfo.timeSlow);
                    }
                }
            }
        }
        else
        {
            if(globalInfo.getAugmentedTimeMillis() - levelStartTime > spawnDelay)
            {
                spawned = true;
            }
        }
    }


    public void rotate(float angleMoving, float rotateSpeed, float slow)
    {
        float delta = (float)enemyBody.angle - angleMoving;
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
                addThrustParticles(thrustersPixels[6], 1, .05f, enemyBody, -(float)(enemyBody.angle - Math.PI/2));
                addThrustParticles(thrustersPixels[4], 1, .05f, enemyBody, -(float)(enemyBody.angle));
                addThrustParticles(thrustersPixels[2], 1, .05f, enemyBody, -(float)(enemyBody.angle - (Math.PI/ 2) * 3));
                addThrustParticles(thrustersPixels[0], 1, .05f, enemyBody, -(float)(enemyBody.angle - Math.PI));
            }
            else
            {
                addThrustParticles(thrustersPixels[3], 1, .05f, enemyBody, -(float)(enemyBody.angle - Math.PI));
                addThrustParticles(thrustersPixels[1], 1, .05f, enemyBody, -(float)(enemyBody.angle - Math.PI/2));
                addThrustParticles(thrustersPixels[7], 1, .05f, enemyBody, -(float)(enemyBody.angle));
                addThrustParticles(thrustersPixels[5], 1, .05f, enemyBody, -(float)(enemyBody.angle - (Math.PI/ 2) * 3));
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
                            //-enemyBody.cosA, enemyBody.sinA,
                            (float)-enemyBody.angle + (float)Math.PI,
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

    public void addThrustParticles(Pixel[] pixels, float ratio, float dist, Collidable c, float ang)
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
