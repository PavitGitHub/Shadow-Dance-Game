public class Note {

    private final int startingY = 100;
    private double noteY = startingY;
    private String noteType;
    private String laneType;
    private int frameNumber;

    public String getLaneType() {
        return laneType;
    }
    public void setLaneType(String laneType) {
        this.laneType = laneType;
    }
    public int getFrameNumber() {
        return frameNumber;
    }
    public String getNoteType() {
        return noteType;
    }
    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }
    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }
    public double getNoteY() {
        return noteY;
    }
    public void updateNote(double stepSize) {
        noteY += stepSize;
    }
}
