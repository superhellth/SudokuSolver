package sudoku;

import java.util.ArrayList;
import java.util.List;

public class SudokuHelper {

    /**
     * Finds the missing numbers from 1 to 9.
     * @param numbers A set of numbers from 1 to 9.
     * @return set of all missing numbers.
     */
    public List<Integer> getMissing(List<Integer> numbers) {
        List<Integer> notInArray = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            if (!numbers.contains(i)) {
                notInArray.add(i);
            }
        }

        return notInArray;
    }

    /**
     * Finds the cut of the two given lists.
     * @param list1
     * @param list2
     * @return list which contains the cut.
     */
    public List<Integer> getCut(List<Integer> list1, List<Integer> list2) {
        List<Integer> cut = new ArrayList<>();
        for (int number : list1) {
            if (list2.contains(number)) {
                cut.add(number);
            }
        }
        return cut;
    }

    /**
     * Returns the corresponding square index.
     * @param row
     * @param column
     * @return
     */
    public int getSquareIndex(int row, int column) {
        return (row / 3) * 3 + (column / 3);
    }

    /**
     * Fills a random field with a random possible number.
     * @param sudoku
     * @return
     */
    public Sudoku takeAGuess(Sudoku sudoku) {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (sudoku.getValue(row, column) == 0) {
                    sudoku.setValue(row, column, sudoku.getPossibleSolutions(row, column).get(0));
                }
            }
        }
        return sudoku;
    }

}
