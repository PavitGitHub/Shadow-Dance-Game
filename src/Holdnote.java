public class Holdnote {

    private final int startingY = 24;
    private double holdNoteY = startingY;
    private String noteType;
    private String laneType;
    private int frameNumber;

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getLaneType() {
        return laneType;
    }

    public void setLaneType(String laneType) {
        this.laneType = laneType;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }
    public double getHoldNoteY() {
        return holdNoteY;
    }
    public void updateHoldNote(double stepSize) {
        holdNoteY += stepSize;
    }

}
