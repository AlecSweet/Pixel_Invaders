package com.example.sweet.Pixel_Invaders.Util.Factories;

import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Enemy;

import java.util.HashMap;

/**
 * Created by Sweet on 6/9/2018.
 */

public class EnemyFactory
{
    private HashMap<Constants.EnemyType,Enemy> enemyCatalog = new HashMap<>();

    public EnemyFactory()
    {
    }

    Enemy getNewEnemy(Constants.EnemyType eT, float difficulty, float delay)
    {
        return enemyCatalog.get(eT).clone(difficulty, delay);
    }

    public void addEnemyToCatalog(Constants.EnemyType eT, Enemy e)
    {
        enemyCatalog.put(eT, e);
    }

    public void setBounds(float xb, float yb)
    {
        distributeBounds(xb, yb);
    }

    private void distributeBounds(float xb, float yb)
    {
        enemyCatalog.get(Constants.EnemyType.SIMPLE).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.KAMIKAZE).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.PULSER).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.CARRIER).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.TINY).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.HEAVY).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.MASSACCELERATOR).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.MINELAYER).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.ASTEROID_GREY_MEDIUM).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.ASTEROID_GREY_SMALL).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.ASTEROID_GREY_TINY).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.ASTEROID_RED_MEDIUM).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.ASTEROID_RED_SMALL).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.ASTEROID_RED_TINY).setBounds(xb, yb);
        enemyCatalog.get(Constants.EnemyType.CENTIPEDE).setBounds(xb, yb);
    }

}
