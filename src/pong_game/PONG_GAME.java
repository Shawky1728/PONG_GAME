package pong_game;



//////****** our project is  PONG_GAME *******///////


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application; 
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PONG_GAME extends Application {
    
    // Constants for the game dimensions and speed
    
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 650;
    private static final double BALL_RADIUS = 10;
    private static final double PADDLE_WIDTH = 10;
    private static final double PADDLE_HEIGHT = 120;
    private static final double PADDLE_SPEED = 5;

    // Variables for the game state
    
    private double ballX, ballY; // ball position
    private double ballVX, ballVY; // ball velocity
    private double paddle1Y, paddle2Y; // paddle positions
    private boolean upPressed, downPressed, wPressed, sPressed; // keys pressed
    private int score1, score2; // scores
    private boolean computerMode; // flag for computer mode
    private int computerDifficulty;
    boolean Pause = false ;

    // Variable for the timeline animation
    
    private Timeline timeline; // timeline
    
    // Variable for the sound effect player
    private MediaPlayer player;
    private MediaPlayer Win;


    @Override
    public void start(Stage stage) {
        
        // Initialize the game state
        
        reset();

        // Create a canvas to draw the game objects
        
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a timeline animation to update the game loop
        
        timeline = new Timeline(new KeyFrame(Duration.millis(8), e -> {
            
            // Update the game state
            
            update();
            
            // Clear the canvas
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            
            // Draw the ball
            gc.setFill(Color.WHITE);
            gc.fillOval(ballX - BALL_RADIUS, ballY - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
            
            //Draw line in middle
            gc.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
            gc.setLineWidth(4);
            gc.strokeLine(WIDTH/2,0,WIDTH/2,HEIGHT);
            
            //Draw line UP
            gc.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
            gc.setLineWidth(8);
            gc.strokeLine(0,0,WIDTH,0);
            
            //Draw line Down
            gc.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
            gc.setLineWidth(8);
            gc.strokeLine(0,HEIGHT,WIDTH,HEIGHT);
            //line in side left
            gc.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
            gc.setLineWidth(8);
            gc.strokeLine(0,0,0,1.0/6* HEIGHT);
              //line in side left down
            gc.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
            gc.setLineWidth(8);
            gc.strokeLine(0,5.0/6*HEIGHT,0,HEIGHT);
             //line in side right
            gc.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
            gc.setLineWidth(8);
            gc.strokeLine(WIDTH,0,WIDTH,1.0/6* HEIGHT);
              //line in side right down
            gc.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
            gc.setLineWidth(8);
            gc.strokeLine(WIDTH,5.0/6*HEIGHT,WIDTH,HEIGHT);
            
            // Draw the paddle1
            gc.setFill(Color.RED);
            gc.fillRect(PADDLE_WIDTH, paddle1Y - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT);
            
            // Draw the paddle2
            gc.setFill(Color.GREEN);
            gc.fillRect(WIDTH - PADDLE_WIDTH * 2, paddle2Y - PADDLE_HEIGHT / 2, PADDLE_WIDTH,
                    PADDLE_HEIGHT);
            
            // Draw the scores
            gc.setFill(Color.color(Math.random(), Math.random(), Math.random()));
            gc.setFont(Font.font(30));
            gc.fillText(String.valueOf(score1), WIDTH / 4, 50);
            gc.fillText(String.valueOf(score2), WIDTH * 3 / 4, 50);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // loop indefinitely

        // Create a scene and attach a key listener
        
        Scene scene = new Scene(new StackPane(canvas));
        scene.setOnKeyPressed(this::onKeyPressed);
        scene.setOnKeyReleased(this::onKeyReleased);
        scene.setOnKeyPressed(e->{
            if(e.getCode()==KeyCode.P && Pause == false){
               timeline.stop();
                Pause = true ;
            }
            else if(e.getCode()==KeyCode.P && Pause == true){
               timeline.play();
                Pause = false ;
            }
            
            
        });
        // Player1 move with mouse
        scene.setOnMouseMoved(e->{paddle1Y=e.getY();});
        
        // Set up the stage and show it
        stage.setScene(scene);
        stage.setTitle("Pong");
        Image pong = new Image("pong.jpg");
        stage.getIcons().add(pong);
        stage.show();

        // Show a page before start the game to say that final score is 10
        showStartPage();

        // Ask the user to choose the mode
        chooseMode();
         // Create a media object with the URL of the sound effect file
        Media sound = new Media("http://codeskulptor-demos.commondatastorage.googleapis.com/GalaxyInvaders/pause.wav");
        Media win=new Media("https://www.soundjay.com/human/sounds/applause-01.wav");
        // Create a media player with the media object
        player = new MediaPlayer(sound);
        Win=new MediaPlayer(win);
    }

    // Method to reset the game state
    private void reset() {
        // Reset the ball position and velocity
        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;
        ballVX = Math.random() < 0.5 ? -5 : 5; // random direction
        ballVY = Math.random() * 10 - 5; // random speed

        // Reset the paddle positions
        paddle1Y = HEIGHT / 2;
        paddle2Y = HEIGHT / 2;

        // Reset the keys pressed
        upPressed = false;
        downPressed = false;
        wPressed = false;
        sPressed = false;

        // Reset the scores
        score1 = 0;
        score2 = 0;

        // Reset the computer mode flag
        computerMode = false;

    }

    
    // Method to update the game state
private void update() {
    // Move the ball by its velocity
    ballX += ballVX;
    ballY += ballVY;

    // Check for collisions with the walls and bounce the ball
    if (ballX < BALL_RADIUS || ballX > WIDTH - BALL_RADIUS ) {
       
        if (ballX < BALL_RADIUS ) {
            
           if ((ballY<HEIGHT/6.0 || ballY>HEIGHT-(1.0/6)* HEIGHT)){
               ballVX *= -1;
           }
           else{
               resetBall(); // reset the ball position and velocity
           score2++; // right player scored
            player.stop();
            player.play();
           }
            
        } else {
            
            if ((ballY<HEIGHT/6.0 || ballY>HEIGHT-(1.0/6)* HEIGHT)){
               ballVX *= -1;
           }
            else{
               resetBall(); // reset the ball position and velocity
               score1++; // left player scored
            player.stop();
            player.play();
            }
        }
        if (score1 == 10 || score2 == 10) {
            Platform.runLater(() -> {
                showWinner(); // final score reached, show the winner
                askToPlayAgain(); // ask the user if they want to play again
            });
            timeline.stop();
        }
    }
    
    
    if (ballY < BALL_RADIUS || ballY > HEIGHT - BALL_RADIUS) {
        ballVY *= -1; // reverse vertical direction
        
    }
    

    // Check for collisions with the paddles and bounce the ball
    if (ballX < PADDLE_WIDTH * 3 && ballY > paddle1Y - PADDLE_HEIGHT / 2 && ballY < paddle1Y + PADDLE_HEIGHT / 2) {
        ballVX *= -1; // reverse horizontal direction
        ballVY += (ballY - paddle1Y) / 10; // add some spin
    }
    if (ballX > WIDTH - PADDLE_WIDTH * 3 && ballY > paddle2Y - PADDLE_HEIGHT / 2 && ballY < paddle2Y + PADDLE_HEIGHT / 2) {
        ballVX *= -1; // reverse horizontal direction
        ballVY += (ballY - paddle2Y) / 10; // add some spin
    }

    // Move the paddles by their speed
    
   if (upPressed && !computerMode) {
        paddle2Y -= PADDLE_SPEED;
    }
    if (downPressed && !computerMode) {
        paddle2Y += PADDLE_SPEED;
    }
    if (wPressed ) {
        paddle1Y -= PADDLE_SPEED;
    }
    if (sPressed  ) {
        paddle1Y += PADDLE_SPEED;
    }

    // Keep the paddles within the bounds
    if (paddle1Y < PADDLE_HEIGHT / 2) {
        paddle1Y = PADDLE_HEIGHT / 2;
    }
    if (paddle1Y > HEIGHT - PADDLE_HEIGHT / 2) {
        paddle1Y = HEIGHT - PADDLE_HEIGHT / 2;
    }
    if (paddle2Y < PADDLE_HEIGHT / 2) {
        paddle2Y = PADDLE_HEIGHT / 2;
    }
    if (paddle2Y > HEIGHT - PADDLE_HEIGHT / 2) {
        paddle2Y = HEIGHT - PADDLE_HEIGHT / 2;
    }

    // Move the second paddle by following the ball if computer mode is on
    if (computerMode) {
        
        if(computerDifficulty==1){
    paddle2Y += (ballY-paddle2Y)/30;

}
else if(computerDifficulty==2){
    paddle2Y += (ballY-paddle2Y)/10;

}
else if(computerDifficulty==3){
paddle2Y = ballY;

}
    }
}
     // Method to handle key pressed events
    private void onKeyPressed(KeyEvent event) {
        // Set the keys pressed flags
        if (event.getCode() == KeyCode.UP) {
            upPressed = true;
        }
        if (event.getCode() == KeyCode.DOWN) {
            downPressed = true;
        }
        if (event.getCode() == KeyCode.W) {
            wPressed = true;
        }
        if (event.getCode() == KeyCode.S) {
            sPressed = true;
        }
    }
    
  // Method to handle key released events
    private void onKeyReleased(KeyEvent event) {
        // Unset the keys pressed flags
        if (event.getCode() == KeyCode.UP) {
            upPressed = false;
        }
        if (event.getCode() == KeyCode.DOWN) {
            downPressed = false;
        }
        if (event.getCode() == KeyCode.W) {
            wPressed = false;
        }
        if (event.getCode() == KeyCode.S) {
            sPressed = false;
        }
    }
  // Method to show a page before start the game to say that final score is 10
    private void showStartPage() {
        
    
    // Create an alert dialog with one button
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome to Pong");
        alert.setHeaderText("The first player to reach 10 points wins the game.");
        alert.setContentText("Press OK to start.");

        ButtonType buttonTypeOk = new ButtonType("OK");

        alert.getButtonTypes().setAll(buttonTypeOk);

        // Show the alert and wait for the user's response
        alert.showAndWait();
    }
    
    // Method to show a message to declare who is the winner
    
    private void showWinner() {
        // sound of win
         Win.stop();
        Win.play();
        // Create an alert dialog with one button
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        if (score1 == 10) {
            alert.setHeaderText("The left player wins!");
        } else if(score2==10) {
            alert.setHeaderText("The right player wins!");
        }
        alert.setContentText("Press OK to continue.");

        ButtonType buttonTypeOk = new ButtonType("OK");

        alert.getButtonTypes().setAll(buttonTypeOk);

        // Show the alert and wait for the user's response
        alert.showAndWait();
    }
     // Method to ask the user if they want to play again
    
    private void askToPlayAgain() {
        
    // Create an alert dialog with two buttons
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Play Again");
        alert.setHeaderText("Do you want to play again?");
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        // Show the alert and wait for the user's choice
        
        alert.showAndWait().ifPresent(type -> {
            if (type == buttonTypeYes) {
                
                // Reset the game state
                reset();
                chooseMode();
                 timeline.play();
            } else if (type == buttonTypeNo) {
                
                // Exit the game
                System.exit(0);
            }
        });
    }
    
 // Method to ask the user to choose the mode
   
   private void chooseDifficulty() {
       
// Create an alert dialog with three buttons

Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
alert.setTitle("Choose Difficulty");
alert.setHeaderText("How hard do you want the computer to be?");
alert.setContentText("Choose your option.");

ButtonType buttonTypeEasy = new ButtonType("Easy");
ButtonType buttonTypeMedium = new ButtonType("Medium");
ButtonType buttonTypeHard = new ButtonType("Hard");
alert.getButtonTypes().setAll(buttonTypeEasy, buttonTypeMedium, buttonTypeHard);

// Show the alert and wait for the user's choice

alert.showAndWait().ifPresent(type -> {
    if (type == buttonTypeEasy) {
        
        // Set the computer difficulty level to 1
        
        computerDifficulty = 1;
    } else if (type == buttonTypeMedium) {
        
        // Set the computer difficulty level to 2
        
        computerDifficulty = 2;
    } else if (type == buttonTypeHard) {
        
        // Set the computer difficulty level to 3
        
        computerDifficulty = 3;
    }
            

});
}

private void chooseMode() {
    
    // Create an alert dialog with two buttons
    
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Choose Mode");
    alert.setHeaderText("Do you want to play with a friend or the computer?");
    alert.setContentText("Choose your option.");

    ButtonType buttonTypeOne = new ButtonType("Friend");
    ButtonType buttonTypeTwo = new ButtonType("Computer");

    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

    // Show the alert and wait for the user's choice
    
    alert.showAndWait().ifPresent(type -> {
        if (type == buttonTypeOne) {
            
            // Set the computer mode flag to false
            
            computerMode = false;
        } else if (type == buttonTypeTwo) {
            
            // Set the computer mode flag to true
            
            computerMode = true;
            chooseDifficulty();
        }
        
        
        // Start the animation timer after the user has chosen a mode
        
        timeline.play();
        
        
    });
}




    // Method to reset the ball position and velocity

    private void resetBall() {

// Reset the ball position and velocity

        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;
        ballVX = Math.random() < 0.5 ? -5 : 5; // random direction
        ballVY = Math.random() * 10 - 5; // random speed
    }

   
    public static void main(String[] args) {
        
        launch(args);
        
    }
    
}