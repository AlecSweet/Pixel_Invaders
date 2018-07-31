package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.util.ScreenShake;

import java.sql.Time;
import java.util.Stack;

/**
 * Created by Sweet on 7/30/2018.
 */

public class TimeSlowEngine
{
    private Stack<TimeSlowEvent> inactive = new Stack<>();

    private TimeSlowEvent[] active = new TimeSlowEvent[5];

    private GlobalInfo globalInfo;

    private float slowTime = 1;

    public boolean oneActive = false;

    public TimeSlowEngine(GlobalInfo gI, int lode)
    {
        globalInfo = gI;
        for(int i = 0; i < lode; i++)
        {
            inactive.add(new TimeSlowEvent());
        }
    }

    public void addSlow(float amp, double dur)
    {
        if(inactive.isEmpty())
        {
            inactive.push(new TimeSlowEvent());
        }
        int indX = getIndex();
        active[indX] = inactive.pop();
        active[indX].resetEvent(
                amp,
                dur
        );
        oneActive = true;
    }

    private int getIndex()
    {
        float lowTimeLeft = 1.1f;
        int lowTimeInd = 0;
        for(int i = 0; i < active.length; i++)
        {
            if(active[i] == null)
            {
                return i;
            }
            else if(active[i].active == false)
            {
                inactive.push(active[i]);
                active[i] = null;
                return i;
            }
            else
            {
                float t = active[i].getRemainingPercentTime();
                if(t < lowTimeLeft)
                {
                    lowTimeLeft = t;
                    lowTimeInd = i;
                }
            }
        }

        inactive.add(active[lowTimeInd]);
        active[lowTimeInd] = null;
        return lowTimeInd;
    }

    /*public float getSlow()
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
    }*/

    public void runEngine()
    {
        if(oneActive)
        {
            oneActive = false;
            slowTime = 1;
            for (TimeSlowEvent t : active)
            {
                if (t != null && t.active)
                {
                    slowTime *= t.getSlow();
                    oneActive = true;
                }
            }

            globalInfo.setTimeSlow(slowTime);
        }
    }

}
