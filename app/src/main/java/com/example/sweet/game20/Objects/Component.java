package com.example.sweet.game20.Objects;

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
    public int
            type,
            texture;

    public float
            x,
            y,
            angle;

    protected ParticleSystem masterParticleSystem;

    public Component()
    {

    }

    public Component(Pixel[] p, int t, float x, float y, float a, ParticleSystem ps)
    {
        masterParticleSystem = ps;
        attachmentPixels = p;
        type = t;
        this.x = x;
        this.y = y;
        angle = a;
    }

    public void draw()
    {

    }
}
