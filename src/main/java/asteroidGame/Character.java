package asteroidGame;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import static asteroidGame.AsteroidsApplication.HEIGHT;
import static asteroidGame.AsteroidsApplication.WIDTH;

public abstract class Character {

    Polygon character;
    Point2D movement;
    Boolean alive;

    public void setColor(Color color) {
        this.character.setFill(color);
        this.character.setStroke(Color.WHITE);
        this.character.setStrokeWidth(1.5);
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    public Boolean isAlive() {
        return alive;
    }

    public Point2D getMovement() {
        return movement;
    }

    public void setMovement(Point2D movement) {
        this.movement = movement;
    }

    public Character(Polygon polygon, int x, int y) {
        this.character = polygon;
        this.character.setTranslateX(x);
        this.character.setTranslateY(y);

        this.movement = new Point2D(0, 0);

        this.alive = true;
    }

    public Polygon getCharacter() {
        return character;
    }

    public void turnLeft() {
        this.character.setRotate(this.character.getRotate() - 5);
    }

    public void turnRight() {
        this.character.setRotate(this.character.getRotate() + 5);
    }

    public void move() {
        this.character.setTranslateX(this.character.getTranslateX() + this.movement.getX());
        this.character.setTranslateY(this.character.getTranslateY() + this.movement.getY());

        if (this.character.getTranslateX() < 0) {
            this.character.setTranslateX(this.character.getTranslateX() + WIDTH);
        }

        if (this.character.getTranslateX() > WIDTH) {
            this.character.setTranslateX(this.character.getTranslateX() % WIDTH);
        }

        if (this.character.getTranslateY() < 0) {
            this.character.setTranslateY(this.character.getTranslateY() + HEIGHT);
        }

        if (this.character.getTranslateY() > HEIGHT) {
            this.character.setTranslateY(this.character.getTranslateY() % HEIGHT);
        }
    }

    public void accelerate() {

        double changeX = Math.cos(Math.toRadians(this.character.getRotate()));
        double changeY = Math.sin(Math.toRadians(this.character.getRotate()));

        changeX *= 0.1;
        changeY *= 0.1;

        this.movement = this.movement.add(changeX, changeY);
    }

    public boolean collide(Character other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }
}
