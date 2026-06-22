import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginUI extends JFrame {
    private Database db;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginUI() {
        db = new Database();
        setTitle("Дневник тренировок - Вход");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
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
        JLabel titleLabel = new JLabel("🏋️ Дневник тренировок");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.black);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Поля ввода
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Логин:");
        usernameLabel.setForeground(Color.black);
        mainPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        mainPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setForeground(Color.black);
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);

        // Кнопки
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        loginButton = new JButton("Войти");
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.black);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> login());

        registerButton = new JButton("Регистрация");
        registerButton.setBackground(new Color(52, 152, 219));
        registerButton.setForeground(Color.black);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> openRegister());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, gbc);

        // Обработка Enter
        usernameField.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());

        add(mainPanel);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Пожалуйста, заполните все поля!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = db.loginUser(username, password);
        if (userId != -1) {
            JOptionPane.showMessageDialog(this,
                    "Добро пожаловать, " + username + "!",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new MainUI(db, userId, username);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Неверный логин или пароль!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegister() {
        this.dispose();
        new RegisterUI(db);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI());
    }
}