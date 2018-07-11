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
    public Constants.ModType modType;

    public int modLevel;

    private float modValue = 1;

    public ModComponent(Pixel[] p, float x, float y, float a, Constants.ModType mType, int mLevel, ParticleSystem ps)
    {
        super(p, x, y, a, ps, Constants.DropType.MOD);
        modType = mType;
        modLevel = mLevel;
        switch (modType)
        {
            case FIRERATE: modValue += .25 * mLevel; break;
            case EXTRASHOTS: modValue = mLevel + 1; break;
            case PRECISION: modValue -= .25 * mLevel; break;
        }
        r = 1;
        g = 0;
        b = 1;
    }

    public Drop modifyGun(Drop drop)
    {
        if(drop != null && drop.component != null)
        {
            Gun gun = ((GunComponent)drop.component).gun;
            switch (modType)
            {
                case FIRERATE:
                    gun.shootDelay /= modValue;
                    break;
                case EXTRASHOTS:
                    gun.numShots *= modValue;
                    break;
                case PRECISION:
                    gun.spread *= modValue;
                    break;
            }
            gun.updateBulletPool();
            return drop;
        }
        else
        {
            return null;
        }
    }

    public Drop unmodifyGun(Drop drop)
    {
        if(drop != null && drop.component != null)
        {
            Gun gun = ((GunComponent)drop.component).gun;
            switch (modType)
            {
                case FIRERATE:
                    gun.shootDelay *= modValue;
                    break;
                case EXTRASHOTS:
                    gun.numShots /= modValue;
                    break;
                case PRECISION:
                    gun.spread /= modValue;
                    break;
            }
            return drop;
        }
        else
        {
            return null;
        }
    }
}
