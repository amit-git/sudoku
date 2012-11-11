package org.swt.sudoku;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SudokuRunner3 {

    private final static int MAX_REPEATITIONS = 1000;
    private final static int width = 9;
    private final static int height = 9;

    private int[][] board = new int[width][height];

    private ArrayList<int[][]> solutions = new ArrayList<int[][]>();

    public void clearBoard() {
        for(int i=0; i < height; i++) {
            for(int j=0; j < width; j++) {
                board[i][j] = 0;
            }
        }
    }
    
    private static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class CellData {
        ArrayList<Integer> candidates;
        Point point;

        CellData(int x, int y) {
            point = new Point(x, y);
            candidates = new ArrayList<Integer>();

        }
    }

    public boolean generateSolutionGrid() {
        Random rand = new Random();

        ArrayList<Integer> freeCols = new ArrayList<Integer>(9);
        ArrayList<Point> filledPoints = new ArrayList<Point>(9);

        for (int i = 1; i <= 9; i++) {
            int repeatitions = 0;

            filledPoints.clear();

            int col = -1;
            for (int row = 0; row < 9; row += 2) {

                repeatitions++;
                if (repeatitions > MAX_REPEATITIONS) {
                    System.out.println("Come on .. you expect me to repeat after all this ?");
                    return false;
                }

                freeCols.clear();
                for (int j = 0; j <= 8; j++) {
                    freeCols.add(j);
                }

                boolean startOver = true;
                while (startOver) {
                    startOver = false;
                    int size = freeCols.size();

                    if (size == 0) {
                        for (int rollback = 0; rollback < filledPoints.size(); rollback++) {
                            Point p = (Point) filledPoints.get(rollback);
                            this.board[p.x][p.y] = 0;

                        }
                        row = -2;
                        startOver = true;
                        break;
                    }

                    // pick a column from to insert i
                    int randNum = rand.nextInt(size);
                    col = freeCols.get(randNum);

                    if (this.board[row][col] != 0) {
                        freeCols.remove(randNum);
                        startOver = true;
                        continue;
                    }

                    int HB = (int) (row / 3);
                    int VB = (int) (col / 3);

                    for (int k = 0; k < 9; k++) {
                        if (board[k][col] == i) {
                            freeCols.remove(randNum);
                            startOver = true;
                        }
                    }

                    if (startOver)
                        continue;

                    for (int k = HB * 3; k < (HB + 1) * 3; k++) {
                        for (int j = VB * 3; j < (VB + 1) * 3; j++) {
                            if (board[k][j] == i) {
                                freeCols.remove(randNum);
                                startOver = true;
                            }
                        }
                    }
                }

                if (startOver) {
                    continue;
                }

                // insert i into the valid column
                this.board[row][col] = i;
                filledPoints.add(new Point(row, col));

                if (row == 8) {
                    row = -1;
                }
            } // end for row

        } // end for i

        return true;

    }

    public boolean generateProblemGrid( GameDifficultyLevel difficultyLevel ) {
        int noTries = MAX_REPEATITIONS;
        while ( noTries > 0 && ! generateSolutionGrid() ) {
            clearBoard();
            noTries--;
        }
        if ( noTries != 0 ) {
            int numUnfilledBoxes = difficultyLevel.getDifficulty(); // base it on the difficulty level
            Random rand = new Random();
            
            while ( numUnfilledBoxes > 0 ) {
                int boxNum = rand.nextInt(81);
                int xBox = boxNum / 9;
                int yBox = boxNum % 9;
                
                int savedVal1 = board[xBox][yBox];
                int savedVal2 = board[yBox][xBox];
                
                board[xBox][yBox] = 0;
                board[yBox][xBox] = 0;
                if (! hasUniqueSolution(board)  ) {
                    board[xBox][yBox] = savedVal1;
                    board[yBox][xBox] = savedVal2;
                } else {
                    numUnfilledBoxes--;
                }
            }
            
            return true;
        } 
        return false; 

    }
    
    public boolean hasUniqueSolution(int[][] orgProblemGrid) {
        solutions.clear();
        generateSolution(orgProblemGrid);
        return (solutions.size() == 1) ? true : false;
    }
    
    
    
    public void generateSolution(int[][] orgProblemGrid) 
    {
        int[][] problemGrid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                problemGrid[i][j] = orgProblemGrid[i][j];
            }
        }

        ArrayList<CellData> problemCells = new ArrayList<CellData>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (problemGrid[i][j] == 0) {
                    CellData cell = new CellData(i, j);
                    cell.candidates = new ArrayList<Integer>(Arrays.asList(1,
                            2, 3, 4, 5, 6, 7, 8, 9));

                    // remove candidates from the same column
                    for (int k = 0; k < 9; k++) {
                        if (problemGrid[k][j] != 0) {
                            cell.candidates.remove(Integer
                                    .valueOf(problemGrid[k][j]));
                        }
                    }
                    // remove candidates from the same row
                    for (int k = 0; k < 9; k++) {
                        if (problemGrid[i][k] != 0) {
                            cell.candidates.remove(Integer
                                    .valueOf(problemGrid[i][k]));
                        }
                    }
                    // remove candidates from the same cell
                    int HB = (int) i / 3;
                    int VB = (int) j / 3;
                    for (int k = HB * 3; k < (HB + 1) * 3; k++) {
                        for (int l = VB * 3; l < (VB + 1) * 3; l++) {
                            if (problemGrid[k][l] != 0) {
                                cell.candidates.remove(Integer
                                        .valueOf(problemGrid[k][l]));
                            }
                        }
                    }
                    problemCells.add(cell);

                }
            }
        }
        if (problemCells.size() == 1) {
            CellData cell = problemCells.get(0);
            if (cell.candidates.size() == 1) {
                problemGrid[cell.point.x][cell.point.y] = cell.candidates
                        .get(0);
                solutions.add( problemGrid );
            }
            return;
        }

        int minCandidates = 10;
        int minIndex = -1;
        for (int i = 0; i < problemCells.size(); i++) {
            if (problemCells.get(i).candidates.size() < minCandidates) {
                minCandidates = problemCells.get(i).candidates.size();
                minIndex = i;
            }
        }
        if (minIndex > -1) {
            // pick the first candidate
            CellData cell = problemCells.get(minIndex);
            if (cell.candidates.size() > 0) {
                for (int i = 0; i < cell.candidates.size(); i++) {
                    int candidate = cell.candidates.get(i);
                    problemGrid[cell.point.x][cell.point.y] = candidate;
                    generateSolution(problemGrid);
                    // find more solutions if they exist by not continuing with the loop
                    problemGrid[cell.point.x][cell.point.y] = 0;
                }
            }
        }
        return;
    }

    public int[][] getBoard() {
        return this.board;
    }
    
    public int[][] getSolution() {
        return this.solutions.get(0);
    }

    public void printBoard( int [] [] board) {
        if ( board == null ) {
            board = this.board;
        }
        
        System.out.println("-------------- BOARD  ------------------");
        String ret = "";
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                ret += board[i][j] + " ";
            }
            ret += "\n";
        }
        System.out.println(ret);
    }
}
