import bagel.*;
import java.lang.Math;
import java.util.ArrayList;

public class Lane {
    private final Image NOTE_LEFT_IMAGE = new Image("res/noteLeft.png");
    private final Image NOTE_UP_IMAGE = new Image("res/noteUp.png");
    private final Image NOTE_DOWN_IMAGE = new Image("res/noteDown.png");
    private final Image NOTE_RIGHT_IMAGE = new Image("res/noteRight.png");
    private final Image HOLDNOTE_LEFT_IMAGE = new Image("res/holdNoteLeft.png");
    private final Image HOLDNOTE_UP_IMAGE = new Image("res/holdNoteUp.png");
    private final Image HOLDNOTE_DOWN_IMAGE = new Image("res/holdNoteDown.png");
    private final Image HOLDNOTE_RIGHT_IMAGE = new Image("res/holdNoteRight.png");
    private static final String PERFECT = "PERFECT";
    private static final String GOOD = "GOOD";
    private static final String BAD = "BAD";
    private static final String MISS = "MISS";
    private final int QUALITY_FONT_SIZE = 40;
    private final Font qualityFont = new Font("res/FSO8BITR.TTF", QUALITY_FONT_SIZE);
    private ArrayList<Note> notes;
    private ArrayList<Holdnote> holdNotes;
    private static final int MAX_NOTES = 100;
    private static final int MAX_HOLD_NOTES = 20;
    private static final int BOTTOM_HOLDNOTE = 82;
    private static final int TOP_HOLDNOTE = 82;
    private static final int TARGET_Y_COORDINATE = 657;
    private static final int LANE_Y_COORDINATE = 384;
    private int laneX;
    private String type;

    public int getCount() {
        return count;
    }

    private static int count = 0;

