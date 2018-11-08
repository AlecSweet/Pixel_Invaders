package com.example.sweet.Pixel_Invaders.Game_Objects;

import com.example.sweet.Pixel_Invaders.UI_System.ImageContainer;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Static.VectorFunctions;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

import static android.opengl.GLES20.glUniform3f;

/**
 * Created by Sweet on 9/14/2018.
 */

public class Rift
{
    private float
            curRadius = 0f,
            curSave = 0f,
            activatedMag = 1,
            activatedMagTarget = 4f,
            targetRadius = 0f,
            saveSpin = 0,
            x = 0f,
            y = 0f,
            spawnFreqMod = 1,
            spawnedLiveTime,
            spawnedStart,
            nextSpawnDelay,
            nextSpawnStart,
            activatedTimeTotal;

    private long activatedTimeStart;

    private ParticleSystem particleSystem;

    public enum RiftState{
        SPAWNED,
        ACTIVATED,
        INACTIVE
    }

    public RiftState riftState;

    public Rift(ParticleSystem pS, GlobalInfo gI)
    {
        riftState = RiftState.INACTIVE;
        particleSystem = pS;
        nextSpawnStart = gI.getAugmentedTimeMillis();
        nextSpawnDelay = 60000;
    }

    private void addParticles(float rt)
    {
        float sp =  Constants.twoPI/8;
        for(float i = rt; i < Constants.twoPI + rt; i+= sp)
        {
            particleSystem.addParticle(
                    x,
                    y,
                    i,
                    1f,
                    1f,
                    1f,
                    .15f,
                    .2f,
                    curRadius,
                    (float)(Math.random()*20)-10
            );
        }
    }

    public float checkState(Player p, GlobalInfo gI)
    {
        if(riftState == RiftState.SPAWNED)
        {
            if(gI.getAugmentedTimeMillis() - spawnedLiveTime > spawnedStart)
            {
                nextSpawnStart = gI.getAugmentedTimeMillis();
                nextSpawnDelay = (float)Math.random() * 10000 * spawnFreqMod + 5000 * spawnFreqMod;
                curRadius = 0;
                targetRadius = 0;
                riftState = RiftState.INACTIVE;
            }
            else
            {
                float percent = (gI.getAugmentedTimeMillis() - spawnedStart) / spawnedLiveTime;
                if(percent > 0 && percent < 1)
                {
                    if (percent < .2f)
                    {
                        curRadius = targetRadius * (percent / .2f);
                        curSave = curRadius;
                    }
                    else if (percent > .8f)
                    {
                        curRadius = targetRadius * ((1 - percent) / .2f);
                        curSave = curRadius;
                    }
                    else
                    {
                        curRadius = targetRadius;
                    }
                    addParticles(percent * Constants.twoPI * 4);
                }

                if(VectorFunctions.getSquaredMagnitude(p.getPixelGroup().getCenterX() - x,
                        p.getPixelGroup().getCenterY() - y) <
                        curRadius * curRadius)
                {
                    activatedTimeStart = System.currentTimeMillis();
                    activatedTimeTotal = 7000 * curRadius / targetRadius * (1 + spawnFreqMod);
                    activatedMagTarget = 4 * curRadius + 1;
                    saveSpin = percent * Constants.twoPI * 4;
                    riftState = RiftState.ACTIVATED;
                    return .8f;
                }
            }
            return 0;
        }
        else if(riftState == RiftState.ACTIVATED)
        {
            if(System.currentTimeMillis() - activatedTimeStart > activatedTimeTotal)
            {
                nextSpawnStart = gI.getAugmentedTimeMillis();
                nextSpawnDelay = (float)Math.random() * 20000 * spawnFreqMod + 20000 * spawnFreqMod;
                curRadius = 0;
                activatedMag = 0;
                riftState = RiftState.INACTIVE;
            }
            else
            {
                float percent = (System.currentTimeMillis() - activatedTimeStart) / activatedTimeTotal;
                if(percent < .2f)
                {
                    if(curSave < targetRadius)
                    {
                        curRadius = curSave * (1 - (percent / .2f));
                    }
                    else
                    {
                        curRadius = targetRadius * (1 - (percent / .2f));
                    }
                    activatedMag = activatedMagTarget * (percent / .2f);
                    addParticles(percent * Constants.twoPI * 4 + saveSpin);
                }
                else
                {
                    activatedMag = activatedMagTarget * ((1 - percent) / .8f);
                    curRadius = 0;
                    targetRadius = 0;
                }
            }
            return 0;
        }
        else if(riftState == RiftState.INACTIVE)
        {
            if(gI.getAugmentedTimeMillis() - nextSpawnDelay > nextSpawnStart)
            {
                spawnedStart = gI.getAugmentedTimeMillis();
                spawnedLiveTime = (float)Math.random() * 10000 + 10000 * (spawnFreqMod + 1);
                targetRadius = (float)Math.random()*.4f + .2f;
                x = (float)Math.random() * 4 - 2;
                y = (float)Math.random() * 4 - 2;
                curRadius = 0;
                riftState = RiftState.SPAWNED;
            }
            return 0;
        }

        return 0;
    }

    public float getSlowDuration()
    {
        return activatedTimeTotal;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public float getRadius()
    {
        return curRadius;
    }

    public float getActivatedMag()
    {
        return activatedMag;
    }

    public void riftDataUniform(int loc)
    {
        glUniform3f(loc, x, y, curRadius);
    }

    public void resetFreqMod()
    {
        spawnFreqMod = 1;
    }

    public void reduceFreqMod(float mV)
    {
        spawnFreqMod *= 1 / mV;
    }

}
