package com.example.sweet.game20;

import android.content.Context;

import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Vibrator;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import com.example.sweet.game20.util.*;
import com.example.sweet.game20.Objects.*;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

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

public class GameRenderer implements Renderer{

    //private ArrayList<Drawable> drawables = new ArrayList<>();

    public boolean
            movementDown = false,
            shootingDown = false,
            pause = false;

    public PointF
            movementOnDown = new PointF(0,0),
            movementOnMove = new PointF(0,0),
            shootingOnDown = new PointF(0,0),
            shootingOnMove = new PointF(0,0),
            panToward = new PointF(0,0);

    private double
            globalStartTime,
            secondMark,
            interpolation = 0;

    private final Context context;

    public float
            xScale,
            yScale,
            xbound,
            ybound,
            xScreenShift = 0,
            yScreenShift = 0,
            cameraSpeed = .014f,
            cameraPanX = 0f,
            cameraPanY = 0f,
            cameraClamp = .16f;

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

    private int
            frames,
            difficulty = 2;

    private int highestColCheck;

    private final long MILLIS_PER_SECOND = 1000;
    private final long UPS = 60;

    private final long mSPU = MILLIS_PER_SECOND / UPS;

    private ArrayList<Enemy> entities = new ArrayList<>();

    private ParticleSystem playerParticles;
    private ParticleSystem enemyParticles;
    private ParticleSystem collisionParticles;
    private CollisionHandler collisionHandler;
    private int whiteTexture;
    private boolean isPlaying = false;

    private Player player1;
    public UI ui;
    private double pastTime;
    private double lag = 0.0;

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

    private PixelGroup simple;
    private PixelGroup carrier;

    private AIThread 
            aiRunnable, 
            aiRunnable1;
            //aiRunnable2;
    
    //private CollisionThread collisionRunnable;

    private Thread 
            aiThread,
            aiThread1;
            //aiThread2,
            //collisionThread;

    //private Simple simpleTemplate = new Simple()
    public GameRenderer(Context c, double gst, GLSurfaceView g)
    {
        globalStartTime = gst;
        pastTime = System.currentTimeMillis()-globalStartTime;
        secondMark = System.currentTimeMillis() - globalStartTime;
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

        simple = ImageParser.parseImage(context, R.drawable.simple1, R.drawable.simple_light, whiteTexture, shaderProgram);
        carrier = ImageParser.parseImage(context, R.drawable.carrier3, R.drawable.carrier_light1, whiteTexture, shaderProgram);

        /*System.out.println("PLAYER  ----------");
        ImageParser.parseImage(context, R.drawable.player1, R.drawable.player_light, whiteTexture, shaderProgram);
        System.out.println("BULLET  ----------");
        ImageParser.parseImage(context, R.drawable.basicbullet, R.drawable.basicbullet_light, whiteTexture, shaderProgram);*/

        aiRunnable = new AIThread();
        aiRunnable1 = new AIThread();
        //aiRunnable2 = new AIThread();
        aiThread = new Thread(aiRunnable);
        aiThread1 = new Thread(aiRunnable1);
        aiThread.start();
        aiThread1.start();
        //aiThread2 = new Thread(aiRunnable2);

        //collisionRunnable = new CollisionThread();
        //collisionThread = new Thread(collisionRunnable);
        //collisionThread.start();
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {
        if(!isPlaying)
        {
            newGame();
            isPlaying = true;
        }
        if (System.currentTimeMillis()-globalStartTime - secondMark >= 1000)
        {
            secondMark = System.currentTimeMillis()-globalStartTime;
            System.out.println(frames);
            frames = 0;
        }

        double currentTime = System.currentTimeMillis()-globalStartTime;
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

        glUseProgram(shaderProgram);
        glUniform1f(xScreenShiftLocation, xScreenShift);
        glUniform1f(yScreenShiftLocation, yScreenShift);

        int s = entities.size();
        for(int i = 0; i < s; i++)
        {
            if (entities.get(i).onScreen && entities.get(i).getPixelGroup().getCollidableLive())
                entities.get(i).draw(interpolation);
            if(entities.get(i).getHasGun())
            {
                int gS = entities.get(i).getGunComponents().length;
                for (int g = 0; g < gS; g++)
                    entities.get(i).getGunComponents()[g].gun.draw(0);
            }
        }
        
        player1.draw(interpolation);

        glUseProgram(particleShaderProgram);
        glUniform1f(timeLocation, (float) ((System.currentTimeMillis() - globalStartTime) / 1000));
        glUniform1f(xScreenShiftLocationParticle, xScreenShift);
        glUniform1f(yScreenShiftLocationParticle, yScreenShift);


        enemyParticles.draw();
        collisionParticles.draw();
        playerParticles.draw();

        glUseProgram(plainShaderProgram);
        ui.draw(interpolation);
        
        frames++;
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        //Detect and set aspect ratio
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
            // Landscape
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
            // Portrait or square
        }

        xbound = 3 * xScale;
        ybound = 3 * yScale;
    }

