package com.example.sweet.Pixel_Invaders.Util.Resource_Readers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.BitmapFactory;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.CollidableGroup;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.CollisionMethods;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Zone;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Static.VectorFunctions;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;

/**
 * Created by Sweet on 4/6/2018.
 */

public class ImageParser
{
    public static PixelGroup parseImage(Context context, int resourceID, int lightingID, int sL, float border)
    {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resourceID);
        Bitmap lm = BitmapFactory.decodeResource(context.getResources(), lightingID);

        int bmHeight = bm.getHeight();
        int bmWidth = bm.getWidth();
        Pixel[][] pMap = new Pixel[bmHeight + 2][bmWidth + 2];
        PixelInfo[][] infoMapT = new PixelInfo[bmHeight + 2][bmWidth + 2];

        int index = 0;
        for (int r = 0; r < bmHeight; r++)
        {
            for (int c = 0; c < bmWidth; c++)
            {
                int tempR = bmHeight - 1 - r;
                int pixelColor = bm.getPixel(c, tempR);
                int lightFactor = lm.getPixel(c, tempR);
                if (Color.alpha(pixelColor) > 0)
                {
                    Pixel temp = new Pixel((short)(r + 1),(short)(c + 1));
                    temp.row = (short)(r + 1);
                    temp.col = (short)(c + 1);
                    PixelInfo pI = new PixelInfo(
                            ((c) - (float)bmWidth / 2f) * Constants.PIXEL_SIZE,
                            ((r) - (float)bmHeight / 2f) * Constants.PIXEL_SIZE,
                            (float) Color.alpha(lightFactor) / 255f,
                            (float) Color.red(pixelColor) / 255f,
                            (float) Color.green(pixelColor) / 255f,
                            (float) Color.blue(pixelColor) / 255f,
                            (float) Color.alpha(pixelColor) / 255f,
                            index
                    );
                    infoMapT[r + 1][c + 1] = pI;
                    pMap[r + 1][c + 1] = temp;
                    index++;
                }
            }
        }

        bm.recycle();
        lm.recycle();

        Pixel[] pixels = new Pixel[index];
        float[] vertArr = new float[pixels.length * 8];

        for(Pixel[] pA: pMap)
        {
            for (Pixel pix : pA)
            {
                if(pix != null)
                {
                    if (pMap[pix.row + 1][pix.col] == null)
                    {
                        infoMapT[pix.row][pix.col].originalState = 2;
                        pix.state = 2;
                    }
                    else if (pMap[pix.row - 1][pix.col] == null)
                    {
                        infoMapT[pix.row][pix.col].originalState = 2;
                        pix.state = 2;
                    }
                    else if (pMap[pix.row][pix.col + 1] == null)
                    {
                        infoMapT[pix.row][pix.col].originalState = 2;
                        pix.state = 2;
                    }
                    else if (pMap[pix.row][pix.col - 1] == null)
                    {
                        infoMapT[pix.row][pix.col].originalState = 2;
                        pix.state = 2;
                    }

                    pixels[infoMapT[pix.row][pix.col].index] = pix;

                    int ind = infoMapT[pix.row][pix.col].index * 8;
                    vertArr[ind] = infoMapT[pix.row][pix.col].xOriginal;
                    vertArr[ind + 1] = infoMapT[pix.row][pix.col].yOriginal;
                    vertArr[ind + 2] = infoMapT[pix.row][pix.col].depth;
                    vertArr[ind + 3] = border;
                    vertArr[ind + 4] = infoMapT[pix.row][pix.col].r;
                    vertArr[ind + 5] = infoMapT[pix.row][pix.col].g;
                    vertArr[ind + 6] = infoMapT[pix.row][pix.col].b;
                    vertArr[ind + 7] = infoMapT[pix.row][pix.col].a;
                }
            }
        }
        float halfSquareLength;
        if (bmHeight > bmWidth)
        {
            halfSquareLength = (float)bmHeight / 2f * Constants.PIXEL_SIZE;
        }
        else
        {
            halfSquareLength = (float)bmWidth / 2f * Constants.PIXEL_SIZE;
        }
        halfSquareLength *= 1.4;


        int[] vBuffer = new int[1];
        glGenBuffers(1, vBuffer, 0);

        FloatBuffer buf = ByteBuffer
                .allocateDirect(vertArr.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertArr);
        buf.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, vBuffer[0]);
        glBufferData(GL_ARRAY_BUFFER, buf.capacity() * Constants.BYTES_PER_FLOAT, buf, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        PixelGroup temp = new PixelGroup(
            pixels,
            halfSquareLength,
            sL,
            vBuffer[0],
            infoMapT,
            pMap
        );
        temp.setHitbox(CollisionMethods.setupNewHitboxTree(infoMapT,pMap));
        return temp;
    }
}
