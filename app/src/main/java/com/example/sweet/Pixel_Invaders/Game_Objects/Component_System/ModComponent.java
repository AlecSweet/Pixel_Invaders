package com.example.sweet.Pixel_Invaders.Game_Objects.Component_System;


import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;

/**
 * Created by Sweet on 4/30/2018.
 */

public class ModComponent extends Component
{
    private Constants.DropType modType;

    private int modLevel;

    private float modValue = 1;

    public ModComponent(Pixel[] p, float x, float y, float a, Constants.DropType mType, int mLevel)
    {
        super(p, x, y, a, mType);
        modType = mType;
        modLevel = mLevel;
        if(modLevel > 4)
        {
            modLevel = 4;
        }
        switch (modType)
        {
            case FIRE_RATE:
                modValue += .1 * mLevel;
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
                modValue += .5 * mLevel;
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
            modifyGroup(group);
        }
        else
        {
            for(Drop d: drops)
            {
                modifyGun(d);
            }
        }
    }

    public void unmodify(Drop[] drops, PixelGroup group)
    {
        if(modType == Constants.DropType.RESTORATION || modType == Constants.DropType.PLATING)
        {
            unmodifyGroup(group);
        }
        else
        {
            for(Drop d: drops)
            {
                unmodifyGun(d);
            }
        }
    }

    private void modifyGun(Drop drop)
    {
        if(drop != null && drop.component != null)
        {
            Gun gun = ((GunComponent)drop.component).gun;
            boolean bulletPoolUpdate = false;
            switch (modType)
            {
                case FIRE_RATE:
                    gun.fireRateMod /= modValue;
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
                    gun.setSpeed(gun.speed * modValue);
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

    private void unmodifyGun(Drop drop)
    {
        if(drop != null && drop.component != null)
        {
            Gun gun = ((GunComponent)drop.component).gun;
            switch (modType)
            {
                case FIRE_RATE:
                    gun.fireRateMod *= modValue;
                    break;
                case EXTRA_SHOTS:
                    gun.numShots -= modValue;
                    break;
                case PRECISION:
                    gun.spread /= modValue;
                    break;
                case BULLET_SPEED:
                    gun.setSpeed(gun.speed / modValue);
                    break;
                case PIERCING:
                    gun.reduceMaxHealth((int)modValue);
                    break;
            }
        }
    }

    private void modifyGroup(PixelGroup group)
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
            case TEMPORAL:
                group.temporal = true;
                group.slowResist /= modValue;
                break;
        }
    }

    private void unmodifyGroup(PixelGroup group)
    {
        switch (modType)
        {
            case RESTORATION:
                group.regenDelay *= modValue;
                break;
            case PLATING:
                group.reduceMaxHealth((int)modValue);
                break;
            case TEMPORAL:
                group.slowResist *= modValue;
                break;
        }
    }

    public int getModLevel()
    {
        return modLevel;
    }
}
