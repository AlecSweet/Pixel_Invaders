package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;

import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;

/**
 * Created by Sweet on 1/16/2018.
 */
public abstract class Drawable
{
    protected ParticleSystem particleSystem;

    public volatile boolean onScreen = false;

    public void draw(double interpolation)
    {
    }

    public ParticleSystem getParticleSystem()
    {
        return particleSystem;
    }
}
