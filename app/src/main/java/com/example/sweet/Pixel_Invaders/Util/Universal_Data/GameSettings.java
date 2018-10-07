package com.example.sweet.Pixel_Invaders.Util.Universal_Data;

/**
 * Created by Sweet on 8/20/2018.
 */

public class GameSettings
{
    public float
            particlePercent = 1,
            musicPercent = 1,
            soundPercent = 1,
            screenShakePercent = 1,
            joyStickSize = 1;

    public boolean
            doubleTapPause = true,
            slowOnKill = true,
            showFps = false,
            skipIntros = true,
            staticJoysticks = false;


    public GameSettings()
    {

    }

    public void toggleSetting(int i)
    {
        switch (i)
        {
            case 0:
                if(slowOnKill)
                {
                    slowOnKill = false;
                }
                else
                {
                    slowOnKill = true;
                }
                break;
            case 1:
                if(showFps)
                {
                    showFps = false;
                }
                else
                {
                    showFps = true;
                }
                break;
            case 2:
                if(doubleTapPause)
                {
                    doubleTapPause = false;
                }
                else
                {
                    doubleTapPause = true;
                }
                break;
            case 3:
                if(skipIntros)
                {
                    skipIntros = false;
                }
                else
                {
                    skipIntros = true;
                }
                break;
            case 5:
                if(staticJoysticks)
                {
                    staticJoysticks = false;
                }
                else
                {
                    staticJoysticks = true;
                }
                break;
        }
    }

    public boolean getToggleSetting(int i)
    {
        switch (i)
        {
            case 0: return slowOnKill;
            case 1: return showFps;
            case 2: return doubleTapPause;
            case 3: return skipIntros;
            case 5: return staticJoysticks;
            default: return false;
        }
    }

    public void setSlideSetting(int i, float percent)
    {
        switch (i)
        {
            case 0: particlePercent = percent; break;
            case 1: screenShakePercent = percent; break;
            case 2: musicPercent = percent; break;
            case 3: soundPercent = percent; break;
            case 4: joyStickSize = percent; break;
        }
    }
}
