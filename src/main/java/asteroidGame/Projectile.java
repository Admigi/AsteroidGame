package asteroidGame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import static asteroidGame.AsteroidsApplication.HEIGHT;
import static asteroidGame.AsteroidsApplication.WIDTH;

public class Projectile extends Character {

    public Projectile(int x, int y) {
        super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
        setColor(Color.YELLOW);
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
