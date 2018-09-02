package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.DropFactory;

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
        thrusters[0] = new ThrustComponent(thrusterPixels, 0, 0, 0,0, 2, ps);

        baseSpeed = .004f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;

        enemyBody.setEdgeColor(.5f, 0f, .5f);
    }

    public Kamikaze(PixelGroup p, float xb, float yb, ParticleSystem ps, DropFactory dF, float difficulty, float delay, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, delay, gI);

        for(int i = 0; i < Constants.kamikazeMThrustCoor.length; i += 2)
        {
            thrusterPixels[i / 2] = enemyBody.getpMap()[Constants.kamikazeMThrustCoor[i + 1] + 1][Constants.kamikazeMThrustCoor[i] + 1];
        }

        thrusters = new ThrustComponent[1];
        thrusters[0] = new ThrustComponent(thrusterPixels, 0, 0, 0,0, 2, ps);

        baseSpeed = .004f;
        enemyBody.speed = baseSpeed;
        p.speed = baseSpeed;

        enemyBody.setEdgeColor(.5f, 0f, .5f);
    }

    @Override
    public void move(float pX, float pY)
    {
        if(spawned)
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
        else
        {
            if(globalInfo.getAugmentedTimeMillis() - levelStartTime > spawnDelay)
            {
                spawned = true;
            }
        }
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
