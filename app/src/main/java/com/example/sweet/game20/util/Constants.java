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

    public static final float COMPONENT_DROP_MOVESPEED = .001f;

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

    public enum DropType
    {
        HEALTH,
        EXTRA_MOD,
        EXTRA_GUN,
        GUN,
        THRUSTER,
        MOD,
        ANY
    }

    public enum ModType
    {
        FIRERATE,
        EXTRASHOTS,
        PRECISION
    }

    public enum GameState
    {
        IN_GAME,
        PAUSE_MENU,
        MAIN_MENU,
        OPTIONS,
    }

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

    public static final float[] swapButton = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -0.5f, -0.1f,   0f, 1f,
            0.5f, -0.1f,   1f, 1f,
            0.5f,  0.1f,   1f, 0f,
            -0.5f,  0.1f,   0f, 0f,
            -0.5f, -0.1f,   0f, 1f
    };

    public static final float[] screenShadeVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            2f,  2f,   1f, 1f,
            2f, -2f,   1f, 0f,
            -2f, -2f,   0f, 0f,
            -2f,  2f,   0f, 1f,
            2f,  2f,   1f, 1f,
    };

    //--------------------------------------Buttons

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
    //----- pause shit
    
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
    public static final float infoPanelY = .656f - .04f;

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

    public static final float dL = .04f;
    public static final float dW = .016f;
    public static final float dSkipY = .064f;
    public static final float[] digitVA = new float[]{
            0f,  0f, 0.5f, 0.5f,
            -dL, -dW,   0f, 1f,
            dL, -dW,   1f, 1f,
            dL,  dW,   1f, 0f,
            -dL,  dW,   0f, 0f,
            -dL, -dW,   0f, 1f
    };
}
