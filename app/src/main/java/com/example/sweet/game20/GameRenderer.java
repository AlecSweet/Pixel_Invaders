package com.example.sweet.game20;

import android.content.Context;

import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Stack;
import com.example.sweet.game20.util.*;
import com.example.sweet.game20.Objects.*;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

import static com.example.sweet.game20.util.Constants.DropType.*;
import static com.example.sweet.game20.util.Constants.EnemyType.*;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;

import static android.opengl.GLES20.glClear;
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

/**
 * Created by Sweet on 1/14/2018.
 */

public class GameRenderer implements Renderer
{
    private Context context;

    private GlobalInfo globalInfo;
    private float averageFrameTime = 0;
    private GLSurfaceView glSurfaceView;

    private ExitListener exitListener = null;

    public interface ExitListener
    {
        public void onExit();
    }

    private EnemyFactory enemyFactory;
    private DropFactory dropFactory;

    public float
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
            uMagLoc;

    private static final String
            X_SCALE = "x_Scale",
            Y_SCALE = "y_Scale",
            X_SCREENSHIFT = "x_ScreenShift",
            Y_SCREENSHIFT = "y_ScreenShift",
            U_TIME = "u_Time",
            U_TEXTURE = "u_Texture",
            POINT_SIZE = "pointSize";

    private int frames;

    private float
            pointSize = 0,
            particlePointSize = 0;

    private final long MILLIS_PER_SECOND = 1000;
    private final long UPS = 60;

    private final long mSPU = MILLIS_PER_SECOND / UPS;

    private long currentFrame = 0;

    private volatile Enemy[] entities;

    private Drop[] drops;

    private int dropIndex = 0;
    
    private ParticleSystem
            playerParticles,
            enemyParticles,
            collisionParticles;

    private CollisionHandler collisionHandler;

    private int whiteTexture;

    public boolean pause = false;

    private boolean
            isPlaying = false,
            init = false,
            saveTime = false,
            scaleSet = false;

    public Player player1;

    public UI ui;

    private double
            pastTime = 0.0,
            lag = 0.0,
            globalStartTime = 0.0,
            secondMark = 0.0,
            interpolation = 0.0;

