package com.example.sweet.Pixel_Invaders.Game_Objects.Enemies;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.GunComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Gun;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;

/**
 * Created by Sweet on 8/23/2018.
 */

public class MassAccelerator extends Enemy
{
    public MassAccelerator(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);

        guns = new GunComponent[1];
        guns[0] = new GunComponent(enemyBody,0, 0, 0, g, Constants.maGunCoors);
        hasGun = true;
    }

    private MassAccelerator(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, float difficulty, float delay, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, delay, gI);

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

        guns = new GunComponent[1];
        guns[0] = new GunComponent(enemyBody, 0, 0, 0, g, Constants.maGunCoors);
        guns[0].gun.reduceSpread(sprd);
        guns[0].gun.incSpeed(shotspd);
        guns[0].gun.reduceDelay(del);
        hasGun = true;
        
        thrusters = new ThrustComponent[3];
        thrusters[0] = new ThrustComponent(enemyBody, power, Constants.maThrustCoors[0]);
        thrusters[1] = new ThrustComponent(enemyBody, power, Constants.maThrustCoors[1]);
        thrusters[2] = new ThrustComponent(enemyBody, power, Constants.maThrustCoors[2]);

        baseSpeed = .004f;
        rotateSpeed = .002f;
        enemyBody.speed = baseSpeed;
        checkOverlap = false;

        generateDrops(guns[0], thrusters[1], difficulty,1);
    }

    public void move(float pX, float pY)
    {
        float angleMoving = -(float)(Math.atan2(pY - enemyBody.centerY, pX - enemyBody.centerX));
        rotate(angleMoving , .04f, globalInfo.timeSlow);
        float distance = baseSpeed * thrusters[0].getThrustPower() * globalInfo.timeSlow;
        float tempDistX = -(distance * enemyBody.cosA);
        float tempDistY = (distance * enemyBody.sinA);

        if(!inRange)
        {
            x += -tempDistX;
            y += -tempDistY;
            enemyBody.move(-tempDistX, -tempDistY);
            addThrustParticles(thrusters[1].getAttachmentPixels(), 1, .05f, enemyBody);
        }
        else
        {
            enemyBody.move(0, 0);
        }

        if(guns[0].canShoot(globalInfo) && inRange)
        {
            guns[0].shoot(
                    enemyBody.centerX,
                    enemyBody.centerY,
                    (float)(enemyBody.angle+Math.PI),
                    globalInfo,
                    enemyBody.cosA,
                    enemyBody.sinA,
                    0f
            );
        }
        guns[0].gun.move(globalInfo);
    }

    public void rotate(float angleMoving, float ratio, float slow)
    {
        float delta = enemyBody.angle - angleMoving;

        float sideSpd, sideSpd2;
        if(thrusters[0] != null)
        {
            sideSpd = rotateSpeed * thrusters[0].getThrustPower() * slow;
        }
        else
        {
            sideSpd = rotateSpeed * slow;
        }
        if(thrusters[2] != null)
        {
            sideSpd2 = -rotateSpeed * thrusters[2].getThrustPower() * slow;
        }
        else
        {
            sideSpd2 = -rotateSpeed * slow;
        }

        if (delta > sideSpd || delta < sideSpd2)
        {
            if (delta < -Math.PI || (delta > 0 && delta < Math.PI))
            {
                enemyBody.angle += sideSpd2;
            }
            else
            {
                enemyBody.angle += sideSpd;
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
                addThrustParticles(thrusters[0].getAttachmentPixels(), ratio, .042f, enemyBody);
            }
            else
            {
                addThrustParticles(thrusters[2].getAttachmentPixels(), ratio, .042f, enemyBody);
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

    public MassAccelerator clone(float difficulty, float delay)
    {
        return new MassAccelerator(
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
