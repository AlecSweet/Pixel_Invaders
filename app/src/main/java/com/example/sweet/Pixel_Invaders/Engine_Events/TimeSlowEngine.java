package com.example.sweet.Pixel_Invaders.Engine_Events;

import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by Sweet on 7/30/2018.
 */

public class TimeSlowEngine
{
    private Stack<TimeSlowEvent> inactive = new Stack<>();

    private TimeSlowEvent[] active = new TimeSlowEvent[5];

    private GlobalInfo globalInfo;

    private HashMap<SlowPattern,SlowPatternFunction> slowFunctions;

    private float slowTime = 1;

    public enum SlowPattern
    {
        POW3_FADEOUT,
        POW2_FADEIN_FADEOUT,
        STATICSLOW,
    }

    private boolean oneActive = false;

    public TimeSlowEngine(GlobalInfo gI, int lode)
    {
        globalInfo = gI;
        slowFunctions = new HashMap<>();
        slowFunctions.put(SlowPattern.POW3_FADEOUT, new Pow3FadeOut());
        slowFunctions.put(SlowPattern.POW2_FADEIN_FADEOUT, new Pow2FadeInFadeOut());
        slowFunctions.put(SlowPattern.STATICSLOW, new StaticSlow());

        for(int i = 0; i < lode; i++)
        {
            inactive.add(new TimeSlowEvent());
        }
    }

    public void addSlow(float amp, float dur, SlowPattern sP)
    {
        if(inactive.isEmpty())
        {
            inactive.push(new TimeSlowEvent());
        }
        int indX = getIndex();
        active[indX] = inactive.pop();
        active[indX].resetEvent(
                amp,
                dur,
                slowFunctions.get(sP)
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
            else if(!active[i].active)
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

interface SlowPatternFunction
{
    float getSlow(float a, float d, float tR);
}

class Pow3FadeOut implements SlowPatternFunction
{
    public Pow3FadeOut()
    {
    }

    @Override
    public float getSlow(float amplitude, float duration, float timeRunning)
    {
        float t = Math.abs((timeRunning / duration));
        return t * t * t * amplitude + 1 - amplitude;
    }
}

class Pow2FadeInFadeOut implements SlowPatternFunction
{
    public Pow2FadeInFadeOut()
    {
    }

    @Override
    public float getSlow(float amplitude, float duration, float timeRunning)
    {
        float t = Math.abs((timeRunning / duration));
        if(t < .06f)
        {
            float ratio = (t / .06f);
            return  1 - amplitude * ratio * ratio;
        }
        else if( t > .9f)
        {
            float ratio = ((t - .8f) / .2f);
            return  1 - amplitude * (1 - ratio * ratio);
        }
        return 1 - amplitude;
    }
}

class StaticSlow implements SlowPatternFunction
{
    public StaticSlow ()
    {
    }

    @Override
    public float getSlow(float amplitude, float duration, float timeRunning)
    {
        return amplitude;
    }
}


