package com.example.sweet.game20;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

class GamePlayer
{
    private final Context context;

    private GLSurfaceView glSurfaceView;

    private GameRenderer gameRender;

    private static final int INVALID_POINTER_ID = -1;

    private int
            movementPointerId = INVALID_POINTER_ID,
            shootingPointerId = INVALID_POINTER_ID;

    private boolean
            movementDown = false,
            shootingDown = false;

    private double globalStartTime;


    public GamePlayer(Context context, Point size, GLSurfaceView glSV)
    {
        globalStartTime = System.currentTimeMillis();
        this.context = context;
        glSurfaceView = glSV;
        gameRender = new GameRenderer(context, glSV);
        glSurfaceView.setEGLConfigChooser(8,8,8,8,16,0);
        glSurfaceView.setRenderer(gameRender);
        //eglSwapInterval(0);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        glSurfaceView.setOnTouchListener(new OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {

                    final int action = event.getAction();
                    final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                            >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = event.getPointerId(pointerIndex);

                    switch (action & MotionEvent.ACTION_MASK)
                    {
                        case MotionEvent.ACTION_DOWN:
                        {
                            //Set movement to down, set movement Id, set movement data
                            movementPointerId = event.getPointerId(0);
                            movementDown = true;

                            //gameRender.movementDown = true;
                            gameRender.ui.setMovementDown(true);
                            gameRender.aiRunnable.movementDown = true;

                            final float normX = ((event.getX(event.findPointerIndex(movementPointerId)) / v.getWidth())*2 - 1)/gameRender.xScale;
                            final float normY = ((event.getY(event.findPointerIndex(movementPointerId)) / v.getHeight())*2 - 1)/gameRender.yScale;

                            //gameRender.movementOnDown.set(normX, normY);
                            //gameRender.movementOnMove.set(normX, normY);
                            gameRender.ui.movementOnDown.set(normX, normY);
                            gameRender.ui.movementOnMove.set(normX, normY);
                            gameRender.aiRunnable.movementOnDownX = normX;
                            gameRender.aiRunnable.movementOnDownY = normY;
                            gameRender.aiRunnable.movementOnMoveX = normX;
                            gameRender.aiRunnable.movementOnMoveY = normY;
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                        {
                            if(movementDown){
                                movementPointerId = event.INVALID_POINTER_ID;
                                movementDown = false;
                                //gameRender.movementDown = false;
                                gameRender.ui.setMovementDown(false);
                                gameRender.aiRunnable.movementDown = false;
                            }
                            if(shootingDown){
                                shootingPointerId = event.INVALID_POINTER_ID;
                                shootingDown = false;
                                //gameRender.shootingDown = false;
                                gameRender.ui.setShootingDown(false);
                                gameRender.aiRunnable.shootingDown = false;
                            }
                            break;
                        }
                        case MotionEvent.ACTION_POINTER_DOWN:
                        {

                            if(movementDown && !shootingDown)
                            {
                                shootingPointerId = pointerId;
                                shootingDown = true;

                                //gameRender.shootingDown = true;
                                gameRender.ui.setShootingDown(true);
                                gameRender.aiRunnable.shootingDown = true;

                                final float normX = ((event.getX(event.findPointerIndex(shootingPointerId)) / v.getWidth())*2 - 1)/gameRender.xScale;
                                final float normY = ((event.getY(event.findPointerIndex(shootingPointerId)) / v.getHeight())*2 - 1)/gameRender.yScale;

                                //gameRender.shootingOnDown.set(normX, normY);
                                //gameRender.shootingOnMove.set(normX, normY);
                                gameRender.ui.shootingOnDown.set(normX, normY);
                                gameRender.ui.shootingOnMove.set(normX, normY);
                                gameRender.aiRunnable.shootingOnDownX = normX;
                                gameRender.aiRunnable.shootingOnDownY = normY;
                                gameRender.aiRunnable.shootingOnMoveX = normX;
                                gameRender.aiRunnable.shootingOnMoveY = normY;
                            }
                            else if(shootingDown && !movementDown)
                            {
                                movementPointerId = pointerId;
                                movementDown = true;

                                //gameRender.movementDown = true;
                                gameRender.ui.setMovementDown(true);
                                gameRender.aiRunnable.movementDown = true;

                                final float normX = ((event.getX(event.findPointerIndex(movementPointerId)) / v.getWidth())*2 - 1)/gameRender.xScale;
                                final float normY = ((event.getY(event.findPointerIndex(movementPointerId)) / v.getHeight())*2 - 1)/gameRender.yScale;

                                //gameRender.movementOnDown.set(normX, normY);
                                //gameRender.movementOnMove.set(normX, normY);
                                gameRender.ui.movementOnDown.set(normX, normY);
                                gameRender.ui.movementOnMove.set(normX, normY);
                                gameRender.aiRunnable.movementOnDownX = normX;
                                gameRender.aiRunnable.movementOnDownY = normY;
                                gameRender.aiRunnable.movementOnMoveX = normX;
                                gameRender.aiRunnable.movementOnMoveY = normY;
                            }
                            break;
                        }
                        case MotionEvent.ACTION_POINTER_UP:
                        {
                            if (pointerId == shootingPointerId && shootingDown)
                            {
                                shootingPointerId = event.INVALID_POINTER_ID;
                                shootingDown = false;
                                //gameRender.shootingDown = false;
                                gameRender.ui.setShootingDown(false);
                                gameRender.aiRunnable.shootingDown = false;
                            }
                            else if (pointerId == movementPointerId && movementDown)
                            {
                                movementPointerId = event.INVALID_POINTER_ID;
                                movementDown = false;
                                //gameRender.movementDown = false;
                                gameRender.ui.setMovementDown(false);
                                gameRender.aiRunnable.movementDown = false;
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE:
                        {
                            // Find the index of the active pointer and fetch its position
                            if (shootingDown)
                            {
                                //System.out.println("moving shooting--------------------------------------" + shootingPointerId);
                                final float normX = ((event.getX(event.findPointerIndex(shootingPointerId)) / v.getWidth())*2 - 1)/gameRender.xScale;
                                final float normY = ((event.getY(event.findPointerIndex(shootingPointerId)) / v.getHeight())*2 - 1)/gameRender.yScale;
                                //gameRender.shootingOnMove.set(normX, normY);
                                gameRender.ui.shootingOnMove.set(normX, normY);
                                gameRender.aiRunnable.shootingOnMoveX = normX;
                                gameRender.aiRunnable.shootingOnMoveY = normY;
                            }
                            if (movementDown)
                            {
                                final float normX = ((event.getX(event.findPointerIndex(movementPointerId)) / v.getWidth())*2 - 1)/gameRender.xScale;
                                final float normY = ((event.getY(event.findPointerIndex(movementPointerId)) / v.getHeight())*2 - 1)/gameRender.yScale;
                                //gameRender.movementOnMove.set(normX, normY);
                                gameRender.ui.movementOnMove.set(normX, normY);
                                gameRender.aiRunnable.movementOnMoveX = normX;
                                gameRender.aiRunnable.movementOnMoveY = normY;
                            }
                            break;
                        }
                    }

                    return true;
                }
            });

        // Start the game
        //newGame();
    }