    public void setInterpolation(double i)
    {
        interpolation = i;
    }
    
    public void newGame()
    {
        ui = new UI(context, plainShaderProgram);

        playerParticles = new ParticleSystem(2000, globalStartTime ,particleShaderProgram , whiteTexture);
        collisionParticles = new ParticleSystem(4000, globalStartTime, particleShaderProgram, whiteTexture);
        enemyParticles = new ParticleSystem(30000, globalStartTime ,particleShaderProgram,whiteTexture);

        collisionHandler = new CollisionHandler(collisionParticles);

        player1 = new Player(context, .004f,0f,0f, globalStartTime, whiteTexture, shaderProgram, particleShaderProgram, playerParticles);
        player1.xscale = xScale;
        player1.yscale = yScale;

        aiRunnable.setPlayer(player1);
        aiRunnable1.setPlayer(player1);

        //aiRunnable2.player1 = player1;
        //collisionRunnable.setPlayer(player1);
        //collisionRunnable.setCollisionHandler(collisionHandler);

        levelController();
    }

    public void enemyActions()
    {
        int eS = entities.size();
        for(int i = 0; i < eS; i++)
        {
            /*  Move all entities;
             */
            if(entities.get(i).getPixelGroup().getCollidableLive())
            {
                entities.get(i).move(player1.getPixelGroup().getCenterX(), player1.getPixelGroup().getCenterY());
            }
        }
    }

    public void update()
    {
        //int s = entities.size();
        Iterator<Enemy> itr = entities.iterator();

        /*for(int i = 0; i < s; i++)
        {*/
        while(itr.hasNext())
        {
            Enemy e = itr.next();
            if(e.getPixelGroup().getCollidableLive())
            {
                if (Math.abs(e.getPixelGroup().getCenterX() - xScreenShift) * xScale <= 1 + e.getPixelGroup().getHalfSquareLength() &&
                        Math.abs(e.getPixelGroup().getCenterY() - yScreenShift) * yScale <= 1 + e.getPixelGroup().getHalfSquareLength())
                {
                    e.onScreen = true;
                } else
                {
                    e.onScreen = false;
                }
            }
            else
            {
                itr.remove();
            }
            /*Enemy e = itr.next();
            if (Math.abs(entities.get(i).getPixelGroup().getCenterX() - xScreenShift) * xScale <= 1 + entities.get(i).getPixelGroup().getHalfSquareLength() &&
                    Math.abs(entities.get(i).getPixelGroup().getCenterY() - yScreenShift) * yScale <= 1 + entities.get(i).getPixelGroup().getHalfSquareLength())
            {
                entities.get(i).onScreen = true;
            }
            else
            {
                entities.get(i).onScreen = false;
            }*/
        }
        //System.out.println("got out ============");
        //enemyActions();

        movePlayer();
        //enemyActions();
        aiRunnable.aImove = true;
        aiRunnable1.aImove = true;
        checkCollisions();
        //aiRunnable2.aImove = true;
        //collisionRunnable.checkCollision = true;
        levelController();
    }

