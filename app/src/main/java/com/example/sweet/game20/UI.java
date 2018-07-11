package com.example.sweet.game20;

import android.content.Context;
import android.graphics.PointF;


import com.example.sweet.game20.Objects.Button;
import com.example.sweet.game20.Objects.Component;
import com.example.sweet.game20.Objects.Drawable;
import com.example.sweet.game20.Objects.Drop;
import com.example.sweet.game20.Objects.GunComponent;
import com.example.sweet.game20.Objects.ImageContainer;
import com.example.sweet.game20.Objects.ModComponent;
import com.example.sweet.game20.Objects.Player;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.TextPresenter;
import com.example.sweet.game20.util.TextureLoader;
import com.example.sweet.game20.util.VectorFunctions;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static com.example.sweet.game20.util.Constants.*;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;

/**
 * Created by Sweet on 2/14/2018.
 */

public class UI extends Drawable
{
    private float
            joyStickRadius = .32f;

    public PointF
            menuOnDown = new PointF(0, 0),
            menuOnMove = new PointF(0, 0),
            movementOnDown = new PointF(0, 0),
            movementOnMove = new PointF(0, 0),
            shootingOnDown = new PointF(0, 0),
            shootingOnMove = new PointF(0, 0);

    public boolean menuPointerDown = false;

    private boolean
            movementDown = false,
            shootingDown = false;

    public float
            xScale,
            yScale,
            pointSize;

    public int
            uiShaderProgram,
            pixelShaderProgram,
            whiteTexture,
            uTextureLocation;

    private Button heldDropButton;
    private Button selectedButton = null;

    public GameState gameState = GameState.MAIN_MENU;

    private ImageContainer
            joyBaseMove,
            joyStickMove,
            joyBaseShoot,
            joyStickShoot,
            screenShade,
            allInfoBox,
            modPanel,
            gunPanel,
            thrusterPanel;

    private Button
            resumeButton,
            optionsButton,
            exitButton;

    private float
            playerModelX = 0.16f,
            playerModelY = -.672f;

    public Player player;

    private float mag = 2.8f;

    private int score = 0;

    public boolean exitFlag = false;

    private Button[][] componentPanel = new Button[4][2];

    private Button[] playerGuns = new Button[3];
    float u = .024f;
    private PointF[] gunOffsets = new PointF[]{
            //new PointF(-.06f * mag + u, -.072f * mag),
            //new PointF(-.06f * mag + u, .056f * mag),
            new PointF(0 + u, -.008f * mag),
            new PointF(.056f * mag + u, -.088f * mag),
            new PointF(.056f * mag + u, .072f * mag)
    };

    private Button[] playerThrusters = new Button[3];
    private PointF[] thrusterOffsets = new PointF[]{
            new PointF(-.132f * mag, -.112f * mag),
            new PointF(-.132f * mag, -.008f * mag),
            new PointF(-.132f * mag, .096f * mag)
    };

    private Button[] playerMods = new Button[5];
    private PointF modLeftOffset = new PointF(-.46f, -.235f);

    private Button[] allComponentButtons = new Button[
            componentPanel.length * componentPanel[0].length +
            playerGuns.length +
            playerMods.length +
            playerThrusters.length
            ];

    private TextPresenter textPresenter;

