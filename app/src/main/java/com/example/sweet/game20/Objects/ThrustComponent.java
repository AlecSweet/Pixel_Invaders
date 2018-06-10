package com.example.sweet.game20.Objects;

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

    public ThrustComponent(Pixel[] p, int t, float x, float y, float a, int tType, float power, ParticleSystem ps)
    {
        super(p, t, x, y, a, ps);
        thrustType = tType;
        thrustPower = power;
    }
}
