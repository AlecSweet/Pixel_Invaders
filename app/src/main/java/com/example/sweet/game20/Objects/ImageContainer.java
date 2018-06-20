package com.example.sweet.game20.Objects;

import com.example.sweet.game20.util.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;

/**
 * Created by Sweet on 6/20/2018.
 */

public class ImageContainer
{
    public int vboHandle[] = new int[1];

    public FloatBuffer buffer;

    //public float[] vertArr;

    public int textureHandle;

    public boolean dynamicLocation = false;

    public float
            x,
            y;

    public String name;

    public ImageContainer(int texture, float[] vA, float x, float y, String n)
    {
        textureHandle = texture;
        this.x = x;
        this.y = y;
        name = n;

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
}
