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
    public static PixelGroup parseImage(Context context, int resourceID, int lightingID, int textureID, int sL)
    {
        PixelGroup pixelGroup;
        ArrayList<Pixel> p = new ArrayList<>();
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resourceID);
        Bitmap lm = BitmapFactory.decodeResource(context.getResources(), lightingID);
        int bmHeight = bm.getHeight();
        int bmWidth = bm.getWidth();
        Pixel[][] pMap = new Pixel[bmHeight][bmWidth];

        int numGroupsWidth = (int)((float)bmWidth/Constants.CELL_LENGTH) + 1;
        int numGroupsHeight = (int)((float)bmHeight/Constants.CELL_LENGTH) + 1;
        int numZonesWidth = (int)((float)(numGroupsWidth) / Constants.ZONE_LENGTH) + 1;
        int numZonesHeight = (int)((float)(numGroupsHeight) / Constants.ZONE_LENGTH) + 1;
        System.out.println("Groups " + numGroupsHeight + ", " + numGroupsWidth);
        System.out.println("Zones " + numZonesHeight + ", " + numZonesWidth);
        CollidableGroup[] collidableGroups = new CollidableGroup[numGroupsWidth * numGroupsHeight];
        Zone[][] zoneMap = new Zone[numZonesHeight][numZonesWidth];
        CollidableGroup[][] groupMap = new CollidableGroup[numGroupsHeight][numGroupsWidth];

        for(int r = 0; r < zoneMap.length; r++)
            for(int c = 0; c < zoneMap[0].length; c++)
                zoneMap[r][c] = new Zone(c * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.ZONE_SIZE / 2);

        for(int r = 0; r < groupMap.length; r++)
            for(int c = 0; c < groupMap[0].length; c++)
            {
                groupMap[r][c] = new CollidableGroup(c * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.CELL_SIZE / 2);
                zoneMap[(int)((float)r / Constants.ZONE_LENGTH)][(int)((float)c / Constants.ZONE_LENGTH)].c.add(groupMap[r][c]);
            }

        for(int r = 0; r < bmHeight; r++)
            for(int c = 0; c < bmWidth; c++)
            {
                int pixelColor = bm.getPixel(c, r);
                int lightFactor = lm.getPixel(c, r);
                if (Color.alpha(pixelColor) > 0)
                {
                    Pixel temp = new Pixel(c, r);
                    temp.r = (float)Color.red(pixelColor) / 255f;
                    temp.g = (float)Color.green(pixelColor) / 255f;
                    temp.b = (float)Color.blue(pixelColor) / 255f;
                    temp.a = (float)Color.alpha(pixelColor) / 255f;
                    temp.depth = (float)Color.alpha(lightFactor) / 255f;
                    p.add(temp);
                    pMap[r][c] = temp;
                    groupMap[(int)((float)r / Constants.CELL_LENGTH)][(int)((float)c / Constants.CELL_LENGTH)].p.add(temp);
                }
            }

        bm.recycle();
        lm.recycle();

        for(int r = 0; r < pMap.length; r++)
            for(int c = 0; c < pMap[0].length; c++)
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


        Pixel[] pixels = p.toArray(new Pixel[p.size()]);
        System.out.println("posloc{");
        float[] locationVA = new float[pixels.length * 3];
        int iter = 0;
        for(int i = 0; i < pixels.length; i++)
        {
            pixels[i].xOriginal -= (float)bmWidth / 2;
            pixels[i].yOriginal -= (float)bmHeight / 2;
            pixels[i].xOriginal = pixels[i].xOriginal * Constants.PIXEL_SIZE;
            pixels[i].yOriginal = pixels[i].yOriginal * Constants.PIXEL_SIZE;

            //pixels[i].xDisp = pixels[i].xOriginal;
            //pixels[i].yDisp = pixels[i].yOriginal;

            for(int n = 0; n < 4; n++)
                if(pixels[i].neighbors[n] == null)
                    pixels[i].outside = true;
            //System.out.print(pixels[i].xOriginal + "f, " + pixels[i].yOriginal + "f, " + pixels[i].depth + "f, ");
            if(i%16 == 0)
                System.out.println();
            locationVA[iter]= pixels[i].xOriginal;
            locationVA[iter+1]= pixels[i].yOriginal;
            locationVA[iter+2]= pixels[i].depth;
            iter += 3;
        }
        System.out.println("}");

        int halfSquareLength;
        if(bmHeight > bmWidth)
            halfSquareLength = bmHeight / 2 ;
        else
            halfSquareLength = bmWidth / 2;

        int tempItr = 0;
        for(int r = 0; r < groupMap.length; r++)
            for(int c = 0; c < groupMap[0].length; c++)
            {
                collidableGroups[tempItr] = groupMap[r][c];
                //collidableGroups[tempItr].xOriginal -= Constants.PIXEL_SIZE;
                //collidableGroups[tempItr].yOriginal -= Constants.PIXEL_SIZE;
                //collidableGroups[tempItr].move(-Constants.PIXEL_SIZE,-Constants.PIXEL_SIZE);
                collidableGroups[tempItr].halfSquareLength+= Constants.PIXEL_SIZE;
                collidableGroups[tempItr].initPixelArray();
                tempItr++;
            }

        tempItr = 0;
        Zone[] tempZones = new Zone[zoneMap.length * zoneMap[0].length];
        for(int r = 0; r < zoneMap.length; r++)
            for(int c = 0; c < zoneMap[0].length; c++)
            {
                tempZones[tempItr] = zoneMap[r][c];
                //tempZones[tempItr].xOriginal -= Constants.PIXEL_SIZE;
                //tempZones[tempItr].yOriginal -= Constants.PIXEL_SIZE;
                //tempZones[tempItr].move(-Constants.PIXEL_SIZE,-Constants.PIXEL_SIZE);
                tempZones[tempItr].halfSquareLength+= Constants.PIXEL_SIZE;
                tempZones[tempItr].initCollidableGroupArray();
                tempItr++;
            }


        /*for(int i = 0; i < collidableGroups.length; i++)
        {
            System.out.println("Collidable Group:  " + collidableGroups[i].x + ", " + collidableGroups[i].y);
            for(int t = 0; t < collidableGroups[i].pixels.size(); t++)
                System.out.println(collidableGroups[i].pixels.get(t).xDisp + ", " + collidableGroups[i].pixels.get(t).yDisp);
        }*/
        /*System.out.println("x{");
        for(int r = 0; r < pMap.length; r++)
        {
            System.out.print(" { ");
            for (int c = 0; c < pMap[0].length; c++)
            {
                if (pMap[r][c] != null)
                    System.out.print(pMap[r][c].xOriginal + "f, ");
                else
                    System.out.print(-1 + "f, ");

            }
            System.out.println("}, ");
        }
        System.out.println(" }");

        System.out.println("y{");
        for(int r = 0; r < pMap.length; r++)
        {
            System.out.print(" { ");
            for (int c = 0; c < pMap[0].length; c++)
            {
                if (pMap[r][c] != null)
                    System.out.print(pMap[r][c].yOriginal + "f, ");
                else
                    System.out.print(-1 + "f, ");

            }
            System.out.println("}, ");
        }
        System.out.println(" }");

        System.out.println("live{");
        for(int r = 0; r < pMap.length; r++)
        {
            System.out.print(" { ");
            for (int c = 0; c < pMap[0].length; c++)
            {
                if (pMap[r][c] != null && pMap[r][c].live)
                    System.out.print(1 + "f, ");
                else if(pMap[r][c] != null && pMap[r][c].live && pMap[r][c].outside)
                    System.out.print(2 + "f, ");
                else
                    System.out.print(-1 + "f, ");

            }
            System.out.println("}, ");
        }
        System.out.println(" }");

        System.out.println("color{");
        for(int r = 0; r < pMap.length; r++)
        {
            for (int c = 0; c < pMap[0].length; c++)
            {
                if (pMap[r][c] != null)
                {
                    System.out.print(pMap[r][c].r + "f, ");
                    System.out.print(pMap[r][c].g + "f, ");
                    System.out.print(pMap[r][c].b + "f, ");
                    System.out.print(pMap[r][c].a + "f, ");
                }

            }
            System.out.println();
        }
        System.out.println(" }");*/
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

        pixelGroup = new PixelGroup(pixels,(float)halfSquareLength * Constants.PIXEL_SIZE + Constants.PIXEL_SIZE, tempZones, textureID, sL,vBuffer[0]);
        pixelGroup.setpMap(pMap);
        return pixelGroup;
    }

    /*public static PixelGroup parseImage(Context context, int resourceID, int resourceID2, int resourceID3, int resourceID4, int resourceID5, int textureID, int sL)
    {
        PixelGroup pixelGroup;
        ArrayList<Pixel> p = new ArrayList<>();
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resourceID);
        Bitmap bm2 = BitmapFactory.decodeResource(context.getResources(), resourceID2);
        Bitmap bm3 = BitmapFactory.decodeResource(context.getResources(), resourceID3);
        Bitmap bm4 = BitmapFactory.decodeResource(context.getResources(), resourceID4);
        Bitmap bm5 = BitmapFactory.decodeResource(context.getResources(), resourceID5);
        int bmHeight = bm.getHeight();
        int bmWidth = bm.getWidth();
        Pixel[][] pMap = new Pixel[bmHeight][bmWidth];

        int numGroupsWidth = (int)((float)bmWidth/Constants.CELL_LENGTH) + 1;
        int numGroupsHeight = (int)((float)bmHeight/Constants.CELL_LENGTH) + 1;
        CollidableGroup[] collidableGroups = new CollidableGroup[numGroupsWidth * numGroupsHeight];
        CollidableGroup[][] groupMap = new CollidableGroup[numGroupsHeight][numGroupsWidth];

        for(int r = 0; r < groupMap.length; r++)
            for(int c = 0; c < groupMap[0].length; c++)
                groupMap[r][c] = new CollidableGroup(c * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.CELL_SIZE / 2 );

        for(int r = 0; r < bmHeight; r++)
            for(int c = 0; c < bmWidth; c++)
            {
                int pixelColor[] = new int[5];
                pixelColor[0] = bm.getPixel(c, r);
                pixelColor[1] = bm2.getPixel(c, r);
                pixelColor[2] = bm3.getPixel(c, r);
                pixelColor[3] = bm4.getPixel(c, r);
                pixelColor[4] = bm5.getPixel(c, r);

                if(Color.alpha(pixelColor[0]) > 0)
                {
                    Pixel temp = new Pixel(c, r);
                    temp.xIndex = c;
                    temp.yIndex = r;
                    p.add(temp);
                    pMap[r][c] = temp;
                    groupMap[(int) ((float) r / Constants.CELL_LENGTH)][(int) ((float) c / Constants.CELL_LENGTH)].pixels.add(temp);

                    for (int i = 0; i < 5; i++) {
                        if (Color.alpha(pixelColor[i]) > 0) {
                            temp.r[i] = (float) Color.red(pixelColor[i]) / 255f;
                            temp.g[i] = (float) Color.green(pixelColor[i]) / 255f;
                            temp.b[i] = (float) Color.blue(pixelColor[i]) / 255f;
                            temp.a[i] = (float) Color.alpha(pixelColor[i]) / 255f;
                        }
                    }
                }
            }

        for(int r = 0; r < pMap.length; r++)
            for(int c = 0; c < pMap[0].length; c++)
                if(pMap[r][c] != null)
                {
                    if(r - 1 >= 0)
                        pMap[r][c].neighbors[0] = pMap[r - 1][c]; //Up
                    if(r + 1 < pMap.length)
                        pMap[r][c].neighbors[2] = pMap[r + 1][c]; //Down
                    if(c - 1 >= 0)
                        pMap[r][c].neighbors[3] = pMap[r][c - 1]; //Left
                    if(c + 1 < pMap[0].length)
                        pMap[r][c].neighbors[1] = pMap[r][c + 1]; //Right
                }

        //boolean widthOdd = (bmWidth % 2 == 1);
        //boolean heightOdd = (bmHeight % 2 == 1);
        for(Pixel pixel: p)
        {
            //if(widthOdd)
            pixel.xOriginal -= (float)bmWidth / 2;
            pixel.yOriginal -= (float)bmHeight / 2;
            pixel.xOriginal = pixel.xOriginal * Constants.PIXEL_SIZE;
            pixel.yOriginal = pixel.yOriginal * Constants.PIXEL_SIZE;

            pixel.xDisp = pixel.xOriginal;
            pixel.yDisp = pixel.yOriginal;

            for(int n = 0; n < 4; n++)
                if(pixel.neighbors[n] == null)
                    pixel.outside = true;
        }

        int halfSquareLength;
        if(bmHeight > bmWidth)
            halfSquareLength = bmHeight / 2;
        else
            halfSquareLength = bmWidth / 2;

        int tempItr = 0;
        for(int r = 0; r < groupMap.length; r++)
            for(int c = 0; c < groupMap[0].length; c++)
            {
                collidableGroups[tempItr] = groupMap[r][c];
                tempItr++;
            }

        pixelGroup = new PixelGroup(p,(float)halfSquareLength * Constants.PIXEL_SIZE, collidableGroups, textureID, sL);
        return pixelGroup;
    }*/
}
