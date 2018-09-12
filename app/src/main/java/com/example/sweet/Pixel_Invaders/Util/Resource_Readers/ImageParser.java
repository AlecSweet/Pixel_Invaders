package com.example.sweet.Pixel_Invaders.Util.Resource_Readers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.BitmapFactory;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.CollidableGroup;
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

        int numGroupsWidth = bmWidth / Constants.CELL_LENGTH + 1;
        int numGroupsHeight = bmHeight / Constants.CELL_LENGTH + 1;
        CollidableGroup[] tempGroups = new CollidableGroup[numGroupsWidth * numGroupsHeight];

        int numZonesWidth = numGroupsWidth / Constants.ZONE_LENGTH + 1;
        int numZonesHeight = numGroupsHeight / Constants.ZONE_LENGTH + 1;
        Zone[] tempZones = new Zone[numZonesHeight * numZonesWidth];

        for (int r = 0; r < numZonesHeight; r++)
        {
            for (int c = 0; c < numZonesWidth; c++)
            {
                tempZones[c + numZonesWidth * r] = new Zone(
                        0,
                        0,
                        0,
                        new ArrayList<CollidableGroup>()
                );
            }
        }

        for (int r = 0; r < numGroupsHeight; r++)
        {
            for (int c = 0; c < numGroupsWidth; c++)
            {
                int tempG = c + numGroupsWidth * r;
                int tempZ = (c / Constants.ZONE_LENGTH) + (r / Constants.ZONE_LENGTH) * numZonesWidth;

                tempGroups[tempG] = new CollidableGroup(
                        0,
                        0,
                        0,
                        new ArrayList<Pixel>()
                );
                tempZones[tempZ].c.add(tempGroups[tempG]);
            }
        }

        int index = 0;
        for (int r = 0; r < bmHeight; r++)
        {
            for (int c = 0; c < bmWidth; c++)
            {
                int pixelColor = bm.getPixel(c, r);
                int lightFactor = lm.getPixel(c, r);
                if (Color.alpha(pixelColor) > 0)
                {
                    Pixel temp = new Pixel(r + 1, c + 1);
                    temp.row = r + 1;
                    temp.col = c + 1;
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
                    tempGroups[(c / Constants.CELL_LENGTH) + numGroupsWidth * (r / Constants.CELL_LENGTH)].p.add(temp);
                    index++;
                }
            }
        }
        bm.recycle();
        lm.recycle();

        Pixel[] pixels = new Pixel[index];
        //float[] locationVA = new float[pixels.length * 4];
        float[] vertArr = new float[pixels.length * 8];
        
        for(Zone z: tempZones)
        {
            float centerZX = 0;
            float centerZY = 0;
            z.initCollidableGroupArray();
            for(CollidableGroup cG: z.collidableGroups)
            {
                float centerPX = 0;
                float centerPY = 0;
                cG.initPixelArray();
                for(Pixel pix: cG.pixels)
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
                    int i = infoMapT[pix.row][pix.col].index;
                    pixels[i] = pix;
                    /*locationVA[i*4] = infoMapT[pix.row][pix.col].xOriginal;
                    locationVA[i*4 + 1] = infoMapT[pix.row][pix.col].yOriginal;
                    locationVA[i*4 + 2] = infoMapT[pix.row][pix.col].depth;
                    locationVA[i*4 + 3] = 0;*/
                    /*int ind = i * 8;
                    vertArr[ind] = infoMapT[pix.row][pix.col].xOriginal;
                    vertArr[ind + 1] = infoMapT[pix.row][pix.col].r;
                    vertArr[ind + 2] = infoMapT[pix.row][pix.col].yOriginal;
                    vertArr[ind + 3] = infoMapT[pix.row][pix.col].g;
                    vertArr[ind + 4] = infoMapT[pix.row][pix.col].depth;
                    vertArr[ind + 5] = infoMapT[pix.row][pix.col].b;
                    vertArr[ind + 6] = 0;
                    vertArr[ind + 7] = infoMapT[pix.row][pix.col].a;*/
                    int ind = i * 8;
                    vertArr[ind] = infoMapT[pix.row][pix.col].xOriginal;
                    vertArr[ind + 1] = infoMapT[pix.row][pix.col].yOriginal;
                    vertArr[ind + 2] = infoMapT[pix.row][pix.col].depth;
                    vertArr[ind + 3] = border;
                    vertArr[ind + 4] = infoMapT[pix.row][pix.col].r;
                    vertArr[ind + 5] = infoMapT[pix.row][pix.col].g;
                    vertArr[ind + 6] = infoMapT[pix.row][pix.col].b;
                    vertArr[ind + 7] = infoMapT[pix.row][pix.col].a;

                    centerPX += infoMapT[pix.row][pix.col].xOriginal;
                    centerPY += infoMapT[pix.row][pix.col].yOriginal;
                }
                if(cG.pixels.length > 1)
                {
                    centerPX /= cG.pixels.length;
                    centerPY /= cG.pixels.length;
                }
                float furthestDist = 0;
                for(Pixel pix: cG.pixels)
                {
                    float tempdist = VectorFunctions.getMagnitude(
                            centerPX - infoMapT[pix.row][pix.col].xOriginal,
                            centerPY - infoMapT[pix.row][pix.col].yOriginal);
                    if(tempdist > furthestDist)
                    {
                        furthestDist = tempdist;
                    }
                }
                cG.setInfo(centerPX, centerPY, furthestDist + Constants.PIXEL_SIZE*.7f);

                centerZX += cG.getxOriginal();
                centerZY += cG.getyOriginal();
            }

            if(z.collidableGroups.length > 1)
            {
                centerZX /= z.collidableGroups.length;
                centerZY /= z.collidableGroups.length;
            }

            float furthestDist = 0;
            for(CollidableGroup cG: z.collidableGroups)
            {

                float tempdist = VectorFunctions.getMagnitude(
                        centerZX - cG.getxOriginal(),
                        centerZY - cG.getyOriginal());
                if(tempdist > furthestDist)
                {
                    furthestDist = tempdist;
                }

            }
            z.setInfo(centerZX, centerZY, furthestDist + Constants.CELL_SIZE);
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

        /*FloatBuffer buf = ByteBuffer
                .allocateDirect(locationVA.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(locationVA);
        buf.position(0);*/
        FloatBuffer buf = ByteBuffer
                .allocateDirect(vertArr.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertArr);
        buf.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, vBuffer[0]);
        glBufferData(GL_ARRAY_BUFFER, buf.capacity() * Constants.BYTES_PER_FLOAT, buf, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        return new PixelGroup(
            pixels,
            halfSquareLength,
            tempZones,
            sL,
            vBuffer[0],
            infoMapT,
            pMap
        );
    }
}
