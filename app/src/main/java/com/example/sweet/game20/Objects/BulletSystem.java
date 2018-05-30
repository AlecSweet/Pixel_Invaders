package com.example.sweet.game20.Objects;

import android.content.Context;

import com.example.sweet.game20.R;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.TextureLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import static android.opengl.GLES20.GL_STREAM_DRAW;
import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glVertexAttribPointer;


/**
 * Created by Sweet on 3/26/2018.
 */

public class BulletSystem
{
    private  ArrayList<Bullet> bullets;

    private static final int
            POSITION_COMPONENT_COUNT = 2,
            ANGLE_COMPONENT_COUNT = 1,
            ALPHA_COMPONENT_COUNT = 1;

    private static final int
            TOTAL_COMPONENT_COUNT =
                POSITION_COMPONENT_COUNT +
                ANGLE_COMPONENT_COUNT +
                ALPHA_COMPONENT_COUNT;

    private int
            aPositionLocation,
            aAngleLocation,
            aAlphaLocation,
            uTextureLocation;

    private static final String
            A_POSITION = "a_Position",
            A_ANGLE = "a_Angle",
            A_ALPHA = "a_Alpha",
            U_TEXTURE = "u_Texture";

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT;

    private int[]
            bulletsVBO = new int[2],
            bulletsVAO = new int[1],
            textureID = new int[1];

    private FloatBuffer bulletDataBuf;

    private final int
            maxBulletCount,
            bulletPixelSize,
            bulletFloats;

    private double globalStartTime;

    private Context context;

    private boolean
            setLocations = false,
            buffersInitiated = false,
            threadLock = false,
            bufferToggle = false;

