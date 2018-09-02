package com.example.sweet.game20.Objects;

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