    public void checkCollisions()
    {
        /*int eS = entities.size();
        for(int i = 0; i < eS; i++)
        {
    
            if (entities.get(i).getPixelGroup().getCollidableLive() && entities.get(i).onScreen)
            {
                // Player -> Entity
                collisionHandler.checkCollisions(player1.getPixelGroup(), entities.get(i).getPixelGroup());

                // Player Gun's Bullets -> Entity
                int gS = player1.getGuns().length;
                for(int gI = 0; gI < gS; gI++)
                {
                    if (player1.getGuns()[gI] != null)
                    {
                        int bS = player1.getGuns()[gI].gun.getBullets().length;
                        for(int bI = 0; bI < bS; bI++)
                        {
                            if(player1.getGuns()[gI].gun.getBullets()[bI].active && player1.getGuns()[gI].gun.getBullets()[bI].live)
                                collisionHandler.checkCollisions(player1.getGuns()[gI].gun.getBullets()[bI].pixelGroup, entities.get(i).getPixelGroup());
                        }
                    }
                }

                // Entity Gun's Bullets -> Player
                if(entities.get(i).getHasGun())
                {
                    int g2S = entities.get(i).getGunComponents().length;
                    for (int gI = 0; gI < g2S; gI++)
                    {
                        if (entities.get(i).getGunComponents()[gI] != null)
                        {
                            int bS = entities.get(i).getGunComponents()[gI].gun.getBullets().length;
                            for (int bI = 0; bI < bS; bI++)
                            {
                                if (entities.get(i).getGunComponents()[gI].gun.getBullets()[bI].active && entities.get(i).getGunComponents()[gI].gun.getBullets()[bI].live)
                                    collisionHandler.checkCollisions(entities.get(i).getGunComponents()[gI].gun.getBullets()[bI].pixelGroup, player1.getPixelGroup());
                            }
                        }
                    }
                }
            }

            // Entity -> Other Entity
            int eSize = entities.size();
            for(int i2 = 0; i2 < eSize; i2++)
            {
                if (entities.get(i) != entities.get(i2))
                {
                    collisionHandler.preventOverlap(entities.get(i).getPixelGroup(), entities.get(i2).getPixelGroup());
                }
            }
        }*/
        for(Enemy e: entities)
        {
            if (e.getPixelGroup().getCollidableLive() && e.onScreen)
            {
                // Player -> Entity
                collisionHandler.checkCollisions(player1.getPixelGroup(), e.getPixelGroup());

                // Player Gun's Bullets -> Entity
                for(GunComponent gc: player1.getGuns())
                {
                    if (gc != null)
                    {
                        for(Bullet b: gc.gun.getBullets())
                        {
                            if(b.live)
                            {
                                collisionHandler.checkCollisions(b.pixelGroup, e.getPixelGroup());
                            }
                        }
                    }
                }

                // Entity Gun's Bullets -> Player
                if(e.getHasGun())
                {
                    for(GunComponent gc: e.getGunComponents())
                    {
                        if (gc != null)
                        {
                            for(Bullet b: gc.gun.getBullets())
                            {
                                if (b.live)
                                {
                                    collisionHandler.checkCollisions(b.pixelGroup, player1.getPixelGroup());
                                }
                            }
                        }
                    }
                }
            }

            for(Enemy e2: entities)
            {
                if (e != e2)
                {
                    collisionHandler.preventOverlap(e.getPixelGroup(), e2.getPixelGroup());
                }
            }
        }
    }

