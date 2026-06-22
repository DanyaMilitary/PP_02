import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterUI extends JFrame {
    private Database db;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterUI(Database db) {
        this.db = db;
        setTitle("Дневник тренировок - Регистрация");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(44, 62, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Заголовок
        JLabel titleLabel = new JLabel("📝 Регистрация");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // Поля ввода
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Логин:");
        usernameLabel.setForeground(Color.WHITE);
        mainPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        mainPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setForeground(Color.WHITE);
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel confirmLabel = new JLabel("Подтвердите пароль:");
        confirmLabel.setForeground(Color.WHITE);
        mainPanel.add(confirmLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(15);
        mainPanel.add(confirmPasswordField, gbc);

        // Кнопки
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton registerButton = new JButton("Зарегистрироваться");
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.black);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> register());

        JButton backButton = new JButton("Назад");
        backButton.setBackground(new Color(231, 76, 60));
        backButton.setForeground(Color.black);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            this.dispose();
            new LoginUI();
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Пожалуйста, заполните все поля!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Пароли не совпадают!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "Пароль должен содержать минимум 4 символа!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (db.registerUser(username, password)) {
            JOptionPane.showMessageDialog(this,
                    "Регистрация успешна! Теперь вы можете войти.",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new LoginUI();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Пользователь с таким логином уже существует!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}