package com.example.sweet.game20.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.BitmapFactory;

import com.example.sweet.game20.Objects.CollidableGroup;
import com.example.sweet.game20.Objects.Pixel;
import com.example.sweet.game20.Objects.PixelGroup;
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
        PixelGroup pixelGroup;
        ArrayList<Pixel> p = new ArrayList<>();
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resourceID);
        Bitmap lm = BitmapFactory.decodeResource(context.getResources(), lightingID);
        int bmHeight = bm.getHeight();
        int bmWidth = bm.getWidth();
        Pixel[][] pMap = new Pixel[bmHeight][bmWidth];

        int numGroupsWidth = bmWidth / Constants.CELL_LENGTH + 1;
        int numGroupsHeight = bmHeight / Constants.CELL_LENGTH + 1;
        int numZonesWidth = numGroupsWidth / Constants.ZONE_LENGTH + 1;
        int numZonesHeight = numGroupsHeight / Constants.ZONE_LENGTH + 1;

        CollidableGroup[] collidableGroups = new CollidableGroup[numGroupsWidth * numGroupsHeight];
        Zone[][] zoneMap = new Zone[numZonesHeight][numZonesWidth];
        CollidableGroup[][] groupMap = new CollidableGroup[numGroupsHeight][numGroupsWidth];

        for (int r = 0; r < zoneMap.length; r++)
        {
            for (int c = 0; c < zoneMap[0].length; c++)
            {
                zoneMap[r][c] = new Zone(c * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.ZONE_SIZE / 2);
            }
        }

        for (int r = 0; r < groupMap.length; r++)
        {
            for (int c = 0; c < groupMap[0].length; c++)
            {
                groupMap[r][c] = new CollidableGroup(c * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.CELL_SIZE / 2);
                zoneMap[r / Constants.ZONE_LENGTH][c / Constants.ZONE_LENGTH].c.add(groupMap[r][c]);
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
                    groupMap[r / Constants.CELL_LENGTH][c / Constants.CELL_LENGTH].p.add(temp);
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

        int tempItr = 0;
        for (int r = 0; r < groupMap.length; r++)
        {
            for (int c = 0; c < groupMap[0].length; c++)
            {
                collidableGroups[tempItr] = groupMap[r][c];
                collidableGroups[tempItr].halfSquareLength += Constants.PIXEL_SIZE;
                collidableGroups[tempItr].initPixelArray();
                tempItr++;
            }
        }

        tempItr = 0;
        Zone[] tempZones = new Zone[zoneMap.length * zoneMap[0].length];
        for (int r = 0; r < zoneMap.length; r++)
        {
            for (int c = 0; c < zoneMap[0].length; c++)
            {
                tempZones[tempItr] = zoneMap[r][c];
                tempZones[tempItr].halfSquareLength += Constants.PIXEL_SIZE * 3;
                tempZones[tempItr].initCollidableGroupArray();
                tempItr++;
            }
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
                (float) halfSquareLength * Constants.PIXEL_SIZE + Constants.PIXEL_SIZE,
                tempZones,
                sL,
                vBuffer[0]
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
        int numZonesWidth = numGroupsWidth / Constants.ZONE_LENGTH + 1;
        int numZonesHeight = numGroupsHeight / Constants.ZONE_LENGTH + 1;

        CollidableGroup[] collidableGroups = new CollidableGroup[numGroupsWidth * numGroupsHeight];
        Zone[][] zoneMap = new Zone[numZonesHeight][numZonesWidth];
        CollidableGroup[][] groupMap = new CollidableGroup[numGroupsHeight][numGroupsWidth];

        for(int r = 0; r < zoneMap.length; r++)
        {
            for (int c = 0; c < zoneMap[0].length; c++)
            {
                zoneMap[r][c] = new Zone(c * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.ZONE_SIZE / 2);
                zoneMap[r][c].collidableGroups = new CollidableGroup[Constants.ZONE_LENGTH * Constants.ZONE_LENGTH];
            }
        }

        for(int r = 0; r < groupMap.length; r++)
        {
            for (int c = 0; c < groupMap[0].length; c++)
            {
                groupMap[r][c] = new CollidableGroup(c * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.CELL_SIZE / 2);
                groupMap[r][c].p = new ArrayList<>();
                //zoneMap[r / Constants.ZONE_LENGTH][c / Constants.ZONE_LENGTH].c.add(groupMap[r][c]);
                zoneMap[r / Constants.ZONE_LENGTH][c / Constants.ZONE_LENGTH].collidableGroups[zoneMap[r / Constants.ZONE_LENGTH][c / Constants.ZONE_LENGTH].tempItr] = groupMap[r][c];
                zoneMap[r / Constants.ZONE_LENGTH][c / Constants.ZONE_LENGTH].tempItr++;
            }
        }

        for(int r = 0; r < bmHeight; r++)
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
                    groupMap[r / Constants.CELL_LENGTH][c / Constants.CELL_LENGTH].p.add(temp);
                    //groupMap[r / Constants.CELL_LENGTH][c / Constants.CELL_LENGTH].tempItr++;
                }
            }
        }

        bm.recycle();
        lm.recycle();

        for(int r = 0; r < pMap.length; r++)
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
        for(int i = 0; i < pixels.length; i++)
        {
            pixels[i].xOriginal -= (float)bmWidth / 2;
            pixels[i].yOriginal -= (float)bmHeight / 2;
            pixels[i].xOriginal = pixels[i].xOriginal * Constants.PIXEL_SIZE;
            pixels[i].yOriginal = pixels[i].yOriginal * Constants.PIXEL_SIZE;
            for(int n = 0; n < 4; n++)
            {
                if (pixels[i].neighbors[n] == null)
                {
                    pixels[i].outside = true;
                }
            }
            locationVA[iter]= pixels[i].xOriginal;
            locationVA[iter+1]= pixels[i].yOriginal;
            locationVA[iter+2]= pixels[i].depth;
            iter += 3;
        }

        int halfSquareLength;
        if(bmHeight > bmWidth)
        {
            halfSquareLength = bmHeight / 2;
        }
        else
        {
            halfSquareLength = bmWidth / 2;
        }

        int tempItr = 0;
        for(int r = 0; r < groupMap.length; r++)
        {
            for (int c = 0; c < groupMap[0].length; c++)
            {
                collidableGroups[tempItr] = groupMap[r][c];
                collidableGroups[tempItr].halfSquareLength += Constants.PIXEL_SIZE;
                tempItr++;
            }
        }

        tempItr = 0;
        Zone[] tempZones = new Zone[zoneMap.length * zoneMap[0].length];
        for(int r = 0; r < zoneMap.length; r++)
        {
            for (int c = 0; c < zoneMap[0].length; c++)
            {
                tempZones[tempItr] = zoneMap[r][c];
                tempZones[tempItr].halfSquareLength += Constants.PIXEL_SIZE * 3;
                tempItr++;
            }
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
                (float)halfSquareLength * Constants.PIXEL_SIZE + Constants.PIXEL_SIZE,
                tempZones,
                sL,
                vBuffer[0]
        );
        pixelGroup.setpMap(pMap);
        return pixelGroup;
    }*/
    /*public PixelGroup clone()
    {
        Pixel[] pArr = new Pixel[pixels.length];
        Pixel[][] cloneMap = new Pixel[pMap.length][pMap[0].length];

        int halfSquareLength;
        if(pMap.length > pMap[0].length)
        {
            halfSquareLength = pMap.length / 2;
        }
        else
        {
            halfSquareLength = pMap[0].length / 2;
        }

        int numGroupsWidth = pMap[0].length/Constants.CELL_LENGTH + 1;
        int numGroupsHeight = pMap.length/Constants.CELL_LENGTH + 1;
        int numZonesWidth = numGroupsWidth / Constants.ZONE_LENGTH + 1;
        int numZonesHeight = numGroupsHeight / Constants.ZONE_LENGTH + 1;

        Zone[] zones = new Zone[numZonesHeight * numZonesWidth];
        CollidableGroup[] groups = new CollidableGroup[numGroupsHeight * numGroupsWidth];

        for(int r = 0; r < numZonesHeight; r++)
        {
            for (int c = 0; c < numZonesWidth; c++)
            {
                int tempZ = c + numZonesWidth * r;
                zones[tempZ] = new Zone(c * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.ZONE_SIZE / 2);
                zones[tempZ].xOriginal -= Constants.PIXEL_SIZE;
                zones[tempZ].yOriginal -= Constants.PIXEL_SIZE;
                zones[tempZ].move(-Constants.PIXEL_SIZE, -Constants.PIXEL_SIZE);
                zones[tempZ].halfSquareLength += Constants.PIXEL_SIZE;
            }
        }

        for(int r = 0; r < numGroupsHeight; r++)
        {
            for (int c = 0; c < numGroupsWidth; c++)
            {
                int tempG = c + numGroupsWidth * r;
                groups[tempG] = new CollidableGroup(c * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.CELL_SIZE / 2
                );
                groups[tempG].xOriginal -= Constants.PIXEL_SIZE;
                groups[tempG].yOriginal -= Constants.PIXEL_SIZE;
                groups[tempG].move(-Constants.PIXEL_SIZE, -Constants.PIXEL_SIZE);
                groups[tempG].halfSquareLength += Constants.PIXEL_SIZE;

                int tempZ = (c / Constants.ZONE_LENGTH) + (r / Constants.ZONE_LENGTH) * numZonesWidth;
                zones[tempZ].collidableGroups[zones[tempZ].tempItr] = groups[c + numGroupsWidth * r];
                zones[tempZ].tempItr++;
            }
        }

        int itr = 0;
        for(int r = 0; r < pMap.length; r++)
        {
            for (int c = 0; c < pMap[0].length; c++)
            {
                if (pMap[r][c] != null)
                {
                    cloneMap[r][c] = pMap[r][c].clone();
                    pArr[itr] = cloneMap[r][c];
                    itr++;
                    int temp = (c / Constants.CELL_LENGTH) + numGroupsWidth * (r / Constants.CELL_LENGTH);
                    groups[temp].pixels[groups[temp].tempItr] = cloneMap[r][c];
                    groups[temp].tempItr++;
                }
            }
        }

        for(int r = 0; r < cloneMap.length; r++)
        {
            for(int c = 0; c < cloneMap[0].length; c++)
            {
                if(cloneMap[r][c] != null)
                {
                    if(r - 1 >= 0)
                    {
                        cloneMap[r][c].neighbors[0] = cloneMap[r - 1][c]; //Up
                    }
                    if(r + 1 < cloneMap.length)
                    {
                        cloneMap[r][c].neighbors[2] = cloneMap[r + 1][c]; //Down
                    }
                    if(c - 1 >= 0)
                    {
                        cloneMap[r][c].neighbors[3] = cloneMap[r][c - 1]; //Left
                    }
                    if(c + 1 < cloneMap[0].length)
                    {
                        cloneMap[r][c].neighbors[1] = cloneMap[r][c + 1]; //Right
                    }
                }
            }
        }

        for(int i = 0; i < pixels.length; i++)
        {
            for(int n = 0; n < 4; n++)
            {
                if (pArr[i].neighbors[n] == null)
                {
                    pArr[i].outside = true;
                }
            }
        }

        PixelGroup pixelGroup = new PixelGroup(
                pArr,
                (float)halfSquareLength * Constants.PIXEL_SIZE,
                zones,
                shaderLocation,
                vBuffer[0]
        );
        pixelGroup.setpMap(cloneMap);
        pixelGroup.restorable = restorable;
        return pixelGroup;
    }*/
}