    /*public void newGame()
    {
        ui = new UI();
        collisionParticles = new ParticleSystem(4000, globalStartTime,context);
        collisionHandler = new CollisionHandler(collisionParticles);
        gameRender.collisionParticles = collisionParticles;
        player1 = new Player(context, .004f,.1f,0f,0f, globalStartTime);

        for(float i = -1.4f; i < 1.4; i+=.40)
        {
            Enemy t;
            if(i <  0)
                t = new Enemy(1, context);
            else
                t = new Enemy(0, context);
            t.setLoc(-.7f,i);
            gameRender.addDrawable(t);
            entities.add(t);
        }

        for(float w = -1f; w <= 1f; w+= .1){
            for(float i = -1.4f; i < 1.4; i+=.10)
            {
                Enemy t;
                if(i <  0)
                    t = new Enemy(1, collisionParticles);
                else
                    t = new Enemy(0, collisionParticles);
                t.setLoc(w,i);
                gameRender.addDrawable(t);
                entities.add(t);
            }
        }
        gameRender.addDrawable(ui);
        gameRender.addDrawable(player1);


        gLoopThread = new Thread(this);
        //gLoopThread = new Thread(new ThreadGroup("Game"), this, "GameLoop", 2097152);
        gLoopThread.start();
    }

    public void update()
    {
        if(mainTouchDown)
        {
            if(yOnDown < -.9 && xOnDown < -.9)
                player1.getPlayerBody().setAllToLive();
            player1.move(xOnMovement - xOnDown,yOnDown - yOnMovement);
        }

        player1.getGun().move();

        for(Enemy e: entities)
        {
            collisionHandler.checkCollisions(player1.getPlayerBody(), e.getPixelGroup());

            //synchronized (player1.getGun().getBullets())
            //{
                for (Bullet b : player1.getGun().getBullets()) {
                    collisionHandler.checkCollisions(b.pixelGroup, e.getPixelGroup());
                }
            //}
        }

    }*/

    /*@Override
    public void run()
    {
        // Define variables to hold past time and render lag
        double pastTime = System.currentTimeMillis()-globalStartTime;
        double lag = 0.0;

        // Main Game Loop: Fixed update loop with interpolated rendering when possible.
        while (isPlaying)
        {
            // Calculate elapsed time and determine effective render lag.
            double currentTime = System.currentTimeMillis()-globalStartTime;
            double elapsedTime = currentTime - pastTime;
            pastTime = currentTime;
            lag += elapsedTime;

            // Inner loop for updating game state on a fixed time-step.
            while( lag >= mSPU)
            {
                update();
               // gameRender.setInterpolation((((long)lag >> 6)<<6)/mSPU);
                //gameRender.setInterpolation(lag/mSPU);
                //glSurfaceView.requestRender();
                lag -= mSPU;
            }

            gameRender.setInterpolation((((long)lag >> 5)<<5)/mSPU);
            glSurfaceView.requestRender();
        }
    }*/

    //Unsure of workability*
    /*public void pauseGame()
    {
        isPlaying = false;
        try
        {
            gLoopThread.join();
        }
        catch (InterruptedException e)
        {
            // Error
        }
    }

    public void resumeGame()
    {
        isPlaying = true;
        gLoopThread = new Thread(this);
        gLoopThread.start();
    }*/



}
