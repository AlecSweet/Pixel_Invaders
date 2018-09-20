package com.example.sweet.Pixel_Invaders.UI_System;

import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetAttribLocation;

import static android.opengl.GLES20.glVertexAttribPointer;
/**
 * Created by Sweet on 3/18/2018.
 */

public class ParticleSystem
{
    private static final int
            POSITION_COMPONENT_COUNT = 2,
            COLOR_COMPONENT_COUNT = 4,
            DIRECTION_COMPONENT_COUNT = 1,
            START_TIME_COMPONENT_COUNT = 1,
            SPEED_COMPONENT_COUNT = 1,
            MAX_DISTANCE_COMPONENT_COUNT = 1,
            ROTATION_SPEED_COMPONENT_COUNT = 1;

    private static final int
            TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT +
            COLOR_COMPONENT_COUNT +
            DIRECTION_COMPONENT_COUNT +
            START_TIME_COMPONENT_COUNT +
            SPEED_COMPONENT_COUNT +
            MAX_DISTANCE_COMPONENT_COUNT +
            ROTATION_SPEED_COMPONENT_COUNT;

    private int
            aSpeedLocation,
            aStartTimeLocation,
            aPositionLocation,
            aColorLocation,
            aDirectionLocation,
            aMaxDistanceLocation,
            aRotationSpeedLocation;

    private static final String
            A_COLOR = "a_Color",
            A_POSITION = "a_Position",
            A_DIRECTIONVECTOR = "a_Angle",
            A_STARTTIME = "a_StartTime",
            A_SPEED = "a_Speed",
            A_MAXDISTANCE = "a_MaxDistance",
            A_ROTATIONSPEED = "a_RotationSpeed";

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT;

    private final float[] particles;

    private int[]
            particleVBO = new int[1],
            textureID = new int[1];

    private FloatBuffer particleBuf;

    private double globalStartTime;

    private GlobalInfo globalInfo;

    private final int maxParticleCount;
    private volatile int curMaxParticleCount;

    private int
            currentParticleCount,
            nextParticle,
            lastUpdatePos = 0;

    private int shaderLocation;

    private boolean needsUpdate = false;