    public Lane() {
        this.notes = new ArrayList<>();
        this.holdNotes = new ArrayList<>();
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public int getLaneX() {
        return laneX;
    }

    public void setLaneX(int laneX) {
        this.laneX = laneX;
    }

    public int getLaneY() {
        return LANE_Y_COORDINATE;
    }
    public int getTargetY() {
        return TARGET_Y_COORDINATE;
    }
    public void addNote(Note note) {
        if (notes.size() < MAX_NOTES) {
            notes.add(note);
        }
    }

    public void addHoldNote(Holdnote holdnote) {
        if (holdNotes.size() < MAX_HOLD_NOTES) {
            holdNotes.add(holdnote);
        }
    }

    private void drawNoteCondition(Note note) {
        if (type.equals("Left")) {
            NOTE_LEFT_IMAGE.draw(laneX, note.getNoteY());
        } else if (type.equals("Up")) {
            NOTE_UP_IMAGE.draw(laneX, note.getNoteY());
        } else if (type.equals("Down")) {
            NOTE_DOWN_IMAGE.draw(laneX, note.getNoteY());
        } else if (type.equals("Right")) {
            NOTE_RIGHT_IMAGE.draw(laneX, note.getNoteY());
        }
    }

    private void drawHoldNoteCondition(Holdnote holdnote) {
        if (type.equals("Left")) {
            HOLDNOTE_LEFT_IMAGE.draw(laneX, holdnote.getHoldNoteY());
        } else if (type.equals("Up")) {
            HOLDNOTE_UP_IMAGE.draw(laneX, holdnote.getHoldNoteY());
        } else if (type.equals("Down")) {
            HOLDNOTE_DOWN_IMAGE.draw(laneX, holdnote.getHoldNoteY());
        } else if (type.equals("Right")) {
            HOLDNOTE_RIGHT_IMAGE.draw(laneX, holdnote.getHoldNoteY());
        }
    }

    public void drawAllNotes(int frame, ArrayList<Note> otherNote, ArrayList<Holdnote> otherHoldNote) {

        for (Note note: notes) {

            if (note.getFrameNumber() == frame) {
                drawNoteCondition(note);
                otherNote.add(note);
                count++;
            }
        }

        for (Holdnote holdnote: holdNotes) {

            if (holdnote.getFrameNumber() == frame) {
                drawHoldNoteCondition(holdnote);
                otherHoldNote.add(holdnote);
                count++;
            }
        }
    }

    public void updateAllNotes(ArrayList<Note> otherNote, ArrayList<Holdnote> otherHoldNote, double stepSize) {

        for (Note note: otherNote) {
            note.updateNote(stepSize); // update next pixel to be printed
            drawNoteCondition(note);
        }

        for (Holdnote holdnote: otherHoldNote) {
            holdnote.updateHoldNote(stepSize); // update next pixel to be printed
            drawHoldNoteCondition(holdnote);
        }
    }

    public int distanceNote(ArrayList<Note> otherNote) {
        int score = 0;
        double y = otherNote.get(0).getNoteY();
        otherNote.remove(0); // remove the notes from the list when we press a key

        // calculate distance
        double distance = Math.abs(TARGET_Y_COORDINATE - y);

        // check which message to print
        if (distance > 0 && distance <= 15) {
            qualityFont.drawString(PERFECT, (Window.getWidth() - qualityFont.getWidth(PERFECT)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = 10;
        } else if (distance > 15 && distance <= 50) {
            qualityFont.drawString(GOOD, (Window.getWidth() - qualityFont.getWidth(GOOD)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = 5;
        } else if (distance> 50 && distance <= 100) {
            qualityFont.drawString(BAD, (Window.getWidth() - qualityFont.getWidth(BAD)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = -1;
        } else if (distance > 100 && distance <= 200) {
            qualityFont.drawString(MISS, (Window.getWidth() - qualityFont.getWidth(MISS)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = -5;
        } else if (distance > 200) {
            qualityFont.drawString(MISS, (Window.getWidth() - qualityFont.getWidth(MISS)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = -5;
        }

        return score;
    }

    public int initialDistHoldNote(ArrayList<Holdnote> otherHoldNote) {
        int score = 0;
        double y = otherHoldNote.get(0).getHoldNoteY();

        // calculate distance
        double distance = Math.abs(TARGET_Y_COORDINATE - y + BOTTOM_HOLDNOTE);

        // check which message to print
        if (distance > 0 && distance <= 15) {
            qualityFont.drawString(PERFECT, (Window.getWidth() - qualityFont.getWidth(PERFECT)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = 10;
        } else if (distance > 15 && distance <= 50) {
            qualityFont.drawString(GOOD, (Window.getWidth() - qualityFont.getWidth(GOOD)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = 5;
        } else if (distance > 50 && distance <= 100) {
            qualityFont.drawString(BAD, (Window.getWidth() - qualityFont.getWidth(BAD)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = -1;
        } else if (distance > 100 && distance <= 200) {
            qualityFont.drawString(MISS, (Window.getWidth() - qualityFont.getWidth(MISS)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = -5;
        } else if (distance > 200) {
            qualityFont.drawString(MISS, (Window.getWidth() - qualityFont.getWidth(MISS)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = -5;
        }

        return score;
    }

    public int distanceHoldNote(ArrayList<Holdnote> otherHoldNote) {

        int score = 0;
        double y = otherHoldNote.get(0).getHoldNoteY();
        otherHoldNote.remove(0); // remove the notes when key is released

        // calculate distance
        double distance = Math.abs(TARGET_Y_COORDINATE - y - TOP_HOLDNOTE);

        // check which message to print
        if (distance > 0 && distance <= 15) {
            qualityFont.drawString(PERFECT, (Window.getWidth() - qualityFont.getWidth(PERFECT)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = 10;
        } else if (distance > 15 && distance <= 50) {
            qualityFont.drawString(GOOD, (Window.getWidth() - qualityFont.getWidth(GOOD)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = 5;
        } else if (distance > 50 && distance <= 100) {
            qualityFont.drawString(BAD, (Window.getWidth() - qualityFont.getWidth(BAD)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = -1;
        } else if (distance > 100 && distance <= 200) {
            qualityFont.drawString(MISS, (Window.getWidth() - qualityFont.getWidth(MISS)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = -5;
        } else if (distance > 200) {
            qualityFont.drawString(MISS, (Window.getWidth() - qualityFont.getWidth(MISS)) / 2.0, (Window.getHeight() - QUALITY_FONT_SIZE) / 2.0);
            score = -5;
        }

        return score;
    }
}
