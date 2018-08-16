package com.example.sweet.game20.Objects;

import android.graphics.PointF;

import com.example.sweet.game20.util.Constants;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_STREAM_DRAW;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
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
import static android.opengl.GLES20.glUniform2f;
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
            pointSizeLoc,
            uCenterMassXLoc,
            uCenterMassYLoc;

    private FloatBuffer cbuf;

    private int[]
            vBuffer = new int[1],
            cBuffer = new int[1];

    private int shaderLocation;

    private boolean glInit = false;

    private float
            edgeColorR,
            edgeColorG,
            edgeColorB;

    private boolean whiteToColor = false;

    public PixelGroup(Pixel[] p, float halfSquareLength, Zone[] z, CollidableGroup[] g, int sL, int vB, PixelInfo[][] iM)
    {
        super(0 , 0, halfSquareLength, p,true, z, g, iM);
        shaderLocation = sL;
        vBuffer[0] = vB;
    }

    public PixelGroup(Pixel[] p, float halfSquareLength, Zone[] z, int sL, int vB, PixelInfo[][] iM, Pixel[][] pM)
    {
        super(0 , 0, halfSquareLength, p,true, z, iM, pM);
        shaderLocation = sL;
        vBuffer[0] = vB;
    }

    public PixelGroup(Pixel[] p, float halfSquareLength, Zone[] z, int sL, int vB, PixelInfo[][] iM)
    {
        super(0 , 0, halfSquareLength, p,true, z, iM);
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
            while(locationDrawTail.nextLocation != null && locationDrawTail.nextLocation.readyToBeConsumed)
            {
                locationDrawTail = locationDrawTail.nextLocation;
            }
            /*if(locationDrawTail.nextLocation != null && locationDrawTail.nextLocation.readyToBeConsumed)
            {
                locationDrawTail.collisionConsumed = true;
                if(locationDrawTail.uiConsumed)
                {
                    locationPoolHead.nextLocation = locationDrawTail;
                    locationPoolHead = locationHead.nextLocation;
                    locationDrawTail = locationDrawTail.nextLocation;
                    locationPoolHead.reset();
                }
                else
                {
                    locationDrawTail = locationDrawTail.nextLocation;
                }
            }*/
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
        glUniform1f(uCenterMassXLoc, centerMassX);
        glUniform1f(uCenterMassYLoc, centerMassY);

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

        cbuf = FloatBuffer.allocate(pixels.length * 4);
        cbuf.position(0);

        glGenBuffers(1, cBuffer, 0);

        glBindBuffer(GL_ARRAY_BUFFER, cBuffer[0]);
        glBufferData(GL_ARRAY_BUFFER, cbuf.capacity() * Constants.BYTES_PER_FLOAT, cbuf, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        int tSize = pixels.length;
        /*for (int i = 0; i < tSize; i++)
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
        }*/
        for (int i = 0; i < tSize; i++)
        {
            if (pixels[i].state >= 1)
            {
                cbuf.put((i + 1) * 4 - 4, infoMap[pixels[i].row][pixels[i].col].r);
                cbuf.put((i + 1) * 4 - 3, infoMap[pixels[i].row][pixels[i].col].g);
                cbuf.put((i + 1) * 4 - 2, infoMap[pixels[i].row][pixels[i].col].b);
                cbuf.put((i + 1) * 4 - 1, infoMap[pixels[i].row][pixels[i].col].a);
            }
            //else if(infoMap[pixels[i].row][pixels[i].col].a != 0 && !pixels[i].live)
            else if(infoMap[pixels[i].row][pixels[i].col].a != 0 && pixels[i].state == 0)
            {
                //infoMap[pixels[i].row][pixels[i].col].a = 0;
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
       /* if(restorable)
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
                    cbuf.put((i + 1) * 4 - 1, pixels[i].a);
                }
                else if (!pixels[i].live)
                {
                    cbuf.put((i + 1) * 4 - 1, 0f);
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
        }*/

        /*if(restorable)
        {
            for (int i = 0; i < tSize; i++)
            {
                //if (pixels[i].live && !pixels[i].insideEdge)
                if(pixels[i].state == 1 || pixels[i].state == 2)
                {
                    cbuf.put((i + 1) * 4 - 4, infoMap[pixels[i].row][pixels[i].col].r);
                    cbuf.put((i + 1) * 4 - 3, infoMap[pixels[i].row][pixels[i].col].g);
                    cbuf.put((i + 1) * 4 - 2, infoMap[pixels[i].row][pixels[i].col].b);
                    cbuf.put((i + 1) * 4 - 1, infoMap[pixels[i].row][pixels[i].col].a);
                }
                //else if(pixels[i].insideEdge)
                else if(pixels[i].state == 3)
                {
                    cbuf.put((i + 1) * 4 - 4, edgeColorR);
                    cbuf.put((i + 1) * 4 - 3, edgeColorG);
                    cbuf.put((i + 1) * 4 - 2, edgeColorB);
                    cbuf.put((i + 1) * 4 - 1, infoMap[pixels[i].row][pixels[i].col].a);
                }
                //else if (!pixels[i].live)
                else if (pixels[i].state == 0)
                {
                    cbuf.put((i + 1) * 4 - 1, 0f);
                }
            }
        }
        else
        {
            for (int i = 0; i < tSize; i++)
            {
                //if(infoMap[pixels[i].row][pixels[i].col].a != 0 && !pixels[i].live)
                if(infoMap[pixels[i].row][pixels[i].col].a != 0 && pixels[i].state == 0)
                {
                    cbuf.put((i + 1) * 4 - 1, 0f);
                }
                //else if(pixels[i].insideEdge)
                else if(pixels[i].state == 3)
                {
                    cbuf.put((i + 1) * 4 - 4, edgeColorR);
                    cbuf.put((i + 1) * 4 - 3, edgeColorG);
                    cbuf.put((i + 1) * 4 - 2, edgeColorB);
                    pixels[i].state = 2;
                }
            }
        }*/
        /*for (int i = 0; i < tSize; i++)
        {
            //if (pixels[i].live && !pixels[i].insideEdge)
            if(pixels[i].state == 1 || pixels[i].state == 2)
            {
                cbuf.put((i + 1) * 4 - 4, infoMap[pixels[i].row][pixels[i].col].r);
                cbuf.put((i + 1) * 4 - 3, infoMap[pixels[i].row][pixels[i].col].g);
                cbuf.put((i + 1) * 4 - 2, infoMap[pixels[i].row][pixels[i].col].b);
                cbuf.put((i + 1) * 4 - 1, infoMap[pixels[i].row][pixels[i].col].a);
            }
            //else if(pixels[i].insideEdge)
            else if(pixels[i].state == 3)
            {
                cbuf.put((i + 1) * 4 - 4, edgeColorR);
                cbuf.put((i + 1) * 4 - 3, edgeColorG);
                cbuf.put((i + 1) * 4 - 2, edgeColorB);
                cbuf.put((i + 1) * 4 - 1, infoMap[pixels[i].row][pixels[i].col].a);
            }
            //else if (!pixels[i].live)
            else if (pixels[i].state == 0)
            {
                cbuf.put((i + 1) * 4 - 1, 0f);
            }
        }*/
        for (int i = 0; i < tSize; i++)
        {
            //if (pixels[i].live && !pixels[i].insideEdge)
            if(pixels[i].state == 1)
            {
                cbuf.put((i + 1) * 4 - 4, infoMap[pixels[i].row][pixels[i].col].r);
                cbuf.put((i + 1) * 4 - 3, infoMap[pixels[i].row][pixels[i].col].g);
                cbuf.put((i + 1) * 4 - 2, infoMap[pixels[i].row][pixels[i].col].b);
                cbuf.put((i + 1) * 4 - 1, infoMap[pixels[i].row][pixels[i].col].a);
            }
            else if(pixels[i].state >= 2 )
            {
                cbuf.put((i + 1) * 4 - 4, 1f);
                cbuf.put((i + 1) * 4 - 3, 1f);
                cbuf.put((i + 1) * 4 - 2, 1f);
                cbuf.put((i + 1) * 4 - 1, 1f);
            }
            //else if(pixels[i].insideEdge)
            else if(pixels[i].state == 3)
            {
                cbuf.put((i + 1) * 4 - 4, edgeColorR);
                cbuf.put((i + 1) * 4 - 3, edgeColorG);
                cbuf.put((i + 1) * 4 - 2, edgeColorB);
                cbuf.put((i + 1) * 4 - 1, infoMap[pixels[i].row][pixels[i].col].a);
            }
            //else if (!pixels[i].live)
            else if (pixels[i].state == 0)
            {
                cbuf.put((i + 1) * 4 - 1, 0f);
            }
        }
        glBindBuffer(GL_ARRAY_BUFFER, cBuffer[0]);
        glBufferSubData(GL_ARRAY_BUFFER,0,cbuf.capacity()*Constants.BYTES_PER_FLOAT, cbuf);
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
        /*for(int i = 0; i < tSize; i++)
        {
            if( pixels[i].r == 1 && pixels[i].g == 1 && pixels[i].b == 1)
            {
                cbuf.put((i + 1) * 4 - 4, edgeColorR);
                cbuf.put((i + 1) * 4 - 3, edgeColorG);
                cbuf.put((i + 1) * 4 - 2, edgeColorB);
            }
        }*/
        for(int i = 0; i < tSize; i++)
        {
            if( infoMap[pixels[i].row][pixels[i].col].r == 1 && 
                    infoMap[pixels[i].row][pixels[i].col].g == 1 && 
                    infoMap[pixels[i].row][pixels[i].col].b == 1)
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
        uCenterMassXLoc = glGetUniformLocation(shaderLocation, "centerMassX");
        uCenterMassYLoc = glGetUniformLocation(shaderLocation, "centerMassY");

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

    public PixelGroup clone()
    {
        Pixel[] pArr = new Pixel[pixels.length];
        Pixel[][] cloneMap = new Pixel[pMap.length][pMap[0].length];

        int halfSquareLength;
        if(pMap.length > pMap[0].length)
        {
            halfSquareLength = (pMap.length - 2) / 2;
        }
        else
        {
            halfSquareLength = (pMap[0].length - 2) / 2;
        }

        int numGroupsWidth = (pMap[0].length - 2)/Constants.CELL_LENGTH + 1;
        int numGroupsHeight = (pMap.length - 2)/Constants.CELL_LENGTH + 1;
        CollidableGroup[]  tempGroups = new CollidableGroup[numGroupsWidth * numGroupsHeight];

        int numZonesWidth = numGroupsWidth / Constants.ZONE_LENGTH + 1;
        int numZonesHeight = numGroupsHeight / Constants.ZONE_LENGTH + 1;
        Zone[] tempZones = new Zone[numZonesHeight * numZonesWidth];

        for(int r = 0; r < numZonesHeight; r++)
        {
            for (int c = 0; c < numZonesWidth; c++)
            {
                int tempZ = c + numZonesWidth * r;
                /*tempZones[tempZ] = new Zone(c * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * numZonesHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.ZONE_SIZE / 2);*/
                tempZones[tempZ] = new Zone(c * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * (float)numZonesWidth / 2f,
                        r * Constants.ZONE_SIZE + Constants.ZONE_SIZE / 2 - Constants.ZONE_SIZE * (float)numZonesHeight / 2f,
                        Constants.ZONE_SIZE / 2);
                tempZones[tempZ].collidableGroups = new CollidableGroup[zones[tempZ].collidableGroups.length];
            }
        }

        for(int r = 0; r < numGroupsHeight; r++)
        {
            for (int c = 0; c < numGroupsWidth; c++)
            {
                int tempG = c + numGroupsWidth * r;
                int tempZ = (c / Constants.ZONE_LENGTH) + (r / Constants.ZONE_LENGTH) * numZonesWidth;
               /* tempGroups[tempG] = new CollidableGroup(c * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsWidth / 2 + Constants.PIXEL_SIZE,
                        r * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * numGroupsHeight / 2 + Constants.PIXEL_SIZE,
                        Constants.CELL_SIZE / 2
                );*/
                tempGroups[tempG] = new CollidableGroup(c * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * (float)numGroupsWidth / 2f,
                        r * Constants.CELL_SIZE + Constants.CELL_SIZE / 2 - Constants.CELL_SIZE * (float)numGroupsHeight / 2f,
                        Constants.CELL_SIZE / 2
                );
                tempGroups[tempG].pixels = new Pixel[totalGroups[tempG].pixels.length];

                tempZones[tempZ].collidableGroups[tempZones[tempZ].tempItr] = tempGroups[tempG];
                tempZones[tempZ].tempItr++;
            }
        }

        int itr = 0;
        for(int r = 1; r < pMap.length - 1; r++)
        {
            for (int c = 1; c < pMap[0].length - 1; c++)
            {
                if (pMap[r][c] != null)
                {
                    cloneMap[r][c] = pMap[r][c].clone();
                    //p1.add(cloneMap[r][c]);
                    pArr[itr] = cloneMap[r][c];
                    itr++;
                    int tempG = ((c - 1) / Constants.CELL_LENGTH) + numGroupsWidth * ((r - 1) / Constants.CELL_LENGTH);
                    //tempGroups[tempG].p.add(cloneMap[r][c]);
                    tempGroups[tempG].pixels[tempGroups[tempG].tempItr] = cloneMap[r][c];
                    tempGroups[tempG].tempItr++;
                }
            }
        }

        for(Pixel p: pArr)
        {
            if (cloneMap[p.row + 1][p.col] == null)
            {
                //p.outside = true;
                p.state = 2;
            }
            else if (cloneMap[p.row - 1][p.col] == null)
            {
                //p.outside = true;
                p.state = 2;
            }
            else if (cloneMap[p.row][p.col + 1] == null)
            {
               // p.outside = true;
                p.state = 2;
            }
            else if (cloneMap[p.row][p.col - 1] == null)
            {
                //p.outside = true;
                p.state = 2;
            }
        }

        for(CollidableGroup cG: tempGroups)
        {
            cG.xOriginal -= Constants.PIXEL_SIZE;
            cG.yOriginal -= Constants.PIXEL_SIZE;
            //cG.move(-Constants.PIXEL_SIZE, -Constants.PIXEL_SIZE);
            cG.halfSquareLength *= 2f;
        }

        for(Zone z: tempZones)
        {
            z.xOriginal -= Constants.PIXEL_SIZE;
            z.yOriginal -= Constants.PIXEL_SIZE;
            //z.move(-Constants.PIXEL_SIZE, -Constants.PIXEL_SIZE);
            z.halfSquareLength *= 2f;
        }

        for(Zone z: tempZones)
        {
            float fGLeft = 5;
            float fGRight = -5;
            float fGUp = -5;
            float fGDown = 5;
            boolean zhasone = false;
            for(CollidableGroup cG: z.collidableGroups)
            {
                if(cG.xOriginal - cG.halfSquareLength < fGLeft)
                    fGLeft = cG.xOriginal - cG.halfSquareLength;
                if(cG.xOriginal + cG.halfSquareLength > fGRight)
                    fGRight = cG.xOriginal + cG.halfSquareLength;
                if(cG.yOriginal - cG.halfSquareLength < fGDown)
                    fGDown = cG.xOriginal - cG.halfSquareLength;
                if(cG.yOriginal + cG.halfSquareLength > fGUp)
                    fGUp = cG.xOriginal + cG.halfSquareLength;

                float fPLeft = 5;
                float fPRight = -5;
                float fPUp = -5;
                float fPDown = 5;
                boolean ghasone = false;
                for(Pixel p: cG.pixels)
                {
                    if(infoMap[p.row][p.col].xOriginal - Constants.PIXEL_SIZE / 2 < fPLeft)
                        fPLeft = infoMap[p.row][p.col].xOriginal - Constants.PIXEL_SIZE / 2;
                    if(infoMap[p.row][p.col].xOriginal + Constants.PIXEL_SIZE / 2 > fPRight)
                        fPRight = infoMap[p.row][p.col].xOriginal + Constants.PIXEL_SIZE / 2;
                    if(infoMap[p.row][p.col].yOriginal - Constants.PIXEL_SIZE / 2 < fPDown)
                        fPDown = infoMap[p.row][p.col].xOriginal - Constants.PIXEL_SIZE / 2;
                    if(infoMap[p.row][p.col].yOriginal + Constants.PIXEL_SIZE / 2 > fPUp)
                        fPUp = infoMap[p.row][p.col].xOriginal + Constants.PIXEL_SIZE / 2;
                    ghasone = true;
                    zhasone = true;
                }
                if(ghasone)
                {
                    System.out.println("Collidable Group");
                    System.out.println(fPLeft + " > " + (cG.xOriginal - cG.halfSquareLength) + " And " + fPRight + " < " + (cG.xOriginal + cG.halfSquareLength));
                    if (fPLeft > (cG.xOriginal - cG.halfSquareLength) &&
                            fPRight < (cG.xOriginal + cG.halfSquareLength))
                    {
                        System.out.println("---------------------------------------------True Left/Right");
                    } else
                    {
                        System.out.println("---------------------------------------------False Left/Right");
                    }
                    System.out.println(fPDown + " > " + (cG.yOriginal - cG.halfSquareLength) + " And " + fPUp + " < " + (cG.yOriginal + cG.halfSquareLength));
                    if (
                            fPDown > (cG.yOriginal - cG.halfSquareLength) &&
                                    fPUp < (cG.yOriginal + cG.halfSquareLength))
                    {
                        System.out.println("---------------------------------------------True Up/Down");
                    } else
                    {
                        System.out.println("---------------------------------------------False Up/Down");
                    }
                }
            }
            if(zhasone)
            {
                System.out.println("Zone");
                System.out.println(fGLeft + " > " + (z.xOriginal - z.halfSquareLength) + " And " + fGRight + " < " + (z.xOriginal + z.halfSquareLength));
                if (fGLeft > (z.xOriginal - z.halfSquareLength) &&
                        fGRight < (z.xOriginal + z.halfSquareLength))
                {
                    System.out.println("---------------------------------------------True Left/Right");
                } else
                {
                    System.out.println("---------------------------------------------False Left/Right");
                }
                System.out.println(fGDown + " > " + (z.yOriginal - z.halfSquareLength) + " And " + fGUp + " < " + (z.yOriginal + z.halfSquareLength));
                if (
                        fGDown > (z.yOriginal - z.halfSquareLength) &&
                                fGUp < (z.yOriginal + z.halfSquareLength))
                {
                    System.out.println("---------------------------------------------True Up/Down");
                } else
                {
                    System.out.println("---------------------------------------------False Up/Down");
                }
            }
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------------------end");
        PixelGroup pixelGroup = new PixelGroup(
                pArr,
                (float)halfSquareLength * Constants.PIXEL_SIZE * 1.4f,
                tempZones,
                shaderLocation,
                vBuffer[0],
                infoMap
        );
        pixelGroup.setpMap(cloneMap);
        pixelGroup.restorable = restorable;
        return pixelGroup;
    }

    /*public PixelGroup clone()
    {
        Pixel[] pArr = new Pixel[pixels.length];
        Pixel[][] cloneMap = new Pixel[pMap.length][pMap[0].length];

        Zone[] tempZones = new Zone[zones.length];
        for(int i = 0; i < zones.length; i++)
        {
            tempZones[i] = zones[i].clone();
        }

        for(Zone z: tempZones)
        {
            for(CollidableGroup cG: z.collidableGroups)
            {
                for(Pixel p: cG.pixels)
                {
                    cloneMap[p.row][p.col] = p;
                    int temp = (p.col - 1) + (pMap[0].length - 2) * (p.row - 1);
                    pArr[temp] = p;
                }
            }
        }

        for(Pixel p: pArr)
        {
            if (cloneMap[p.row + 1][p.col] == null)
            {
                //p.outside = true;
                p.state = 2;
            }
            else if (cloneMap[p.row - 1][p.col] == null)
            {
                //p.outside = true;
                p.state = 2;
            }
            else if (cloneMap[p.row][p.col + 1] == null)
            {
                // p.outside = true;
                p.state = 2;
            }
            else if (cloneMap[p.row][p.col - 1] == null)
            {
                //p.outside = true;
                p.state = 2;
            }
        }
        PixelGroup te = new PixelGroup(
                pArr,
                (float)halfSquareLength * Constants.PIXEL_SIZE,
                tempZones,
                shaderLocation,
                vBuffer[0],
                infoMap,
                cloneMap
        );
        te.restorable = restorable;
        return te;
    }*/
}
