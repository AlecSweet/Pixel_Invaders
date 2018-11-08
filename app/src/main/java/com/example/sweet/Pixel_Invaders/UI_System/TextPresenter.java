package com.example.sweet.Pixel_Invaders.UI_System;

import android.content.Context;

import com.example.sweet.Pixel_Invaders.Util.Resource_Readers.TextureLoader;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

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
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform2f;
import static android.opengl.GLES20.glVertexAttribPointer;


/**
 * Created by Sweet on 6/26/2018.
 */

public class TextPresenter
{
    private int
            dispLoc,
            aPositionLocation,
            aTextureCoordLocation,
            uTextureLocation;

    private int[] digitTextures = new int[10];

    private HashMap<Integer, Integer> characters = new HashMap<>();
    private HashMap<Integer, Float> characterWidth = new HashMap<>();

    private int vboHandle[] = new int[3];

    private static final float dSkipY = .048f;

    private static final float pixSize = .01f;
    private static final float charSkipY = 2 * pixSize;
    private static final float charSpaceY = 3 * pixSize;
    public static final float charSkipX = (12 + 6) * pixSize;
    
    private static final float dL = 2 * .008f;
    private static final float dW = 5 * .008f;

    private static final float[] digitVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -dL, -dW,   0f, 1f,
            dL, -dW,   1f, 1f,
            dL,  dW,   1f, 0f,
            -dL,  dW,   0f, 0f,
            -dL, -dW,   0f, 1f
    };

    private static final float cW = 8 * pixSize;
    
    private static final float[] charVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -cW, -cW,   0f, 1f,
            cW, -cW,   1f, 1f,
            cW,  cW,   1f, 0f,
            -cW,  cW,   0f, 0f,
            -cW, -cW,   0f, 1f
    };

    TextPresenter(Context context, int[] glVarLocations)
    {
        dispLoc = glVarLocations[0];
        aPositionLocation = glVarLocations[1];
        aTextureCoordLocation = glVarLocations[2];
        uTextureLocation  = glVarLocations[3];

        initTextures(context);

        FloatBuffer digitBuf = ByteBuffer
                .allocateDirect(digitVA.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(digitVA);
        digitBuf.position(0);

        FloatBuffer charBuf = ByteBuffer
                .allocateDirect(charVA.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(charVA);
        charBuf.position(0);

        glGenBuffers(3, vboHandle, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle[0]);
        glBufferData(GL_ARRAY_BUFFER, digitBuf.capacity() * Constants.BYTES_PER_FLOAT, digitBuf, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle[1]);
        glBufferData(GL_ARRAY_BUFFER, charBuf.capacity() * Constants.BYTES_PER_FLOAT, charBuf, GL_STATIC_DRAW);
    }

    void drawInt(int i, float x, float y, boolean center)
    {
        float disp = 0;
        if(center)
        {
            disp = dSkipY * getNumDigitsInt(i) / 2f;
            disp -= dL*1.5;
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

                glUniform2f(dispLoc, x - itr * dSkipY + disp, y);

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

            glUniform2f(dispLoc, x - itr * dSkipY - disp, y);

            glBindBuffer(GL_ARRAY_BUFFER, vboHandle[0]);
            bindAttributes();
            glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
        }
    }

    public float drawString(String s, float x, float y, int start, int len, float mag, boolean center)
    {
        float disp = 0;
        if(center)
        {
            for(int i = start; i < len; i++)
            {
                if(characters.get((int)s.charAt(i)) != null)
                {
                    disp -= (characterWidth.get((int)s.charAt(i)) + charSkipY) * mag;
                }
                else
                {
                    disp -= (charSpaceY + charSkipY) * mag;
                }
            }
            disp /= 2.4f;
        }

        for(int i = start; i < len; i++)
        {
            if(characters.get((int)s.charAt(i)) != null)
            {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, characters.get((int)s.charAt(i)));
                glUniform1i(uTextureLocation, 0);

                glUniform2f(dispLoc, x + disp, y);

                glBindBuffer(GL_ARRAY_BUFFER, vboHandle[1]);
                bindAttributes();
                glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
                disp += (characterWidth.get((int)s.charAt(i)) + charSkipY) * mag;
            }
            else
            {
                disp += (charSpaceY + charSkipY) * mag;
            }
        }
        return disp;
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

    private void initTextures(Context context)
    {
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
        characters.put((int)'A',TextureLoader.loadTexture(context, R.drawable.a));
        characters.put((int)'B',TextureLoader.loadTexture(context, R.drawable.b));
        characters.put((int)'C',TextureLoader.loadTexture(context, R.drawable.c));
        characters.put((int)'D',TextureLoader.loadTexture(context, R.drawable.d));
        characters.put((int)'E',TextureLoader.loadTexture(context, R.drawable.e));
        characters.put((int)'F',TextureLoader.loadTexture(context, R.drawable.f));
        characters.put((int)'G',TextureLoader.loadTexture(context, R.drawable.g));
        characters.put((int)'H',TextureLoader.loadTexture(context, R.drawable.h));
        characters.put((int)'I',TextureLoader.loadTexture(context, R.drawable.i));
        characters.put((int)'J',TextureLoader.loadTexture(context, R.drawable.j));
        characters.put((int)'K',TextureLoader.loadTexture(context, R.drawable.k));
        characters.put((int)'L',TextureLoader.loadTexture(context, R.drawable.l));
        characters.put((int)'M',TextureLoader.loadTexture(context, R.drawable.m));
        characters.put((int)'N',TextureLoader.loadTexture(context, R.drawable.n));
        characters.put((int)'O',TextureLoader.loadTexture(context, R.drawable.o));
        characters.put((int)'P',TextureLoader.loadTexture(context, R.drawable.p));
        characters.put((int)'Q',TextureLoader.loadTexture(context, R.drawable.q));
        characters.put((int)'R',TextureLoader.loadTexture(context, R.drawable.r));
        characters.put((int)'S',TextureLoader.loadTexture(context, R.drawable.s));
        characters.put((int)'T',TextureLoader.loadTexture(context, R.drawable.t));
        characters.put((int)'U',TextureLoader.loadTexture(context, R.drawable.u));
        characters.put((int)'V',TextureLoader.loadTexture(context, R.drawable.v));
        characters.put((int)'W',TextureLoader.loadTexture(context, R.drawable.w));
        characters.put((int)'X',TextureLoader.loadTexture(context, R.drawable.x));
        characters.put((int)'Y',TextureLoader.loadTexture(context, R.drawable.y));
        characters.put((int)'Z',TextureLoader.loadTexture(context, R.drawable.z));
        characters.put((int)'a',TextureLoader.loadTexture(context, R.drawable.al));
        characters.put((int)'b',TextureLoader.loadTexture(context, R.drawable.bl));
        characters.put((int)'c',TextureLoader.loadTexture(context, R.drawable.cl));
        characters.put((int)'d',TextureLoader.loadTexture(context, R.drawable.dl));
        characters.put((int)'e',TextureLoader.loadTexture(context, R.drawable.el));
        characters.put((int)'f',TextureLoader.loadTexture(context, R.drawable.fl));
        characters.put((int)'g',TextureLoader.loadTexture(context, R.drawable.gl));
        characters.put((int)'h',TextureLoader.loadTexture(context, R.drawable.hl));
        characters.put((int)'i',TextureLoader.loadTexture(context, R.drawable.il));
        characters.put((int)'j',TextureLoader.loadTexture(context, R.drawable.jl));
        characters.put((int)'k',TextureLoader.loadTexture(context, R.drawable.kl));
        characters.put((int)'l',TextureLoader.loadTexture(context, R.drawable.ll));
        characters.put((int)'m',TextureLoader.loadTexture(context, R.drawable.ml));
        characters.put((int)'n',TextureLoader.loadTexture(context, R.drawable.nl));
        characters.put((int)'o',TextureLoader.loadTexture(context, R.drawable.ol));
        characters.put((int)'p',TextureLoader.loadTexture(context, R.drawable.pl));
        characters.put((int)'q',TextureLoader.loadTexture(context, R.drawable.ql));
        characters.put((int)'r',TextureLoader.loadTexture(context, R.drawable.rl));
        characters.put((int)'s',TextureLoader.loadTexture(context, R.drawable.sl));
        characters.put((int)'t',TextureLoader.loadTexture(context, R.drawable.tl));
        characters.put((int)'u',TextureLoader.loadTexture(context, R.drawable.ul));
        characters.put((int)'v',TextureLoader.loadTexture(context, R.drawable.vl));
        characters.put((int)'w',TextureLoader.loadTexture(context, R.drawable.wl));
        characters.put((int)'x',TextureLoader.loadTexture(context, R.drawable.xl));
        characters.put((int)'y',TextureLoader.loadTexture(context, R.drawable.yl));
        characters.put((int)'z',TextureLoader.loadTexture(context, R.drawable.zl));
        characters.put((int)'.',TextureLoader.loadTexture(context, R.drawable.period));
        characters.put((int)',',TextureLoader.loadTexture(context, R.drawable.comma));
        characters.put((int)'\'',TextureLoader.loadTexture(context, R.drawable.apostrophe));
        characters.put((int)'0',TextureLoader.loadTexture(context, R.drawable.s0));
        characters.put((int)'1',TextureLoader.loadTexture(context, R.drawable.s1));
        characters.put((int)'2',TextureLoader.loadTexture(context, R.drawable.s2));
        characters.put((int)'3',TextureLoader.loadTexture(context, R.drawable.s3));
        characters.put((int)'4',TextureLoader.loadTexture(context, R.drawable.s4));
        characters.put((int)'5',TextureLoader.loadTexture(context, R.drawable.s5));
        characters.put((int)'6',TextureLoader.loadTexture(context, R.drawable.s6));
        characters.put((int)'7',TextureLoader.loadTexture(context, R.drawable.s7));
        characters.put((int)'8',TextureLoader.loadTexture(context, R.drawable.s8));
        characters.put((int)'9',TextureLoader.loadTexture(context, R.drawable.s9));
        characters.put((int)'|',TextureLoader.loadTexture(context, R.drawable.bar));
        characters.put((int)':',TextureLoader.loadTexture(context, R.drawable.colon));
        characters.put((int)'-',TextureLoader.loadTexture(context, R.drawable.hyphen));
        characters.put((int)'~',TextureLoader.loadTexture(context, R.drawable.tilde));

        characterWidth.put((int)'A', 5 * pixSize);
        characterWidth.put((int)'B', 5 * pixSize);
        characterWidth.put((int)'C', 5 * pixSize);
        characterWidth.put((int)'D', 5 * pixSize);
        characterWidth.put((int)'E', 4 * pixSize);
        characterWidth.put((int)'F', 4 * pixSize);
        characterWidth.put((int)'G', 5 * pixSize);
        characterWidth.put((int)'H', 5 * pixSize);
        characterWidth.put((int)'I', pixSize);
        characterWidth.put((int)'J', 5 * pixSize);
        characterWidth.put((int)'K', 5 * pixSize);
        characterWidth.put((int)'L', 4 * pixSize);
        characterWidth.put((int)'M', 7 * pixSize);
        characterWidth.put((int)'N', 5 * pixSize);
        characterWidth.put((int)'O', 5 * pixSize);
        characterWidth.put((int)'P', 5 * pixSize);
        characterWidth.put((int)'Q', 6 * pixSize);
        characterWidth.put((int)'R', 5 * pixSize);
        characterWidth.put((int)'S', 5 * pixSize);
        characterWidth.put((int)'T', 5 * pixSize);
        characterWidth.put((int)'U', 5 * pixSize);
        characterWidth.put((int)'V', 5 * pixSize);
        characterWidth.put((int)'W', 9 * pixSize);
        characterWidth.put((int)'X', 5 * pixSize);
        characterWidth.put((int)'Y', 7 * pixSize);
        characterWidth.put((int)'Z', 5 * pixSize);
        characterWidth.put((int)'a', 4 * pixSize);
        characterWidth.put((int)'b', 4 * pixSize);
        characterWidth.put((int)'c', 4 * pixSize);
        characterWidth.put((int)'d', 4 * pixSize);
        characterWidth.put((int)'e', 4 * pixSize);
        characterWidth.put((int)'f', 2 * pixSize);
        characterWidth.put((int)'g', 4 * pixSize);
        characterWidth.put((int)'h', 4 * pixSize);
        characterWidth.put((int)'i', pixSize);
        characterWidth.put((int)'j', 2 * pixSize);
        characterWidth.put((int)'k', 4 * pixSize);
        characterWidth.put((int)'l', pixSize);
        characterWidth.put((int)'m', 7 * pixSize);
        characterWidth.put((int)'n', 4 * pixSize);
        characterWidth.put((int)'o', 4 * pixSize);
        characterWidth.put((int)'p', 4 * pixSize);
        characterWidth.put((int)'q', 4 * pixSize);
        characterWidth.put((int)'r', 4 * pixSize);
        characterWidth.put((int)'s', 4 * pixSize);
        characterWidth.put((int)'t', 2 * pixSize);
        characterWidth.put((int)'u', 4 * pixSize);
        characterWidth.put((int)'v', 5 * pixSize);
        characterWidth.put((int)'w', 9 * pixSize);
        characterWidth.put((int)'x', 5 * pixSize);
        characterWidth.put((int)'y', 5 * pixSize);
        characterWidth.put((int)'z', 4 * pixSize);
        characterWidth.put((int)'.', pixSize);
        characterWidth.put((int)',', pixSize);
        characterWidth.put((int)'\'', pixSize);
        characterWidth.put((int)'0', 5 * pixSize);
        characterWidth.put((int)'1', 2 * pixSize);
        characterWidth.put((int)'2', 5 * pixSize);
        characterWidth.put((int)'3', 5 * pixSize);
        characterWidth.put((int)'4', 5 * pixSize);
        characterWidth.put((int)'5', 5 * pixSize);
        characterWidth.put((int)'6', 5 * pixSize);
        characterWidth.put((int)'7', 5 * pixSize);
        characterWidth.put((int)'8', 5 * pixSize);
        characterWidth.put((int)'9', 5 * pixSize);
        characterWidth.put((int)'|', pixSize);
        characterWidth.put((int)':', pixSize*2);
        characterWidth.put((int)'-', pixSize*4);
        characterWidth.put((int)'~', pixSize*6);
    }
}
