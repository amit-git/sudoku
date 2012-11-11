package org.swt.sudoku;
public enum GameDifficultyLevel {
    EASY(10),
    MODERATE(15),
    HARD(25);
    
    private int difficulty;

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    
    private GameDifficultyLevel(int difficulty) {
        this.difficulty = difficulty;
    }

}
