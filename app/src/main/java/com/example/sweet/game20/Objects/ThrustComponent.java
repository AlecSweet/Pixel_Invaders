package com.example.sweet.game20.Objects;

import com.example.sweet.game20.util.Constants;

/**
 * Created by Sweet on 4/30/2018.
 */

public class ThrustComponent extends Component
{
    /*  0: Main Thrust
        1: Left Thrust
        2: Right Thrust
     */
    public int thrustType;

    public float thrustPower;

    public ThrustComponent(Pixel[] p, float x, float y, float a, int tType, float power, ParticleSystem ps)
    {
        super(p, x, y, a, ps, Constants.DropType.THRUSTER);
        thrustType = tType;
        thrustPower = power;
        setColor();
    }

    public void setColor()
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

    @Override
    public float getVal()
    {
        return thrustPower;
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

}
