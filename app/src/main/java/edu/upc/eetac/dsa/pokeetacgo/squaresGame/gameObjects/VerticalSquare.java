package edu.upc.eetac.dsa.pokeetacgo.squaresGame.gameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import edu.upc.eetac.dsa.pokeetacgo.squaresGame.GamePanel;

public class VerticalSquare extends GameObject {
    final String TAG = "VERTICAL_SQUARE";
    private int side;
    private int speed;
    private boolean isGoingUp;

    public void setIsGoingUp(boolean isGoingUp) {
        this.isGoingUp = isGoingUp;
    }

    public VerticalSquare(int speed, int side) {
        super.y = GamePanel.HEIGHT/3;
        super.x = (GamePanel.WIDTH/2);
        super.width = 2 * side;
        super.height = 2 * side;
        isGoingUp = true;
        this.speed = speed;
        this.side = side;
    }

    public void update() {
        if(isGoingUp) {
            y -= speed;
            //Log.i(TAG, "goingUp: y = " + y);
        } else {
            y += speed;
            //Log.i(TAG, "goingDown: y = " + y);
        }
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(x - side, y - side, x + side, y + side, paint);
    }
}
