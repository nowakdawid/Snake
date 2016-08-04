package it.works.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class GameScreen extends ScreenAdapter {

    //General data
    private SpriteBatch batch;
    private Array<BodyPart> bodyPartArray = new Array<BodyPart>();
    private ShapeRenderer shapeRenderer;
    private static final int GRID_CELL = 32;
    private boolean directionSet;
    private boolean hasHit = false;

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
    private Texture snakeBodyTexture = new Texture(Gdx.files.internal("snakebody.png"));

    private TextureRegion snakeHead;
    private TextureRegion apple;
    private TextureRegion snakeBody;

    //Snake data
    private int snakePositionX = 0;
    private int snakePositionY = 0;

    private int snakeXBeforeUpdate = 0;
    private int snakeYBeforeUpdate = 0;

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
        snakeBody = new TextureRegion(snakeBodyTexture);
        apple = new TextureRegion(appleTexture);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        queryInput();
        updateSnake(delta);
        checkAppleCollision();
        checkAndPlaceApple();
        clearScreen();
        drawGrid();
        draw();
    }

    private void updateSnake(float delta) {
        if (!hasHit) {
            timer -= delta;
            if (timer <= 0) {
                timer = MOVE_TIME;
                moveSnake();
                checkForOutOfBounds();
                updateBodyPartsPosition();
                checkSnakeBodyCollision();
                directionSet = false;
            }
        }
    }

    private void draw() {
        batch.begin();
        batch.draw(snakeHead, snakePositionX, snakePositionY, SNAKE_WIDTH / 2, SNAKE_HEIGHT / 2, SNAKE_WIDTH, SNAKE_HEIGHT, 1, 1, snakeAngle);

        for (BodyPart bodyPart : bodyPartArray) {
            bodyPart.draw(batch, snakePositionX, snakePositionY);
        }

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

        snakeXBeforeUpdate = snakePositionX;
        snakeYBeforeUpdate = snakePositionY;

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

    private void updateBodyPartsPosition() {
        if (bodyPartArray.size > 0) {
            BodyPart bodyPart = bodyPartArray.removeIndex(0);
            bodyPart.updateBodyPosition(snakeXBeforeUpdate, snakeYBeforeUpdate);
            bodyPartArray.add(bodyPart);
        }
    }

    private void queryInput() {
        boolean lPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean uPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean dPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        if (lPressed) {
            updateDirection(LEFT);
        }
        if (rPressed) {
            updateDirection(RIGHT);
        }
        if (uPressed) {
            updateDirection(UP);
        }
        if (dPressed) {
            updateDirection(DOWN);
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
        if (appleAvailable && applePositionX == snakePositionX && applePositionY == snakePositionY) {
            BodyPart bodyPart = new BodyPart(snakeBodyTexture);
            bodyPart.updateBodyPosition(snakePositionX, snakePositionY);
            bodyPartArray.insert(0, bodyPart);
            appleAvailable = false;
        }
    }

    private void drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int x = 0; x < Gdx.graphics.getWidth(); x += GRID_CELL) {
            for (int y = 0; y < Gdx.graphics.getHeight(); y += GRID_CELL) {
                shapeRenderer.rect(x, y, GRID_CELL, GRID_CELL);
            }
        }
        shapeRenderer.end();
    }

    private void updateIfNotOppositeDirection(int newSnakeDirection, int oppositeDirection) {
        if (snakeDirection != oppositeDirection) {
            snakeDirection = newSnakeDirection;
            switch (newSnakeDirection) {
                case LEFT:
                    snakeAngle = ANGLE_LEFT;
                    return;
                case RIGHT:
                    snakeAngle = ANGLE_RIGHT;
                    return;
                case UP:
                    snakeAngle = ANGLE_UP;
                    return;
                case DOWN:
                    snakeAngle = ANGLE_DOWN;
            }
        }
    }

    private void updateDirection(int newSnakeDirection) {
        if (!directionSet && snakeDirection != newSnakeDirection) {
            directionSet = true;
            switch (newSnakeDirection) {
                case LEFT: {
                    updateIfNotOppositeDirection(newSnakeDirection, RIGHT);
                }
                break;
                case RIGHT: {
                    updateIfNotOppositeDirection(newSnakeDirection, LEFT);
                }
                break;
                case UP: {
                    updateIfNotOppositeDirection(newSnakeDirection, DOWN);
                }
                break;
                case DOWN: {
                    updateIfNotOppositeDirection(newSnakeDirection, UP);
                }
                break;
            }
        }
    }

    private void checkSnakeBodyCollision() {
        for (BodyPart bodyPart : bodyPartArray) {
            if (bodyPart.positionX == snakePositionX && bodyPart.positionY == snakePositionY) {
                hasHit = true;
            }
        }
    }

}
