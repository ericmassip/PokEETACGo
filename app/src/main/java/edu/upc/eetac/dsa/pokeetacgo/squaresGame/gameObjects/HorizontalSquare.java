package edu.upc.eetac.dsa.pokeetacgo.squaresGame.gameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import edu.upc.eetac.dsa.pokeetacgo.squaresGame.GamePanel;

/**
 * Created by ericmassip on 10/1/17.
 */

public class HorizontalSquare extends GameObject {
    final String TAG = "HORIZONTAL_SQUARE";
    private int side;
    private int speed;
    private boolean isGoingLeft;

    public void setIsGoingLeft(boolean isGoingLeft) {
        this.isGoingLeft = isGoingLeft;
    }

    public HorizontalSquare(int speed, int side) {
        super.x = GamePanel.WIDTH/2;
        super.y = (GamePanel.HEIGHT/3);
        super.width = 2 * side;
        super.height = 2 * side;
        isGoingLeft = true;
        this.speed = speed;
        this.side = side;
    }

    public void update() {
        if(isGoingLeft) {
            x -= speed;
            //Log.i(TAG, "goingLeft: x = " + x);
        } else {
            x += speed;
            //Log.i(TAG, "goingRight: x = " + x);
        }
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(x - side, y - side, x + side, y + side, paint);
    }
}
