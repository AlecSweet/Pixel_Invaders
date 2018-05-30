package com.example.sweet.game20.Objects;

/**
 * Created by Sweet on 1/16/2018.
 */
public abstract class Drawable
{
    protected boolean shouldBeDrawn = true;

    protected ParticleSystem particleSystem;

    public boolean onScreen = false;

    public void draw(double interpolation)
    {
    }

    public void drawParticles()
    {
        particleSystem.draw();
    }

    public boolean getShouldBeDrawn()
    {
        return shouldBeDrawn;
    }

    public void setShouldBeDrawn(boolean b)
    {
        shouldBeDrawn = b;
    }

    public ParticleSystem getParticleSystem()
    {
        return particleSystem;
    }

}