    public BulletSystem(Context context, int maxCount, int bulletSize)
    {
        this.context = context;
        maxBulletCount = maxCount;
        bullets = new ArrayList<>();
        bulletPixelSize = bulletSize;
        bulletFloats = bulletPixelSize * TOTAL_COMPONENT_COUNT;

        bulletDataBuf = ByteBuffer
                .allocateDirect(maxBulletCount * bulletFloats * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        bulletDataBuf.position(0);
    }

    /*public void addBullet(Bullet b)
    {
        threadLock = true;
        bullets.add(b);
        threadLock = false;
    }

    public void removeDeadBullets()
    {
        threadLock = true;
        Iterator<Bullet> itr = bullets.iterator();
        while (itr.hasNext())
        {
            if(!itr.next().live)
                itr.remove();
        }
        threadLock = false;
    }

    public void move()
    {
        if(bullets.size() > 0)
            for(Bullet b: bullets)
            {
                b.move();
            }

        removeDeadBullets();
    }

    public void updateBuffers()
    {
        bulletDataBuf.position(0);

        for(int i = 0; i < bullets.size(); i++)
        {
            if(i < maxBulletCount - 1)
            {
                Bullet temp = bullets.get(i);
                int tempOffsetBullet = i * bulletFloats;
                for (int t = 0; t < temp.pixels.size(); t++)
                {
                    int tempOffsetPixel = t * TOTAL_COMPONENT_COUNT + tempOffsetBullet;
                    bulletDataBuf.put(tempOffsetPixel, temp.pixels.get(t).xDisp + temp.centerX);
                    bulletDataBuf.put(tempOffsetPixel + 1, temp.pixels.get(t).yDisp + temp.centerY);
                    bulletDataBuf.put(tempOffsetPixel + 2, temp.angle);
                    if(temp.pixels.get(t).live)
                        bulletDataBuf.put(tempOffsetPixel + 3, 1f);
                    else
                        bulletDataBuf.put(tempOffsetPixel + 3, 0f);
                }
            }
            else
                break;
        }

        bulletDataBuf.position(0);

        //if(!threadLock)
       // {
            if(bufferToggle)
            {
                glBindBuffer(GL_ARRAY_BUFFER, bulletsVBO[0]);
                glBufferData(GL_ARRAY_BUFFER, bulletDataBuf.capacity() * Constants.BYTES_PER_FLOAT, bulletDataBuf, GL_STREAM_DRAW);
                bufferToggle = false;
            }
            else
            {
                glBindBuffer(GL_ARRAY_BUFFER, bulletsVBO[1]);
                glBufferData(GL_ARRAY_BUFFER, bulletDataBuf.capacity() * Constants.BYTES_PER_FLOAT, bulletDataBuf, GL_STREAM_DRAW);
                bufferToggle = true;
            }
        //}
    }

    public void draw(int shaderLocation, double interpolation)
    {
        if(!setLocations)
        {
            aPositionLocation = glGetAttribLocation(shaderLocation, A_POSITION);
            aAngleLocation = glGetAttribLocation(shaderLocation, A_ANGLE);
            aAlphaLocation = glGetAttribLocation(shaderLocation, A_ALPHA);
            uTextureLocation = glGetUniformLocation(shaderLocation, U_TEXTURE);
        }

        //Initiate VBOs on first draw method call. Must be done in draw for valid context.
        if(!buffersInitiated)
        {
            initVBOs();
            buffersInitiated = true;
        }

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID[0]);
        glUniform1i(uTextureLocation, 0);

        bindAttributes();

        if(bullets.size() * bulletPixelSize < maxBulletCount * bulletPixelSize)
            glDrawArrays(GL_POINTS, 0, bullets.size()*bulletPixelSize);
        else
            glDrawArrays(GL_POINTS, 0, maxBulletCount * bulletPixelSize);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        if(!threadLock)
        {
            updateBuffers();
        }
    }

    public void initVBOs()
    {
        textureID[0] = TextureLoader.loadTexture(context,  R.drawable.bullets);

        glGenBuffers(2, bulletsVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, bulletsVBO[1]);
        glBufferData(GL_ARRAY_BUFFER, bulletDataBuf.capacity() * Constants.BYTES_PER_FLOAT, bulletDataBuf, GL_STREAM_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, bulletsVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, bulletDataBuf.capacity() * Constants.BYTES_PER_FLOAT, bulletDataBuf, GL_STREAM_DRAW);
    }

    public void bindAttributes()
    {
        if(bufferToggle)
            glBindBuffer(GL_ARRAY_BUFFER, bulletsVBO[1]);
        else
            glBindBuffer(GL_ARRAY_BUFFER, bulletsVBO[0]);

        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer (aPositionLocation, POSITION_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,0 );


        glEnableVertexAttribArray(aAngleLocation);
        glVertexAttribPointer (aAngleLocation, ANGLE_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,(POSITION_COMPONENT_COUNT ) *
                        Constants.BYTES_PER_FLOAT);

        glEnableVertexAttribArray(aAlphaLocation);
        glVertexAttribPointer (aAlphaLocation, ALPHA_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,(POSITION_COMPONENT_COUNT +
                        ALPHA_COMPONENT_COUNT) *
                        Constants.BYTES_PER_FLOAT);
    }

    public ArrayList<Bullet> getBullets()
    {
        return bullets;
    }

    /*public void initVAOs()
    {
        textureID[0] = TextureLoader.loadTexture(context,  R.drawable.bullets);

        glGenBuffers(2, bulletsVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, bulletsVBO[1]);
        glBufferData(GL_ARRAY_BUFFER, bulletDataBuf.capacity() * Constants.BYTES_PER_FLOAT, bulletDataBuf, GL_STREAM_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, bulletsVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, bulletDataBuf.capacity() * Constants.BYTES_PER_FLOAT, bulletDataBuf, GL_STREAM_DRAW);


        glGenVertexArrays(1, bulletsVAO, 0);
        glBindVertexArray ( bulletsVAO[0] );

        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer (aPositionLocation, POSITION_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,0 );


        glEnableVertexAttribArray(aAngleLocation);
        glVertexAttribPointer (aAngleLocation, ANGLE_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,(POSITION_COMPONENT_COUNT ) *
                        Constants.BYTES_PER_FLOAT);

        glEnableVertexAttribArray(aAlphaLocation);
        glVertexAttribPointer (aAlphaLocation, ALPHA_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,(POSITION_COMPONENT_COUNT +
                        ALPHA_COMPONENT_COUNT) *
                        Constants.BYTES_PER_FLOAT);

        glBindVertexArray ( 0 );
    }

    public void draw(int shaderLocation, double interpolation)
    {
        if(!setLocations)
        {
            aPositionLocation = glGetAttribLocation(shaderLocation, A_POSITION);
            aAngleLocation = glGetAttribLocation(shaderLocation, A_ANGLE);
            aAlphaLocation = glGetAttribLocation(shaderLocation, A_ALPHA);
            uTextureLocation = glGetUniformLocation(shaderLocation, U_TEXTURE);
        }

        //Initiate VBOs on first draw method call. Must be done in draw for valid context.
        if(!buffersInitiated)
        {
            initVAOs();
            buffersInitiated = true;
        }

        if(bufferToggle)
            glBindBuffer(GL_ARRAY_BUFFER, bulletsVBO[1]);
        else
            glBindBuffer(GL_ARRAY_BUFFER, bulletsVBO[0]);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID[0]);
        glUniform1i(uTextureLocation, 0);

        glBindVertexArray(bulletsVAO[0]);
        if(bullets.size() * bulletPixelSize < maxBulletCount * bulletPixelSize)
            glDrawArrays(GL_POINTS, 0, bullets.size()*bulletPixelSize);
        else
            glDrawArrays(GL_POINTS, 0, maxBulletCount * bulletPixelSize);
        glBindVertexArray(0);

        if(!threadLock)
        {
            updateBuffers();
        }
    }*/

}
