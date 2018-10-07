package com.example.sweet.Pixel_Invaders.Game_Objects;

import android.content.Context;
import android.graphics.PointF;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;

import com.example.sweet.Pixel_Invaders.Engine_Events.ScreenShakeEngine;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Collidable;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Drawable;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelInfo;
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
            yBound,
            slowResist;

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

    public float
            movementOnDownX = 0f,
            movementOnMoveX = 0f,
            shootingOnDownX = 0f,
            shootingOnMoveX = 0f,
            movementOnDownY = 0f,
            movementOnMoveY = 0f,
            shootingOnDownY = 0f,
            shootingOnMoveY = 0f;

    public boolean
            movementDown = false,
            shootingDown = false;

    public float lastCollisionTime = 0;

    private int[] leftBoost = new int[]{0, 2, 0, 3};
    private int[] rightBoost = new int[]{0, 28, 0, 29};
    private int[] mainBoost = new int[]{0, 14, 0, 15, 0, 16, 0, 17, 1, 16, 1, 15};

    private int[] topLeft = new int[]{22, 5, 22, 6, 23, 5, 23, 6};

    private int[] middle = new int[]{16, 15, 17, 15, 16, 16, 17, 16};

    private int[] topRight = new int[]{22, 25, 22, 26, 23, 25, 23, 26};

    private int
            tiltLoc,
            magLoc;

    private float
            stageCounter = 0;

    private PixelGroup playerBody, bonusBar;

    public ParticleSystem staticParticles;

    private static final float EXCHANGABLE_DROP_RANGE = .2f;

    private Pixel[]
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

    public Player(DropFactory dF, Context context, float sp, int sL, ParticleSystem ps, ParticleSystem sP, PixelGroup body, GlobalInfo gI)
    {
        particleSystem = ps;
        staticParticles = sP;
        baseSpeed = sp;
        globalInfo = gI;
        rift = new Rift(ps, globalInfo);

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

        bonusBar = ImageParser.parseImage(context, R.drawable.bonusbargroup, R.drawable.bonusbargroup, sL,0);
        bonusBar.setRestorable(true);
        bonusBar.setEnableLocationChain(false);
        bonusBar.setLoc(.922f, .003f);
        int i = 0;
        for(PixelInfo[] pIA: bonusBar.infoMap)
        {
            if(i < bonusBar.infoMap.length - 2)
            {
                for (PixelInfo pI : pIA)
                {
                    if(pI != null)
                    {
                        pI.originalState = 0;
                    }
                }
            }
            i++;
        }
        bonusBar.resetPixels();

        gunDrops[0] = dF.getNewDrop(Constants.DropType.GUN,
                0,
                0,
                new GunComponent
                (
                        playerBody,
                        gunOffsets[0].x,
                        gunOffsets[0].y,
                        0,
                        new BasicGun
                                (
                                        ImageParser.parseImage(context, R.drawable.longb, R.drawable.longb, sL, -1),
                                        particleSystem,
                                        50,
                                        .1f
                                ),
                        middle
                )
        );
        gunDrops[0].held = true;
        addComponenentDrop(gunDrops[0]);

        addComponenentDrop(dF.getNewDrop(Constants.DropType.GUN,
                0,
                0,
                new GunComponent
                        (
                                playerBody,
                                gunOffsets[1].x,
                                gunOffsets[1].y,
                                0,
                                new BasicGun
                                        (
                                                ImageParser.parseImage(context, R.drawable.bullet, R.drawable.bullet2_light, sL, -1),
                                                particleSystem,
                                                400,
                                                .06f
                                        ),
                                middle
                        )
        ));

        addComponenentDrop(dF.getNewDrop(Constants.DropType.GUN,
                0,
                0,
                new GunComponent
                        (
                                playerBody,
                                gunOffsets[1].x,
                                gunOffsets[1].y,
                                0,
                                new BasicGun
                                        (
                                                ImageParser.parseImage(context, R.drawable.bullet3, R.drawable.bullet3, sL, -1),
                                                particleSystem,
                                                600,
                                                .04f
                                        ),
                                middle
                        )
        ));

        thrusters[1] = dF.getNewDrop(Constants.DropType.THRUSTER,
                0,
                0,
                new ThrustComponent(playerBody, 2, mainBoost)
        );
        thrusters[1].held = true;
        addComponenentDrop(thrusters[1]);

        thrusters[0] = dF.getNewDrop(Constants.DropType.THRUSTER,
                0,
                0,
                new ThrustComponent(playerBody, 2, leftBoost)
        );
        thrusters[0].held = true;
        addComponenentDrop(thrusters[0]);

        thrusters[2] = dF.getNewDrop(Constants.DropType.THRUSTER,
                0,
                0,
                new ThrustComponent(playerBody, 2, rightBoost)
        );
        thrusters[2].held = true;
        addComponenentDrop(thrusters[2]);

        tiltLoc = glGetUniformLocation(sL, "tilt");
        magLoc = glGetUniformLocation(sL, "mag");
    }

    public void update()
    {
        handleDrops();
        shoot();
        handleScreenShake(globalInfo.gameSettings.screenShakePercent);
        checkRegen();
        globalInfo.screenShiftX = xScreenShift - screenShakeX;
        globalInfo.screenShiftY = yScreenShift - screenShakeY;
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
        float slow = globalInfo.timeSlow + slowResist * (1 - globalInfo.timeSlow);
        float distance = baseSpeed * pow * slow;
        float tempDistX = -(float)(distance * -Math.cos(angleMoving));
        float tempDistY = -(float)(distance * Math.sin(angleMoving));

        if(tempMagnitude> .15 * globalInfo.gameSettings.joyStickSize)
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
        else if(tempMagnitude <= .15 * globalInfo.gameSettings.joyStickSize &&
                tempMagnitude > .028 * globalInfo.gameSettings.joyStickSize)
        {
            float tempRatio = (float)((tempMagnitude - .028 * globalInfo.gameSettings.joyStickSize) /
                    (.13f * globalInfo.gameSettings.joyStickSize));
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
        float slow = globalInfo.timeSlow + slowResist * (1 - globalInfo.timeSlow);
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

    private void handleDrops()
    {
        consumableCollisionCheck();
        for(Drop d: consumableDrops)
        {
            if(d!= null && d.live)
            {
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
        if(numKilled > 0)
        {
            bonusBar.revivePixels((numKilled / 10 + 1) * 5);
            score += numKilled * bonus;
        }
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
        slowResist = 1;
        rift.resetFreqMod();
        for(Drop d: mods)
        {
            if(d != null)
            {
                if(d.component.type == Constants.DropType.TEMPORAL)
                {
                    float val = ((ModComponent)d.component).getModValue();
                    rift.reduceFreqMod(val);
                    slowResist *= 1 / val;
                }
                ((ModComponent)d.component).modify(gunDrops, playerBody);
                d.x = playerBody.getCenterX();
                d.y = playerBody.getCenterY();
            }
        }
        slowResist = 1 - slowResist;
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

    public void drawBonusBar()
    {
        bonusBar.draw();
    }

    private void handleCosmetics(float slow)
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
            if(!globalInfo.gameSettings.staticJoysticks)
            {
                move(movementOnMoveX - movementOnDownX, movementOnMoveY - movementOnDownY);
            }
            else
            {
                move(movementOnMoveX - Constants.staticMoveLocX, movementOnMoveY - Constants.staticMoveLocY);
            }
        }

        if(bonusBar.numLivePixels >= bonusBar.totalPixels)
        {
            destroyCollidableAnimation(bonusBar, staticParticles);
            bonus++;
            bonusBar.resetPixels();
        }

        handleCosmetics(globalInfo.timeSlow + slowResist * (1 - globalInfo.timeSlow));
        moveCamera();
    }

    private void checkRegen()
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
            float diffX;
            float diffY;
            if(!globalInfo.gameSettings.staticJoysticks)
            {
                diffX = shootingOnMoveX - shootingOnDownX;
                diffY = shootingOnMoveY - shootingOnDownY;
            }
            else
            {
                diffX = shootingOnMoveX - Constants.staticShootLocX;
                diffY = shootingOnMoveY - Constants.staticShootLocY;
            }

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
                            playerBody.sinA,
                            slowResist
                        ))
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

    public void destroyCollidableAnimation(Collidable c, ParticleSystem pS)
    {
        for (Pixel p : c.getPixels())
        {
            if(p.state >= 1)
            {
                p.xDisp = c.infoMap[p.row][p.col].xOriginal * c.cosA + c.infoMap[p.row][p.col].yOriginal * c.sinA;
                p.yDisp = c.infoMap[p.row][p.col].yOriginal * c.cosA - c.infoMap[p.row][p.col].xOriginal * c.sinA;
                addParticleHelper(p, c, pS);
                c.killPixel(p);
                c.numLivePixels--;
                c.pixelsKilled++;
            }
        }
    }

    private void addParticleHelper(Pixel p, Collidable c, ParticleSystem pS)
    {
        float angle = (float)(Math.atan2(p.yDisp, p.xDisp) + Math.random() * .2 - .1);
        pS.addParticle(
                p.xDisp + c.getCenterX(),
                p.yDisp + c.getCenterY(),
                angle,
                c.infoMap[p.row][p.col].r,
                c.infoMap[p.row][p.col].g,
                c.infoMap[p.row][p.col].b,
                .8f,
                (float)(Math.random())+.1f,
                (float)(Math.random()*.5)+.01f,
                (float)(Math.random()*40)-20
        );
    }

    public void addParticleToCenter()
    {
        float angle = (float)Math.random() * Constants.twoPI;
        float dist = (float)Math.random()*1.5f + 1f;
        float xT = (float)Math.cos(angle) * dist;
        float yT = (float)Math.sin(angle) * dist;
        particleSystem.addParticle(
                xT,
                yT,
                angle,
                1,
                (float)Math.random(),
                1,
                .4f,
                (float)(Math.random() * .2f)+.1f,
                (float)(Math.random()*.5)+.01f,
                (float)(Math.random()*40)-20
        );
    }

    public void addParticleCircleToCenter(float dist, float spd, float distance)
    {
        if(spd == -1)
        {
            spd = (float) (Math.random() * .05f) + .05f;
        }
        if(distance == -1)
        {
            distance = (float) (Math.random() * .2) + .1f;
        }
        for(float angle = 0; angle < Constants.twoPI;  angle += .01f)
        {
            float xT = (float) Math.cos(angle) * dist;
            float yT = (float) Math.sin(angle) * dist;
            particleSystem.addParticle(
                    xT,
                    yT,
                    angle,
                    1,
                    1,
                    1,
                    .2f,
                        spd,
                    distance,
                    (float) (Math.random() * 40) - 20
            );
        }
    }
}