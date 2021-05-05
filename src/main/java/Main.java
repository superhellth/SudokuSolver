import gui.MainFrame;

public class Main {

    public static void main(String[] args) {
//        Sudoku sudoku = new Sudoku(createSudoku());
//        System.out.println(sudoku);
//        Backtracker backtracker = new Backtracker(sudoku, 5);
//        backtracker.solve();
        new MainFrame();
    }

    private static int[][] createSudoku() {
        int[][] initValues = new int[9][9];
        int row = 0;
        int column = 0;
        initValues[row][column++] = 6;
        initValues[row][column++] = 4;
        initValues[row][column++] = 0;
        initValues[row][column++] = 9;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row++][column] = 0;
        column = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 1;
        initValues[row][column++] = 3;
        initValues[row][column++] = 0;
        initValues[row++][column] = 0;
        column = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row++][column] = 2;
        column = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 3;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 9;
        initValues[row][column++] = 0;
        initValues[row++][column] = 6;
        column = 0;
        initValues[row][column++] = 1;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 7;
        initValues[row][column++] = 5;
        initValues[row++][column] = 0;
        column = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 2;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 8;
        initValues[row][column++] = 5;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row++][column] = 1;
        column = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 8;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row++][column] = 0;
        column = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 9;
        initValues[row++][column] = 7;
        column = 0;
        initValues[row][column++] = 2;
        initValues[row][column++] = 7;
        initValues[row][column++] = 9;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 0;
        initValues[row][column++] = 6;
        initValues[row][column] = 4;

        return initValues;
    }

}
