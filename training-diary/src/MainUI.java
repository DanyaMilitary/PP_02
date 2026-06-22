import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class MainUI extends JFrame {
    private Database db;
    private int userId;
    private String username;
    private DefaultTableModel setsTableModel;
    private DefaultTableModel recordsTableModel;
    private DefaultTableModel historyTableModel;
    private JComboBox<String> exerciseComboBox;
    private JTextField weightField;
    private JTextField repsField;
    private JTextField dateField;
    private List<Exercise> exercises = new ArrayList<>();
    private String currentDate;

    public MainUI(Database db, int userId, String username) {
        this.db = db;
        this.userId = userId;
        this.username = username;
        this.currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        setTitle("🏋️ Дневник тренировок - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);

        exercises = db.getExercises(userId);
        System.out.println("Загружено упражнений: " + exercises.size());

        initComponents();
        loadTodaySets();
        loadRecords();
        setVisible(true);
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("💪 Тренировка", createTrainingPanel());
        tabbedPane.addTab("🏆 Рекорды", createRecordsPanel());
        tabbedPane.addTab("📊 История", createHistoryPanel());
        add(tabbedPane);
    }

    private JPanel createTrainingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("📅 Дата тренировки"));

        topPanel.add(new JLabel("Дата:"));
        dateField = new JTextField(currentDate, 12);
        dateField.setEditable(false);
        topPanel.add(dateField);

        JButton changeDateButton = new JButton("Изменить");
        changeDateButton.addActionListener(e -> {
            String newDate = JOptionPane.showInputDialog(this, "Дата (ГГГГ-ММ-ДД):", currentDate);
            if (newDate != null && !newDate.trim().isEmpty()) {
                try {
                    LocalDate.parse(newDate);
                    currentDate = newDate;
                    dateField.setText(currentDate);
                    loadTodaySets();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Неверный формат!");
                }
            }
        });
        topPanel.add(changeDateButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Подходы за " + currentDate));

        String[] columns = {"ID", "Упражнение", "Вес (кг)", "Повторения", "Дата"};
        setsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable setsTable = new JTable(setsTableModel);
        setsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(setsTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        addPanel.setBorder(BorderFactory.createTitledBorder("➕ Добавить подход"));

        exerciseComboBox = new JComboBox<>();
        exerciseComboBox.setPreferredSize(new Dimension(200, 30));

        for (Exercise ex : exercises) {
            exerciseComboBox.addItem(ex.getName());
        }
        if (exerciseComboBox.getItemCount() == 0) {
            exerciseComboBox.addItem("Нет упражнений");
        }

        weightField = new JTextField(8);
        repsField = new JTextField(8);

        JButton addButton = new JButton("Добавить");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.black);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addSet());

        addPanel.add(new JLabel("Упражнение:"));
        addPanel.add(exerciseComboBox);
        addPanel.add(new JLabel("Вес (кг):"));
        addPanel.add(weightField);
        addPanel.add(new JLabel("Повторения:"));
        addPanel.add(repsField);
        addPanel.add(addButton);

        JPanel managePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        managePanel.setBorder(BorderFactory.createTitledBorder("Управление"));

        JButton editButton = new JButton("✏️ Редактировать");
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.black);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> editSelectedSet(setsTable));

        JButton deleteButton = new JButton("🗑️ Удалить");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.black);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteSelectedSet(setsTable));

        JButton refreshButton = new JButton("🔄 Обновить");
        refreshButton.setBackground(new Color(52, 73, 94));
        refreshButton.setForeground(Color.black);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            loadTodaySets();
        });

        JButton manageExercisesButton = new JButton("📋 Упражнения");
        manageExercisesButton.setBackground(new Color(155, 89, 182));
        manageExercisesButton.setForeground(Color.black);
        manageExercisesButton.setFocusPainted(false);
        manageExercisesButton.addActionListener(e -> {
            new ExercisesDialog(this, db, userId, this);
        });

        managePanel.add(editButton);
        managePanel.add(deleteButton);
        managePanel.add(refreshButton);
        managePanel.add(manageExercisesButton);

        bottomPanel.add(addPanel, BorderLayout.NORTH);
        bottomPanel.add(managePanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("🏆 Личные рекорды"));
        topPanel.add(new JLabel("Максимальный вес и максимальные повторения в каждом упражнении"));

        String[] columns = {"Упражнение", "Вес (кг)", "Повторения", "Дата"};
        recordsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable recordsTable = new JTable(recordsTableModel);
        recordsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(recordsTable);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        datePanel.setBorder(BorderFactory.createTitledBorder("📅 Выбор периода"));

        JTextField startDateField = new JTextField(LocalDate.now().minusDays(30).toString(), 12);
        JTextField endDateField = new JTextField(currentDate, 12);
        JButton viewButton = new JButton("📊 Показать");
        viewButton.setBackground(new Color(52, 152, 219));
        viewButton.setForeground(Color.black);
        viewButton.setFocusPainted(false);
        viewButton.addActionListener(e -> {
            loadHistory(startDateField.getText(), endDateField.getText());
        });

        datePanel.add(new JLabel("С:"));
        datePanel.add(startDateField);
        datePanel.add(new JLabel("По:"));
        datePanel.add(endDateField);
        datePanel.add(viewButton);

        String[] columns = {"Дата", "Упражнение", "Вес (кг)", "Повторения"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable historyTable = new JTable(historyTableModel);
        historyTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        panel.add(datePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Загружаем историю по умолчанию
        loadHistory(LocalDate.now().minusDays(30).toString(), currentDate);

        return panel;
    }

    public void loadTodaySets() {
        if (setsTableModel == null) return;

        setsTableModel.setRowCount(0);
        System.out.println("Загружаем подходы за " + currentDate);

        List<Set> allSets = db.getSetsByDate(userId, currentDate);
        System.out.println("Найдено подходов: " + allSets.size());

        for (Set set : allSets) {
            String exerciseName = set.getExerciseName();
            if (exerciseName == null) {
                exerciseName = exercises.stream()
                        .filter(e -> e.getId() == set.getExerciseId())
                        .map(Exercise::getName)
                        .findFirst()
                        .orElse("Неизвестно");
            }

            setsTableModel.addRow(new Object[]{
                    set.getId(),
                    exerciseName,
                    set.getWeight(),
                    set.getReps(),
                    set.getWorkoutDate()
            });
        }

        try {
            JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(0);
            JPanel trainingPanel = (JPanel) tabbedPane.getComponentAt(0);
            JPanel centerPanel = (JPanel) ((BorderLayout) trainingPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            centerPanel.setBorder(BorderFactory.createTitledBorder("Подходы за " + currentDate));
            centerPanel.revalidate();
            centerPanel.repaint();
        } catch (Exception e) {}
    }

    private void addSet() {
        if (exerciseComboBox == null || exerciseComboBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Нет упражнений!");
            return;
        }

        try {
            String weightText = weightField.getText().trim();
            String repsText = repsField.getText().trim();

            if (weightText.isEmpty() || repsText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните вес и повторения!");
                return;
            }

            double weight = Double.parseDouble(weightText);
            int reps = Integer.parseInt(repsText);

            if (weight <= 0 || reps <= 0) {
                JOptionPane.showMessageDialog(this, "Вес и повторения должны быть положительными!");
                return;
            }

            String exerciseName = (String) exerciseComboBox.getSelectedItem();
            if (exerciseName == null || exerciseName.equals("Нет упражнений")) {
                JOptionPane.showMessageDialog(this, "Выберите упражнение!");
                return;
            }

            Exercise exercise = exercises.stream()
                    .filter(e -> e.getName().equals(exerciseName))
                    .findFirst()
                    .orElse(null);

            if (exercise == null) {
                String muscleGroup = "Другое";
                if (exerciseName.contains("жим") || exerciseName.contains("разведение") || exerciseName.contains("отжимания")) {
                    muscleGroup = "Грудь";
                } else if (exerciseName.contains("тяга") || exerciseName.contains("подтягивания") || exerciseName.contains("становая")) {
                    muscleGroup = "Спина";
                } else if (exerciseName.contains("приседания") || exerciseName.contains("выпады") || exerciseName.contains("жим ногами")) {
                    muscleGroup = "Ноги";
                } else if (exerciseName.contains("бицепс") || exerciseName.contains("трицепс") || exerciseName.contains("французский")) {
                    muscleGroup = "Руки";
                } else if (exerciseName.contains("плечи") || exerciseName.contains("разводка")) {
                    muscleGroup = "Плечи";
                } else if (exerciseName.contains("пресс") || exerciseName.contains("скручивания") || exerciseName.contains("планка")) {
                    muscleGroup = "Пресс";
                }

                db.addExercise(userId, exerciseName, muscleGroup, true);
                exercises = db.getExercises(userId);

                exerciseComboBox.removeAllItems();
                for (Exercise ex : exercises) {
                    exerciseComboBox.addItem(ex.getName());
                }

                exercise = exercises.stream()
                        .filter(e -> e.getName().equals(exerciseName))
                        .findFirst()
                        .orElse(null);
            }

            if (exercise != null) {
                // ПРАВИЛЬНЫЙ ВЫЗОВ - СНАЧАЛА userId, ПОТОМ exerciseId
                boolean success = db.addSet(userId, exercise.getId(), currentDate, weight, reps);
                if (success) {
                    weightField.setText("");
                    repsField.setText("");
                    loadTodaySets();
                    loadRecords();
                    JOptionPane.showMessageDialog(this, "✅ Подход добавлен!");
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Ошибка!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите корректные числа!");
        }
    }

    private void editSelectedSet(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите подход для редактирования!");
            return;
        }

        int setId = (int) setsTableModel.getValueAt(selectedRow, 0);
        String exerciseName = (String) setsTableModel.getValueAt(selectedRow, 1);
        double currentWeight = (double) setsTableModel.getValueAt(selectedRow, 2);
        int currentReps = (int) setsTableModel.getValueAt(selectedRow, 3);

        JDialog editDialog = new JDialog(this, "✏️ Редактировать подход", true);
        editDialog.setLayout(new GridBagLayout());
        editDialog.setSize(350, 200);
        editDialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        editDialog.add(new JLabel("Упражнение:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField exerciseField = new JTextField(exerciseName, 15);
        exerciseField.setEditable(false);
        exerciseField.setBackground(Color.LIGHT_GRAY);
        editDialog.add(exerciseField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        editDialog.add(new JLabel("Вес (кг):"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField weightFieldEdit = new JTextField(String.valueOf(currentWeight), 10);
        editDialog.add(weightFieldEdit, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        editDialog.add(new JLabel("Повторения:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField repsFieldEdit = new JTextField(String.valueOf(currentReps), 10);
        editDialog.add(repsFieldEdit, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton saveButton = new JButton("💾 Сохранить");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.black);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> {
            try {
                double newWeight = Double.parseDouble(weightFieldEdit.getText().trim());
                int newReps = Integer.parseInt(repsFieldEdit.getText().trim());

                if (newWeight <= 0 || newReps <= 0) {
                    JOptionPane.showMessageDialog(editDialog, "Вес и повторения должны быть положительными!");
                    return;
                }

                boolean success = db.updateSet(setId, newWeight, newReps);
                if (success) {
                    editDialog.dispose();
                    loadTodaySets();
                    loadRecords();
                    JOptionPane.showMessageDialog(this, "✅ Подход обновлен!");
                } else {
                    JOptionPane.showMessageDialog(editDialog, "❌ Ошибка при обновлении!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog, "Введите корректные числа!");
            }
        });

        JButton cancelButton = new JButton("❌ Отмена");
        cancelButton.addActionListener(e -> editDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        editDialog.add(buttonPanel, gbc);

        editDialog.setVisible(true);
    }

    private void deleteSelectedSet(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите подход!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Удалить подход?", "Подтверждение", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int setId = (int) setsTableModel.getValueAt(selectedRow, 0);
            db.deleteSet(setId);
            loadTodaySets();
            loadRecords();
            JOptionPane.showMessageDialog(this, "✅ Подход удален!");
        }
    }

    public void loadExercises() {
        exercises = db.getExercises(userId);
        if (exerciseComboBox != null) {
            exerciseComboBox.removeAllItems();
            for (Exercise ex : exercises) {
                exerciseComboBox.addItem(ex.getName());
            }
            if (exerciseComboBox.getItemCount() == 0) {
                exerciseComboBox.addItem("Нет упражнений");
            }
        }
    }

    private void loadRecords() {
        if (recordsTableModel == null) return;
        recordsTableModel.setRowCount(0);
        List<Record> records = db.getRecords(userId);
        for (Record record : records) {
            recordsTableModel.addRow(new Object[]{
                    record.getExerciseName(),
                    record.getWeight(),
                    record.getReps(),
                    record.getRecordDate()
            });
        }
    }

    // ===== МЕТОД ЗАГРУЗКИ ИСТОРИИ - ИСПРАВЛЕН =====
    private void loadHistory(String startDate, String endDate) {
        if (historyTableModel == null) return;

        historyTableModel.setRowCount(0);
        System.out.println("Загружаем историю с " + startDate + " по " + endDate);

        List<Workout> history = db.getWorkoutHistory(userId);
        System.out.println("Всего тренировок: " + history.size());

        int count = 0;
        for (Workout w : history) {
            if (w.getDate().compareTo(startDate) >= 0 && w.getDate().compareTo(endDate) <= 0) {
                historyTableModel.addRow(new Object[]{
                        w.getDate(),
                        w.getExerciseName(),
                        w.getWeight(),
                        w.getReps()
                });
                count++;
            }
        }
        System.out.println("Добавлено в таблицу: " + count + " записей");
    }

    public void refreshData() {
        loadExercises();
        loadTodaySets();
        loadRecords();
    }

    @Override
    public void dispose() {
        if (db != null) {
            db.close();
        }
        super.dispose();
    }
}