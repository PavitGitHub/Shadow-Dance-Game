import bagel.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Skeleton Code for SWEN20003 Project 1, Semester 2, 2023
 * Please enter your name below
 * Pavit Vathna
 * @author
 */
public class ShadowDance extends AbstractGame  {

    // default window size
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;

    // positions for title, instruction, score messages
    private final static int TITLE_X = 220;
    private final static int TITLE_Y = 250;
    private final static int INSTRUCTION_X = 220 + 100;
    private final static int INSTRUCTION_Y = 250 + 190;
    private final static int SPACING = 30;
    private final static int SCORE_X = 35;
    private final static int SCORE_Y = 35;

    // font style and sizes
    private final int NORMAL_FONT_SIZE = 64;
    private final Font normalFont = new Font("res/FSO8BITR.TTF", NORMAL_FONT_SIZE);
    private final int INSTRUCTION_FONT_SIZE = 24;
    private final Font instructionFont = new Font("res/FSO8BITR.TTF", INSTRUCTION_FONT_SIZE);
    private final int SCORE_FONT_SIZE = 30;
    private final Font scoreFont = new Font("res/FSO8BITR.TTF", SCORE_FONT_SIZE);

    // messages for game
    private static final String TITLE = "SHADOW DANCE";
    private static final String START = "PRESS SPACE TO START";
    private static final String PLAY = "USE ARROW KEYS TO PLAY";
    private static final String WIN = "CLEAR!";
    private static final String LOSE = "TRY AGAIN";

    // images for game
    private final Image BACKGROUND_IMAGE = new Image("res/background.png");
    private final Image LANE_LEFT_IMAGE = new Image("res/laneLeft.png");
    private final Image LANE_UP_IMAGE = new Image("res/laneUp.png");
    private final Image LANE_DOWN_IMAGE = new Image("res/laneDown.png");
    private final Image LANE_RIGHT_IMAGE = new Image("res/laneRight.png");

    // state of the game
    private boolean gameIsRunning = false;
    private boolean gameIsPaused = false;
    private boolean gameisOver = false;
    private static int gameFrame = 0;

    // lanes
    Lane leftLane = new Lane();
    Lane rightLane = new Lane();
    Lane upLane = new Lane();
    Lane downLane = new Lane();
    ArrayList<Note> laneLeftNotes = new ArrayList<>();
    ArrayList<Holdnote> laneLeftHoldNotes = new ArrayList<>();
    ArrayList<Note> laneUpNotes = new ArrayList<>();
    ArrayList<Holdnote> laneUpHoldNotes = new ArrayList<>();
    ArrayList<Note> laneRightNotes = new ArrayList<>();
    ArrayList<Holdnote> laneRightHoldNotes = new ArrayList<>();
    ArrayList<Note> laneDownNotes = new ArrayList<>();
    ArrayList<Holdnote> laneDownHoldNotes = new ArrayList<>();

    // game score
    private final int TARGET_SCORE = 150;
    private int playerScore = 0;

    // speed of note
    private final double STEP_SIZE = 2;
    private int points = 0;

