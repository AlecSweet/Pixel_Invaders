package com.example.sweet.game20.Objects;

import com.example.sweet.game20.util.Constants;

/**
 * Created by Sweet on 4/15/2018.
 */

public class Component
{
    public Pixel[] attachmentPixels;

    public boolean live = true;

    /*  0: Thruster
        1: Gun
        2: Mod
     */
    public Constants.DropType type;

    public float
            x,
            y,
            angle;

    public float
            r,
            g,
            b;

    public Gun gun = null;

    protected ParticleSystem masterParticleSystem;

    public Component()
    {

    }

    public Component(Pixel[] p, float x, float y, float a, ParticleSystem ps, Constants.DropType dT)
    {
        masterParticleSystem = ps;
        attachmentPixels = p;
        type = dT;
        this.x = x;
        this.y = y;
        angle = a;
    }

    public void draw()
    {

    }
}
