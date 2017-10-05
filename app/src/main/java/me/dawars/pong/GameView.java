package me.dawars.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by dawars on 18/07/16.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, Runnable {
    private static final String TAG = "GameView";
    /**
     * Game thread
     */
    private Thread gameThread;
    private boolean isRunning;

    private Game game;

    private Canvas mCanvas;
    private final SurfaceHolder surfaceHolder;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);

        setOnTouchListener(this);

        gameThread = new Thread(this);
        game = new Game();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.v(TAG, "surface changed");
        game.width = getWidth();
        game.height = getHeight();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning = false;

        boolean retry = true;
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return game.onTouch(event);
    }

    /**
     * This runs on a new thread with an infinite loop running the game
     */
    @Override
    public void run() {
        while (isRunning) {

            mCanvas = surfaceHolder.lockCanvas();
            if (mCanvas != null) {
                game.tick(mCanvas);
                surfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
