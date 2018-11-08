package com.example.sweet.Pixel_Invaders.Game_Objects.Component_System;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Collidable;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

import java.util.ArrayList;

/**
 * Created by Sweet on 9/24/2018.
 */

public class PixelGroupComponent extends Component
{
    private PixelGroup pixelGroup;

    private Pixel[] componentAttachment;
    private ThrustComponent[] thrusters;
    private GunComponent[] guns;
    private boolean hasGun = false;
    private ParticleSystem particleSystem;

    private ArrayList<PixelGroupComponent> attachments;
    private boolean hasAttachments = false;

    public volatile boolean
            onScreen = false,
            inRange = false;

    private float
            attachOriginalX,
            attachOriginalY,
            baseOriginalX,
            baseOriginalY,
            attachDispX,
            attachDispY,
            rotateSpeed,
            speed,
            originalAngle,
            attachDist,
            attachDist2;

    private boolean seperated = false;

    public PixelGroupComponent(PixelGroup base, PixelGroup compGroup, float rSpd, float spd,
                               ThrustComponent[] ts, GunComponent[] gs, ParticleSystem pS,
                               int[] baseAtchIndices, int[] compAtchIndices)
    {
        super(base, Constants.DropType.PIXELGROUP, baseAtchIndices);

        if(compAtchIndices != null)
        {
            componentAttachment = new Pixel[compAtchIndices.length / 2];
            for (int i = 0; i < compAtchIndices.length; i += 2)
            {
                componentAttachment[i / 2] = base.getpMap()[compAtchIndices[i + 1] + 1][compAtchIndices[i] + 1];
            }
            //attachmentPixels = p;
        }

        for(Pixel pX: attachmentPixels)
        {
            baseOriginalX += base.infoMap[pX.row][pX.col].xOriginal;
            baseOriginalY += base.infoMap[pX.row][pX.col].yOriginal;
        }

        baseOriginalX /= attachmentPixels.length;
        baseOriginalY /= attachmentPixels.length;
        attachDist = (float)Math.hypot(baseOriginalX, baseOriginalY);

        thrusters = ts;
        guns = gs;
        particleSystem = pS;
        pixelGroup = compGroup;
        rotateSpeed = rSpd;
        speed = spd;
        originalAngle = base.angle;
        //componentAttachment = cP;
        for(Pixel pX: componentAttachment)
        {
            if(pX != null)
            {
                attachOriginalX += pixelGroup.infoMap[pX.row][pX.col].xOriginal;
                attachOriginalY += pixelGroup.infoMap[pX.row][pX.col].yOriginal;
            }
        }
        attachOriginalX /= componentAttachment.length;
        attachOriginalY /= componentAttachment.length;
        attachDispX = attachOriginalX;
        attachDispY = attachOriginalY;
        attachDist2 = (float)Math.hypot(attachDispX, attachDispY);
    }

    @Override
    public void checkAlive()
    {
        live = false;
        for(Pixel p: attachmentPixels)
        {
            if(p != null && p.state > 0)
            {
                live = true;
                break;
            }
        }

        if(live)
        {
            live = false;
            for(Pixel p: componentAttachment)
            {
                if(p != null && p.state > 0)
                {
                    live = true;
                    break;
                }
            }
        }
        else
        {
            seperated = true;
        }
    }

    public void setAttachments(ArrayList<PixelGroupComponent> aT)
    {
        attachments = aT;
        hasAttachments = true;
    }

