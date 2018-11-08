package com.example.sweet.Pixel_Invaders.Util.Factories;

import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Enemy;
import com.example.sweet.Pixel_Invaders.Game_Objects.Level;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Callable;

import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.EntityType.*;

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
    private EntityFactory entityFactory;
    private Comparator<Enemy> comparator;

    public LoadLevel(int difficulty, EntityFactory eF)
    {
        enemies = new Enemy[Constants.ENTITIES_LENGTH];
        backgroundEnemies = new Enemy[20];
        this.difficulty = difficulty;
        points = difficulty * 10 + 10;
        entityFactory = eF;
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
            switch((int)(Math.random()*4.5))
            {
                case 0: enemies[iter] = entityFactory.getNewEntity(ASTEROID_GREY_TINY, difficulty, (float)Math.random() * .5f * delay); break;
                case 1: enemies[iter] = entityFactory.getNewEntity(ASTEROID_GREY_SMALL, difficulty, (float)Math.random() * .5f * delay); break;
                case 2: enemies[iter] = entityFactory.getNewEntity(ASTEROID_RED_SMALL, difficulty, (float)Math.random() * .5f * delay); break;
                case 3: enemies[iter] = entityFactory.getNewEntity(ASTEROID_RED_TINY, difficulty, (float)Math.random() * .5f * delay); break;
                case 4: switch ((int)(Math.random()*1.5))
                        {
                            case 0: enemies[iter] = entityFactory.getNewEntity(ASTEROID_GREY_MEDIUM, difficulty, (float)Math.random() * .5f * delay); break;
                            case 1: enemies[iter] = entityFactory.getNewEntity(ASTEROID_RED_MEDIUM, difficulty, (float)Math.random() * .5f * delay); break;
                        } break;
            }
            backgroundEnemies[i] = enemies[iter];
            iter++;
        }
        if(difficulty == 0)
        {
            enemies[iter] = entityFactory.getNewEntity(CARRIER,0,0);
            //enemies[iter].getPixelGroup().getHitBox().printHitbox(0);
            enemies[iter].setLoc(0,0);
        }
        else if(difficulty % 5 !=0 || difficulty == 0)
        {
            while (points >= 0)
            {
                enemies[iter] = decideOnEnemy();
                iter++;
            }
            Arrays.sort(enemies, comparator);
        }
        else
        {
            enemies[iter] = entityFactory.getNewEntity(CENTIPEDE, 1, 0);
        }
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
                return entityFactory.getNewEntity(Constants.EntityType.TINY, difficulty, delay);
            }
            else if(select < Constants.tier1Per[0][difIndex] + Constants.tier1Per[1][difIndex])
            {
                points -= Constants.kamikazeVal;
                return entityFactory.getNewEntity(Constants.EntityType.KAMIKAZE, difficulty, delay);
            }
            else
            {
                points -= Constants.simpelVal;
                return entityFactory.getNewEntity(Constants.EntityType.SIMPLE, difficulty, delay);
            }
        }
        else if(tier < Constants.tierPercents[0][difIndex] + Constants.tierPercents[1][difIndex])
        {
            if(select < Constants.tier2Per[0][difIndex])
            {
                points -= Constants.heavyVal;
                return entityFactory.getNewEntity(Constants.EntityType.HEAVY, difficulty, delay);
            }
            else if(select < Constants.tier2Per[0][difIndex] + Constants.tier2Per[1][difIndex])
            {
                points -= Constants.mineLayerVal;
                return entityFactory.getNewEntity(Constants.EntityType.MINELAYER, difficulty, delay);
            }
            else
            {
                points -= Constants.pulserVal;
                return entityFactory.getNewEntity(Constants.EntityType.PULSER, difficulty, delay);
            }
        }
        else
        {
            if(select < Constants.tier3Per[0][difIndex])
            {
                points -= Constants.massAcceleratorVal;
                return entityFactory.getNewEntity(Constants.EntityType.MASSACCELERATOR, difficulty, points * 200);
            }
            else
            {
                points -= Constants.carrierVal;
                return entityFactory.getNewEntity(Constants.EntityType.CARRIER, difficulty, points * 200);
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
