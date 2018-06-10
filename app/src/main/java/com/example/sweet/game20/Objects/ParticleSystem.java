package com.example.sweet.game20.Objects;

import com.example.sweet.game20.util.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glBindTexture;
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
            aRotationSpeedLocation,
            uTimeLocation,
            uTextureLocation;

    private static final String
            A_COLOR = "a_Color",
            A_POSITION = "a_Position",
            A_DIRECTIONVECTOR = "a_Angle",
            A_STARTTIME = "a_StartTime",
            A_SPEED = "a_Speed",
            A_MAXDISTANCE = "a_MaxDistance",
            A_ROTATIONSPEED = "a_RotationSpeed",
            U_TIME = "u_Time",
            U_TEXTURE = "u_Texture";

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT;

    private final float[] particles;

    private int[]
            particleVBO = new int[1],
            textureID = new int[1];

    private FloatBuffer particleBuf;

    private double globalStartTime;

    private final int maxParticleCount;

    private int
            currentParticleCount,
            nextParticle;

    private int shaderLocation;
    private boolean
            needsUpdate = false;

    public ParticleSystem(int maxParticleCount, int sL, int tID, double gst)
    {
        globalStartTime = gst;
        this.maxParticleCount = maxParticleCount;
        shaderLocation = sL;
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];

        for(int i = 0; i < particles.length; i++)
            particles[i] = 0f;

        particleBuf = ByteBuffer
                .allocateDirect(particles.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(particles);
        particleBuf.position(0);

        aSpeedLocation = glGetAttribLocation(shaderLocation, A_SPEED);
        aStartTimeLocation = glGetAttribLocation(shaderLocation, A_STARTTIME);
        aPositionLocation = glGetAttribLocation(shaderLocation, A_POSITION);
        aColorLocation = glGetAttribLocation(shaderLocation, A_COLOR);
        aDirectionLocation = glGetAttribLocation(shaderLocation, A_DIRECTIONVECTOR);
        aMaxDistanceLocation = glGetAttribLocation(shaderLocation, A_MAXDISTANCE);
        aRotationSpeedLocation = glGetAttribLocation(shaderLocation, A_ROTATIONSPEED);
        uTimeLocation = glGetUniformLocation(shaderLocation, U_TIME);
        uTextureLocation = glGetUniformLocation(shaderLocation, U_TEXTURE);

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
        final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;

        int currentOffset = particleOffset;
        nextParticle++;

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++;
        }

        if (nextParticle == maxParticleCount) {
            // Start over at the beginning, but keep currentParticleCount so
            // that all the other particles still get drawn.
            nextParticle = 0;
        }

        particles[currentOffset++] = x;
        particles[currentOffset++] = y;

        particles[currentOffset++] = r;
        particles[currentOffset++] = g;
        particles[currentOffset++] = b;
        particles[currentOffset++] = a;

        particles[currentOffset++] = angle;

        particles[currentOffset++] = (float)((System.currentTimeMillis() - globalStartTime) / 1000);
        particles[currentOffset++] = speed;
        particles[currentOffset++] = maxDistance;
        particles[currentOffset++] = rotationSpeed;

        needsUpdate = true;
    }

    public void draw()
    {
        if(needsUpdate)
        {
            glBindBuffer(GL_ARRAY_BUFFER, particleVBO[0]);
            particleBuf.position(0);
            particleBuf.put(particles);
            particleBuf.position(0);
            glBufferData(GL_ARRAY_BUFFER, particleBuf.capacity() * Constants.BYTES_PER_FLOAT, particleBuf, GL_DYNAMIC_DRAW);
            needsUpdate = false;
        }

        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureID[0]);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(uTextureLocation, 0);

        glUniform1f(uTimeLocation,(float)((System.currentTimeMillis() - globalStartTime) / 1000));

        bindAttributes();

        glDrawArrays(GL_POINTS, 0, maxParticleCount);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }


    public void bindAttributes()
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

    public ParticleSystem clone()
    {
        return new ParticleSystem(maxParticleCount, shaderLocation, textureID[0], globalStartTime);
    }
}
