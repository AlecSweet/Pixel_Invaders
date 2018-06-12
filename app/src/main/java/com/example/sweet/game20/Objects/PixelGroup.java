package com.example.sweet.game20.Objects;

import com.example.sweet.game20.util.Constants;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by Sweet on 2/14/2018.
 */

public class PixelGroup extends Collidable
{
    private static final int
            POSITION_COMPONENT_COUNT = 3,
            COLOR_COMPONENT_COUNT = 4;

    private int
            aPositionLocation,
            aColorLocation,
            uTextureLocation,
            uAngleLocation,
            uSquareLengthLocation,
            xDispLoc,
            yDispLoc;

    private static final String
            A_COLOR = "a_Color",
            A_POSITION = "a_Position",
            U_TEXTURE = "u_Texture",
            U_ANGLE = "angle",
            U_SQUARELENGTH = "squareLength",
            X_DISP = "x_displacement",
            Y_DISP = "y_displacement";

    private float[] colorVA;

    private FloatBuffer cbuf;

    private int[]
            vBuffer = new int[1],
            cBuffer = new int[1],
            textureID = new int[1];

    private Pixel[][] pMap;

    private int shaderLocation;

    private boolean glInit = false;

    public boolean onScreen = false;

    public PixelGroup(Pixel[] p, float halfSquareLength, Zone[] z,int  tID, int sL, int vB)
    {
        super(0 , 0, halfSquareLength, p,true, z);
        textureID[0] = tID;
        shaderLocation = sL;
        vBuffer[0] = vB;
    }

    public void draw()
    {
        if(!glInit)
        {
            initGlCalls();
        }

        glUniform1f(xDispLoc, centerX);
        glUniform1f(yDispLoc, centerY);

        if(needsUpdate)
        {
            updatePixels();
            needsUpdate = false;
        }

        if(numLivePixels < totalPixels * livablePercentage)
            collidableLive = false;

        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureID[0]);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(uTextureLocation, 0);
        glUniform1f(uAngleLocation, (float)angle);
        glUniform1f(uSquareLengthLocation, halfSquareLength);

