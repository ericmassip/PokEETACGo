package edu.upc.eetac.dsa.pokeetacgo.squaresGame;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    final String TAG = "MAIN_THREAD";
    private final SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        int FPS = 30;
        long targetTime = 1000/ FPS;

        while (running) {
            startTime = System.nanoTime();
            Canvas canvas = null;

            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            } catch (Exception e) {
                Log.e(TAG, "Game Loop update and draw failed");
            }

            finally {
                if(canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            timeMillis = (System.nanoTime() - startTime)/1000000;
            waitTime = targetTime - timeMillis;

            //Log.i(TAG, "startTime: " + startTime);
            //Log.i(TAG, "timeMillis: " + timeMillis);
            //Log.i(TAG, "waitTime: " + waitTime);
            //Log.i(TAG, "targetTime: " + targetTime);

            if(waitTime > 0) {
                try {
                    sleep(waitTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            //Log.i(TAG, "totalTime: " + totalTime);
            //Log.i(TAG, "frameCount: " + frameCount);

            if(frameCount == FPS) {
                double averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                Log.i(TAG, String.valueOf(averageFPS));
            }
        }
    }

    public void setRunning(boolean isRunning) {
        running = isRunning;
    }
}
