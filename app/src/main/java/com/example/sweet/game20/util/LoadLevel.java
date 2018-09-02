package com.example.sweet.game20.util;

import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.Level;
import com.example.sweet.game20.Objects.MassAccelerator;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static com.example.sweet.game20.util.Constants.EnemyType.ASTEROID_GREY_SMALL;
import static com.example.sweet.game20.util.Constants.EnemyType.ASTEROID_GREY_TINY;
import static com.example.sweet.game20.util.Constants.EnemyType.ASTEROID_RED_MEDIUM;
import static com.example.sweet.game20.util.Constants.EnemyType.ASTEROID_RED_SMALL;
import static com.example.sweet.game20.util.Constants.EnemyType.ASTEROID_RED_TINY;
import static com.example.sweet.game20.util.Constants.EnemyType.CARRIER;
import static com.example.sweet.game20.util.Constants.EnemyType.MASSACCELERATOR;

/**
 * Created by Sweet on 8/28/2018.
 */

public class LoadLevel implements Callable<Level>
{
    private int
            points,
            difficulty;
    private Enemy[] enemies;
    private Enemy[] backgroundEnemies;
    private EnemyFactory enemyFactory;

    public LoadLevel(int difficulty, EnemyFactory eF)
    {
        enemies = new Enemy[Constants.ENTITIES_LENGTH];
        backgroundEnemies = new Enemy[20];
        this.difficulty = difficulty;
        points = difficulty * 10 + 10;
        enemyFactory = eF;
    }

    @Override
    public Level call()
    {
        int iter = 0;
        while(points >= 0)
        {
            enemies[iter] = decideOnEnemy();
            iter++;
        }
        /*for(int i = 0; i < 4; i++)
        {
            enemies[iter] = enemyFactory.getNewEnemy(CARRIER, 1, 0);
            iter++;
        }*/
        /*for(int i = 0; i < difficulty / 2 + 4; i++)
        {
            switch((int)(Math.random()*5.99))
            {
                case 0: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_GREY_TINY, 1, 0); break;
                case 1: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_GREY_SMALL, 1, 0); break;
                case 2: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_GREY_TINY, 1, 0); break;
                case 3: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_RED_SMALL, 1, 0); break;
                case 4: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_RED_TINY, 1, 0); break;
                case 5: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_RED_MEDIUM, 1, 0); break;
            }
            backgroundEnemies[i] = enemies[iter];
            iter++;
        }*/
        return new Level(enemies, backgroundEnemies);
    }

    public Enemy decideOnEnemy()
    {
        float tier = (float)Math.random();
        float select = (float)Math.random();
        int difIndex;
        if(difficulty <= 9)
        {
            difIndex = difficulty;
        }
        else
        {
            difIndex = 9;
        }
        if(tier < Constants.tierPercents[0][difIndex])
        {
            if(select < Constants.tier1Per[0][difIndex])
            {
                points -= Constants.tinyVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.TINY, difficulty, points * 1000);
            }
            else if(select < Constants.tier1Per[0][difIndex] + Constants.tier1Per[1][difIndex])
            {
                points -= Constants.kamikazeVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.KAMIKAZE, difficulty, points * 1000);
            }
            else
            {
                points -= Constants.simpelVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.SIMPLE, difficulty, points * 1000);
            }
        }
        else if(tier < Constants.tierPercents[0][difIndex] + Constants.tierPercents[1][difIndex])
        {
            if(select < Constants.tier2Per[0][difIndex])
            {
                points -= Constants.heavyVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.HEAVY, difficulty, points * 1000);
            }
            else if(select < Constants.tier2Per[0][difIndex] + Constants.tier2Per[1][difIndex])
            {
                points -= Constants.mineLayerVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.MINELAYER, difficulty, points * 100);
            }
            else
            {
                points -= Constants.pulserVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.PULSER, difficulty, points * 100);
            }
        }
        else
        {
            if(select < Constants.tier3Per[0][difIndex])
            {
                points -= Constants.massAcceleratorVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.MASSACCELERATOR, difficulty, points * 100);
            }
            else
            {
                points -= Constants.carrierVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.CARRIER, difficulty, points * 100);
            }
        }
    }

    public void resetLoadLevel(int difficulty)
    {
        Arrays.fill(enemies, null);
        Arrays.fill(backgroundEnemies, null);
        System.out.println(difficulty);
        this.difficulty = difficulty;
        points = difficulty * 10 + 10;
    }
}
