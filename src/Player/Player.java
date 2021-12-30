package Player;

public class Player {
    private int numberOfCapturedStones;
    private int playerNumber;
    private int finalScore;

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
}
