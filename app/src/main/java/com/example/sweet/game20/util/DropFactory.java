package com.example.sweet.game20.util;

import com.example.sweet.game20.Objects.Component;
import com.example.sweet.game20.Objects.Drop;
import com.example.sweet.game20.Objects.PixelGroup;

import java.util.HashMap;
import java.util.zip.CheckedOutputStream;

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
        return new Drop(dropCatalog.get(dT).clone(), x, y, Constants.TYPE_1_DROP_LIVETIME, c, dT);
    }
}
