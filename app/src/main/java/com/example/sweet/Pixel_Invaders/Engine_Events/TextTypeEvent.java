package com.example.sweet.Pixel_Invaders.Engine_Events;

import com.example.sweet.Pixel_Invaders.UI_System.TextPresenter;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

import java.util.ArrayList;

/**
 * Created by Sweet on 9/17/2018.
 */

public class TextTypeEvent
{
    private String
            text,
            bar = "|";

    private float
            typeSpeed = 1,
            charDelay = 100,
            spaceDelay = 200,
            lineDelay = 300,
            deleteDelay = 20,
            nextCharTime,
            nextFlashTime,
            flashDelay = 400,
            lingerDelay = 0,
            lingerStartTime,
            totalEventTime,
            eventStartTime;
    
    private long
            nextCharTimeRT,
            nextFlashTimeRT,
            lingerStartTimeRT,
            eventStartTimeRT;

    private int
            currentChar = 0,
            curLine = 0,
            textLength,
            flash = 1;

    private boolean
            ended = true,
            started = true,
            centeredTyping = false,
            finishedTyping = true,
            deleting = false;

    private ArrayList<Integer> lineBreaks;

    public TextTypeEvent(String t, float tspd, boolean cT, float lD)
    {
        text = t;
        typeSpeed = tspd;
        centeredTyping = cT;
        lingerDelay = lD;
        textLength = text.length();
        lineBreaks = new ArrayList<>();
        lineBreaks.add(0);
        for(int i =  0; i < textLength; i++)
        {
            if (text.charAt(i) != ' ' && text.charAt(i) != '\\')
            {
                totalEventTime += charDelay * typeSpeed;
            }
            else if (text.charAt(i) == '\\')
            {
                lineBreaks.add(i+1);
                totalEventTime += lineDelay * typeSpeed;
            }
            else
            {
                totalEventTime += spaceDelay * typeSpeed;
            }
        }
        lineBreaks.add(textLength);
        totalEventTime += lingerDelay * 2;
        totalEventTime += deleteDelay * textLength;

    }

    public void startTypeEvent(GlobalInfo gI)
    {
        float tempTime = gI.getAugmentedTimeMillis();
        eventStartTime = tempTime;
        nextCharTime = tempTime;

        currentChar = 0;
        curLine = 0;
        flash = 1;
        started = true;
        ended = false;
        deleting = false;
        finishedTyping = false;
    }

    public void startTypeEventRealTime()
    {
        long tempTime = System.currentTimeMillis();
        eventStartTimeRT = tempTime;
        nextCharTimeRT = tempTime;

        currentChar = 0;
        curLine = 0;
        flash = 1;
        started = true;
        ended = false;
        deleting = false;
        finishedTyping = false;
    }

    private int getTextIndex(GlobalInfo gI)
    {
        if(currentChar < textLength && !finishedTyping)
        {
            if (text.charAt(currentChar) != ' ' && text.charAt(currentChar) != '\\' )
            {
                if (gI.getAugmentedTimeMillis() - charDelay * typeSpeed > nextCharTime)
                {
                    nextCharTime = gI.getAugmentedTimeMillis();
                    currentChar++;
                }
            }
            else if (text.charAt(currentChar) == '\\')
            {
                if (gI.getAugmentedTimeMillis() - lineDelay * typeSpeed > nextCharTime)
                {
                    nextCharTime = gI.getAugmentedTimeMillis();
                    curLine++;
                    currentChar++;
                }
            }
            else
            {
                if (gI.getAugmentedTimeMillis() - spaceDelay * typeSpeed > nextCharTime)
                {
                    nextCharTime = gI.getAugmentedTimeMillis();
                    currentChar++;
                }
            }

            return currentChar;
        }
        else if(!deleting)
        {
            if(!finishedTyping)
            {
                float time = gI.getAugmentedTimeMillis();
                nextFlashTime = time;
                lingerStartTime = time;
                finishedTyping = true;
            }
            return textLength;
        }
        else if(deleting)
        {
            if(currentChar > 0)
            {
                if (gI.getAugmentedTimeMillis() - deleteDelay * typeSpeed > nextCharTime)
                {
                    nextCharTime = gI.getAugmentedTimeMillis();
                    currentChar--;
                    if(currentChar > 0 && lineBreaks.contains(currentChar))
                    {
                        curLine--;
                    }
                }
            }
            return  currentChar;
        }
        else
        {
            return  0;
        }
    }

