package com.example.sweet.Pixel_Invaders.UI_System;

import android.content.Context;
import android.graphics.PointF;

import com.example.sweet.Pixel_Invaders.Engine_Events.TextTypeEvent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Component;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Drop;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ModComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.ThrustComponent;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Drawable;
import com.example.sweet.Pixel_Invaders.Game_Objects.Player;
import com.example.sweet.Pixel_Invaders.Game_Objects.Rift;
import com.example.sweet.Pixel_Invaders.Util.Resource_Readers.TextureLoader;
import com.example.sweet.Pixel_Invaders.Util.Static.VectorFunctions;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.GunComponent;
import com.example.sweet.Pixel_Invaders.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.background;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.earthVA;
import static com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants.moonVA;

/**
 * Created by Sweet on 2/14/2018.
 */

public class UI extends Drawable
{
    private double globalStartTime;
    private float joyStickRadius = .32f;

    public PointF
            menuOnDown = new PointF(0, 0),
            menuOnMove = new PointF(0, 0),
            movementOnDown = new PointF(0, 0),
            movementOnMove = new PointF(0, 0),
            shootingOnDown = new PointF(0, 0),
            shootingOnMove = new PointF(0, 0);

    private boolean
            movementDown = false,
            shootingDown = false;

    public boolean
            menuPointerDown = false,
            exitFlag = false,
            newGameFlag = false,
            changeParticlesFlag = false,
            pauseFlag = false,
            readyFlag = false,
            displayReady = false;

    public boolean intro = true;
    public boolean introRevTime = false;
    private float introTimeStart;
    private float introTimeMod;

    public boolean titleIntro = true;
    private double titleIntroStart;
    private float
            titleIntroLengh = 2000f,
            titleIntroButtonY = -.2f,
            titleBackgroundY = .5f;

    private boolean settingsChanged = false;

    public float
            xScale,
            yScale,
            uiAvgFrame,
            collisionAvgFrame,
            aiAvgFrame;

    public int
            difficulty,
            uiShaderProgram,
            pixelShaderProgram,
            whiteTexture,
            uTextureLocation,
            magLoc,
            alphaLoc;

    private float flash = 0;
    private float flashSwitch = .5f;
    private float flashDelay = 1000;
    private long lastFlash;

    public volatile Constants.GameState gameState = Constants.GameState.MAIN_MENU;

    public Constants.GameState prevGameState = Constants.GameState.MAIN_MENU;

    private ImageContainer
            joyBaseMove,
            joyStickMove,
            joyBaseShoot,
            joyStickShoot,
            screenShade,
            allInfoBox,
            modPanel,
            gunPanel,
            thrusterPanel,
            optionsMenu,
            shadeBar,
            fireRate,
            fireRateDesc,
            extraShots,
            extraShotsDesc,
            precision,
            precisionDesc,
            plating,
            platingDesc,
            piercing,
            piercingDesc,
            temporal,
            temporalDesc,
            bulletSpeed,
            bulletSpeedDesc,
            restoration,
            restorationDesc,
            title,
            title1,
            title2,
            title3,
            title4,
            cannotPlace,
            riftShade;

    private Button
            resumeButton,
            optionsButton,
            optionsButton2,
            exitButton,
            arcadeButton,
            challengeButton,
            shipLogsButton,
            backButton,
            mainMenuButton,
            mainMenuButton2,
            pauseButton,
            readyButton,
            heldDropButton,
            selectedButton = null;

    private ImageContainer
            earthC,
            moonC,
            stars1,
            stars2,
            stars3,
            stars4;

    private Button[][] componentPanel = new Button[3][2];

    private Button[] slideButtons = new Button[4];
    private Button[] checkButtons = new Button[3];

    private float
            playerModelX = 0.08f,
            playerModelY = -.672f,
            mag = 2.8f;

    public Player player;

    private float u = .024f;
    private Button[] playerGuns = new Button[3];
    private PointF[] gunOffsets = new PointF[]{
            new PointF(0 + u, 0f * mag),
            new PointF(.056f * mag + u, -.08f * mag),
            new PointF(.056f * mag + u, .08f * mag)
    };

    private Button[] playerThrusters = new Button[3];

