package solving;

import gui.MainFrame;
import sudoku.Sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Backtracker {

    private ExecutorService service;
    private Sudoku sudoku;
    private final MainFrame parent;
    private final List<SolvingThread> runningThreads = Collections.synchronizedList(new ArrayList<>());

    public Backtracker(MainFrame parent, Sudoku sudoku, int nThreads) {
        this.parent = parent;
        this.sudoku = sudoku;
        service = Executors.newFixedThreadPool(nThreads);
    }

    public void setSudoku(Sudoku sudoku) {
        this.sudoku = sudoku;
    }

    public void setNThreads(int nThreads) {
        service = Executors.newFixedThreadPool(nThreads);
    }

    public void solve() {
        service.submit(new SolvingThread(this, sudoku, service));
    }

    public void done(Sudoku sudoku) {
        service.shutdown();
        parent.sendSolution(sudoku);
    }


    public void terminate() {
        service.shutdown();
    }

    public synchronized void addActive(SolvingThread thread) {
        runningThreads.add(thread);
    }

    public synchronized void removeActive(SolvingThread thread) {
        runningThreads.remove(thread);
    }

    public synchronized void checkActive() {
        if (runningThreads.size() == 0) {
            failedDone();
        }
    }

    private void failedDone() {
        service.shutdown();
        parent.sendFail();
    }
}