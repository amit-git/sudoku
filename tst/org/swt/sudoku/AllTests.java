package org.swt.sudoku;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AllTests {

    private SudokuRunner3 sudoku;

    @Before
    public void init() {
        sudoku = new SudokuRunner3();
    }

    
    @Test
    public void createSolution() {
        boolean status = sudoku.generateSolutionGrid();
        assertTrue(status);
    }

    @Test
    public void findUniqueSolution() {
        int[][] data = { { 0, 0, 9, 0, 4, 0, 0, 5, 0 },
                { 0, 6, 0, 0, 0, 0, 8, 0, 0 }, { 2, 0, 0, 0, 6, 0, 0, 0, 0 },
                { 5, 8, 0, 4, 0, 0, 0, 2, 0 }, { 0, 0, 0, 3, 0, 9, 0, 0, 0 },
                { 0, 1, 0, 0, 0, 6, 0, 4, 7 }, { 0, 0, 0, 0, 2, 0, 0, 0, 9 },
                { 0, 0, 4, 0, 0, 0, 0, 8, 0 }, { 0, 7, 0, 0, 1, 0, 3, 0, 0 } };
        assertTrue(sudoku.hasUniqueSolution(data));
        sudoku.printBoard(sudoku.getSolution());
    }
    
    @Test
    public void createEasyProblem() {
        boolean status = sudoku.generateProblemGrid( GameDifficultyLevel.EASY );
        assertTrue(status);
        //UI sudokuUI = new UI( sudoku );
        //sudokuUI.show();
    }
    
    @Test
    public void createProblemWithModerateDifficulty() {
        boolean status = sudoku.generateProblemGrid( GameDifficultyLevel.MODERATE );
        assertTrue(status);
        sudoku.printBoard(null);
    }
}
