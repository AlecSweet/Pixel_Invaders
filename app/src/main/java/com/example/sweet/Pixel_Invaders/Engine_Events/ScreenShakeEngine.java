package com.example.sweet.Pixel_Invaders.Engine_Events;

import com.example.sweet.Pixel_Invaders.Game_Objects.Pooling.ObjectNode;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

import java.util.Stack;

/**
 * Created by Sweet on 7/30/2018.
 */

public class ScreenShakeEngine
{
    private float[][]
            freq240Patterns = new float[8][240],
            freq120Patterns = new float[8][120],
            freq60Patterns = new float[8][60];

    private ObjectNode
            activeTailX,
            activeHeadX,
            activeTailY,
            activeHeadY,
            poolHead,
            poolTail,
            poolTemp;

    private GlobalInfo globalInfo;


    public ScreenShakeEngine(GlobalInfo gI, int lode)
    {
        globalInfo = gI;

        for(int i = 0; i < 8; i++)
        {
            freq60Patterns[i] = generatePattern(60);
            freq120Patterns[i] = generatePattern(120);
            freq240Patterns[i] = generatePattern(240);
        }

        poolTail = new ObjectNode(new ScreenShake(gI), null);
        poolHead = poolTail;

        for(int i = 0; i < lode; i++)
        {
            poolHead.nextObject = new ObjectNode(
                    new ScreenShake(gI),
                    null
            );
            poolHead = poolHead.nextObject;
        }
        
        activeTailX = new ObjectNode(
                new ScreenShake(0, 0 ,0, gI),
                null
        );
        activeHeadX = activeTailX;
        
        activeTailY = new ObjectNode(
                new ScreenShake(0, 0 ,0, gI),
                null
        );
        activeHeadY = activeTailY;
    }

    public void addShake(float amp, int freq, double dur)
    {
        switch(freq)
        {
            case 240:
                    addHelper(
                            amp,
                            freq,
                            dur,
                            freq240Patterns[(int)(Math.random() * 7.99)],
                            freq240Patterns[(int)(Math.random() * 7.99)]
                    );
                    break;
            case 120:
                    addHelper(
                            amp,
                            freq,
                            dur,
                            freq120Patterns[(int)(Math.random() * 7.99)],
                            freq120Patterns[(int)(Math.random() * 7.99)]
                    );
                    break;
            case 60:
                    addHelper(
                            amp,
                            freq,
                            dur,
                            freq60Patterns[(int)(Math.random() * 7.99)],
                            freq60Patterns[(int)(Math.random() * 7.99)]
                    );
                    break;
        }
    }

    private void addHelper(float amp, int freq, double dur, float[] patX, float[] patY)
    {
        activeHeadX.nextObject = takePoolTail();
        activeHeadX = activeHeadX.nextObject;
        ((ScreenShake)activeHeadX.object).resetScreenShake(
                amp,
                freq,
                dur,
                patX
        );

        activeHeadY.nextObject = takePoolTail();
        activeHeadY = activeHeadY.nextObject;
        ((ScreenShake)activeHeadY.object).resetScreenShake(
                amp,
                freq,
                dur,
                patY
        );
    }

    private void checkPoolTail()
    {
        if(poolTail == null)
        {
            poolTail = new ObjectNode(
                    new ScreenShake(globalInfo),
                    null
            );

            poolTail.nextObject = new ObjectNode(
                    new ScreenShake(globalInfo),
                    null
            );

            poolHead = poolTail.nextObject;
        }
        else if(poolTail.nextObject == null)
        {
            poolTail.nextObject = new ObjectNode(
                    new ScreenShake(globalInfo),
                    null
            );

            poolHead = poolTail.nextObject;
        }
    }
    
    private ObjectNode takePoolTail()
    {
        checkPoolTail();
        poolTemp = poolTail;
        poolTail = poolTail.nextObject;
        poolTemp.nextObject = null;
        return poolTemp;        
    }

    private void removeNextNode(ObjectNode o)
    {
        poolTemp = o.nextObject;
        if(poolTemp == activeHeadX)
        {
            activeHeadX = o;
        }
        else if(poolTemp == activeHeadY)
        {
            activeHeadY = o;
        }
        o.nextObject = poolTemp.nextObject;
        poolTemp.nextObject = null;
        poolHead.nextObject = poolTemp;
        poolHead = poolTemp;
    }

    private float[] generatePattern(int length)
    {
        float[] points = new float[length];
        float flip;
        if(Math.random() < .5)
        {
            flip = -1;
        }
        else
        {
            flip = 1;
        }

        for(int i = 0; i < length; i++)
        {
            points[i] = (float)(Math.random() * flip);
            flip *= -1;
        }
        return points;
    }

    public float getShakeX()
    {
        float screenShakeX = 0;

        ObjectNode activeItrX = activeTailX;
        
        while(activeItrX != null)
        {
            if(activeItrX.nextObject != null)
            {
                if (((ScreenShake) activeItrX.nextObject.object).live)
                {
                    screenShakeX += ((ScreenShake) activeItrX.nextObject.object).getShake();
                    activeItrX = activeItrX.nextObject;
                }
                else
                {
                    removeNextNode(activeItrX);
                    activeItrX = activeItrX.nextObject;
                }
            }
            else
            {
                break;
            }
        }
        return screenShakeX;
    }

    public float getShakeY()
    {
        float screenShakeY = 0;

        ObjectNode activeItrY = activeTailY;

        while(activeItrY != null)
        {
            if(activeItrY.nextObject != null)
            {
                if (((ScreenShake) activeItrY.nextObject.object).live)
                {
                    screenShakeY += ((ScreenShake) activeItrY.nextObject.object).getShake();
                    activeItrY = activeItrY.nextObject;
                }
                else
                {
                    removeNextNode(activeItrY);
                    activeItrY = activeItrY.nextObject;
                }
            }
            else
            {
                break;
            }
        }
        return screenShakeY;
    }
}
