package com.example.sweet.game20.Objects;

import android.content.Context;

import com.example.sweet.game20.R;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.ImageParser;

/**
 * Created by Sweet on 3/27/2018.
 */

public class BasicGun extends Gun
{
    public BasicGun(PixelGroup pG, ParticleSystem ps, double delay, float spd)
    {
        super(delay, pG, ps, spd);
        spread = .04f;
    }

    /*public BasicGun(ParticleSystem ps)
    {
        super(1000, ps, .018f);
        //ImageParser.parseImage(context, R.drawable.basicbullet, R.drawable.basicbullet_light, tID, sL)
        spread = .06f;
    }*/

    /*@Override
    public boolean shoot(float x, float y, float angle)
    {
        if (System.currentTimeMillis() > lastShotTime + shootDelay * fireRateMod)
        {
            float arc = (numShots - 1) * .1f;

            for(int i = 0; i < numShots; i++)
            {
                float angDisp = (((float)(i + 1) / numShots) * arc) - (arc / 2);
                angDisp += (float)(Math.random() * spread - spread / 2);
                lastShotTime = System.currentTimeMillis();
                bulletPool.pop().resetBullet(x, y, angle + angDisp);
                addShotParticles(angle, x, y);
            }
            return true;
        }
        else
            return false;
    }*/

    @Override
    public boolean shoot(float x, float y, float angle, long currentFrame, float slow)
    {
        if (currentFrame > lastShotFrame + (shotFrameDelay * fireRateMod) * (1 / slow))
        {
            float arc = (numShots - 1) * .1f;

            for(int i = 0; i < numShots; i++)
            {
                float angDisp = (((float)(i + 1) / numShots) * arc) - (arc / 2);
                angDisp += (float)(Math.random() * spread - spread / 2);
                bulletPool.pop().resetBullet(x, y, angle + angDisp);
                addShotParticles(angle, x, y);
            }
            lastShotFrame = currentFrame;
            return true;
        }
        else
            return false;
    }

    @Override
    public void move(float slow)
    {
        for (Bullet b: bullets)
        {
            float centerX;
            float centerY;
            if(b.pixelGroup.enableLocationChain)
            {
                /*centerX = b.pixelGroup.locationDrawTail.x;
                centerY = b.pixelGroup.locationDrawTail.y;*/
                centerX = b.pixelGroup.centerX - b.cosA * .01f;
                centerY = b.pixelGroup.centerY - b.sinA * .01f;
            }
            else
            {
                centerX = b.pixelGroup.centerX - b.cosA * .01f;
                centerY = b.pixelGroup.centerY - b.sinA * .01f;
            }
            if(b.live)
            {
                b.move(slow);
                /*for(int i = 0; i < 4; i ++)
                {
                    masterParticleSystem.addParticle(
                            centerX, centerY,
                            b.angle + (float) (Math.random() * .3 - .15),
                            b.pixelGroup.pixels[0].r, b.pixelGroup.pixels[0].g, b.pixelGroup.pixels[0].b, 1f,
                            (float)(Math.random()*.03f) + .01f, (float)(Math.random()*.003f) + .002f, 10f
                    );
                }*/
                for(Pixel p: b.pixelGroup.pixels)
                {
                    //float cC = (float)(Math.random()) * .1f -.5f + 1;
                    //if (p.live && p.outside)
                    if(p.state >= 2)
                    {
                        float cC = (float)(Math.random()) * 1f + .5f;
                        masterParticleSystem.addParticle(
                                p.xDisp + centerX, p.yDisp + centerY,
                                -b.angle + (float)(Math.random()* .2f - 1f),
                                b.pixelGroup.infoMap[p.row][p.col].r * cC,
                                b.pixelGroup.infoMap[p.row][p.col].g * cC,
                                b.pixelGroup.infoMap[p.row][p.col].b * cC,
                                .6f,
                                .2f, .02f, 4f
                        );
                    }
                }
            }
            if(!b.live)
            {
                bulletPool.add(b);
            }
        }
    }

    public void addShotParticles(float angle, float x, float y)
    {
        for (float t = 0; t < Constants.twoPI; t += .04)
        {
            masterParticleSystem.addParticle(
                    x, y,
                    t,
                    0f, 0f, 0f, .1f,
                    .4f, .1f, 10f
            );
        }
    }

    @Override
    public BasicGun clone()
    {
        return new BasicGun(pixelGroupTemplate, masterParticleSystem, shootDelay, speed);
    }
}
