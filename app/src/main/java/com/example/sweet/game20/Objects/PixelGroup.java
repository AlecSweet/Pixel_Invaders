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

public class  PixelGroup extends Collidable
{
    private static final int
            POSITION_COMPONENT_COUNT = 3,
            COLOR_COMPONENT_COUNT = 4;

    private int
            aPositionLocation,
            aColorLocation,
            uAngleLocation,
            uSquareLengthLocation,
            xDispLoc,
            yDispLoc,
            uTiltAngleLocation,
            uMagLoc,
            pointSizeLoc;

    private FloatBuffer cbuf;

    private int[]
            vBuffer = new int[1],
            cBuffer = new int[1];

    private Pixel[][] pMap;

    private int shaderLocation;

    private boolean glInit = false;

    private float
            edgeColorR,
            edgeColorG,
            edgeColorB;

    private boolean whiteToColor = false;

    public PixelGroup(Pixel[] p, float halfSquareLength, Zone[] z, int sL, int vB)
    {
        super(0 , 0, halfSquareLength, p,true, z);
        shaderLocation = sL;
        vBuffer[0] = vB;
    }

    public void draw()
    {
        if(!glInit)
        {
            initGlCalls();
        }

        if(needsUpdate)
        {
            updatePixels();
            needsUpdate = false;
        }

        if(numLivePixels < totalPixels * livablePercentage)
        {
            collidableLive = false;
        }
        if(enableLocationChain)
        {
            /*if(locationDrawTail.nextLocation != null && locationDrawTail.nextLocation.readyToBeConsumed)
            {
                locationDrawTail = locationDrawTail.nextLocation;
            }*/
            while(locationDrawTail.nextLocation != null && locationDrawTail.nextLocation.readyToBeConsumed)
            {
                locationDrawTail = locationDrawTail.nextLocation;
            }
            glUniform1f(xDispLoc, locationDrawTail.x);
            glUniform1f(yDispLoc, locationDrawTail.y);
        }
        else
        {
            glUniform1f(xDispLoc, centerX);
            glUniform1f(yDispLoc, centerY);
        }

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

    public void softDraw(float x, float y, float ang, float tiltAng)
    {
        if(!glInit)
        {
            initGlCalls();
        }

        if(whiteToColor)
        {
            setWhiteToColor();
            whiteToColor = false;
        }

        glUniform1f(xDispLoc, x);
        glUniform1f(yDispLoc, y);
        glUniform1f(uAngleLocation, ang);
        glUniform1f(uSquareLengthLocation, halfSquareLength);
        glUniform1f(uTiltAngleLocation, tiltAng);

        glBindBuffer(GL_ARRAY_BUFFER, vBuffer[0]);
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, cBuffer[0]);
        glEnableVertexAttribArray(aColorLocation);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_POINTS, 0, pixels.length);

        glUniform1f(uTiltAngleLocation, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void softDraw(float x, float y, float ang, float tiltAng, float mag, float pointSize)
    {
        if(!glInit)
        {
            initGlCalls();
        }

        if(whiteToColor)
        {
            setWhiteToColor();
            whiteToColor = false;
        }

        glUniform1f(xDispLoc, x);
        glUniform1f(yDispLoc, y);
        glUniform1f(uAngleLocation, ang);
        glUniform1f(uSquareLengthLocation, halfSquareLength*mag);
        glUniform1f(uTiltAngleLocation, tiltAng);
        glUniform1f(uMagLoc, mag);
        glUniform1f(pointSizeLoc, pointSize);

        glBindBuffer(GL_ARRAY_BUFFER, vBuffer[0]);
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, cBuffer[0]);
        glEnableVertexAttribArray(aColorLocation);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_POINTS, 0, pixels.length);

