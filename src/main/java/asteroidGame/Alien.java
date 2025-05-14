package asteroidGame;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import static asteroidGame.AsteroidsApplication.HEIGHT;
import static asteroidGame.AsteroidsApplication.WIDTH;

public class Alien extends Character {

    private long lastShotTime;
    private long shootingInterval;
    private Ship targetShip;

    public Alien(int x, int y, Ship targetShip) {
        super(createPolygon(), x, y);
        setColor(Color.RED);

        this.targetShip = targetShip;

        java.util.Random random = new java.util.Random();

        Point2D initialMovement = new Point2D(
                -0.5 + random.nextDouble(),
                -0.5 + random.nextDouble()
        );
        setMovement(initialMovement.normalize().multiply(0.3 + random.nextDouble() * 0.4));

        this.shootingInterval = 3000 + random.nextInt(2000);
        this.lastShotTime = System.currentTimeMillis();
    }

    private static Polygon createPolygon() {
        Polygon polygon = new Polygon();

        polygon.getPoints().addAll(
                -15.0, 0.0,
                -10.0, -5.0,
                10.0, -5.0,
                15.0, 0.0,
                10.0, 5.0,
                -10.0, 5.0
        );

        return polygon;
    }

    @Override
    public void move() {
        getCharacter().setTranslateX(getCharacter().getTranslateX() + getMovement().getX());
        getCharacter().setTranslateY(getCharacter().getTranslateY() + getMovement().getY());

        checkBoundaries();

        java.util.Random random = new java.util.Random();
        if (random.nextInt(100) < 1) {
            Point2D newMovement = new Point2D(
                    -0.5 + random.nextDouble(),
                    -0.5 + random.nextDouble()
            );
            setMovement(newMovement.normalize().multiply(0.3 + random.nextDouble() * 0.4));
        }
    }

    private void checkBoundaries() {
        double x = getCharacter().getTranslateX();
        double y = getCharacter().getTranslateY();
        double size = 15;
        boolean hitBoundary = false;

        Point2D currentMovement = getMovement();
        Point2D newMovement = currentMovement;

        if (x - size <= 0) {
            newMovement = new Point2D(Math.abs(currentMovement.getX()), currentMovement.getY());
            hitBoundary = true;
        } else if (x + size >= WIDTH) {
            newMovement = new Point2D(-Math.abs(currentMovement.getX()), currentMovement.getY());
            hitBoundary = true;
        }

        if (y - size <= 0) {
            newMovement = new Point2D(newMovement.getX(), Math.abs(currentMovement.getY()));
            hitBoundary = true;
        } else if (y + size >= HEIGHT) {
            newMovement = new Point2D(newMovement.getX(), -Math.abs(currentMovement.getY()));
            hitBoundary = true;
        }

        if (hitBoundary) {
            setMovement(newMovement);

            double constrainedX = Math.max(size, Math.min(WIDTH - size, x));
            double constrainedY = Math.max(size, Math.min(HEIGHT - size, y));
            getCharacter().setTranslateX(constrainedX);
            getCharacter().setTranslateY(constrainedY);
        }
    }

    public boolean shouldShoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= shootingInterval) {
            lastShotTime = currentTime;
            return true;
        }
        return false;
    }

    public AlienProjectile shoot() {
        AlienProjectile projectile = new AlienProjectile(
                (int) getCharacter().getTranslateX(),
                (int) getCharacter().getTranslateY()
        );

        double dx = targetShip.getCharacter().getTranslateX() - getCharacter().getTranslateX();
        double dy = targetShip.getCharacter().getTranslateY() - getCharacter().getTranslateY();

        Point2D direction = new Point2D(dx, dy).normalize();
        projectile.setMovement(direction.multiply(2.0));

        return projectile;
    }
}
