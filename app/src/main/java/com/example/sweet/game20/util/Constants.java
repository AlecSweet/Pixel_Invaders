package com.example.sweet.game20.util;

/**
 * Created by Sweet on 1/22/2018.
 */

public class Constants
{
    public static final int BYTES_PER_FLOAT = 4;

    public static final float PIXEL_SIZE = .008f;

    public static final float twoPI = (float)(2*Math.PI);

    public static final float CELL_SIZE = .032f;

    public static final int CELL_LENGTH = 4;

    public static final float ZONE_SIZE = .128f;

    public static final int ZONE_LENGTH = 4;

    public static final int ENTITIES_LENGTH = 200;

    public static final int SCREEN_BOUND = 4;

    public enum EnemyType
    {
        //Enemies
        SIMPLE,
        CARRIER,
        ASTEROID_RED_TINY,
        ASTEROID_GREY_TINY,
        ASTEROID_RED_SMALL,
        ASTEROID_GREY_SMALL,
        ASTEROID_RED_MEDIUM,
        ASTEROID_GREY_MEDIUM
    }
}
