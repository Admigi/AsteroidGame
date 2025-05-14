package asteroidGame;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AsteroidsApplication extends Application {

    public static int WIDTH = 600;
    public static int HEIGHT = 400;

    private final AtomicInteger points = new AtomicInteger();
    private final AudioClip buttonSound = new AudioClip("file:src/main/resources/sounds/buttonSound.mp3");
    private final AudioClip shootSound = new AudioClip("file:src/main/resources/sounds/shoot.mp3");
    private final AudioClip hitSound = new AudioClip("file:src/main/resources/sounds/asteroidHit.mp3");
    private final AudioClip explosionSound = new AudioClip("file:src/main/resources/sounds/explosion.mp3");
    private final Media music = new Media(new File("src/main/resources/sounds/retroMusic.mp3").toURI().toString());
    private final MediaPlayer mediaPlayer = new MediaPlayer(music);
    private final Font titleFont = Font.loadFont("file:src/main/resources/space-crusader-font/SpaceCrusader-a7Ma.ttf", 80);
    private final Font buttonFont = Font.loadFont("file:src/main/resources/hyperspace-font/HyperspaceBold-GM0g.ttf", 20);
    private final File file = new File("src/main/resources/images/background.jpg");
    private final Image background = new Image(file.toURI().toString());

    public static void main(String[] args) {
        launch(AsteroidsApplication.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        createMenuScene(stage);

        shootSound.setVolume(0.2);
        explosionSound.setVolume(0.2);
        hitSound.setVolume(0.2);
        buttonSound.setVolume(0.2);

        mediaPlayer.setVolume(0.2);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        stage.setTitle("Asteroids!");
        stage.show();
    }

    private void createMenuScene(Stage stage) {

        BorderPane menuPane = new BorderPane();
        menuPane.setPrefSize(WIDTH, HEIGHT);

        Pane root = createBackground(menuPane);

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);

        Text titleText = new Text("ASTEROIDS");
        titleText.setFont(titleFont);
        titleText.setFill(Color.LIGHTBLUE);
        titleText.setStroke(Color.WHITE);
        titleText.setStrokeWidth(1.5);

        Button startButton = new Button("Start Game");
        startButton.setFont(buttonFont);
        startButton.setPrefWidth(200);
        startButton.setPrefHeight(50);
        startButton.setFocusTraversable(false);

        startButton.setOnAction(event -> {
            buttonSound.play();
            createGameScene(stage);
        });

        menuBox.getChildren().addAll(titleText, startButton);

        menuPane.setCenter(menuBox);

        Scene menuScene = new Scene(root);

        stage.setScene(menuScene);

    }

    private void createGameScene(Stage stage) {
        points.set(0);

        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);

        Pane root = createBackground(pane);

        Text score = new Text(10, 20, "Score: 0");
        score.setFont(Font.font("Arial", 20));
        score.setFill(Color.RED);
        pane.getChildren().add(score);

        List<Asteroid> asteroids = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Random rand = new Random();
            Asteroid asteroid = new Asteroid(rand.nextInt(WIDTH / 3), rand.nextInt(HEIGHT));
            asteroids.add(asteroid);
        }

        asteroids.forEach(Character::turnRight);
        asteroids.forEach(Character::turnRight);
        asteroids.forEach(Character::accelerate);
        asteroids.forEach(Character::accelerate);

        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));

        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);
        pane.getChildren().add(ship.getCharacter());

        List<Projectile> projectiles = new ArrayList<>();

        List<Alien> aliens = new ArrayList<>();
        List<AlienProjectile> alienProjectiles = new ArrayList<>();

        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

        Scene scene = new Scene(root);

        scene.setOnKeyPressed(event
                        -> {
                    pressedKeys.put(event.getCode(), Boolean.TRUE);
                }
        );

        scene.setOnKeyReleased(event
                        -> {
                    pressedKeys.put(event.getCode(), Boolean.FALSE);
                }
        );

        new AnimationTimer() {

            @Override
            public void handle(long now
            ) {
                if (pressedKeys.getOrDefault(KeyCode.Q, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.D, false)) {
                    ship.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.Z, false)) {
                    ship.accelerate();
                }

                if (pressedKeys.getOrDefault(KeyCode.SPACE, false) && projectiles.isEmpty()) {
                    shootSound.play();
                    Projectile projectile = new Projectile((int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY());
                    projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                    projectiles.add(projectile);

                    projectile.accelerate();
                    projectile.setMovement(projectile.getMovement().normalize().multiply(3));

                    pane.getChildren().add(projectile.getCharacter());
                }

                if (Math.random() < 0.005 && asteroids.size() < 10) {
                    Asteroid asteroid = new Asteroid(WIDTH, HEIGHT);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }
                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        explosionSound.play();
                        stop();
                        createGameOverScene(stage);
                    }
                });

                removeDeadObjects(asteroids, pane);

                projectiles.forEach(projectile -> {
                    asteroids.forEach(asteroid -> {
                        if (projectile.collide(asteroid)) {
                            hitSound.play();
                            projectile.setAlive(false);
                            asteroid.setAlive(false);
                            score.setText("Score: " + points.addAndGet(100));
                        }
                    });
                });

                removeDeadObjects(projectiles, pane);

                if (Math.random() < 0.001 && aliens.size() < 3) {
                    Random rnd = new Random();

                    int spawnLocation = rnd.nextInt(4);
                    int x, y;

                    switch (spawnLocation) {
                        case 0:
                            x = rnd.nextInt(WIDTH);
                            y = 30;
                            break;
                        case 1:
                            x = WIDTH - 30;
                            y = rnd.nextInt(HEIGHT);
                            break;
                        case 2:
                            x = rnd.nextInt(WIDTH);
                            y = HEIGHT - 30;
                            break;
                        case 3:
                            x = 30;
                            y = rnd.nextInt(HEIGHT);
                            break;
                        default:
                            x = rnd.nextInt(WIDTH);
                            y = rnd.nextInt(HEIGHT);
                    }

                    Alien alien = new Alien(x, y, ship);

                    if (!alien.collide(ship)) {
                        aliens.add(alien);
                        pane.getChildren().add(alien.getCharacter());
                    }
                }

                aliens.forEach(alien -> {
                    if (alien.shouldShoot()) {
                        AlienProjectile projectile = alien.shoot();
                        alienProjectiles.add(projectile);
                        pane.getChildren().add(projectile.getCharacter());
                    }

                    projectiles.forEach(projectile -> {
                        if (projectile.collide(alien)) {
                            hitSound.play();
                            projectile.setAlive(false);
                            alien.setAlive(false);
                            score.setText("Score: " + points.addAndGet(200));
                        }
                    });

                    if (ship.collide(alien)) {
                        explosionSound.play();
                        stop();
                        createGameOverScene(stage);
                    }
                });
                removeDeadObjects(aliens, pane);

                alienProjectiles.forEach(projectile -> {
                    if (ship.collide(projectile)) {
                        explosionSound.play();
                        stop();
                        createGameOverScene(stage);
                    }
                });

                removeDeadObjects(alienProjectiles, pane);

                ship.move();
                asteroids.forEach(Asteroid::move);
                projectiles.forEach(Projectile::move);
                aliens.forEach(Alien::move);
                alienProjectiles.forEach(AlienProjectile::move);

            }
        }
                .start();

        stage.setScene(scene);
    }

    private void createGameOverScene(Stage stage) {

        BorderPane gameOverPane = new BorderPane();
        gameOverPane.setPrefSize(WIDTH, HEIGHT);

        Pane root = createBackground(gameOverPane);

        VBox gameOverBox = new VBox(20);
        gameOverBox.setAlignment(Pos.CENTER);

        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        gameOverText.setFill(Color.WHITE);

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: black;");
        Text score = new Text(10, 20, "Score: " + points);
        stackPane.getChildren().add(score);
        score.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        score.setFill(Color.RED);

        Button restartButton = new Button("Play Again");
        restartButton.setFont(buttonFont);
        restartButton.setPrefWidth(200);
        restartButton.setPrefHeight(50);
        restartButton.setFocusTraversable(false);

        restartButton.setOnAction(event -> {
            buttonSound.play();
            createGameScene(stage);
        });

        Button menuButton = new Button("Main Menu");
        menuButton.setFont(buttonFont);
        menuButton.setPrefWidth(200);
        menuButton.setPrefHeight(50);
        menuButton.setFocusTraversable(false);

        menuButton.setOnAction(event -> {
            buttonSound.play();
            createMenuScene(stage);
        });

        gameOverBox.getChildren().addAll(gameOverText, stackPane, restartButton, menuButton);

        gameOverPane.setCenter(gameOverBox);

        Scene gameOverScene = new Scene(root);

        stage.setScene(gameOverScene);
    }

    private <T extends Character> void removeDeadObjects(List<T> objects, Pane pane) {
        objects.stream()
                .filter(object -> !object.isAlive())
                .forEach(object -> pane.getChildren().remove(object.getCharacter()));

        objects.removeAll(objects.stream()
                .filter(object -> !object.isAlive())
                .collect(Collectors.toList()));
    }

    public Pane createBackground(Pane pane) {
        StackPane root = new StackPane();
        root.setPrefSize(WIDTH, HEIGHT);

        ImageView backgroundView = new ImageView(background);
        backgroundView.setFitWidth(WIDTH);
        backgroundView.setFitHeight(HEIGHT);

        root.getChildren().add(backgroundView);
        root.getChildren().add(pane);

        return root;
    }

}
