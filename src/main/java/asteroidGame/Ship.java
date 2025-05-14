package asteroidGame;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

public class Ship extends Character {

    public Ship(int x, int y) {
        super(new Polygon(-5, -5, 10, 0, -5, 5), x, y);
        setColor(javafx.scene.paint.Color.CYAN);
    }

    @Override
    public void accelerate() {
        super.movement = new Point2D(0, 0);
        double changeX = Math.cos(Math.toRadians(this.character.getRotate()));
        double changeY = Math.sin(Math.toRadians(this.character.getRotate()));

        changeX *= 1;
        changeY *= 1;

        this.movement = this.movement.add(changeX, changeY);
    }
}
