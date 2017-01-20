package edu.upc.eetac.dsa.pokeetacgo.squaresGame;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

import edu.upc.eetac.dsa.pokeetacgo.MapsActivity;
import edu.upc.eetac.dsa.pokeetacgo.R;
import edu.upc.eetac.dsa.pokeetacgo.squaresGame.gameObjects.Background;
import edu.upc.eetac.dsa.pokeetacgo.squaresGame.gameObjects.GameObject;
import edu.upc.eetac.dsa.pokeetacgo.squaresGame.gameObjects.HorizontalSquare;
import edu.upc.eetac.dsa.pokeetacgo.squaresGame.gameObjects.VerticalSquare;

@SuppressLint("ViewConstructor")
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    final String TAG = "GAME_PANEL";
    public static final int WIDTH = 640;
    public static final int HEIGHT = 1136;
    public static final int TOP_OF_BOUNCE = (GamePanel.HEIGHT / 3) - 300;
    public static final int BOTTOM_OF_BOUNCE = (GamePanel.HEIGHT / 3) + 300;
    public static final int MAX_LEFT_OF_BOUNCE = (GamePanel.WIDTH / 2) - 300;
    public static final int MAX_RIGHT_OF_BOUNCE = (GamePanel.WIDTH / 2) + 300;
    private MainThread thread;
    private Background background;
    private VerticalSquare verticalSquare;
    private HorizontalSquare horizontalSquare;
    private int turnOffThreadTriesCounter = 0;
    private boolean playing = true;
    private int requestIdOfGeofenceTriggered;
    private boolean capturadoIsSuccessful;

    public GamePanel(Context context, int requestIdOfGeofenceTriggered) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        this.requestIdOfGeofenceTriggered = requestIdOfGeofenceTriggered;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (turnOffThreadTriesCounter < 1000) {
            try {
                turnOffThreadTriesCounter++;
                thread.setRunning(false);
                thread.join();
                thread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                surfaceDestroyed(holder);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Random random = new Random();
        int speed = 20 + random.nextInt(50);
        Log.i(TAG, "speed = " + speed);

        int squareSide = 20 + random.nextInt(40);
        Log.i(TAG, "squareSide = " + squareSide);

        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.black_blackground));
        verticalSquare = new VerticalSquare(speed, squareSide);
        horizontalSquare = new HorizontalSquare(speed, squareSide);

        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            playing = false;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setMessage("Press OK to keep on looking for profemons!");
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent goBackToMaps = new Intent(getContext(), MapsActivity.class);
                    goBackToMaps.putExtra("capturadoIsSuccessful", capturadoIsSuccessful);
                    goBackToMaps.putExtra("requestIdOfGeofenceTriggered", requestIdOfGeofenceTriggered);
                    goBackToMaps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(goBackToMaps);
                }
            });


            if (collision(verticalSquare, horizontalSquare)) {
                Log.i(TAG, "COLLISION!!!");
                capturadoIsSuccessful = true;
                alertDialogBuilder.setTitle("Profemon Caught!");
                showAlertDialog(alertDialogBuilder, Color.BLUE);
            } else {
                Log.i(TAG, "YOU LOSE");
                capturadoIsSuccessful = false;
                alertDialogBuilder.setTitle("Profemon Lost!");
                showAlertDialog(alertDialogBuilder, Color.RED);
            }
        }
        return super.onTouchEvent(event);
    }

    private void showAlertDialog(AlertDialog.Builder alertDialogBuilder, final int color) {
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
            }
        });
        alertDialog.show();
    }

    public boolean collision(GameObject a, GameObject b) {
        return Rect.intersects(a.getRectangle(), b.getRectangle());
    }

    public void update() {
        if (playing) {
            updateVerticalSquare();
            updateHorizontalSquare();
        }
    }

    public void updateVerticalSquare() {
        if (verticalSquare.y < TOP_OF_BOUNCE) {
            verticalSquare.setIsGoingUp(false);
            //Log.i(TAG, "Reached the top: y = " + verticalSquare.y);
        } else if (verticalSquare.y > BOTTOM_OF_BOUNCE) {
            verticalSquare.setIsGoingUp(true);
            //Log.i(TAG, "Reached the bottom: y = " + verticalSquare.y);
        }
        verticalSquare.update();
    }

    public void updateHorizontalSquare() {
        if (horizontalSquare.x < MAX_LEFT_OF_BOUNCE) {
            horizontalSquare.setIsGoingLeft(false);
            //Log.i(TAG, "Reached left of screen: x = " + horizontalSquare.x);
        } else if (horizontalSquare.x > MAX_RIGHT_OF_BOUNCE) {
            horizontalSquare.setIsGoingLeft(true);
            //Log.i(TAG, "Reached right of screen: x = " + horizontalSquare.x);
        }
        horizontalSquare.update();
    }

    @Override
    public void draw(Canvas canvas) {
        final float scaleFactorX = getWidth() / (WIDTH * 1.f);
        final float scaleFactorY = getHeight() / (HEIGHT * 1.f);

        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);

            background.draw(canvas);
            verticalSquare.draw(canvas);
            horizontalSquare.draw(canvas);

            drawInstructionsText(canvas);

            canvas.restoreToCount(savedState);
        }
    }

    private void drawInstructionsText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("PRESS TO STOP", GamePanel.WIDTH / 5, BOTTOM_OF_BOUNCE + 300, paint);
        paint.setTextSize(25);
        canvas.drawText("STOP THE SQUARES WHEN CROSSING", GamePanel.WIDTH / 5, BOTTOM_OF_BOUNCE + 250, paint);
    }
}
