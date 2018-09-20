package com.example.sweet.Pixel_Invaders.Game_Objects.Enemies;

import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.GunComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Gun;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Collidable;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;
import com.example.sweet.Pixel_Invaders.Util.Static.VectorFunctions;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

/**
 * Created by Sweet on 5/15/2018.
 */

public class Simple extends Enemy
{
    private Pixel[]
            gun1Pixels = new Pixel[Constants.simpleGunCoor.length/2],
            thrusterPixels = new Pixel[Constants.simpleMThrustCoor.length/2];

    private float trackDistance = 1.2f;
    private float engageDistance = 1f;
    private float retreatDistance = .5f;
    private boolean track = false;
    private boolean retreat = false;
    private boolean dropAdded = false;

    public Simple(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);

        for(int i = 0; i < Constants.simpleMThrustCoor.length; i += 2)
        {
            thrusterPixels[i / 2] = enemyBody.getpMap()[Constants.simpleMThrustCoor[i + 1] + 1][Constants.simpleMThrustCoor[i] + 1];
        }

        for(int i = 0; i < Constants.simpleGunCoor.length; i += 2)
        {
            gun1Pixels[i / 2] = enemyBody.getpMap()[Constants.simpleGunCoor[i + 1] + 1][Constants.simpleGunCoor[i] + 1];
        }

        guns = new GunComponent[1];
        guns[0] = new GunComponent(gun1Pixels, Constants.simpleGunOffset[0], Constants.simpleGunOffset[1], 0, g);
        hasGun = true;

        thrusters = new ThrustComponent[1];
        thrusters[0] = new ThrustComponent(thrusterPixels, 0, 0, 0, 2);

        enemyBody.angle = 3.14f;
        enemyBody.rotate(enemyBody.angle);

        baseSpeed = .005f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;
    }

    private Simple(PixelGroup p, Gun g, ParticleSystem ps, DropFactory dF, float xb, float yb, float difficulty, float delay, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, delay, gI);

        for(int i = 0; i < Constants.simpleMThrustCoor.length; i += 2)
        {
            thrusterPixels[i / 2] = enemyBody.getpMap()[Constants.simpleMThrustCoor[i + 1] + 1][Constants.simpleMThrustCoor[i] + 1];
        }

        for(int i = 0; i < Constants.simpleGunCoor.length; i += 2)
        {
            gun1Pixels[i / 2] = enemyBody.getpMap()[Constants.simpleGunCoor[i + 1] + 1][Constants.simpleGunCoor[i] + 1];
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
        guns[0] = new GunComponent(gun1Pixels, Constants.simpleGunOffset[0], Constants.simpleGunOffset[1], 0, g);
        guns[0].gun.reduceSpread(sprd);
        guns[0].gun.incSpeed(shotspd);
        guns[0].gun.reduceDelay(del);


        hasGun = true;

        thrusters = new ThrustComponent[1];
        thrusters[0] = new ThrustComponent(thrusterPixels, 0, 0, 0, power);

        enemyBody.angle = 3.14f;
        enemyBody.rotate(enemyBody.angle);

        baseSpeed = .005f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;

        generateDrops(guns[0], thrusters[0], difficulty, 1);
    }

    @Override
    public void move(float pX, float pY)
    {
        float distanceToPlayer = VectorFunctions.getMagnitude(pX - enemyBody.centerX, pY - enemyBody.centerY);
        float angleMoving = 0f;
        float ratio = 1;
        if (distanceToPlayer > trackDistance)
        {
            track = true;
            retreat = false;
        }
        else if (distanceToPlayer < retreatDistance)
        {
            retreat = true;
            track = false;
        }

        if (track)
        {
            angleMoving = -(float) (Math.atan2(pY - enemyBody.centerY, pX - enemyBody.centerX));
        }

        if (retreat)
        {
            angleMoving = -(float) (Math.atan2(enemyBody.centerY - pY, enemyBody.centerX - pX));
        }


        rotate(angleMoving, .06f, globalInfo.timeSlow);
        float distance = baseSpeed * thrusters[0].getThrustPower() * globalInfo.timeSlow;
        float tempDistX = -(float) (distance * Math.cos(enemyBody.angle));
        float tempDistY = (float) (distance * Math.sin(enemyBody.angle));

        if (distanceToPlayer < engageDistance && distanceToPlayer > retreatDistance && track)
        {
            tempDistX *= .6;
            tempDistY *= .6;
            ratio = 0f;
            if (guns[0] != null)
            {
                guns[0].shoot(guns[0].x + enemyBody.centerX,
                        guns[0].y + enemyBody.centerY,
                        enemyBody.angle + (float) Math.PI,
                        globalInfo,
                        enemyBody.cosA,
                        enemyBody.sinA,
                        0f
                );
            }
        }

        x += -tempDistX;
        y += -tempDistY;
        enemyBody.move(-tempDistX, -tempDistY);
        if (guns[0] != null)
        {
            guns[0].gun.move(globalInfo);
        }
        addThrustParticles(thrusterPixels, ratio, .03f, enemyBody);
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
    
    public void addThrustParticles(Pixel[] pixels, float ratio, float dist, Collidable c)
    {
        for(Pixel p: pixels)
        {
            //if(p.live)
            if(p.state >= 1)
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
                            -enemyBody.angle + (float)Math.PI,
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

    @Override
    public void draw(double interpolation)
    {
        if(guns[0] != null)
        {
            guns[0].gun.draw(0);
        }
        enemyBody.draw();
    }

    public void shoot()
    {

    }

    public Simple clone(float difficulty, float delay)
    {
        return new Simple(
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
