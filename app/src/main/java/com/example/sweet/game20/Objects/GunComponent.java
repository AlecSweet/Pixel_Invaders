package com.example.sweet.game20.Objects;

/**
 * Created by Sweet on 4/30/2018.
 */

public class GunComponent extends Component
{
    public Gun gun;

    public GunComponent(Pixel[] p, int t, float x, float y, float a, Gun g, ParticleSystem ps)
    {
        super(p, t, x, y, a, ps);
        gun = g;
    }
}