    public void movePlayer() {
        if (movementOnDown.x > .8f) {
            player1.getPixelGroup().resetPixels();
        }

        if (movementDown) {
            player1.move(movementOnMove.x - movementOnDown.x, movementOnMove.y - movementOnDown.y);
        }

        if (shootingDown) {
            float diffX = shootingOnMove.x - shootingOnDown.x;
            float diffY = shootingOnMove.y - shootingOnDown.y;
            float tempMagnitude = VectorFunctions.getMagnitude(diffX, diffY);

            if (tempMagnitude > .1) {
                player1.shoot(diffX, diffY);
                panToward.set(cameraClamp * diffX / tempMagnitude, cameraClamp * diffY / tempMagnitude);
            } else {
                panToward.set(0, 0);
            }
        } else {
            panToward.set(0, 0);
        }

        float panAngle;
        float panDiffX = panToward.x - cameraPanX;
        float panDiffY = panToward.y - cameraPanY;
        float panMag = VectorFunctions.getMagnitude(panDiffX, panDiffY);

        if (panMag > .01) {
            panAngle = (float) Math.atan2(panToward.y - cameraPanY, panToward.x - cameraPanX);
            cameraPanX += cameraSpeed * Math.cos(panAngle);
            cameraPanY += cameraSpeed * Math.sin(panAngle);
        } else {
            cameraPanX = panToward.x;
            cameraPanY = panToward.y;
        }

        if (player1.getPixelGroup().getCenterX() + cameraPanX < xbound && player1.getPixelGroup().getCenterX() + cameraPanX > -xbound)
        {
            xScreenShift = player1.getPixelGroup().getCenterX() + cameraPanX;
        }
        if (player1.getPixelGroup().getCenterY() - cameraPanY < ybound && player1.getPixelGroup().getCenterY() - cameraPanY > -ybound)
        {
            yScreenShift = player1.getPixelGroup().getCenterY() - cameraPanY;
        }

        //for(GunComponent g: player1.getGuns())
        int gS = player1.getGuns().length;
        for(int i = 0; i < gS; i++)
        {
            if (player1.getGuns()[i] != null)
            {
                player1.getGuns()[i].gun.move();
            }
        }
    }

    public void levelController()
    {
        if(entities.size() == 0)
        {
            //catchAIThreads();
            ArrayList<Enemy> tempList = new ArrayList<>();
            ArrayList<Enemy> tempList1 = new ArrayList<>();
            //ArrayList<Enemy> totalList = new ArrayList<>();
            for(int i = 0; i < difficulty; i++)
            {
                /*Simple s = new Simple(
                        simple.clone(),
                        shaderProgram,
                        new BasicGun(globalStartTime, whiteTexture, shaderProgram, particleShaderProgram, context, enemyParticles),
                        enemyParticles,
                        0);
                s.setLoc((float) (Math.random() * 2 - 1), (float) (Math.random() * 2 - 1));*/
                Carrier s = new Carrier(carrier.clone(),
                        shaderProgram,
                        enemyParticles,
                        0);
                s.setLoc((float) (Math.random() * 2 - 1), (float) (Math.random() * 2 - 1));
                entities.add(s);
                //totalList.add(s);
                if(i < difficulty * .5)
                    tempList.add(s);
                else
                    tempList1.add(s);
            }

            aiRunnable.setEntities(tempList);
            aiRunnable1.setEntities(tempList1);
            //collisionRunnable.setEntities(totalList);
        }
    }

    public void drawBackground()
    {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, backgroundTexture);
        glUniform1i(uTextureLocation, 0);

        glUniform1f(xDispLocation, -xScreenShift);
        glUniform1f(yDispLocation, -yScreenShift);

        glBindBuffer(GL_ARRAY_BUFFER, backgroundVBO[0]);
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer (aPositionLocation, 2,
                GL_FLOAT, false, Constants.BYTES_PER_FLOAT * 4,0 );

        glEnableVertexAttribArray(aTextureCoordLocation);
        glVertexAttribPointer (aTextureCoordLocation, 2,
                GL_FLOAT, false, Constants.BYTES_PER_FLOAT * 4,Constants.BYTES_PER_FLOAT * 2);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}