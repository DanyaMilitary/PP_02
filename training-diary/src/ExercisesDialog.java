import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ExercisesDialog extends JDialog {
    private Database db;
    private int userId;
    private DefaultTableModel exercisesTableModel;
    private MainUI mainUI;

    public ExercisesDialog(Frame owner, Database db, int userId, MainUI mainUI) {
        super(owner, "📋 Управление упражнениями", true);
        this.db = db;
        this.userId = userId;
        this.mainUI = mainUI;

        setSize(700, 500);
        setLocationRelativeTo(owner);
        initComponents();
        loadExercises();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Панель добавления
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        addPanel.setBorder(BorderFactory.createTitledBorder("➕ Добавить упражнение"));

        JTextField exerciseNameField = new JTextField(20);
        JComboBox<String> muscleGroupCombo = new JComboBox<>();
        String[] muscleGroups = {"Грудь", "Спина", "Ноги", "Плечи", "Руки", "Пресс", "Другое"};
        for (String group : muscleGroups) {
            muscleGroupCombo.addItem(group);
        }

        JButton addExerciseButton = new JButton("Добавить");
        addExerciseButton.setBackground(new Color(46, 204, 113));
        addExerciseButton.setForeground(Color.black);
        addExerciseButton.setFocusPainted(false);
        addExerciseButton.addActionListener(e -> {
            String name = exerciseNameField.getText().trim();
            if (!name.isEmpty()) {
                if (db.addExercise(userId, name, (String) muscleGroupCombo.getSelectedItem(), true)) {
                    JOptionPane.showMessageDialog(this, "✅ Упражнение добавлено!");
                    exerciseNameField.setText("");
                    loadExercises();
                    if (mainUI != null) {
                        mainUI.refreshData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Ошибка! Упражнение с таким названием уже существует.",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addPanel.add(new JLabel("Название:"));
        addPanel.add(exerciseNameField);
        addPanel.add(new JLabel("Группа мышц:"));
        addPanel.add(muscleGroupCombo);
        addPanel.add(addExerciseButton);

        // Таблица упражнений
        String[] columns = {"ID", "Название", "Группа мышц", "Тип"};
        exercisesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable exercisesTable = new JTable(exercisesTableModel);
        exercisesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        exercisesTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(exercisesTable);

        // Панель управления
        JPanel managePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        managePanel.setBorder(BorderFactory.createTitledBorder("Управление упражнениями"));

        JButton editButton = new JButton("✏️ Редактировать");
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.black);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> editExercise(exercisesTable));

        JButton deleteButton = new JButton("🗑️ Удалить");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.black);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteExercise(exercisesTable));

        JButton closeButton = new JButton("✖ Закрыть");
        closeButton.addActionListener(e -> dispose());

        managePanel.add(editButton);
        managePanel.add(deleteButton);
        managePanel.add(closeButton);

        add(addPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(managePanel, BorderLayout.SOUTH);
    }

    private void loadExercises() {
        exercisesTableModel.setRowCount(0);
        List<Exercise> exercises = db.getExercises(userId);

        for (Exercise ex : exercises) {
            String type = ex.isCustom() ? "Пользовательское" : "Стандартное";
            exercisesTableModel.addRow(new Object[]{
                    ex.getId(),
                    ex.getName(),
                    ex.getMuscleGroup(),
                    type
            });
        }
    }

    private void editExercise(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите упражнение для редактирования!");
            return;
        }

        int exerciseId = (int) exercisesTableModel.getValueAt(selectedRow, 0);
        String currentName = (String) exercisesTableModel.getValueAt(selectedRow, 1);
        String type = (String) exercisesTableModel.getValueAt(selectedRow, 3);

        if (!type.equals("Пользовательское")) {
            JOptionPane.showMessageDialog(this,
                    "Можно редактировать только пользовательские упражнения!",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newName = JOptionPane.showInputDialog(this,
                "Введите новое название упражнения:", currentName);

        if (newName != null && !newName.trim().isEmpty()) {
            if (db.updateExerciseName(exerciseId, newName.trim())) {
                loadExercises();
                if (mainUI != null) {
                    mainUI.refreshData();
                }
                JOptionPane.showMessageDialog(this, "✅ Упражнение обновлено!");
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при обновлении!");
            }
        }
    }

    private void deleteExercise(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите упражнение для удаления!");
            return;
        }

        String type = (String) exercisesTableModel.getValueAt(selectedRow, 3);
        if (!type.equals("Пользовательское")) {
            JOptionPane.showMessageDialog(this,
                    "Можно удалять только пользовательские упражнения!",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int exerciseId = (int) exercisesTableModel.getValueAt(selectedRow, 0);
        String exerciseName = (String) exercisesTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Удалить упражнение \"" + exerciseName + "\"?\nВсе подходы будут также удалены.",
                "🗑️ Подтверждение удаления", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (db.deleteExercise(exerciseId)) {
                loadExercises();
                if (mainUI != null) {
                    mainUI.refreshData();
                }
                JOptionPane.showMessageDialog(this, "✅ Упражнение удалено!");
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении!");
            }
        }
    }
}