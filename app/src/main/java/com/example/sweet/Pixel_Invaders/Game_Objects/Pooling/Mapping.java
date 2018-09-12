package com.example.sweet.Pixel_Invaders.Game_Objects.Pooling;

import java.util.HashSet;

/**
 * Created by Sweet on 5/13/2018.
 */

public class Mapping
{
    public int size = 0;
    public int totalSize = 0;
    public HashSet<Integer> connections = new HashSet<>();
    public Mapping(int g)
    {
        connections.add(g);
    }
}
