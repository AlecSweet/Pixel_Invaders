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
    public Constants.DropType modType;

    public int modLevel;

    private float modValue = 1;

    public ModComponent(Pixel[] p, float x, float y, float a, Constants.DropType mType, int mLevel, ParticleSystem ps)
    {
        super(p, x, y, a, ps, Constants.DropType.MOD);
        modType = mType;
        modLevel = mLevel;
        if(modLevel > 4)
        {
            modLevel = 4;
        }
        switch (modType)
        {
            case FIRE_RATE:
                modValue += .25 * mLevel;
                break;
            case EXTRA_SHOTS:
                modValue = mLevel;
                break;
            case PRECISION:
                modValue -= .25 * mLevel;
                break;
            case RESTORATION:
                modValue += .18 * mLevel;
                break;
            case PIERCING:
                if(modLevel > 3)
                {
                    modLevel = 3;
                }
                modValue = mLevel;
                break;
            case PLATING:
                if(modLevel > 3)
                {
                    modLevel = 3;
                }
                modValue = mLevel;
                break;
            case TEMPORAL:
                modValue += .25 * mLevel;
                break;
            case BULLET_SPEED:
                modValue += .5 * mLevel;
                break;

        }
    }

    public void modify(Drop[] drops, PixelGroup group)
    {
        if(modType == Constants.DropType.RESTORATION || modType == Constants.DropType.PLATING)
        {
            for(Drop d: drops)
            {
                modifyGun(d);
            }
        }
        else
        {
            modifyGroup(group);
        }
    }

    public void unmodify(Drop[] drops, PixelGroup group)
    {
        if(modType == Constants.DropType.RESTORATION || modType == Constants.DropType.PLATING)
        {
            for(Drop d: drops)
            {
                unmodifyGun(d);
            }
        }
        else
        {
            unmodifyGroup(group);
        }
    }

    public void modifyGun(Drop drop)
    {
        if(drop != null && drop.component != null)
        {
            Gun gun = ((GunComponent)drop.component).gun;
            boolean bulletPoolUpdate = false;
            switch (modType)
            {
                case FIRE_RATE:
                    gun.shootDelay /= modValue;
                    bulletPoolUpdate = true;
                    break;
                case EXTRA_SHOTS:
                    gun.numShots += modValue;
                    bulletPoolUpdate = true;
                    break;
                case PRECISION:
                    gun.spread *= modValue;
                    break;
                case BULLET_SPEED:
                    gun.speed *= modValue;
                    break;
                case PIERCING:
                    gun.addMaxHealth((int)modValue);
                    break;
            }
            if(bulletPoolUpdate)
            {
                gun.updateBulletPool();
            }
        }
    }

    public void unmodifyGun(Drop drop)
    {
        if(drop != null && drop.component != null)
        {
            Gun gun = ((GunComponent)drop.component).gun;
            switch (modType)
            {
                case FIRE_RATE:
                    gun.shootDelay *= modValue;
                    break;
                case EXTRA_SHOTS:
                    gun.numShots -= modValue;
                    break;
                case PRECISION:
                    gun.spread /= modValue;
                    break;
                case BULLET_SPEED:
                    gun.speed /= modValue;
                    break;
                case PIERCING:
                    gun.reduceMaxHealth((int)modValue);
                    break;
            }
        }
    }

    public void modifyGroup(PixelGroup group)
    {
        switch (modType)
        {
            case RESTORATION:
                group.regen = true;
                group.regenDelay /= modValue;
                break;
            case PLATING:
                group.addMaxHealth((int)modValue);
                break;
        }
    }

    public void unmodifyGroup(PixelGroup group)
    {
        switch (modType)
        {
            case RESTORATION:
                group.regen = false;
                group.regenDelay *= modValue;
                break;
            case PLATING:
                group.reduceMaxHealth((int)modValue);
                break;
        }
    }
}