    public ParticleSystem(int maxParticleCount, int sL, int tID, double gst, GlobalInfo gI)
    {
        globalStartTime = gst;
        this.maxParticleCount = maxParticleCount;
        curMaxParticleCount = maxParticleCount;
        shaderLocation = sL;
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        globalInfo = gI;

        for(int i = 0; i < particles.length; i++)
        {
            particles[i] = 0f;
        }

        particleBuf = ByteBuffer
                .allocateDirect(particles.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(particles);
        particleBuf.position(0);
       /* particleBuf = FloatBuffer.allocate(particles.length * Constants.BYTES_PER_FLOAT);
        particleBuf.position(0);*/

        aSpeedLocation = glGetAttribLocation(shaderLocation, A_SPEED);
        aStartTimeLocation = glGetAttribLocation(shaderLocation, A_STARTTIME);
        aPositionLocation = glGetAttribLocation(shaderLocation, A_POSITION);
        aColorLocation = glGetAttribLocation(shaderLocation, A_COLOR);
        aDirectionLocation = glGetAttribLocation(shaderLocation, A_DIRECTIONVECTOR);
        aMaxDistanceLocation = glGetAttribLocation(shaderLocation, A_MAXDISTANCE);
        aRotationSpeedLocation = glGetAttribLocation(shaderLocation, A_ROTATIONSPEED);

        textureID[0] = tID;
        glGenBuffers(1, particleVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, particleVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, particleBuf.capacity() * Constants.BYTES_PER_FLOAT, particleBuf, GL_DYNAMIC_DRAW);
    }

    public void addParticle(float x, float y,
                            float angle,
                            float r, float g, float b, float a,
                            float speed, float maxDistance, float rotationSpeed)
    {
        int currentOffset = nextParticle * TOTAL_COMPONENT_COUNT;

        /*if (currentParticleCount < curMaxParticleCount)
        {
            currentParticleCount++;
        }*/

        if (nextParticle + 1 >= curMaxParticleCount)
        {
            nextParticle = 0;
        }
        else
        {
            nextParticle++;
        }

        particles[currentOffset++] = x;
        particles[currentOffset++] = y;

        particles[currentOffset++] = r;
        particles[currentOffset++] = g;
        particles[currentOffset++] = b;
        particles[currentOffset++] = a;

        particles[currentOffset++] = angle;

        particles[currentOffset++] = globalInfo.getAugmentedTimeSeconds();
        particles[currentOffset++] = speed;
        particles[currentOffset++] = maxDistance;
        particles[currentOffset++] = rotationSpeed;

        needsUpdate = true;
    }

    void addParticle(float x, float y,
                            float angle,
                            float r, float g, float b, float a,
                            float speed, float maxDistance, float rotationSpeed, boolean realtime)
    {
        int currentOffset = nextParticle * TOTAL_COMPONENT_COUNT;

        /*if (currentParticleCount < curMaxParticleCount)
        {
            currentParticleCount++;
        }*/

        if (nextParticle + 1 >= curMaxParticleCount)
        {
            nextParticle = 0;
        }
        else
        {
            nextParticle++;
        }

        particles[currentOffset++] = x;
        particles[currentOffset++] = y;

        particles[currentOffset++] = r;
        particles[currentOffset++] = g;
        particles[currentOffset++] = b;
        particles[currentOffset++] = a;

        particles[currentOffset++] = angle;

        particles[currentOffset++] = (float)(System.currentTimeMillis() - globalStartTime)/1000;
        particles[currentOffset++] = speed;
        particles[currentOffset++] = maxDistance;
        particles[currentOffset++] = rotationSpeed;

        needsUpdate = true;
    }

    public void draw()
    {
        /*if(needsUpdate)
        {
            //long start = System.nanoTime();
            particleBuf.position(0);
            particleBuf.put(particles);
            particleBuf.position(0);
            //long dif = (System.nanoTime() - start) / 1000;
           // start = System.nanoTime();
            glBindBuffer(GL_ARRAY_BUFFER, particleVBO[0]);
            glBufferSubData(GL_ARRAY_BUFFER, 0, particleBuf.capacity() * Constants.BYTES_PER_FLOAT, particleBuf);
            //System.out.println(dif + "  :  " + (System.nanoTime() - start) / 1000);
            needsUpdate = false;
        }*/
        if (needsUpdate)
        {
            for (int i = lastUpdatePos; i != nextParticle;)
            {
                int c = 0;
                if (i >= curMaxParticleCount)
                {
                    i = 0;
                }
                else
                {
                    c = i * TOTAL_COMPONENT_COUNT;
                    i++;
                }
                particleBuf.put(c, particles[c++]);
                particleBuf.put(c, particles[c++]);
                particleBuf.put(c, particles[c++]);
                particleBuf.put(c, particles[c++]);
                particleBuf.put(c, particles[c++]);
                particleBuf.put(c, particles[c++]);
                particleBuf.put(c, particles[c++]);
                particleBuf.put(c, particles[c++]);
                particleBuf.put(c, particles[c++]);
                particleBuf.put(c, particles[c++]);
                particleBuf.put(c, particles[c]);
            }
            glBindBuffer(GL_ARRAY_BUFFER, particleVBO[0]);
            if (nextParticle - lastUpdatePos < 0)
            {
                particleBuf.position(lastUpdatePos * TOTAL_COMPONENT_COUNT);
                glBufferSubData(
                        GL_ARRAY_BUFFER,
                        (lastUpdatePos) * TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT,
                        (curMaxParticleCount - lastUpdatePos) * TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT,
                        particleBuf);
                particleBuf.position(0);
                glBufferSubData(
                        GL_ARRAY_BUFFER,
                        0,
                        (nextParticle) * TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT,
                        particleBuf);
            }
            else
            {
                particleBuf.position(lastUpdatePos * TOTAL_COMPONENT_COUNT);
                glBufferSubData(GL_ARRAY_BUFFER,
                        (lastUpdatePos) * TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT,
                        (nextParticle - lastUpdatePos) * TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT,
                        particleBuf
                );
            }
            lastUpdatePos = nextParticle;
            needsUpdate = false;
        }

        bindAttributes();

        glDrawArrays(GL_POINTS, 0, curMaxParticleCount);
    }


    private void bindAttributes()
    {
        glBindBuffer(GL_ARRAY_BUFFER, particleVBO[0]);

        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer (aPositionLocation, POSITION_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,0 );

        glEnableVertexAttribArray(aColorLocation);
        glVertexAttribPointer (aColorLocation, COLOR_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,POSITION_COMPONENT_COUNT *
                        Constants.BYTES_PER_FLOAT);

        glEnableVertexAttribArray(aDirectionLocation);
        glVertexAttribPointer (aDirectionLocation, DIRECTION_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,(POSITION_COMPONENT_COUNT +
                        COLOR_COMPONENT_COUNT) *
                        Constants.BYTES_PER_FLOAT);

        glEnableVertexAttribArray(aStartTimeLocation);
        glVertexAttribPointer (aStartTimeLocation, START_TIME_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,(POSITION_COMPONENT_COUNT +
                        COLOR_COMPONENT_COUNT +
                        DIRECTION_COMPONENT_COUNT) *
                        Constants.BYTES_PER_FLOAT);

        glEnableVertexAttribArray(aSpeedLocation);
        glVertexAttribPointer (aSpeedLocation, SPEED_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,(POSITION_COMPONENT_COUNT +
                        COLOR_COMPONENT_COUNT +
                        DIRECTION_COMPONENT_COUNT +
                        START_TIME_COMPONENT_COUNT) *
                        Constants.BYTES_PER_FLOAT);

        glEnableVertexAttribArray(aMaxDistanceLocation);
        glVertexAttribPointer (aMaxDistanceLocation, MAX_DISTANCE_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,(POSITION_COMPONENT_COUNT +
                        COLOR_COMPONENT_COUNT +
                        DIRECTION_COMPONENT_COUNT +
                        START_TIME_COMPONENT_COUNT +
                        SPEED_COMPONENT_COUNT) *
                        Constants.BYTES_PER_FLOAT);

        glEnableVertexAttribArray(aRotationSpeedLocation);
        glVertexAttribPointer (aRotationSpeedLocation, ROTATION_SPEED_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE,(POSITION_COMPONENT_COUNT +
                        COLOR_COMPONENT_COUNT +
                        DIRECTION_COMPONENT_COUNT +
                        START_TIME_COMPONENT_COUNT +
                        SPEED_COMPONENT_COUNT +
                        MAX_DISTANCE_COMPONENT_COUNT) *
                        Constants.BYTES_PER_FLOAT);
    }

    public void setCurMax(float percent)
    {
        curMaxParticleCount = (int)(maxParticleCount * percent);
    }

    public ParticleSystem clone()
    {
        return new ParticleSystem(maxParticleCount, shaderLocation, textureID[0], globalStartTime, globalInfo);
    }

    public void clear()
    {
        nextParticle = 0;
        lastUpdatePos = 0;
        Arrays.fill(particles, 0f);
        particleBuf.position(0);
        particleBuf.put(particles);
        particleBuf.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, particleVBO[0]);
        glBufferSubData(GL_ARRAY_BUFFER, 0, particleBuf.capacity() * Constants.BYTES_PER_FLOAT, particleBuf);
    }

    public void clear(int start, int end)
    {
        start *= TOTAL_COMPONENT_COUNT;
        end *= TOTAL_COMPONENT_COUNT;
        for(int i = start; i < end; i++)
        {
            particles[i] = 0;
        }
        particleBuf.position(0);
        particleBuf.put(particles);
        particleBuf.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, particleVBO[0]);
        glBufferSubData(GL_ARRAY_BUFFER, 0, particleBuf.capacity() * Constants.BYTES_PER_FLOAT, particleBuf);
    }

    public int getCurParticle()
    {
        return nextParticle;
    }
}
