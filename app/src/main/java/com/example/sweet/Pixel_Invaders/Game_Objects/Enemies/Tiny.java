package com.example.sweet.Pixel_Invaders.Game_Objects.Enemies;

import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;

/**
 * Created by Sweet on 8/28/2018.
 */

public class Tiny extends Enemy
{
    private Pixel[] thrusterPixels = new Pixel[Constants.tinyMThrustCoor.length/2];

    public Tiny(PixelGroup p, float xb, float yb, ParticleSystem ps, DropFactory dF, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);

        for(int i = 0; i < Constants.tinyMThrustCoor.length; i += 2)
        {
            thrusterPixels[i / 2] = enemyBody.getpMap()[Constants.tinyMThrustCoor[i + 1] + 1][Constants.tinyMThrustCoor[i] + 1];
        }

        thrusters = new ThrustComponent[1];
        thrusters[0] = new ThrustComponent(thrusterPixels, 0, 0, 0, 1);

        baseSpeed = .007f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;
    }

    public Tiny(PixelGroup p, float xb, float yb, ParticleSystem ps, DropFactory dF, float difficulty, float delay, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, delay, gI);

        for(int i = 0; i < Constants.tinyMThrustCoor.length; i += 2)
        {
            thrusterPixels[i / 2] = enemyBody.getpMap()[Constants.tinyMThrustCoor[i + 1] + 1][Constants.tinyMThrustCoor[i] + 1];
        }

        float power = 1;
        if(difficulty <= 20)
        {
            power += 2 * difficulty / 20;
        }
        else
        {
            power = 3;
        }
        thrusters = new ThrustComponent[1];
        thrusters[0] = new ThrustComponent(thrusterPixels, 0, 0, 0, power);

        baseSpeed = .007f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;
    }

    @Override
    public void move(float pX, float pY)
    {
        float angleMoving = -(float) (Math.atan2(pY - enemyBody.centerY, pX - enemyBody.centerX));

        rotate(angleMoving, .04f, globalInfo.timeSlow);
        float distance = baseSpeed * thrusters[0].getThrustPower() * globalInfo.timeSlow;
        float tempDistX = -(float) (distance * Math.cos(enemyBody.angle));
        float tempDistY = (float) (distance * Math.sin(enemyBody.angle));

        x += -tempDistX;
        y += -tempDistY;
        enemyBody.move(-tempDistX, -tempDistY);

        addThrustParticles(thrusterPixels, 1, .03f, enemyBody);
    }

    public Tiny clone(float difficulty, float delay)
    {
        return new Tiny(
                enemyBody.clone(),
                xbound,
                ybound,
                particleSystem,
                dropFactory,
                difficulty,
                delay,
                globalInfo
        );
    }
}
