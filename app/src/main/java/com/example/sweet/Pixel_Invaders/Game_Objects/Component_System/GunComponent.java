package com.example.sweet.Pixel_Invaders.Game_Objects.Component_System;

import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;

/**
 * Created by Sweet on 4/30/2018.
 */

public class GunComponent extends Component
{
    public Gun gun;

    public GunComponent(Pixel[] p, float x, float y, float a, Gun g)
    {
        super(p, x, y, a, Constants.DropType.GUN);
        gun = g;
        gun.x = x;
        gun.y = y;
        setColor();
    }

    private void setColor()
    {
        float pps = (float)gun.pixelGroupTemplate.totalPixels * (1000 / (float)gun.shootDelay);
        float halfMaxPPs = Constants.MAX_PPS /2;
        b = 1;
        g = 1;
        r = 1;
        if(pps < halfMaxPPs)
        {
            b -= pps / halfMaxPPs;
            g = 1;
        }
        else if(pps < Constants.MAX_PPS && pps >= halfMaxPPs)
        {
            b = 0;
            g -= (pps - halfMaxPPs) / halfMaxPPs;
        }
        else
        {
            g = 0;
            b = 0;
        }
    }

    public void setGunOffset(float xO, float yO)
    {
        if(gun != null)
        {
            gun.x = xO;
            gun.y = yO;
        }
    }

    public boolean shoot(float cX, float cY, float angle, GlobalInfo gI, float cosA, float sinA, float slowRed)
    {
        checkAlive();
        return live && gun.shoot(cX, cY, angle, gI, cosA, sinA, slowRed);
    }

    public boolean shoot(float cX, float cY, float angle, GlobalInfo gI)
    {
        checkAlive();
        return live && gun.shoot(cX, cY, angle, gI);
    }

    public boolean canShoot(GlobalInfo gI)
    {
        return gun.canShoot(gI);
    }

    public void move(GlobalInfo gI)
    {
        gun.move(gI);
    }
}
