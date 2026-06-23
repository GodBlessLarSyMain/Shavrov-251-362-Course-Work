package ui;

import database.agent_handler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class AdAgent extends Window {

    private int idAgent;

    private Object[][] broadcastdata = new Object[][]{};
    private Object[][] agentdata = new Object[][]{};

    private Object[] broadcastsHeader = new String[]{
            "Передача",
            "Рейтинг",
            "Дата",
            "Время"
    };

    private Object[] agentHeader = new String[]{
            "Заказчик",
            "Реклама",
            "Сумма",
            "Процент агента"
    };

    private JTable table_agents = new JTable();
    private JTable table_broadcasts = new JTable();

    private JTextField fullNameField = new JTextField(25);
    private JTextField mailField = new JTextField(25);

    private JButton saveProfileButton = new JButton("Сохранить данные агента");
    private JButton refreshButton = new JButton("Обновить");

    private agent_handler handler = new agent_handler();

    public AdAgent(int idAgent) {
        this.idAgent = idAgent;

        getContentPane().setLayout(new BorderLayout());

        JPanel profilePanel = createProfilePanel();
        getContentPane().add(profilePanel, BorderLayout.NORTH);

        loadAgentProfile();
        loadBroadcasts();
        loadAgent();

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Передачи", new JScrollPane(table_broadcasts));
        tabs.addTab("Заказы", new JScrollPane(table_agents));

        getContentPane().add(tabs, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        saveProfileButton.addActionListener(e -> saveAgentProfile());

        refreshButton.addActionListener(e -> {
            loadAgentProfile();
            loadBroadcasts();
            loadAgent();
        });

        this.setVisible(true);
    }

    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Данные рекламного агента");
        JLabel fullNameLabel = new JLabel("ФИО");
        JLabel mailLabel = new JLabel("Почта");

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fullNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        fullNameField.setMaximumSize(fullNameField.getPreferredSize());
        mailField.setMaximumSize(mailField.getPreferredSize());

        fullNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveProfileButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        profilePanel.add(Box.createVerticalStrut(10));
        profilePanel.add(titleLabel);
        profilePanel.add(Box.createVerticalStrut(10));

        profilePanel.add(fullNameLabel);
        profilePanel.add(fullNameField);
        profilePanel.add(Box.createVerticalStrut(10));

        profilePanel.add(mailLabel);
        profilePanel.add(mailField);
        profilePanel.add(Box.createVerticalStrut(10));

        profilePanel.add(saveProfileButton);
        profilePanel.add(Box.createVerticalStrut(10));

        return profilePanel;
    }

    private void loadAgentProfile() {
        try {
            Object[] profile = handler.get_agent_profile(idAgent);

            fullNameField.setText(profile[0] == null ? "" : profile[0].toString());
            mailField.setText(profile[1] == null ? "" : profile[1].toString());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка загрузки данных агента: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void saveAgentProfile() {
        String fullName = fullNameField.getText();
        String mail = mailField.getText();

        if (fullName.isEmpty() || mail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите ФИО и почту агента");
            return;
        }

        try {
            handler.update_agent_profile(idAgent, fullName, mail);

            JOptionPane.showMessageDialog(this, "Данные агента сохранены");

            loadAgentProfile();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка сохранения данных агента: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadBroadcasts() {
        try {
            broadcastdata = handler.get_ad_broadcast();

            DefaultTableModel model = new DefaultTableModel(broadcastdata, broadcastsHeader);
            table_broadcasts.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка загрузки передач: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadAgent() {
        try {
            agentdata = handler.get_agent_data(idAgent);

            DefaultTableModel model = new DefaultTableModel(agentdata, agentHeader);
            table_agents.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка загрузки заказов агента: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}