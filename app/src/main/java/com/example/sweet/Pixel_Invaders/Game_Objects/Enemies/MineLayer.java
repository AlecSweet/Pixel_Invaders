package com.example.sweet.Pixel_Invaders.Game_Objects.Enemies;

import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Bullet;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.GunComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Gun;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

/**
 * Created by Sweet on 8/23/2018.
 */

public class MineLayer extends Enemy
{
    private Pixel[] leftThrusterPixels = new Pixel[Constants.mineLayerThrustCoors[1].length/2];
    private Pixel[] rightThrusterPixels = new Pixel[Constants.mineLayerThrustCoors[0].length/2];
    private Pixel[] gunPixels = new Pixel[Constants.mineLayerGunCoors.length/2];

    private float targetX, targetY;
    private float numshot = 0;

    public MineLayer(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);

        for(int i = 0; i < Constants.mineLayerThrustCoors[1].length; i += 2)
        {
            leftThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.mineLayerThrustCoors[1][i + 1] + 1][Constants.mineLayerThrustCoors[1][i] + 1];
            rightThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.mineLayerThrustCoors[0][i + 1] + 1][Constants.mineLayerThrustCoors[0][i] + 1];
        }
        for(int i = 0; i < Constants.mineLayerGunCoors.length; i += 2)
        {
            gunPixels[i / 2] = enemyBody.getpMap()[Constants.mineLayerGunCoors[i + 1] + 1][Constants.mineLayerGunCoors[i] + 1];
        }


        guns = new GunComponent[1];
        guns[0] = new GunComponent(gunPixels, 0, 0, 0, g);

        hasGun = true;
        thrusters = new ThrustComponent[2];
        thrusters[0] = new ThrustComponent(leftThrusterPixels, 0, 0, 0,2);
        thrusters[1] = new ThrustComponent(rightThrusterPixels, 0, 0, 0,2);

        baseSpeed = .004f;
        rotateSpeed = .03f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;

        targetX = (float)(Math.random()) * xbound * 2 - xbound;
        targetY = (float)(Math.random()) * ybound * 2 - ybound;

    }

    private MineLayer(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, float difficulty, float delay, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, delay, gI);

        for(int i = 0; i < Constants.mineLayerThrustCoors[1].length; i += 2)
        {
            leftThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.mineLayerThrustCoors[1][i + 1] + 1][Constants.mineLayerThrustCoors[1][i] + 1];
            rightThrusterPixels[i / 2] = enemyBody.getpMap()[Constants.mineLayerThrustCoors[0][i + 1] + 1][Constants.mineLayerThrustCoors[0][i] + 1];
        }
        for(int i = 0; i < Constants.mineLayerGunCoors.length; i += 2)
        {
            gunPixels[i / 2] = enemyBody.getpMap()[Constants.mineLayerGunCoors[i + 1] + 1][Constants.mineLayerGunCoors[i] + 1];
        }

        float power = 1;
        float del = 1;
        if(difficulty <= 25f)
        {
            float per = difficulty / 25f;
            power += 2f * per;
            del -= per / 2f;
        }
        else
        {
            power = 3f;
            del = .5f;
        }

        guns = new GunComponent[1];
        guns[0] = new GunComponent(gunPixels, 0, 0, 0, g);
        guns[0].gun.reduceDelay(del);
        hasGun = true;
        thrusters = new ThrustComponent[2];
        thrusters[0] = new ThrustComponent(leftThrusterPixels, 0, 0, 0, power);
        thrusters[1] = new ThrustComponent(rightThrusterPixels, 0, 0, 0,    power);

        baseSpeed = .006f;
        rotateSpeed = .03f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;

        targetX = (float)(Math.random()) * xbound * 2 - xbound;
        targetY = (float)(Math.random()) * ybound * 2 - ybound;

        generateDrops(guns[0], thrusters[0], difficulty, 1);
    }

    public void move(float pX, float pY)
    {
        float angleMoving = -(float)(Math.atan2(targetY - enemyBody.centerY, targetX - enemyBody.centerX));

        rotate(angleMoving , .04f, globalInfo.timeSlow);
        float distance = baseSpeed * thrusters[0].getThrustPower() * globalInfo.timeSlow;
        float tempDistX = -(distance * enemyBody.cosA);
        float tempDistY = (distance * enemyBody.sinA);

        x += -tempDistX;
        y += -tempDistY;
        enemyBody.move(-tempDistX, -tempDistY);

        if(Math.abs(targetY - enemyBody.centerY) < baseSpeed &&
                Math.abs(targetX - enemyBody.centerX) < baseSpeed)
        {
            targetX = (float)(Math.random()) * xbound * -targetX / Math.abs(targetX);
            targetY = (float)(Math.random()) * ybound * -targetY / Math.abs(targetY);
        }

        float ang = (float)(Math.atan2(pY - enemyBody.centerY, enemyBody.centerX - pX));
        if(numshot < guns[0].gun.totalBullets &&
                guns[0].canShoot(globalInfo) &&
                Math.abs(enemyBody.centerX) < xbound*.8 &&
                Math.abs(enemyBody.centerY) < ybound*.8)
        {
            if(guns[0].shoot(
                    enemyBody.centerX,
                    enemyBody.centerY,
                    ang,
                    globalInfo,
                    enemyBody.cosA,
                    enemyBody.sinA))
            {
                numshot++;
            }
        }
        guns[0].gun.move(globalInfo);
        addThrustParticles(leftThrusterPixels, 1, .05f, enemyBody);
        addThrustParticles(rightThrusterPixels, 1, .05f, enemyBody);
        for(Bullet b: guns[0].gun.getBullets())
        {
            b.checkOnScreen(globalInfo);
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
        if(thrusters[1] != null)
        {
            sideSpd2 = -rotateSpeed * thrusters[1].getThrustPower() * slow;
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
    public MineLayer clone(float difficulty, float delay)
    {
        return new MineLayer(
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