    private float tempOff = -.132f + .04f;
    private PointF[] thrusterOffsets = new PointF[]{
            new PointF(tempOff * mag, -.104f * mag),
            new PointF(tempOff * mag, 0f * mag),
            new PointF(tempOff * mag, .104f * mag)
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

    private GlobalInfo globalInfo;

    private ParticleSystem uiParticles;

    private TextTypeEvent
            riftDetected,
            onceMore;

    public UI(Context context, int shaderLocation, GlobalInfo globalInfo, ParticleSystem ps)
    {
        this.globalInfo = globalInfo;
        globalStartTime = System.currentTimeMillis();
        lastFlash = System.currentTimeMillis();
        uiParticles = ps;

        riftDetected = new TextTypeEvent("Rift Detected...", .5f, true, 3000);
        onceMore = new TextTypeEvent("Once More", 1.5f, false, 3000);

        alphaLoc = glGetUniformLocation(shaderLocation, "alpha");
        magLoc = glGetUniformLocation(shaderLocation, "mag");
        init(context,shaderLocation);
    }

    private void moveJoySticks()
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
        if (gameState == Constants.GameState.IN_GAME || gameState == Constants.GameState.PAUSE_MENU ||
                (gameState == Constants.GameState.OPTIONS && prevGameState == Constants.GameState.PAUSE_MENU))
        {
            //riftShade.setLoc(player.rift.x, player.rift.y);
            if(!intro)
            {
                if (player.rift.getRadius() > 0)
                {
                    if (!riftDetected.getStarted())
                    {
                        riftDetected.startTypeEvent(globalInfo);
                    }
                    glUniform1f(magLoc, player.rift.getRadius());
                    riftShade.draw(player.rift.getX() - globalInfo.getScreenShiftX(),
                            player.rift.getY() - globalInfo.getScreenShiftY());
                }
                else
                {
                    riftDetected.setStarted(false);
                }
                if (player.rift.riftState == Rift.RiftState.ACTIVATED)
                {
                    glUniform1f(magLoc, player.rift.getActivatedMag());
                    riftShade.draw(player.getPixelGroup().getCenterX() - globalInfo.getScreenShiftX(),
                            player.getPixelGroup().getCenterY() - globalInfo.getScreenShiftY());
                }

                glUniform1f(magLoc, .8f);
                riftDetected.drawTyping(textPresenter, .6f, 0f, .8f, globalInfo);
                glUniform1f(magLoc, 1f);
            }

        }
        if (gameState == Constants.GameState.IN_GAME)
        {
            if(intro)
            {
                //if(!introRevTime)
                if(!onceMore.getEnded())
                {
                    float progress = onceMore.getEventProgressRealTime();
                    glUniform1f(magLoc, 1f);
                    glUniform1f(alphaLoc, 1 - progress * progress * progress);
                    screenShade.draw();
                    glUniform1f(alphaLoc, 1f);
                    onceMore.drawTypingRealTime(textPresenter, .34f, .208f, 1f);
                   /* for(int i = 0; i < 4; i++)
                    {
                        player.addParticleToCenter();
                    }*/
                    /*if((int)(progress*1000) % 10 == 0)
                    {
                        player.addParticleCircleToCenter((1 - progress)*1.5f + 1f);
                    }*/
                    //System.out.println(progress);
                    if (progress > .6f && !introRevTime)
                    {
                        introRevTime = true;
                        globalInfo.setTimeSlow(introTimeMod);
                    }
                }
                if(introRevTime)
                {
                    introTimeMod = -(1 - 1 / (float)Math.pow(Math.abs(globalInfo.getAugmentedTimeMillis() - introTimeStart), .05)) * 3;
                    if(introTimeMod > -.1)
                    {
                        introTimeMod = -.1f;
                    }
                    //System.out.println(introTimeMod);
                    globalInfo.setTimeSlow(introTimeMod);
                    if (globalInfo.getAugmentedTimeMillis() <= introTimeStart)
                    {
                        globalInfo.setTimeSlow(1);
                        player.getParticleSystem().clear(0, player.getPixelGroup().totalPixels);
                        intro = false;
                    }
                }

            }
            else
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
                if (!globalInfo.gameSettings.doubleTapPause)
                {
                    pauseButton.draw(movementOnMove.x, movementOnMove.y, shootingOnMove.x, shootingOnMove.y);
                }
                if (displayReady)
                {
                    glUniform1f(magLoc, .8f);
                    readyButton.draw(movementOnMove.x, movementOnMove.y, shootingOnMove.x, shootingOnMove.y);
                    glUniform1f(magLoc, 1f);
                }
                textPresenter.drawInt(player.score, .9f, 0f, true);
            }

            //textPresenter.drawString("Hello World", 0, 0, 11);
        }
        else if (gameState == Constants.GameState.PAUSE_MENU)
        {
            screenShade.draw();
            //player.uiCosmetics(.5f);
            glUseProgram(pixelShaderProgram);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, whiteTexture);
            glUniform1i(uTextureLocation, 0);
            player.getPixelGroup().softDraw(playerModelX + player.driftX,
                    playerModelY + player.driftY,
                    1,0,
                    player.tiltAngle,
                    mag,
                    globalInfo.pointSize * mag);

            for (int g = 0; g < player.getMaxGuns(); g++)
            {
                if (playerGuns[g].drop != null && playerGuns[g] != heldDropButton)
                {
                    playerGuns[g].drop.menuDraw(
                            playerGuns[g].regular.x,
                            playerGuns[g].regular.y,
                            1f,
                            globalInfo.pointSize
                    );
                }
            }

            for (int m = 0; m < player.getMaxMods(); m++)
            {
                if (playerMods[m].drop != null && playerMods[m] != heldDropButton)
                {
                    playerMods[m].drop.menuDraw(
                            playerMods[m].regular.x,
                            playerMods[m].regular.y,
                            1f,
                            globalInfo.pointSize
                    );
                }
            }

            for (int t = 0; t < player.thrusters.length; t++)
            {
                if (playerThrusters[t].drop != null && playerThrusters[t] != heldDropButton)
                {
                    playerThrusters[t].drop.menuDraw(
                            playerThrusters[t].regular.x,
                            playerThrusters[t].regular.y,
                            1f,
                            globalInfo.pointSize
                    );
                }
            }

            if (heldDropButton != null)
            {
                heldDropButton.drop.menuDraw(menuOnMove.x, -menuOnMove.y, 2f, globalInfo.pointSize * 2f);
            }

            for (Button[] bA : componentPanel)
            {
                for (Button b : bA)
                {
                    if (b.drop != null && b != heldDropButton)
                    {
                        b.drop.menuDraw(b.regular.x, b.regular.y, 1, globalInfo.pointSize);
                    }
                }
            }

            if (selectedButton != null)
            {
                if (selectedButton.drop != null)
                {
                    if (selectedButton.drop.component.type == Constants.DropType.GUN)
                    {
                        ((GunComponent) selectedButton.drop.component).gun.getTemplate().softDraw(
                                Constants.gpBulletX,
                                Constants.gpBulletY,
                                1, 0, 0, 1, globalInfo.pointSize
                        );
                    }
                }
            }

            glUseProgram(uiShaderProgram);
            allInfoBox.draw();
            resumeButton.draw(menuOnMove.x, menuOnMove.y);
            optionsButton.draw(menuOnMove.x, menuOnMove.y);
            mainMenuButton.draw(menuOnMove.x, menuOnMove.y);


