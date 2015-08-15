package com.game.bakigoal.droidgame;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by bakigoal on 15.08.15.
 */
public class MainThread extends Thread {

    private static final String TAG = MainThread.class.getSimpleName();

    private final SurfaceHolder surfaceHolder;
    private final MainGamePanel gamePanel;
    private boolean running;

    public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        Canvas canvas;
        long tickCount = 0L;
        Log.d(TAG, "Starting game loop");
        while (running) {
            canvas = null;
            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    // update game state
                    // draws the canvas on the panel
                    this.gamePanel.draw(canvas);
                }
            }finally {
                if(canvas!=null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            tickCount++;
        }
        Log.d(TAG, "Game loop executed " + tickCount + " times");
    }
}
