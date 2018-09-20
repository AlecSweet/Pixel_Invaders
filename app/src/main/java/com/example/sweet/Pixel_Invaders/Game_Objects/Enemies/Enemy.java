package com.example.sweet.Pixel_Invaders.Game_Objects.Enemies;

import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Drop;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ModComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Bullet;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Collidable;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Drawable;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.GunComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;

import static android.opengl.GLES20.glUniform1f;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.BULLET_SPEED;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.EXTRA_GUN;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.EXTRA_MOD;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.EXTRA_SHOTS;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.FIRE_RATE;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.GUN;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.PIERCING;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.RESTORATION;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.TEMPORAL;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.PLATING;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.PRECISION;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.DropType.THRUSTER;

/**
 * Created by Sweet on 2/14/2018.
 */

public abstract class Enemy extends Drawable
{
    protected PixelGroup enemyBody;

    protected ParticleSystem particleSystem;

    protected DropFactory dropFactory;

    protected GlobalInfo globalInfo;

    GunComponent[] guns;

    protected ThrustComponent[] thrusters;

    public Drop[] dropsToAdd = new Drop[3];
    public Drop[] consumables = new Drop[1];
    protected int consumIndex = 0;

    float
            spawnDelay,
            creationTime,
            levelStartTime,
            baseSpeed,
            xbound,
            ybound,
            rotateSpeed;

    public boolean inBackground = false, isAsteriod = false;

    public float
            backgroundX,
            backgroundY;

    protected volatile float
            x,
            y;

    boolean hasGun = false;

    public volatile boolean
            uiRemoveConsensus = false,
            aiRemoveConsensus = false,
            collisionRemoveConsensus = false,
            inRange = false,
            spawned = false;

    public boolean
            live = true,
            checkOverlap = true,
            aiRecognized = false,
            collisionRecognized = false;

    public Enemy(PixelGroup p, ParticleSystem ps, DropFactory dF, float xb, float yb, GlobalInfo gI)
    {
        xbound = xb + p.halfSquareLength;
        ybound = yb + p.halfSquareLength;
        particleSystem = ps;
        enemyBody = p;
        enemyBody.setEnableOrphanChunkDeletion(true);
        dropFactory = dF;
        spawnDelay = 0;
        globalInfo = gI;
    }

    public Enemy(PixelGroup p, ParticleSystem ps, DropFactory dF, float xb, float yb, float delay, GlobalInfo gI)
    {
        xbound = xb + p.halfSquareLength;
        ybound = yb + p.halfSquareLength;
        particleSystem = ps;
        enemyBody = p;
        enemyBody.setEnableOrphanChunkDeletion(true);
        dropFactory = dF;
        spawnDelay = delay;
        globalInfo = gI;
        creationTime = globalInfo.getAugmentedTimeMillis();
        generateLocation();
        enemyBody.resetLocationHistory(x,y);
    }

    public void move(float mX, float mY)
    {
    }

    public boolean checkSpawned()
    {
        if(globalInfo.getAugmentedTimeMillis() - levelStartTime > spawnDelay)
        {
            spawned = true;
            return spawned;
        }
        else
        {
            return false;
        }
    }

    public void setLevelStartTime(float sT)
    {
        levelStartTime = sT;
    }

    public void rotate(float angleMoving, float ratio)
    {
    }

    public void rotate(float angleMoving, float rotateSpeed, float slow)
    {
        float delta = enemyBody.angle - angleMoving;
        if (delta > rotateSpeed || delta < -rotateSpeed)
        {
            if (delta < -Math.PI || (delta > 0 && delta < Math.PI))
            {
                enemyBody.angle -= rotateSpeed * slow;
            }
            else
            {
                enemyBody.angle += rotateSpeed * slow;
            }
        }
        else
        {
            enemyBody.angle = angleMoving;
        }

        enemyBody.rotate(enemyBody.angle);

        if (enemyBody.angle > Math.PI)
        {
            enemyBody.angle -= Constants.twoPI;
        }
        else if (enemyBody.angle < -Math.PI)
        {
            enemyBody.angle += Constants.twoPI;
        }
    }