            if (selectedButton != null &&
                    selectedButton.drop != null &&
                    selectedButton.drop.component != null)
            {
                Component c = selectedButton.drop.component;
                switch (c.type)
                {
                    case THRUSTER:
                        thrusterPanel.draw();
                        textPresenter.drawInt(
                                (int)(((ThrustComponent) c).getMaxThrustPower()*100f),
                                Constants.iPnum1X, Constants.infoPanelY,
                                false
                        );
                        break;
                    case GUN:
                        gunPanel.draw();
                        GunComponent gC = (GunComponent)c;
                        textPresenter.drawInt(
                                (int) gC.gun.getShotDelay(),
                                Constants.iPnum1X, Constants.infoPanelY,
                                false
                        );
                        textPresenter.drawInt(
                                gC.gun.getTemplate().totalPixels,
                                Constants.iPnum2X, Constants.infoPanelY,
                                false
                        );
                        textPresenter.drawInt(
                                (int) (gC.gun.getTemplate().totalPixels *
                                        (1000 / gC.gun.getShotDelay())),
                                Constants.iPnum3X, Constants.infoPanelY,
                                false
                        );
                        break;
                    case MOD:
                        modPanel.draw();
                        break;
                    case FIRE_RATE:
                        modPanel.draw();
                        fireRate.draw();
                        fireRateDesc.draw();
                        textPresenter.drawInt(
                                ((ModComponent) c).getModLevel(),
                                Constants.iPnum3X, Constants.infoPanelY,
                                false
                        );
                        break;
                    case BULLET_SPEED:
                        modPanel.draw();
                        bulletSpeed.draw();
                        bulletSpeedDesc.draw();
                        textPresenter.drawInt(
                                ((ModComponent) c).getModLevel(),
                                Constants.iPnum3X, Constants.infoPanelY,
                                false
                        );
                        break;
                    case EXTRA_SHOTS:
                        modPanel.draw();
                        extraShots.draw();
                        extraShotsDesc.draw();
                        textPresenter.drawInt(
                                ((ModComponent) c).getModLevel(),
                                Constants.iPnum3X, Constants.infoPanelY,
                                false
                        );
                        break;
                    case PRECISION:
                        modPanel.draw();
                        precision.draw();
                        precisionDesc.draw();
                        textPresenter.drawInt(
                                ((ModComponent) c).getModLevel(),
                                Constants.iPnum3X, Constants.infoPanelY,
                                false
                        );
                        break;
                    case PLATING:
                        modPanel.draw();
                        plating.draw();
                        platingDesc.draw();
                        textPresenter.drawInt(
                                ((ModComponent) c).getModLevel(),
                                Constants.iPnum3X, Constants.infoPanelY,
                                false
                        );
                        break;
                    case PIERCING:
                        modPanel.draw();
                        piercing.draw();
                        piercingDesc.draw();
                        textPresenter.drawInt(
                                ((ModComponent) c).getModLevel(),
                                Constants.iPnum3X, Constants.infoPanelY,
                                false
                        );
                        break;
                    case RESTORATION:
                        modPanel.draw();
                        restoration.draw();
                        restorationDesc.draw();
                        textPresenter.drawInt(
                                ((ModComponent) c).getModLevel(),
                                Constants.iPnum3X, Constants.infoPanelY,
                                false
                        );
                        break;
                    case TEMPORAL:
                        modPanel.draw();
                        temporal.draw();
                        temporalDesc.draw();
                        textPresenter.drawInt(
                                ((ModComponent) c).getModLevel(),
                                Constants.iPnum3X, Constants.infoPanelY,
                                false
                        );
                        break;
                }
            }
            Constants.DropType dType;
            if(heldDropButton != null && heldDropButton.drop != null)
            {
                dType = heldDropButton.drop.component.type;
            }
            else
            {
                dType = Constants.DropType.NONE;
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
                if(dType == Constants.DropType.GUN || dType == Constants.DropType.NONE)
                {
                    playerGuns[g].draw(menuOnMove.x, menuOnMove.y);
                }
                else
                {
                    cannotPlace.draw(playerGuns[g].getX(), playerGuns[g].getY());
                }
            }

            for (Button b : playerThrusters)
            {
                if(dType == Constants.DropType.THRUSTER || dType == Constants.DropType.NONE)
                {
                    b.draw(menuOnMove.x, menuOnMove.y);
                }
                else
                {
                    cannotPlace.draw(b.getX(), b.getY());
                }
            }

            for (int m = 0; m < player.getMaxMods(); m++)
            {

                if((dType != Constants.DropType.THRUSTER && dType != Constants.DropType.GUN) ||
                        dType == Constants.DropType.NONE)
                {
                    playerMods[m].draw(menuOnMove.x, menuOnMove.y);
                }
                else
                {
                    cannotPlace.draw(playerMods[m].getX(), playerMods[m].getY());
                }

            }

