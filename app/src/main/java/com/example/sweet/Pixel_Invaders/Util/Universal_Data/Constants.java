package com.example.sweet.Pixel_Invaders.Util.Universal_Data;

/**
 * Created by Sweet on 1/22/2018.
 */

public class Constants
{
    public static final int BYTES_PER_FLOAT = 4;

    public static final float PIXEL_SIZE = .0056f;

    public static final float MAX_DIST_JUMP = PIXEL_SIZE * 1.4f;

    public static final float twoPI = (float)(2*Math.PI);

    public static final int CELL_LENGTH = 4;

    public static final float CELL_SIZE = PIXEL_SIZE * CELL_LENGTH;

    public static final int ZONE_LENGTH = 4;

    public static final float ZONE_SIZE = CELL_SIZE * ZONE_LENGTH;

    public static final int ENTITIES_LENGTH = 200;

    public static final int DROPS_LENGTH = 200;

    public static final double TYPE_0_DROP_LIVETIME = 40000;

    public static final double TYPE_1_DROP_LIVETIME = 120000;

    public static final float PUll_DROP_RADIUS = .5f;

    public static final float MIN_THRUST_MULT = 1f;

    public static final float MAX_THRUST_MULT = 3f;

    public static final float MIN_PPS = 32f;

    public static final float MAX_PPS = 1000f;

    public static final float msPerFrame = 16.67f;

    public static final float COMPONENT_DROP_RADIUS = .06f;

    public static final float COMPONENT_DROP_MOVESPEED = .0002f;

    public enum EnemyType
    {
        //Enemies
        SIMPLE,
        TINY,
        KAMIKAZE,
        PULSER,
        MASSACCELERATOR,
        MINELAYER,
        HEAVY,
        CARRIER,
        ASTEROID_RED_TINY,
        ASTEROID_GREY_TINY,
        ASTEROID_RED_SMALL,
        ASTEROID_GREY_SMALL,
        ASTEROID_RED_MEDIUM,
        ASTEROID_GREY_MEDIUM
    }

    public enum DropType
    {
        HEALTH,
        EXTRA_MOD,
        EXTRA_GUN,
        GUN,
        THRUSTER,
        MOD,
        ANY,
        FIRE_RATE,
        EXTRA_SHOTS,
        PRECISION,
        PLATING,
        PIERCING,
        TEMPORAL,
        BULLET_SPEED,
        RESTORATION,
        NONE
    }

    public enum GameState
    {
        IN_GAME,
        PAUSE_MENU,
        MAIN_MENU,
        OPTIONS,
        GAME_OVER
    }

    //--------------------------------------Level Control
    public static final float[][] tierPercents = new float[][]{
    //       0      1       2       3       4       5       6       7       8       9
            {1f,    .9f,    .8f,    .75f,   .7f,    .6f,    .55f,   .5f,    .4f,    .3f},   //Tier 1
            {0f,    .1f,    .2f,    .25f,   .3f,    .35f,   .4f,    .4f,    .45f,   .5f},   //Tier 2
            {0f,    0f,     0f,     0f,     0f,     .5f,    .5f,    .1f,    .15f,   .2f}    //Tier 3
    };

    public static final float[][] tier1Per = new float[][]{
    //       0      1       2       3       4       5       6       7       8       9
            {.7f,    .6f,    .5f,    .4f,    .3f,    .2f,    .1f,    0f,     0f,    0f},   //tiny
            {.3f,    .3f,    .3f,    .3f,    .3f,    .4f,    .5f,    .6f,    .6f,   .6f},   //kami
            {0f,     .1f,    .2f,    .3f,    .4f,    .4f,    .4f,    .4f,    .4f,   .4f}    //simple
    };

    public static final float[][] tier3Per = new float[][]{
    //       0      1       2       3       4       5       6       7       8       9
            {1f,    1f,     1f,      1f,    1f,     1f,    .9f,   .8f,    .7f,    .65f},   //MA
            {0f,    0f,    0f,    0f,   0f,    0f,   .1f,    .2f,    .3f,   .35f}   //carrier
    };

