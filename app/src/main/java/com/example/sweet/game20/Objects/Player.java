package com.example.sweet.game20.Objects;

import android.content.Context;
import android.graphics.PointF;
import android.os.VibrationEffect;
import android.os.Vibrator;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform2fv;

import com.example.sweet.game20.R;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.ImageParser;
import com.example.sweet.game20.util.PixelShapes;
import com.example.sweet.game20.util.VectorFunctions;

import java.util.ArrayList;

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
            driftCount = -300;

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

    private int[] gunFarLeft = new int[]{4, 3, 3, 2, 4, 2, 3, 3};
    private int[] gunLeft = new int[]{12, 10, 11, 9, 12, 9, 11, 10};
    private int[] gunMiddle = new int[]{25, 16, 24, 15, 25, 15, 24, 16};
    private int[] gunRight = new int[]{12, 22, 11, 21, 12, 21, 11, 22};
    private int[] gunFarRight = new int[]{4, 29, 3, 28, 4, 28, 3, 29};

    private double
            globalStartTime;

    private int
            xDispLoc,
            yDispLoc,
            tiltLoc,
            stageCounter = 0;

    private boolean
            movementDown = false;

    private PixelGroup playerBody,
                        sheild;

    private Pixel[]
            gravParticlePixels = new Pixel[gravParticles.length/2],

            leftBoostPixels = new Pixel[leftBoost.length/2],
            rightBoostPixels = new Pixel[rightBoost.length/2],
            mainBoostPixels = new Pixel[mainBoost.length/2],

            gunFarLeftPixels = new Pixel[ gunFarLeft.length/2],
            gunLeftPixels = new Pixel[gunLeft.length/2],
            gunMiddlePixels = new Pixel[gunMiddle.length/2],
            gunRightPixels = new Pixel[gunRight.length/2],
            gunFarRightPixels = new Pixel[gunFarRight.length/2];

    private Vibrator vibrator;

    /*  0: Main Thrust
        1: Left Thrust
        2: Right Thrust
     */
    private ThrustComponent[] thrusters = new ThrustComponent[3];

    private GunComponent[] guns = new GunComponent[3];
    //Number of guns 1, 2, or 3
    private int maxGuns = 1;

    private ArrayList<ModComponent> mods = new ArrayList<>();
    //Can Be any Number
    private int maxMods = 1;

    private String
            X_DISP = "x_displacement",
            Y_DISP = "y_displacement",
            TILT = "tilt";

    public Player(Context context, float sp, float xT, float yT, double gst, int textureID, int sL, int pSL, ParticleSystem ps)
    {
        globalStartTime = gst;
        particleSystem = ps;
        baseSpeed = sp;
        playerBody = ImageParser.parseImage(context, R.drawable.player, R.drawable.player_light, textureID, sL);
        //sheild = ImageParser.parseImage(context, R.drawable.sheild1, textureID, sL);
        //sheild.setEnableOrphanChunkDeletion(false);
        playerBody.knockBackFactor = .01f;
        //sheild.knockBackFactor = .01f;

        initParticleAttachments();

        guns[0] = new GunComponent(gunMiddlePixels,1, playerBody.centerX, playerBody.centerY, (float)playerBody.angle, new BasicGun(gst, textureID, sL, pSL, context, ps), ps);
        guns[1] = null;
        guns[2] = null;
        thrusters[0] = new ThrustComponent(mainBoostPixels, 0, 0, 0, 0, 0, 3, ps);
        thrusters[1] = new ThrustComponent(leftBoostPixels, 0, 0, 0, 0, 1, 2, ps);
        thrusters[2] = new ThrustComponent(rightBoostPixels, 0, 0, 0, 0, 2, 2, ps);

        xDispLoc = glGetUniformLocation(sL, X_DISP);
        yDispLoc = glGetUniformLocation(sL, Y_DISP);
        tiltLoc = glGetUniformLocation(sL, TILT);
    }

    public void move(float mX, float mY)
    {
        float tempMagnitude = VectorFunctions.getMagnitude(mX, mY);
        float angleMoving = (float)(Math.atan2(mY, mX));
        float tempDistX = -(float)(baseSpeed * thrusters[0].thrustPower * -Math.cos(angleMoving));
        float tempDistY = -(float)(baseSpeed * thrusters[0].thrustPower * Math.sin(angleMoving));

        if(tempMagnitude>.15)
        {
            if(playerBody.getCenterX() + tempDistX < -4f || playerBody.getCenterX() + tempDistX > 4f)
                tempDistX *=-1;
            if(playerBody.getCenterY() + tempDistY < -3.5f || playerBody.getCenterY() + tempDistY > 3.5f)
                tempDistY *=-1;
            playerBody.move(tempDistX, tempDistY);
            addThrustParticles(mainBoostPixels,1, .05f);
            rotate(angleMoving,1);
        }
        else if(tempMagnitude <= .15 && tempMagnitude > .028)
        {
            float tempRatio = (float)((tempMagnitude - .028) / .122);
            float tempX = tempDistX * tempRatio;
            float tempY = tempDistY * tempRatio;
            if(playerBody.getCenterX() + tempX < -4f || playerBody.getCenterX() + tempX > 4f)
                tempX *=-1 ;
            if(playerBody.getCenterY() + tempY < -3.5f || playerBody.getCenterY() + tempY > 3.5f)
                tempY *=-1 ;
            playerBody.move(tempX,tempY);
            addThrustParticles(mainBoostPixels,tempRatio,.05f);
            rotate(angleMoving,tempRatio);
        }
    }

    public void rotate(float angleMoving, float ratio)
    {
        float delta = (float)playerBody.angle - angleMoving;

        if (delta > rotateSpeed * thrusters[1].thrustPower || delta < -rotateSpeed * thrusters[2].thrustPower)
        {
            if (delta < -Math.PI || (delta > 0 && delta < Math.PI))
            {
                playerBody.angle -= rotateSpeed * thrusters[2].thrustPower;
            }
            else
            {
                playerBody.angle += rotateSpeed * thrusters[1].thrustPower;
            }
        }
        else
            playerBody.angle =  angleMoving;

        if (delta > .01 || delta < -.01)
        {
            if (delta < -Math.PI || (delta > 0 && delta < Math.PI))
            {
                addThrustParticles(leftBoostPixels, ratio, .034f);
                //addThrustParticles(leftBoostPixels, ratio, .034f);
            }
            else
            {
                addThrustParticles(rightBoostPixels, ratio, .034f);
                //addThrustParticles(rightBoostPixels, ratio, .034f);
            }
        }

        playerBody.rotate(playerBody.angle);
        //sheild.rotate(playerBody.angle);

        if (playerBody.angle > Math.PI)
            playerBody.angle -= Constants.twoPI;
        else if (playerBody.angle < -Math.PI)
            playerBody.angle += Constants.twoPI;
    }

    public void addThrustParticles(Pixel[] pixels, float ratio, float dist)
    {
        for(Pixel p: pixels)
        {
            if(p.live)
            {
                /*double angle = Math.atan2(p.yDisp, p.xDisp);
                float c = (float) Math.cos(-angle);
                float s = (float) Math.sin(-angle);

                float xShift = Constants.PIXEL_SIZE/2 * c + Constants.PIXEL_SIZE/2 * s;
                float yShift = Constants.PIXEL_SIZE/2 * c - Constants.PIXEL_SIZE/2 * s;
                */
                float xDisp = p.xOriginal * playerBody.cosA + p.yOriginal * playerBody.sinA;
                float yDisp = p.yOriginal * playerBody.cosA - p.xOriginal * playerBody.sinA;
                float angle = (float)(Math.atan2(p.yDisp - playerBody.centerY, p.xDisp - playerBody.centerX) + Math.random() * .2 - .1);
                for (int t = 0; t < 2; t++)
                {
                    particleSystem.addParticle(p.xDisp, p.yDisp,
                            (float)(-playerBody.angle+Math.PI),
                            (float) (Math.random() * .2 + .8), (float) (Math.random()), 0, .7f,
                            (baseSpeed * thrusters[0].thrustPower * (float)(Math.random()*70+20)) * ratio, dist * ratio * (float)Math.random()*2, (float)(Math.random()*20)
                            //0, 1, (float)(Math.random()*20)
                    );
                }
            }
        }
    }

    public void drift()
    {
        float tX = (float)Math.cos(driftCount/200*Constants.twoPI)/6000;
        float tY = (float)Math.sin(driftCount/200*Constants.twoPI)/6000;
        playerBody.move(tX, tY);
        if(driftCount <= -200) {
            driftDirection = 1;
        }else if(driftCount >= 200) {
            driftDirection = -1;
        }
        driftCount += driftDirection;

        //if(driftCount % 2 == 0)
        //    addGravParticles();
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

    public void shoot(float sX, float sY)
    {
        float shootAngle = (float) (Math.atan2(sY, sX));
        /*if(guns[0].shoot(playerBody.centerX, playerBody.centerY, shootAngle+(float)Math.PI, context))
        {
            //playerBody.knockBack(shootAngle, 1 + guns[0].speed);
           // sheild.knockBack(shootAngle, 1 + guns[0].speed);
        }*/
        for (GunComponent g : guns)
            if (g != null)
            {
                g.gun.shoot(playerBody.centerX, playerBody.centerY, shootAngle + (float)Math.PI);
            }
    }
    @Override
    public void draw( double interpolation)
    {
        handleCosmetics();
        // Set shader uniform values for the player
        glUniform1f(xDispLoc, playerBody.getCenterX());//+ (speed * (float)( Math.pow(-Math.cos(playerBody.angle),0) * interpolation)));
        glUniform1f(yDispLoc, playerBody.getCenterY());// + (speed * (float)( Math.pow(Math.sin(playerBody.angle),0) * interpolation)));
        glUniform1f(tiltLoc, tiltAngle);
        //Draw the players PixelGroup
        playerBody.draw();
        glUniform1f(tiltLoc, 0);
        //sheild.draw();
        for(GunComponent g: guns)
            if(g != null)
                g.gun.draw(interpolation);
    }

    @Override
    public void drawParticles()
    {
        particleSystem.draw();
    }

    public void handleCosmetics()
    {
        if(stageCounter >= 120)
            stageDirection = -1;
        else if( stageCounter <= 0)
            stageDirection = 1;

        tiltAngle = (float)Math.sin(((double)stageCounter - 60)/240);
        drift();

        stageCounter += stageDirection;
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

        for(int i = 0; i < gunMiddle.length; i += 2)
            gunMiddlePixels[i/2] = playerBody.getpMap()[gunMiddle[i+1]][gunMiddle[i]];

        for(int i = 0; i < gunRight.length; i += 2)
            gunRightPixels[i/2] = playerBody.getpMap()[gunRight[i+1]][gunRight[i]];

        for(int i = 0; i < gunLeft.length; i += 2)
            gunLeftPixels[i/2] = playerBody.getpMap()[gunLeft[i+1]][gunLeft[i]];

        for(int i = 0; i < gunFarLeft.length; i += 2)
            gunFarLeftPixels[i/2] = playerBody.getpMap()[gunFarLeft[i+1]][gunFarLeft[i]];

        for(int i = 0; i < gunFarRight.length; i += 2)
            gunFarRightPixels[i/2] = playerBody.getpMap()[gunFarRight[i+1]][gunFarRight[i]];
    }

    public PixelGroup getPixelGroup()
    {
        return playerBody;
    }

    public PixelGroup getPlayerSheild()
    {
        return sheild;
    }

    public float getSpeed()
    {
        return baseSpeed;
    }

    public GunComponent[] getGuns()
    {
        return guns;
    }

    public void setMovementDown(boolean mD)
    {
        movementDown = mD;
    }
}