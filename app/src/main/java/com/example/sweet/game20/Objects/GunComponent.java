package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.util.Constants;

/**
 * Created by Sweet on 4/30/2018.
 */

public class GunComponent extends Component
{
    public Gun gun;

    public GunComponent(Pixel[] p, float x, float y, float a, Gun g, ParticleSystem ps)
    {
        super(p, x, y, a, ps, Constants.DropType.GUN);
        gun = g;
        gun.x = x;
        gun.y = y;
        setColor();
    }

    public void setColor()
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

    public boolean shoot(float cX, float cY, float angle, GlobalInfo gI, float cosA, float sinA)
    {
        checkAlive();
        if(live)
        {
            return gun.shoot(cX, cY, angle, gI, cosA, sinA);
        }
        else
        {
            return false;
        }
    }

    public boolean shoot(float cX, float cY, float angle, GlobalInfo gI)
    {
        checkAlive();
        if(live)
        {
            return gun.shoot(cX, cY, angle, gI);
        }
        else
        {
            return false;
        }
    }

    public boolean canShoot(GlobalInfo gI)
    {
        return gun.canShoot(gI);
    }

    public void move(float slow)
    {
        gun.move(slow);
    }
}
