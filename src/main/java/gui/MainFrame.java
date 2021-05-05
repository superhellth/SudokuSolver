package gui;

import imageAnalysis.ImageAnalyzer;
import solving.Backtracker;
import sudoku.Sudoku;
import sudoku.SudokuHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {

    // data
    private final Sudoku sudoku = new Sudoku(new int[9][9]);
    private final int nThreads = 1;
    private final Backtracker backtracker;

    // data vars
    private final static int MAX_NUM_THREADS = 10;
    private final static int MIN_NUM_THREADS = 1;
    private final static int INIT_NUM_THREADS = 1;

    // gui
    private final JTextField[][] fields = new JTextField[9][9];
    private final JButton calc = new JButton("Solve!");
    private final MainFrame instance;
    private JSlider slider;
    private final JButton cancel = new JButton("Cancel!");
    private final JButton reset = new JButton("Reset!");

    // gui vars
    private final int fieldSize = 60;
    private final int vertBuffer = 25;
    private final int horBuffer;

    public MainFrame() {
        backtracker = new Backtracker(this, sudoku, nThreads);
        instance = this;
        setTitle("Sudoku solver V3");
        setSize(1080, 1920);
        setExtendedState(MAXIMIZED_BOTH);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        horBuffer = (int) (getWidth() / 2 - fieldSize * 4.5);
        createSudokuGui();
        createButton();
        createThreadChooser();
        createCancelButton();
        createResetButton();
        createFileChooser();

        setVisible(false);
        setVisible(true);
        repaint(500);
    }

    public void sendSolution(Sudoku solution) {
        cancel.setEnabled(false);
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                JTextField field = fields[row][column];
//                field.setEnabled(false);
                if (field.getText().equals("")) {
                    field.setForeground(Color.GRAY);
                } else {
                    field.setForeground(Color.WHITE);
                }
                field.setText(String.valueOf(solution.getValue(row, column)));
            }
        }
    }

    public void sendFail() {
        cancel.setEnabled(false);
        JOptionPane.showConfirmDialog(this, "Sudoku is not solvable!", "Wrong inputs!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                JTextField field = fields[row][column];
                field.setForeground(Color.RED);
            }
        }
    }

    private void createSudokuGui() {
        SudokuHelper helper = new SudokuHelper();
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                int square = helper.getSquareIndex(row, column);
                JTextField textField = new JTextField();
                textField.setSize(fieldSize, fieldSize);
                textField.setLocation(horBuffer + column * fieldSize, vertBuffer + row * fieldSize);
                textField.setFont(new Font("Times new Roman", Font.BOLD, fieldSize));
                Color bgColor = square % 2 == 0 ? Color.BLACK : Color.DARK_GRAY;
                textField.setBackground(bgColor);
                textField.setForeground(Color.WHITE);
                textField.setHorizontalAlignment(SwingConstants.CENTER);
                textField.setColumns(1);
                add(textField);
                fields[row][column] = textField;
            }
        }
    }

    private void createFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSize(650, 400);
        chooser.setLocation(25, 20);
        chooser.addActionListener(e -> {
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File(chooser.getSelectedFile().getAbsolutePath()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            ImageAnalyzer analyzer = new ImageAnalyzer(image);
            int[][] values = analyzer.getValues();
            for (int row = 0; row < 9; row++) {
                for (int column = 0; column < 9; column++) {
                    fields[row][column].setForeground(Color.WHITE);
                    if (values[row][column] != 0) {
                        fields[row][column].setText(String.valueOf(values[row][column]));
                    } else {
                        fields[row][column].setText("");
                    }
                    sudoku.setValue(row, column, values[row][column]);
                }
            }
        });
        add(chooser);
    }

    private void createButton() {
        calc.setSize(400, 50);
        calc.setLocation(getWidth() / 2 - calc.getWidth() - 12, vertBuffer + fieldSize * 9 + 50);
        calc.setBackground(Color.WHITE);
        calc.setForeground(Color.BLACK);
        calc.setFont(new Font("Times New Roman", Font.BOLD, 45));
        calc.addActionListener(e -> {
            try {
                for (int row = 0; row < 9; row++) {
                    for (int column = 0; column < 9; column++) {
                        String inField = fields[row][column].getText();
                        int value;
                        if (inField.equals("")) {
                            value = 0;
                        } else {
                            value = Integer.parseInt(inField);
                        }
                        if (value > 9 || value < 0) {
                            throw new NumberFormatException();
                        }
                        sudoku.setValue(row, column, value);
                    }
                }
                backtracker.setSudoku(sudoku);
                backtracker.setNThreads(slider.getValue());
                if (sudoku.isSolvable()) {
                    cancel.setEnabled(true);
                    backtracker.solve();
                } else {
                    JOptionPane.showConfirmDialog(instance, "Sudoku is not solvable!", "Wrong inputs!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showConfirmDialog(instance, "Please only write one integer digit per field!", "Wrong inputs!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });
        add(calc);
    }

    private void createCancelButton() {
        cancel.setEnabled(false);
        cancel.setSize(calc.getWidth(), calc. getHeight());
        cancel.setLocation(getWidth() / 2 + 12, calc.getY());
        cancel.setBackground(Color.WHITE);
        cancel.setForeground(Color.RED);
        cancel.setFont(calc.getFont());
        cancel.addActionListener(e -> {
            backtracker.terminate();
            cancel.setEnabled(false);
        });
        add(cancel);
    }

    private void createResetButton() {
        reset.setSize(calc.getWidth(), calc.getHeight());
        reset.setLocation(getWidth() / 2 - reset.getWidth() / 2, slider.getY() + slider.getHeight() + 25);
        reset.setBackground(Color.WHITE);
        reset.setFont(calc.getFont());
        reset.setForeground(Color.BLACK);
        reset.addActionListener(e -> {
            for (int row = 0; row < 9; row++) {
                for (int column = 0; column < 9; column++) {
                    fields[row][column].setText("");
                    fields[row][column].setForeground(Color.WHITE);
                    fields[row][column].setEnabled(true);
                }
            }
        });
        add(reset);
    }

    private void createThreadChooser() {
        JLabel sliderHeader = new JLabel("Number of Threads to use:");
        sliderHeader.setSize(300, 25);
        sliderHeader.setLocation(getWidth() / 2 - sliderHeader.getWidth() / 2, calc.getY() + calc.getHeight() + 35);
        sliderHeader.setHorizontalAlignment(SwingConstants.CENTER);
        sliderHeader.setFont(new Font("Times New Roman", Font.BOLD, 20));
        add(sliderHeader);
        slider = new JSlider(JSlider.HORIZONTAL, MIN_NUM_THREADS, MAX_NUM_THREADS, INIT_NUM_THREADS);
        slider.setMajorTickSpacing(9);
        slider.setMinorTickSpacing(1);
        slider.setSize(350, 75);
        slider.setBackground(Color.WHITE);
        slider.setLocation(getWidth() / 2 - slider.getWidth() / 2, sliderHeader.getY() + sliderHeader.getHeight() + 5);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);

        add(slider);
    }

}
