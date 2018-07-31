package com.example.sweet.game20.Objects;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;

/**
 * Created by Sweet on 3/25/2018.
 */


public class Bullet
{
    public float
            speed,
            angle,
            maxDistance,
            distance,
            cosA,
            sinA;

    public boolean live = true;

    public PixelGroup pixelGroup;

    public Bullet(float cx, float cy, float a, float spd, float mD, PixelGroup pG)
    {
        pixelGroup = pG;
        pixelGroup.setEnableOrphanChunkDeletion(true);
        pixelGroup.restorable = true;
        pixelGroup.orphanChunkCheckDelay = 0;
        speed = spd;
        angle = a;
        cosA = (float)Math.cos(angle);
        sinA = (float)Math.sin(angle);
        pixelGroup.move(cx,cy);
        pixelGroup.rotate(a);
        maxDistance = mD;
        distance = 0;
        pixelGroup.livablePercentage = .2f;
    }

    public void move(float slow)
    {
        //float slowRatio = (float)(1 - distance / (maxDistance));
        float dist = speed * slow;
        pixelGroup.move((dist * -cosA), (dist * sinA));
        distance += dist;
        if(maxDistance < distance)
        {
            live = false;
        }
    }

    public void rotate(float a)
    {
        angle = a;
        cosA = (float)Math.cos(angle);
        sinA = (float)Math.sin(angle);
    }

    public void checkLive()
    {
        live = false;
        for(Pixel p: pixelGroup.pixels)
            if(p.live)
                live = true;
    }

    public void draw()
    {
        pixelGroup.draw();
    }

    public void freeResources()
    {
        pixelGroup.freeMemory();
    }

    public void resetBullet(float x, float y, float a)
    {
        pixelGroup.resetPixels();
        //pixelGroup.resetLocationHistory(x, y);
        pixelGroup.setLoc(x,y);
        rotate(a);
        pixelGroup.rotate(a);
        distance = 0;
        live = true;
    }
    /*@Override
    public Bullet clone()
    {
        return new Bullet(0,0, angle, speed, maxDistance, shaderLocation, pixelGroup.clone());
    }*/
}