    public void move(float nX, float nY, float nCosA, float nSinA, float nAngle, GlobalInfo gI)
    {
        float followX =  baseOriginalX * nCosA + baseOriginalY * nSinA + nX;
        float followY =  baseOriginalY * nCosA - baseOriginalX * nSinA + nY;

        float dirX = followX - pixelGroup.getCenterX();
        float dirY = followY - pixelGroup.getCenterY();
        float angle = (float)Math.atan2(dirY, dirX);
        float distance = (float)Math.hypot(dirX, dirY);
        float d = (distance)/attachDist2;

        rotate(-angle + originalAngle, gI.timeSlow);

        pixelGroup.move((float) Math.cos(angle) * speed * gI.timeSlow * d, (float) Math.sin(angle) * speed * gI.timeSlow * d);

        for(ThrustComponent tC: thrusters)
        {
            if(tC != null)
            {
                addThrustParticles(tC.getAttachmentPixels(), 1, .03f, pixelGroup);
            }
        }

        if(hasAttachments)
        {
            int size = attachments.size();
            for(int i = 0; i < size; i++)
            {
                if(!attachments.get(i).seperated)
                {
                    attachments.get(i).move(
                            pixelGroup.getCenterX(),
                            pixelGroup.getCenterY(),
                            pixelGroup.cosA,
                            pixelGroup.sinA,
                            pixelGroup.angle,
                            gI
                    );
                }
            }
        }
    }

    public void rotate(float angleMoving, float slow)
    {
        float delta = pixelGroup.angle - angleMoving;
        if (delta > rotateSpeed || delta < -rotateSpeed)
        {
            if (delta < -Math.PI || (delta > 0 && delta < Math.PI))
            {
                pixelGroup.angle -= rotateSpeed * slow;
            }
            else
            {
                pixelGroup.angle += rotateSpeed * slow;
            }
        }
        else
        {
            pixelGroup.angle = angleMoving;
        }

        pixelGroup.rotate(pixelGroup.angle);

        attachDispX = attachOriginalX * pixelGroup.cosA + attachOriginalY * pixelGroup.sinA + pixelGroup.getCenterX();
        attachDispY = attachOriginalY * pixelGroup.cosA - attachOriginalX * pixelGroup.sinA + pixelGroup.getCenterY();

        if (pixelGroup.angle > Math.PI)
        {
            pixelGroup.angle -= Constants.twoPI;
        }
        else if (pixelGroup.angle < -Math.PI)
        {
            pixelGroup.angle += Constants.twoPI;
        }
    }

    public void addThrustParticles(Pixel[] pixels, float ratio, float dist, Collidable c)
    {
        for(Pixel p: pixels)
        {
            if(p.state >= 1)
            {
                float xDisp = c.infoMap[p.row][p.col].xOriginal * pixelGroup.cosA +
                        c.infoMap[p.row][p.col].yOriginal * pixelGroup.sinA;
                float yDisp = c.infoMap[p.row][p.col].yOriginal * pixelGroup.cosA -
                        c.infoMap[p.row][p.col].xOriginal * pixelGroup.sinA;
                for (int t = 0; t < 2; t++)
                {
                    particleSystem.addParticle(xDisp + pixelGroup.centerX,
                            yDisp + pixelGroup.centerY,
                            //-pixelGroup.cosA, pixelGroup.sinA,
                            -pixelGroup.angle + (float)Math.PI,
                            c.infoMap[p.row][p.col].r,
                            c.infoMap[p.row][p.col].g,
                            c.infoMap[p.row][p.col].b,
                            .7f,
                            (speed * (float)(Math.random()*70+20)) * ratio,
                            dist * ratio * (float)Math.random()*2,
                            (float)(Math.random()*20)
                    );
                }
            }
        }
    }

    public void draw()
    {
        if(hasAttachments)
        {
            int size = attachments.size();
            for(int i = 0; i < size; i++)
            {
                attachments.get(i).draw();
            }
        }
        /*if(hasGun)
        {
            for(GunComponent gC: guns)
            {
                if(gC != null)
                {
                    gC.gun.draw(0);
                }
            }
        }*/
        if(onScreen && pixelGroup.getCollidableLive())
        {
            pixelGroup.draw();
        }
    }

    public int getPixelsKilled()
    {
        /*int temp = pixelGroup.pixelsKilled;
        if(hasAttachments)
        {
            int size = attachments.size();
            for(int i = 0; i < size; i++)
            {
                temp += attachments.get(i).getPixelsKilled();
            }
        }*/

        return pixelGroup.pixelsKilled;
    }

    public PixelGroup getPixelGroup()
    {
        return pixelGroup;
    }

    public boolean getSeperated()
    {
        return seperated;
    }

    public boolean getHasGun()
    {
        return hasGun;
    }

    public GunComponent[] getGuns()
    {
        return guns;
    }


}
