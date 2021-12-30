package Player;

public class Player {
    private int numberOfCapturedStones;
    private int playerNumber;
    private int finalScore;
    private boolean hasPassedMove;
    private boolean resigned;
//    public Player(int numberOfCapturedStones) {
//        this.numberOfCapturedStones = numberOfCapturedStones;
//    }

    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
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

    public void passMove() {
        hasPassedMove = true;
    }

    public void resign() {
        resigned = true;
    }

    public boolean hasResigned() {
        return resigned;
    }

    public void setNumberOfCapturedStones(int numberOfCapturedStones) {
        this.numberOfCapturedStones = numberOfCapturedStones;
    }
}
