package com.example.sweet.Pixel_Invaders.Game_Objects.Enemies;

import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;

/**
 * Created by Sweet on 8/23/2018.
 */

public class Kamikaze extends Enemy
{
    private Pixel[] thrusterPixels = new Pixel[Constants.kamikazeMThrustCoor.length/2];

    public Kamikaze(PixelGroup p, float xb, float yb, ParticleSystem ps, DropFactory dF, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);

        for(int i = 0; i < Constants.kamikazeMThrustCoor.length; i += 2)
        {
            thrusterPixels[i / 2] = enemyBody.getpMap()[Constants.kamikazeMThrustCoor[i + 1] + 1][Constants.kamikazeMThrustCoor[i] + 1];
        }

        thrusters = new ThrustComponent[1];
        thrusters[0] = new ThrustComponent(thrusterPixels, 0, 0, 0, 2);

        baseSpeed = .004f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;
    }

    private Kamikaze(PixelGroup p, float xb, float yb, ParticleSystem ps, DropFactory dF, float difficulty, float delay, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, delay, gI);

        for(int i = 0; i < Constants.kamikazeMThrustCoor.length; i += 2)
        {
            thrusterPixels[i / 2] = enemyBody.getpMap()[Constants.kamikazeMThrustCoor[i + 1] + 1][Constants.kamikazeMThrustCoor[i] + 1];
        }

        float power = 1;
        if(difficulty <= 24)
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

        generateDrops(null,thrusters[0],difficulty,1);
    }

    @Override
    public void move(float pX, float pY)
    {
        float angleMoving = -(float)(Math.atan2(pY - enemyBody.centerY, pX - enemyBody.centerX));

        rotate(angleMoving , .02f, globalInfo.timeSlow);
        float distance = baseSpeed * thrusters[0].getThrustPower() * globalInfo.timeSlow;
        float tempDistX = -(distance * enemyBody.cosA);
        float tempDistY = (distance * enemyBody.sinA);

        x += -tempDistX;
        y += -tempDistY;
        enemyBody.move(-tempDistX, -tempDistY);

        addThrustParticles(thrusterPixels, 1, .03f, enemyBody);
    }

    public Kamikaze clone(float difficulty, float delay)
    {
        return new Kamikaze(
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
