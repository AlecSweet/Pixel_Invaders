package com.example.sweet.game20.util;

import com.example.sweet.game20.Objects.Pixel;

import java.util.ArrayList;

/**
 * Created by Sweet on 1/29/2018.
 */

public class PixelShapes
{
    public static ArrayList<Pixel> makeFillPixelCircle( int cX, int cY, int radius, float size)
    {
        ArrayList<Pixel> p = new ArrayList<>();
        Pixel[][] map = new Pixel[radius * 2 +  2][radius * 2 + 2];
        int d = (5 - radius * 4) / 4;
        int x = 0;
        int y = radius;

        do
        {
            map[cX - x + radius + 1][cY + y + radius + 1] = new Pixel(cX - x, cY + y);
            p.add(map[cX - x + radius + 1][cY + y + radius + 1]);
            map[cX + x + radius + 1][cY + y + radius + 1] = new Pixel(cX + x, cY + y);
            p.add(map[cX + x + radius + 1][cY + y + radius + 1]);
            for(int i = cX - x + 1; i < cX + x; i++)
                if (map[i + radius + 1][cY + y + radius + 1]==null)
                {
                    map[i + radius + 1][cY + y + radius + 1] = new Pixel(i, cY + y);
                    p.add(map[i + radius + 1][cY + y + radius + 1]);
                }

            map[cX - x + radius + 1][cY - y + radius + 1] = new Pixel(cX - x, cY - y);
            p.add(map[cX - x + radius + 1][cY - y + radius + 1]);
            map[cX + x + radius + 1][cY - y + radius + 1] = new Pixel(cX + x, cY - y);
            p.add(map[cX + x + radius + 1][cY - y + radius + 1]);
            for(int i = cX - x + 1; i < cX + x; i++)
                if (map[i + radius + 1][cY - y + radius + 1]==null)
                {
                    map[i + radius + 1][cY - y + radius + 1] = new Pixel(i, cY - y);
                    p.add(map[i + radius + 1][cY - y + radius + 1]);
                }

            map[cX - y + radius + 1][cY + x + radius + 1] = new Pixel(cX - y, cY + x);
            p.add(map[cX - y + radius + 1][cY + x + radius + 1]);
            map[cX + y + radius + 1][cY + x + radius + 1] = new Pixel(cX + y, cY + x);
            p.add(map[cX + y + radius + 1][cY + x + radius + 1]);
            for(int i = cX - y + 1; i < cX + y; i++)
                if (map[i + radius + 1][cY + x + radius + 1]==null)
                {
                    map[i + radius + 1][cY + x + radius + 1] = new Pixel(i, cY + x);
                    p.add(map[i + radius + 1][cY + x + radius + 1]);
                }

            map[cX - y + radius + 1][cY - x + radius + 1] = new Pixel(cX - y, cY - x);
            p.add(map[cX - y + radius + 1][cY - x + radius + 1]);
            map[cX + y + radius + 1][cY - x + radius + 1] = new Pixel(cX + y, cY - x);
            p.add(map[cX + y + radius + 1][cY - x + radius + 1]);
            for(int i = cX - y + 1; i < cX + y; i++)
                if (map[i + radius + 1][cY - x + radius + 1]==null)
                {
                    map[i + radius + 1][cY - x + radius + 1] = new Pixel(i, cY - x);
                    p.add(map[i + radius + 1][cY - x + radius + 1]);
                }

            if (d < 0)
            {
                d += 2 * x + 1;
            }
            else
            {
                d += 2 * (x - y) + 1;
                y--;
            }

            x++;

        } while (x <= y);

        for(int r = 0; r < radius * 2 + 2; r++)
            for(int c = 0; c < radius * 2 + 2; c++)
                if(map[r][c] != null)
                {
                    if(r - 1 > 0)
                        map[r][c].neighbors[0] = map[r - 1][c]; //Up
                    if(r + 1 < radius * 2 + 2)
                        map[r][c].neighbors[2] = map[r + 1][c]; //Down
                    if(c - 1 > 0)
                        map[r][c].neighbors[3] = map[r][c - 1]; //Left
                    if(c + 1 < radius * 2 + 2)
                        map[r][c].neighbors[1] = map[r][c + 1]; //Right
                }

        for(Pixel pixel: p)
        {
            pixel.xDisp *= Constants.PIXEL_SIZE;
            pixel.yDisp *= Constants.PIXEL_SIZE;
            pixel.xOriginal *= Constants.PIXEL_SIZE;
            pixel.yOriginal *= Constants.PIXEL_SIZE;
            for(int n = 0; n < 4; n++)
            {
                if(pixel.neighbors[n] == null)
                    pixel.outside = true;
            }
        }

        return p;
    }

    public static ArrayList<Pixel> makeFillPixelRectangle( int x, int y, int width, int height, float size)
    {
        ArrayList<Pixel> p = new ArrayList<>();
        Pixel[][] pMap = new Pixel[width][height];

        for(int r = 0; r < pMap.length; r++)
        {
            for(int c = 0; c < pMap[0].length; c++)
            {
                pMap[r][c] = new Pixel((x + r) * Constants.PIXEL_SIZE, (y + c) * Constants.PIXEL_SIZE);
                p.add(pMap[r][c]);
            }
        }

        for(int r = 0; r < pMap.length; r++)
        {
            for(int c = 0; c < pMap[0].length; c++)
            {
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
            }
        }

        for(Pixel pixel: p)
        {
            pixel.xOriginal -= width / 2 * Constants.PIXEL_SIZE;
            pixel.yOriginal -= height / 2 * Constants.PIXEL_SIZE;
            pixel.xDisp -= width / 2 * Constants.PIXEL_SIZE;
            pixel.yDisp -= height / 2 * Constants.PIXEL_SIZE;
            for(int n = 0; n < 4; n++)
            {
                if(pixel.neighbors[n] == null)
                    pixel.outside = true;
            }
        }

        return p;
    }
}
