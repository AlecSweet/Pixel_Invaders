package com.example.sweet.game20.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Sweet on 5/17/2018.
 */

public class BasicBulletData
{
    public static final float[][] x = new float[][]{
            { -0.0168f, -0.0112f, -0.0056f, 0.0f, 0.0056f, 0.0112f},
            { -0.0168f, -0.0112f, -0.0056f, 0.0f, 0.0056f, 0.0112f},
     };

    public static final float[][] y = new float[][]{
             { -0.0056f, -0.0056f, -0.0056f, -0.0056f, -0.0056f, -0.0056f},
             { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f},
    };

    public static final float[] colors = new float[]{
            0.90588236f, 0.39215687f, 0.92156863f, 1.0f, 0.90588236f, 0.39215687f, 0.92156863f, 1.0f, 0.9137255f, 0.627451f, 0.92156863f, 1.0f, 0.9137255f, 0.627451f, 0.92156863f, 1.0f, 0.90588236f, 0.39215687f, 0.92156863f, 1.0f, 0.90588236f, 0.39215687f, 0.92156863f, 1.0f,
            0.90588236f, 0.39215687f, 0.92156863f, 1.0f, 0.90588236f, 0.39215687f, 0.92156863f, 1.0f, 0.9137255f, 0.627451f, 0.92156863f, 1.0f, 0.9137255f, 0.627451f, 0.92156863f, 1.0f, 0.90588236f, 0.39215687f, 0.92156863f, 1.0f, 0.90588236f, 0.39215687f, 0.92156863f, 1.0f,
    };

    public static final float[] posArr = new float[]{
                -0.0168f, -0.0056f, 1.0f,
                 -0.0112f, -0.0056f, 1.0f, -0.0056f, -0.0056f, 1.0f, 0.0f, -0.0056f, 1.0f, 0.0056f, -0.0056f, 1.0f, 0.0112f, -0.0056f, 1.0f, -0.0168f, 0.0f, 1.0f, -0.0112f, 0.0f, 1.0f, -0.0056f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0056f, 0.0f, 1.0f, 0.0112f, 0.0f, 1.0f
    };

    public static final FloatBuffer posBuf = (FloatBuffer)ByteBuffer
            .allocateDirect(posArr.length * Constants.BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(posArr).position(0);
}
