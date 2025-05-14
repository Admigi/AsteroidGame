package asteroidGame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import static asteroidGame.AsteroidsApplication.HEIGHT;
import static asteroidGame.AsteroidsApplication.WIDTH;

public class AlienProjectile extends Character {

    public AlienProjectile(int x, int y) {
        super(createPolygon(), x, y);
        setColor(Color.ORANGERED);
    }

    private static Polygon createPolygon() {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(
                0.0, -3.0,
                3.0, 0.0,
                0.0, 3.0,
                -3.0, 0.0
        );

        return polygon;
    }

    @Override
    public void move() {
        getCharacter().setTranslateX(getCharacter().getTranslateX() + getMovement().getX());
        getCharacter().setTranslateY(getCharacter().getTranslateY() + getMovement().getY());

        if (getCharacter().getTranslateX() < 0
                || getCharacter().getTranslateX() > WIDTH
                || getCharacter().getTranslateY() < 0
                || getCharacter().getTranslateY() > HEIGHT) {
            setAlive(false);
        }
    }
}
