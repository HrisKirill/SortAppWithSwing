package khrystoforov;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JFrame {
    private static final int MAX_NUMBER = 1000;
    private static final int MIN_VALUE = 30;
    private static final int ROWS_PER_COLUMN = 10;
    private static final String INTRO_LAYOUT_NAME = "Intro";
    private static final String SORT_LAYOUT_NAME = "Sort";
    private List<Integer> numbers;
    private boolean sortDescending = true;

    private JPanel numberPanel;
    private JButton sortButton;
    private JButton enterButton;
    private JButton resetButton;
    private JTextField numberInputField;

    public Main() {
        initFrame();
        createIntroPanel();
        createSortPanel();

        CardLayout cl = (CardLayout) getContentPane().getLayout();
        setupActions(cl);
    }

    private void initFrame() {
        setTitle("Sort Application");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new CardLayout());
        setVisible(true);
    }

    private void createIntroPanel() {
        JPanel introPanel = new JPanel(new GridBagLayout());
        introPanel.setBackground(Color.WHITE);

        numberInputField = new JTextField(10);
        JLabel promptLabel = new JLabel("How many numbers to display?");

        enterButton = new JButton("Enter");
        enterButton.setBackground(Color.BLUE);
        enterButton.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 0, 5, 0);

        Component[] components = {promptLabel, numberInputField, enterButton};

        for (Component component : components) {
            introPanel.add(component, gbc);
            gbc.gridy++;
        }

        add(introPanel, INTRO_LAYOUT_NAME);
    }


    private void createSortPanel() {
        JPanel sortPanel = new JPanel(new BorderLayout());
        numberPanel = new JPanel();
        sortButton = new JButton("Sort Descending");
        resetButton = new JButton("Reset");

        configureButtonColors();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(Box.createVerticalStrut(50));
        buttonPanel.add(sortButton);
        buttonPanel.add(resetButton);

        sortPanel.add(numberPanel, BorderLayout.CENTER);
        sortPanel.add(buttonPanel, BorderLayout.EAST);

        add(sortPanel, SORT_LAYOUT_NAME);
    }

    private void configureButtonColors() {
        sortButton.setBackground(Color.GREEN);
        resetButton.setBackground(Color.GREEN);
        sortButton.setForeground(Color.WHITE);
        resetButton.setForeground(Color.WHITE);
    }

    private void setupActions(CardLayout cl) {
        sortButton.addActionListener(e -> handleSortAction());
        resetButton.addActionListener(e -> cl.show(getContentPane(), INTRO_LAYOUT_NAME));
        enterButton.addActionListener(e -> handleEnterAction(cl));
    }

    private void handleEnterAction(CardLayout cl) {
        int numberCount;
        try {
            numberCount = Integer.parseInt(numberInputField.getText());
            if (numberCount <= 0 || numberCount > MAX_NUMBER) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid integer between 1 and 1000.");
            return;
        }
        generateRandomNumbers(numberCount);
        displayNumbers(numberCount);
        cl.show(getContentPane(), SORT_LAYOUT_NAME);
    }

    private void handleSortAction() {
        quickSort(numbers, 0, numbers.size() - 1);
        sortDescending = !sortDescending;
        sortButton.setText(sortDescending ? "Sort Descending" : "Sort Ascending");
        displayNumbers(numbers.size());
    }

    private void generateRandomNumbers(int count) {
        Random random = new Random();
        numbers = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int value = random.nextInt(MAX_NUMBER) + 1;
            numbers.add(value);
        }

        if (numbers.stream().noneMatch(n -> n <= MIN_VALUE)) {
            numbers.set(random.nextInt(numbers.size()), random.nextInt(MIN_VALUE) + 1);
        }
    }

    private void displayNumbers(int count) {
        numberPanel.removeAll();
        numberPanel.setLayout(new GridLayout(1, 0, 10, 10));

        int numColumns = (int) Math.ceil((double) count / ROWS_PER_COLUMN);

        for (int i = 0; i < numColumns; i++) {
            JPanel columnPanel = new JPanel();
            columnPanel.setLayout(new GridLayout(ROWS_PER_COLUMN, 1, 5, 5));

            for (int j = 0; j < ROWS_PER_COLUMN && i * ROWS_PER_COLUMN + j < count; j++) {
                JButton numberButton = new JButton(String.valueOf(numbers.get(i * ROWS_PER_COLUMN + j)));
                numberButton.setBackground(Color.BLUE);
                numberButton.setForeground(Color.WHITE);
                numberButton.addActionListener(new NumberButtonListener(numbers.get(i * ROWS_PER_COLUMN + j)));
                columnPanel.add(numberButton);
            }

            numberPanel.add(columnPanel);
        }

        numberPanel.revalidate();
        numberPanel.repaint();
    }

    private void quickSort(List<Integer> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);

            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }

    private int partition(List<Integer> list, int low, int high) {
        int pivot = list.get(high);
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (sortDescending == (list.get(j) > pivot)) {
                i++;

                int temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }

        int temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);

        return i + 1;
    }

    private class NumberButtonListener implements ActionListener {
        private final int value;

        public NumberButtonListener(int value) {
            this.value = value;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (value <= MIN_VALUE) {
                generateRandomNumbers(numbers.size());
                displayNumbers(numbers.size());
            } else {
                JOptionPane.showMessageDialog(Main.this, "Please select a value smaller or equal to 30.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
