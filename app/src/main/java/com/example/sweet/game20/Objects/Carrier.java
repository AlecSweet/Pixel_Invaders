package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.util.DropFactory;
import com.example.sweet.game20.util.VectorFunctions;
import com.example.sweet.game20.util.Constants;
import static android.opengl.GLES20.glUniform1f;

public class Carrier extends Enemy
{
    private int[]
            gunPixelCoordinates = {0, 0},
            thusterPixelCoordinates = {0, 0};

    private Pixel[]
            gun1Pixels = new Pixel[gunPixelCoordinates.length / 2],
            thrusterPixels = new Pixel[thusterPixelCoordinates.length / 2];

    private float trackDistance = 1.2f;
    private float engageDistance = 1f;
    private float retreatDistance = .5f;
    private boolean track = false;
    private boolean retreat = false;

    public Carrier(PixelGroup p, ParticleSystem ps, DropFactory dF)
    {
        super(p, ps, dF);

        for (int i = 0; i < thusterPixelCoordinates.length; i += 2)
            thrusterPixels[i / 2] = enemyBody.getpMap()[thusterPixelCoordinates[i + 1]][thusterPixelCoordinates[i]];

        for (int i = 0; i < gunPixelCoordinates.length; i += 2)
            gun1Pixels[i / 2] = enemyBody.getpMap()[gunPixelCoordinates[i + 1]][gunPixelCoordinates[i]];

        thrusters = new ThrustComponent[1];
        thrusters[0] = new ThrustComponent(thrusterPixels, 0, 0, 0, 0, 2, ps);
        //enemyBody.angle = 3.14;
        enemyBody.rotate(enemyBody.angle);
        baseSpeed = .0002f;
        hasGun = false;
        enemyBody.setEdgeColor(.5f, 0f, .5f);
    }

    //public void move(float pX, float pY, long curFrame, float slow)
    public void move(float pX, float pY, GlobalInfo gI)
    {
        float distanceToPlayer = VectorFunctions.getMagnitude(pX - enemyBody.centerX, pY - enemyBody.centerY);
        float angleMoving = 0f;
        float ratio = 1;
        if(distanceToPlayer > trackDistance)
        {
            track = true;
            retreat = false;
        }
        else if(distanceToPlayer < retreatDistance)
        {
            retreat = true;
            track = false;
        }

        if(track)
            angleMoving = -(float)(Math.atan2(pY - enemyBody.centerY, pX - enemyBody.centerX));

        if(retreat)
            angleMoving = -(float)(Math.atan2(enemyBody.centerY - pY, enemyBody.centerX - pX));


        rotate(angleMoving, .002f, gI.timeSlow);
        float distance = baseSpeed * thrusters[0].thrustPower * gI.timeSlow;
        float tempDistX = -(float)(distance * Math.cos(enemyBody.angle));
        float tempDistY = (float)(distance * Math.sin(enemyBody.angle));

        if(distanceToPlayer < engageDistance && distanceToPlayer > retreatDistance && track)
        {
            tempDistX *= .6;
            tempDistY *= .6;
            ratio = 0f;
            /*if(guns[0] != null)
            {
                boolean t = guns[0].gun.shoot(guns[0].x + enemyBody.centerX,
                        guns[0].y + enemyBody.centerY,
                        (float) enemyBody.angle + (float) Math.PI,
                        curFrame,
                        slow);
            }*/
        }

        x += -tempDistX;
        y += -tempDistY;
        enemyBody.move(-tempDistX, -tempDistY);
        /*if(guns[0] != null)
        {
            guns[0].gun.move(slow);
        }*/
        //addThrustParticles(thrusterPixels, ratio, .03f);

        /*if(!dropAdded && enemyBody.numLivePixels <= .5 * enemyBody.totalPixels)
        {
            dropsToAdd.add(
                    dropFactory.getNewDrop(
                            Constants.DropType.GUN,
                            x,
                            y,
                            guns[0]
                    )
            );
            dropsToAdd.add(
                    dropFactory.getNewDrop(
                            Constants.DropType.MOD,
                            x,
                            y,
                            new ModComponent(null,x,y,0, Constants.ModType.EXTRASHOTS, 3, null)
                    )
            );
            //dropsToAdd.add(dropFactory.getNewDrop(Constants.DropType.EXTRA_GUN, x, y));
            guns[0] = null;
            dropAdded = true;
        }*/

        // public Drop(PixelGroup p, float x, float y, int t, double lT, Component c)
    }

    public void rotate(float angleMoving, float rotateSpeed, float slow)
    {
        float delta = (float)enemyBody.angle - angleMoving;
        if (delta > rotateSpeed || delta < -rotateSpeed)
        {
            if (delta < -Math.PI || (delta > 0 && delta < Math.PI))
                enemyBody.angle -= rotateSpeed * slow;
            else
                enemyBody.angle += rotateSpeed * slow;
        }
        else
            enemyBody.angle =  angleMoving;

        enemyBody.rotate(enemyBody.angle);

        if (enemyBody.angle > Math.PI)
            enemyBody.angle -= Constants.twoPI;
        else if (enemyBody.angle < -Math.PI)
            enemyBody.angle += Constants.twoPI;
    }

    @Override
    public void draw(double interpolation)
    {
        enemyBody.draw();
    }

    public void shoot()
    {

    }

    @Override
    public Carrier clone()
    {
        return new Carrier(enemyBody.clone(), particleSystem, dropFactory);
    }
}
