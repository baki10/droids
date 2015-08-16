package com.game.bakigoal.droidgame.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.game.bakigoal.droidgame.model.components.Speed;

/**
 * Created by bakigoal on 15.08.15.
 */
public class Droid {
    private Bitmap bitmap;
    private int x;
    private int y;
    private boolean touched;

    private Speed speed;

    public Droid(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        speed = new Speed();
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap,
                (getX() - bitmap.getWidth() / 2),
                (getY() - bitmap.getHeight() / 2),
                null);
    }

    public void handleActionDown(int eventX, int eventY) {
        if (eventX >= (getX() - bitmap.getWidth() / 2) &&
                eventX <= (getX() + bitmap.getWidth() / 2) &&
                eventY >= (getY() - bitmap.getHeight() / 2) &&
                eventY <= (getY() + bitmap.getHeight() / 2)) {
            setTouched(true);
        } else {
            setTouched(false);
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public Speed getSpeed() {
        return speed;
    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }

    public void update() {
        if (!touched) {
            x += speed.getXv() * speed.getxDirection();
            y += speed.getYv() * speed.getyDirection();
        }
    }
}