    void setLoc(float sX, float sY)
    {
        x = sX;
        y = sY;
        enemyBody.setLoc(sX, sY);
        enemyBody.move(0, 0);
    }

    public void knockBack(float angle, float extraRatio, float dist)
    {
        float tempDistX = (float) (dist * extraRatio * Math.cos(angle));
        float tempDistY = (float) (dist * extraRatio * Math.sin(angle));
        x += tempDistX;
        y += tempDistY;
        enemyBody.knockBack(tempDistX, tempDistY);
    }

    @Override
    public void draw(double interpolation)
    {
        if(spawned)
        {
            if (onScreen && getPixelGroup().getCollidableLive())
            {
                enemyBody.draw();
            }

            if (getHasGun())
            {
                for (GunComponent gC : getGunComponents())
                {
                    if (gC != null)
                    {
                        gC.gun.draw(0);
                    }
                }
            }
        }
    }

    public void drawInBackground()
    {
        enemyBody.softDraw(backgroundX, backgroundY, enemyBody.cosA, enemyBody.sinA, 0f, .5f, globalInfo.pointSize * .5f);
    }

    public void setShift(int locX, int locY)
    {
        glUniform1f(locX, (-globalInfo.screenShiftX));
        glUniform1f(locY, (-globalInfo.screenShiftY));
    }

    public void addThrustParticles(Pixel[] pixels, float ratio, float dist, Collidable c)
    {
        for (Pixel p : pixels)
        {
            //if(p.live)
            if (p != null && p.state >= 1)
            {
                float xDisp = c.infoMap[p.row][p.col].xOriginal * enemyBody.cosA +
                        c.infoMap[p.row][p.col].yOriginal * enemyBody.sinA;
                float yDisp = c.infoMap[p.row][p.col].yOriginal * enemyBody.cosA -
                        c.infoMap[p.row][p.col].xOriginal * enemyBody.sinA;
                for (int t = 0; t < 2; t++)
                {
                    particleSystem.addParticle(xDisp + enemyBody.centerX,
                            yDisp + enemyBody.centerY,
                            -enemyBody.angle + (float) Math.PI,
                            c.infoMap[p.row][p.col].r,
                            c.infoMap[p.row][p.col].g,
                            c.infoMap[p.row][p.col].b,
                            .7f,
                            (baseSpeed * thrusters[0].getThrustPower() * (float) (Math.random() * 70 + 20)) * ratio,
                            dist * ratio * (float) Math.random() * 2,
                            (float) (Math.random() * 20)
                    );
                }
            }
        }
    }

    public void collisionOccured()
    {
        if(guns != null)
        {
            for (GunComponent gC : guns)
            {
                if (gC != null)
                {
                    gC.checkAlive();
                }
            }
        }

        if(thrusters != null)
        {
            for (ThrustComponent tC : thrusters)
            {
                if (tC != null)
                {
                    tC.checkAlive();
                }
            }
        }
    }

    public void shoot()
    {
    }

    private void generateLocation()
    {
        switch((int)(Math.random()*3.99))
        {
            case 0:
                setLoc((float) (Math.random() * xbound * 2) - xbound, ybound);
                break;
            case 1:
                setLoc((float) (Math.random() * xbound * 2) - xbound, -ybound);
                break;
            case 2:
                setLoc(xbound, (float) (Math.random() * ybound * 2) - ybound);
                break;
            case 3:
                setLoc(-xbound, (float) (Math.random() * xbound * 2) - xbound);
                break;
        }
    }

