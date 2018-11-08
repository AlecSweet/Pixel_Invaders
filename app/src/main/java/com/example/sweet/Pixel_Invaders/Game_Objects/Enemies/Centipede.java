package com.example.sweet.Pixel_Invaders.Game_Objects.Enemies;

import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Gun;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.GunComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.PixelGroupComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Collidable;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

import java.util.ArrayList;

/**
 * Created by Sweet on 9/24/2018.
 */

public class Centipede extends Enemy
{
    private Gun segmentGun;
    private PixelGroup segementTemplate;

    public Centipede(PixelGroup p, PixelGroup p2, Gun gun1, Gun gun2, ParticleSystem ps, DropFactory dF, float xb, float yb, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, gI);

        guns = new GunComponent[1];
        guns[0] = new GunComponent(enemyBody, 0, 0, 0, gun1, Constants.centipedeGunCoors);
        hasGun = true;
        segmentGun = gun2;
        segementTemplate = p2;
    }

    private Centipede(PixelGroup p, PixelGroup p2, Gun gun1, Gun gun2, ParticleSystem ps, DropFactory dF, float xb, float yb, float difficulty, float delay, GlobalInfo gI)
    {
        super(p, ps, dF, xb, yb, delay, gI);

        guns = new GunComponent[1];
        guns[0] = new GunComponent(enemyBody, 0, 0, 0, gun1, Constants.centipedeGunCoors);
        hasGun = true;
        segmentGun = gun2;
        segementTemplate = p2;
        checkOverlap = false;
        hasAttachments = true;

        enemyBody.setLoc(0,0);
        segementTemplate.setLoc(0,0);
        enemyBody.angle = 0;
        enemyBody.rotate(enemyBody.angle);

        baseSpeed = .004f;
        enemyBody.speed = baseSpeed;

        thrusters = new ThrustComponent[4];
        thrusters[0] = new ThrustComponent(enemyBody, 2f, Constants.centipedeHeadThrust[0]);
        thrusters[1] = new ThrustComponent(enemyBody,2f, Constants.centipedeHeadThrust[1]);
        thrusters[2] = new ThrustComponent(enemyBody,2f, Constants.centipedeSegementThrust[0]);
        thrusters[3] = new ThrustComponent(enemyBody,2f, Constants.centipedeSegementThrust[1]);


        attachments = new PixelGroupComponent[20];
        attachments[0] = setUpComponent(enemyBody);
        for(int i = 1; i < attachments.length; i++)
        {
            attachments[i] = setUpComponent(attachments[i - 1].getPixelGroup());
        }
        for(int i = 0; i < attachments.length - 1; i++)
        {
            ArrayList<PixelGroupComponent> pGC = new ArrayList<>(1);
            pGC.add(attachments[i + 1]);
            attachments[i].setAttachments(pGC);
        }

        //generateDrops(guns[0], thrusters[0], difficulty, 1);
    }

    @Override
    public void move(float pX, float pY)
    {
        //float distanceToPlayer = VectorFunctions.getMagnitude(pX - enemyBody.centerX, pY - enemyBody.centerY);
        float angleMoving = -(float) (Math.atan2(pY - enemyBody.centerY, pX - enemyBody.centerX));

        rotate(angleMoving, .005f, globalInfo.timeSlow);
        float distance = baseSpeed * globalInfo.timeSlow;
        float tempDistX = -(float) (distance * Math.cos(enemyBody.angle));
        float tempDistY = (float) (distance * Math.sin(enemyBody.angle));


        x += -tempDistX;
        y += -tempDistY;
        enemyBody.move(-tempDistX, -tempDistY);
        for(ThrustComponent tC: thrusters)
        {
            addThrustParticles(tC.getAttachmentPixels(),1,.03f, enemyBody);
        }

        if(!attachments[0].getSeperated())
        {
            attachments[0].move(
                    enemyBody.getCenterX(),
                    enemyBody.getCenterY(),
                    enemyBody.cosA,
                    enemyBody.sinA,
                    enemyBody.angle,
                    globalInfo
            );
        }
        for(int i = 0; i < attachments.length; i++)
        {
            attachments[i].checkAlive();
            if(attachments[i].getSeperated() && attachments[i].getPixelGroup().getCollidableLive())
            {
                angleMoving = -(float) (Math.atan2(pY - attachments[i].getPixelGroup().centerY, pX - attachments[i].getPixelGroup().centerX));
                attachments[i].rotate(angleMoving, globalInfo.timeSlow);
                distance = baseSpeed * globalInfo.timeSlow;
                tempDistX = -(float) (distance * Math.cos(attachments[i].getPixelGroup().angle));
                tempDistY = (float) (distance * Math.sin(attachments[i].getPixelGroup().angle));
                attachments[i].getPixelGroup().move(-tempDistX, -tempDistY);
                if(i + 1 < attachments.length && !attachments[i + 1].getSeperated())
                {
                    attachments[i+1].move(
                            attachments[i].getPixelGroup().getCenterX(),
                            attachments[i].getPixelGroup().getCenterY(),
                            attachments[i].getPixelGroup().cosA,
                            attachments[i].getPixelGroup().sinA,
                            attachments[i].getPixelGroup().angle,
                            globalInfo
                    );
                }
            }
        }
        /*if (guns[0] != null)
        {
            guns[0].gun.move(globalInfo);
        }
        addThrustParticles(thrusterPixels, ratio, .03f, enemyBody);*/
    }

    public PixelGroupComponent setUpComponent(PixelGroup base)
    {
        PixelGroup seg = segementTemplate.clone();
        seg.setLoc(0,0);

        ThrustComponent[] segThrusters = new ThrustComponent[2];
        segThrusters[0] = new ThrustComponent(seg, 2f, Constants.centipedeSegementThrust[0]);
        segThrusters[1] = new ThrustComponent(seg, 2f, Constants.centipedeSegementThrust[1]);

        GunComponent[] segGuns = new GunComponent[1];
        segGuns[0] = new GunComponent(seg,  0, 0, 0 , segmentGun.clone(), Constants.centipedeGunCoors);

        return new PixelGroupComponent(
                base,
                seg,
                .005f,
                .006f,
                segThrusters,
                null,
                particleSystem,
                Constants.centipedeSegementAttch[1],
                Constants.centipedeSegementAttch[0]
        );
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

    public void addThrustParticles(Pixel[] pixels, float ratio, float dist, Collidable c)
    {
        for(Pixel p: pixels)
        {
            //if(p.live)
            if(p.state >= 1)
            {
                float xDisp = c.infoMap[p.row][p.col].xOriginal * enemyBody.cosA +
                        c.infoMap[p.row][p.col].yOriginal * enemyBody.sinA;
                float yDisp = c.infoMap[p.row][p.col].yOriginal * enemyBody.cosA -
                        c.infoMap[p.row][p.col].xOriginal * enemyBody.sinA;
                for (int t = 0; t < 2; t++)
                {
                    particleSystem.addParticle(
                            xDisp + enemyBody.centerX,
                            yDisp + enemyBody.centerY,
                            //-enemyBody.cosA, enemyBody.sinA,
                            -enemyBody.angle + (float)Math.PI,
                            c.infoMap[p.row][p.col].r,
                            c.infoMap[p.row][p.col].g,
                            c.infoMap[p.row][p.col].b,
                            .7f,
                            (baseSpeed * thrusters[0].getThrustPower() * (float)(Math.random()*70+20)) * ratio,
                            dist * ratio * (float)Math.random()*2,
                            (float)(Math.random()*20)
                    );
                }
            }
        }
    }

    @Override
    public void draw(double interpolation)
    {
        /*if(guns[0] != null)
        {
            guns[0].gun.draw(0);
        }*/

        attachments[0].draw();
        enemyBody.draw();
        //System.out.println(allSegments[0].getPixelGroup().getCenterX() + ",  " + allSegments[0].getPixelGroup().getCenterY());
    }

    @Override
    public void publishLocation(long frame)
    {
        if(spawned)
        {
            if (enemyBody.getEnableLocationChain())
            {
                enemyBody.publishLocation(frame);
            }
            for(int i = 0; i < attachments.length; i++)
            {
                if(attachments[i]!= null)
                {
                    attachments[i].getPixelGroup().publishLocation(frame);
                }
            }
            /*if (getHasGun())
            {
                for (GunComponent gC : getGunComponents())
                {
                    if (gC != null)
                    {
                        gC.gun.publishLocation(frame);
                    }
                }
            }*/
        }
    }

    public void shoot()
    {

    }

    public Centipede clone(float difficulty, float delay)
    {
        return new Centipede(
                enemyBody.clone(),
                segementTemplate.clone(),
                guns[0].gun.clone(),
                segmentGun.clone(),
                particleSystem,
                dropFactory,
                xbound,
                ybound,
                difficulty,
                delay,
                globalInfo
        );
    }
}
