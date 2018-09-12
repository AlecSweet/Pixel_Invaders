package com.example.sweet.Pixel_Invaders.Util.Factories;

import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Enemy;
import com.example.sweet.Pixel_Invaders.Game_Objects.Level;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Callable;

import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.EnemyType.*;

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
    private Comparator<Enemy> comparator;

    public LoadLevel(int difficulty, EnemyFactory eF)
    {
        enemies = new Enemy[Constants.ENTITIES_LENGTH];
        backgroundEnemies = new Enemy[20];
        this.difficulty = difficulty;
        points = difficulty * 10 + 10;
        enemyFactory = eF;
        comparator = new Comparator<Enemy>()
        {
            @Override
            public int compare(Enemy e, Enemy e2)
            {
                if(e != null && e2 != null)
                {
                    if(e.getPixelGroup().totalPixels > e2.getPixelGroup().totalPixels)
                    {
                        return -1;
                    }
                    else if(e.getPixelGroup().totalPixels < e2.getPixelGroup().totalPixels)
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                }
                else if(e != null && e2 == null)
                {
                    return -1;
                }
                else if(e2 != null && e == null)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
        };
    }

    @Override
    public Level call()
    {
        int iter = 0;
        float delay = (float)Math.sqrt(points) * 400f + 2000f;
        if(delay < 1000f)
        {
            delay = 1000f;
        }
        for(int i = 0; i < difficulty / 2 + 4; i++)
        {
            switch((int)(Math.random()*5.99))
            {
                case 0: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_GREY_TINY, difficulty, (float)Math.random() * .5f * delay); break;
                case 1: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_GREY_SMALL, difficulty, (float)Math.random() * .5f * delay); break;
                case 2: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_GREY_TINY, difficulty, (float)Math.random() * .5f * delay); break;
                case 3: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_RED_SMALL, difficulty, (float)Math.random() * .5f * delay); break;
                case 4: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_RED_TINY, difficulty, (float)Math.random() * .5f * delay); break;
                case 5: enemies[iter] = enemyFactory.getNewEnemy(ASTEROID_RED_MEDIUM, difficulty, (float)Math.random() * .5f * delay); break;
            }
            backgroundEnemies[i] = enemies[iter];
            iter++;
        }
        while(points >= 0)
        {
            enemies[iter] = decideOnEnemy();
            iter++;
        }
        Arrays.sort(enemies,comparator);
        return new Level(enemies, backgroundEnemies);
    }

    private Enemy decideOnEnemy()
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
        
        float delay = (float)Math.sqrt(points)*400f + 2000f;
        if(delay < 1000f)
        {
            delay = 1000f;
        }
        if(tier < Constants.tierPercents[0][difIndex])
        {
            if(select < Constants.tier1Per[0][difIndex])
            {
                points -= Constants.tinyVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.TINY, difficulty, delay);
            }
            else if(select < Constants.tier1Per[0][difIndex] + Constants.tier1Per[1][difIndex])
            {
                points -= Constants.kamikazeVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.KAMIKAZE, difficulty, delay);
            }
            else
            {
                points -= Constants.simpelVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.SIMPLE, difficulty, delay);
            }
        }
        else if(tier < Constants.tierPercents[0][difIndex] + Constants.tierPercents[1][difIndex])
        {
            if(select < Constants.tier2Per[0][difIndex])
            {
                points -= Constants.heavyVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.HEAVY, difficulty, delay);
            }
            else if(select < Constants.tier2Per[0][difIndex] + Constants.tier2Per[1][difIndex])
            {
                points -= Constants.mineLayerVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.MINELAYER, difficulty, delay);
            }
            else
            {
                points -= Constants.pulserVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.PULSER, difficulty, delay);
            }
        }
        else
        {
            if(select < Constants.tier3Per[0][difIndex])
            {
                points -= Constants.massAcceleratorVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.MASSACCELERATOR, difficulty, points * 200);
            }
            else
            {
                points -= Constants.carrierVal;
                return enemyFactory.getNewEnemy(Constants.EnemyType.CARRIER, difficulty, points * 200);
            }
        }
    }

    public void resetLoadLevel(int difficulty)
    {
        Arrays.fill(enemies, null);
        Arrays.fill(backgroundEnemies, null);
        //System.out.println(difficulty);
        this.difficulty = difficulty;
        points = difficulty * 10 + 10;
    }
}
