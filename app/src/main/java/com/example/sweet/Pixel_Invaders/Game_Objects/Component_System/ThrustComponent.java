package com.example.sweet.Pixel_Invaders.Game_Objects.Component_System;

import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;

/**
 * Created by Sweet on 4/30/2018.
 */

public class ThrustComponent extends Component
{
    private float thrustPower;

    public ThrustComponent(Pixel[] p, float x, float y, float a, float power)
    {
        super(p, x, y, a, Constants.DropType.THRUSTER);
        thrustPower = power;
        setColor();
    }

    private void setColor()
    {
        float halfMaxPower = Constants.MAX_THRUST_MULT / 2;
        r = 1;
        g = 1;
        b = 1;
        if(thrustPower < halfMaxPower)
        {
            r -= thrustPower / halfMaxPower;
        }
        else if(thrustPower < Constants.MAX_PPS && thrustPower >= halfMaxPower)
        {
            r = 0;
            g -= (thrustPower - halfMaxPower) / halfMaxPower;
        }
        else
        {
            r = 0;
            g = 0;
        }
    }

    public float getThrustPower()
    {
        if(live)
        {
            return thrustPower;
        }
        else
        {
            return 1;
        }
    }

    public float getMaxThrustPower()
    {
        return thrustPower;
    }

}