    public void drawTyping(TextPresenter tP, float x, float y, float mag, GlobalInfo gI, boolean hang)
    {
        if(!ended)
        {
            float disp = 0;
            int indx = getTextIndex(gI);
            for(int i = 0; i <= curLine; i++)
            {
                if(indx > lineBreaks.get(i+1))
                {
                    disp = tP.drawString(
                            text,
                            x, y - tP.charSkipX * mag * i,
                            lineBreaks.get(i), lineBreaks.get(i+1),
                            mag,
                            centeredTyping
                    );
                }
                else
                {
                    disp = tP.drawString(
                            text,
                            x, y - tP.charSkipX * mag * i,
                            lineBreaks.get(i), indx,
                            mag,
                            centeredTyping
                    );
                }
            }
            if(flash == 1)
            {
                tP.drawString(
                        bar,
                        x + disp, y - tP.charSkipX * curLine,
                        0,1,
                        mag,
                        centeredTyping
                );
            }
            if(finishedTyping && !deleting)
            {
                if(gI.getAugmentedTimeMillis() - flashDelay > nextFlashTime)
                {
                    nextFlashTime = gI.getAugmentedTimeMillis();
                    flash *= -1;
                }
                if(gI.getAugmentedTimeMillis() - lingerDelay > lingerStartTime)
                {
                    if(!hang)
                    {
                        flash = 1;
                        lingerStartTime = gI.getAugmentedTimeMillis();
                        deleting = true;
                    }
                }
            }
            else if(deleting)
            {
                if(currentChar <= 0)
                {
                    if(gI.getAugmentedTimeMillis() - flashDelay > nextFlashTime)
                    {
                        nextFlashTime = gI.getAugmentedTimeMillis();
                        flash *= -1;
                    }
                    if(gI.getAugmentedTimeMillis() - lingerDelay > lingerStartTime)
                    {
                        ended = true;
                    }
                }
            }
        }
    }

    private int getTextIndexRealTime()
    {
        if(currentChar < textLength && !finishedTyping)
        {
            if (text.charAt(currentChar) != ' ' && text.charAt(currentChar) != '\\')
            {
                if (System.currentTimeMillis() - nextCharTimeRT > charDelay * typeSpeed)
                {
                    nextCharTimeRT = System.currentTimeMillis();
                    currentChar++;
                }
            }
            else if (text.charAt(currentChar) == '\\')
            {
                if (System.currentTimeMillis() - nextCharTimeRT > lineDelay * typeSpeed)
                {
                    nextCharTimeRT = System.currentTimeMillis();
                    curLine++;
                    currentChar++;
                }
            }
            else
            {
                if (System.currentTimeMillis() -  nextCharTimeRT > spaceDelay * typeSpeed)
                {
                    nextCharTimeRT = System.currentTimeMillis();
                    currentChar++;
                }
            }

            return currentChar;
        }
        else if(!deleting)
        {
            if(!finishedTyping)
            {
                long time = System.currentTimeMillis();
                nextFlashTimeRT = time;
                lingerStartTimeRT = time;
                finishedTyping = true;
            }
            return textLength;
        }
        else if(deleting)
        {
            if(currentChar > 0)
            {
                if (System.currentTimeMillis() -  nextCharTimeRT > deleteDelay * typeSpeed)
                {
                    nextCharTimeRT = System.currentTimeMillis();
                    currentChar--;
                    if(currentChar > 0 && lineBreaks.contains(currentChar))
                    {
                        curLine--;
                    }
                }
            }
            return  currentChar;
        }
        else
        {
            return  0;
        }
    }

    public void drawTypingRealTime(TextPresenter tP, float x, float y, float mag, boolean hang)
    {
        if(!ended)
        {
            float disp = 0;
            int indx = getTextIndexRealTime();
            for(int i = 0; i <= curLine; i++)
            {
                if(indx > lineBreaks.get(i+1))
                {
                    disp = tP.drawString(
                            text,
                            x, y - tP.charSkipX * mag * i,
                            lineBreaks.get(i), lineBreaks.get(i+1),
                            mag,
                            centeredTyping
                    );
                }
                else
                {
                    disp = tP.drawString(
                            text,
                            x, y - tP.charSkipX * mag * i,
                            lineBreaks.get(i), indx,
                            mag,
                            centeredTyping
                    );
                }
            }
            if(flash == 1)
            {
                tP.drawString(
                        bar,
                        x + disp, y - tP.charSkipX * mag * curLine,
                        0,1,
                        mag,
                        centeredTyping
                );
            }
            if(finishedTyping && !deleting)
            {
                if(System.currentTimeMillis() - nextFlashTimeRT > flashDelay)
                {
                    nextFlashTimeRT = System.currentTimeMillis();
                    flash *= -1;
                }
                if(System.currentTimeMillis() - lingerStartTimeRT > lingerDelay)
                {
                    if(!hang)
                    {
                        flash = 1;
                        lingerStartTimeRT = System.currentTimeMillis();
                        deleting = true;
                    }
                }
            }
            else if(deleting)
            {
                if(currentChar <= 0)
                {
                    if(System.currentTimeMillis() -  nextFlashTimeRT > flashDelay)
                    {
                        nextFlashTimeRT = System.currentTimeMillis();
                        flash *= -1;
                    }
                    if(System.currentTimeMillis() - lingerStartTimeRT > lingerDelay)
                    {
                        ended = true;
                    }
                }
            }
        }
    }

    public float getEventProgress(GlobalInfo gI)
    {
        return (gI.getAugmentedTimeMillis() - eventStartTime) / totalEventTime;
    }

    public float getEventProgressRealTime()
    {
        return (System.currentTimeMillis() - eventStartTimeRT) / totalEventTime;
    }

    public boolean getStarted()
    {
        return started;
    }

    public void setEnded(boolean e)
    {
        ended = e;
    }

    public void setStarted(boolean b)
    {
        started = b;
    }

    public String getText()
    {
        return text;
    }

    public boolean getEnded()
    {
        return ended;
    }
}