    public static final float[][] tier2Per = new float[][]{
    //       0      1       2       3       4       5       6       7       8       9
            {.85f,    .75f,    .7f,    .7f,   .65f,    .65f,    .6f,   .6f,    .5f,   .5f},
            {.15f,   .15f,   .15f,   .15f,  .15f,   .15f,   .15f,  .15f,   .2f,  .2f},
            {0f,    .1f,    .15f,    .15f,  .2f,    .2f,    .25f,  .25f,   .3f,   .3f}
    };

    public static final int
            tinyVal = 1,
            kamikazeVal = 2,
            simpelVal = 4,
            heavyVal = 7,
            mineLayerVal = 8,
            pulserVal = 10,
            massAcceleratorVal = 20,
            carrierVal = 30;
    //--------------------------------------Background Info

    public static final float[] background = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -2.5f, -2.5f,   0f, 1f,
            2.5f, -2.5f,   1f, 1f,
            2.5f,  2.5f,   1f, 0f,
            -2.5f,  2.5f,   0f, 0f,
            -2.5f, -2.5f,   0f, 1f
    };
    public static final float[] moonVA = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -.088f, -.088f,   0f, 1f,
            .088f, -.088f,   1f, 1f,
            .088f,  .088f,   1f, 0f,
            -.088f,  .088f,   0f, 0f,
            -.088f, -.088f,   0f, 1f
    };

    public static final float[] earthVA = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -.256f, -.256f,   0f, 1f,
            .256f, -.256f,   1f, 1f,
            .256f,  .256f,   1f, 0f,
            -.256f,  .256f,   0f, 0f,
            -.256f, -.256f,   0f, 1f
    };

    //--------------------------------------UI Info

    public static final float joyStickRadius = .32f;

    public static final float[] joyBaseMoveVA = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -joyStickRadius, -joyStickRadius,   0f, 1f,
            joyStickRadius, -joyStickRadius,   1f, 1f,
            joyStickRadius,  joyStickRadius,   1f, 0f,
            -joyStickRadius,  joyStickRadius,   0f, 0f,
            -joyStickRadius, -joyStickRadius,   0f, 1f
    };

    public static final float[] joyStickMoveVA = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -joyStickRadius/1.8f, -joyStickRadius/1.8f,   0f, 1f,
            joyStickRadius/1.8f, -joyStickRadius/1.8f,   1f, 1f,
            joyStickRadius/1.8f,  joyStickRadius/1.8f,   1f, 0f,
            -joyStickRadius/1.8f,  joyStickRadius/1.8f,   0f, 0f,
            -joyStickRadius/1.8f, -joyStickRadius/1.8f,   0f, 1f
    };

    public static final float[]
            joyBaseShootVA = joyBaseMoveVA,
            joyStickShootVA = joyStickMoveVA;

    public static final float[] screenShadeVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            2f,  2f,   1f, 1f,
            2f, -2f,   1f, 0f,
            -2f, -2f,   0f, 0f,
            -2f,  2f,   0f, 1f,
            2f,  2f,   1f, 1f,
    };

    public static final float[] riftShadeVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            1f,  1f,   1f, 1f,
            1f, -1f,   1f, 0f,
            -1f, -1f,   0f, 0f,
            -1f,  1f,   0f, 1f,
            1f,  1f,   1f, 1f,
    };
    
    public static final float
            rbL = .1f,
            rbW = .4f,
            hoverMag = 1.04f;

    public static final float[] resumeButtonVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -rbL, -rbW,   0f, 1f,
            rbL, -rbW,   1f, 1f,
            rbL,  rbW,   1f, 0f,
            -rbL,  rbW,   0f, 0f,
            -rbL, -rbW,   0f, 1f
    };

    public static final float[] resumeButtonHoveredVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -rbL * hoverMag, -rbW * hoverMag,   0f, 1f,
            rbL * hoverMag, -rbW * hoverMag,   1f, 1f,
            rbL * hoverMag,  rbW * hoverMag,   1f, 0f,
            -rbL * hoverMag,  rbW * hoverMag,   0f, 0f,
            -rbL * hoverMag, -rbW * hoverMag,   0f, 1f
    };

    public static final float
            sbL = 1.2f,
            sbW = .5f;
    
    public static final float[] shadeBarVa = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -sbL, -sbW,   0f, 1f,
            sbL, -sbW,   1f, 1f,
            sbL,  sbW,   1f, 0f,
            -sbL,  sbW,   0f, 0f,
            -sbL, -sbW,   0f, 1f
    };

    public static final float radius = .128f;

    public static final float[] componentSquareVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            radius,  radius,   1f, 1f,
            radius, -radius,   1f, 0f,
            -radius, -radius,   0f, 0f,
            -radius,  radius,   0f, 1f,
            radius,  radius,   1f, 1f,
    };

    public static final float[] componentSquareHoverVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            radius * hoverMag,  radius * hoverMag,   1f, 1f,
            radius * hoverMag, -radius * hoverMag,   1f, 0f,
            -radius * hoverMag, -radius * hoverMag,   0f, 0f,
            -radius * hoverMag,  radius * hoverMag,   0f, 1f,
            radius * hoverMag,  radius * hoverMag,   1f, 1f,
    };
    
    public static final float r2 = .092f;
    
    public static final float[] playerSquareVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            r2,  r2,   1f, 1f,
            r2, -r2,   1f, 0f,
            -r2, -r2,   0f, 0f,
            -r2,  r2,   0f, 1f,
            r2,  r2,   1f, 1f,
    };

    public static final float[] playerSquareHoverVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            r2 * hoverMag,  r2 * hoverMag,   1f, 1f,
            r2 * hoverMag, -r2 * hoverMag,   1f, 0f,
            -r2 * hoverMag, -r2 * hoverMag,   0f, 0f,
            -r2 * hoverMag,  r2 * hoverMag,   0f, 1f,
            r2 * hoverMag,  r2 * hoverMag,   1f, 1f,
    };


    //----- pause Info
    
    public static final float
            aibL = .808f, //aibL = .664f,
            aibW = 1.344f;
    
    public static final float[] allInfoBoxVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -aibL, -aibW,   0f, 1f,
            aibL, -aibW,   1f, 1f,
            aibL,  aibW,   1f, 0f,
            -aibL,  aibW,   0f, 0f,
            -aibL, -aibW,   0f, 1f
    };
    
    public static final float
            ipW = .328f, //aibL = .664f,
            ipL = .584f;

    public static final float[] infoPanelVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -ipL, -ipW,   0f, 1f,
            ipL, -ipW,   1f, 1f,
            ipL,  ipW,   1f, 0f,
            -ipL,  ipW,   0f, 0f,
            -ipL, -ipW,   0f, 1f
    };

    //info Panel center x: -.044f    y: .936f
    public static final float infoPanelY = .666f;

    public static final float iPnum1X = -.044f + .392f;
    public static final float iPnum2X = -.044f + .256f;
    public static final float iPnum3X = -.044f + .12f;

    public static final float gpPathX = -.044f + .048f;
    public static final float gpPathY = .656f + .08f;

    public static final float gpBulletY = .936f;
    public static final float gpBulletX = -.044f - .28f;

    public static final float pbR = .587f;

    public static final float[] playerBoxVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -pbR, -pbR,   0f, 1f,
            pbR, -pbR,   1f, 1f,
            pbR,  pbR,   1f, 0f,
            -pbR,  pbR,   0f, 0f,
            -pbR, -pbR,   0f, 1f
    };

    public static final float sldBR = .1f;

    public static final float[] slideButtonVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -sldBR, -sldBR,   0f, 1f,
            sldBR, -sldBR,   1f, 1f,
            sldBR,  sldBR,   1f, 0f,
            -sldBR,  sldBR,   0f, 0f,
            -sldBR, -sldBR,   0f, 1f
    };

    public static final float slideMax = .988f;
    public static final float slideMin = .112f;

    public static final float chckR = .072f;

    public static final float[] checkVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -chckR, -chckR,   0f, 1f,
            chckR, -chckR,   1f, 1f,
            chckR,  chckR,   1f, 0f,
            -chckR,  chckR,   0f, 0f,
            -chckR, -chckR,   0f, 1f
    };

    public static final float
            paL = .072f,
            paW = .224f;

    public static final float[] pauseVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -paL, -paW,   0f, 1f,
            paL, -paW,   1f, 1f,
            paL,  paW,   1f, 0f,
            -paL,  paW,   0f, 0f,
            -paL, -paW,   0f, 1f
    };

    public static final float
            modL = .112f,
            modW = .328f;

    public static final float[] modNameVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -modL, -modW,   0f, 1f,
            modL, -modW,   1f, 1f,
            modL,  modW,   1f, 0f,
            -modL,  modW,   0f, 0f,
            -modL, -modW,   0f, 1f
    };

    public static final float
            modL2 = .256f;

    public static final float[] modDescripVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -modL2, -modW,   0f, 1f,
            modL2, -modW,   1f, 1f,
            modL2,  modW,   1f, 0f,
            -modL2,  modW,   0f, 0f,
            -modL2, -modW,   0f, 1f
    };
    public static final float
            titleL = .256f,
            titleW = .512f;

    public static final float[] titleVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -titleL, -titleW,   0f, 1f,
            titleL, -titleW,   1f, 1f,
            titleL,  titleW,   1f, 0f,
            -titleL,  titleW,   0f, 0f,
            -titleL, -titleW,   0f, 1f
    };

    //------------------------------------------------------------------------Enemy Data
    public static final int[]
            simpleGunCoor = {19, 8, 20, 8, 21, 8, 19, 11, 20, 11, 21, 11},
            simpleMThrustCoor = {0, 8, 0, 9, 0, 10, 0, 11, 1, 9, 1, 10};

    public static final float[]
            simpleGunOffset = {(20.5f - 12f) * Constants.PIXEL_SIZE, (9.5f - 10f) * Constants.PIXEL_SIZE};

    public static final int[]
            kamikazeMThrustCoor = {1, 6, 1, 7, 1, 8, 1, 9};

    public static final int[]
            tinyMThrustCoor = {1, 5, 1, 6};
   /* public static final int[]
            pulseThrustCoor1 = {31, 1, 32, 2},
            pulseThrustCoor2 = {44, 12, 43, 13},
            pulseThrustCoor3 = {43, 32, 44, 33},
            pulseThrustCoor4 = {32, 43, 33, 44},
            pulseThrustCoor5 = {13, 43, 12, 44},
            pulseThrustCoor6 = {1, 33, 2, 32},
            pulseThrustCoor7 = {2, 13, 1, 12},
            pulseThrustCoor8 = {12, 1, 13, 2},
            pulseGunCoor1 = {38, 15, 38, 16, 38, 17, 38, 18, 
                    39, 19, 39, 20, 39, 21, 39, 22, 39, 23, 39, 24, 39, 25, 39, 26,
                    38, 27, 38, 28, 38, 29, 38, 30},
            pulseGunCoor2 = {15, 38, 16, 38, 17, 38, 18, 38,
                    19, 39, 20, 39, 21, 39, 22, 39, 23, 39, 24, 39, 25, 39, 26, 39,
                    27, 38, 28, 38, 29, 38, 30, 38},
            pulseGunCoor3 = {7, 15, 7, 16, 7, 17, 7, 18,
                    6, 19, 6, 20, 6, 21, 6, 22, 6, 23, 6, 24, 6, 25, 6, 26,
                    7, 27, 7, 28, 7, 29, 7, 30},
            pulseGunCoor4 = {15, 7, 16, 7, 17, 7, 18, 7,
                    19, 6, 20, 6, 21, 6, 22, 6, 23, 6, 24, 6, 25, 6, 26, 6,
                    27, 7, 28, 7, 29, 7, 30, 7};

    public static final float[]
            pulseGunOffset1 = {(38.5f - 23f) * Constants.PIXEL_SIZE, (22.5f - 23f) * Constants.PIXEL_SIZE},
            pulseGunOffset2 = {(22.5f - 23f) * Constants.PIXEL_SIZE, (38.5f - 23f) * Constants.PIXEL_SIZE},
            pulseGunOffset3 = {(6.5f - 23f) * Constants.PIXEL_SIZE, (22.5f - 23f) * Constants.PIXEL_SIZE},
            pulseGunOffset4 = {(22.5f - 23f) * Constants.PIXEL_SIZE, (6.5f - 23f) * Constants.PIXEL_SIZE};*/

    public static final int[][]
            pulseThrustCoors = new int[][]{
                {31, 1, 32, 2},
                {44, 12, 43, 13},
                {43, 32, 44, 33},
                {32, 43, 33, 44},
                {13, 43, 12, 44},
                {1, 33, 2, 32},
                {2, 13, 1, 12},
                {12, 1, 13, 2}
            },
            pulseGunCoors = new int[][]{
                {38, 15, 38, 16, 38, 17, 38, 18,
                        39, 19, 39, 20, 39, 21, 39, 22, 39, 23, 39, 24, 39, 25, 39, 26,
                        38, 27, 38, 28, 38, 29, 38, 30},
                {15, 38, 16, 38, 17, 38, 18, 38,
                        19, 39, 20, 39, 21, 39, 22, 39, 23, 39, 24, 39, 25, 39, 26, 39,
                        27, 38, 28, 38, 29, 38, 30, 38},
                {7, 15, 7, 16, 7, 17, 7, 18,
                        6, 19, 6, 20, 6, 21, 6, 22, 6, 23, 6, 24, 6, 25, 6, 26,
                        7, 27, 7, 28, 7, 29, 7, 30},
                {15, 7, 16, 7, 17, 7, 18, 7,
                        19, 6, 20, 6, 21, 6, 22, 6, 23, 6, 24, 6, 25, 6, 26, 6,
                        27, 7, 28, 7, 29, 7, 30, 7}
            };

    public static final float[][] pulseGunOffsets = new float[][]{
            {(38.5f - 23f) * Constants.PIXEL_SIZE, (22.5f - 23f) * Constants.PIXEL_SIZE},
            {(22.5f - 23f) * Constants.PIXEL_SIZE, (38.5f - 23f) * Constants.PIXEL_SIZE},
            {(6.5f - 23f) * Constants.PIXEL_SIZE, (22.5f - 23f) * Constants.PIXEL_SIZE},
            {(22.5f - 23f) * Constants.PIXEL_SIZE, (6.5f - 23f) * Constants.PIXEL_SIZE}
    };

    public static final int[][]
            heavyThrustCoors = new int[][]{
                    {1, 6, 1, 7, 1, 8, 2, 7},
                    {2, 13, 2, 14, 2, 15, 2, 16, 3, 14, 3, 15},
                    {1, 21, 1, 22, 1, 23, 2, 22}
            };

    public static final int[] heavyGunCoors = new int[]{15, 14, 16, 14, 16, 15, 15, 15};

    public static final int[][]
            mineLayerThrustCoors = new int[][]{
            {14, 37, 14, 36, 14, 35, 15, 36},
            {14, 2, 14, 3, 14, 4, 15, 3}
    };

    public static final int[] mineLayerGunCoors = new int[]{24, 19, 23, 19, 24, 20, 23, 20};

    public static final int[][]
            maThrustCoors = new int[][]{
            {63, 2, 63, 3, 63, 4},
            {2, 20, 2, 21, 2, 22, 2, 23, 2, 24, 3, 25, 3, 26, 3, 27, 3, 28, 2, 29, 2, 30, 2, 31, 2, 32, 2, 33},
            {63, 39, 63, 40, 63, 41}
    };

    public static final int[] maGunCoors = new int[]{32, 25, 32, 26, 32, 27, 32, 28, 33, 25, 33, 26, 33, 27, 33, 28};

    public static final int[][]
            carrierThrustCoors = new int[][]{
            {4, 18, 4, 19, 4, 20, 4, 21, 4, 22},
            {14, 38, 14, 39, 14, 40, 14, 41, 14, 42, 14, 43, 14, 44, 14, 45, 15, 40, 15, 41, 15, 42, 15, 43},
            {4, 61, 4, 62, 4, 63, 4, 64, 4, 65}
    };
}
