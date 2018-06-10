package com.example.sweet.game20.Objects;

import com.example.sweet.game20.util.Constants;

import static android.opengl.GLES20.glUniform1f;

/**
 * Created by Sweet on 6/6/2018.
 */

public class Asteroid extends Enemy
{
    public float
            rotationSpeed,
            distX,
            distY;

    public Asteroid(PixelGroup p, ParticleSystem ps)
    {
        super(p, ps);
        guns = null;
        thrusters = null;
        enemyBody.angle = 3.14;
        enemyBody.rotate(enemyBody.angle);
        baseSpeed = .005f;
        p.speed = baseSpeed;
        hasGun = false;
        generatePath();
    }

    public void generatePath()
    {
        float travelAngle = 0;
        switch((int)(Math.random()*3.99))
        {
            case 0: enemyBody.setLoc((float)(Math.random()*9)-4.5f, 4.5f);
                    switch((int)(Math.random()*2.99))
                    {
                        case 0: travelAngle = (float)Math.atan2(enemyBody.centerY - (-4.5), enemyBody.centerX - (Math.random() * 9 - 4.5f));
                                break;
                        case 1: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * 9 - 4.5f), enemyBody.centerX - 4.5f);
                                break;
                        case 2: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * 9 - 4.5f), enemyBody.centerX - (-4.5));
                                break;
                    }
                    break;
            case 1: enemyBody.setLoc((float)(Math.random()*9)-4.5f, -4.5f);
                    switch((int)(Math.random()*2.99))
                    {
                        case 0: travelAngle = (float)Math.atan2(enemyBody.centerY - 4.5, enemyBody.centerX - (Math.random() * 9 - 4.5f));
                                break;
                        case 1: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * 9 - 4.5f), enemyBody.centerX - 4.5f);
                                break;
                        case 2: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * 9 - 4.5f), enemyBody.centerX - (-4.5));
                                break;
                    }
                    break;
            case 2: enemyBody.setLoc(4.5f, (float)(Math.random()*9)-4.5f);
                    switch((int)(Math.random()*2.99))
                    {
                        case 0: travelAngle = (float)Math.atan2(enemyBody.centerY - (-4.5), enemyBody.centerX - (Math.random() * 9 - 4.5f));
                                break;
                        case 1: travelAngle = (float)Math.atan2(enemyBody.centerY - 4.5f, enemyBody.centerX - (Math.random() * 9 - 4.5f));
                                break;
                        case 2: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * 9 - 4.5f), enemyBody.centerX - (-4.5));
                                break;
                    }
                    break;
            case 3: enemyBody.setLoc(-4.5f, (float)(Math.random()*9)-4.5f);
                    switch((int)(Math.random()*2.99))
                    {
                        case 0: travelAngle = (float)Math.atan2(enemyBody.centerY - (-4.5), enemyBody.centerX - (Math.random() * 9 - 4.5f));
                                break;
                        case 1: travelAngle = (float)Math.atan2(enemyBody.centerY - 4.5f, enemyBody.centerX - (Math.random() * 9 - 4.5f));
                                break;
                        case 2: travelAngle = (float)Math.atan2(enemyBody.centerY - (Math.random() * 9 - 4.5f), enemyBody.centerX - 4.5);
                                break;
                    }
                    break;
        }
        rotationSpeed = (float)(Math.random()*.02);
        distX = -(float)(baseSpeed * Math.cos(travelAngle));
        distY = (float)(baseSpeed * Math.sin(travelAngle));
        //enemyBody.setLoc(0f,0f);
        enemyBody.move(.01f,.01f);
    }

    @Override
    public void move(float unused, float unused1)
    {
        rotate();
        x += distX;
        y += distY;
        enemyBody.move(-distX, -distY);

        //if( x > 4.5 || x < -4.5 || y > 4.5 || y < -4.5)
         //   enemyBody.collidableLive = false;
    }

    public void rotate()
    {
        enemyBody.angle += rotationSpeed;
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

    @Override
    public Asteroid clone()
    {
        return new Asteroid(enemyBody.clone(), particleSystem);
    }
}
