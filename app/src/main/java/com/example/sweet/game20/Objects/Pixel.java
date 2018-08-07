package com.example.sweet.game20.Objects;


/**
 * Created by Sweet on 1/29/2018.
 */

public class Pixel
{

    public float
            xDisp = 0f,
            yDisp = 0f;

    public int
            row,
            col,
            groupFlag = -1;

    public volatile int state = 1;

    /*public boolean
            live = true,
            outside = false,
            insideEdge = false;*/

    public Pixel( int r, int c)
    {
        row = r;
        col = c;
    }

    public void killPixel(Pixel[][] pMap)
    {
        /*live = false;
        if (pMap[row + 1][col] != null && !pMap[row + 1][col].outside)
        {
            pMap[row + 1][col].insideEdge = true;
            pMap[row + 1][col].outside = true;
        }
        
        if (pMap[row - 1][col] != null && !pMap[row - 1][col].outside)
        {
            pMap[row - 1][col].insideEdge = true;
            pMap[row - 1][col].outside = true;
        }
        
        if (pMap[row][col + 1] != null && !pMap[row][col + 1].outside)
        {
            pMap[row][col + 1].insideEdge = true;
            pMap[row][col + 1].outside = true;
        }
        
        if (pMap[row][col - 1] != null && !pMap[row][col - 1].outside)
        {
            pMap[row][col - 1].insideEdge = true;
            pMap[row][col - 1].outside = true;
        }*/
        state = 0;
        if (pMap[row + 1][col] != null && pMap[row + 1][col].state == 1)
        {
            pMap[row + 1][col].state = 3;
        }

        if (pMap[row - 1][col] != null && pMap[row - 1][col].state == 1)
        {
            pMap[row - 1][col].state = 3;
        }

        if (pMap[row][col + 1] != null && pMap[row][col + 1].state == 1)
        {
            pMap[row][col + 1].state = 3;
        }

        if (pMap[row][col - 1] != null && pMap[row][col - 1].state == 1)
        {
            pMap[row][col - 1].state = 3;
        }
    }

    @Override
    public Pixel clone()
    {
        return new Pixel(row, col);
    }
}
