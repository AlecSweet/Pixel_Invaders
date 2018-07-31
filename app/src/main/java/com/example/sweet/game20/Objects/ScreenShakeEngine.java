package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.util.ScreenShake;

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

    private Stack<ScreenShake> inactive = new Stack<>();

    private ScreenShake[]
            activeX = new ScreenShake[20],
            activeY = new ScreenShake[20];

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

        for(int i = 0; i < lode; i++)
        {
            inactive.add(new ScreenShake(gI));
        }
    }

    public void addShake(float amp, int freq, double dur)
    {
        switch(freq)
        {
            case 240:
                    addToArrHelper(
                            amp,
                            freq,
                            dur,
                            freq240Patterns[(int)(Math.random() * 7.99)],
                            freq240Patterns[(int)(Math.random() * 7.99)]
                    );
                    break;
            case 120:
                    addToArrHelper(
                            amp,
                            freq,
                            dur,
                            freq120Patterns[(int)(Math.random() * 7.99)],
                            freq120Patterns[(int)(Math.random() * 7.99)]
                    );
                    break;
            case 60:
                    addToArrHelper(
                            amp,
                            freq,
                            dur,
                            freq60Patterns[(int)(Math.random() * 7.99)],
                            freq60Patterns[(int)(Math.random() * 7.99)]
                    );
                    break;
        }
    }

    private void addToArrHelper(float amp, int freq, double dur, float[] patX, float[] patY)
    {
        if(inactive.isEmpty())
        {
            inactive.push(new ScreenShake(globalInfo));
        }
        int indX = getIndex(activeX);
        activeX[indX] = inactive.pop();
        activeX[indX].resetScreenShake(
                amp,
                freq,
                dur,
                patX
        );

        if(inactive.isEmpty())
        {
            inactive.push(new ScreenShake(globalInfo));
        }
        int indY = getIndex(activeY);
        activeY[indY] = inactive.pop();
        activeY[indY].resetScreenShake(
                amp,
                freq,
                dur,
                patY
        );
    }

    private int getIndex(ScreenShake[] shakeList)
    {
        float lowTimeLeft = 1.1f;
        int lowTimeInd = 0;
        for(int i = 0; i < shakeList.length; i++)
        {
            if(shakeList[i] == null)
            {
                return i;
            }
            else if(shakeList[i].live == false)
            {
                inactive.add(shakeList[i]);
                shakeList[i] = null;
                return i;
            }
            else
            {
                float t = shakeList[i].getRemainingPercentTime();
                if(t < lowTimeLeft)
                {
                    lowTimeLeft = t;
                    lowTimeInd = i;
                }
            }
        }

        inactive.add(shakeList[lowTimeInd]);
        shakeList[lowTimeInd] = null;
        return lowTimeInd;
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
        for(int i = 0; i < activeX.length; i++)
        {
            if(activeX[i] != null && activeX[i].live == true)
            {
                screenShakeX += activeX[i].getShake();
            }
        }
        return screenShakeX;
    }

    public float getShakeY()
    {
        float screenShakeY = 0;
        for(int i = 0; i < activeY.length; i++)
        {
            if(activeY[i] != null && activeY[i].live == true)
            {
                screenShakeY += activeY[i].getShake();
            }
        }
        return screenShakeY;
    }

}