    public PixelGroup getPixelGroup()
    {
        return enemyBody;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public boolean getHasGun()
    {
        return hasGun;
    }

    public GunComponent[] getGunComponents()
    {
        return guns;
    }

    public void setBounds(float xb, float yb)
    {
        xbound = xb + getPixelGroup().halfSquareLength;
        ybound = yb + getPixelGroup().halfSquareLength;
    }

    public Enemy clone(float difficulty, float delay)
    {
        return null;
    }

    public void publishLocation(long frame)
    {
        if(spawned)
        {
            if (enemyBody.getEnableLocationChain())
            {
                enemyBody.publishLocation(frame);
            }
            if (getHasGun())
            {
                for (GunComponent gC : getGunComponents())
                {
                    if (gC != null)
                    {
                        gC.gun.publishLocation(frame);
                    }
                }
            }
        }
    }

    public void freeMemory()
    {
        if (getHasGun())
        {
            for (GunComponent gC : getGunComponents())
            {
                boolean dropped = false;
                for(Drop d: dropsToAdd)
                {
                    if (d != null && d.component != null && gC == d.component)
                    {
                        dropped = true;
                    }
                }
                if (gC != null && !dropped)
                {
                    for (Bullet b : gC.gun.getBullets())
                    {
                        b.freeResources();
                    }
                }
            }
        }
        getPixelGroup().freeMemory();
    }

    public void generateDrops(GunComponent gC, ThrustComponent tC, float difficulty, float modifier)
    {
        for(int i = 0; i < 3; i++)
        {
            float temp = (float)Math.random();
            if(temp < .2f)
            {
                if(gC != null)
                {
                    dropsToAdd[i] = dropFactory.getNewDrop(GUN, x, y, gC);
                }
                else
                {
                    ModComponent mC = getMod(difficulty, modifier);
                    dropsToAdd[i] = dropFactory.getNewDrop(mC.type, x, y, mC);
                }
            }
            else if(temp < .4f)
            {
                if(tC != null)
                {
                    dropsToAdd[i] = dropFactory.getNewDrop(THRUSTER, x, y, tC);
                }
                else
                {
                    ModComponent mC = getMod(difficulty, modifier);
                    dropsToAdd[i] = dropFactory.getNewDrop(mC.type, x, y, mC);
                }
            }
            else
            {
                ModComponent mC = getMod(difficulty, modifier);
                dropsToAdd[i] = dropFactory.getNewDrop(mC.type, x, y, mC);
            }
            if(Math.random() < .6)
            {
                break;
            }
        }

        float extraNum = (float)Math.random();
        if(extraNum < .33f)
        {
            extraNum = (float)Math.random();
            if(extraNum < globalInfo.extraGunChance)
            {
                consumables[0] = dropFactory.getNewDrop(EXTRA_GUN, x, y);
                if(globalInfo.extraGunChance > .005)
                {
                    globalInfo.extraGunChance /= 5f;
                }
            }
        }
        else
        {
            extraNum = (float)Math.random();
            if(extraNum < globalInfo.extraModChance)
            {
                consumables[0] = dropFactory.getNewDrop(EXTRA_MOD, x, y);
                if(globalInfo.extraModChance > .005)
                {
                    globalInfo.extraModChance /= 5f;
                }
            }
        }
    }

    private ModComponent getMod(float difficulty, float modifier)
    {
        /*FIRE_RATE,
                EXTRA_SHOTS,
                PRECISION,
                PLATING,
                PIERCING,
                TEMPORAL,
                BULLET_SPEED,
                RESTORATION*/
        Constants.DropType dT = FIRE_RATE;
        switch((int)(Math.random()*7.99))
        {
            case 0: dT = FIRE_RATE; break;
            case 1: dT = EXTRA_SHOTS; break;
            case 2: dT = PRECISION; break;
            case 3: dT = PLATING; break;
            case 4: dT = PIERCING; break;
            case 5: dT = TEMPORAL; break;
            case 6: dT = BULLET_SPEED; break;
            case 7: dT = RESTORATION; break;
        }
        return new ModComponent(null, x, y, 0, dT, (int)difficulty + 1);
    }

}