        glBindBuffer(GL_ARRAY_BUFFER, vBuffer[0]);
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, cBuffer[0]);
        glEnableVertexAttribArray(aColorLocation);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_POINTS, 0, pixels.length);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void initBuffers()
    {
        colorVA = new float[pixels.length * 4];

        //Fill the VA's with data from the pixel array;
        int cIter = 0;
        for(Pixel tP: pixels)
        {
            colorVA[cIter] = tP.r;
            colorVA[cIter+1] = tP.g;
            colorVA[cIter+2] = tP.b;
            colorVA[cIter+3] = tP.a;
            cIter+=4;
        }

        cbuf = ByteBuffer
                .allocateDirect(colorVA.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(colorVA);
        cbuf.position(0);


        glGenBuffers(1, cBuffer, 0);

        glBindBuffer(GL_ARRAY_BUFFER, cBuffer[0]);
        glBufferData(GL_ARRAY_BUFFER, cbuf.capacity() * Constants.BYTES_PER_FLOAT, cbuf, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /*Update the color buffer for the group of pixels so that live pixels have an alpha of 1
    and not live pixels have an alpha of 0
     */
    public void updatePixels()
    {
        int tSize = pixels.length;
        for(int i = 0; i < tSize; i++)
        {
            if(pixels[i].live)
            {
                cbuf.put((i + 1) * 4 - 4, pixels[i].r);
                cbuf.put((i + 1) * 4 - 3, pixels[i].g);
                cbuf.put((i + 1) * 4 - 2, pixels[i].b);
                cbuf.put((i + 1) * 4 - 1, pixels[i].a);
            }
            else
                cbuf.put((i + 1) * 4 - 1,0f);
            if(pixels[i].outside)
                cbuf.put((i + 1) * 4 - 4, pixels[i].r + (float)(Math.random()* .5 - .25));
            /*if(onScreen && pixels[i].outside)
            {
                cbuf.put((i + 1) * 4 - 4, 1f);
                cbuf.put((i + 1) * 4 - 3, 1f);
                cbuf.put((i + 1) * 4 - 2, 1f);
            }
            else if(pixels[i].outside)
            {
                cbuf.put((i + 1) * 4 - 4, pixels[i].r);
                cbuf.put((i + 1) * 4 - 3, pixels[i].g);
                cbuf.put((i + 1) * 4 - 2, pixels[i].b);
            }*/

        }

        glBindBuffer(GL_ARRAY_BUFFER, cBuffer[0]);
        glBufferSubData(GL_ARRAY_BUFFER,0,cbuf.capacity()*Constants.BYTES_PER_FLOAT,cbuf);
    }

    public void initGlCalls()
    {
        aPositionLocation = glGetAttribLocation(shaderLocation, A_POSITION);
        aColorLocation = glGetAttribLocation(shaderLocation, A_COLOR);
        uTextureLocation = glGetUniformLocation(shaderLocation, U_TEXTURE);
        uAngleLocation = glGetUniformLocation(shaderLocation,U_ANGLE);
        uSquareLengthLocation = glGetUniformLocation(shaderLocation,U_SQUARELENGTH);
        xDispLoc = glGetUniformLocation(shaderLocation,X_DISP);
        yDispLoc = glGetUniformLocation(shaderLocation,Y_DISP);

        initBuffers();
        glInit = true;
    }

    public Pixel[] getPixels()
    {
        return pixels;
    }

    public Pixel[][] getpMap()
    {
        return pMap;
    }

    public void setPixels(Pixel[] p)
    {
        pixels = p;
    }

    public void setNeedsUpdate (boolean b)
    {
        needsUpdate = b;
    }

    public void setEnableOrphanChunkDeletion(boolean b)
    {
        enableOrphanChunkDeletion = b;
    }

    public void setpMap(Pixel[][] pM)
    {
        pMap = pM;
    }

    public void freeMemory()
    {
        glDeleteBuffers(1, cBuffer, 0);
    }

    public void resetPixels()
    {
        for(Pixel p: pixels)
        {
            p.live = true;
            p.outside = false;
            p.groupFlag = -1;
            for(Pixel n: p.neighbors)
                if(n == null)
                    p.outside = true;
        }
        collidableLive = true;
        needsUpdate = true;
        numLivePixels = totalPixels;
    }
    @Override
    public PixelGroup clone()
    {
        ArrayList<Pixel> p1 = new ArrayList<>();
        Pixel[][] cloneMap = new Pixel[pMap.length][pMap[0].length];
        int numGroupsWidth = (int)((float)pMap[0].length/Constants.CELL_LENGTH) + 1;
        int numGroupsHeight = (int)((float)pMap.length/Constants.CELL_LENGTH) + 1;
        int numZonesWidth = (int)((float)(numGroupsWidth) / Constants.ZONE_LENGTH) + 1;
        int numZonesHeight = (int)((float)(numGroupsHeight) / Constants.ZONE_LENGTH) + 1;

        CollidableGroup[] cGroups = new CollidableGroup[numGroupsWidth * numGroupsHeight];
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


        for(int r = 0; r < pMap.length; r++)
            for(int c = 0; c < pMap[0].length; c++)
            {
                if(pMap[r][c]!=null)
                {
                    cloneMap[r][c] = pMap[r][c].clone();
                    p1.add(cloneMap[r][c]);
                    groupMap[(int)((float)r / Constants.CELL_LENGTH)][(int)((float)c / Constants.CELL_LENGTH)].p.add(cloneMap[r][c]);
                }
            }

        for(int r = 0; r < cloneMap.length; r++)
            for(int c = 0; c < cloneMap[0].length; c++)
                if(cloneMap[r][c] != null)
                {
                    if(r - 1 >= 0)
                        cloneMap[r][c].neighbors[0] = cloneMap[r - 1][c]; //Up
                    if(r + 1 < cloneMap.length)
                        cloneMap[r][c].neighbors[2] = cloneMap[r + 1][c]; //Down
                    if(c - 1 >= 0)
                        cloneMap[r][c].neighbors[3] = cloneMap[r][c - 1]; //Left
                    if(c + 1 < cloneMap[0].length)
                        cloneMap[r][c].neighbors[1] = cloneMap[r][c + 1]; //Right
                }

        Pixel[] pArr = p1.toArray(new Pixel[p1.size()]);
        for(int i = 0; i < pixels.length; i++)
        {
            for(int n = 0; n < 4; n++)
                if(pArr[i].neighbors[n] == null)
                    pArr[i].outside = true;
        }

        int halfSquareLength;
        if(pMap.length > pMap[0].length)
            halfSquareLength = pMap.length / 2;
        else
            halfSquareLength = pMap[0].length / 2;

        int tempItr = 0;
        for(int r = 0; r < groupMap.length; r++)
            for(int c = 0; c < groupMap[0].length; c++)
            {
                cGroups[tempItr] = groupMap[r][c];
                cGroups[tempItr].xOriginal -= Constants.PIXEL_SIZE;
                cGroups[tempItr].yOriginal -= Constants.PIXEL_SIZE;
                cGroups[tempItr].move(-Constants.PIXEL_SIZE,-Constants.PIXEL_SIZE);
                cGroups[tempItr].halfSquareLength+= Constants.PIXEL_SIZE;
                cGroups[tempItr].initPixelArray();
                tempItr++;
            }

        tempItr = 0;
        Zone[] tempZones = new Zone[zoneMap.length * zoneMap[0].length];
        for(int r = 0; r < zoneMap.length; r++)
            for(int c = 0; c < zoneMap[0].length; c++)
            {
                tempZones[tempItr] = zoneMap[r][c];
                tempZones[tempItr].xOriginal -= Constants.PIXEL_SIZE;
                tempZones[tempItr].yOriginal -= Constants.PIXEL_SIZE;
                tempZones[tempItr].move(-Constants.PIXEL_SIZE,-Constants.PIXEL_SIZE);
                tempZones[tempItr].halfSquareLength+= Constants.PIXEL_SIZE;
                tempZones[tempItr].initCollidableGroupArray();
                tempItr++;
            }

        PixelGroup pixelGroup = new PixelGroup(pArr,(float)halfSquareLength * Constants.PIXEL_SIZE, tempZones, textureID[0], shaderLocation, vBuffer[0]);
        pixelGroup.setpMap(cloneMap);
        return pixelGroup;
    }
}
