package solving;

import sudoku.Sudoku;

import java.util.concurrent.ExecutorService;

public class SolvingThread implements Runnable {

    private final Sudoku sudoku;
    private final ExecutorService service;
    private final Backtracker parent;

    public SolvingThread(Backtracker parent, Sudoku sudoku, ExecutorService service) {
        this.parent = parent;
        this.sudoku = sudoku;
        this.service = service;
    }

    @Override
    public void run() {
        while (true) {
            if (!sudoku.setDefiniteValues()) break;
        }
        if (!sudoku.isSolvable()) {
            parent.removeActive(this);
            parent.checkActive();
            return;
        }
        if (sudoku.isSolved()) {
            parent.done(sudoku);
            return;
        }
        int guessRow = -1;
        int guessColumn = -1;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (sudoku.getValue(row, column) == 0) {
                    guessRow = row;
                    guessColumn = column;
                }
            }
        }
        for (int possibleSolution : sudoku.getPossibleSolutions(guessRow, guessColumn)) {
            Sudoku guess = new Sudoku(sudoku);
            guess.setValue(guessRow, guessColumn, possibleSolution);
            SolvingThread guessThread = new SolvingThread(parent, guess, service);
            service.submit(guessThread);
            parent.addActive(guessThread);
        }
        parent.removeActive(this);
        parent.checkActive();
    }
}
