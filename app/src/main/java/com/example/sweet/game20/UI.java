package com.example.sweet.game20;

import android.content.Context;
import android.graphics.PointF;


import com.example.sweet.game20.Objects.Drawable;
import com.example.sweet.game20.util.Constants;
import com.example.sweet.game20.util.TextureLoader;
import com.example.sweet.game20.util.VectorFunctions;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glUniform1i;
/**
 * Created by Sweet on 2/14/2018.
 */

public class UI extends Drawable
{
    private float
            joyStickRadius = .32f;

    public PointF
            menuOnDown = new PointF(0,0),
            menuOnMove = new PointF(0,0),
            movementOnDown = new PointF(0,0),
            movementOnMove = new PointF(0,0),
            shootingOnDown = new PointF(0,0),
            shootingOnMove = new PointF(0,0);

    private int
            xDispLoc,
            yDispLoc,
            aPositionLocation,
            aTextureCoordLocation,
            uTextureLocation;

    private int
            swapButtonTexture,
            resumeButtonTexture,
            moveJoyStickBaseTexture,
            moveJoyStickTexture,
            shootJoyStickBaseTexture,
            shootJoyStickTexture,
            screenShadeTexture,
            componentSqaureTexture;

    public boolean menuPointerDown = false;

    private boolean
            movementDown = false,
            shootingDown = false;

    private static final String
            A_POSITION = "a_Position",
            A_TEXTURECOORDINATE = "a_TexCoordinate",
            U_TEXTURE = "u_Texture",
            X_DISP = "x_displacement",
            Y_DISP = "y_displacement";