    public UI(Context context, int shaderLocation)
    {
        int[] glVarLocations = new int[5];
        glVarLocations[0] = glGetUniformLocation(shaderLocation, "x_displacement");
        glVarLocations[1] = glGetUniformLocation(shaderLocation, "y_displacement");
        glVarLocations[2] = glGetAttribLocation(shaderLocation, "a_Position");
        glVarLocations[3] = glGetAttribLocation(shaderLocation, "a_TexCoordinate");
        glVarLocations[4] = glGetUniformLocation(shaderLocation, "u_Texture");

        textPresenter = new TextPresenter(context, glVarLocations);

        joyBaseMove = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.mjb),
                        joyBaseMoveVA,
                        0, 0,
                        "joyBaseMove",
                        glVarLocations,
                        -1, -1
                );
        joyStickMove = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.mj),
                        joyStickMoveVA,
                        0, 0,
                        "joyStickMove",
                        glVarLocations,
                        -1, -1
                );
        joyBaseShoot = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.sjb),
                        joyBaseShootVA,
                        0, 0,
                        "joyBaseShoot",
                        glVarLocations,
                        -1, -1
                );
        joyStickShoot = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.sj),
                        joyStickShootVA,
                        0, 0,
                        "joyStickShoot",
                        glVarLocations,
                        -1, -1
                );
        screenShade = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.shade),
                        screenShadeVA,
                        0, 0,
                        "screenShade",
                        glVarLocations,
                        -1, -1
                );
        modPanel = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.modpanel),
                        infoPanelVA,
                        -.024f + .02f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        gunPanel = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.gunpanel),
                        infoPanelVA,
                        -.024f + .02f, .936f,
                        "gunPanel",
                        glVarLocations,
                        -1, -1
                );
        thrusterPanel = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.thrusterpanel),
                        infoPanelVA,
                        -.024f + .02f, .936f,
                        "thrusterPanel",
                        glVarLocations,
                        -1, -1
                );
        allInfoBox = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.largebox),
                        allInfoBoxVA,
                        -.024f - .144f + .02f, 0,
                        "largeBox",
                        glVarLocations,
                        -1, -1
                );

        resumeButton = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.resume),
                                        resumeButtonVA,
                                        .82f + .02f, .55f,
                                        "resumeButton",
                                        glVarLocations,
                                        rbL, rbW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.resume2),
                                        resumeButtonHoveredVA,
                                        .82f + .02f, .55f,
                                        "resumeButtonHover",
                                        glVarLocations,
                                        rbL, rbW
                                ),
                        null
                );

        optionsButton = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.options),
                                        resumeButtonVA,
                                        .82f + .02f, 0,
                                        "optionsButton",
                                        glVarLocations,
                                        rbL, rbW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.options2),
                                        resumeButtonHoveredVA,
                                        .82f + .02f, 0,
                                        "optionsButtonHover",
                                        glVarLocations,
                                        rbL, rbW
                                ),
                        null
                );

        exitButton = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.exit),
                                        resumeButtonVA,
                                        .82f + .02f, -.55f,
                                        "exitButton",
                                        glVarLocations,
                                        rbL, rbW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.exit2),
                                        resumeButtonHoveredVA,
                                        .82f + .02f, -.55f,
                                        "exitButtonHover",
                                        glVarLocations,
                                        rbL, rbW
                                ),
                        null
                );

        int itrAllCompButtons = 0;
        int squareTexture = TextureLoader.loadTexture(context, R.drawable.square);
        float midX = .429f + .02f;
        float midY = .098f;
        for (int r = 0; r < componentPanel.length; r++)
        {
            float xL = midX - r * (radius * 2 + .048f);
            for (int c = 0; c < componentPanel[0].length; c++)
            {
                float yL = midY + c * (radius * 2 + .048f);
                componentPanel[r][c] = new Button
                        (
                                new ImageContainer
                                        (
                                                squareTexture,
                                                componentSquareVA,
                                                xL, yL,
                                                "square",
                                                glVarLocations,
                                                radius, radius
                                        ),
                                new ImageContainer
                                        (
                                                squareTexture,
                                                componentSquareHoverVA,
                                                xL, yL,
                                                "squareHover",
                                                glVarLocations,
                                                radius, radius
                                        ),
                                DropType.ANY
                        );
                allComponentButtons[itrAllCompButtons] = componentPanel[r][c];
                itrAllCompButtons++;
            }
        }

        int square2Texture = TextureLoader.loadTexture(context, R.drawable.square2);
        for (int i = 0; i < playerGuns.length; i++)
        {
            playerGuns[i] = new Button
                    (
                            new ImageContainer
                                    (
                                            square2Texture,
                                            playerSquareVA,
                                            playerModelX + gunOffsets[i].x, playerModelY + gunOffsets[i].y,
                                            "square",
                                            glVarLocations,
                                            r2, r2
                                    ),
                            new ImageContainer
                                    (
                                            square2Texture,
                                            playerSquareHoverVA,
                                            playerModelX + gunOffsets[i].x, playerModelY + gunOffsets[i].y,
                                            "squareHover",
                                            glVarLocations,
                                            r2, r2
                                    ),
                            DropType.GUN
                    );
            allComponentButtons[itrAllCompButtons] = playerGuns[i];
            itrAllCompButtons++;
        }

        for (int i = 0; i < playerThrusters.length; i++)
        {
            playerThrusters[i] = new Button
                    (
                            new ImageContainer
                                    (
                                            square2Texture,
                                            playerSquareVA,
                                            playerModelX + thrusterOffsets[i].x, playerModelY + thrusterOffsets[i].y,
                                            "square",
                                            glVarLocations,
                                            r2, r2
                                    ),
                            new ImageContainer
                                    (
                                            square2Texture,
                                            playerSquareHoverVA,
                                            playerModelX + thrusterOffsets[i].x, playerModelY + thrusterOffsets[i].y,
                                            "squareHover",
                                            glVarLocations,
                                            r2, r2
                                    ),
                            DropType.THRUSTER
                    );
            allComponentButtons[itrAllCompButtons] = playerThrusters[i];
            itrAllCompButtons++;
        }

        for (int i = 0; i < playerMods.length; i++)
        {
            playerMods[i] = new Button
                    (
                            new ImageContainer
                                    (
                                            square2Texture,
                                            playerSquareVA,
                                            modLeftOffset.x, modLeftOffset.y - i * (.038f + r2 * 2),
                                            "square",
                                            glVarLocations,
                                            r2, r2
                                    ),
                            new ImageContainer
                                    (
                                            square2Texture,
                                            playerSquareHoverVA,
                                            modLeftOffset.x, modLeftOffset.y - i * (.038f + r2 * 2),
                                            "squareHover",
                                            glVarLocations,
                                            r2, r2
                                    ),
                            DropType.MOD
                    );
            allComponentButtons[itrAllCompButtons] = playerMods[i];
            itrAllCompButtons++;
        }
    }

    public void moveJoySticks()
    {
        if (movementDown)
        {
            joyBaseMove.x = movementOnDown.x;
            joyBaseMove.y = -movementOnDown.y;

            float xTempDifference = (movementOnMove.x - movementOnDown.x);
            float yTempDifference = (movementOnMove.y - movementOnDown.y);
            float tempMagnitude = VectorFunctions.getMagnitude(xTempDifference, yTempDifference);

            if (tempMagnitude > joyStickRadius * .8f)
            {
                joyStickMove.x = joyStickRadius * .8f * (xTempDifference / tempMagnitude) + movementOnDown.x;
                joyStickMove.y = -(joyStickRadius * .8f * (yTempDifference / tempMagnitude) + movementOnDown.y);
            }
            else
            {
                joyStickMove.x = movementOnMove.x;
                joyStickMove.y = -movementOnMove.y;
            }
        }

        if (shootingDown)
        {
            joyBaseShoot.x = shootingOnDown.x;
            joyBaseShoot.y = -shootingOnDown.y;

            float xTempDifference = (shootingOnMove.x - shootingOnDown.x);
            float yTempDifference = (shootingOnMove.y - shootingOnDown.y);
            float tempMagnitude = VectorFunctions.getMagnitude(xTempDifference, yTempDifference);

            if (tempMagnitude > joyStickRadius * .8f)
            {
                joyStickShoot.x = joyStickRadius * .8f * (xTempDifference / tempMagnitude) + shootingOnDown.x;
                joyStickShoot.y = -(joyStickRadius * .8f * (yTempDifference / tempMagnitude) + shootingOnDown.y);
            }
            else
            {
                joyStickShoot.x = shootingOnMove.x;
                joyStickShoot.y = -shootingOnMove.y;
            }
        }
    }

    @Override
    public void draw(double interp)
    {
        if (gameState == GameState.IN_GAME)
        {
            moveJoySticks();
            if (movementDown)
            {
                joyBaseMove.draw();
                joyStickMove.draw();
            }
            if (shootingDown)
            {
                joyBaseShoot.draw();
                joyStickShoot.draw();
            }
            textPresenter.drawInt(score++, -.9f, 0f - String.valueOf(score).length() / 2 * dSkipY);
        }
        else if (gameState == GameState.PAUSE_MENU)
        {
            //drawPausedMenu();
            screenShade.draw();

            glUseProgram(pixelShaderProgram);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, whiteTexture);
            glUniform1i(uTextureLocation, 0);
            player.getPixelGroup().softDraw(playerModelX + player.driftX,
                    playerModelY + player.driftY,
                    0,
                    player.tiltAngle,
                    mag,
                    pointSize * mag);

            for(int g = 0; g < player.getMaxGuns(); g++)
            {
                if(playerGuns[g].drop != null && playerGuns[g] != heldDropButton)
                {
                    playerGuns[g].drop.menuDraw(
                            playerGuns[g].regular.x,
                            playerGuns[g].regular.y,
                            1f,
                            pointSize
                    );
                }
            }

            for(int m = 0; m < player.getMaxMods(); m++)
            {
                if(playerMods[m].drop != null && playerMods[m] != heldDropButton)
                {
                    playerMods[m].drop.menuDraw(
                            playerMods[m].regular.x,
                            playerMods[m].regular.y,
                            1f,
                            pointSize
                    );
                }
            }

            for(int t = 0; t < player.thrusters.length; t++)
            {
                if(playerThrusters[t].drop != null && playerThrusters[t] != heldDropButton)
                {
                    playerThrusters[t].drop.menuDraw(
                            playerThrusters[t].regular.x,
                            playerThrusters[t].regular.y,
                            1f,
                            pointSize
                    );
                }
            }

            if(heldDropButton != null)
            {
                heldDropButton.drop.menuDraw(menuOnMove.x, -menuOnMove.y, 2f, pointSize * 2f);
            }

            for (Button[] bA : componentPanel)
            {
                for (Button b : bA)
                {
                    if (b.drop != null && b != heldDropButton)
                    {
                        b.drop.menuDraw(b.regular.x, b.regular.y, 1, pointSize);
                    }
                }
            }

            if(selectedButton != null)
            {
                if(selectedButton.drop != null)
                {
                    if(selectedButton.drop.component.type == DropType.GUN)
                    {
                        ((GunComponent)selectedButton.drop.component).gun.getTemplate().softDraw(
                                Constants.gpBulletX,
                                Constants.gpBulletY,
                                0,0
                        );
                    }
                }
            }

            glUseProgram(uiShaderProgram);
            allInfoBox.draw();
            //playerBox.draw();
            resumeButton.draw(menuOnMove.x, menuOnMove.y);
            optionsButton.draw(menuOnMove.x, menuOnMove.y);
            exitButton.draw(menuOnMove.x, menuOnMove.y);


            if(selectedButton != null &&
                    selectedButton.drop != null &&
                    selectedButton.drop.component != null)
            {
                switch (selectedButton.drop.component.type)
                {
                    case THRUSTER: thrusterPanel.draw();
                            break;
                    case GUN: gunPanel.draw();
                            textPresenter.drawInt(
                                    (int)((GunComponent)selectedButton.drop.component).gun.getShotDelay(),
                                    Constants.iPnum1X, Constants.infoPanelY
                            );
                            textPresenter.drawInt(
                                    ((GunComponent)selectedButton.drop.component).gun.getTemplate().totalPixels,
                                    Constants.iPnum2X, Constants.infoPanelY
                            );
                            textPresenter.drawInt(
                                    (int)(((GunComponent)selectedButton.drop.component).gun.getTemplate().totalPixels *
                                            (1000 / ((GunComponent)selectedButton.drop.component).gun.getShotDelay())),
                                    Constants.iPnum3X, Constants.infoPanelY
                            );
                            break;
                    case MOD: modPanel.draw();
                            break;
                }
            }
            for (Button[] bA : componentPanel)
            {
                for (Button b : bA)
                {
                    b.draw(menuOnMove.x, menuOnMove.y);
                }
            }

            for (int g = 0; g < player.getMaxGuns(); g++)
            {
                playerGuns[g].draw(menuOnMove.x, menuOnMove.y);
            }

            for (Button b : playerThrusters)
            {
                b.draw(menuOnMove.x, menuOnMove.y);
            }

            for(int m = 0; m < player.getMaxMods(); m++)
            {
                playerMods[m].draw(menuOnMove.x, menuOnMove.y);
            }

            textPresenter.drawInt(score, -.86f, -.07f);
        }
    }

    public void pointerDown()
    {
        if (gameState == GameState.PAUSE_MENU)
        {
            for(Button b: allComponentButtons)
            {
                if(b.drop != null && b.pointOnButton(menuOnDown.x, menuOnDown.y))
                {
                    heldDropButton = b;
                    selectedButton = b;
                }
            }
        }
    }

    public void pointerUp()
    {
        for(Button b: allComponentButtons)
        {
            if(b.cursorOnButton)
            {
                selectedButton = b;
                break;
            }
        }

        if( heldDropButton != null &&
            heldDropButton.drop != null &&
            heldDropButton.drop.component != null)
        {
            if( selectedButton.type == DropType.ANY ||
                selectedButton.type == heldDropButton.drop.component.type)
            {
                if( selectedButton.drop != null)
                {
                    Component t = selectedButton.drop.component;

                    selectedButton.drop.component = heldDropButton.drop.component;
                    if (selectedButton.drop.component != null)
                    {
                        selectedButton.drop.creationTime = System.currentTimeMillis();
                        selectedButton.drop.pixelGroup.setWhiteToColor(
                                selectedButton.drop.component.r,
                                selectedButton.drop.component.g,
                                selectedButton.drop.component.b
                        );
                    }

                    heldDropButton.drop.component = t;
                    if(heldDropButton.drop.component != null)
                    {
                        heldDropButton.drop.creationTime = System.currentTimeMillis();
                        heldDropButton.drop.pixelGroup.setWhiteToColor(
                                heldDropButton.drop.component.r,
                                heldDropButton.drop.component.g,
                                heldDropButton.drop.component.b
                        );
                    }
                }
                else
                {
                    selectedButton.drop = heldDropButton.drop;

                    heldDropButton.drop = null;
                }
            }
        }

        heldDropButton = null;

        if (exitButton.cursorOnButton)
        {
            exitFlag = true;
        }
    }

    public boolean checkPause()
    {
        if (resumeButton.cursorOnButton)
        {

            for(int g = 0; g < player.gunDrops.length; g++)
            {
                player.gunDrops[g] = playerGuns[g].drop;
            }

            for(int m = 0; m < player.getMaxMods(); m++)
            {
                player.mods[m] = playerMods[m].drop;
                if(player.mods[m] != null && player.mods[m].component != null)
                {
                    for(int g = 0; g < player.gunDrops.length; g++)
                    {
                        player.gunDrops[g] = ((ModComponent)player.mods[m].component).modifyGun(player.gunDrops[g]);
                    }
                }
            }
            //player.modUpdate = true;

            for(int t = 0; t < player.thrusters.length; t++)
            {
                player.thrusters[t] = playerThrusters[t].drop;
            }

            for(Button[] row: componentPanel)
            {
                for(Button b: row)
                {
                    if(b.drop != null)
                    {
                        b.drop.held = false;
                    }
                }
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    public void setScale(float xS, float yS)
    {
        xScale = xS;
        yScale = yS;
        resumeButton.applyScale(xS, yS);
        exitButton.applyScale(xS, yS);
        optionsButton.applyScale(xS, yS);
    }

    public void setMovementDown(boolean b)
    {
        movementDown = b;
    }

    public void setShootingDown(boolean b)
    {
        shootingDown = b;
    }

    public void freeMemory()
    {

    }

    public void setDropsInRange(Drop[] drops)
    {
        int i = 0;
        for (Button[] bA : componentPanel)
        {
            for (Button b : bA)
            {
                b.drop = null;
                if (drops[i] != null)
                {
                    b.drop = drops[i];
                }
                i++;
            }
        }
        for(int g = 0; g < player.gunDrops.length; g++)
        {
            playerGuns[g].drop = player.gunDrops[g];
        }

        for(int m = 0; m < player.getMaxMods(); m++)
        {
            playerMods[m].drop = player.mods[m];
        }

        for(int t = 0; t < player.thrusters.length; t++)
        {
            playerThrusters[t].drop = player.thrusters[t];
        }
    }
}
