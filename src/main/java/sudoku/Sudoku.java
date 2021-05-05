package sudoku;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that has rows and columns from 0 to 8.
 */
public class Sudoku {

    private final int[][] numbers;
    private final SudokuHelper helper;

    public Sudoku(int[][] values) {
        numbers = values;
        helper = new SudokuHelper();
    }

    public Sudoku(Sudoku sudoku) {
        numbers = new int[9][9];
        helper = new SudokuHelper();
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                numbers[row][column] = sudoku.getValue(row, column);
            }
        }
    }

    /**
     * Get the value of the given row and column.
     * @param row
     * @param column
     * @return the value in the field.
     */
    public int getValue(int row, int column) {
        return numbers[row][column];
    }

    /**
     * Sets the value of the matched field.
     * @param row
     * @param column
     * @param value
     */
    public void setValue(int row, int column, int value) {
        numbers[row][column] = value;
    }

    /**
     * Get the entries of a row as a List.
     * @param row to get: starting from 0. Up to 8.
     * @return An integer list.
     */
    public List<Integer> getRow(int row) {
        List<Integer> rowList = new ArrayList<>();
        for (int column = 0; column < 9; column++) {
            rowList.add(getValue(row, column));
        }
        return rowList;
    }

    /**
     * Get the entries of a column as a List.
     * @param column to get: starting from 0. Up to 8.
     * @return An integer list.
     */
    public List<Integer> getColumn(int column) {
        List<Integer> columnList = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            columnList.add(getValue(row, column));
        }
        return columnList;
    }

    /**
     * Get the entries of a square as a List.
     * @param square to get: starting from 0. Up to 8.
     * @return An integer list.
     */
    public List<Integer> getSquare(int square) {
        List<Integer> squareList = new ArrayList<>();
        int startRow = (square / 3) * 3;
        int startColumn = (square % 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squareList.add(getValue(startRow + i, startColumn + j));
            }
        }
        return squareList;
    }

    /**
     * Checks whether or not the sudoku.Sudoku is solved.
     * @return
     */
    public boolean isSolved() {
        boolean isSolved = true;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (getValue(row, column) == 0) {
                    isSolved = false;
                }
            }
        }
        return isSolved;
    }

    /**
     * Checks if sudoku is solvable.
     * @return
     */
    public boolean isSolvable() {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (getValue(row, column) == 0) {
                    if (getPossibleSolutions(row, column).size() == 0) {
                        return false;
                    }
                }
            }
        }
        for (int value = 1; value < 10; value++) {
            for (int row = 0; row < 9; row++) {
                if (!getRow(row).contains(value)) {
                    List<Integer> possibleColumnIndices = getPossibleIndicesInRow(row, value);
                    if (possibleColumnIndices.size() == 1) {
                        if (getValue(row, possibleColumnIndices.get(0)) != 0) {
                            return false;
                        }
                    }
                }
            }
            for (int column = 0; column < 9; column++) {
                if (!getColumn(column).contains(value)) {
                    List<Integer> possibleRowIndices = getPossibleIndicesInColumn(column, value);
                    if (possibleRowIndices.size() == 1) {
                        if (getValue(possibleRowIndices.get(0), column) != 0) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets the values which are obvious.
     * @return
     */
    public boolean setDefiniteValues() {
        boolean valueSet = false;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (getValue(row, column) != 0) {
                    continue;
                }
                List<Integer> possibleSolutions = getPossibleSolutions(row, column);
                if (getPossibleSolutions(row, column).size() == 1) {
                    setValue(row, column, possibleSolutions.get(0));
                    valueSet = true;
                }
            }
        }
        return valueSet;
    }

    public List<Integer> getPossibleSolutions(int row, int column) {
        List<Integer> cut = helper.getCut(helper.getMissing(getRow(row)), helper.getMissing(getColumn(column)));
        cut = helper.getCut(cut, helper.getMissing(getSquare(helper.getSquareIndex(row, column))));
        return cut;
    }

    private List<Integer> getPossibleIndicesInRow(int row, int value) {
        List<Integer> possibleIndices = new ArrayList<>();
        for (int column = 0; column < 9; column++) {
            if (!getColumn(column).contains(value) && getValue(row, column) == 0) {
                possibleIndices.add(column);
            }
        }
        return possibleIndices;
    }

    private List<Integer> getPossibleIndicesInColumn(int column, int value) {
        List<Integer> possibleIndices = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            if (!getRow(row).contains(value) && getValue(row, column) == 0) {
                possibleIndices.add(row);
            }
        }
        return possibleIndices;
    }

    @Override
    public String toString() {
        String sudoku = "";
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                sudoku += (getValue(row, column) + "   ");
                if (column == 8) {
                    sudoku += "\n";
                }
            }
        }
        return sudoku;
    }
}