    private float[] joyBaseMovement = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -joyStickRadius, -joyStickRadius,   0f, 1f,
            joyStickRadius, -joyStickRadius,   1f, 1f,
            joyStickRadius,  joyStickRadius,   1f, 0f,
            -joyStickRadius,  joyStickRadius,   0f, 0f,
            -joyStickRadius, -joyStickRadius,   0f, 1f
            };

    private float[] joyStickMovement = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -joyStickRadius/1.8f, -joyStickRadius/1.8f,   0f, 1f,
            joyStickRadius/1.8f, -joyStickRadius/1.8f,   1f, 1f,
            joyStickRadius/1.8f,  joyStickRadius/1.8f,   1f, 0f,
            -joyStickRadius/1.8f,  joyStickRadius/1.8f,   0f, 0f,
            -joyStickRadius/1.8f, -joyStickRadius/1.8f,   0f, 1f
    };

    private float[]
            joyBaseShooting = joyBaseMovement,
            joyStickShooting = joyStickMovement;

    private float[] swapButton = new float[]{
            0f,    0f, 0.5f, 0.5f,
            -0.5f, -0.1f,   0f, 1f,
            0.5f, -0.1f,   1f, 1f,
            0.5f,  0.1f,   1f, 0f,
            -0.5f,  0.1f,   0f, 0f,
            -0.5f, -0.1f,   0f, 1f
    };

    private static final int
            POSITION_COMPONENT_COUNT = 2,
            COLOR_COMPONENT_COUNT = 4,
            STRIDE = Constants.BYTES_PER_FLOAT * (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT);

    private static final float
            UNPAUSE_BUTTON_XBOUND = .8f,
            UNPAUSE_BUTTON_YBOUND = .4f;

    private float[] resumeButton = new float[]{
            UNPAUSE_BUTTON_XBOUND + .1f,  UNPAUSE_BUTTON_YBOUND + .3f, 0.5f, 0.5f,
            1f,  1f,   1f, 1f,
            1f, UNPAUSE_BUTTON_YBOUND,   1f, 0f,
            UNPAUSE_BUTTON_XBOUND, UNPAUSE_BUTTON_YBOUND,   0f, 0f,
            UNPAUSE_BUTTON_XBOUND,  1f,   0f, 1f,
            1f,  1f,   1f, 1f,
    };

    private float[] screenShade = new float[]{
            0f,  0f, 0.5f, 0.5f,
            1f,  1f,   1f, 1f,
            1f, -1f,   1f, 0f,
            -1f, -1f,   0f, 0f,
            -1f,  1f,   0f, 1f,
            1f,  1f,   1f, 1f,
    };
    
    private float[] componentSquare = new float[]{
            0f,  0f, 0.5f, 0.5f,
            .2f,  2f,   1f, 1f,
            .2f, -.2f,   1f, 0f,
            -.2f, -.2f,   0f, 0f,
            -.2f,  .2f,   0f, 1f,
            .2f,  .2f,   1f, 1f,
    };

    public float
            xScale,
            yScale;

    private int
            joyBaseMovementVBO[] = new int[1],
            joyStickMovementVBO[] = new int[1],
            joyBaseShootingVBO[] = new int[1],
            joyStickShootingVBO[] = new int[1],
            swapButtonVBO[] = new int[1],
            resumeButtonVBO[] = new int[1],
            screenShadeVBO[] = new int[1],
            componentSquareVBO[] = new int[1];

    private FloatBuffer
            joyBaseMovementBuf,
            joyStickMovementBuf,
            joyBaseShootingBuf,
            joyStickShootingBuf,
            swapButtonBuf,
            resumeButtonBuf,
            screenShadeBuf,
            componentSquareBuf;

    /*
    0: Main Menu
    1: In Game
    2: Paused
     */
    public int gameState = 0;

    public UI(Context context, int shaderLocation)
    {
        xDispLoc = glGetUniformLocation(shaderLocation,X_DISP);
        yDispLoc = glGetUniformLocation(shaderLocation,Y_DISP);
        aPositionLocation = glGetAttribLocation(shaderLocation, A_POSITION);
        aTextureCoordLocation = glGetAttribLocation(shaderLocation, A_TEXTURECOORDINATE);
        uTextureLocation = glGetUniformLocation(shaderLocation, U_TEXTURE);

        //swapButtonTexture = TextureLoader.loadTexture(context, R.drawable.swap);
        componentSqaureTexture = TextureLoader.loadTexture(context, R.drawable.square);
        screenShadeTexture = TextureLoader.loadTexture(context, R.drawable.shade);
        resumeButtonTexture = TextureLoader.loadTexture(context, R.drawable.resume);
        moveJoyStickBaseTexture = TextureLoader.loadTexture(context, R.drawable.mjb);
        moveJoyStickTexture = TextureLoader.loadTexture(context, R.drawable.mj);
        shootJoyStickBaseTexture = TextureLoader.loadTexture(context, R.drawable.sjb);
        shootJoyStickTexture = TextureLoader.loadTexture(context, R.drawable.sj);
        
        fillUIBuffers();
        initVBOs();
    }

    public void initVBOs()
    {
        glGenBuffers(1, joyBaseMovementVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, joyBaseMovementVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, joyBaseMovementBuf.capacity() * Constants.BYTES_PER_FLOAT, joyBaseMovementBuf, GL_STATIC_DRAW);

        glGenBuffers(1, joyStickMovementVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, joyStickMovementVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, joyStickMovementBuf.capacity() * Constants.BYTES_PER_FLOAT, joyStickMovementBuf, GL_STATIC_DRAW);

        glGenBuffers(1, joyBaseShootingVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, joyBaseShootingVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, joyBaseShootingBuf.capacity() * Constants.BYTES_PER_FLOAT, joyBaseShootingBuf, GL_STATIC_DRAW);

        glGenBuffers(1, joyStickShootingVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, joyStickShootingVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, joyStickShootingBuf.capacity() * Constants.BYTES_PER_FLOAT, joyStickShootingBuf, GL_STATIC_DRAW);

        glGenBuffers(1, swapButtonVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, swapButtonVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, swapButtonBuf.capacity() * Constants.BYTES_PER_FLOAT, swapButtonBuf, GL_STATIC_DRAW);

        glGenBuffers(1, resumeButtonVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, resumeButtonVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, resumeButtonBuf.capacity() * Constants.BYTES_PER_FLOAT, resumeButtonBuf, GL_STATIC_DRAW);

        glGenBuffers(1, resumeButtonVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, resumeButtonVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, resumeButtonBuf.capacity() * Constants.BYTES_PER_FLOAT, resumeButtonBuf, GL_STATIC_DRAW);

        glGenBuffers(1, resumeButtonVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, resumeButtonVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, resumeButtonBuf.capacity() * Constants.BYTES_PER_FLOAT, resumeButtonBuf, GL_STATIC_DRAW);
    }

    private void fillUIBuffers()
    {
        joyStickMovementBuf = ByteBuffer
                .allocateDirect(joyStickMovement.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(joyStickMovement);
        joyStickMovementBuf.position(0);

        joyBaseMovementBuf = ByteBuffer
                .allocateDirect(joyBaseMovement.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(joyBaseMovement);
        joyBaseMovementBuf.position(0);

        joyStickShootingBuf = ByteBuffer
                .allocateDirect(joyStickShooting.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(joyStickShooting);
        joyStickShootingBuf.position(0);

        joyBaseShootingBuf = ByteBuffer
                .allocateDirect(joyBaseShooting.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(joyBaseShooting);
        joyBaseShootingBuf.position(0);
        
        swapButtonBuf = ByteBuffer
                .allocateDirect(swapButton.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(swapButton);
        swapButtonBuf.position(0);

        resumeButtonBuf = ByteBuffer
                .allocateDirect(swapButton.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(resumeButton);
        resumeButtonBuf.position(0);
    }
    
    public void bindAttributes()
    {
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer (aPositionLocation, 2,
                GL_FLOAT, false, Constants.BYTES_PER_FLOAT * 4,0 );

        glEnableVertexAttribArray(aTextureCoordLocation);
        glVertexAttribPointer (aTextureCoordLocation, 2,
                GL_FLOAT, false, Constants.BYTES_PER_FLOAT * 4,Constants.BYTES_PER_FLOAT * 2);
    }

    public void drawJoySticks()
    {
        if(movementDown)
        {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, moveJoyStickBaseTexture);
            glUniform1i(uTextureLocation, 0);

            glUniform1f(xDispLoc, movementOnDown.x);
            glUniform1f(yDispLoc, -movementOnDown.y);

            glBindBuffer(GL_ARRAY_BUFFER, joyBaseMovementVBO[0]);
            bindAttributes();
            glDrawArrays(GL_TRIANGLE_FAN, 0, 6);


            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, moveJoyStickTexture);
            glUniform1i(uTextureLocation, 0);

            float xTempDifference = (movementOnMove.x - movementOnDown.x);
            float yTempDifference = (movementOnMove.y - movementOnDown.y);
            float tempMagnitude = VectorFunctions.getMagnitude(xTempDifference, yTempDifference);

            if (tempMagnitude > joyStickRadius * .8f)
            {
                glUniform1f(xDispLoc, joyStickRadius * .8f * (xTempDifference / tempMagnitude) + movementOnDown.x);
                glUniform1f(yDispLoc, -(joyStickRadius * .8f * (yTempDifference / tempMagnitude) + movementOnDown.y));
            } else
            {
                glUniform1f(xDispLoc, movementOnMove.x);
                glUniform1f(yDispLoc, -movementOnMove.y);
            }

            glBindBuffer(GL_ARRAY_BUFFER, joyStickMovementVBO[0]);
            bindAttributes();
            glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
        }
        if(shootingDown)
        {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, shootJoyStickBaseTexture);
            glUniform1i(uTextureLocation, 0);

            glUniform1f(xDispLoc, shootingOnDown.x);
            glUniform1f(yDispLoc, -shootingOnDown.y);

            glBindBuffer(GL_ARRAY_BUFFER, joyBaseShootingVBO[0]);
            bindAttributes();
            glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, shootJoyStickTexture);
            glUniform1i(uTextureLocation, 0);

            //Set displacement, clamp max displacement from base, and draw the joystick.
            float xTempDifference = (shootingOnMove.x - shootingOnDown.x);
            float yTempDifference = (shootingOnMove.y - shootingOnDown.y);
            float tempMagnitude = VectorFunctions.getMagnitude(xTempDifference, yTempDifference);

            if (tempMagnitude > joyStickRadius * .8f)
            {
                glUniform1f(xDispLoc, joyStickRadius * .8f * (xTempDifference / tempMagnitude) + shootingOnDown.x);
                glUniform1f(yDispLoc, -(joyStickRadius * .8f * (yTempDifference / tempMagnitude) + shootingOnDown.y));
            }
            else
            {
                glUniform1f(xDispLoc, shootingOnMove.x);
                glUniform1f(yDispLoc, -shootingOnMove.y);
            }

            glBindBuffer(GL_ARRAY_BUFFER, joyStickShootingVBO[0]);
            bindAttributes();
            glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
        }

        /*glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, swapButtonTexture);
        glUniform1i(uTextureLocation, 0);

        glUniform1f(xDispLoc, 0);
        glUniform1f(yDispLoc, .7f);

        glBindBuffer(GL_ARRAY_BUFFER, swapButtonVBO[0]);
        bindAttributes();

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);*/
    }

    @Override
    public void draw(double i)
    {
        if(gameState == 1)
        {
            drawJoySticks();
        }
        else if(gameState == 2)
        {
            drawPausedMenu();
        }
    }

    public void drawPausedMenu()
    {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, resumeButtonTexture);
        glUniform1i(uTextureLocation, 0);

        glUniform1f(xDispLoc, 0);
        glUniform1f(yDispLoc, 0f);

        glBindBuffer(GL_ARRAY_BUFFER, resumeButtonVBO[0]);
        bindAttributes();

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }


    public boolean checkPause(float x, float y)
    {
        System.out.println(x + ", " + y + "    " + xScale + ", " + yScale);
        if(x >= UNPAUSE_BUTTON_XBOUND / xScale && y <= -UNPAUSE_BUTTON_YBOUND / yScale)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public void setScale(float xS,float yS)
    {
        xScale = xS;
        yScale = yS;

        for(int i = 4; i <= resumeButton.length; i += 4)
        {
            resumeButton[i-4] = resumeButton[i-4] / xScale;
            resumeButton[i-3] = resumeButton[i-3] / yScale;
            resumeButtonBuf.put(resumeButton);
            resumeButtonBuf.position(0);
            glBindBuffer(GL_ARRAY_BUFFER, resumeButtonVBO[0]);
            glBufferData(GL_ARRAY_BUFFER, resumeButtonBuf.capacity() * Constants.BYTES_PER_FLOAT, resumeButtonBuf, GL_STATIC_DRAW);
        }
    }

    public void setMovementDown(boolean b)
    {
        movementDown = b;
    }

    public void setShootingDown(boolean b)
    {
        shootingDown = b;
    }
}
