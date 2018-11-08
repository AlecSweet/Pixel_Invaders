package com.example.sweet.Pixel_Invaders.Util.Factories;

import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Enemy;

import java.util.HashMap;

/**
 * Created by Sweet on 6/9/2018.
 */

public class EntityFactory
{
    private HashMap<Constants.EntityType,Enemy> enemyCatalog = new HashMap<>();

    public EntityFactory()
    {
    }

    Enemy getNewEntity(Constants.EntityType eT, float difficulty, float delay)
    {
        return enemyCatalog.get(eT).clone(difficulty, delay);
    }

    public void addEntityToCatalog(Constants.EntityType eT, Enemy e)
    {
        enemyCatalog.put(eT, e);
    }

    public void setBounds(float xb, float yb)
    {
        distributeBounds(xb, yb);
    }

    private void distributeBounds(float xb, float yb)
    {
        enemyCatalog.get(Constants.EntityType.SIMPLE).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.KAMIKAZE).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.PULSER).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.CARRIER).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.TINY).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.HEAVY).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.MASSACCELERATOR).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.MINELAYER).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.ASTEROID_GREY_MEDIUM).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.ASTEROID_GREY_SMALL).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.ASTEROID_GREY_TINY).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.ASTEROID_RED_MEDIUM).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.ASTEROID_RED_SMALL).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.ASTEROID_RED_TINY).setBounds(xb, yb);
        enemyCatalog.get(Constants.EntityType.CENTIPEDE).setBounds(xb, yb);
    }

}