            textPresenter.drawInt(player.score, -.86f, -.006f, false);
            textPresenter.drawInt(difficulty, -.86f, -1.26f, false);
        }
        else if (gameState == Constants.GameState.MAIN_MENU)
        {
            glUseProgram(uiShaderProgram);
            //shadeBar.draw();
            /*if(System.currentTimeMillis() - lastFlash > flashDelay)
            {
                flash += flashSwitch;
                if(flash <= .05f)
                {
                    flashDelay = (float)Math.random()*5000f + 1000f;
                    lastFlash = System.currentTimeMillis();
                }
            }*/
            if(titleIntro)
            {
                float progress = (float)(System.currentTimeMillis() - titleIntroStart) / titleIntroLengh;
                if(progress <= .1f)
                {
                    screenShade.draw();
                }
                else if(progress > .1 && progress <= .4f)
                {
                    float locProg = (progress - .1f) / .3f;
                    locProg *= locProg * locProg;
                    glUniform1f(alphaLoc, 1 - locProg);
                    screenShade.draw();
                    glUniform1f(alphaLoc, locProg);
                    drawTitleHelper(0, titleBackgroundY);
                    glUniform1f(alphaLoc, 1);
                }
                else if(progress > .4f && progress <= .7f)
                {
                    float locProg = (progress - .4f) / .3f;
                    locProg *= locProg;
                    titleBackgroundY = .5f * (1 - locProg);
                    drawTitleHelper(0, titleBackgroundY);
                }
                else if(progress > .7f && progress < .8f)
                {
                    float locProg = (progress - .7f) / .1f;
                    locProg *= locProg * locProg;
                    glUniform1f(alphaLoc, locProg);
                    arcadeButton.drawShift(0, (1 - locProg) * titleIntroButtonY);
                    exitButton.drawShift(0, (1 - locProg) * titleIntroButtonY);
                    glUniform1f(alphaLoc, 1);
                    drawTitleHelper(0, 0);
                }
                else if(progress > .8f && progress < .9f)
                {
                    float locProg = (progress - .8f) / .1f;
                    locProg *= locProg * locProg;
                    glUniform1f(alphaLoc, locProg);
                    challengeButton.drawShift(0, (1 - locProg) * titleIntroButtonY);
                    optionsButton2.drawShift(0, (1 - locProg) * titleIntroButtonY);
                    glUniform1f(alphaLoc, 1);
                    arcadeButton.draw(menuOnMove.x, menuOnMove.y);
                    exitButton.draw(menuOnMove.x, menuOnMove.y);
                    drawTitleHelper(0, 0);
                }
                else if(progress > .9f && progress < 1f)
                {
                    float locProg = (progress - .9f) / .1f;
                    locProg *= locProg * locProg;
                    glUniform1f(alphaLoc, locProg);
                    shipLogsButton.drawShift(0, (1 - locProg) * titleIntroButtonY);
                    glUniform1f(alphaLoc, 1);
                    arcadeButton.draw(menuOnMove.x, menuOnMove.y);
                    challengeButton.draw(menuOnMove.x, menuOnMove.y);
                    optionsButton2.draw(menuOnMove.x, menuOnMove.y);
                    exitButton.draw(menuOnMove.x, menuOnMove.y);
                    drawTitleHelper(0, 0);
                }
                else if(progress >= 1f)
                {
                    titleIntro = false;
                }
            }

            if(!titleIntro)
            {
                arcadeButton.draw(menuOnMove.x, menuOnMove.y);
                challengeButton.draw(menuOnMove.x, menuOnMove.y);
                shipLogsButton.draw(menuOnMove.x, menuOnMove.y);
                optionsButton2.draw(menuOnMove.x, menuOnMove.y);
                exitButton.draw(menuOnMove.x, menuOnMove.y);

                drawTitleHelper(0,0);
                /*switch ((int) flash)
                {
                    case 0:
                        title.draw();
                        break;
                    case 1:
                        title1.draw();
                        break;
                    case 2:
                        title2.draw();
                        break;
                    case 3:
                        title3.draw();
                        break;
                    case 4:
                        title4.draw();
                        break;
                }
                if (flash <= .05)
                {
                    flashSwitch = .5f;
                }
                else if (flash >= 5)
                {
                    flashSwitch = -.5f;
                }*/
            }
        }
        else if (gameState == Constants.GameState.OPTIONS)
        {
            glUseProgram(uiShaderProgram);
            screenShade.draw();
            optionsMenu.draw();
            backButton.draw(menuOnMove.x, menuOnMove.y);
            if (menuPointerDown)
            {
                int index = 0;
                for (Button b : slideButtons)
                {
                    if (b.touchedOnDown)
                    {
                        b.drawHighlight();
                        if (menuOnMove.y > Constants.slideMax)
                        {
                            b.setY(-Constants.slideMax);
                        }
                        else if (menuOnMove.y < Constants.slideMin)
                        {
                            b.setY(-Constants.slideMin);
                        }
                        else
                        {
                            b.setY(-menuOnMove.y);
                        }
                        float percent = (-b.regular.y - Constants.slideMin) / (Constants.slideMax - Constants.slideMin);
                        globalInfo.gameSettings.setSlideSetting(index, percent);
                        settingsChanged = true;
                    }
                    else
                    {
                        b.draw(menuOnMove.x, menuOnMove.y);
                    }
                    index++;
                }
            }
            else
            {
                for (Button b : slideButtons)
                {
                    b.draw(menuOnMove.x, menuOnMove.y);
                }
            }

            for (int i = 0; i < checkButtons.length; i++)
            {
                if (globalInfo.gameSettings.getToggleSetting(i))
                {
                    checkButtons[i].draw(menuOnMove.x, menuOnMove.y);
                }
                else
                {
                    checkButtons[i].checkOnMove(menuOnMove.x, menuOnMove.y);
                }
            }

            textPresenter.drawInt((int) (globalInfo.gameSettings.particlePercent * 100), .348f, -1.224f, false);
            textPresenter.drawInt((int) (globalInfo.gameSettings.screenShakePercent * 100), -.044f, -1.224f, false);
            textPresenter.drawInt((int) (globalInfo.gameSettings.musicPercent * 100), -.436f, -1.224f, false);
            textPresenter.drawInt((int) (globalInfo.gameSettings.soundPercent * 100), -.828f, -1.224f, false);
        }
        else if(gameState == Constants.GameState.GAME_OVER)
        {
            screenShade.draw();
            mainMenuButton2.draw(menuOnMove.x, menuOnMove.y);
            textPresenter.drawInt(player.score, -.2f, 0, true);
        }

        if(gameState != Constants.GameState.IN_GAME)
        {
            if(menuPointerDown)
            {
                addParticleCircle(
                        menuOnMove.x,
                        -menuOnMove.y,
                        .03f,
                        .3f,
                        .05f,
                        -1f
                );
                /*int len = (int)(Math.random()*5) + 1;
                for(int i = 0; i < len; i++)
                {
                    uiParticles.addParticle(
                            menuOnMove.x,
                            -menuOnMove.y,
                            (float)Math.random()*Constants.twoPI,
                            0,
                            (float)Math.random()*.8f,
                            1,
                            1,
                            (float)(Math.random())+.1f,
                            (float)(Math.random()*.3)+.01f,
                            (float)(Math.random()*40)-20,
                            true
                    );
                }*/
            }
        }

        if (globalInfo.gameSettings.showFps)
        {
            textPresenter.drawInt((int)uiAvgFrame, .9f, -.6f, false);
            textPresenter.drawInt((int)aiAvgFrame, .9f, -.9f, false);
            textPresenter.drawInt((int)collisionAvgFrame, .9f, -1.2f, false);
        }
    }

    public void pointerDown()
    {
        if (gameState == Constants.GameState.PAUSE_MENU)
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
        else if(gameState == Constants.GameState.OPTIONS)
        {
            for(Button b: slideButtons)
            {
                b.checkOnDown(menuOnDown.x, menuOnDown.y);
            }

            for(Button b: checkButtons)
            {
                b.checkOnDown(menuOnDown.x, menuOnDown.y);
            }
        }
    }

    public void pointerUp()
    {
        if(gameState == Constants.GameState.IN_GAME)
        {
            if(!globalInfo.gameSettings.doubleTapPause)
            {
                if(pauseButton.cursorOnButton)
                {
                    pauseFlag = true;
                    pauseButton.cursorOnButton = false;
                }
            }
            if(displayReady)
            {
                if(readyButton.cursorOnButton)
                {
                    readyFlag = true;
                    readyButton.cursorOnButton = false;
                }
            }
        }
        else if(gameState == Constants.GameState.PAUSE_MENU)
        {
            for (Button b : allComponentButtons)
            {
                if (b.cursorOnButton)
                {
                    selectedButton = b;
                    break;
                }
            }

            if (heldDropButton != null &&
                    heldDropButton.drop != null &&
                    heldDropButton.drop.component != null)
            {
                if (selectedButton.type == Constants.DropType.ANY ||
                        selectedButton.type == heldDropButton.drop.component.type ||
                        (heldDropButton.drop.component.type != Constants.DropType.GUN &&
                                heldDropButton.drop.component.type != Constants.DropType.THRUSTER &&
                                selectedButton.type == Constants.DropType.MOD))
                {
                    if (selectedButton.drop != null)
                    {
                        Drop d = selectedButton.drop;
                        selectedButton.drop = heldDropButton.drop;
                        heldDropButton.drop = d;
                    }
                    else
                    {
                        selectedButton.drop = heldDropButton.drop;
                        heldDropButton.drop = null;
                    }
                }
            }

            heldDropButton = null;

            if(mainMenuButton.cursorOnButton)
            {
                startTitleIntro();
                gameState = Constants.GameState.MAIN_MENU;
                prevGameState = Constants.GameState.PAUSE_MENU;
                mainMenuButton.cursorOnButton = false;
            }
            else if (optionsButton.cursorOnButton)
            {
                prevGameState = Constants.GameState.PAUSE_MENU;
                gameState = Constants.GameState.OPTIONS;
                optionsButton.cursorOnButton = false;
            }
        }
        else if(gameState == Constants.GameState.OPTIONS)
        {
            if(backButton.cursorOnButton)
            {
                if(prevGameState == Constants.GameState.MAIN_MENU)
                {
                    gameState = Constants.GameState.MAIN_MENU;
                }
                else if(prevGameState == Constants.GameState.PAUSE_MENU)
                {
                    gameState = Constants.GameState.PAUSE_MENU;
                }
                prevGameState = Constants.GameState.OPTIONS;
                if(settingsChanged)
                {
                    changeParticlesFlag = true;
                    settingsChanged = false;
                }
                backButton.cursorOnButton = false;
            }
            for(int i = 0; i < checkButtons.length; i++)
            {
                if(checkButtons[i].cursorOnButton && checkButtons[i].touchedOnDown)
                {
                    globalInfo.gameSettings.toggleSetting(i);
                }
                checkButtons[i].touchedOnDown = false;
            }
        }
        else if(gameState == Constants.GameState.MAIN_MENU)
        {
            if(exitButton.cursorOnButton)
            {
                exitFlag = true;
            }
            else if(arcadeButton.cursorOnButton)
            {
                newGameFlag = true;
                arcadeButton.cursorOnButton = false;
            }
            else if(optionsButton2.cursorOnButton)
            {
                prevGameState = Constants.GameState.MAIN_MENU;
                gameState = Constants.GameState.OPTIONS;
                optionsButton2.cursorOnButton = false;
            }
        }
        else if(gameState == Constants.GameState.GAME_OVER)
        {
            if (mainMenuButton2.cursorOnButton)
            {
                riftDetected.setEnded(true);
                startTitleIntro();
                gameState = Constants.GameState.MAIN_MENU;
                prevGameState = Constants.GameState.GAME_OVER;
                mainMenuButton2.cursorOnButton = false;
            }
        }
    }

    public void drawBackground()
    {
        float tShiftX = 0;
        float tShiftY = 0;

        if(gameState == Constants.GameState.MAIN_MENU ||
                (gameState == Constants.GameState.OPTIONS && prevGameState == Constants.GameState.MAIN_MENU))
        {
            tShiftX = (float) Math.cos((System.currentTimeMillis() - globalStartTime) / 4000) * .06f - .588f;
            tShiftY = (float) Math.sin((System.currentTimeMillis() - globalStartTime) / 4000) * .2f - 1f + titleBackgroundY * 2;
           /* if(!titleIntro)
            {
                tShiftX = (float) Math.cos((System.currentTimeMillis() - globalStartTime) / 4000) * .06f - .588f;
                tShiftY = (float) Math.sin((System.currentTimeMillis() - globalStartTime) / 4000) * .2f - 1f + titleBackgroundY * 2;
            }
            else
            {
                tShiftX =  - .588f;
                tShiftY =  - 1f + titleBackgroundY * 2;
            }*/
        }
        else
        {
            if(player != null)
            {
                tShiftX = -(player.xScreenShift - player.screenShakeX);
                tShiftY = -(player.yScreenShift - player.screenShakeY);
            }
        }

        stars4.draw(tShiftX / 6f, tShiftY / 6f);
        stars3.draw(tShiftX / 4.6f, tShiftY / 4.6f);
        stars2.draw(tShiftX / 4f, tShiftY / 4f);
        stars1.draw(tShiftX / 3.4f, tShiftY / 3.4f);
        moonC.draw(tShiftX / 2.1f, tShiftY / 2.1f);
        earthC.draw(tShiftX / 2, tShiftY / 2);
    }

    public void startIntro()
    {
        intro = true;
        introRevTime = false;
        introTimeMod = -2.4f;
        float m = .04f;
        for(float d = 0; d < 2f; d += m)
        {
            player.addParticleCircleToCenter(.5f + d, (float)Math.sqrt(d), d);
            m *= 1.1;
        }
        introTimeStart = globalInfo.getAugmentedTimeMillis();
        onceMore.startTypeEventRealTime();
    }

    public void startTitleIntro()
    {
        titleIntro = true;
        titleBackgroundY = .5f;
        titleIntroStart = System.currentTimeMillis();
    }

    public boolean checkPause()
    {
        if (resumeButton.cursorOnButton)
        {
            for(int g = 0; g < playerGuns.length; g++)
            {
                player.setGun(playerGuns[g].drop, g);
            }

            for(int m = 0; m < player.getMaxMods(); m++)
            {
                player.mods[m] = playerMods[m].drop;
            }

            for(int t = 0; t < player.thrusters.length; t++)
            {
                player.setThruster(playerThrusters[t].drop, t);
            }

            player.applyMods();

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

            gameState = Constants.GameState.IN_GAME;
            prevGameState = Constants.GameState.PAUSE_MENU;
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
        mainMenuButton.applyScale(xS, yS);
        optionsButton.applyScale(xS, yS);
        backButton.applyScale(xS, yS);
    }

    public void setMovementDown(boolean b)
    {
        movementDown = b;
    }

    public void setShootingDown(boolean b)
    {
        shootingDown = b;
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

    private void addParticleCircle(float x, float y, float rad, float spd, float distance, float angMod)
    {
        if(spd == -1)
        {
            spd = (float) (Math.random() * .05f) + .05f;
        }
        if(distance == -1)
        {
            distance = (float) (Math.random() * .2) + .1f;
        }
        float g = (float)Math.random();
        for(float angle = 0; angle < Constants.twoPI;  angle += .02f)
        {
            float xT = (float) Math.cos(angle) * rad + x;
            float yT = (float) Math.sin(angle) * rad + y;
            uiParticles.addParticle(
                    xT,
                    yT,
                    angle,
                    0,
                    g,
                    1,
                    .1f,
                    spd,
                    distance,
                    (float) (Math.random() * 40) - 20,
                    true
            );
        }
    }

    private void drawTitleHelper(float x, float y)
    {
        if(System.currentTimeMillis() - lastFlash > flashDelay)
        {
            flash += flashSwitch;
            if(flash <= .05f)
            {
                flashDelay = (float)Math.random()*5000f + 1000f;
                lastFlash = System.currentTimeMillis();
            }
        }
        switch ((int) flash)
        {
            case 0:
                title.draw(x, y);
                break;
            case 1:
                title1.draw(x, y);
                break;
            case 2:
                title2.draw(x, y);
                break;
            case 3:
                title3.draw(x, y);
                break;
            case 4:
                title4.draw(x, y);
                break;
        }
        if (flash <= .05)
        {
            flashSwitch = .5f;
        }
        else if (flash >= 5)
        {
            flashSwitch = -.5f;
        }
    }

    public void reset()
    {

    }

    private void init(Context context, int shaderLocation)
    {
        int[] glVarLocations = new int[4];
        glVarLocations[0] = glGetUniformLocation(shaderLocation, "displacement");
        glVarLocations[1] = glGetAttribLocation(shaderLocation, "a_Position");
        glVarLocations[2] = glGetAttribLocation(shaderLocation, "a_TexCoordinate");
        glVarLocations[3] = glGetUniformLocation(shaderLocation, "u_Texture");
        textPresenter = new TextPresenter(context, glVarLocations);

        earthC = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.earth),
                        earthVA,
                        0, 0,
                        "earth",
                        glVarLocations,
                        -1, -1
                );

        moonC = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.moon),
                        moonVA,
                        -.2f, -.34f,
                        "earth",
                        glVarLocations,
                        -1, -1
                );
        stars1 = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.layer0),
                        background,
                        0, 0,
                        "",
                        glVarLocations,
                        -1, -1
                );
        stars2 = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.layer1),
                        background,
                        0, 0,
                        "",
                        glVarLocations,
                        -1, -1
                );
        stars3 = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.layer2),
                        background,
                        0, 0,
                        "",
                        glVarLocations,
                        -1, -1
                );
        stars4 = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.spacebackground),
                        //TextureLoader.loadTexture(context, R.drawable.white),
                        background,
                        0, 0,
                        "",
                        glVarLocations,
                        -1, -1
                );
        joyBaseMove = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.mjb),
                        Constants.joyBaseMoveVA,
                        0, 0,
                        "joyBaseMove",
                        glVarLocations,
                        -1, -1
                );
        joyStickMove = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.mj),
                        Constants.joyStickMoveVA,
                        0, 0,
                        "joyStickMove",
                        glVarLocations,
                        -1, -1
                );
        joyBaseShoot = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.sjb),
                        Constants.joyBaseShootVA,
                        0, 0,
                        "joyBaseShoot",
                        glVarLocations,
                        -1, -1
                );
        joyStickShoot = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.sj),
                        Constants.joyStickShootVA,
                        0, 0,
                        "joyStickShoot",
                        glVarLocations,
                        -1, -1
                );
        screenShade = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.shade),
                        Constants.screenShadeVA,
                        0, 0,
                        "screenShade",
                        glVarLocations,
                        -1, -1
                );

        riftShade = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.riftshade),
                        Constants.riftShadeVA,
                        0, 0,
                        "screenShade",
                        glVarLocations,
                        -1, -1
                );

        shadeBar = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.shade),
                        Constants.shadeBarVa,
                        0, .85f,
                        "screenShade",
                        glVarLocations,
                        -1, -1
                );

        modPanel = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.modpanel),
                        Constants.infoPanelVA,
                        -.024f + .02f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );

        fireRate = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.weaponsoverdrive),
                        Constants.modNameVA,
                        -.024f + .308f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );

        bulletSpeed = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.accelerator),
                        Constants.modNameVA,
                        -.024f + .308f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        extraShots = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.projectilecloner),
                        Constants.modNameVA,
                        -.024f + .308f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        precision = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.precisionbarrels),
                        Constants.modNameVA,
                        -.024f + .308f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        restoration = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.restorativecells),
                        Constants.modNameVA,
                        -.024f + .308f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        plating = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.armoredplating),
                        Constants.modNameVA,
                        -.024f + .308f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        piercing = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.piercingrounds),
                        Constants.modNameVA,
                        -.024f + .308f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        temporal = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.temporalmanipulator),
                        Constants.modNameVA,
                        -.024f + .308f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );

        fireRateDesc = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.overdrivedescrip),
                        Constants.modDescripVA,
                        -.284f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );

        bulletSpeedDesc = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.acceleratordescrip),
                        Constants.modDescripVA,
                        -.284f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        extraShotsDesc = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.projectileclonerdescrip),
                        Constants.modDescripVA,
                        -.284f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        precisionDesc = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.precisionbarrelsdescrip),
                        Constants.modDescripVA,
                        -.284f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        restorationDesc = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.restorativecellsdescrip),
                        Constants.modDescripVA,
                        -.284f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        platingDesc = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.pixelplatingdescrip),
                        Constants.modDescripVA,
                        -.284f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        piercingDesc = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.piercingroundsdescrip),
                        Constants.modDescripVA,
                        -.284f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );
        temporalDesc = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.temporaldescrip),
                        Constants.modDescripVA,
                        -.284f, .936f,
                        "modPanel",
                        glVarLocations,
                        -1, -1
                );

        gunPanel = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.gunpanel),
                        Constants.infoPanelVA,
                        -.024f + .02f, .936f,
                        "gunPanel",
                        glVarLocations,
                        -1, -1
                );
        thrusterPanel = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.thrusterpanel),
                        Constants.infoPanelVA,
                        -.024f + .02f, .936f,
                        "thrusterPanel",
                        glVarLocations,
                        -1, -1
                );
        allInfoBox = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.largebox2),
                        Constants.allInfoBoxVA,
                        -.024f - .144f + .02f, 0,
                        "largeBox",
                        glVarLocations,
                        -1, -1
                );
        optionsMenu = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.optionsmenu),
                        Constants.allInfoBoxVA,
                        -.024f - .144f + .02f, 0,
                        "largeBox",
                        glVarLocations,
                        -1, -1
                );

        float titleX = .32f;
        float titleY = -.5f;
        title = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.pixelinvaders),
                        Constants.titleVA,
                        titleX, titleY,
                        "largeBox",
                        glVarLocations,
                        -1, -1
                );
        title1 = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.pixelinvaders1),
                        Constants.titleVA,
                        titleX, titleY + .02f,
                        "largeBox",
                        glVarLocations,
                        -1, -1
                );
        title2 = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.pixelinvaders2),
                        Constants.titleVA,
                        titleX, titleY + .04f,
                        "largeBox",
                        glVarLocations,
                        -1, -1
                );
        title3 = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.pixelinvaders3),
                        Constants.titleVA,
                        titleX, titleY + .02f,
                        "largeBox",
                        glVarLocations,
                        -1, -1
                );
        title4 = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.pixelinvaders4),
                        Constants.titleVA,
                        //.35f, -.56f,
                        titleX, titleY + .04f,
                        "largeBox",
                        glVarLocations,
                        -1, -1
                );

        cannotPlace = new ImageContainer
                (
                        TextureLoader.loadTexture(context, R.drawable.squarec),
                        Constants.playerSquareVA,
                        //.35f, -.56f,
                        0, 0,
                        "largeBox",
                        glVarLocations,
                        -1, -1
                );

        resumeButton = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.resume),
                                        Constants.resumeButtonVA,
                                        .82f + .02f, .55f,
                                        "resumeButton",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.resume2),
                                        Constants.resumeButtonHoveredVA,
                                        .82f + .02f, .55f,
                                        "resumeButtonHover",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        null
                );

        int optionsText = TextureLoader.loadTexture(context, R.drawable.options);
        int optionsText2 = TextureLoader.loadTexture(context, R.drawable.options);
        optionsButton = new Button
                (
                        new ImageContainer
                                (
                                        optionsText,
                                        Constants.resumeButtonVA,
                                        .82f + .02f, 0,
                                        "optionsButton",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        new ImageContainer
                                (
                                        optionsText2,
                                        Constants.resumeButtonHoveredVA,
                                        .82f + .02f, 0,
                                        "optionsButtonHover",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        null
                );

        readyButton = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.ready),
                                        Constants.resumeButtonVA,
                                        -.85f, 0,
                                        "optionsButton",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.ready2),
                                        Constants.resumeButtonHoveredVA,
                                        -.85f, 0,
                                        "optionsButtonHover",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        null
                );

        int mainmenuTex = TextureLoader.loadTexture(context, R.drawable.mainmenu);
        int mainmenuTex2 = TextureLoader.loadTexture(context, R.drawable.mainmenu2);
        mainMenuButton = new Button
                (
                        new ImageContainer
                                (
                                        mainmenuTex,
                                        Constants.resumeButtonVA,
                                        .82f + .02f, -.55f,
                                        "exitButton",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        new ImageContainer
                                (
                                        mainmenuTex2,
                                        Constants.resumeButtonHoveredVA,
                                        .82f + .02f, -.55f,
                                        "exitButtonHover",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        null
                );

        mainMenuButton2 = new Button
                (
                        new ImageContainer
                                (
                                        mainmenuTex,
                                        Constants.resumeButtonVA,
                                        0, 0,
                                        "exitButton",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        new ImageContainer
                                (
                                        mainmenuTex2,
                                        Constants.resumeButtonHoveredVA,
                                        0, 0,
                                        "exitButtonHover",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        null
                );

        backButton = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.back),
                                        Constants.resumeButtonVA,
                                        .82f + .02f, -.55f,
                                        "exitButton",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.back2),
                                        Constants.resumeButtonHoveredVA,
                                        .82f + .02f, -.55f,
                                        "exitButtonHover",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        null
                );

        float shiftUp = 0f;
        float xScaleB = .3f;
        float group1y = .50f;
        float group2y = .66f;
        float group3y = .75f;
        arcadeButton = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.arcade),
                                        Constants.resumeButtonVA,
                                        //.4f, .65f,
                                        xScaleB * 2 + shiftUp, group1y,
                                        "optionsButton",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.arcade2),
                                        Constants.resumeButtonHoveredVA,
                                        //.4f, .65f,
                                        xScaleB * 2 + shiftUp, group1y,
                                        "optionsButtonHover",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        null
                );

        challengeButton = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.challenge),
                                        Constants.resumeButtonVA,
                                        //.4f, .65f,
                                        xScaleB + shiftUp, group2y,
                                        "optionsButton",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.challenge2),
                                        Constants.resumeButtonHoveredVA,
                                        //.4f, .65f,
                                        xScaleB + shiftUp, group2y,
                                        "optionsButtonHover",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        null
                );

        shipLogsButton = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.shiplogs),
                                        Constants.resumeButtonVA,
                                        //.4f, .65f,
                                        shiftUp, group3y,
                                        "optionsButton",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.shiplogs2),
                                        Constants.resumeButtonHoveredVA,
                                        //.4f, .65f,
                                        shiftUp, group3y,
                                        "optionsButtonHover",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        null
                );

        optionsButton2 = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.optionss),
                                        Constants.resumeButtonVA,
                                        //0f, .75f,
                                        -xScaleB + shiftUp, group2y,
                                        "optionsButton",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.optionss2),
                                        Constants.resumeButtonHoveredVA,
                                        //0f, .75f,
                                        -xScaleB + shiftUp, group2y,
                                        "optionsButtonHover",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        null
                );

        exitButton = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.exit),
                                        Constants.resumeButtonVA,
                                        //-.4f, .65f,
                                        -xScaleB * 2 + shiftUp, group1y,
                                        "optionsButton",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.exit2),
                                        Constants.resumeButtonHoveredVA,
                                        //-.4f, .65f,
                                        -xScaleB * 2 + shiftUp, group1y,
                                        "optionsButtonHover",
                                        glVarLocations,
                                        Constants.rbL, Constants.rbW
                                ),
                        null
                );

        pauseButton = new Button
                (
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.pause),
                                        Constants.pauseVA,
                                        .8f, 1f,
                                        "optionsButton",
                                        glVarLocations,
                                        Constants.paL, Constants.paW
                                ),
                        new ImageContainer
                                (
                                        TextureLoader.loadTexture(context, R.drawable.pause2),
                                        Constants.pauseVA,
                                        .8f, 1f,
                                        "optionsButtonHover",
                                        glVarLocations,
                                        Constants.paL, Constants.paW
                                ),
                        null
                );

        int slideTexture = TextureLoader.loadTexture(context, R.drawable.slidebutton);
        int slideTexture2 = TextureLoader.loadTexture(context, R.drawable.slidebutton2);

        for(int i = 0; i < slideButtons.length; i++)
        {
            slideButtons[i] = new Button
                    (
                            new ImageContainer
                                    (
                                            slideTexture,
                                            Constants.slideButtonVA,
                                            .332f + .02f - .392f * i, -.992f,
                                            "optionsButton",
                                            glVarLocations,
                                            Constants.sldBR, Constants.sldBR
                                    ),
                            new ImageContainer
                                    (
                                            slideTexture2,
                                            Constants.slideButtonVA,
                                            .332f + .02f - .392f * i, -.992f,
                                            "optionsButtonHover",
                                            glVarLocations,
                                            Constants.sldBR, Constants.sldBR
                                    ),
                            null
                    );
        }

        int checkTexture = TextureLoader.loadTexture(context, R.drawable.check);
        for(int i = 0; i < checkButtons.length; i++)
        {
            checkButtons[i] = new Button
                    (
                            new ImageContainer
                                    (
                                            checkTexture,
                                            Constants.checkVA,
                                            .53f - .192f * i, .16f,
                                            "optionsButton",
                                            glVarLocations,
                                            Constants.chckR, Constants.chckR
                                    ),
                            new ImageContainer
                                    (
                                            checkTexture,
                                            Constants.checkVA,
                                            .53f - .192f * i, .16f,
                                            "optionsButtonHover",
                                            glVarLocations,
                                            Constants.chckR, Constants.chckR
                                    ),
                            null
                    );
        }

        int itrAllCompButtons = 0;
        int squareTexture = TextureLoader.loadTexture(context, R.drawable.square);
        float midX = .429f + .02f + .048f;
        float midY = .098f;
        for (int r = 0; r < componentPanel.length; r++)
        {
            float xL = midX - (r + 1) * (Constants.radius * 2 + .048f);
            for (int c = 0; c < componentPanel[0].length; c++)
            {
                float yL = midY + c * (Constants.radius * 2 + .048f);
                componentPanel[r][c] = new Button
                        (
                                new ImageContainer
                                        (
                                                squareTexture,
                                                Constants.componentSquareVA,
                                                xL, yL,
                                                "square",
                                                glVarLocations,
                                                Constants.radius, Constants.radius
                                        ),
                                new ImageContainer
                                        (
                                                squareTexture,
                                                Constants.componentSquareHoverVA,
                                                xL, yL,
                                                "squareHover",
                                                glVarLocations,
                                                Constants.radius, Constants.radius
                                        ),
                                Constants.DropType.ANY
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
                                            Constants.playerSquareVA,
                                            playerModelX + gunOffsets[i].x, playerModelY + gunOffsets[i].y,
                                            "square",
                                            glVarLocations,
                                            Constants.r2, Constants.r2
                                    ),
                            new ImageContainer
                                    (
                                            square2Texture,
                                            Constants.playerSquareHoverVA,
                                            playerModelX + gunOffsets[i].x, playerModelY + gunOffsets[i].y,
                                            "squareHover",
                                            glVarLocations,
                                            Constants.r2, Constants.r2
                                    ),
                            Constants.DropType.GUN
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
                                            Constants.playerSquareVA,
                                            playerModelX + thrusterOffsets[i].x, playerModelY + thrusterOffsets[i].y,
                                            "square",
                                            glVarLocations,
                                            Constants.r2, Constants.r2
                                    ),
                            new ImageContainer
                                    (
                                            square2Texture,
                                            Constants.playerSquareHoverVA,
                                            playerModelX + thrusterOffsets[i].x, playerModelY + thrusterOffsets[i].y,
                                            "squareHover",
                                            glVarLocations,
                                            Constants.r2, Constants.r2
                                    ),
                            Constants.DropType.THRUSTER
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
                                            Constants.playerSquareVA,
                                            modLeftOffset.x, modLeftOffset.y - i * (.038f + Constants.r2 * 2),
                                            "square",
                                            glVarLocations,
                                            Constants.r2, Constants.r2
                                    ),
                            new ImageContainer
                                    (
                                            square2Texture,
                                            Constants.playerSquareHoverVA,
                                            modLeftOffset.x, modLeftOffset.y - i * (.038f + Constants.r2 * 2),
                                            "squareHover",
                                            glVarLocations,
                                            Constants.r2, Constants.r2
                                    ),
                            Constants.DropType.MOD
                    );
            allComponentButtons[itrAllCompButtons] = playerMods[i];
            itrAllCompButtons++;
        }
    }
}
