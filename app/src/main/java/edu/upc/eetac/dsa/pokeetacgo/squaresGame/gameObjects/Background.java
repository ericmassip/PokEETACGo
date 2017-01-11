package edu.upc.eetac.dsa.pokeetacgo.squaresGame.gameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Background {
    private Bitmap image;
    public int x, y;

    public Background(Bitmap res) {
        image = res;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
