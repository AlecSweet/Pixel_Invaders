package com.example.sweet.Pixel_Invaders.Game_Objects;

import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Enemy;

/**
 * Created by Sweet on 8/28/2018.
 */

public class Level
{
    public Enemy[] levelEnemies;
    public Enemy[] backgroundEnemies;

    public Level(Enemy[] lE, Enemy[] bE)
    {
        levelEnemies = lE;
        backgroundEnemies = bE;
    }
}
