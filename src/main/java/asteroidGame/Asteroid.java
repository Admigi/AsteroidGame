package asteroidGame;

import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Asteroid extends Character {

    private double rotationalMovement;

    public Asteroid(int x, int y) {
        super(new PolygonFactory().createPolygon(), x, y);

        Random rand = new Random();

        super.getCharacter().setRotate(rand.nextInt(360));

        int accelerationAmount = 1 + rand.nextInt(10);
        for (int i = 0; i < accelerationAmount; i++) {
            accelerate();
        }

        this.rotationalMovement = 0.5 - rand.nextDouble();

        Color color = Color.rgb(
                150 + new java.util.Random().nextInt(105),
                150 + new java.util.Random().nextInt(105),
                150 + new java.util.Random().nextInt(105)
        );

        setColor(color);
    }

    @Override
    public void move() {
        super.move();
        super.getCharacter().setRotate(super.getCharacter().getRotate() + rotationalMovement);
    }
}
