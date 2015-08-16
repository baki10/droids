package com.game.bakigoal.droidgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.game.bakigoal.droidgame.model.Droid;
import com.game.bakigoal.droidgame.model.components.Speed;

/**
 * Created by bakigoal on 15.08.15.
 */
public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = MainGamePanel.class.getSimpleName();
    private final float scale;

    private MainThread thread;
    private Droid droid;

    // the fps to be displayed
    private String avgFps;

    public MainGamePanel(Context context) {
        super(context);
        scale = context.getResources().getDisplayMetrics().density;
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
        // at this point the surface is created and
        // we can safely start the game loop
        thread.setRunning(true);
        thread.start();
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
        Log.d(TAG, "Thread was shut down cleanly");
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

    /**
     * This is the game updateGameState method. It iterates through all the objects
     * and calls their updateGameState method if they have one or calls specific
     * engine's updateGameState method.
     */
    public void updateGameState() {
        // check collision with right wall if heading right
        if (droid.getSpeed().getxDirection() == Speed.DIRECTION_RIGHT &&
                droid.getX() + droid.getBitmap().getWidth() / 2 >= getWidth()) {
            droid.getSpeed().toggleXDirection();
        }
        // check collision with left wall
        if (droid.getSpeed().getxDirection() == Speed.DIRECTION_LEFT &&
                droid.getX() - droid.getBitmap().getWidth() / 2 <= 0) {
            droid.getSpeed().toggleXDirection();
        }
        // check collision with top wall
        if (droid.getSpeed().getyDirection() == Speed.DIRECTION_UP &&
                droid.getY() - droid.getBitmap().getHeight() / 2 <= 0) {
            droid.getSpeed().toggleYDirection();
        }
        // check collision with bottom wall
        if (droid.getSpeed().getyDirection() == Speed.DIRECTION_DOWN &&
                droid.getY() + droid.getBitmap().getHeight() / 2 >= getHeight()) {
            droid.getSpeed().toggleYDirection();
        }

        //Update the lone droid
        droid.update();
    }

    public void displayGameState(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        //fills the canvas with black
        canvas.drawColor(Color.BLACK);
        droid.draw(canvas);
        // display fps
        displayFps(canvas, avgFps);
    }

    public void setAvgFps(String avgFps) {
        this.avgFps = avgFps;
    }

    private void displayFps(Canvas canvas, String fps) {
        if (canvas != null && fps != null) {
            Paint paint = new Paint();
            paint.setTextSize(15 * scale);
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(fps, this.getWidth() - 10, paint.getTextSize() + 10, paint);
        }
    }

}
