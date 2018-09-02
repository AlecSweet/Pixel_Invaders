package com.example.sweet.game20.util;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.Objects.BasicGun;
import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.Kamikaze;
import com.example.sweet.game20.Objects.MassAccelerator;
import com.example.sweet.game20.Objects.MineLayer;
import com.example.sweet.game20.Objects.ParticleSystem;
import com.example.sweet.game20.Objects.PixelGroup;
import com.example.sweet.game20.Objects.Simple;

import java.util.HashMap;

import static com.example.sweet.game20.util.Constants.EnemyType.*;
import static com.example.sweet.game20.util.Constants.EnemyType;

/**
 * Created by Sweet on 6/9/2018.
 */

public class EnemyFactory
{
    private HashMap<EnemyType,Enemy> enemyCatalog = new HashMap<>();

    private float
            xbound,
            ybound;

    public EnemyFactory()
    {
    }

    public Enemy getNewEnemy(EnemyType eT, float difficulty, float delay)
    {
        return enemyCatalog.get(eT).clone(difficulty, delay);
    }

    public void addEnemyToCatalog(EnemyType eT, Enemy e)
    {
        enemyCatalog.put(eT, e);
    }

    public void setBounds(float xb, float yb)
    {
        xbound = xb;
        ybound = yb;
        distrbuteBounds(xbound, ybound);
    }

    private void distrbuteBounds(float xb, float yb)
    {
        enemyCatalog.get(SIMPLE).setBounds(xb, yb);
        enemyCatalog.get(KAMIKAZE).setBounds(xb, yb);
        enemyCatalog.get(PULSER).setBounds(xb, yb);
        enemyCatalog.get(CARRIER).setBounds(xb, yb);
        enemyCatalog.get(TINY).setBounds(xb, yb);
        enemyCatalog.get(HEAVY).setBounds(xb, yb);
        enemyCatalog.get(MASSACCELERATOR).setBounds(xb, yb);
        enemyCatalog.get(MINELAYER).setBounds(xb, yb);
        enemyCatalog.get(ASTEROID_GREY_MEDIUM).setBounds(xb, yb);
        enemyCatalog.get(ASTEROID_GREY_SMALL).setBounds(xb, yb);
        enemyCatalog.get(ASTEROID_GREY_TINY).setBounds(xb, yb);
        enemyCatalog.get(ASTEROID_RED_MEDIUM).setBounds(xb, yb);
        enemyCatalog.get(ASTEROID_RED_SMALL).setBounds(xb, yb);
        enemyCatalog.get(ASTEROID_RED_TINY).setBounds(xb, yb);
    }

}
