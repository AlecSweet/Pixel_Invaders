package com.example.sweet.Pixel_Invaders.Game_Objects;

import com.example.sweet.Pixel_Invaders.UI_System.ImageContainer;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;

/**
 * Created by Sweet on 9/14/2018.
 */

public class Rift
{
    public float
            radius = .2f,
            x = .5f,
            y = .5f,
            delay,
            spawnTime,
            liveTime,
            nextSpawnTime,
            activeTimeLeft;

    public boolean
            spawned = false,
            active = false;

    public Rift()
    {
    }

    public void addParticles(ParticleSystem pS)
    {
        float sp =  Constants.twoPI/8;
        for(float i = 0; i < Constants.twoPI; i+= sp)
        {
            pS.addParticle(
                    x,
                    y,
                    i,
                    1f,
                    1f,
                    1f,
                    .1f,
                    .2f,
                    radius,
                    (float)(Math.random()*20)-10
            );
        }
    }
}
