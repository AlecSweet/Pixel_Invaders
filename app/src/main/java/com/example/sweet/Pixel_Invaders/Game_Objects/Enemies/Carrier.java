package com.example.sweet.Pixel_Invaders.Game_Objects.Enemies;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Static.VectorFunctions;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;

import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.carrierThrustCoors;

public class Carrier extends Enemy
{
    private boolean inPosition = false;
    private float
        targetX = 0,
        targetY = 0,
        distX = 0,
        distY = 0;

    public Carrier(PixelGroup p, ParticleSystem ps, DropFactory dF, float xb, float yb, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);
    }

    private Carrier(PixelGroup p, ParticleSystem ps, DropFactory dF, float xb, float yb, float difficulty, float delay, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, delay, gI);

        checkOverlap = false;
        thrusters = new ThrustComponent[3];
        thrusters[0] = new ThrustComponent(enemyBody, 2, Constants.carrierThrustCoors[0]);
        thrusters[1] = new ThrustComponent(enemyBody,2, Constants.carrierThrustCoors[1]);
        thrusters[2] = new ThrustComponent(enemyBody, 2, Constants.carrierThrustCoors[2]);
        enemyBody.rotate(enemyBody.angle);
        baseSpeed = .005f;
        enemyBody.speed = baseSpeed;
        hasGun = false;

        targetX = (float)Math.random() * 1.2f * (x / Math.abs(x));
        targetY = (float)Math.random() * 1.2f * (y / Math.abs(y));
        enemyBody.speed = baseSpeed;

        float travelAngle = -(float)(Math.atan2(targetY - enemyBody.centerY, targetX - enemyBody.centerX));
        enemyBody.angle = travelAngle;
        rotate(travelAngle,6, 1);
        distX = -(float)(baseSpeed * Math.cos(travelAngle));
        distY = (float)(baseSpeed * Math.sin(travelAngle));
    }

    @Override
    public void move(float pX, float pY)
    {
        if(!inPosition)
        {
            if (Math.abs(enemyBody.getCenterX() - targetX) <= baseSpeed * 3 &&
                    Math.abs(enemyBody.getCenterY() - targetY) <= baseSpeed * 3)
            {
                inPosition = true;
                enemyBody.move(0, 0);
            }
            else
            {
                x += -distX;
                y += -distY;
                enemyBody.move(-distX, -distY);
                addThrustParticles(thrusters[1].getAttachmentPixels(), 1, .06f, enemyBody);
            }
        }
        else
        {
            enemyBody.move(0, 0);
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

    public void shoot()
    {

    }

    public Carrier clone(float difficulty, float delay)
    {
        return new Carrier(
                enemyBody.clone(),
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
