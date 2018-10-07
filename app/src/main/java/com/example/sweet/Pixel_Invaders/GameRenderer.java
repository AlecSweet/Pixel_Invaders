package com.example.sweet.Pixel_Invaders;

import android.content.Context;

import android.opengl.GLSurfaceView.Renderer;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Centipede;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Heavy;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Kamikaze;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Pulser;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Tiny;
import com.example.sweet.Pixel_Invaders.Util.CollisionHandler;
import com.example.sweet.Pixel_Invaders.Util.Factories.EnemyFactory;
import com.example.sweet.Pixel_Invaders.Util.Factories.LoadLevel;
import com.example.sweet.Pixel_Invaders.Util.Resource_Readers.ImageParser;
import com.example.sweet.Pixel_Invaders.Util.Resource_Readers.TextureLoader;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Asteroid;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Carrier;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Enemy;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.MassAccelerator;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.MineLayer;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Simple;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.BasicGun;
import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.MineGun;
import com.example.sweet.Pixel_Invaders.Game_Objects.Level;
import com.example.sweet.Pixel_Invaders.Game_Objects.Player;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.UI_System.UI;
import com.example.sweet.Pixel_Invaders.Util.Factories.DropFactory;
import com.example.sweet.Pixel_Invaders.Util.Resource_Readers.ShaderHelper;
import com.example.sweet.Pixel_Invaders.Util.Resource_Readers.TextResourceReader;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GameSettings;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform3f;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Created by Sweet on 1/14/2018.
 */

public class GameRenderer implements Renderer
{
    private Context context;

    public GlobalInfo globalInfo;

    private Constants.GameState gameState = Constants.GameState.MAIN_MENU;

    private ExecutorService levelLoader = newSingleThreadExecutor();

    private float averageFrameTime;
    private float totalTime;

    private int framesPast = 0;

    private LoadLevel loadLevel;

    private Future<Level> levelFuture;

    private ExitListener exitListener = null;

    public interface ExitListener
    {
         void onExit();
    }

    private EnemyFactory enemyFactory;
    private DropFactory dropFactory;

    float
            xScale,
            yScale,
            xbound,
            ybound;

    private int
            xScaleLocation,
            yScaleLocation,
            shaderProgram,
            xScaleLocationParticle,
            yScaleLocationParticle,
            xScaleLocationPlain,
            yScaleLocationPlain,
            timeLocation,
            particleShaderProgram,
            plainShaderProgram,
            xScreenShiftLocationParticle,
            yScreenShiftLocationParticle,
            xScreenShiftLocation,
            yScreenShiftLocation,
            uTextureLocation,
            pointSizeLocation,
            particlePointSizeLocation,
            uMagLoc,
            difficulty,
            pTimeLocation,
            riftDataLoc,
            partRiftDataLoc,
            plainMagLoc,
            plainAlphaLoc;

    private static final String
            X_SCALE = "x_Scale",
            Y_SCALE = "y_Scale",
            X_SCREENSHIFT = "x_ScreenShift",
            Y_SCREENSHIFT = "y_ScreenShift",
            U_TIME = "u_Time",
            U_TEXTURE = "u_Texture",
            POINT_SIZE = "pointSize";

    private final long MILLIS_PER_SECOND = 1000;
    private final long UPS = 60;

    private final long mSPU = MILLIS_PER_SECOND / UPS;

    private volatile Enemy[] entities;
    
    private ParticleSystem
            playerParticles,
            enemyParticles,
            collisionParticles,
            staticParticles,
            uiParticles;

    private int whiteTexture;

    private boolean
            init = false,
            threadsStarted = false,
            saveTime = false,
            scaleSet = false,
            levelDone = true,
            levelLoaded = false,
            requestLevel = false,
            enemiesLive = false;

    public boolean setUp = false;

    Player player1;
    UI ui;

    private double
            pastTime = 0.0,
            lag = 0.0,
            globalStartTime = 0.0,
            interpolation = 0.0;

    private AIThread aiRunnable;

    private CollisionThread collisionRunnable;

    private Thread
            aiThread,
            collisionThread;

    GameRenderer(Context c)
    {
        globalStartTime = System.currentTimeMillis();
        pastTime = System.currentTimeMillis();
        context = c;
        globalInfo = new GlobalInfo((long)globalStartTime, new GameSettings());
        globalInfo.setTimeSlow(1);
    }

