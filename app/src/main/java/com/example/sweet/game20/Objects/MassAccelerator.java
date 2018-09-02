package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.DropFactory;

/**
 * Created by Sweet on 8/23/2018.
 */

public class MassAccelerator extends Enemy
{
    private Pixel[] mainThrusterPixels = new Pixel[Constants.maThrustCoors[1].length/2];
    private Pixel[] leftThrusterPixels = new Pixel[Constants.maThrustCoors[0].length/2];
    private Pixel[] rightThrusterPixels = new Pixel[Constants.maThrustCoors[2].length/2];
    private Pixel[] gunPixels = new Pixel[Constants.maGunCoors.length/2];

    public MassAccelerator(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);

        for(int i = 0; i < Constants.maThrustCoors[1].length; i += 2)
        {
            mainThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.maThrustCoors[1][i + 1] + 1][Constants.maThrustCoors[1][i] + 1];
        }
        for(int i = 0; i < Constants.maThrustCoors[0].length; i += 2)
        {
            leftThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.maThrustCoors[0][i + 1] + 1][Constants.maThrustCoors[0][i] + 1];
            rightThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.maThrustCoors[2][i + 1] + 1][Constants.maThrustCoors[2][i] + 1];
        }
        for(int i = 0; i < Constants.maGunCoors.length; i += 2)
        {
            gunPixels[i / 2] = enemyBody.getpMap()[Constants.maGunCoors[i + 1] + 1][Constants.maGunCoors[i] + 1];
        }

        guns = new GunComponent[1];
        guns[0] = new GunComponent(gunPixels, 0, 0, 0, g, ps);
        hasGun = true;
        thrusters = new ThrustComponent[3];
        thrusters[0] = new ThrustComponent(leftThrusterPixels, 0, 0, 0,0, 2, ps);
        thrusters[1] = new ThrustComponent(mainThrusterPixels, 0, 0, 0,0, 2, ps);
        thrusters[2] = new ThrustComponent(rightThrusterPixels, 0, 0, 0,0, 2, ps);

        baseSpeed = .004f;
        rotateSpeed = .002f;
        enemyBody.speed = baseSpeed;
        checkOverlap = false;
        enemyBody.setEdgeColor(.8f, 0f, .8f);
    }

    public MassAccelerator(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, float difficulty, float delay, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, delay, gI);


        for(int i = 0; i < Constants.maThrustCoors[1].length; i += 2)
        {
            mainThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.maThrustCoors[1][i + 1] + 1][Constants.maThrustCoors[1][i] + 1];
        }
        for(int i = 0; i < Constants.maThrustCoors[0].length; i += 2)
        {
            leftThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.maThrustCoors[0][i + 1] + 1][Constants.maThrustCoors[0][i] + 1];
            rightThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.maThrustCoors[2][i + 1] + 1][Constants.maThrustCoors[2][i] + 1];
        }
        for(int i = 0; i < Constants.maGunCoors.length; i += 2)
        {
            gunPixels[i / 2] = enemyBody.getpMap()[Constants.maGunCoors[i + 1] + 1][Constants.maGunCoors[i] + 1];
        }

        guns = new GunComponent[1];
        guns[0] = new GunComponent(gunPixels, 0, 0, 0, g, ps);
        hasGun = true;
        thrusters = new ThrustComponent[3];
        thrusters[0] = new ThrustComponent(leftThrusterPixels, 0, 0, 0,0, 2, ps);
        thrusters[1] = new ThrustComponent(mainThrusterPixels, 0, 0, 0,0, 2, ps);
        thrusters[2] = new ThrustComponent(rightThrusterPixels, 0, 0, 0,0, 2, ps);

        baseSpeed = .004f;
        rotateSpeed = .002f;
        enemyBody.speed = baseSpeed;
        checkOverlap = false;
        enemyBody.setEdgeColor(.8f, 0f, .8f);
    }

    public void move(float pX, float pY)
    {
        if(spawned)
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
                addThrustParticles(mainThrusterPixels, 1, .05f, enemyBody);
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
                        enemyBody.sinA
                );
            }
            guns[0].gun.move(globalInfo.timeSlow);
        }
        else
        {
            if(globalInfo.getAugmentedTimeMillis() - levelStartTime > spawnDelay)
            {
                spawned = true;
            }
        }
    }

    public void rotate(float angleMoving, float ratio, float slow)
    {
        float delta = (float)enemyBody.angle - angleMoving;

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
                addThrustParticles(leftThrusterPixels, ratio, .042f, enemyBody);
            }
            else
            {
                addThrustParticles(rightThrusterPixels, ratio, .042f, enemyBody);
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
