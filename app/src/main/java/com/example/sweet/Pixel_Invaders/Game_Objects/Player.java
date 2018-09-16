package com.example.sweet.Pixel_Invaders.Game_Objects;

import android.content.Context;
import android.graphics.PointF;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;

import com.example.sweet.Pixel_Invaders.Engine_Events.ScreenShakeEngine;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Drawable;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.R;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Resource_Readers.ImageParser;
import com.example.sweet.Pixel_Invaders.Util.Static.VectorFunctions;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Drop;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.GunComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ModComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.BasicGun;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;

import java.util.Arrays;

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

    public Rift rift;

    public int
            bonus = 1,
            score = 0;

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
            shootingDown = false;

    public float lastCollisionTime = 0;

    /*private int[] gravParticles = new int[]{
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
            0,24, 0, 23, 1, 22, 1, 21, 1 ,20, 2, 19, 2, 18, 3, 17, 4, 17};*/

    private int[] leftBoost = new int[]{0, 2, 0, 3};
    private int[] rightBoost = new int[]{0, 28, 0, 29};
    private int[] mainBoost = new int[]{0, 14, 0, 15, 0, 16, 0, 17, 1, 16, 1, 15};

    private int[] topLeft = new int[]{22, 5, 22, 6, 23, 5, 23, 6};

    //private int[] bottomLeft = new int[]{8, 7, 9, 7, 8, 8, 9, 8};

    private int[] middle = new int[]{16, 15, 17, 15, 16, 16, 17, 16};

    private int[] topRight = new int[]{22, 25, 22, 26, 23, 25, 23, 26};

    //private int[] bottomRight = new int[]{8, 23, 9, 23, 8, 24, 9, 24};

    private int
            tiltLoc,
            magLoc;

    private float
            stageCounter = 0;

    private PixelGroup playerBody;

    private static final float EXCHANGABLE_DROP_RANGE = .2f;

    private Pixel[]
            //gravParticlePixels = new Pixel[gravParticles.length/2],
            leftBoostPixels = new Pixel[leftBoost.length/2],
            rightBoostPixels = new Pixel[rightBoost.length/2],
            mainBoostPixels = new Pixel[mainBoost.length/2],
            gunTopLeftPixels = new Pixel[topLeft.length/2],
            gunMiddlePixels = new Pixel[middle.length/2],
            gunTopRightPixels = new Pixel[topRight.length/2];

    /*  0: Main Thrust
        1: Left Thrust
        2: Right Thrust
     */
    public Drop[] thrusters = new Drop[3];

    public Drop[] gunDrops = new Drop[3];
    private PointF[] gunOffsets = new PointF[]{
            new PointF(0, -.008f),
            new PointF(.056f, -.088f),
            new PointF(.056f, .072f)
    };
    //Number of guns 1-5
    private int maxGuns = 1;

    public Drop[] mods = new Drop[5];
    private int maxMods = 1;

    public Drop[] consumableDrops = new Drop[Constants.DROPS_LENGTH];

    private int  consumableDropIndex = 0;

    public Drop[] componentDrops = new Drop[200];
    private int dropIndex = 0;

    public boolean pause = false;

    private GlobalInfo globalInfo;
    public ScreenShakeEngine shakeEngine;

    private int[] affectedPixels;


    public Player(DropFactory dF, Context context, float sp, int sL, ParticleSystem ps, PixelGroup body, GlobalInfo gI)
    {
        particleSystem = ps;
        baseSpeed = sp;
        globalInfo = gI;
        rift = new Rift();

        playerBody = body;
        playerBody.setLoc(0,0);
        playerBody.setNeedsUpdate(true);
        playerBody.setEnableLocationChain(false);
        playerBody.knockBackFactor = .01f;
        playerBody.setRestorable(true);

        affectedPixels = new int[250];
        Arrays.fill(affectedPixels, -1);

        shakeEngine = new ScreenShakeEngine(gI, 200);
        initAttachmentPixels();


        gunDrops[0] = dF.getNewDrop(Constants.DropType.GUN,
                0,
                0,
                new GunComponent
                (
                        gunMiddlePixels,
                        gunOffsets[0].x,
                        gunOffsets[0].y,
                        0,
                        new BasicGun
                                (
                                        ImageParser.parseImage(context, R.drawable.longb, R.drawable.longb, sL, -1),
                                        particleSystem,
                                        200,
                                        .1f
                                )
                )
        );
        gunDrops[0].held = true;
        addComponenentDrop(gunDrops[0]);

        addComponenentDrop(dF.getNewDrop(Constants.DropType.GUN,
                0,
                0,
                new GunComponent
                        (
                                gunMiddlePixels,
                                gunOffsets[1].x,
                                gunOffsets[1].y,
                                0,
                                new BasicGun
                                        (
                                                ImageParser.parseImage(context, R.drawable.bullet, R.drawable.bullet2_light, sL, -1),
                                                particleSystem,
                                                400,
                                                .06f
                                        )
                        )
        ));

        addComponenentDrop(dF.getNewDrop(Constants.DropType.GUN,
                0,
                0,
                new GunComponent
                        (
                                gunMiddlePixels,
                                gunOffsets[1].x,
                                gunOffsets[1].y,
                                0,
                                new BasicGun
                                        (
                                                ImageParser.parseImage(context, R.drawable.bullet3, R.drawable.bullet3, sL, -1),
                                                particleSystem,
                                                600,
                                                .04f
                                        )
                        )
        ));

        thrusters[1] = dF.getNewDrop(Constants.DropType.THRUSTER,
                0,
                0,
                new ThrustComponent(mainBoostPixels, 0, 0, 0, 2)
        );
        thrusters[1].held = true;
        addComponenentDrop(thrusters[1]);

        thrusters[0] = dF.getNewDrop(Constants.DropType.THRUSTER,
                0,
                0,
                new ThrustComponent(leftBoostPixels, 0, 0, 0, 2)
        );
        thrusters[0].held = true;
        addComponenentDrop(thrusters[0]);

        thrusters[2] = dF.getNewDrop(Constants.DropType.THRUSTER,
                0,
                0,
                new ThrustComponent(rightBoostPixels, 0, 0, 0, 2)
        );
        thrusters[2].held = true;
        addComponenentDrop(thrusters[2]);

        tiltLoc = glGetUniformLocation(sL, "tilt");
        magLoc = glGetUniformLocation(sL, "mag");
    }

    public void move(float mX, float mY)
    {
        float tempMagnitude = VectorFunctions.getMagnitude(mX, mY);
        float angleMoving = (float)(Math.atan2(mY, mX));
        float pow = 1;
        if(thrusters[1] != null)
        {
            pow = ((ThrustComponent) thrusters[1].component).getThrustPower();
        }
        float slow = globalInfo.timeSlow + (1-playerBody.slowResist) * (1 - globalInfo.timeSlow);
        float distance = baseSpeed * pow * slow;
        float tempDistX = -(float)(distance * -Math.cos(angleMoving));
        float tempDistY = -(float)(distance * Math.sin(angleMoving));

        if(tempMagnitude>.15)
        {
            if(playerBody.getCenterX() + tempDistX < -xBound  || playerBody.getCenterX() + tempDistX > xBound)
            {
                tempDistX = 0;
            }
            if(playerBody.getCenterY() + tempDistY < -yBound || playerBody.getCenterY() + tempDistY > yBound)
            {
                tempDistY = 0;
            }
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
            {
                tempX = 0;
            }
            if(playerBody.getCenterY() + tempY < -yBound || playerBody.getCenterY() + tempY > yBound)
            {
                tempY = 0;
            }
            playerBody.move(tempX,tempY);
            addThrustParticles(mainBoostPixels,tempRatio,.054f);
            rotate(angleMoving, tempRatio, slow);
        }

    }

    public void rotate(float angleMoving, float ratio, float slow)
    {
        float delta = playerBody.angle - angleMoving;

        float sideSpd, sideSpd2;
        if(thrusters[2] != null)
        {
            sideSpd = rotateSpeed * ((ThrustComponent) thrusters[2].component).getThrustPower() * slow;
        }
        else
        {
            sideSpd = rotateSpeed * slow;
        }
        if(thrusters[0] != null)
        {
            sideSpd2 = -rotateSpeed * ((ThrustComponent)thrusters[0].component).getThrustPower() * slow;
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
        {
            playerBody.angle = angleMoving;
        }

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
        {
            playerBody.angle -= Constants.twoPI;
        }
        else if (playerBody.angle < -Math.PI)
        {
            playerBody.angle += Constants.twoPI;
        }
    }

    private void addThrustParticles(Pixel[] pixels, float ratio, float dist)
    {
        float pow = 1;
        if(thrusters[1] != null)
        {
            pow = ((ThrustComponent) thrusters[1].component).getThrustPower();
        }
        for(Pixel p: pixels)
        {
            //if(p.live)
            if(p.state >= 1)
            {
                float tX = playerBody.infoMap[p.row][p.col].xOriginal * playerBody.cosA +
                        playerBody.infoMap[p.row][p.col].yOriginal * playerBody.sinA;
                float tY = playerBody.infoMap[p.row][p.col].yOriginal * playerBody.cosA -
                        playerBody.infoMap[p.row][p.col].xOriginal * playerBody.sinA;
                for (int t = 0; t < 4; t++)
                {
                    particleSystem.addParticle(tX + playerBody.getCenterX(),
                            tY + playerBody.getCenterY(),
                            (float)(-playerBody.angle + Math.PI),
                            (float) (Math.random() * .2 + .8),
                            (float) (Math.random()),
                            0,
                            .7f,
                            (baseSpeed * pow * (float)(Math.random() * 70 + 20)) * ratio,
                            dist * ratio * (float)Math.random() * 2,
                            (float)(Math.random()*20)
                    );
                }
            }
        }
    }

    private void drift(float slow)
    {
        float tX = (float)Math.cos(driftCount/200*Constants.twoPI)/6000;
        float tY = (float)Math.sin(driftCount/200*Constants.twoPI)/6000;
        driftX += tX;
        driftY += tY;
        playerBody.move(tX, tY);
        if(driftCount <= -200)
        {
            driftDirection = 1;
        }
        else if(driftCount >= 200)
        {
            driftDirection = -1;
        }
        driftCount += driftDirection * slow;
    }

    private void moveGuns()
    {
        float slow = globalInfo.timeSlow + (1-playerBody.slowResist) * (1 - globalInfo.timeSlow);
        if(getGuns()!= null)
        {
            for (Drop d: gunDrops)
            {
                if (d != null && d.component != null)
                {
                    ((GunComponent)d.component).gun.move(globalInfo, slow);
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
            if(consumableDropIndex >= 200)
            {
                consumableDropIndex = 0;
            }
        }
        else
        {
            addComponenentDrop(d);
        }
    }

    public void consumableCollisionCheck()
    {
        for(Drop d: consumableDrops)
        {
            if(d!= null && d.live)
            {
                float dX = playerBody.getCenterX() - d.x;
                float dY = playerBody.getCenterY() - d.y;
                float distSquared = dX * dX + dY * dY;
                if(distSquared < Constants.PUll_DROP_RADIUS * Constants.PUll_DROP_RADIUS)
                {
                    if (distSquared < .0025f)
                    {
                        switch (d.type)
                        {
                            case HEALTH:
                                d.live = false;
                                playerBody.revivePixels(20);
                                checkComponents();
                                break;
                            case EXTRA_GUN:
                                d.live = false;
                                if(maxGuns < 3)
                                {
                                    maxGuns++;
                                }
                                else
                                {
                                    score += 10000 * bonus;
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
                                    score += 10000 * bonus;
                                }
                                break;
                        }
                    }
                    else
                    {
                        d.inPullRange = true;
                    }
                }
                else
                {
                    d.inPullRange = false;
                }
            }
        }
    }

    /*public void moveConsumables()
    {
        for(Drop d: consumableDrops)
        {
            if(d!= null && d.live && d.inPullRange)
            {
                //d.checkAlive();
                float dX = playerBody.getCenterX() - d.x;
                float dY = playerBody.getCenterY() - d.y;
                float distSquared = dX * dX + dY * dY;
                if(distSquared >= .0025)
                {
                    float speed = .0004f / distSquared;
                    float dist = (float) Math.sqrt(distSquared);
                    d.move(speed * (dX / dist), speed * (dY / dist));
                }
            }
        }
    }*/

    public void handleDrops()
    {
        consumableCollisionCheck();
        for(Drop d: consumableDrops)
        {
            if(d!= null && d.live)
            {
                //d.checkAlive();
                if(d.inPullRange)
                {
                    float dX = playerBody.getCenterX() - d.x;
                    float dY = playerBody.getCenterY() - d.y;
                    float distSquared = dX * dX + dY * dY;
                    if (distSquared >= .0025)
                    {
                        float speed = .0004f / distSquared;
                        float dist = (float) Math.sqrt(distSquared);
                        d.move(speed * (dX / dist), speed * (dY / dist));
                    }
                }
                d.checkOnScreen(globalInfo);
            }
        }
        for(Drop d: componentDrops)
        {
            if(d != null)
            {
                for (Drop d2 : componentDrops)
                {
                    if (d2 != null && d != d2)
                    {
                        float cX = d.x - d2.x;
                        float cY = d.y - d2.y;
                        float radius = Constants.COMPONENT_DROP_RADIUS * 2;
                        float dist = VectorFunctions.getMagnitude(cX, cY);

                        if (cX == 0 && cY == 0)
                        {
                            d.move((float) Math.random() * Constants.COMPONENT_DROP_MOVESPEED,
                                    (float) Math.random() * Constants.COMPONENT_DROP_MOVESPEED);
                        }
                        else if (dist < radius)
                        {
                            d.move((cX/dist) * Constants.COMPONENT_DROP_MOVESPEED,
                                    (cY/dist) * Constants.COMPONENT_DROP_MOVESPEED);
                            d2.move((-cX/dist) * Constants.COMPONENT_DROP_MOVESPEED,
                                    (-cY/dist) * Constants.COMPONENT_DROP_MOVESPEED);
                        }
                    }
                }
                d.checkOnScreen(globalInfo);
            }
        }
        preventDropOverlap();
    }

    /*public void checkDropsOnScreen()
    {
        for(Drop d: consumableDrops)
        {
            if(d != null)
            {
                d.checkOnScreen(globalInfo);
            }
        }
        for(Drop d: componentDrops)
        {
            if(d != null)
            {
                d.checkOnScreen(globalInfo);
            }
        }
    }*/

    private void checkComponents()
    {
        for (Drop gCD : gunDrops)
        {
            if (gCD != null && gCD.component != null)
            {
                gCD.component.checkAlive();
            }
        }

        for (Drop tCD : thrusters)
        {
            if (tCD != null && tCD.component != null)
            {
                tCD.component.checkAlive();
            }
        }
    }

    public void collisionOccured(int numKilled)
    {
        score += numKilled * bonus;
        if(playerBody.gotHit)
        {
            playerBody.gotHit = false;
            checkComponents();
            lastCollisionTime = globalInfo.getAugmentedTimeSeconds();
            bonus = 1;
        }
    }

    private void preventDropOverlap()
    {
        for(Drop d: componentDrops)
        {
            if(d != null)
            {
                for (Drop d2 : componentDrops)
                {
                    if (d2 != null && d != d2)
                    {
                        float cX = d.x - d2.x;
                        float cY = d.y - d2.y;
                        float radius = Constants.COMPONENT_DROP_RADIUS * 2;
                        float dist = VectorFunctions.getMagnitude(cX, cY);

                        if (cX == 0 && cY == 0)
                        {
                            d.move((float) Math.random() * Constants.COMPONENT_DROP_MOVESPEED,
                                    (float) Math.random() * Constants.COMPONENT_DROP_MOVESPEED);
                        }
                        else if (dist < radius)
                        {
                            d.move((cX/dist) * Constants.COMPONENT_DROP_MOVESPEED,
                                     (cY/dist) * Constants.COMPONENT_DROP_MOVESPEED);
                            d2.move((-cX/dist) * Constants.COMPONENT_DROP_MOVESPEED,
                                    (-cY/dist) * Constants.COMPONENT_DROP_MOVESPEED);
                        }
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
                    particleSystem.addParticle(
                            p.xDisp + playerBody.centerX + xShift,
                            p.yDisp + playerBody.centerY + yShift,
                            (float) Math.cos(angle),
                            (float) Math.sin(angle),
                            .5f, .1f, .6f, .05f,
                            .006f * (t * t), .004f * (t * t), 10f
                    );
                }
            }
        }
    }*/

    public Drop[] getExchangableComponentDrops()
    {
        Drop[] nearDrops = new Drop[8];
        int dropIndex = 0;
        for(Drop d: componentDrops)
        {
            if(d != null && !d.held)
            {
                float dX = playerBody.getCenterX() - d.x;
                float dY = playerBody.getCenterY() - d.y;
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

    private void updatePlayerHeldDrops()
    {
        for(Drop d: gunDrops)
        {
            if(d != null)
            {
                d.x = playerBody.getCenterX();
                d.y = playerBody.getCenterY();
            }
        }
        for(Drop d: mods)
        {
            if(d != null)
            {
                ((ModComponent)d.component).unmodify(gunDrops, playerBody);
                d.x = playerBody.getCenterX();
                d.y = playerBody.getCenterY();
            }
        }
        for(Drop d: thrusters)
        {
            if(d != null)
            {
                d.x = playerBody.getCenterX();
                d.y = playerBody.getCenterY();
            }
        }
    }

    public void applyMods()
    {
        playerBody.regen = false;
        playerBody.temporal = false;
        for(Drop d: mods)
        {
            if(d != null)
            {
                ((ModComponent)d.component).modify(gunDrops, playerBody);
                d.x = playerBody.getCenterX();
                d.y = playerBody.getCenterY();
            }
        }
    }

    @Override
    public void draw(double interpolation)
    {
        //handleCosmetics();
        glUniform1f(magLoc, .8f);
        for(Drop d: componentDrops)
        {
            if(d != null && d.onScreen && !d.held)
            {
                d.draw();
            }
        }
        for(Drop d: consumableDrops)
        {
            if(d != null && d.live && d.onScreen)
            {
                d.draw();
            }
        }
        glUniform1f(magLoc, 1f);

        for(Drop d: gunDrops)
        {
            if (d != null && ((GunComponent)d.component).gun != null)
            {
                ((GunComponent)d.component).gun.draw(interpolation);
            }
        }
        glUniform1f(tiltLoc, tiltAngle);
        playerBody.draw();
        glUniform1f(tiltLoc, 0);
    }

    public void handleCosmetics(float slow)
    {

        if (stageCounter >= 120)
        {
            stageDirection = -1;
        }
        else if (stageCounter <= 0)
        {
            stageDirection = 1;
        }

        tiltAngle = (float) Math.sin((stageCounter - 60) / 200);
        drift(slow);

        stageCounter += stageDirection * slow;
    }

    private void initAttachmentPixels()
    {
        /*for(int i = 0; i < gravParticles.length; i += 2)
        {
            gravParticlePixels[i / 2] = playerBody.getpMap()[gravParticles[i + 1] + 1][gravParticles[i] + 1];
        }*/
        float tempCX = 0;
        float tempCY = 0;
        for(int i = 0; i < leftBoost.length; i += 2)
        {
            leftBoostPixels[i / 2] = playerBody.getpMap()[leftBoost[i + 1] + 1][leftBoost[i] + 1];
        }

        for(int i = 0; i < rightBoost.length; i += 2)
        {
            rightBoostPixels[i / 2] = playerBody.getpMap()[rightBoost[i + 1] + 1][rightBoost[i] + 1];
        }

        for(int i = 0; i < mainBoost.length; i += 2)
        {
            mainBoostPixels[i / 2] = playerBody.getpMap()[mainBoost[i + 1] + 1][mainBoost[i] + 1];
        }

        for(int i = 0; i < middle.length; i += 2)
        {
            gunMiddlePixels[i / 2] = playerBody.getpMap()[middle[i + 1] + 1][middle[i] + 1];
            tempCX += playerBody.infoMap[gunMiddlePixels[i / 2].row][gunMiddlePixels[i / 2].col].xOriginal;
            tempCY += playerBody.infoMap[gunMiddlePixels[i / 2].row][gunMiddlePixels[i / 2].col].yOriginal;
        }
        gunOffsets[0].set(tempCX / middle.length, tempCY / middle.length);
        tempCX = 0;
        tempCY = 0;

        for(int i = 0; i < topRight.length; i += 2)
        {
            gunTopRightPixels[i / 2] = playerBody.getpMap()[topRight[i + 1] + 1][topRight[i] + 1];
            tempCX += playerBody.infoMap[gunTopRightPixels[i / 2].row][gunTopRightPixels[i / 2].col].xOriginal;
            tempCY += playerBody.infoMap[gunTopRightPixels[i / 2].row][gunTopRightPixels[i / 2].col].yOriginal;
        }
        gunOffsets[2].set(tempCX / topRight.length, tempCY / topRight.length);
        tempCX = 0;
        tempCY = 0;

        for(int i = 0; i < topLeft.length; i += 2)
        {
            gunTopLeftPixels[i / 2] = playerBody.getpMap()[topLeft[i + 1] + 1][topLeft[i] + 1];
            tempCX += playerBody.infoMap[gunTopLeftPixels[i / 2].row][gunTopLeftPixels[i / 2].col].xOriginal;
            tempCY += playerBody.infoMap[gunTopLeftPixels[i / 2].row][gunTopLeftPixels[i / 2].col].yOriginal;
        }
        gunOffsets[1].set(tempCX / topLeft.length, tempCY / topLeft.length);

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

    public void setGun(Drop d, int ind)
    {
        if(d != null)
        {
            switch (ind)
            {
                case 0:
                    d.component.setAttachmentPixels(gunMiddlePixels);
                    break;
                case 1:
                    d.component.setAttachmentPixels(gunTopLeftPixels);
                    break;
                case 2:
                    d.component.setAttachmentPixels(gunTopRightPixels);
                    break;
            }
            ((GunComponent)d.component).setGunOffset(gunOffsets[ind].x, gunOffsets[ind].y);
            d.component.checkAlive();
        }
        gunDrops[ind] = d;
    }

    public void setThruster(Drop d, int ind)
    {
        if(d != null)
        {
            switch(ind)
            {
                case 0:
                    d.component.setAttachmentPixels(leftBoostPixels);
                    break;
                case 1:
                    d.component.setAttachmentPixels(mainBoostPixels);
                    break;
                case 2:
                    d.component.setAttachmentPixels(rightBoostPixels);
                    break;
            }
            d.component.checkAlive();
        }
        thrusters[ind] = d;
    }


    public int getMaxMods()
    {
        return maxMods;
    }

    public int getMaxGuns()
    {
        return maxGuns;
    }

    public void movePlayer()
    {
        if(movementDown)
        {
            move(movementOnMoveX - movementOnDownX, movementOnMoveY - movementOnDownY);
        }
        rift.addParticles(particleSystem);
        handleCosmetics(globalInfo.timeSlow);
        moveCamera();
    }

    public void checkRegen()
    {
        if(playerBody.regen)
        {
            if(globalInfo.getAugmentedTimeSeconds() - playerBody.lastRegenTime > playerBody.regenDelay)
            {
                playerBody.revivePixels(10);
                playerBody.lastRegenTime = globalInfo.getAugmentedTimeSeconds();
            }
        }
    }

    public void shoot()
    {
        if(shootingDown)
        {
            float diffX = shootingOnMoveX - shootingOnDownX;
            float diffY = shootingOnMoveY - shootingOnDownY;
            float tempMagnitude = VectorFunctions.getMagnitude(diffX, diffY);

            if (tempMagnitude > .1)
            {
                float shootAngle = (float) (Math.atan2(diffY, diffX));
                for(Drop d: gunDrops)
                {
                    if (d != null && d.component != null)
                    {
                        if(((GunComponent)d.component).shoot(
                            playerBody.getCenterX(),
                            playerBody.getCenterY(),
                            shootAngle + (float) Math.PI,
                            globalInfo,
                            playerBody.cosA,
                            playerBody.sinA))
                        {
                            shakeEngine.addShake(
                                    ((GunComponent)d.component).gun.shakeMod,
                                    240,
                                    800
                            );
                        }
                    }
                }
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

        moveGuns();
    }

    private void moveCamera()
    {
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

        if(playerBody.getCenterX() + cameraPanX < xbound &&
                playerBody.getCenterX() + cameraPanX > -xbound)
        {
            xScreenShift = playerBody.getCenterX() + cameraPanX;
        }

        if (playerBody.getCenterY() - cameraPanY < ybound &&
                playerBody.getCenterY() - cameraPanY > -ybound)
        {
            yScreenShift = playerBody.getCenterY() - cameraPanY;
        }
    }

    public void handleScreenShake(float dampening)
    {
        screenShakeX = shakeEngine.getShakeX() * dampening;
        screenShakeY = shakeEngine.getShakeY() * dampening;
    }

    private void addComponenentDrop(Drop d)
    {
        while(componentDrops[dropIndex] != null && componentDrops[dropIndex].held)
        {
            dropIndex++;
            if(dropIndex >= componentDrops.length)
            {
                dropIndex = 0;
            }
        }
        componentDrops[dropIndex] = d;
        dropIndex++;
        if(dropIndex >= componentDrops.length)
        {
            dropIndex = 0;
        }
        /*double lowTimeLeft = 120000;
        int lowTimeInd = 0;

        for(int i = 0; i < 100; i++)
        {
            if(componentDrops[i] == null)
            {
                componentDrops[i] = d;
                break;
            }
            else if(!componentDrops[i].live)
            {
                componentDrops[i].freeMemory();
                componentDrops[i] = d;
                break;
            }
            else
            {
                //double t = componentDrops[i].checkAlive();
                if(!componentDrops[i].live)
                {
                    componentDrops[i].freeMemory();
                    componentDrops[i] = d;
                    break;
                }
                else if(t < lowTimeLeft && !componentDrops[i].held)
                {
                    lowTimeLeft = t;
                    lowTimeInd = i;
                }
            }
        }

        componentDrops[lowTimeInd].freeMemory();
        componentDrops[lowTimeInd] = d;*/
    }

    public void publishLocation(long frame)
    {
        if(playerBody.getEnableLocationChain())
        {
            playerBody.publishLocation(frame);
        }
        if(getGuns()!= null)
        {
            for (Drop d: gunDrops)
            {
                if (d != null && d.component != null)
                {
                    ((GunComponent)d.component).gun.publishLocation(frame);
                }
            }
        }
    }

    public void resetDrops()
    {
        Arrays.fill(consumableDrops,null);
        for(int i = 0; i < componentDrops.length; i++)
        {
            if(componentDrops[i] != null && !componentDrops[i].held)
            {
                componentDrops[i].freeMemory();
                componentDrops[i] = null;
            }
        }
    }

}