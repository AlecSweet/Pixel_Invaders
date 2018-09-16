package com.example.sweet.Pixel_Invaders.UI_System;

import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform2f;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by Sweet on 6/20/2018.
 */

public class ImageContainer
{
    private int vboHandle[] = new int[1];

    private FloatBuffer buffer;

    private int textureHandle;

    float
            x,
            y,
            halfLength,
            halfWidth,
            xScale = 1,
            yScale = 1;

    public String name;

    private int
            xDispLoc,
            yDispLoc,
            dispLoc,
            aPositionLocation,
            aTextureCoordLocation,
            uTextureLocation;

    public ImageContainer(int texture, float[] vA, float x, float y, String n, int[] glVarLocations, float hL, float hW)
    {
        textureHandle = texture;
        this.x = x;
        this.y = y;
        name = n;
        halfLength = hL;
        halfWidth = hW;
       /* xDispLoc = glVarLocations[0];
        yDispLoc = glVarLocations[1];
        aPositionLocation = glVarLocations[2];
        aTextureCoordLocation = glVarLocations[3];
        uTextureLocation  = glVarLocations[4];*/
        dispLoc = glVarLocations[0];
        aPositionLocation = glVarLocations[1];
        aTextureCoordLocation = glVarLocations[2];
        uTextureLocation  = glVarLocations[3];

        buffer = ByteBuffer
                .allocateDirect(vA.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vA);
        buffer.position(0);

        glGenBuffers(1, vboHandle, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle[0]);
        glBufferData(GL_ARRAY_BUFFER, buffer.capacity() * Constants.BYTES_PER_FLOAT, buffer, GL_STATIC_DRAW);
    }

    private void bindAttributes()
    {
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer (aPositionLocation, 2,
                GL_FLOAT, false, Constants.BYTES_PER_FLOAT * 4,0 );

        glEnableVertexAttribArray(aTextureCoordLocation);
        glVertexAttribPointer (aTextureCoordLocation, 2,
                GL_FLOAT, false, Constants.BYTES_PER_FLOAT * 4,Constants.BYTES_PER_FLOAT * 2);
    }

    public void draw()
    {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glUniform1i(uTextureLocation, 0);

        /*glUniform1f(xDispLoc, x);
        glUniform1f(yDispLoc, y);*/
        glUniform2f(dispLoc, x, y);

        glBindBuffer(GL_ARRAY_BUFFER, vboHandle[0]);
        bindAttributes();
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }

    public void draw(float xShift, float yShift)
    {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glUniform1i(uTextureLocation, 0);

        /*glUniform1f(xDispLoc, x + xShift);
        glUniform1f(yDispLoc, y + yShift);*/
        glUniform2f(dispLoc, x + xShift, y + yShift);

        glBindBuffer(GL_ARRAY_BUFFER, vboHandle[0]);
        bindAttributes();
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }

    void applyScale(float xS, float yS)
    {
        if(xS != xScale || yS != yScale)
        {
            if (halfWidth == -1 || halfLength == -1)
            {
                x = (x / xS) * xScale;
                y = (y / yS) * yScale;
            }
            else
            {
                x = (x / xS) * xScale;
                y = (y / yS) * yScale;
            }
            xScale = xS;
            yScale = yS;
        }
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public void setLoc(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

}
