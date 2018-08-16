package com.example.sweet.game20;

import com.example.sweet.game20.Objects.Asteroid;
import com.example.sweet.game20.Objects.BasicGun;
import com.example.sweet.game20.Objects.Carrier;
import com.example.sweet.game20.Objects.Drop;
import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.GunComponent;
import com.example.sweet.game20.Objects.ModComponent;
import com.example.sweet.game20.Objects.Player;
import com.example.sweet.game20.Objects.Simple;
import com.example.sweet.game20.util.CollisionThread;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.EnemyFactory;

import static com.example.sweet.game20.util.Constants.EnemyType.*;

import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Sweet on 6/8/2018.
 */

public class LevelControllerThread implements Runnable
{
    public Player player1;

    public ConcurrentLinkedQueue<Enemy> enemiesToAdd = new ConcurrentLinkedQueue<>();

    private EnemyFactory enemyFactory;

    private GlobalInfo globalInfo;

    private Stack<Integer> openEntityIndices = new Stack<>();

    public volatile boolean
            running = true,
            pause = false;

    private boolean saveTime = false;

    public float
            xbound,
            ybound;

    private double
            lastSpawn,
            spawnDelay = 4000,
            pauseTime = 0;

    public LevelControllerThread(EnemyFactory eF, GlobalInfo gI)
    {
        globalInfo = gI;
        enemyFactory = eF;
        for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            openEntityIndices.add(i);
        }
    }

    public void run()
    {
        while(running)
        {
            if(!pause)
            {
                if(!saveTime)
                {
                    double pauseLength = System.currentTimeMillis() - pauseTime;
                    lastSpawn += pauseLength;
                    saveTime = true;
                }
                levelController();
            }
            else
            {
                if(saveTime)
                {
                    pauseTime = System.currentTimeMillis();
                    saveTime = false;
                }
            }
        }
    }

    public boolean spawnonce =  false;
    public void levelController()
    {
        /*if(false == true)
        {
            for(int i = 0; i < difficulty; i++)
            {
                Simple s = new Simple(
                        simple.clone(),
                        new BasicGun(whiteTexture, shaderProgram, particleShaderProgram, context, enemyParticles),
                        enemyParticles,
                        0);
                s.setLoc((float) (Math.random() * 2 - 1), (float) (Math.random() * 2 - 1));
                Simple s2 = new Simple(
                        simple.clone(),
                        new BasicGun(whiteTexture, shaderProgram, particleShaderProgram, context, enemyParticles),
                        enemyParticles,
                        0);
                s2.setLoc((float) (Math.random() * 2 - 1), (float) (Math.random() * 2 - 1));
            }
        }*/


        if(System.currentTimeMillis() - spawnDelay > lastSpawn)
        {
            lastSpawn = System.currentTimeMillis();
            System.out.println("Spawned");
            Asteroid a = null;

            switch((int)(Math.random()*5.99))
            {
                case 0: enemiesToAdd.add(enemyFactory.getNewEnemy(ASTEROID_GREY_TINY)); break;
                case 1: enemiesToAdd.add(enemyFactory.getNewEnemy(ASTEROID_GREY_SMALL)); break;
                case 2: enemiesToAdd.add(enemyFactory.getNewEnemy(ASTEROID_GREY_TINY)); break;
                case 3: enemiesToAdd.add(enemyFactory.getNewEnemy(ASTEROID_RED_SMALL)); break;
                case 4: enemiesToAdd.add(enemyFactory.getNewEnemy(ASTEROID_RED_TINY)); break;
                case 5: enemiesToAdd.add(enemyFactory.getNewEnemy(ASTEROID_RED_MEDIUM)); break;
            }

            if (!spawnonce)
            {
                enemiesToAdd.add(enemyFactory.getNewEnemy(CARRIER));
                /*enemiesToAdd.add(enemyFactory.getNewEnemy(ASTEROID_RED_MEDIUM));
                enemiesToAdd.add(enemyFactory.getNewEnemy(ASTEROID_GREY_MEDIUM));
                enemiesToAdd.add(enemyFactory.getNewEnemy(ASTEROID_RED_MEDIUM));
                enemiesToAdd.add(enemyFactory.getNewEnemy(ASTEROID_RED_MEDIUM));*/
            }
            //distributeEnemy(a);
            //int index = openEntityIndices.pop();
            //aiRunnable.entities[index] = a;
            //collisionRunnable.entities[index] = a;
            enemiesToAdd.add(enemyFactory.getNewEnemy(SIMPLE));
            //enemiesToAdd.add(a);
            //uiEntities[index] = a;
            spawnonce = true;
        }
        if(player1.modUpdate)
        {
            for(Drop d: player1.getGuns())
            {
                if( d != null && d.component != null)
                {
                    ((GunComponent)d.component).gun.updateBulletPool();
                }
            }
            /*for(int m = 0; m < player1.getMaxMods(); m++)
            {
                if(player1.mods[m] != null && player1.mods[m].component != null)
                {
                    for(int g = 0; g < player1.gunDrops.length; g++)
                    {
                        player1.gunDrops[g] = ((ModComponent)player1.mods[m].component).modifyGun(player1.gunDrops[g]);
                    }
                }
            }
            player1.modUpdate = false;*/
        }
    }

    /*public void distributeEnemy(Enemy e)
    {
        if(!openEntityIndices.isEmpty())
        {
            int index = openEntityIndices.pop();
            for (int r = 0; r < entityLists.length; r++)
            {
                entityLists[r][index] = e;
            }
        }
        else
        {
            enemyOverflow.add(e);
        }
    }*/

    public void setPlayer(Player p)
    {
        player1 = p;
    }
}