    private float[] background = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -2.5f, -2.5f,   0f, 1f,
            2.5f, -2.5f,   1f, 1f,
            2.5f,  2.5f,   1f, 0f,
            -2.5f,  2.5f,   0f, 0f,
            -2.5f, -2.5f,   0f, 1f
    };
    private float[] moonVA = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -.088f, -.088f,   0f, 1f,
            .088f, -.088f,   1f, 1f,
            .088f,  .088f,   1f, 0f,
            -.088f,  .088f,   0f, 0f,
            -.088f, -.088f,   0f, 1f
    };

    private float[] earthVA = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -.256f, -.256f,   0f, 1f,
            .256f, -.256f,   1f, 1f,
            .256f,  .256f,   1f, 0f,
            -.256f,  .256f,   0f, 0f,
            -.256f, -.256f,   0f, 1f
    };
    private ImageContainer
            earthC,
            moonC,
            stars1,
            stars2,
            stars3,
            stars4;

    public AIThread aiRunnable;

    private CollisionThread collisionRunnable;

    private LevelControllerThread levelRunnable;

    private Thread
            aiThread,
            collisionThread,
            levelThread;

    private Stack<Integer> openEntityIndices = new Stack<>();

    private LinkedList<Enemy> enemyOverflow = new LinkedList<>();

    private float catchUp = 1;

    public GameRenderer(Context c)
    {
        globalStartTime = System.currentTimeMillis();
        pastTime = System.currentTimeMillis();
        secondMark = System.currentTimeMillis();
        context = c;
        globalInfo = new GlobalInfo((long)globalStartTime);
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

        xScreenShiftLocation = glGetUniformLocation(shaderProgram, X_SCREENSHIFT);
        yScreenShiftLocation = glGetUniformLocation(shaderProgram, Y_SCREENSHIFT);
        xScaleLocation = glGetUniformLocation(shaderProgram, X_SCALE);
        yScaleLocation = glGetUniformLocation(shaderProgram, Y_SCALE);
        pointSizeLocation = glGetUniformLocation(shaderProgram, POINT_SIZE);
        uMagLoc = glGetUniformLocation(shaderProgram, "mag");

        xScaleLocationPlain = glGetUniformLocation(plainShaderProgram, X_SCALE);
        yScaleLocationPlain = glGetUniformLocation(plainShaderProgram, Y_SCALE);

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

        int[] glVarLocations = new int[5];
        glVarLocations[0] = glGetUniformLocation(plainShaderProgram, "x_displacement");
        glVarLocations[1] = glGetUniformLocation(plainShaderProgram, "y_displacement");
        glVarLocations[2] = glGetAttribLocation(plainShaderProgram, "a_Position");
        glVarLocations[3] = glGetAttribLocation(plainShaderProgram, "a_TexCoordinate");
        glVarLocations[4] = glGetUniformLocation(plainShaderProgram, "u_Texture");
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
                        background,
                        0, 0,
                        "",
                        glVarLocations,
                        -1, -1
                );
        if(!init)
        {
            init();
            init = true;
            newGame();
            isPlaying = true;
        }
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {
        long startTime = System.currentTimeMillis();
        if(!isPlaying)
        {
            newGame();
            isPlaying = true;
        }

        if(System.currentTimeMillis() - secondMark >= 1000)
        {
            secondMark = System.currentTimeMillis();
            frames = 0;
        }

        if(ui.exitFlag)
        {
            exitGame();
        }

        if(!pause)
        {
            if(!saveTime)
            {
                pastTime = System.currentTimeMillis();
                saveTime = true;
            }

            double currentTime = System.currentTimeMillis();
            double elapsedTime = currentTime - pastTime;
            pastTime = currentTime;
            /*if(aiRunnable.frameRequest - aiRunnable.currentFrame < 2 &&
                collisionRunnable.frameRequest - collisionRunnable.currentFrame < 2)
            {*/
                lag += elapsedTime;

                while (lag >= mSPU * catchUp)
                {
                    int frameLag = 0;
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
                    }

                    update();
                    aiRunnable.frameRequest++;
                    collisionRunnable.frameRequest++;
                    frames++;
                    lag -= mSPU;
                }
            //}
            /*if(currentFrame < aiRunnable.currentFrame)
            {
                update();
                frames++;
            }*/
           /* while(currentFrame < aiRunnable.currentFrame)
            {
                update();
            }*/
        }
        else
        {
            if(saveTime)
            {
                saveTime = false;
            }
        }

        draw();

        if(averageFrameTime == 0)
        {
            averageFrameTime = startTime - System.currentTimeMillis();
        }
        else
        {
            averageFrameTime = (averageFrameTime + (System.currentTimeMillis() - startTime)) / 2;
        }
        ui.aiAvgFrame = aiRunnable.averageFrameTime;
        ui.collisionAvgFrame = collisionRunnable.averageFrameTime;
        ui.uiAvgFrame = averageFrameTime;
    }

    public void draw()
    {
        //setInterpolation((((long)lag >> 4)<<4)/mSPU);
        setInterpolation(0);
        glClear(GL_COLOR_BUFFER_BIT);

        drawBackground();

        glUseProgram(particleShaderProgram);
        //glUniform1f(timeLocation, (float) ((System.currentTimeMillis() - globalStartTime) / 1000));
        glUniform1f(timeLocation, globalInfo.getAugmentedTimeSeconds());
        glUniform1f(xScreenShiftLocationParticle, player1.xScreenShift - player1.screenShakeX);
        glUniform1f(yScreenShiftLocationParticle, player1.yScreenShift - player1.screenShakeY);
        glUniform1f(particlePointSizeLocation, particlePointSize * 1.1f);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, whiteTexture);
        glUniform1i(uTextureLocation, 0);

        enemyParticles.draw();
        playerParticles.draw();

        glUseProgram(shaderProgram);
        glUniform1f(xScreenShiftLocation, player1.xScreenShift - player1.screenShakeX);
        glUniform1f(yScreenShiftLocation, player1.yScreenShift - player1.screenShakeY);
        glUniform1f(pointSizeLocation, pointSize);
        glUniform1f(uMagLoc, 1);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, whiteTexture);
        glUniform1i(uTextureLocation, 0);

        /*for(Drop d: player1.consumableDrops)
        {
            if(d != null && d.live)
            {
                d.draw();
            }
        }
        for(Drop d: player1.Drops)
        {
            if(d != null && d.live)
            {
                d.draw();
            }
        }*/

        player1.draw(interpolation);

        for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            if(entities[i] != null)
            {
                while(!entities[i].dropsToAdd.isEmpty())
                {
                    drops[dropIndex] = entities[i].dropsToAdd.peek();
                    entities[i].dropsToAdd.remove(drops[dropIndex]);
                    player1.addDrop(drops[dropIndex]);
                    dropIndex++;
                    if(dropIndex >= Constants.DROPS_LENGTH)
                    {
                        dropIndex -= Constants.DROPS_LENGTH;
                    }
                }

                if(entities[i].aiRemoveConsensus && entities[i].collisionRemoveConsensus)
                {
                    entities[i].freeMemory();
                    entities[i].uiRemoveConsensus  = true;
                    entities[i] = null;
                    openEntityIndices.push(i);
                }
                else
                {
                    entities[i].draw(0);
                }
            }
        }
        
        if(pause)
        {
            glUniform1f(xScreenShiftLocation, 0);
            glUniform1f(yScreenShiftLocation, 0);
        }

        glUseProgram(particleShaderProgram);

        //glUniform1f(timeLocation, (float) ((System.currentTimeMillis() - globalStartTime) / 1000));
        glUniform1f(timeLocation, globalInfo.getAugmentedTimeSeconds());
        glUniform1f(xScreenShiftLocationParticle, player1.xScreenShift - player1.screenShakeX);
        glUniform1f(yScreenShiftLocationParticle, player1.yScreenShift - player1.screenShakeY);
        glUniform1f(particlePointSizeLocation, particlePointSize * 1.1f);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, whiteTexture);
        glUniform1i(uTextureLocation, 0);

        collisionParticles.draw();

        glUseProgram(plainShaderProgram);
        if(!pause)
        {
            ui.gameState = Constants.GameState.IN_GAME;
        }
        else
        {
            ui.gameState = Constants.GameState.PAUSE_MENU;
        }
        ui.draw(interpolation);

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

                glUseProgram(particleShaderProgram);
                glUniform1f(xScaleLocationParticle, aspectRatio);
                glUniform1f(yScaleLocationParticle, 1);

                glUseProgram(plainShaderProgram);
                glUniform1f(xScaleLocationPlain, aspectRatio);
                glUniform1f(yScaleLocationPlain, 1);

                xScale = aspectRatio;
                yScale = 1;
                ui.xScale = xScale;
                ui.yScale = yScale;
                player1.setScale(xScale, yScale);
                globalInfo.setScale(xScale, yScale);
                float pSize = (height / numPixels);
                pointSize = 2.02f * pSize;
                particlePointSize = 3.02f * pSize;
                ui.pointSize = pointSize;
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
                player1.setScale(xScale, yScale);
                globalInfo.setScale(xScale, yScale);
                float pSize = (width / numPixels);
                pointSize = 2.02f * pSize;
                particlePointSize = 3.02f * pSize;
                ui.pointSize = pointSize;
            }

            xbound = 1.5f;
            ybound = 1.5f;
            player1.xbound = 1.5f;
            player1.ybound = 1.5f;
            ui.setScale(xScale, yScale);
            enemyFactory.setBounds(2.5f, 3f);
            scaleSet = true;
        }
    }

    public void setInterpolation(double i)
    {
        interpolation = i;
    }
    
    public void newGame()
    {
        openEntityIndices = new Stack<>();
        for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            openEntityIndices.push(i);
        }

        player1 = new Player(dropFactory,
                context,
                .0054f,
                shaderProgram,
                playerParticles,
                ImageParser.parseImage(context, R.drawable.player, R.drawable.player_light, shaderProgram),
                globalInfo
        );
        player1.xscale = xScale;
        player1.yscale = yScale;
        ui.player = player1;
        ui.pointSize = pointSize;
        ui.pixelShaderProgram = shaderProgram;
        ui.uiShaderProgram = plainShaderProgram;
        ui.whiteTexture = whiteTexture;
        ui.uTextureLocation = uTextureLocation;

        drops = new Drop[Constants.DROPS_LENGTH];

        entities = new Enemy[Constants.ENTITIES_LENGTH];

        aiRunnable = new AIThread(entities, globalInfo, collisionHandler);
        aiThread = new Thread(aiRunnable);
        aiRunnable.setPlayer(player1);

        collisionRunnable = new CollisionThread(entities);
        //collisionThread = new Thread(collisionRunnable);
        collisionThread = new Thread(null, collisionRunnable, "collision", 6000000);
        collisionRunnable.setPlayer(player1);
        collisionRunnable.setCollisionHandler(collisionHandler);

        levelRunnable = new LevelControllerThread(enemyFactory, globalInfo);
        levelThread = new Thread(levelRunnable);
        levelRunnable.setPlayer(player1);

        aiThread.start();
        collisionThread.start();
        levelThread.start();
    }

    public void update()
    {
        if(!levelRunnable.enemiesToAdd.isEmpty())
        {
            Enemy e = levelRunnable.enemiesToAdd.peek();
            levelRunnable.enemiesToAdd.remove(e);
            if(!openEntityIndices.isEmpty())
            {
                int index = openEntityIndices.pop();
                entities[index] = e;
            }
            else
            {
                enemyOverflow.add(e);
            }
        }
        player1.movePlayer(globalInfo.timeSlow);
        currentFrame++;
    }

    public void drawBackground()
    {
        float tShiftX = -player1.xScreenShift - player1.screenShakeX;
        float tShiftY = -player1.yScreenShift - player1.screenShakeY;
        stars4.draw(tShiftX / 6f, tShiftY / 6f);
        stars3.draw(tShiftX / 4.4f, tShiftY /  5.4f);
        stars2.draw(tShiftX / 3.8f, tShiftY / 4.8f);
        stars1.draw(tShiftX / 3.2f, tShiftY / 4.2f);
        moonC.draw(tShiftX / 2.1f, tShiftY / 2.1f);
        earthC.draw(tShiftX / 2, tShiftY / 2);
    }

    public void init()
    {
        playerParticles = new ParticleSystem(20000, particleShaderProgram , whiteTexture, globalStartTime, globalInfo);

        enemyParticles = new ParticleSystem(30000, particleShaderProgram, whiteTexture, globalStartTime, globalInfo);

        enemyFactory = initEnemyFactory();

        collisionParticles = new ParticleSystem(12000, particleShaderProgram, whiteTexture, globalStartTime, globalInfo);

        collisionHandler = new CollisionHandler(collisionParticles);

        ui = new UI(context, plainShaderProgram);

    }

    public EnemyFactory initEnemyFactory()
    {
        dropFactory = initDropFactory();
        EnemyFactory e = new EnemyFactory();

        e.addEnemyToCatalog
                (
                        SIMPLE,
                        new Simple
                                (
                                        ImageParser.parseImage(context, R.drawable.simple1, R.drawable.simple_light, shaderProgram),
                                        new BasicGun
                                                (
                                                        ImageParser.parseImage(context, R.drawable.basicbullet, R.drawable.basicbullet_light, shaderProgram),
                                                        enemyParticles,
                                                        1000,
                                                        .022f
                                                ),
                                        enemyParticles,
                                        dropFactory
                                )
                );

        e.addEnemyToCatalog
                (
                        CARRIER,
                        new Carrier
                                (
                                        ImageParser.parseImage(context, R.drawable.carrier3, R.drawable.carrier_light1, shaderProgram),
                                        enemyParticles,
                                        dropFactory
                                )
                );

        e.addEnemyToCatalog
                (
                        ASTEROID_GREY_TINY,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidgraysmall, R.drawable.asteroidgraysmall_light, shaderProgram),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory
                                )
                );

        e.addEnemyToCatalog
                (
                        ASTEROID_RED_TINY,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidredtiny, R.drawable.asteroidredtiny_light, shaderProgram),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory
                                )
                );

        e.addEnemyToCatalog
                (
                        ASTEROID_GREY_SMALL,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidgraymedium, R.drawable.asteroidgraymedium_light, shaderProgram),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory
                                )
                );

        e.addEnemyToCatalog
                (
                        ASTEROID_RED_SMALL,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidredsmall, R.drawable.asteroidredsmall_light, shaderProgram),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory
                                )
                );

        e.addEnemyToCatalog
                (
                        ASTEROID_GREY_MEDIUM,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidgraylarge, R.drawable.asteroidgraylarge_light, shaderProgram),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory
                                )
                );


        e.addEnemyToCatalog
                (
                        ASTEROID_RED_MEDIUM,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidredlarge, R.drawable.asteroidredlarge_light, shaderProgram),
                                        enemyParticles,
                                        xbound,
                                        ybound,
                                        dropFactory
                                )
                );

        return e;
    }

    public DropFactory initDropFactory()
    {
        DropFactory d = new DropFactory();

        d.addDropToCatalog(HEALTH, ImageParser.parseImage(context, R.drawable.health, R.drawable.health_light, shaderProgram));

        d.addDropToCatalog(EXTRA_GUN, ImageParser.parseImage(context, R.drawable.extragun, R.drawable.extragun_light, shaderProgram));

        d.addDropToCatalog(EXTRA_MOD, ImageParser.parseImage(context, R.drawable.extramod, R.drawable.extragun_light, shaderProgram));

        d.addDropToCatalog(GUN, ImageParser.parseImage(context, R.drawable.guncomponent, R.drawable.guncomponent, shaderProgram));

        d.addDropToCatalog(THRUSTER, ImageParser.parseImage(context, R.drawable.thrustercomponent, R.drawable.thrustercomponent, shaderProgram));

        d.addDropToCatalog(MOD, ImageParser.parseImage(context, R.drawable.modcomponent, R.drawable.modcomponent, shaderProgram));
        return d;
    }

    public void inGamePause()
    {
        pause = true;
        pauseThreads();
        player1.pause = true;
        ui.setDropsInRange(player1.getExchangableComponentDrops());
    }

    public void inGameUnpause()
    {
        pause = false;
        unpauseThreads();
        player1.pause = false;
    }

    public void pauseThreads()
    {
        if(init)
        {
            aiRunnable.pause = true;
            collisionRunnable.pause = true;
            levelRunnable.pause = true;
        }
    }

    public void unpauseThreads()
    {
        if(init)
        {
            aiRunnable.pause = false;
            collisionRunnable.pause = false;
            levelRunnable.pause = false;
        }
    }

    public void exitGame()
    {
        levelRunnable.running = false;
        aiRunnable.running = false;
        collisionRunnable.running = false;
        try
        {
            levelThread.join();
            aiThread.join();
            collisionThread.join();
        }
        catch(InterruptedException e)
        {
        }
        /*for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            if(entities[i] != null)
            {
                if (entities[i].getHasGun())
                {
                    for (GunComponent gC : entities[i].getGunComponents())
                    {
                        if (gC != null)
                        {
                            for (Bullet b : gC.gun.getBullets())
                            {
                                b.freeResources();
                            }
                        }
                    }
                }
                entities[i].getPixelGroup().freeMemory();
            }
        }

        ui.freeMemory();
        */
        //context.finish();
        exitListener.onExit();
    }

    public void setExitListener(ExitListener e)
    {
        exitListener = e;
    }
}