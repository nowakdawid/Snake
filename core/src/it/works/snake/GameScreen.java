package it.works.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class GameScreen extends ScreenAdapter {

    //General data
    private SpriteBatch batch;

    //Directions
    private final static int RIGHT = 0;
    private final static int LEFT = 1;
    private final static int UP = 2;
    private final static int DOWN = 3;

    private final static int ANGLE_RIGHT = 90;
    private final static int ANGLE_LEFT = 270;
    private final static int ANGLE_UP = 180;
    private final static int ANGLE_DOWN = 0;

    //Frame timings
    private static final float MOVE_TIME = 0.18F;
    private float timer = MOVE_TIME;

    //Textures
    private Texture snakeHeadTexture = new Texture(Gdx.files.internal("snakehead.png"));
    private Texture appleTexture = new Texture(Gdx.files.internal("apple.png"));

    private TextureRegion snakeHead;
    private TextureRegion apple;

    //Snake data
    private int snakePositionX = 0;
    private int snakePositionY = 0;

    private int snakeDirection = RIGHT;
    private int snakeAngle = 90;

    private static final int SNAKE_MOVEMENT = 32;
    private static final int SNAKE_WIDTH = 32;
    private static final int SNAKE_HEIGHT = 32;

    //Apple data
    private boolean appleAvailable = false;

    private int applePositionX = 0;
    private int applePositionY = 0;

    private static final int APPLE_WIDTH = 32;
    private static final int APPLE_HEIGHT = 32;

    @Override
    public void show() {
        batch = new SpriteBatch();
        snakeHead = new TextureRegion(snakeHeadTexture);
        apple = new TextureRegion(appleTexture);
    }

    @Override
    public void render(float delta) {
        queryInput();
        timer -= delta;

        if (timer <= 0) {
            timer = MOVE_TIME;
            moveSnake();
            checkForOutOfBounds();
        }

        checkAppleCollision();
        checkAndPlaceApple();
        clearScreen();
        draw();
    }

    private void draw() {
        batch.begin();
        batch.draw(snakeHead, snakePositionX, snakePositionY, SNAKE_WIDTH / 2, SNAKE_HEIGHT / 2, SNAKE_WIDTH, SNAKE_HEIGHT, 1, 1, snakeAngle);
        if (appleAvailable) {
            batch.draw(apple, applePositionX, applePositionY, APPLE_WIDTH / 2, APPLE_HEIGHT / 2, APPLE_WIDTH, APPLE_HEIGHT, 1, 1, 0);
        }
        batch.end();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void checkForOutOfBounds() {
        if (snakePositionX >= Gdx.graphics.getWidth()) {
            snakePositionX = 0;
        }
        if (snakePositionX < 0) {
            snakePositionX = Gdx.graphics.getWidth() - SNAKE_MOVEMENT;
        }
        if (snakePositionY >= Gdx.graphics.getHeight()) {
            snakePositionY = 0;
        }
        if (snakePositionY < 0) {
            snakePositionY = Gdx.graphics.getHeight() - SNAKE_MOVEMENT;
        }
    }

    private void moveSnake() {
        switch (snakeDirection) {
            case RIGHT:
                snakePositionX += SNAKE_MOVEMENT;
                return;
            case LEFT:
                snakePositionX -= SNAKE_MOVEMENT;
                return;
            case UP:
                snakePositionY += SNAKE_MOVEMENT;
                return;
            case DOWN:
                snakePositionY -= SNAKE_MOVEMENT;
        }
    }

    private void queryInput() {
        boolean lPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean uPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean dPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        if (lPressed) {
            snakeDirection = LEFT;
            snakeAngle = ANGLE_LEFT;
        }
        if (rPressed) {
            snakeDirection = RIGHT;
            snakeAngle = ANGLE_RIGHT;
        }
        if (uPressed) {
            snakeDirection = UP;
            snakeAngle = ANGLE_UP;
        }
        if (dPressed) {
            snakeDirection = DOWN;
            snakeAngle = ANGLE_DOWN;
        }
    }

    private void checkAndPlaceApple() {
        if (!appleAvailable) {
            //keep setting applePositionX and applePositionX until they are not equal to snakeX and snakeY
            //then go out of the loop
            while (applePositionX == snakePositionX && applePositionY == snakePositionY) {
                applePositionX = MathUtils.random(Gdx.graphics.getWidth() / SNAKE_MOVEMENT - 1) * SNAKE_MOVEMENT;
                applePositionY = MathUtils.random(Gdx.graphics.getHeight() / SNAKE_MOVEMENT - 1) * SNAKE_MOVEMENT;
                appleAvailable = true;
            }
        }
    }

    private void checkAppleCollision() {
        if(appleAvailable && applePositionX == snakePositionX && applePositionY == snakePositionY) {
            appleAvailable = false;
        }
    }

}
