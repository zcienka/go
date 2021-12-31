package Player;

public class Player {
    private int numberOfCapturedStones;
    private final int playerNumber;
    private int finalScore;
    private boolean hasPassedMove;
    private boolean resigned;

    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public Player(int numberOfCapturedStones, int playerNumber, int finalScore, boolean hasPassedMove,
            boolean resigned) {
        this.numberOfCapturedStones = numberOfCapturedStones;
        this.playerNumber = playerNumber;
        this.finalScore = finalScore;
        this.hasPassedMove = hasPassedMove;
        this.resigned = resigned;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void addNumberOfCapturedStones(int numberOfCapturedStones) {
        this.numberOfCapturedStones += numberOfCapturedStones;
    }

    public int getNumberOfCapturedStones() {
        return numberOfCapturedStones;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public boolean isMovePassed() {
        return hasPassedMove;
    }

    public void setPassMove(boolean hasPassedMove) {
        this.hasPassedMove = hasPassedMove;
    }

    public void resign() {
        this.resigned = true;
    }

    public boolean hasResigned() {
        return resigned;
    }

    public void setNumberOfCapturedStones(int numberOfCapturedStones) {
        this.numberOfCapturedStones = numberOfCapturedStones;
    }
}