    // boolean flags to track key states
    private boolean leftKeyHeld = false;
    private boolean rightKeyHeld = false;
    private boolean upKeyHeld = false;
    private boolean downKeyHeld = false;
    public ShadowDance(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, TITLE);
        readCSV();
    }

    /**
     * Method used to read file and create objects (you can change
     * this method as you wish).
     */
    private void readCSV() {

        // Check if file exists from "GROK: FILE MANIPUlATION"
        try (BufferedReader br = new BufferedReader(new FileReader("res/test1.csv"))) {
            String text;

            // reading file
            while ((text = br.readLine()) != null) {
                String[] columns = text.split(",");

                // check for lanes
                if (columns[0].equals("Lane")) {
                    if (columns[1].equals("Up")) {
                        upLane.setType(columns[1]);
                        upLane.setLaneX(Integer.parseInt(columns[2]));
                    } else if (columns[1].equals("Down")) {
                        downLane.setType(columns[1]);
                        downLane.setLaneX(Integer.parseInt(columns[2]));
                    } else if (columns[1].equals("Left")) {
                        leftLane.setType(columns[1]);
                        leftLane.setLaneX(Integer.parseInt(columns[2]));
                    } else if (columns[1].equals("Right")) {
                        rightLane.setType(columns[1]);
                        rightLane.setLaneX(Integer.parseInt(columns[2]));
                    }

                // check for notes
                } else {

                    // check for normal notes
                    if (columns[1].equals("Normal")) {
                        Note note = new Note();
                        note.setLaneType(columns[0]);
                        note.setNoteType(columns[1]);
                        note.setFrameNumber(Integer.parseInt(columns[2]));

                        if (columns[0].equals("Left")) {
                            leftLane.addNote(note);
                        } else if (columns[0].equals("Up")) {
                            upLane.addNote(note);
                        } else if (columns[0].equals("Down")) {
                            downLane.addNote(note);
                        } else if (columns[0].equals("Right")) {
                            rightLane.addNote(note);
                        }

                    // check for hold notes
                    } else if (columns[1].equals("Hold")) {
                        Holdnote holdNote = new Holdnote();
                        holdNote.setLaneType(columns[0]);
                        holdNote.setNoteType(columns[1]);
                        holdNote.setFrameNumber(Integer.parseInt(columns[2]));

                        if (columns[0].equals("Left")) {
                            leftLane.addHoldNote(holdNote);
                        } else if (columns[0].equals("Up")) {
                            upLane.addHoldNote(holdNote);
                        } else if (columns[0].equals("Down")) {
                            downLane.addHoldNote(holdNote);
                        } else if (columns[0].equals("Right")) {
                            rightLane.addHoldNote(holdNote);
                        }
                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        new ShadowDance().run();
    }

    /**
     * Performs a state update.
     * Allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {

        // game is not running
        if (!gameIsRunning) {
            BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
            normalFont.drawString(TITLE, TITLE_X, TITLE_Y);
            instructionFont.drawString(START, INSTRUCTION_X, INSTRUCTION_Y);
            instructionFont.drawString(PLAY, INSTRUCTION_X, INSTRUCTION_Y + SPACING);

        // game is running
        } else {

            // game is not paused
            if (!gameIsPaused) {

                // game is not over
                if (!gameisOver) {

                    /* draw the background */
                    BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

                    /* draw the player's score */
                    scoreFont.drawString(String.format("SCORE %d", playerScore), 35, 35);

                    /* draw the images of the lanes */
                    LANE_LEFT_IMAGE.draw(leftLane.getLaneX(), leftLane.getLaneY());
                    LANE_UP_IMAGE.draw(upLane.getLaneX(), upLane.getLaneY());
                    LANE_DOWN_IMAGE.draw(downLane.getLaneX(), downLane.getLaneY());
                    LANE_RIGHT_IMAGE.draw(rightLane.getLaneX(), rightLane.getLaneY());

                    /* update values of notes and hold notes y position and keep printing them */
                    leftLane.updateAllNotes(laneLeftNotes, laneLeftHoldNotes, STEP_SIZE);
                    upLane.updateAllNotes(laneUpNotes, laneUpHoldNotes, STEP_SIZE);
                    downLane.updateAllNotes(laneDownNotes, laneDownHoldNotes, STEP_SIZE);
                    rightLane.updateAllNotes(laneRightNotes, laneRightHoldNotes, STEP_SIZE);

                    /* store notes and hold notes into their respective lane and print them once */
                    leftLane.drawAllNotes(gameFrame, laneLeftNotes, laneLeftHoldNotes);
                    upLane.drawAllNotes(gameFrame, laneUpNotes, laneUpHoldNotes);
                    downLane.drawAllNotes(gameFrame, laneDownNotes, laneDownHoldNotes);
                    rightLane.drawAllNotes(gameFrame, laneRightNotes, laneRightHoldNotes);

                    /* Keys Pressing when playing game, can still press keys even if keys are not there */
                    if (input.wasPressed(Keys.LEFT) && !laneLeftNotes.isEmpty()) {
                        playerScore += leftLane.distanceNote(laneLeftNotes);
                    }
                    if (input.wasPressed(Keys.RIGHT) && !laneRightNotes.isEmpty()) {
                        playerScore += rightLane.distanceNote(laneRightNotes);
                    }
                    if (input.wasPressed(Keys.UP) && !laneUpNotes.isEmpty()) {
                        playerScore += upLane.distanceNote(laneUpNotes);
                    }
                    if (input.wasPressed(Keys.DOWN) && !laneDownNotes.isEmpty()) {
                        playerScore += downLane.distanceNote(laneDownNotes);
                    }

                    /* Hold keys when playing game, can still hold even if hold notes are not there */
                    if (input.isDown(Keys.UP) && !input.wasPressed(Keys.UP) && !laneUpHoldNotes.isEmpty()) {
                        points = upLane.initialDistHoldNote(laneUpHoldNotes);
                        if (input.wasReleased(Keys.UP)) {
                            playerScore += points;
                            playerScore += upLane.distanceHoldNote(laneUpHoldNotes);
                        }
                    }

                    if (input.isDown(Keys.DOWN) && !laneDownHoldNotes.isEmpty()) {
                        points = downLane.initialDistHoldNote(laneDownHoldNotes);
                        if (input.wasReleased(Keys.DOWN)) {
                            playerScore += points;
                            playerScore += downLane.distanceHoldNote(laneDownHoldNotes);
                        }
                    }

                    if (input.isDown(Keys.LEFT) && !laneLeftHoldNotes.isEmpty()) {
                        points = leftLane.initialDistHoldNote(laneLeftHoldNotes);
                        if (input.wasReleased(Keys.LEFT)) {
                            playerScore += points;
                            playerScore += leftLane.distanceHoldNote(laneLeftHoldNotes);
                        }
                    }

                    if (input.isDown(Keys.RIGHT) && !laneRightHoldNotes.isEmpty()) {
                        points = rightLane.initialDistHoldNote(laneRightHoldNotes);
                        if (input.wasReleased(Keys.RIGHT)) {
                            playerScore += points;
                            playerScore += rightLane.distanceHoldNote(laneRightHoldNotes);
                        }
                    }

                    /* Game is Over */
                    if (leftLane.getCount() == 18 && laneRightHoldNotes.isEmpty() && laneUpHoldNotes.isEmpty() && laneDownHoldNotes.isEmpty() && laneLeftHoldNotes.isEmpty()
                    && laneLeftNotes.isEmpty() && laneRightNotes.isEmpty() && laneUpNotes.isEmpty() && laneDownNotes.isEmpty()) {
                        gameisOver = true;
                    }

                /* Game is Over */
                } else {
                    BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
                    // user won the game
                    if (playerScore > TARGET_SCORE) {
                        normalFont.drawString(WIN, (Window.getWidth()-normalFont.getWidth(WIN))/2.0, (Window.getHeight()-NORMAL_FONT_SIZE)/2.0);

                        // user lost the game
                    } else {
                        normalFont.drawString(LOSE, (Window.getWidth()-normalFont.getWidth(LOSE))/2.0, (Window.getHeight()-NORMAL_FONT_SIZE)/2.0);
                    }
                }

                gameFrame++;

            // game is paused
            } else {
                BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
                scoreFont.drawString(String.format("SCORE %d", playerScore), SCORE_X, SCORE_Y);
                normalFont.drawString("GAME IS PAUSED!", (Window.getWidth()-normalFont.getWidth("GAME IS PAUSED!"))/2.0, (Window.getHeight()-NORMAL_FONT_SIZE)/2.0);

            }
        }

        // start game
        if (input.wasPressed(Keys.SPACE)) {
            gameIsRunning = true;
        }
        // close game
        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }
        // pause game
        if (input.wasPressed(Keys.TAB)) {
            gameIsPaused = true;
        }
        // unpause game
        if (input.wasPressed(Keys.SLASH)) {
            gameIsPaused = false;
        }
    }
}
