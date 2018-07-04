package com.example.sweet.game20.Objects;


import com.example.sweet.game20.util.Constants;

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

    public ModComponent(Pixel[] p, float x, float y, float a, int mType, int mLevel, ParticleSystem ps)
    {
        super(p, x, y, a, ps, Constants.DropType.MOD);
        modType = mType;
        modLevel = mLevel;
    }
}