    @Override
    public void onSurfaceCreated(GL10 unused,EGLConfig eglConfig)
    {
        glClearColor(0.0f,0f,0f,0.0f);

        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.vert_shader);
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.frag_shader);

        String particleVertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.particle_vert_shader);
        String particleFragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.particle_frag_shader);

        String plainVertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.plain_vert_shader);
        String plainFragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.plain_frag_shader);

        whiteTexture = TextureLoader.loadTexture(context,R.drawable.white);

        int vertShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        int particleVertShader = ShaderHelper.compileVertexShader(particleVertexShaderSource);
        int particleFragShader = ShaderHelper.compileFragmentShader(particleFragmentShaderSource);

        int plainVertShader = ShaderHelper.compileVertexShader(plainVertexShaderSource);
        int plainFragShader = ShaderHelper.compileFragmentShader(plainFragmentShaderSource);

        shaderProgram = ShaderHelper.linkProgram(vertShader, fragShader);
        particleShaderProgram = ShaderHelper.linkProgram(particleVertShader, particleFragShader);
        plainShaderProgram = ShaderHelper.linkProgram(plainVertShader, plainFragShader);

        uTextureLocation = glGetUniformLocation(plainShaderProgram, U_TEXTURE);

        xScaleLocationParticle = glGetUniformLocation(particleShaderProgram, X_SCALE);
        yScaleLocationParticle = glGetUniformLocation(particleShaderProgram, Y_SCALE);
        xScreenShiftLocationParticle = glGetUniformLocation(particleShaderProgram, X_SCREENSHIFT);
        yScreenShiftLocationParticle = glGetUniformLocation(particleShaderProgram, Y_SCREENSHIFT);
        timeLocation = glGetUniformLocation(particleShaderProgram, U_TIME);
        particlePointSizeLocation = glGetUniformLocation(particleShaderProgram, POINT_SIZE);
        partRiftDataLoc = glGetUniformLocation(particleShaderProgram, "riftData");

        xScreenShiftLocation = glGetUniformLocation(shaderProgram, X_SCREENSHIFT);
        yScreenShiftLocation = glGetUniformLocation(shaderProgram, Y_SCREENSHIFT);
        xScaleLocation = glGetUniformLocation(shaderProgram, X_SCALE);
        yScaleLocation = glGetUniformLocation(shaderProgram, Y_SCALE);
        pointSizeLocation = glGetUniformLocation(shaderProgram, POINT_SIZE);
        uMagLoc = glGetUniformLocation(shaderProgram, "mag");
        pTimeLocation = glGetUniformLocation(shaderProgram, "time");
        riftDataLoc = glGetUniformLocation(shaderProgram, "riftData");

        xScaleLocationPlain = glGetUniformLocation(plainShaderProgram, X_SCALE);
        yScaleLocationPlain = glGetUniformLocation(plainShaderProgram, Y_SCALE);
        plainMagLoc = glGetUniformLocation(plainShaderProgram, "mag");
        plainAlphaLoc = glGetUniformLocation(plainShaderProgram, "alpha");
        /*plainRiftDataLoc = glGetUniformLocation(plainShaderProgram, "riftData");
        plainTimeLoc = glGetUniformLocation(plainShaderProgram, "time");*/

        glUseProgram(particleShaderProgram);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glUseProgram(shaderProgram);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glUseProgram(plainShaderProgram);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glUniform1f(plainMagLoc, 1);
        glUniform1f(plainAlphaLoc, 1);

        int[] glVarLocations = new int[4];
        /*glVarLocations[0] = glGetUniformLocation(plainShaderProgram, "x_displacement");
        glVarLocations[1] = glGetUniformLocation(plainShaderProgram, "y_displacement");
        glVarLocations[2] = glGetAttribLocation(plainShaderProgram, "a_Position");
        glVarLocations[3] = glGetAttribLocation(plainShaderProgram, "a_TexCoordinate");
        glVarLocations[4] = glGetUniformLocation(plainShaderProgram, "u_Texture");*/
        glVarLocations[0] = glGetUniformLocation(plainShaderProgram, "displacement");
        glVarLocations[1] = glGetAttribLocation(plainShaderProgram, "a_Position");
        glVarLocations[2] = glGetAttribLocation(plainShaderProgram, "a_TexCoordinate");
        glVarLocations[3] = glGetUniformLocation(plainShaderProgram, "u_Texture");

        if(!init)
        {
            init();
            init = true;
            setUp = true;
        }
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {

        long startTime = System.currentTimeMillis();

        checkUiFlags();

        if(ui.gameState == Constants.GameState.IN_GAME)
        {
            if(!saveTime)
            {
                pastTime = System.currentTimeMillis();
                saveTime = true;
            }

            double currentTime = System.currentTimeMillis();
            double elapsedTime = currentTime - pastTime;
            pastTime = currentTime;
            lag += elapsedTime;

            while (lag >= mSPU)
            {
                /*int frameLag;
                if(aiRunnable.frameRequest - aiRunnable.currentFrame <
                        collisionRunnable.frameRequest - collisionRunnable.currentFrame)
                {
                    frameLag = (int)(collisionRunnable.frameRequest - collisionRunnable.lowestFrame);
                }
                else
                {
                    frameLag = (int)(aiRunnable.frameRequest - aiRunnable.currentFrame);
                }

                if(frameLag <= 2)
                {
                    catchUp = 1;
                }
                else if( frameLag > 2 && frameLag <= 6)
                {
                    catchUp = 2;
                }
                else if( frameLag > 6)
                {
                    catchUp = 4;
                }*/

                update();
                aiRunnable.frameRequest++;
                collisionRunnable.frameRequest++;
                lag -= mSPU;
            }
        }
        else
        {
            if(saveTime)
            {
                saveTime = false;
            }
        }

        draw();
        //long averageFrameTime = System.currentTimeMillis() - startTime;
        framesPast++;
        totalTime += System.currentTimeMillis() - startTime;
        if(framesPast >= 60)
        {
            averageFrameTime = 1000 / (totalTime / (float)framesPast);
            totalTime = 0;
            framesPast = 0;
        }

        ui.aiAvgFrame = 1000 / (aiRunnable.averageFrameTime + 1);
        ui.collisionAvgFrame = 1000 / (collisionRunnable.averageFrameTime + 1);
        ui.uiAvgFrame = averageFrameTime;


    }

    public void draw()
    {
        ui.drawBackground();

        if(ui.gameState == Constants.GameState.IN_GAME || ui.gameState == Constants.GameState.PAUSE_MENU ||
                (ui.gameState == Constants.GameState.OPTIONS && ui.prevGameState != Constants.GameState.MAIN_MENU))
        {

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, whiteTexture);
            glUniform1i(uTextureLocation, 0);

            glUseProgram(shaderProgram);
            player1.rift.riftDataUniform(riftDataLoc);
            glUniform1f(xScreenShiftLocation, player1.xScreenShift - player1.screenShakeX);
            glUniform1f(yScreenShiftLocation, player1.yScreenShift - player1.screenShakeY);


            glUniform1f(pointSizeLocation, globalInfo.pointSize);
            glUniform1f(uMagLoc, 1f);

            if(!ui.intro)
            {
                player1.draw(interpolation);
            }

            glUniform1f(pTimeLocation, globalInfo.getAugmentedTimeSeconds());
            glUniform1f(xScreenShiftLocation, globalInfo.getScreenShiftX());
            glUniform1f(yScreenShiftLocation, globalInfo.getScreenShiftY());


            levelDone = true;
            boolean tempEnemiesLive = false;
            for (int i = 0; i < Constants.ENTITIES_LENGTH; i++)
            {
                if (entities[i] != null)
                {
                    if ((entities[i].aiRemoveConsensus && entities[i].collisionRemoveConsensus) ||
                            (!entities[i].collisionRemoveConsensus && collisionRunnable.entities[i] == null))
                    {

                        if(!entities[i].isAsteriod)
                        {
                            for (int dI = 0; dI < entities[i].dropsToAdd.length; dI++)
                            {
                                if (entities[i].dropsToAdd[dI] != null)
                                {
                                    entities[i].dropsToAdd[dI].x = entities[i].getX();
                                    entities[i].dropsToAdd[dI].y = entities[i].getY();
                                    entities[i].dropsToAdd[dI].component.live = true;
                                    player1.addDrop(entities[i].dropsToAdd[dI]);
                                }
                            }
                            for (int dI = 0; dI < entities[i].consumables.length; dI++)
                            {
                                if (entities[i].consumables[dI] != null)
                                {
                                    entities[i].consumables[dI].x = entities[i].getX();
                                    entities[i].consumables[dI].y = entities[i].getY();
                                    player1.addDrop(entities[i].consumables[dI]);
                                }
                            }
                        }
                        entities[i].freeMemory();
                        entities[i].uiRemoveConsensus = true;
                        entities[i].aiRemoveConsensus = true;
                        entities[i].collisionRemoveConsensus = true;
                        entities[i] = null;
                    }
                    else
                    {
                        if(entities[i].spawned)
                        {
                            entities[i].draw(0);
                        }

                        if(!entities[i].isAsteriod)
                        {
                            tempEnemiesLive = true;
                        }
                        else
                        {
                            if(!enemiesLive)
                            {
                                ((Asteroid)entities[i]).levelDone = true;
                            }
                        }

                        levelDone = false;
                    }
                }
            }
            enemiesLive = tempEnemiesLive;



            /*if (gameState == Constants.GameState.PAUSE_MENU)
            {
                glUniform1f(xScreenShiftLocation, 0);
                glUniform1f(yScreenShiftLocation, 0);
            }*/

            glUniform3f(riftDataLoc, 0, 0, 0);
            glUniform1f(xScreenShiftLocation, 0);
            glUniform1f(yScreenShiftLocation, 0);

            if(!ui.intro)
            {
                player1.drawBonusBar();
            }

            glUseProgram(particleShaderProgram);
            player1.rift.riftDataUniform(partRiftDataLoc);
            glUniform1f(timeLocation, globalInfo.getAugmentedTimeSeconds());
            glUniform1f(xScreenShiftLocationParticle, globalInfo.getScreenShiftX());
            glUniform1f(yScreenShiftLocationParticle, globalInfo.getScreenShiftY());
            glUniform1f(particlePointSizeLocation, globalInfo.particlePointSize * 1.1f);

            if(ui.introRevTime || !ui.intro)
            {
                collisionParticles.draw();
                enemyParticles.draw();
                playerParticles.draw();
            }
            glUniform1f(xScreenShiftLocationParticle, 0);
            glUniform1f(yScreenShiftLocationParticle, 0);
            glUniform3f(partRiftDataLoc, 0, 0, 0);
            staticParticles.draw();
        }

        glUseProgram(plainShaderProgram);
        ui.draw(interpolation);
        if(ui.gameState != Constants.GameState.IN_GAME)
        {
            glUseProgram(particleShaderProgram);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, whiteTexture);
            glUniform1i(uTextureLocation, 0);
            glUniform1f(timeLocation, (float)(System.currentTimeMillis() - globalStartTime)/1000);
            glUniform1f(xScreenShiftLocationParticle, 0);
            glUniform1f(yScreenShiftLocationParticle, 0);
            glUniform1f(particlePointSizeLocation, globalInfo.particlePointSize * 1.1f);
            uiParticles.draw();
            glUseProgram(plainShaderProgram);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        if(!scaleSet)
        {
            glViewport(0, 0, width, height);
            float aspectRatio = width > height ?
                    (float) height / (float) width :
                    (float) width / (float) height;

            float numPixels = 2 / Constants.PIXEL_SIZE;

            if(width > height)
            {
                glUseProgram(shaderProgram);
                glUniform1f(xScaleLocation, aspectRatio);
                glUniform1f(yScaleLocation, 1);
                glUniform1f(pointSizeLocation, globalInfo.pointSize);

                glUseProgram(particleShaderProgram);
                glUniform1f(xScaleLocationParticle, aspectRatio);
                glUniform1f(yScaleLocationParticle, 1);
                glUniform1f(particlePointSizeLocation, globalInfo.particlePointSize * 1.1f);

                glUseProgram(plainShaderProgram);
                glUniform1f(xScaleLocationPlain, aspectRatio);
                glUniform1f(yScaleLocationPlain, 1);

                xScale = aspectRatio;
                yScale = 1;
                ui.xScale = xScale;
                ui.yScale = yScale;
                globalInfo.setScale(xScale, yScale);
                float pSize = (height / numPixels);
                globalInfo.pointSize = 2.04f * pSize;
                globalInfo.particlePointSize = 3.04f * pSize;
            }
            else
            {
                glUseProgram(shaderProgram);
                glUniform1f(xScaleLocation, 1);
                glUniform1f(yScaleLocation, aspectRatio);

                glUseProgram(particleShaderProgram);
                glUniform1f(xScaleLocationParticle, 1);
                glUniform1f(yScaleLocationParticle, aspectRatio);

                glUseProgram(plainShaderProgram);
                glUniform1f(xScaleLocationPlain, 1);
                glUniform1f(yScaleLocationPlain, aspectRatio);

                xScale = 1;
                yScale = aspectRatio;
                ui.xScale = xScale;
                ui.yScale = yScale;
                globalInfo.setScale(xScale, yScale);
                float pSize = (width / numPixels);
                globalInfo.pointSize = 2.04f * pSize;
                globalInfo.particlePointSize = 3.04f * pSize;
            }

            xbound = 1.5f;
            ybound = 1.5f;
            ui.setScale(xScale, yScale);
            enemyFactory.setBounds(2.5f, 3.4f);
            scaleSet = true;
        }
    }
    
    private void newGame()
    {
        playerParticles.clear();
        collisionParticles.clear();
        enemyParticles.clear();

        for(int i = 0; i < entities.length; i++)
        {
            if(entities[i] != null)
            {
                entities[i].aiRemoveConsensus = true;
                entities[i].collisionRemoveConsensus = true;
                entities[i] = null;
            }
        }

        player1 = new Player(dropFactory,
                context,
                .008f,
                shaderProgram,
                playerParticles,
                staticParticles,
                ImageParser.parseImage(context, R.drawable.playerm, R.drawable.player_lightm, shaderProgram,2),
                globalInfo
        );
        player1.setScale(xScale, yScale);
        player1.xbound = 1.5f;
        player1.ybound = 1.5f;
        ui.setPlayer(player1);
        if(!globalInfo.gameSettings.skipIntros)
        {
            player1.destroyCollidableAnimation(player1.getPixelGroup(), playerParticles);
            player1.getPixelGroup().resetPixels();
        }

        aiRunnable.setInfo(player1);
        collisionRunnable.setInfo(player1);

        globalInfo.extraGunChance = .2f;
        globalInfo.extraModChance = .5f;
        globalInfo.setTimeSlow(1);
        ui.gameState = Constants.GameState.IN_GAME;
        gameState = Constants.GameState.IN_GAME;

        saveTime = false;
        pastTime = 0.0;
        lag = 0.0;
        interpolation = 0.0;
        difficulty = 0;

        if(!threadsStarted)
        {
            aiThread.start();
            collisionThread.start();
            threadsStarted = true;
        }
        else
        {
            synchronized (aiRunnable.lock)
            {
                aiRunnable.lock.notify();
            }
            synchronized (collisionRunnable.lock)
            {
                collisionRunnable.lock.notify();
            }
        }
    }

    private void update()
    {
        if(levelDone && aiRunnable.getClearedConsensus() && collisionRunnable.getClearedConsensus())
        {
            if(!requestLevel)
            {
                loadLevel.resetLoadLevel(difficulty);
                difficulty++;
                ui.difficulty = difficulty;
                levelFuture = levelLoader.submit(loadLevel);
                requestLevel = true;
                levelLoaded = false;
            }
            if(!levelLoaded)
            {
                if(levelFuture.isDone())
                {
                    levelLoaded = true;
                    ui.displayReady = true;
                }
            }
        }

        if(!ui.intro)
        {
            player1.movePlayer();
        }

        if(!player1.getPixelGroup().getCollidableLive())
        {
            ui.gameState = Constants.GameState.GAME_OVER;
        }
    }

    private void init()
    {
        playerParticles = new ParticleSystem(10000, particleShaderProgram , whiteTexture, globalStartTime, globalInfo);

        enemyParticles = new ParticleSystem(12000, particleShaderProgram, whiteTexture, globalStartTime, globalInfo);

        collisionParticles = new ParticleSystem(12000, particleShaderProgram, whiteTexture, globalStartTime, globalInfo);

        uiParticles = new ParticleSystem(4000, particleShaderProgram, whiteTexture, globalStartTime, globalInfo);

        staticParticles = new ParticleSystem(1500, particleShaderProgram, whiteTexture, globalStartTime, globalInfo);

        enemyFactory = initEnemyFactory();

        loadLevel = new LoadLevel(0, enemyFactory);

        entities = new Enemy[Constants.ENTITIES_LENGTH];

        ui = new UI(
                context,
                plainShaderProgram,
                globalInfo,
                uiParticles
        );

        ui.pixelShaderProgram = shaderProgram;
        ui.uiShaderProgram = plainShaderProgram;
        ui.whiteTexture = whiteTexture;
        ui.uTextureLocation = uTextureLocation;

        aiRunnable = new AIThread(globalInfo);
        aiThread = new Thread(aiRunnable);

        collisionRunnable = new CollisionThread(globalInfo);
        collisionThread = new Thread(null, collisionRunnable, "collision", 6000000);
        collisionRunnable.setCollisionHandler(new CollisionHandler(collisionParticles));
        ui.startTitleIntro();
    }

    private EnemyFactory initEnemyFactory()
    {
        dropFactory = initDropFactory();
        EnemyFactory e = new EnemyFactory();

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.SIMPLE,
                        new Simple
                                (
                                        ImageParser.parseImage(context, R.drawable.simple1, R.drawable.simple_light, shaderProgram, 1),
                                        new BasicGun
                                                (
                                                        ImageParser.parseImage(context, R.drawable.basicbullet, R.drawable.basicbullet_light, shaderProgram, 1),
                                                        enemyParticles,
                                                        1000,
                                                        .022f
                                                ),
                                        enemyParticles,
                                        dropFactory,
                                        xbound,
                                        ybound,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.KAMIKAZE,
                        new Kamikaze
                                (
                                        ImageParser.parseImage(context, R.drawable.kamikaze, R.drawable.kamikaze_light, shaderProgram, 1),
                                        xbound,
                                        ybound,
                                        enemyParticles,
                                        dropFactory,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.TINY,
                        new Tiny
                                (
                                        ImageParser.parseImage(context, R.drawable.tiny, R.drawable.tiny_light, shaderProgram, 1),
                                        xbound,
                                        ybound,
                                        enemyParticles,
                                        dropFactory,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.PULSER,
                        new Pulser
                                (
                                        ImageParser.parseImage(context, R.drawable.pulser1, R.drawable.pulser_light1, shaderProgram, 1),
                                        new BasicGun
                                                (
                                                        ImageParser.parseImage(context, R.drawable.pulserbullet, R.drawable.pulserbullet, shaderProgram, 1),
                                                        enemyParticles,
                                                        2000,
                                                        .02f
                                                ),
                                        enemyParticles,
                                        dropFactory,
                                        xbound,
                                        ybound,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.HEAVY,
                        new Heavy
                                (
                                        ImageParser.parseImage(context, R.drawable.heavy, R.drawable.heavy_light, shaderProgram, 1),
                                        new BasicGun
                                                (
                                                        ImageParser.parseImage(context, R.drawable.heavybullet, R.drawable.heavybullet, shaderProgram, 1),
                                                        enemyParticles,
                                                        1000,
                                                        .03f
                                                ),
                                        enemyParticles,
                                        dropFactory,
                                        xbound,
                                        ybound,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.MINELAYER,
                        new MineLayer
                                (
                                        ImageParser.parseImage(context, R.drawable.minelayer, R.drawable.minelayer_light, shaderProgram, 1),
                                        new MineGun
                                                (
                                                        ImageParser.parseImage(context, R.drawable.minelayerbullet, R.drawable.minelayerbullet, shaderProgram, 1),
                                                        enemyParticles,
                                                        2000,
                                                        10
                                                ),
                                        enemyParticles,
                                        dropFactory,
                                        xbound,
                                        ybound,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.MASSACCELERATOR,
                        new MassAccelerator
                                (
                                        ImageParser.parseImage(context, R.drawable.massaccelerator1, R.drawable.massaccelerator_light, shaderProgram, 1),
                                        new BasicGun
                                                (
                                                        ImageParser.parseImage(context, R.drawable.acceleratorbullet, R.drawable.acceleratorbullet, shaderProgram, 1),
                                                        enemyParticles,
                                                        4000,
                                                        .3f
                                                ),
                                        enemyParticles,
                                        dropFactory,
                                        xbound,
                                        ybound,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.CARRIER,
                        new Carrier
                                (
                                        ImageParser.parseImage(context, R.drawable.carrier3, R.drawable.carrier_light1, shaderProgram, 1),
                                        enemyParticles,
                                        dropFactory,
                                        xbound,
                                        ybound,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.CENTIPEDE,
                        new Centipede
                                (
                                        ImageParser.parseImage(context, R.drawable.head, R.drawable.head_light, shaderProgram, 1),
                                        ImageParser.parseImage(context, R.drawable.segment, R.drawable.segment_light, shaderProgram, 1),
                                        new BasicGun
                                                (
                                                        ImageParser.parseImage(context, R.drawable.basicbullet, R.drawable.basicbullet_light, shaderProgram, 1),
                                                        enemyParticles,
                                                        1000,
                                                        .022f
                                                ),
                                        new BasicGun
                                                (
                                                        ImageParser.parseImage(context, R.drawable.basicbullet, R.drawable.basicbullet_light, shaderProgram, 1),
                                                        enemyParticles,
                                                        1000,
                                                        .022f
                                                ),
                                        enemyParticles,
                                        dropFactory,
                                        xbound,
                                        ybound,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.ASTEROID_GREY_TINY,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidgraysmall, R.drawable.asteroidgraysmall_light, shaderProgram, 0),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.ASTEROID_RED_TINY,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidredtiny, R.drawable.asteroidredtiny_light, shaderProgram, 0),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.ASTEROID_GREY_SMALL,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidgraymedium, R.drawable.asteroidgraymedium_light, shaderProgram, 0),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.ASTEROID_RED_SMALL,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidredsmall, R.drawable.asteroidredsmall_light, shaderProgram, 0),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory,
                                        globalInfo
                                )
                );

        e.addEnemyToCatalog
                (
                        Constants.EnemyType.ASTEROID_GREY_MEDIUM,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidgraylarge, R.drawable.asteroidgraylarge_light, shaderProgram, 0),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory,
                                        globalInfo
                                )
                );


        e.addEnemyToCatalog
                (
                        Constants.EnemyType.ASTEROID_RED_MEDIUM,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidredlarge, R.drawable.asteroidredlarge_light, shaderProgram, 0),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory,
                                        globalInfo
                                )
                );

        return e;
    }

    private DropFactory initDropFactory()
    {
        DropFactory d = new DropFactory();

        d.addDropToCatalog(Constants.DropType.HEALTH, ImageParser.parseImage(context, R.drawable.health, R.drawable.health, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.EXTRA_GUN, ImageParser.parseImage(context, R.drawable.extragun, R.drawable.extragun_light, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.EXTRA_MOD, ImageParser.parseImage(context, R.drawable.extramod, R.drawable.extragun_light, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.GUN, ImageParser.parseImage(context, R.drawable.guncomponent, R.drawable.guncomponent, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.THRUSTER, ImageParser.parseImage(context, R.drawable.thruster, R.drawable.thruster, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.MOD, ImageParser.parseImage(context, R.drawable.modcomponent, R.drawable.modcomponent, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.PIERCING, ImageParser.parseImage(context, R.drawable.piercingroundsicon, R.drawable.piercingroundsicon, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.PLATING, ImageParser.parseImage(context, R.drawable.pixelplatingicon, R.drawable.pixelplatingicon, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.BULLET_SPEED, ImageParser.parseImage(context, R.drawable.acceleratoricon, R.drawable.acceleratoricon, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.FIRE_RATE, ImageParser.parseImage(context, R.drawable.overdriveicon, R.drawable.overdriveicon, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.EXTRA_SHOTS, ImageParser.parseImage(context, R.drawable.projectilefabricatoricon, R.drawable.projectilefabricatoricon, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.RESTORATION, ImageParser.parseImage(context, R.drawable.restorativecellsicon, R.drawable.restorativecellsicon, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.PRECISION, ImageParser.parseImage(context, R.drawable.precisionbarrelsicon, R.drawable.precisionbarrelsicon, shaderProgram, -1));

        d.addDropToCatalog(Constants.DropType.TEMPORAL, ImageParser.parseImage(context, R.drawable.temporalicon, R.drawable.temporalicon, shaderProgram, -1));
        return d;
    }

    void inGamePause()
    {
        gameState = Constants.GameState.PAUSE_MENU;
        ui.gameState = Constants.GameState.PAUSE_MENU;
        /*for(int i = 0; i < entities.length; i++)
        {
            if(entities[i] != null)
            {
                System.out.println();
                System.out.println(
                        "Size: " + entities[i].getPixelGroup().totalPixels +
                        " Spawned: " + entities[i].spawned +
                        " live: " + entities[i].getPixelGroup().getCollidableLive() +
                        " X: " + entities[i].getPixelGroup().centerX +
                        " Y: " + entities[i].getPixelGroup().centerY +
                        " Asteroid: " + entities[i].isAsteriod +
                        " Spawned: " + entities[i].spawned +
                        " Render: " + entities[i] +
                        " AI: " + aiRunnable.entities[i] +
                        " Collision: " + collisionRunnable.entities[i]
                );
                System.out.println();
            }
        }*/
        globalInfo.pauseTime();
        player1.pause = true;
        ui.setDropsInRange(player1.getExchangableComponentDrops());
    }

    void inGameUnpause()
    {
        gameState = Constants.GameState.IN_GAME;
        ui.gameState = Constants.GameState.IN_GAME;
        globalInfo.unpauseTime();
        player1.pause = false;
    }

    private void exitGame()
    {
        aiRunnable.running = false;
        collisionRunnable.running = false;
        try
        {
            aiThread.join();
            collisionThread.join();
        }
        catch(InterruptedException e)
        {
            System.out.println("InterruptedException");
        }
        exitListener.onExit();
    }

    void setExitListener(ExitListener e)
    {
        exitListener = e;
    }

    private void checkUiFlags()
    {
        if(ui.exitFlag)
        {
            exitGame();
        }
        if(ui.newGameFlag)
        {
            newGame();
            ui.gameState = Constants.GameState.IN_GAME;
            ui.startIntro();
            //ui.intro = false;
            inGameUnpause();
            ui.newGameFlag = false;
        }
        if(ui.changeParticlesFlag)
        {
            collisionParticles.setCurMax(globalInfo.gameSettings.particlePercent);
            enemyParticles.setCurMax(globalInfo.gameSettings.particlePercent);
            playerParticles.setCurMax(globalInfo.gameSettings.particlePercent);
            uiParticles.setCurMax(globalInfo.gameSettings.particlePercent);
            ui.changeParticlesFlag = false;
        }
        if(ui.pauseFlag)
        {
            inGamePause();
            ui.pauseFlag = false;
        }
        if(ui.readyFlag)
        {
            player1.resetDrops();
            globalInfo.extraGunChance = .2f / (player1.getMaxGuns() * 2f);
            globalInfo.extraModChance = .5f / (player1.getMaxMods());
            aiRunnable.block = true;
            collisionRunnable.block = true;
            while(aiThread.getState() != Thread.State.WAITING &&
                    collisionThread.getState() != Thread.State.WAITING)
            {
                System.out.println(aiThread.getState()+ ", " + aiThread.getState());
            }
            levelDone = false;
            requestLevel = false;
            ui.displayReady = false;
            ui.readyFlag = false;
            Arrays.fill(collisionRunnable.entities, null);
            Arrays.fill(aiRunnable.entities, null);
            try
            {
                float st = globalInfo.getAugmentedTimeMillis();
                for(int i = 0; i < entities.length; i++)
                {
                    if(entities[i] != null)
                    {
                        entities[i].freeMemory();
                    }
                    entities[i] = levelFuture.get().levelEnemies[i];
                    collisionRunnable.entities[i] = entities[i];
                    aiRunnable.entities[i] = entities[i];
                    if(entities[i] != null)
                    {
                        entities[i].setLevelStartTime(st);
                    }
                }
            }
            catch (InterruptedException | ExecutionException e)
            {
                System.out.println("Exception");
            }
            synchronized (aiRunnable.lock)
            {
                aiRunnable.lock.notify();
            }
            synchronized (collisionRunnable.lock)
            {
                collisionRunnable.lock.notify();
            }

        }
    }
}