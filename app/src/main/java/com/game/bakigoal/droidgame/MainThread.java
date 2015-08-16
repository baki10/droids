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

    // desired fps
    private final static int MAX_FPS = 50;
    // maximum number of frames to be skipped
    private final static int MAX_FRAME_SKIPS = 5;
    // the frame period
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;

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
        Log.d(TAG, "Starting game loop");

        long beginTime;     // the time when the cycle begun
        long timeDiff;      // the time it took for the cycle to execute
        int sleepTime;      // ms to sleep (<0 if we're behind)
        int framesSkipped;  // number of frames being skipped

        sleepTime = 0;

        while (running) {
            canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;

                    this.gamePanel.updateGameState();
                    this.gamePanel.displayGameState(canvas);

                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int)(FRAME_PERIOD - timeDiff);
                    if(sleepTime>0){
                        try {
                            Thread.sleep(sleepTime);
                        }catch (InterruptedException ignored){
                        }
                    }
                    while (sleepTime<0 && framesSkipped < MAX_FRAME_SKIPS){
                        // we need to catch up
                        // update without rendering
                        this.gamePanel.updateGameState();
                        // add frame period to check if in next frame
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;

                    }
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
