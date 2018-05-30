package com.example.sweet.game20.Objects;

import java.util.ArrayList;

/**
 * Created by Sweet on 4/30/2018.
 */

public class ModComponent extends Component
{
    /*  0: Fire Rate
        1: Extra Shots
        2: Pixel Plating
        3: Extra Guns
        4: Extra Mods
        5: Shield
     */
    public int modType;

    public int modLevel;

    public ModComponent(Pixel[] p, int t, float x, float y, float a, int mType, int mLevel, ParticleSystem ps)
    {
        super(p, t, x, y, a, ps);
        modType = mType;
        modLevel = mLevel;
    }
}
