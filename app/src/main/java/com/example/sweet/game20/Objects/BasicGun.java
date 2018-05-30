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
    public BasicGun(float sD, double gst, int tID, int sL, int pSL, PixelGroup pG, ParticleSystem ps)
    {
        super(100, gst, tID, sL, pSL, pG, ps, .03f);
        spread = .04f;
    }
    public BasicGun(double gst, int tID, int sL, int pSL, Context context, ParticleSystem ps)
    {
        super(100, gst, tID, sL, pSL, ImageParser.parseImage(context, R.drawable.kamikaze, R.drawable.kamikaze_light, tID, sL), ps, .03f);
        spread = .06f;
    }

    @Override
    public boolean shoot(float x, float y, float angle)
    {
        if (System.currentTimeMillis() - globalStartTime > lastShotTime + shootDelay)
        {
            lastShotTime = System.currentTimeMillis() - globalStartTime;

            /*bullets.add(new Bullet(x, y, angle + (float) (Math.random() * spread - spread / 2),
                    speed, 3.4f, shaderLocation, pixelGroupTemplate.clone())
            );*/
            bulletPool.pop().resetBullet(x, y,angle + (float) (Math.random() * spread - spread / 2));

            addShotParticles(angle, x, y);
            return true;
        } else
            return false;
    }

    @Override
    public void move()
    {
        for (Bullet b: bullets)
        {
            if(b.live)
            {
                b.move();
                for(Pixel p: b.pixelGroup.pixels)
                {
                    if (p.live && p.outside)
                    {
                        masterParticleSystem.addParticle(p.xDisp, p.yDisp,
                                //b.pixelGroup.cosA, b.pixelGroup.sinA,
                                b.angle,
                                p.r, p.g, p.b, .1f,
                                .04f, .01f, 10f)
                        ;
                    }
                }
            }

            if(!b.live)
            {
                //bulletPool.add(b);
                bulletPool.push(b);
            }

        }
    }

    public void addShotParticles(float angle, float x, float y)
    {
        for (float t = 0; t < Constants.twoPI; t += .04)
        {
            masterParticleSystem.addParticle(x, y,
                    //(float) Math.cos(t), (float) -Math.sin(t),
                    t,
                    0f, 0f, 0f, .1f,
                    .4f, .1f, 10f
            );
        }
    }

    @Override
    public BasicGun clone()
    {
        BasicGun g = new BasicGun((float)shootDelay, globalStartTime, textureID, shaderLocation, 0, pixelGroupTemplate.clone(), masterParticleSystem);
        return g;
    }
}
