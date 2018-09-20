package com.example.sweet.Pixel_Invaders.Game_Objects.Enemies;

import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.GunComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Gun;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;

/**
 * Created by Sweet on 8/23/2018.
 */

public class Heavy extends Enemy
{
    private Pixel[] mainThrusterPixels = new Pixel[Constants.heavyThrustCoors[1].length/2];
    private Pixel[] leftThrusterPixels = new Pixel[Constants.heavyThrustCoors[0].length/2];
    private Pixel[] rightThrusterPixels = new Pixel[Constants.heavyThrustCoors[2].length/2];
    private Pixel[] gunPixels = new Pixel[Constants.heavyGunCoors.length/2];

    private float targetX, targetY;

    public Heavy(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);

        for(int i = 0; i < Constants.heavyThrustCoors[1].length; i += 2)
        {
            mainThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.heavyThrustCoors[1][i + 1] + 1][Constants.heavyThrustCoors[1][i] + 1];
        }
        for(int i = 0; i < Constants.heavyThrustCoors[0].length; i += 2)
        {
            leftThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.heavyThrustCoors[0][i + 1] + 1][Constants.heavyThrustCoors[0][i] + 1];
            rightThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.heavyThrustCoors[2][i + 1] + 1][Constants.heavyThrustCoors[2][i] + 1];
        }
        for(int i = 0; i < Constants.heavyGunCoors.length; i += 2)
        {
            gunPixels[i / 2] = enemyBody.getpMap()[Constants.heavyGunCoors[i + 1] + 1][Constants.heavyGunCoors[i] + 1];
        }

        guns = new GunComponent[1];
        guns[0] = new GunComponent(gunPixels, 0, 0, 0, g);
        hasGun = true;
        thrusters = new ThrustComponent[3];
        thrusters[0] = new ThrustComponent(leftThrusterPixels, 0, 0, 0, 2);
        thrusters[1] = new ThrustComponent(mainThrusterPixels, 0, 0, 0, 2);
        thrusters[2] = new ThrustComponent(rightThrusterPixels, 0, 0, 0, 2);

        baseSpeed = .004f;
        rotateSpeed = .03f;
        enemyBody.speed = baseSpeed;

        targetX = (float)(Math.random()) * xbound * 2 - xbound;
        targetY = (float)(Math.random()) * ybound * 2 - ybound;
    }

    private Heavy(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, float difficulty, float delay, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, delay, gI);

        for(int i = 0; i < Constants.heavyThrustCoors[1].length; i += 2)
        {
            mainThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.heavyThrustCoors[1][i + 1] + 1][Constants.heavyThrustCoors[1][i] + 1];
        }
        for(int i = 0; i < Constants.heavyThrustCoors[0].length; i += 2)
        {
            leftThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.heavyThrustCoors[0][i + 1] + 1][Constants.heavyThrustCoors[0][i] + 1];
            rightThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.heavyThrustCoors[2][i + 1] + 1][Constants.heavyThrustCoors[2][i] + 1];
        }
        for(int i = 0; i < Constants.heavyGunCoors.length; i += 2)
        {
            gunPixels[i / 2] = enemyBody.getpMap()[Constants.heavyGunCoors[i + 1] + 1][Constants.heavyGunCoors[i] + 1];
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

        guns = new GunComponent[1];
        guns[0] = new GunComponent(gunPixels, 0, 0, 0, g);
        guns[0].gun.reduceSpread(sprd);
        guns[0].gun.incSpeed(shotspd);
        guns[0].gun.reduceDelay(del);

        hasGun = true;

        thrusters = new ThrustComponent[3];
        thrusters[0] = new ThrustComponent(leftThrusterPixels, 0, 0, 0, power);
        thrusters[1] = new ThrustComponent(mainThrusterPixels, 0, 0, 0, power);
        thrusters[2] = new ThrustComponent(rightThrusterPixels, 0, 0, 0, power);

        baseSpeed = .004f;
        rotateSpeed = .03f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;

        targetX = (float)(Math.random()) * xbound * 2 - xbound;
        targetY = (float)(Math.random()) * ybound * 2 - ybound;

        generateDrops(guns[0],thrusters[1],difficulty,1);
    }

    public void move(float pX, float pY)
    {
        float angleMoving = -(float) (Math.atan2(targetY - enemyBody.centerY, targetX - enemyBody.centerX));

        rotate(angleMoving, .04f, globalInfo.timeSlow);
        float distance = baseSpeed * thrusters[0].getThrustPower() * globalInfo.timeSlow;
        float tempDistX = -(distance * enemyBody.cosA);
        float tempDistY = (distance * enemyBody.sinA);

        x += -tempDistX;
        y += -tempDistY;
        enemyBody.move(-tempDistX, -tempDistY);

        if (Math.abs(targetY - enemyBody.centerY) < baseSpeed &&
                Math.abs(targetX - enemyBody.centerX) < baseSpeed)
        {
            targetX = (float) (Math.random()) * xbound * -targetX / Math.abs(targetX);
            targetY = (float) (Math.random()) * ybound * -targetY / Math.abs(targetY);
        }

        float ang = (float) (Math.atan2(pY - enemyBody.centerY, enemyBody.centerX - pX));
        if (guns[0].canShoot(globalInfo) && inRange)
        {
            guns[0].shoot(
                    enemyBody.centerX,
                    enemyBody.centerY,
                    ang,
                    globalInfo,
                    enemyBody.cosA,
                    enemyBody.sinA,
                    0f
            );
        }
        guns[0].gun.move(globalInfo);

        addThrustParticles(mainThrusterPixels, 1, .05f, enemyBody);
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

        if (delta > .01 || delta < -.01)
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

    @Override
    public Heavy clone(float difficulty, float delay)
    {
        return new Heavy(
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
