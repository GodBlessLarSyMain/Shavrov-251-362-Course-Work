package ui;

import database.auth_handler;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CheckWin extends Window {

    private final JPanel writepanel = new JPanel();

    private final JTextField textlogin = new JTextField(25);
    private final JPasswordField textpass = new JPasswordField(25);

    private final JButton loginButton = new JButton("Войти");
    private final JButton registerButton = new JButton("Зарегистрироваться");

    private final JComboBox<String> roleBox = new JComboBox<>(new String[]{
            "Рекламный агент",
            "Заказчик",
            "Администратор"
    });

    private final auth_handler handler = new auth_handler();

    public CheckWin() {
        writepanel.setLayout(new BoxLayout(writepanel, BoxLayout.Y_AXIS));

        JLabel loginLabel = new JLabel("Логин");
        JLabel passLabel = new JLabel("Пароль");
        JLabel roleLabel = new JLabel("Роль для регистрации");

        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textlogin.setMaximumSize(textlogin.getPreferredSize());
        textpass.setMaximumSize(textpass.getPreferredSize());
        roleBox.setMaximumSize(roleBox.getPreferredSize());

        textlogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        textpass.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.addActionListener(e -> loginUser());
        registerButton.addActionListener(e -> registerUser());

        writepanel.add(loginLabel);
        writepanel.add(textlogin);
        writepanel.add(Box.createVerticalStrut(15));

        writepanel.add(passLabel);
        writepanel.add(textpass);
        writepanel.add(Box.createVerticalStrut(15));

        writepanel.add(roleLabel);
        writepanel.add(roleBox);
        writepanel.add(Box.createVerticalStrut(20));

        writepanel.add(loginButton);
        writepanel.add(Box.createVerticalStrut(10));
        writepanel.add(registerButton);

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(writepanel);

        this.setVisible(true);
    }

    private void loginUser() {
        String loginInput = textlogin.getText().trim();
        String passInput = new String(textpass.getPassword());

        if (loginInput.isEmpty() || passInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите логин и пароль");
            return;
        }

        try {
            int roleId = handler.login(loginInput, passInput);

            if (roleId == -1) {
                JOptionPane.showMessageDialog(this, "Пользователь не найден или пароль неверный");
                return;
            }

            Integer idAgent = null;
            Integer idCustomer = null;

            if (roleId == 1) {
                idAgent = handler.getAgentIdByLoginAndPassword(loginInput, passInput);

                if (idAgent == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "У пользователя с ролью рекламного агента не указан id_agent"
                    );
                    return;
                }
            } else if (roleId == 2) {
                idCustomer = handler.getCustomerIdByLoginAndPassword(loginInput, passInput);

                if (idCustomer == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "У пользователя с ролью заказчика не указан id_customer"
                    );
                    return;
                }
            }

            openWindowByRole(roleId, idAgent, idCustomer);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка входа: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void registerUser() {
        String loginInput = textlogin.getText().trim();
        String passInput = new String(textpass.getPassword());

        if (loginInput.isEmpty() || passInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите логин и пароль");
            return;
        }

        if (!passcheck(passInput)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Пароль должен содержать минимум 8 символов, цифру, заглавную букву и спецсимвол"
            );
            return;
        }

        int roleId = getSelectedRoleId();

        try {
            if (handler.userExists(loginInput)) {
                JOptionPane.showMessageDialog(this, "Пользователь с таким логином уже существует");
                return;
            }

            handler.register(loginInput, passInput, roleId);

            JOptionPane.showMessageDialog(
                    this,
                    "Регистрация успешна. Теперь войдите под своим логином и паролем."
            );

            textlogin.setText("");
            textpass.setText("");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка регистрации: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private int getSelectedRoleId() {
        String selectedRole = roleBox.getSelectedItem().toString();

        switch (selectedRole) {
            case "Рекламный агент":
                return 1;
            case "Заказчик":
                return 2;
            case "Администратор":
                return 3;
            default:
                return 2;
        }
    }

    private void openWindowByRole(int roleId, Integer idAgent, Integer idCustomer) {
        if (roleId == 1) {
            openagentwin(idAgent);
        } else if (roleId == 2) {
            openuserwin(idCustomer);
        } else if (roleId == 3) {
            opentvcompwin();
        } else {
            JOptionPane.showMessageDialog(this, "Неизвестная роль пользователя");
        }
    }

    public boolean passcheck(String passinput) {
        String relpass = "^(?=.*[A-Z])(?=.*[0-9])(?=.*\\W).{8,}$";
        return passinput.matches(relpass);
    }

    public void openuserwin(int idCustomer) {
        User user = new User(idCustomer);
        user.setVisible(true);
        this.dispose();
    }

    public void openagentwin(int idAgent) {
        AdAgent agent = new AdAgent(idAgent);
        agent.setVisible(true);
        this.dispose();
    }

    public void opentvcompwin() {
        TVComp tvcomp = new TVComp();
        tvcomp.setVisible(true);
        this.dispose();
    }
}
