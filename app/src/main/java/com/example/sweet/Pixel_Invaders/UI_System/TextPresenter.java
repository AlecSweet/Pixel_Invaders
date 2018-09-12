package com.example.sweet.Pixel_Invaders.UI_System;

import android.content.Context;

import com.example.sweet.Pixel_Invaders.Util.Resource_Readers.TextureLoader;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.R;

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
 * Created by Sweet on 6/26/2018.
 */

public class TextPresenter
{
    private int
            xDispLoc,
            yDispLoc,
            dispLoc,
            aPositionLocation,
            aTextureCoordLocation,
            uTextureLocation;

    private int[] digitTextures = new int[10];

    private int dot;

    private int vboHandle[] = new int[1];

    private FloatBuffer buffer;

    TextPresenter(Context context, int[] glVarLocations)
    {
       /* xDispLoc = glVarLocations[0];
        yDispLoc = glVarLocations[1];
        aPositionLocation = glVarLocations[2];
        aTextureCoordLocation = glVarLocations[3];
        uTextureLocation  = glVarLocations[4];*/
        dispLoc = glVarLocations[0];
        aPositionLocation = glVarLocations[1];
        aTextureCoordLocation = glVarLocations[2];
        uTextureLocation  = glVarLocations[3];

        digitTextures[0] = TextureLoader.loadTexture(context, R.drawable.t0);
        digitTextures[1] = TextureLoader.loadTexture(context, R.drawable.t1);
        digitTextures[2] = TextureLoader.loadTexture(context, R.drawable.t2);
        digitTextures[3] = TextureLoader.loadTexture(context, R.drawable.t3);
        digitTextures[4] = TextureLoader.loadTexture(context, R.drawable.t4);
        digitTextures[5] = TextureLoader.loadTexture(context, R.drawable.t5);
        digitTextures[6] = TextureLoader.loadTexture(context, R.drawable.t6);
        digitTextures[7] = TextureLoader.loadTexture(context, R.drawable.t7);
        digitTextures[8] = TextureLoader.loadTexture(context, R.drawable.t8);
        digitTextures[9] = TextureLoader.loadTexture(context, R.drawable.t9);

        dot = TextureLoader.loadTexture(context, R.drawable.tdot);

        buffer = ByteBuffer
                .allocateDirect(Constants.digitVA.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(Constants.digitVA);
        buffer.position(0);

        glGenBuffers(1, vboHandle, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle[0]);
        glBufferData(GL_ARRAY_BUFFER, buffer.capacity() * Constants.BYTES_PER_FLOAT, buffer, GL_STATIC_DRAW);
    }

    void drawInt(int i, float x, float y, boolean center)
    {
        //glUniform1f(xDispLoc, x);
        float disp = 0;
        if(center)
        {
            disp = (Constants.dSkipY * getNumDigitsInt(i)) / 2;
        }
        int itr = 0;
        if(i > 0)
        {
            while (i > 0)
            {
                int digit = i % 10;
                i /= 10;

                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, digitTextures[digit]);
                glUniform1i(uTextureLocation, 0);

                //glUniform1f(yDispLoc, y + itr * Constants.dSkipY - disp);
                glUniform2f(dispLoc, x, y + itr * Constants.dSkipY - disp);

                glBindBuffer(GL_ARRAY_BUFFER, vboHandle[0]);
                bindAttributes();
                glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

                itr++;
                if (i <= 0)
                {
                    break;
                }
            }
        }
        else
        {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, digitTextures[0]);
            glUniform1i(uTextureLocation, 0);

            //glUniform1f(yDispLoc, y + itr * Constants.dSkipY - disp);
            glUniform2f(dispLoc, x, y + itr * Constants.dSkipY - disp);

            glBindBuffer(GL_ARRAY_BUFFER, vboHandle[0]);
            bindAttributes();
            glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
        }
    }

    private void bindAttributes()
    {
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer(aPositionLocation, 2,
                GL_FLOAT, false, Constants.BYTES_PER_FLOAT * 4,0 );

        glEnableVertexAttribArray(aTextureCoordLocation);
        glVertexAttribPointer(aTextureCoordLocation, 2,
                GL_FLOAT, false, Constants.BYTES_PER_FLOAT * 4,Constants.BYTES_PER_FLOAT * 2);
    }

    private int getNumDigitsInt(int number)
    {
        if (number < 100000)
        {
            if (number < 100)
            {
                if (number < 10)
                {
                    return 1;
                }
                else
                {
                    return 2;
                }
            }
            else
            {
                if (number < 1000)
                {
                    return 3;
                }
                else
                {
                    if (number < 10000)
                    {
                        return 4;
                    }
                    else
                    {
                        return 5;
                    }
                }
            }
        }
        else
        {
            if (number < 10000000)
            {
                if (number < 1000000)
                {
                    return 6;
                }
                else
                {
                    return 7;
                }
            }
            else
            {
                if (number < 100000000)
                {
                    return 8;
                }
                else
                {
                    if (number < 1000000000)
                    {
                        return 9;
                    }
                    else
                    {
                        return 10;
                    }
                }
            }
        }
    }
}
