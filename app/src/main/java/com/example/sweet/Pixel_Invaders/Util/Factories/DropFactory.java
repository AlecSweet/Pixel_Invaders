package com.example.sweet.Pixel_Invaders.Util.Factories;

import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Component;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Drop;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;

import java.util.HashMap;

/**
 * Created by Sweet on 6/12/2018.
 */

public class DropFactory
{
    private HashMap<Constants.DropType,PixelGroup> dropCatalog = new HashMap<>();

    public DropFactory()
    {
    }

    public void addDropToCatalog(Constants.DropType dT, PixelGroup p)
    {
        dropCatalog.put(dT, p);
    }

    public Drop getNewDrop(Constants.DropType dT, float x, float y)
    {
        return new Drop(dropCatalog.get(dT), x, y, Constants.TYPE_0_DROP_LIVETIME, dT);
    }

    public Drop getNewDrop(Constants.DropType dT, float x, float y, Component c)
    {
        return new Drop(dropCatalog.get(dT), x, y, Constants.TYPE_1_DROP_LIVETIME, c, dT);
    }
}
