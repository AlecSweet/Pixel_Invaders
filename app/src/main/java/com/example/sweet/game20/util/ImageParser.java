package com.example.sweet.game20.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.BitmapFactory;

import com.example.sweet.game20.Objects.CollidableGroup;
import com.example.sweet.game20.Objects.Pixel;
import com.example.sweet.game20.Objects.PixelGroup;
import com.example.sweet.game20.Objects.PixelInfo;
import com.example.sweet.game20.Objects.Zone;

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
    public static PixelGroup parseImage(Context context, int resourceID, int lightingID, int sL)
    {
        float cmX = 0;
        float cmY = 0;
        int numOutside = 0;
        PixelGroup pixelGroup;
        ArrayList<Pixel> p = new ArrayList<>();
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
                int tempZ = c + numZonesWidth * r;
                tempZones[tempZ] = new Zone(
                        c * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * (float)numZonesWidth / 2f + Constants.PIXEL_SIZE,
                        r * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * (float)numZonesHeight / 2f + Constants.PIXEL_SIZE,
                        Constants.ZONE_SIZE / 2,
                        new ArrayList<CollidableGroup>()
                );
                /*tempZones[tempZ] = new Zone(
                        c * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesWidth / 2,
                        r * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesHeight / 2,
                        Constants.ZONE_SIZE / 2,
                        new ArrayList<CollidableGroup>()
                );*/
            }
        }

        for (int r = 0; r < numGroupsHeight; r++)
        {
            for (int c = 0; c < numGroupsWidth; c++)
            {
                int tempG = c + numGroupsWidth * r;
                int tempZ = (c / Constants.ZONE_LENGTH) + (r / Constants.ZONE_LENGTH) * numZonesWidth;

                tempGroups[tempG] = new CollidableGroup(
                        c * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * (float)numGroupsWidth / 2f + Constants.PIXEL_SIZE,
                        r * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * (float)numGroupsHeight / 2f + Constants.PIXEL_SIZE,
                        Constants.CELL_SIZE / 2,
                        new ArrayList<Pixel>()
                );
                /*tempGroups[tempG] = new CollidableGroup(
                        c * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsWidth / 2,
                        r * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsHeight / 2,
                        Constants.CELL_SIZE / 2,
                        new ArrayList<Pixel>()
                );*/
                tempZones[tempZ].c.add(tempGroups[tempG]);
            }
        }

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
                    p.add(temp);
                    PixelInfo pI = new PixelInfo(
                            ((c) - (float)bmWidth / 2f) * Constants.PIXEL_SIZE,
                            ((r) - (float)bmHeight / 2f) * Constants.PIXEL_SIZE,
                            (float) Color.alpha(lightFactor) / 255f,
                            (float) Color.red(pixelColor) / 255f,
                            (float) Color.green(pixelColor) / 255f,
                            (float) Color.blue(pixelColor) / 255f,
                            (float) Color.alpha(pixelColor) / 255f
                    );
                    infoMapT[r + 1][c + 1] = pI;
                    pMap[r + 1][c + 1] = temp;
                    tempGroups[(c / Constants.CELL_LENGTH) + numGroupsWidth * (r / Constants.CELL_LENGTH)].p.add(temp);
                }
            }
        }

        bm.recycle();
        lm.recycle();

        Pixel[] pixels = p.toArray(new Pixel[p.size()]);
        float[] locationVA = new float[pixels.length * 3];

        /*int iter = 0;
        for (int i = 0; i < pixels.length; i++)
        {
            pixels[i].xOriginal -= (float) bmWidth / 2;
            pixels[i].yOriginal -= (float) bmHeight / 2;
            pixels[i].xOriginal = pixels[i].xOriginal * Constants.PIXEL_SIZE;
            pixels[i].yOriginal = pixels[i].yOriginal * Constants.PIXEL_SIZE;
            for (int n = 0; n < 4; n++)
            {
                if (pixels[i].neighbors[n] == null)
                {
                    pixels[i].outside = true;
                }
            }
            locationVA[iter] = pixels[i].xOriginal;
            locationVA[iter + 1] = pixels[i].yOriginal;
            locationVA[iter + 2] = pixels[i].depth;
            iter += 3;
        }*/
        int iter = 0;
        for(Pixel pix: p)
        {
            /*pix.xOriginal -= (float) bmWidth / 2;
            pix.yOriginal -= (float) bmHeight / 2;
            pix.xOriginal = pix.xOriginal * Constants.PIXEL_SIZE;
            pix.yOriginal = pix.yOriginal * Constants.PIXEL_SIZE;*/

            if (pMap[pix.row + 1][pix.col] == null)
            {
                //pix.outside = true;
                pix.state = 2;
            }
            else if (pMap[pix.row - 1][pix.col] == null)
            {
                //pix.outside = true;
                pix.state = 2;
            }
            else if (pMap[pix.row][pix.col + 1] == null)
            {
                //pix.outside = true;
                pix.state = 2;
            }
            else if (pMap[pix.row][pix.col - 1] == null)
            {
                //pix.outside = true;
                pix.state = 2;
            }

            /*if(pix.state == 2)
            {
                cmX += infoMapT[pix.row][pix.col].xOriginal;
                cmY += infoMapT[pix.row][pix.col].yOriginal;
                numOutside++;
            }*/
            locationVA[iter] = infoMapT[pix.row][pix.col].xOriginal;
            locationVA[iter + 1] = infoMapT[pix.row][pix.col].yOriginal;
            locationVA[iter + 2] = infoMapT[pix.row][pix.col].depth;
            iter += 3;
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

        for(CollidableGroup cG: tempGroups)
        {
            cG.halfSquareLength *= 1.4;
            cG.initPixelArray();
        }

        for(Zone z: tempZones)
        {
            z.halfSquareLength *= 1.4;
            z.initCollidableGroupArray();
        }

        int[] vBuffer = new int[1];
        glGenBuffers(1, vBuffer, 0);

        FloatBuffer buf = ByteBuffer
                .allocateDirect(locationVA.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(locationVA);
        buf.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, vBuffer[0]);
        glBufferData(GL_ARRAY_BUFFER, buf.capacity() * Constants.BYTES_PER_FLOAT, buf, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        pixelGroup = new PixelGroup(
                pixels,
                halfSquareLength,
                tempZones,
                tempGroups,
                sL,
                vBuffer[0],
                infoMapT
        );
        pixelGroup.setpMap(pMap);
        return pixelGroup;
    }

    /*public static PixelGroup parseImage(Context context, int resourceID, int lightingID, int sL)
    {
        PixelGroup pixelGroup;
        ArrayList<Pixel> p = new ArrayList<>();
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resourceID);
        Bitmap lm = BitmapFactory.decodeResource(context.getResources(), lightingID);

        int bmHeight = bm.getHeight();
        int bmWidth = bm.getWidth();
        Pixel[][] pMap = new Pixel[bmHeight][bmWidth];

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
                int tempZ = c + numZonesWidth * r;
                tempZones[tempZ] = new Zone(c * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.ZONE_SIZE / 2,
                        new ArrayList<CollidableGroup>());
            }
        }

        for (int r = 0; r < numGroupsHeight; r++)
        {
            for (int c = 0; c < numGroupsWidth; c++)
            {
                int tempG = c + numGroupsWidth * r;
                int tempZ = (c / Constants.ZONE_LENGTH) + (r / Constants.ZONE_LENGTH) * numZonesWidth;

                tempGroups[tempG] = new CollidableGroup(c * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.CELL_SIZE / 2,
                        new ArrayList<Pixel>());
                tempZones[tempZ].c.add(tempGroups[tempG]);
            }
        }

        for (int r = 0; r < bmHeight; r++)
        {
            for (int c = 0; c < bmWidth; c++)
            {
                int pixelColor = bm.getPixel(c, r);
                int lightFactor = lm.getPixel(c, r);
                if (Color.alpha(pixelColor) > 0)
                {
                    Pixel temp = new Pixel(c, r);
                    temp.r = (float) Color.red(pixelColor) / 255f;
                    temp.g = (float) Color.green(pixelColor) / 255f;
                    temp.b = (float) Color.blue(pixelColor) / 255f;
                    temp.a = (float) Color.alpha(pixelColor) / 255f;
                    temp.depth = (float) Color.alpha(lightFactor) / 255f;
                    p.add(temp);
                    pMap[r][c] = temp;
                    tempGroups[(c / Constants.CELL_LENGTH) + numGroupsWidth * (r / Constants.CELL_LENGTH)].p.add(temp);
                }
            }
        }

        bm.recycle();
        lm.recycle();

        for (int r = 0; r < pMap.length; r++)
        {
            for (int c = 0; c < pMap[0].length; c++)
            {
                if (pMap[r][c] != null)
                {
                    if (r - 1 >= 0)
                        pMap[r][c].neighbors[0] = pMap[r - 1][c]; //Up
                    if (r + 1 < pMap.length)
                        pMap[r][c].neighbors[2] = pMap[r + 1][c]; //Down
                    if (c - 1 >= 0)
                        pMap[r][c].neighbors[3] = pMap[r][c - 1]; //Left
                    if (c + 1 < pMap[0].length)
                        pMap[r][c].neighbors[1] = pMap[r][c + 1]; //Right
                }
            }
        }

        Pixel[] pixels = p.toArray(new Pixel[p.size()]);
        float[] locationVA = new float[pixels.length * 3];

        int iter = 0;
        for (int i = 0; i < pixels.length; i++)
        {
            pixels[i].xOriginal -= (float) bmWidth / 2;
            pixels[i].yOriginal -= (float) bmHeight / 2;
            pixels[i].xOriginal = pixels[i].xOriginal * Constants.PIXEL_SIZE;
            pixels[i].yOriginal = pixels[i].yOriginal * Constants.PIXEL_SIZE;
            for (int n = 0; n < 4; n++)
            {
                if (pixels[i].neighbors[n] == null)
                {
                    pixels[i].outside = true;
                }
            }
            locationVA[iter] = pixels[i].xOriginal;
            locationVA[iter + 1] = pixels[i].yOriginal;
            locationVA[iter + 2] = pixels[i].depth;
            iter += 3;
        }

        int halfSquareLength;
        if (bmHeight > bmWidth)
        {
            halfSquareLength = bmHeight / 2;
        } else
        {
            halfSquareLength = bmWidth / 2;
        }

        for(CollidableGroup cG: tempGroups)
        {
            cG.halfSquareLength += Constants.PIXEL_SIZE;
            cG.initPixelArray();
        }
        *//*int tempItr = 0;
        for (int r = 0; r < numGroupsHeight; r++)
        {
            for (int c = 0; c < numGroupsWidth; c++)
            {
                collidableGroups[tempItr] = groupMap[r][c];
                collidableGroups[tempItr].halfSquareLength += Constants.PIXEL_SIZE;
                collidableGroups[tempItr].initPixelArray();
                tempItr++;
            }
        }*//*

        for(Zone z: tempZones)
        {
            z.halfSquareLength += Constants.PIXEL_SIZE * 3;
            z.initCollidableGroupArray();
        }
        *//*tempItr = 0;

        for (int r = 0; r < zoneMap.length; r++)
        {
            for (int c = 0; c < zoneMap[0].length; c++)
            {
                tempZones[tempItr] = zoneMap[r][c];
                tempZones[tempItr].halfSquareLength += Constants.PIXEL_SIZE * 3;
                tempZones[tempItr].initCollidableGroupArray();
                tempItr++;
            }
        }*//*

        int[] vBuffer = new int[1];
        glGenBuffers(1, vBuffer, 0);

        FloatBuffer buf = ByteBuffer
                .allocateDirect(locationVA.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(locationVA);
        buf.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, vBuffer[0]);
        glBufferData(GL_ARRAY_BUFFER, buf.capacity() * Constants.BYTES_PER_FLOAT, buf, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        pixelGroup = new PixelGroup(
                pixels,
                (float) halfSquareLength * Constants.PIXEL_SIZE + Constants.PIXEL_SIZE,
                tempZones,
                tempGroups,
                sL,
                vBuffer[0]
        );
        pixelGroup.setpMap(pMap);
        return pixelGroup;
    }*/
}
