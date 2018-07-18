package com.example.sweet.game20.Objects;

import android.content.Context;
import android.graphics.PointF;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.R;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.DropFactory;
import com.example.sweet.game20.util.ImageParser;
import com.example.sweet.game20.util.ScreenShake;
import com.example.sweet.game20.util.VectorFunctions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Player extends Drawable
{
    public float
            xscale = 1,
            yscale = 1,
            baseSpeed,
            rotateSpeed = .05f,
            stageDirection = 1,
            tiltAngle = 0,
            driftDirection = 1,
            driftCount = -300,
            driftX,
            driftY,
            xBound,
            yBound;

    private volatile PointF panToward = new PointF(0,0);

    public volatile float
            xbound = 0,
            ybound = 0,
            xScreenShift = 0,
            yScreenShift = 0,
            cameraSpeed = .014f,
            cameraPanX = 0f,
            cameraPanY = 0f,
            cameraClamp = .16f,
            screenShakeX = 0,
            screenShakeY = 0;

    public volatile float
            movementOnDownX = 0f,
            movementOnMoveX = 0f,
            shootingOnDownX = 0f,
            shootingOnMoveX = 0f,
            movementOnDownY = 0f,
            movementOnMoveY = 0f,
            shootingOnDownY = 0f,
            shootingOnMoveY = 0f;

    public volatile boolean
            movementDown = false,
            shootingDown = false,
            modUpdate = false;

    public ArrayList<TimeSlowEvent> timeSlowEvents = new ArrayList<>();

    private int[] gravParticles = new int[]{
             6,  3,  7,  2,  8,  2,  9,  1, 10,  1, 11,  1, 12,  0, 13,  0, 14,  0, 15,  0, 16,  0, 17,
             0, 18,  0, 19,  0, 20,  0, 21,  1, 22,  1, 23,  1, 24,  2, 25,  3, 26,  3, 27,  4, 28,  5,
            29,  6, 29,  7, 30,  8, 30,  9, 31, 10, 31, 11, 30, 12, 29, 11, 28, 11, 27, 10, 26, 10,
            25, 9, 24, 9, 23, 9, 22, 9, 21, 9, 20, 9 , 19, 9, 18, 9, 17 ,9, 16, 10, 15, 10, 14, 10, 13, 10,
            12, 11, 11, 11, 10, 11, 9, 12, 8, 12, 7, 13, 6, 13, 5, 3, 4, 4 , 3, 4, 2, 5, 1, 6, 5, 14,
            0, 8, 1, 9, 1, 10, 1, 11, 2 ,12, 2, 13, 3, 14, 4 ,14,

             6, 28,  7, 29,  8, 29,  9, 30, 10, 30, 11, 30, 12, 31, 13, 31, 14, 31, 15, 31, 16, 31,
            17, 31, 18, 31, 19, 31, 20, 31, 21, 30, 22, 30, 23, 30, 24, 29, 25, 28, 26, 28, 27, 27,
            28, 26, 29, 25, 29, 24, 30, 23, 30, 22, 31, 21, 31, 20, 5, 28, 4, 27, 3, 27, 2 ,26, 1, 25,
            30, 19, 29, 20, 28, 20, 27, 21, 26, 21, 25, 22, 24 ,22, 23, 22, 22, 22, 21, 22, 20, 22, 19, 22, 18, 22, 17, 22,
            16, 21, 15, 21, 14, 21, 13, 21, 12, 20, 11, 20, 10, 20, 9, 19, 8, 19, 7, 18, 6, 18, 5, 17,
            0,24, 0, 23, 1, 22, 1, 21, 1 ,20, 2, 19, 2, 18, 3, 17, 4, 17};

    private int[] leftBoost = new int[]{0, 2, 0, 3};
    private int[] rightBoost = new int[]{0, 28, 0, 29};
    private int[] mainBoost = new int[]{0, 14, 0, 15, 0, 16, 0, 17, 1, 16, 1, 15};

    private int[] topLeft = new int[]{22, 5, 22, 6, 23, 5, 23, 6};

    private int[] bottomLeft = new int[]{8, 7, 9, 7, 8, 8, 9, 8};

    private int[] middle = new int[]{16, 15, 17, 15, 16, 16, 17, 16};

    private int[] topRight = new int[]{22, 25, 22, 26, 23, 25, 23, 26};

    private int[] bottomRight = new int[]{8, 23, 9, 23, 8, 24, 9, 24};

    private int
            tiltLoc,
            magLoc,
            stageCounter = 0;

    private PixelGroup playerBody;

    private static final float EXCHANGABLE_DROP_RANGE = .2f;

    private Pixel[]
            gravParticlePixels = new Pixel[gravParticles.length/2],

            leftBoostPixels = new Pixel[leftBoost.length/2],
            rightBoostPixels = new Pixel[rightBoost.length/2],
            mainBoostPixels = new Pixel[mainBoost.length/2],

            gunTopLeftPixels = new Pixel[topLeft.length/2],
            //gunBottomLeftPixels = new Pixel[bottomLeft.length/2],
            gunMiddlePixels = new Pixel[middle.length/2],
            //gunBottomRightPixels = new Pixel[bottomRight.length/2],
            gunTopRightPixels = new Pixel[topRight.length/2];

    /*  0: Main Thrust
        1: Left Thrust
        2: Right Thrust
     */
    public Drop[] thrusters = new Drop[3];

    //public GunComponent[] guns = new GunComponent[3];
    public Drop[] gunDrops = new Drop[3];
    public PointF[] gunOffsets = new PointF[]{
            new PointF(0, -.008f),
            new PointF(.056f, -.088f),
            new PointF(.056f, .072f)
    };
    //Number of guns 1-5
    private int maxGuns = 1;

    public Drop[] mods = new Drop[5];
    private int maxMods = 1;

    private String TILT = "tilt";

    public Drop[] consumableDrops = new Drop[Constants.DROPS_LENGTH];

    private int  consumableDropIndex = 0;

    public ArrayList<Drop> componentDrops = new ArrayList<>();

    public boolean pause = false;

    public ArrayList<ScreenShake>
            screenShakeEventsX = new ArrayList<>(),
            screenShakeEventsY = new ArrayList<>();

    public Player(DropFactory dF, Context context, float sp, int sL, ParticleSystem ps, PixelGroup body)
    {
        particleSystem = ps;
        baseSpeed = sp;
        playerBody = body;
        playerBody.knockBackFactor = .01f;
        playerBody.enableLocationChain = false;
        initParticleAttachments();

        gunDrops[0] = dF.getNewDrop(Constants.DropType.GUN,
                0,
                0,
                new GunComponent
                (
                        gunMiddlePixels,
                        playerBody.centerX,
                        playerBody.centerY,
                        (float)playerBody.angle,
                        new BasicGun
                                (
                                        ImageParser.parseImage(context, R.drawable.bullet, R.drawable.bullet_light, sL),
                                        particleSystem,
                                        100
                                ),
                        particleSystem
                )
        );
        gunDrops[0].held = true;
        componentDrops.add(gunDrops[0]);

        thrusters[1] = dF.getNewDrop(Constants.DropType.THRUSTER,
                0,
                0,
                new ThrustComponent(mainBoostPixels, 0, 0, 0, 0, 3, particleSystem)
        );
        thrusters[1].held = true;
        componentDrops.add(thrusters[1]);

        thrusters[0] = dF.getNewDrop(Constants.DropType.THRUSTER,
                0,
                0,
                new ThrustComponent(leftBoostPixels, 0, 0, 0, 0, 2, particleSystem)
        );
        thrusters[0].held = true;
        componentDrops.add(thrusters[0]);

        thrusters[2] = dF.getNewDrop(Constants.DropType.THRUSTER,
                0,
                0,
                new ThrustComponent(rightBoostPixels, 0, 0, 0, 0, 2, particleSystem)
        );
        thrusters[2].held = true;
        componentDrops.add(thrusters[2]);
        //thrusters[0] = new ThrustComponent(mainBoostPixels, 0, 0, 0, 0, 3, ps);
        //thrusters[1] = new ThrustComponent(leftBoostPixels, 0, 0, 0, 1, 2, ps);
        //thrusters[2] = new ThrustComponent(rightBoostPixels, 0, 0, 0, 2, 2, ps);

        tiltLoc = glGetUniformLocation(sL, TILT);
        magLoc = glGetUniformLocation(sL, "mag");
    }

    public void move(float mX, float mY, float slow)
    {
        float tempMagnitude = VectorFunctions.getMagnitude(mX, mY);
        float angleMoving = (float)(Math.atan2(mY, mX));
        float pow = 1;
        if(thrusters[1] != null)
        {
            pow = ((ThrustComponent) thrusters[1].component).thrustPower;
        }
        float distance = baseSpeed * pow * slow;
        float tempDistX = -(float)(distance * -Math.cos(angleMoving));
        float tempDistY = -(float)(distance * Math.sin(angleMoving));

        if(tempMagnitude>.15)
        {
            if(playerBody.getCenterX() + tempDistX < -xBound  || playerBody.getCenterX() + tempDistX > xBound)
                tempDistX = 0;
            if(playerBody.getCenterY() + tempDistY < -yBound || playerBody.getCenterY() + tempDistY > yBound)
                tempDistY = 0;
            playerBody.move(tempDistX, tempDistY);
            addThrustParticles(mainBoostPixels,1, .05f);
            rotate(angleMoving,1, slow);
        }
        else if(tempMagnitude <= .15 && tempMagnitude > .028)
        {
            float tempRatio = (float)((tempMagnitude - .028) / .122);
            float tempX = tempDistX * tempRatio;
            float tempY = tempDistY * tempRatio;
            if(playerBody.getCenterX() + tempX < -xBound || playerBody.getCenterX() + tempX > xBound)
                tempX = 0 ;
            if(playerBody.getCenterY() + tempY < -yBound || playerBody.getCenterY() + tempY > yBound)
                tempY = 0 ;
            playerBody.move(tempX,tempY);
            addThrustParticles(mainBoostPixels,tempRatio,.054f);
            rotate(angleMoving, tempRatio, slow);
        }
    }



    public void rotate(float angleMoving, float ratio, float slow)
    {
        float delta = (float)playerBody.angle - angleMoving;

        float sideSpd, sideSpd2;
        if(thrusters[0] != null)
        {
            sideSpd = rotateSpeed * ((ThrustComponent) thrusters[0].component).thrustPower * slow;
        }
        else
        {
            sideSpd = rotateSpeed * slow;
        }
        if(thrusters[2] != null)
        {
            sideSpd2 = -rotateSpeed * ((ThrustComponent)thrusters[2].component).thrustPower * slow;
        }
        else
        {
            sideSpd2 = -rotateSpeed * slow;
        }

        if (delta > sideSpd || delta < sideSpd2)
            {
            if (delta < -Math.PI || (delta > 0 && delta < Math.PI))
            {
                playerBody.angle += sideSpd2;
            }
            else
            {
                playerBody.angle += sideSpd;
            }
        }
        else
            playerBody.angle =  angleMoving;

        if (delta > .01 || delta < -.01)
        {
            if (delta < -Math.PI || (delta > 0 && delta < Math.PI))
            {
                addThrustParticles(leftBoostPixels, ratio, .042f);
            }
            else
            {
                addThrustParticles(rightBoostPixels, ratio, .042f);
            }
        }

        playerBody.rotate(playerBody.angle);

        if (playerBody.angle > Math.PI)
            playerBody.angle -= Constants.twoPI;
        else if (playerBody.angle < -Math.PI)
            playerBody.angle += Constants.twoPI;
    }

    public void addThrustParticles(Pixel[] pixels, float ratio, float dist)
    {
        float pow = 1;
        if(thrusters[1] != null)
        {
            pow = ((ThrustComponent) thrusters[1].component).thrustPower;
        }
        for(Pixel p: pixels)
        {
            if(p.live)
            {
                for (int t = 0; t < 4; t++)
                {
                    particleSystem.addParticle(p.xDisp + playerBody.centerX,
                            p.yDisp+ playerBody.centerY,
                            (float)(-playerBody.angle+Math.PI),
                            (float) (Math.random() * .2 + .8),
                            (float) (Math.random()),
                            0,
                            .7f,
                            (baseSpeed * pow * (float)(Math.random()*70+20)) * ratio,
                            dist * ratio * (float)Math.random()*2,
                            (float)(Math.random()*20)
                    );
                }
            }
        }
    }

    public void drift()
    {
        float tX = (float)Math.cos(driftCount/200*Constants.twoPI)/6000;
        float tY = (float)Math.sin(driftCount/200*Constants.twoPI)/6000;
        driftX += tX;
        driftY += tY;
        playerBody.move(tX, tY);
        if(driftCount <= -200)
        {
            driftDirection = 1;
        }else if(driftCount >= 200)
        {
            driftDirection = -1;
        }
        driftCount += driftDirection;
    }

    public void moveGuns(float slow)
    {
        if(getGuns()!= null)
        {
            for (Drop d: gunDrops)
            {
                if (d != null && d.component != null)
                {
                    ((GunComponent)d.component).gun.move(slow);
                }
            }
        }
    }

    public void addDrop(Drop d)
    {
        if(d.consumable)
        {
            consumableDrops[consumableDropIndex] = d;
            consumableDropIndex++;
        }
        else
        {
            componentDrops.add(d);
        }
    }

    public void consumableCollisionCheck()
    {
        for(Drop d: consumableDrops)
        {
            if(d!= null && d.live)
            {
                d.checkAlive();
                float dX = playerBody.centerX - d.x;
                float dY = playerBody.centerY - d.y;
                float distSquared = dX * dX + dY * dY;
                if(distSquared < Constants.PUll_DROP_RADIUS * Constants.PUll_DROP_RADIUS)
                {
                    if (distSquared < .0025f)
                    {
                        switch (d.type)
                        {
                            case HEALTH:
                                d.live = false;
                                revivePixels();
                                break;
                            case EXTRA_GUN:
                                d.live = false;
                                if(maxGuns < 3)
                                {
                                    maxGuns++;
                                }
                                else
                                {
                                }
                                break;
                            case EXTRA_MOD:
                                d.live = false;
                                if(maxMods < 5)
                                {
                                    maxMods++;
                                }
                                else
                                {
                                }
                                break;
                        }

                    }
                    else
                    {
                        float speed = .0001f / distSquared;
                        float dist = (float) Math.sqrt(distSquared);
                        d.move(speed * (dX / dist), speed * (dY / dist));
                    }
                }
            }
        }
    }

    public void preventDropOverlap()
    {
        for(Drop d: componentDrops)
        {
            for (Drop d2 : componentDrops)
            {
                if(d != d2)
                {
                    float cX = d.x - d2.x;
                    float cY = d.y - d2.y;
                    float radius = Constants.COMPONENT_DROP_RADIUS * 2;

                    if(cX == 0 || cY == 0)
                    {
                        d.move((float)Math.random()* Constants.COMPONENT_DROP_MOVESPEED ,
                                (float)Math.random() * Constants.COMPONENT_DROP_MOVESPEED);
                    }

                    if (cX * cX + cY * cY < radius * radius)
                    {
                        float angle = (float) Math.atan2(-cY, -cX);
                        d.move((float) -Math.cos(angle) * Constants.COMPONENT_DROP_MOVESPEED,
                                (float) -Math.sin(angle) * Constants.COMPONENT_DROP_MOVESPEED);
                    }
                }
            }
        }
    }

    /*public void addGravParticles()
    {
        for(Pixel p: gravParticlePixels)
        {
            if(p != null && p.live)
            {
                double angle = Math.atan2(p.yDisp, p.xDisp);
                float c = (float) Math.cos(-angle);
                float s = (float) Math.sin(-angle);

                float xShift = Constants.PIXEL_SIZE/2 * c + Constants.PIXEL_SIZE/2 *s ;
                float yShift = Constants.PIXEL_SIZE/2 * c - Constants.PIXEL_SIZE/2 *s;

                for (int t = 2; t < 4; t++)
                {
                    particleSystem.addParticle(p.xDisp + playerBody.centerX + xShift, p.yDisp + playerBody.centerY + yShift,
                            (float) Math.cos(angle), (float) Math.sin(angle),
                            .5f, .1f, .6f, .05f,
                            .006f * (t * t), .004f * (t * t), 10f
                    );
                }
            }
        }
    }*/
    public void applyPauseLength(double p)
    {
        for(Drop d: gunDrops)
        {
            if( d!= null && d.component != null)
            {
                //g.gun.applyPauseLength(p);
            }
        }
    }

    public Drop[] getExchangableComponentDrops()
    {
        Drop[] nearDrops = new Drop[8];
        int dropIndex = 0;
        for(Drop d: componentDrops)
        {
            if(d != null && d.held == false)
            {
                float dX = playerBody.centerX - d.x;
                float dY = playerBody.centerY - d.y;
                float distSquared = dX * dX + dY * dY;
                if (distSquared < EXCHANGABLE_DROP_RANGE * EXCHANGABLE_DROP_RANGE)
                {
                    d.held = true;
                    nearDrops[dropIndex] = d;
                    dropIndex++;
                    if(dropIndex >= 8)
                    {
                        break;
                    }
                }
            }
        }

        updatePlayerHeldDrops();

        return nearDrops;
    }

    public void updatePlayerHeldDrops()
    {
        for(Drop d: gunDrops)
        {
            if(d != null)
            {
                d.x = playerBody.centerX;
                d.y = playerBody.centerY;
                if(d.component != null)
                {
                    for(Drop m: mods)
                    {
                        if(m != null && m.component != null)
                        {
                            d = ((ModComponent)m.component).unmodifyGun(d);
                        }
                    }
                }
            }
        }
        for(Drop d: mods)
        {
            if(d != null)
            {
                d.x = playerBody.centerX;
                d.y = playerBody.centerY;
            }
        }
        for(Drop d: thrusters)
        {
            if(d != null)
            {
                d.x = playerBody.centerX;
                d.y = playerBody.centerY;
            }
        }
    }

    public void revivePixels()
    {
        HashSet<Pixel> affectedPixels = new HashSet<>();
        int resNum = 24;
        for(Pixel p: playerBody.pixels)
        {
            if(p.outside && p.live)
            {
                for(Pixel n: p.neighbors)
                {
                    if(n != null)
                    {
                        if (!n.live)
                        {
                            affectedPixels.add(p);
                            resNum = revivePixelHelper(n, resNum, affectedPixels);
                        }
                    }
                    if (resNum <= 0)
                    {
                        break;
                    }
                }
            }
            if(resNum <= 0)
            {
                break;
            }
        }

        for(Pixel p: affectedPixels)
        {
            p.outside = false;
            for(Pixel n: p.neighbors)
            {
                if((n != null && !n.live) || n == null)
                {
                    p.outside = true;
                }
            }
        }
        playerBody.needsUpdate = true;
    }

    private int revivePixelHelper(Pixel p, int rN, HashSet<Pixel> affectedPixels)
    {
        int resNum = rN;
        p.live = true;
        playerBody.numLivePixels++;
        affectedPixels.add(p);
        resNum--;
        for(Pixel n: p.neighbors)
        {
            if(n != null)
            {
                if (n.live && n.outside)
                {
                    affectedPixels.add(n);
                }

                if (!n.live && resNum > 0)
                {
                    resNum = revivePixelHelper(n, resNum, affectedPixels);
                }
            }
        }
        return resNum;
    }

    public void shoot(float sX, float sY, long curFrame, GlobalInfo gI)
    {
        float shootAngle = (float) (Math.atan2(sY, sX));
        int i = 0;
        for(Drop d: gunDrops)
        {
            if (d != null && d.component != null)
            {
                if(((GunComponent)d.component).gun.shoot(
                        playerBody.centerX + gunOffsets[i].x,
                        playerBody.centerY + gunOffsets[i].y,
                        shootAngle + (float) Math.PI,
                        curFrame,
                        gI.timeSlow))
                {
                    screenShakeEventsX.add(new ScreenShake(((GunComponent)d.component).gun.shakeMod,
                            240,
                            800,
                            gI
                    ));
                    screenShakeEventsY.add(new ScreenShake(((GunComponent)d.component).gun.shakeMod,
                            240,
                            800,
                             gI
                    ));
                    //timeSlowEvents.add(new TimeSlowEvent(.7f, 400));
                }
            }
            i++;
        }
    }

    @Override
    public void draw(double interpolation)
    {

        handleCosmetics();
        glUniform1f(magLoc, .7f);
        for(Drop d: consumableDrops)
        {
            if(d != null && d.live)
            {
                d.draw();
            }
        }

        Iterator<Drop> dItr = componentDrops.iterator();
        while(dItr.hasNext())
        {
            Drop d = dItr.next();
            if(d != null && d.live && !d.held)
            {
                d.draw();
                d.checkAlive();
            }
            if(d != null && !d.live)
            {
                d.freeMemory();
                dItr.remove();
            }
        }

        preventDropOverlap();

        glUniform1f(magLoc, 1f);
        glUniform1f(tiltLoc, tiltAngle);
        playerBody.draw();
        glUniform1f(tiltLoc, 0);
        for(Drop d: gunDrops)
        {
            if (d != null && ((GunComponent)d.component).gun != null)
            {
                ((GunComponent)d.component).gun.draw(interpolation);
            }
        }
    }

    @Override
    public void drawParticles()
    {
        particleSystem.draw();
    }

    public void handleCosmetics()
    {
        //if(!pause)
        //{
            if (stageCounter >= 120)
            {
                stageDirection = -1;
            }
            else if (stageCounter <= 0)
            {
                stageDirection = 1;
            }

            tiltAngle = (float) Math.sin(((double) stageCounter - 60) / 240);
            drift();

            stageCounter += stageDirection;
        //}
    }

    public void initParticleAttachments()
    {
        for(int i = 0; i < gravParticles.length; i += 2)
            gravParticlePixels[i/2] = playerBody.getpMap()[gravParticles[i+1]][gravParticles[i]];

        for(int i = 0; i < leftBoost.length; i += 2)
            leftBoostPixels[i/2] = playerBody.getpMap()[leftBoost[i+1]][leftBoost[i]];

        for(int i = 0; i < rightBoost.length; i += 2)
            rightBoostPixels[i/2] = playerBody.getpMap()[rightBoost[i+1]][rightBoost[i]];

        for(int i = 0; i < mainBoost.length; i += 2)
            mainBoostPixels[i/2] = playerBody.getpMap()[mainBoost[i+1]][mainBoost[i]];

        for(int i = 0; i < middle.length; i += 2)
            gunMiddlePixels[i/2] = playerBody.getpMap()[middle[i+1]][middle[i]];

        for(int i = 0; i < topRight.length; i += 2)
            gunTopRightPixels[i/2] = playerBody.getpMap()[topRight[i+1]][topRight[i]];

        for(int i = 0; i < topLeft.length; i += 2)
            gunTopLeftPixels[i/2] = playerBody.getpMap()[topLeft[i+1]][topLeft[i]];

        /*for(int i = 0; i < bottomLeft.length; i += 2)
            gunBottomLeftPixels[i/2] = playerBody.getpMap()[bottomLeft[i+1]][bottomLeft[i]];

        for(int i = 0; i < bottomRight.length; i += 2)
            gunBottomRightPixels[i/2] = playerBody.getpMap()[bottomRight[i+1]][bottomRight[i]];*/
    }

    public void setScale(float sX, float sY)
    {
        xscale = sX;
        yscale = sY;
        xBound = 1.5f + 1f / xscale;
        yBound = 1.5f + 1f / yscale;
    }
    public PixelGroup getPixelGroup()
    {
        return playerBody;
    }

    public float getSpeed()
    {
        return baseSpeed;
    }

    public Drop[] getGuns()
    {
        return gunDrops;
    }

    public int getMaxMods()
    {
        return maxMods;
    }

    public int getMaxGuns()
    {
        return maxGuns;
    }

    public void movePlayer(float slow)
    {
        if(movementOnDownX > .8f)
        {
            playerBody.resetPixels();
        }

        if(movementDown)
        {
            move(movementOnMoveX - movementOnDownX, movementOnMoveY - movementOnDownY, slow);
        }
    }

    public void shoot(long curFrame, GlobalInfo gI)
    {
        if(shootingDown)
        {
            float diffX = shootingOnMoveX - shootingOnDownX;
            float diffY = shootingOnMoveY - shootingOnDownY;
            float tempMagnitude = VectorFunctions.getMagnitude(diffX, diffY);

            if (tempMagnitude > .1)
            {
                shoot(diffX, diffY, curFrame, gI);
                panToward.set(cameraClamp * diffX / tempMagnitude, cameraClamp * diffY / tempMagnitude);
            }
            else
            {
                panToward.set(0, 0);
            }
        }
        else
        {
            panToward.set(0, 0);
        }

        moveGuns(gI.timeSlow);
    }

    public void moveCamera()
    {
        /*if (shootingDown)
        {
            float diffX = shootingOnMoveX - shootingOnDownX;
            float diffY = shootingOnMoveY - shootingOnDownY;
            float tempMagnitude = VectorFunctions.getMagnitude(diffX, diffY);
            if (tempMagnitude > .1)
            {
                panToward.set(cameraClamp * diffX / tempMagnitude, cameraClamp * diffY / tempMagnitude);
            }
            else
            {
                panToward.set(0, 0);
            }
        }
        else
        {
            panToward.set(0, 0);
        }*/

        float panAngle;
        float panDiffX = panToward.x - cameraPanX;
        float panDiffY = panToward.y - cameraPanY;
        float panMag = VectorFunctions.getMagnitude(panDiffX, panDiffY);

        if(panMag > .01)
        {
            panAngle = (float) Math.atan2(panToward.y - cameraPanY, panToward.x - cameraPanX);
            cameraPanX += cameraSpeed * Math.cos(panAngle);
            cameraPanY += cameraSpeed * Math.sin(panAngle);
        }
        else
        {
            cameraPanX = panToward.x;
            cameraPanY = panToward.y;
        }

        if(playerBody.centerX + cameraPanX < xbound &&
                playerBody.centerX + cameraPanX > -xbound)
        {
            xScreenShift = playerBody.centerX + cameraPanX;
        }
       /* else
        {
            xScreenShift -= screenShakeX;
        }*/

        if (playerBody.centerY - cameraPanY < ybound &&
                playerBody.centerY - cameraPanY > -ybound)
        {
            yScreenShift = playerBody.centerY - cameraPanY;
        }
        /*else
        {
            yScreenShift -= screenShakeY;
        }*/
    }

    public void handleScreenShake()
    {
        screenShakeX = 0;
        screenShakeY = 0;

        Iterator<ScreenShake> itrX = screenShakeEventsX.iterator();
        while(itrX.hasNext())
        {
            ScreenShake s = itrX.next();
            if(!s.live)
            {
                itrX.remove();
            }
            else
            {
                screenShakeX += s.getShake();
            }
        }

        Iterator<ScreenShake> itrY = screenShakeEventsY.iterator();
        while(itrY.hasNext())
        {
            ScreenShake s = itrY.next();
            if(!s.live)
            {
                itrY.remove();
            }
            else
            {
                screenShakeY += s.getShake();
            }
        }
        //xScreenShift += screenShakeX;
        //yScreenShift += screenShakeY;
    }
}