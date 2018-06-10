package com.example.sweet.game20;

import android.content.Context;

import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import com.example.sweet.game20.util.*;
import com.example.sweet.game20.Objects.*;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

import static com.example.sweet.game20.util.Constants.EnemyType.*;
import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
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
    public boolean pause = false;
    private double
            lastSpawn,
            spawnDelay = 2000;
    private double
            secondMark,
            interpolation = 0;

    private final Context context;
    
    private EnemyFactory enemyFactory;

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
            aPositionLocation,
            aTextureCoordLocation,
            uTextureLocation,
            xDispLocation,
            yDispLocation;

    private static final String
            X_SCALE = "x_Scale",
            Y_SCALE = "y_Scale",
            X_SCREENSHIFT = "x_ScreenShift",
            Y_SCREENSHIFT = "y_ScreenShift",
            U_TIME = "u_Time",
            A_POSITION = "a_Position",
            A_TEXTURECOORDINATE = "a_TexCoordinate",
            U_TEXTURE = "u_Texture",
            X_DISP = "x_displacement",
            Y_DISP = "y_displacement";

    private int frames;

    private final long MILLIS_PER_SECOND = 1000;
    private final long UPS = 60;

    private final long mSPU = MILLIS_PER_SECOND / UPS;

    private Enemy[] entities;
    
    private ParticleSystem
            playerParticles,
            enemyParticles,
            collisionParticles;

    private CollisionHandler collisionHandler;

    private int whiteTexture;

    private boolean isPlaying = false,
                    init = false;

    private Player player1;

    public UI ui;

    private double pastTime;

    private double lag = 0.0;

    private double globalStartTime;

    private float[] background = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -4f, -4f,   0f, 1f,
            4f, -4f,   1f, 1f,
            4f,  4f,   1f, 0f,
            -4f,  4f,   0f, 0f,
            -4f, -4f,   0f, 1f
    };

    private int backgroundVBO[] = new int[1];

    private int backgroundTexture;

    public AIThread aiRunnable;

    private CollisionThread collisionRunnable;

    private LevelControllerThread levelRunnable;

    private Thread
            aiThread,
            collisionThread,
            levelThread;

    private Stack<Integer> openEntityIndices = new Stack<>();

    private LinkedList<Enemy> enemyOverflow = new LinkedList<>();

    public GameRenderer(Context c, GLSurfaceView g)
    {
        globalStartTime = System.currentTimeMillis();
        pastTime = System.currentTimeMillis();
        secondMark = System.currentTimeMillis();
        context = c;
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

        xDispLocation = glGetUniformLocation(plainShaderProgram,X_DISP);
        yDispLocation = glGetUniformLocation(plainShaderProgram,Y_DISP);
        aPositionLocation = glGetAttribLocation(plainShaderProgram, A_POSITION);
        aTextureCoordLocation = glGetAttribLocation(plainShaderProgram, A_TEXTURECOORDINATE);
        uTextureLocation = glGetUniformLocation(plainShaderProgram, U_TEXTURE);

        xScaleLocationParticle = glGetUniformLocation(particleShaderProgram,X_SCALE);
        yScaleLocationParticle = glGetUniformLocation(particleShaderProgram,Y_SCALE);
        xScreenShiftLocationParticle = glGetUniformLocation(particleShaderProgram, X_SCREENSHIFT);
        yScreenShiftLocationParticle = glGetUniformLocation(particleShaderProgram, Y_SCREENSHIFT);
        timeLocation = glGetUniformLocation(particleShaderProgram,U_TIME);

        xScreenShiftLocation = glGetUniformLocation(shaderProgram, X_SCREENSHIFT);
        yScreenShiftLocation = glGetUniformLocation(shaderProgram, Y_SCREENSHIFT);
        xScaleLocation = glGetUniformLocation(shaderProgram,X_SCALE);
        yScaleLocation = glGetUniformLocation(shaderProgram,Y_SCALE);

        xScaleLocationPlain = glGetUniformLocation(plainShaderProgram,X_SCALE);
        yScaleLocationPlain = glGetUniformLocation(plainShaderProgram,Y_SCALE);

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

        FloatBuffer temp = ByteBuffer
                .allocateDirect(background.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(background);
        temp.position(0);

        glGenBuffers(1, backgroundVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, backgroundVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, temp.capacity() * Constants.BYTES_PER_FLOAT, temp, GL_STATIC_DRAW);

        backgroundTexture = TextureLoader.loadTexture(context, R.drawable.spacebackground);

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
        if(!isPlaying)
        {
            newGame();
            isPlaying = true;
        }

        if (System.currentTimeMillis() - secondMark >= 1000)
        {
            secondMark = System.currentTimeMillis();
            System.out.println(frames);
            frames = 0;
        }

        double currentTime = System.currentTimeMillis();
        double elapsedTime = currentTime - pastTime;
        pastTime = currentTime;
        lag += elapsedTime;

        while( lag >= mSPU)
        {
            //if(!pause)
            update();
            lag -= mSPU;
        }

        //setInterpolation((((long)lag >> 4)<<4)/mSPU);
        setInterpolation(0);
        glClear(GL_COLOR_BUFFER_BIT);

        drawBackground();

        glUseProgram(particleShaderProgram);
        glUniform1f(timeLocation, (float) ((System.currentTimeMillis() - globalStartTime) / 1000));
        glUniform1f(xScreenShiftLocationParticle, aiRunnable.xScreenShift);
        glUniform1f(yScreenShiftLocationParticle, aiRunnable.yScreenShift);

        enemyParticles.draw();
        playerParticles.draw();

        glUseProgram(shaderProgram);
        glUniform1f(xScreenShiftLocation, aiRunnable.xScreenShift);
        glUniform1f(yScreenShiftLocation, aiRunnable.yScreenShift);

        player1.draw(interpolation);

        for(Enemy e: entities)
        {
            if(e != null)
            {
                if (e.onScreen && e.getPixelGroup().getCollidableLive())
                {
                    e.draw(interpolation);
                }
                if (e.getHasGun())
                {
                    for (GunComponent gC: e.getGunComponents())
                    {
                        gC.gun.draw(0);
                    }
                }
            }
        }
        


        glUseProgram(particleShaderProgram);
        glUniform1f(timeLocation, (float) ((System.currentTimeMillis() - globalStartTime) / 1000));
        glUniform1f(xScreenShiftLocationParticle, aiRunnable.xScreenShift);
        glUniform1f(yScreenShiftLocationParticle, aiRunnable.yScreenShift);

        collisionParticles.draw();

        glUseProgram(plainShaderProgram);
        ui.draw(interpolation);
        
        frames++;
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        glViewport(0, 0, width, height);
        float aspectRatio = width > height ?
                (float) height / (float) width :
                (float) width / (float) height;

        if (width > height)
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
        }

        xbound = 3 * xScale;
        ybound = 3 * yScale;
        aiRunnable.xbound = xbound;
        aiRunnable.ybound = ybound;
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

        player1 = new Player(context, .004f,0f,0f, whiteTexture, shaderProgram, particleShaderProgram, playerParticles);
        player1.xscale = xScale;
        player1.yscale = yScale;

        Enemy[] aiEntities = new Enemy[Constants.ENTITIES_LENGTH];
        Enemy[] collisionEntities = new Enemy[Constants.ENTITIES_LENGTH];
        entities = new Enemy[Constants.ENTITIES_LENGTH];

        Enemy[][] entityList = new Enemy[][]{
                aiEntities,
                collisionEntities,
                entities
        };

        aiRunnable = new AIThread(entities);
        aiThread = new Thread(aiRunnable);
        aiRunnable.setPlayer(player1);

        collisionRunnable = new CollisionThread(entities);
        collisionThread = new Thread(collisionRunnable);
        collisionRunnable.setPlayer(player1);
        collisionRunnable.setCollisionHandler(collisionHandler);

        //levelRunnable = new LevelControllerThread(entityList, enemyFactory);
        levelRunnable = new LevelControllerThread(enemyFactory);
        levelThread = new Thread(levelRunnable);
        levelRunnable.setPlayer(player1);

        aiThread.start();
        collisionThread.start();
        levelThread.start();
    }

    public void update()
    {
        for(int i = 0; i < Constants.ENTITIES_LENGTH; i++)
        {
            if(entities[i] != null)
            {
                if (entities[i].getPixelGroup().getCollidableLive())
                {
                    if (Math.abs(entities[i].getPixelGroup().getCenterX() - aiRunnable.xScreenShift) * xScale <=
                        1 + entities[i].getPixelGroup().getHalfSquareLength() &&
                        Math.abs(entities[i].getPixelGroup().getCenterY() - aiRunnable.yScreenShift) * yScale <=
                        1 + entities[i].getPixelGroup().getHalfSquareLength())
                    {
                        entities[i].onScreen = true;
                    }
                    else
                    {
                        entities[i].onScreen = false;
                    }
                }
                else if(!entities[i].getPixelGroup().getCollidableLive() &&
                        entities[i].aiRemoveConsensus &&
                        entities[i].collisionRemoveConsensus)
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
                    entities[i] = null;
                    openEntityIndices.push(i);
                }
            }
        }

        if(!levelRunnable.enemiesToAdd.isEmpty())
        {
            Enemy e = levelRunnable.enemiesToAdd.peek();
            levelRunnable.enemiesToAdd.remove(e);
            if(!openEntityIndices.isEmpty())
            {
                int index = openEntityIndices.pop();
                entities[index] = e;
                aiRunnable.entities[index] = e;
                collisionRunnable.entities[index] = e;
            }
            else
            {
                enemyOverflow.add(e);
            }
        }

        /*if(System.currentTimeMillis() - spawnDelay > lastSpawn)
        {
            lastSpawn = System.currentTimeMillis();
            System.out.println("Spawned");
            Asteroid a = null;

            switch((int)(Math.random()*5.9))
            {
                case 0: a = (Asteroid)enemyFactory.getNewEnemy(ASTEROID_GREY_MEDIUM); break;
                case 1: a = (Asteroid)enemyFactory.getNewEnemy(ASTEROID_GREY_SMALL); break;
                case 2: a = (Asteroid)enemyFactory.getNewEnemy(ASTEROID_GREY_TINY); break;
                case 3: a = (Asteroid)enemyFactory.getNewEnemy(ASTEROID_RED_SMALL); break;
                case 4: a = (Asteroid)enemyFactory.getNewEnemy(ASTEROID_RED_SMALL); break;
                case 5: a = (Asteroid)enemyFactory.getNewEnemy(ASTEROID_RED_MEDIUM); break;
            }
            //distributeEnemy(a);
            int index = openEntityIndices.pop();
            //aiRunnable.entities[index] = a;
            //collisionRunnable.entities[index] = a;
            entities[index] = a;
            aiRunnable.entities[index] = a;
            collisionRunnable.entities[index] = a;
            //uiEntities[index] = a;
        }*/
        aiRunnable.frameRequest++;
    }

    public void drawBackground()
    {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, backgroundTexture);
        glUniform1i(uTextureLocation, 0);

        glUniform1f(xDispLocation, -aiRunnable.xScreenShift);
        glUniform1f(yDispLocation, -aiRunnable.yScreenShift);

        glBindBuffer(GL_ARRAY_BUFFER, backgroundVBO[0]);
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer (aPositionLocation, 2,
                GL_FLOAT, false, Constants.BYTES_PER_FLOAT * 4,0 );

        glEnableVertexAttribArray(aTextureCoordLocation);
        glVertexAttribPointer (aTextureCoordLocation, 2,
                GL_FLOAT, false, Constants.BYTES_PER_FLOAT * 4,Constants.BYTES_PER_FLOAT * 2);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }

    public void init()
    {
        playerParticles = new ParticleSystem(2000, particleShaderProgram , whiteTexture, globalStartTime);

        enemyParticles = new ParticleSystem(30000, particleShaderProgram, whiteTexture, globalStartTime);

        enemyFactory = initEnemyFactory();

        collisionParticles = new ParticleSystem(4000, particleShaderProgram, whiteTexture, globalStartTime);

        collisionHandler = new CollisionHandler(collisionParticles);

        ui = new UI(context, plainShaderProgram);
    }

    public EnemyFactory initEnemyFactory()
    {
        EnemyFactory e = new EnemyFactory();

        e.addEnemyToCatalog
                (
                        SIMPLE,
                        new Simple
                                (
                                        ImageParser.parseImage(context, R.drawable.simple1, R.drawable.simple_light, whiteTexture, shaderProgram),
                                        new BasicGun
                                                (
                                                        ImageParser.parseImage(context, R.drawable.basicbullet, R.drawable.basicbullet_light, whiteTexture, shaderProgram),
                                                        enemyParticles
                                                ),
                                        enemyParticles
                                )
                );

        e.addEnemyToCatalog
                (
                        CARRIER,
                        new Carrier
                                (
                                        ImageParser.parseImage(context, R.drawable.carrier3, R.drawable.carrier_light1, whiteTexture, shaderProgram),
                                        enemyParticles
                                )
                );

        e.addEnemyToCatalog
                (
                        ASTEROID_GREY_TINY,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidgraysmall, R.drawable.asteroidgraysmall_light, whiteTexture, shaderProgram),
                                        enemyParticles
                                )
                );

        e.addEnemyToCatalog
                (
                        ASTEROID_RED_TINY,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidredtiny, R.drawable.asteroidredtiny_light, whiteTexture, shaderProgram),
                                        enemyParticles
                                )
                );

        e.addEnemyToCatalog
                (
                        ASTEROID_GREY_SMALL,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidgraymedium, R.drawable.asteroidgraymedium_light, whiteTexture, shaderProgram),
                                        enemyParticles
                                )
                );

        e.addEnemyToCatalog
                (
                        ASTEROID_RED_SMALL,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidredsmall, R.drawable.asteroidredsmall_light, whiteTexture, shaderProgram),
                                        enemyParticles
                                )
                );

        e.addEnemyToCatalog
                (
                        ASTEROID_GREY_MEDIUM,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidgraylarge, R.drawable.asteroidgraylarge_light, whiteTexture, shaderProgram),
                                        enemyParticles
                                )
                );


        e.addEnemyToCatalog
                (
                        ASTEROID_RED_MEDIUM,
                        new Asteroid
                                (
                                        ImageParser.parseImage(context, R.drawable.asteroidredlarge, R.drawable.asteroidredlarge_light, whiteTexture, shaderProgram),
                                        enemyParticles
                                )
                );

        return e;
    }
}