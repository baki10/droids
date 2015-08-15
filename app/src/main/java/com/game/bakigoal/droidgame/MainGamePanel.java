package com.game.bakigoal.droidgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by bakigoal on 15.08.15.
 */
public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = MainGamePanel.class.getSimpleName();

    private MainThread thread;
    private Droid droid;

    public MainGamePanel(Context context) {
        super(context);
        // adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);
        // create droid
        droid = new Droid(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1), 50, 50);
        // create the game loop thread
        thread = new MainThread(getHolder(), this);
        // make the GamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!thread.isAlive()) {
            thread.setRunning(true);
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException ignored) {
                //try again shutting down the thread
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // delegating event handling to the droid
                droid.handleActionDown((int) eventX, (int) eventY);

                // check if in the lower part of the screen we exit
                if (eventY > getHeight() - 50) {
                    thread.setRunning(false);
                    ((Activity) getContext()).finish();
                } else {
                    Log.d(TAG, "Coords: x = " + eventX + ", y = " + eventY);
                    Log.d(TAG, "Droid Coords: x = " + droid.getX() + ", y = " + droid.getY());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (droid.isTouched()) {
                    // the droid was picked up and is being dragged
                    droid.setX((int) eventX);
                    droid.setY((int) eventY);
                }
                break;
            case MotionEvent.ACTION_UP:
                // touch was released
                if (droid.isTouched()) {
                    droid.setTouched(false);
                }
                break;
        }

        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        //fills the canvas with black
        canvas.drawColor(Color.BLACK);
        droid.draw(canvas);
    }
}