        glUniform1f(uTiltAngleLocation, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void initBuffers()
    {
        /*colorVA = new float[pixels.length * 4];

        //Fill the VA's with data from the pixel array;
        int cIter = 0;
        for(Pixel tP: pixels)
        {
            colorVA[cIter] = tP.r;
            colorVA[cIter+1] = tP.g;
            colorVA[cIter+2] = tP.b;
            colorVA[cIter+3] = tP.a;
            cIter+=4;
        }*/

        cbuf = ByteBuffer
                .allocateDirect(pixels.length * 4 * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        cbuf.position(0);

        glGenBuffers(1, cBuffer, 0);

        glBindBuffer(GL_ARRAY_BUFFER, cBuffer[0]);
        glBufferData(GL_ARRAY_BUFFER, cbuf.capacity() * Constants.BYTES_PER_FLOAT, cbuf, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        int tSize = pixels.length;
        for (int i = 0; i < tSize; i++)
        {
            if (pixels[i].live)
            {
                cbuf.put((i + 1) * 4 - 4, pixels[i].r);
                cbuf.put((i + 1) * 4 - 3, pixels[i].g);
                cbuf.put((i + 1) * 4 - 2, pixels[i].b);
                cbuf.put((i + 1) * 4 - 1, pixels[i].a);
            }
            else if(pixels[i].a != 0 && !pixels[i].live)
            {
                pixels[i].a = 0;
                cbuf.put((i + 1) * 4 - 1, 0f);
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, cBuffer[0]);
        glBufferSubData(GL_ARRAY_BUFFER,0,cbuf.capacity()*Constants.BYTES_PER_FLOAT,cbuf);
    }

    /*Update the color buffer for the group of pixels so that live pixels have an alpha of 1
    and not live pixels have an alpha of 0
     */
    public void updatePixels()
    {
        int tSize = pixels.length;
        if(restorable)
        {
            for (int i = 0; i < tSize; i++)
            {
                if (pixels[i].live && !pixels[i].insideEdge)
                {
                    cbuf.put((i + 1) * 4 - 4, pixels[i].r);
                    cbuf.put((i + 1) * 4 - 3, pixels[i].g);
                    cbuf.put((i + 1) * 4 - 2, pixels[i].b);
                    cbuf.put((i + 1) * 4 - 1, pixels[i].a);
                }
                else if(pixels[i].insideEdge)
                {
                    cbuf.put((i + 1) * 4 - 4, edgeColorR);
                    cbuf.put((i + 1) * 4 - 3, edgeColorG);
                    cbuf.put((i + 1) * 4 - 2, edgeColorB);
                }
                else
                {
                    if (pixels[i].a != 0 && !pixels[i].live)
                    {
                        pixels[i].a = 0;
                        cbuf.put((i + 1) * 4 - 1, 0f);
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < tSize; i++)
            {
                if(pixels[i].a != 0 && !pixels[i].live)
                {
                    pixels[i].a = 0;
                    cbuf.put((i + 1) * 4 - 1, 0f);
                }
                else if(pixels[i].insideEdge)
                {
                    cbuf.put((i + 1) * 4 - 4, edgeColorR);
                    cbuf.put((i + 1) * 4 - 3, edgeColorG);
                    cbuf.put((i + 1) * 4 - 2, edgeColorB);
                    pixels[i].insideEdge = false;
                }
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, cBuffer[0]);
        glBufferSubData(GL_ARRAY_BUFFER,0,cbuf.capacity()*Constants.BYTES_PER_FLOAT,cbuf);
    }

    public void setWhiteToColor(float r, float g, float b)
    {
        this.edgeColorR = r;
        this.edgeColorG = g;
        this.edgeColorB = b;
        whiteToColor = true;
    }

    public void setEdgeColor(float r, float g, float b)
    {
        this.edgeColorR = r;
        this.edgeColorG = g;
        this.edgeColorB = b;
    }

    public void setWhiteToColor()
    {
        int tSize = pixels.length;
        for(int i = 0; i < tSize; i++)
        {
            if( pixels[i].r == 1 && pixels[i].g == 1 && pixels[i].b == 1)
            {
                cbuf.put((i + 1) * 4 - 4, edgeColorR);
                cbuf.put((i + 1) * 4 - 3, edgeColorG);
                cbuf.put((i + 1) * 4 - 2, edgeColorB);
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, cBuffer[0]);
        glBufferSubData(GL_ARRAY_BUFFER,0,cbuf.capacity()*Constants.BYTES_PER_FLOAT,cbuf);
    }

    public void initGlCalls()
    {
        aPositionLocation = glGetAttribLocation(shaderLocation, "a_Position");
        aColorLocation = glGetAttribLocation(shaderLocation, "a_Color");
        uAngleLocation = glGetUniformLocation(shaderLocation,"angle");
        uSquareLengthLocation = glGetUniformLocation(shaderLocation,"squareLength");
        xDispLoc = glGetUniformLocation(shaderLocation,"x_displacement");
        yDispLoc = glGetUniformLocation(shaderLocation,"y_displacement");
        uTiltAngleLocation = glGetUniformLocation(shaderLocation,"tilt");
        uMagLoc = glGetUniformLocation(shaderLocation,"mag");
        pointSizeLoc = glGetUniformLocation(shaderLocation,"pointSize");

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
        pMap = null;
        cbuf = null;
        pixels = null;
        zones = null;
        lastPixelKilled = null;
    }

    @Override
    public PixelGroup clone()
    {
        ArrayList<Pixel> p1 = new ArrayList<>();
        Pixel[][] cloneMap = new Pixel[pMap.length][pMap[0].length];
        int numGroupsWidth = pMap[0].length/Constants.CELL_LENGTH + 1;
        int numGroupsHeight = pMap.length/Constants.CELL_LENGTH + 1;
        int numZonesWidth = numGroupsWidth / Constants.ZONE_LENGTH + 1;
        int numZonesHeight = numGroupsHeight / Constants.ZONE_LENGTH + 1;

        CollidableGroup[] cGroups = new CollidableGroup[numGroupsWidth * numGroupsHeight];
        Zone[][] zoneMap = new Zone[numZonesHeight][numZonesWidth];
        CollidableGroup[][] groupMap = new CollidableGroup[numGroupsHeight][numGroupsWidth];

        for(int r = 0; r < zoneMap.length; r++)
        {
            for (int c = 0; c < zoneMap[0].length; c++)
            {
                zoneMap[r][c] = new Zone(c * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.ZONE_SIZE / 2);
            }
        }
        for(int r = 0; r < groupMap.length; r++)
        {
            for (int c = 0; c < groupMap[0].length; c++)
            {
                groupMap[r][c] = new CollidableGroup(c * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.CELL_SIZE / 2
                );
                //zoneMap[(int)((float) r / Constants.ZONE_LENGTH)][(int)((float) c / Constants.ZONE_LENGTH)].c.add(groupMap[r][c]);
                zoneMap[r / Constants.ZONE_LENGTH][c / Constants.ZONE_LENGTH].c.add(groupMap[r][c]);
            }
        }

        for(int r = 0; r < pMap.length; r++)
        {
            for (int c = 0; c < pMap[0].length; c++)
            {
                if (pMap[r][c] != null)
                {
                    cloneMap[r][c] = pMap[r][c].clone();
                    p1.add(cloneMap[r][c]);
                    groupMap[r / Constants.CELL_LENGTH][c / Constants.CELL_LENGTH].p.add(cloneMap[r][c]);
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
        Pixel[] pArr = p1.toArray(new Pixel[p1.size()]);
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

        int halfSquareLength;
        if(pMap.length > pMap[0].length)
        {
            halfSquareLength = pMap.length / 2;
        }
        else
        {
            halfSquareLength = pMap[0].length / 2;
        }

        int tempItr = 0;
        for(int r = 0; r < groupMap.length; r++)
        {
            for (int c = 0; c < groupMap[0].length; c++)
            {
                cGroups[tempItr] = groupMap[r][c];
                cGroups[tempItr].xOriginal -= Constants.PIXEL_SIZE;
                cGroups[tempItr].yOriginal -= Constants.PIXEL_SIZE;
                cGroups[tempItr].move(-Constants.PIXEL_SIZE, -Constants.PIXEL_SIZE);
                cGroups[tempItr].halfSquareLength += Constants.PIXEL_SIZE;
                cGroups[tempItr].initPixelArray();
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
                tempZones[tempItr].xOriginal -= Constants.PIXEL_SIZE;
                tempZones[tempItr].yOriginal -= Constants.PIXEL_SIZE;
                tempZones[tempItr].move(-Constants.PIXEL_SIZE, -Constants.PIXEL_SIZE);
                tempZones[tempItr].halfSquareLength += Constants.PIXEL_SIZE;
                tempZones[tempItr].initCollidableGroupArray();
                tempItr++;
            }
        }

        PixelGroup pixelGroup = new PixelGroup(
                pArr,
                (float)halfSquareLength * Constants.PIXEL_SIZE,
                tempZones,
                shaderLocation,
                vBuffer[0]
        );
        pixelGroup.setpMap(cloneMap);
        return pixelGroup;
    }
}
