package com.example.sweet.game20.Objects;

import android.content.Context;

import com.example.sweet.game20.R;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.ImageParser;

import java.util.ArrayList;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;

/**
 * Created by Sweet on 3/25/2018.
 */


public class Bullet
{
    public volatile float
            speed,
            angle,
            maxDistance,
            distance,
            cosA,
            sinA;

    public volatile boolean live = true;
    public volatile boolean active = false;

    public PixelGroup pixelGroup;

    private int
            xDispLoc,
            yDispLoc;

    private String
            X_DISP = "x_displacement",
            Y_DISP = "y_displacement";

    public Bullet(float cx, float cy, float a, float spd, float mD, int sL, PixelGroup pG)
    {
        pixelGroup = pG;
        pixelGroup.setEnableOrphanChunkDeletion(true);
        pixelGroup.orphanChunkCheckDelay = 0;
        speed = spd;
        angle = a;
        cosA = (float)Math.cos(angle);
        sinA = (float)Math.sin(angle);
        pixelGroup.move(cx,cy);
        pixelGroup.rotate(a);
        maxDistance = mD;
        distance = 0;
        pixelGroup.livablePercentage = .3f;
        xDispLoc = glGetUniformLocation(sL,X_DISP);
        yDispLoc = glGetUniformLocation(sL,Y_DISP);
    }

    public void move()
    {
        //float slowRatio = (float)(1 - distance / (maxDistance));
        pixelGroup.move((speed * -cosA), (speed * sinA));
        distance += speed;
        if(maxDistance < distance)
        {
            live = false;
            active = false;
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
        // Set locations for uniforms in the vert shader

        // Set shader uniform values for the player
        glUniform1f(xDispLoc, pixelGroup.centerX);
        glUniform1f(yDispLoc, pixelGroup.centerY);

        pixelGroup.draw();
        //Draw the players PixelGroup
    }

    public void freeResources()
    {
        pixelGroup.freeMemory();
    }

    public void resetBullet(float x, float y, float a)
    {
        pixelGroup.setLoc(x,y);
        rotate(a);
        pixelGroup.rotate(a);//angle + (float) (Math.random() * spread - spread / 2));
        pixelGroup.resetPixels();
        distance = 0;
        live = true;
        active = true;
    }
    /*@Override
    public Bullet clone()
    {
        return new Bullet(0,0, angle, speed, maxDistance, shaderLocation, pixelGroup.clone());
    }*/
}
/*public class Bullet extends Collidable
{
    public float
            speed,
            maxDistance,
            distance;

    public boolean live = true;

    public Bullet(float cx, float cy, float a, float spd, float mD, float hH, float hW, ArrayList<Pixel> p)
    {
        super(cx , cy, hH, hW, p,false);

        angle = a;
        speed = spd;
        maxDistance = mD;
        rotate(angle);
        distance = 0;
    }

    public void move()
    {
        //float slowRatio = (float)(1 - distance / (maxDistance));
        centerX += speed * -Math.cos(angle);
        centerY += speed * -Math.sin(angle);
        distance += speed;
        if(maxDistance < distance)
            live = false;
    }

    public void rotate(double a)
    {
        float c = -(float)Math.cos(a);
        float s = (float)Math.sin(a);
        for(Pixel p: pixels)
        {
            p.xDisp = p.xOriginal*c + p.yOriginal*s;
            p.yDisp = p.yOriginal*c - p.xOriginal*s;
        }
    }

    public void checkLive()
    {
        live = false;
        for(Pixel p: pixels)
            if(p.live)
                live = true;
    }
}*/
