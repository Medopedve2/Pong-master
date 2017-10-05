package me.dawars.pong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * Created by dawar on 2016. 07. 24..
 */
public class Game {

    private static final int BALL_RADIUS = 25;
    private static final int PLATFORM_WIDTH = 10 * BALL_RADIUS;
    private static final int BALL_SPEED = 10;

    private Paint paintWhite = new Paint();
    private Paint paintDivider = new Paint();
    private Paint paintScore = new Paint();

    private static final Random random = new Random();

    public int width, height;

    private PointF ballPosition = new PointF();
    private PointF direction;

    private static final PointF validDirections[] = {
            new PointF(1, 1),
            new PointF(1, -1),
            new PointF(-1, 1),
            new PointF(-1, -1),
    };

    float platformX1, platformX2;
    int score1, score2;

    /**
     * Constructor, gets called ONCE at the beginning
     */
    public Game() {

        // instantiating paints to draw with
        paintWhite.setColor(Color.WHITE);

        paintDivider.setColor(Color.WHITE);
        paintDivider.setStrokeWidth(BALL_RADIUS);
        paintDivider.setStyle(Paint.Style.STROKE);
        paintDivider.setPathEffect(new DashPathEffect(new float[]{BALL_RADIUS, BALL_RADIUS,}, 0));

        paintScore.setColor(Color.GRAY);
        paintScore.setTextSize(100);

        // centering platforms (bats)
        platformX1 = platformX2 = width / 2;

        newBall();
    }


    private void newBall() {
        ballPosition.set(width / 2, height / 2);    // reset position
        direction = validDirections[random.nextInt(validDirections.length)];    // new direction
    }

    /**
     * Gets called every frame
     *
     * @param canvas to draw on the screen
     */
    void tick(Canvas canvas) {
        /********* Update **********/

        // bounce off of sides
        if (ballPosition.x < 0 || ballPosition.x > width) direction.x *= -1;

        // bounce off of platforms
        if (ballPosition.y < 4 * BALL_RADIUS && Math.abs(ballPosition.x - platformX1) < PLATFORM_WIDTH / 2)
            direction.y *= -1;
        if (ballPosition.y > height - 4 * BALL_RADIUS && Math.abs(ballPosition.x - platformX2) < PLATFORM_WIDTH / 2)
            direction.y *= -1;

        // check for goal
        if (ballPosition.y < 0) {
            newBall();
            score2++;
        }

        if (ballPosition.y > height) {
            newBall();
            score1++;
        }

        // Move the ball
        for (int i = 0; i < BALL_SPEED; i++) {
            ballPosition.x += direction.x;
            ballPosition.y += direction.y;
        }

        /********* Draw **********/

        canvas.drawColor(Color.BLACK);

        // Scores
        canvas.drawText(score1 + "", width / 2, height / 4, paintScore);
        canvas.drawText(score2 + "", width / 2, 3 * height / 4, paintScore);

        // Divider
        canvas.drawLine(0, height / 2, width, height / 2, paintDivider);

        // Ball
        canvas.drawRect(ballPosition.x - BALL_RADIUS, ballPosition.y - BALL_RADIUS, ballPosition.x + BALL_RADIUS, ballPosition.y + BALL_RADIUS, paintWhite);

        // Platforms
        canvas.drawRect(platformX1 - PLATFORM_WIDTH / 2, BALL_RADIUS, platformX1 + PLATFORM_WIDTH / 2, 3 * BALL_RADIUS, paintWhite);
        canvas.drawRect(platformX2 - PLATFORM_WIDTH / 2, height - 3 * BALL_RADIUS, platformX2 + PLATFORM_WIDTH / 2, height - BALL_RADIUS, paintWhite);
    }

    public boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE ||
                event.getAction() == MotionEvent.ACTION_DOWN ||
                event.getAction() == MotionEvent.ACTION_POINTER_DOWN)
            for (int i = 0; i < event.getPointerCount(); i++) {
                float x = event.getX(i);
                if (event.getY(i) < height / 2)
                    platformX1 = x;
                else
                    platformX2 = x;
            }
        return true;
    }
}
