package com.example.sweet.game20;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

class GamePlayer
{
    private GLSurfaceView glSurfaceView;

    private GameRenderer gameRender;

    private static final int INVALID_POINTER_ID = -1;

    private int
            movementPointerId = INVALID_POINTER_ID,
            shootingPointerId = INVALID_POINTER_ID;

    private boolean
            movementDown = false,
            shootingDown = false,
            pause = false;

    private long lastTapShooting = 0;
    private long lastTapMoving = 0;
    private long pauseCoolDownLength = 800;
    private long pauseCoolDownStart = 0;
    private static final long doubleTapLength = 500;

    public GamePlayer(Context context, Point size, GLSurfaceView glSV, View d)
    {
        glSurfaceView = glSV;
        gameRender = new GameRenderer(context);
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
                            if (System.currentTimeMillis() - pauseCoolDownStart > pauseCoolDownLength)
                            {
                                if (System.currentTimeMillis() - lastTapMoving < doubleTapLength)
                                {
                                    if (!pause)
                                    {
                                        pause = true;
                                        gameRender.inGamePause();
                                        pauseCoolDownStart = System.currentTimeMillis();
                                    }
                                }
                                lastTapMoving = System.currentTimeMillis();
                            }

                            movementPointerId = event.getPointerId(0);
                            final float normX = ((event.getX(event.findPointerIndex(movementPointerId)) / v.getWidth()) * 2 - 1) / gameRender.xScale;
                            final float normY = ((event.getY(event.findPointerIndex(movementPointerId)) / v.getHeight()) * 2 - 1) / gameRender.yScale;

                            if(!pause)
                            {
                                movementDown = true;

                                gameRender.ui.setMovementDown(true);
                                gameRender.aiRunnable.movementDown = true;

                                gameRender.ui.movementOnDown.set(normX, normY);
                                gameRender.ui.movementOnMove.set(normX, normY);
                                gameRender.aiRunnable.movementOnDownX = normX;
                                gameRender.aiRunnable.movementOnDownY = normY;
                                gameRender.aiRunnable.movementOnMoveX = normX;
                                gameRender.aiRunnable.movementOnMoveY = normY;
                            }
                            else
                            {
                                gameRender.ui.menuPointerDown = true;
                                gameRender.ui.menuOnDown.set(normX, normY);
                                gameRender.ui.menuOnMove.set(normX, normY);
                                //gameRender.triggerActionDown();
                            }
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                        {
                            if(!pause)
                            {
                                if(movementDown)
                                {
                                    movementPointerId = event.INVALID_POINTER_ID;
                                    movementDown = false;
                                    gameRender.ui.setMovementDown(false);
                                    gameRender.aiRunnable.movementDown = false;
                                }
                                if(shootingDown)
                                {
                                    shootingPointerId = event.INVALID_POINTER_ID;
                                    shootingDown = false;
                                    gameRender.ui.setShootingDown(false);
                                    gameRender.aiRunnable.shootingDown = false;
                                }
                            }
                            else
                            {
                                if(gameRender.ui.menuPointerDown)
                                {
                                    final float normX = ((event.getX() / v.getWidth()) * 2 - 1) / gameRender.xScale;
                                    final float normY = ((event.getY() / v.getHeight()) * 2 - 1) / gameRender.yScale;
                                    gameRender.ui.menuPointerDown = false;
                                    if (System.currentTimeMillis() - pauseCoolDownStart > pauseCoolDownLength)
                                    {
                                        pause = gameRender.ui.checkPause();
                                        if(!pause)
                                        {
                                            gameRender.inGameUnpause();
                                            pauseCoolDownStart = System.currentTimeMillis();
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_POINTER_DOWN:
                        {
                            if(!pause)
                            {
                                if (movementDown && !shootingDown)
                                {
                                    if (System.currentTimeMillis() - pauseCoolDownStart > pauseCoolDownLength)
                                    {
                                        if (System.currentTimeMillis() - lastTapShooting < doubleTapLength)
                                        {
                                            if (!pause)
                                            {
                                                pause = true;
                                                gameRender.inGamePause();
                                                pauseCoolDownStart = System.currentTimeMillis();
                                            }
                                        }
                                        lastTapShooting = System.currentTimeMillis();
                                    }

                                    shootingPointerId = pointerId;
                                    shootingDown = true;

                                    gameRender.ui.setShootingDown(true);
                                    gameRender.aiRunnable.shootingDown = true;

                                    final float normX = ((event.getX(event.findPointerIndex(shootingPointerId)) / v.getWidth()) * 2 - 1) / gameRender.xScale;
                                    final float normY = ((event.getY(event.findPointerIndex(shootingPointerId)) / v.getHeight()) * 2 - 1) / gameRender.yScale;

                                    gameRender.ui.shootingOnDown.set(normX, normY);
                                    gameRender.ui.shootingOnMove.set(normX, normY);
                                    gameRender.aiRunnable.shootingOnDownX = normX;
                                    gameRender.aiRunnable.shootingOnDownY = normY;
                                    gameRender.aiRunnable.shootingOnMoveX = normX;
                                    gameRender.aiRunnable.shootingOnMoveY = normY;
                                }
                                else if (shootingDown && !movementDown)
                                {
                                    if (System.currentTimeMillis() - pauseCoolDownStart > pauseCoolDownLength)
                                    {
                                        if (System.currentTimeMillis() - lastTapMoving < doubleTapLength)
                                        {
                                            if (!pause)
                                            {
                                                pause = true;
                                                gameRender.inGamePause();
                                                pauseCoolDownStart = System.currentTimeMillis();
                                            }
                                        }
                                    }
                                    lastTapMoving = System.currentTimeMillis();

                                    movementPointerId = pointerId;
                                    movementDown = true;

                                    gameRender.ui.setMovementDown(true);
                                    gameRender.aiRunnable.movementDown = true;

                                    final float normX = ((event.getX(event.findPointerIndex(movementPointerId)) / v.getWidth()) * 2 - 1) / gameRender.xScale;
                                    final float normY = ((event.getY(event.findPointerIndex(movementPointerId)) / v.getHeight()) * 2 - 1) / gameRender.yScale;

                                    gameRender.ui.movementOnDown.set(normX, normY);
                                    gameRender.ui.movementOnMove.set(normX, normY);
                                    gameRender.aiRunnable.movementOnDownX = normX;
                                    gameRender.aiRunnable.movementOnDownY = normY;
                                    gameRender.aiRunnable.movementOnMoveX = normX;
                                    gameRender.aiRunnable.movementOnMoveY = normY;
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_POINTER_UP:
                        {
                            if (pointerId == shootingPointerId && shootingDown)
                            {
                                shootingPointerId = event.INVALID_POINTER_ID;
                                shootingDown = false;
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
                            if(!pause)
                            {
                                if (shootingDown)
                                {
                                    //System.out.println("moving shooting--------------------------------------" + shootingPointerId);
                                    final float normX = ((event.getX(event.findPointerIndex(shootingPointerId)) / v.getWidth()) * 2 - 1) / gameRender.xScale;
                                    final float normY = ((event.getY(event.findPointerIndex(shootingPointerId)) / v.getHeight()) * 2 - 1) / gameRender.yScale;
                                    gameRender.ui.shootingOnMove.set(normX, normY);
                                    gameRender.aiRunnable.shootingOnMoveX = normX;
                                    gameRender.aiRunnable.shootingOnMoveY = normY;
                                }
                                if (movementDown)
                                {
                                    final float normX = ((event.getX(event.findPointerIndex(movementPointerId)) / v.getWidth()) * 2 - 1) / gameRender.xScale;
                                    final float normY = ((event.getY(event.findPointerIndex(movementPointerId)) / v.getHeight()) * 2 - 1) / gameRender.yScale;
                                    gameRender.ui.movementOnMove.set(normX, normY);
                                    gameRender.aiRunnable.movementOnMoveX = normX;
                                    gameRender.aiRunnable.movementOnMoveY = normY;
                                }
                            }
                            else
                            {
                                final float normX = ((event.getX() / v.getWidth()) * 2 - 1) / gameRender.xScale;
                                final float normY = ((event.getY() / v.getHeight()) * 2 - 1) / gameRender.yScale;
                                gameRender.ui.menuOnMove.set(normX, normY);
                            }
                            break;
                        }
                    }

                    return true;
                }
            });
    }
}
