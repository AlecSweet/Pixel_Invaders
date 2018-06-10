package com.example.sweet.game20.util;

import com.example.sweet.game20.Objects.BasicGun;
import com.example.sweet.game20.Objects.Enemy;
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

    public EnemyFactory()
    {
    }

    public Enemy getNewEnemy(EnemyType eT)
    {
        return enemyCatalog.get(eT).clone();
    }

    public void addEnemyToCatalog(EnemyType eT, Enemy e)
    {
        enemyCatalog.put(eT, e);
    }

}
