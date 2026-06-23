package ui;

import database.user_handler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class User extends Window {

    private final int idCustomer;

    private final user_handler handler = new user_handler();

    private Object[][] agent_data = new Object[][]{};
    private Object[][] broadcasts_data = new Object[][]{};
    private Object[][] user_data = new Object[][]{};
    private Object[][] orders_data = new Object[][]{};

    private final Object[] agentHeader = new String[]{
            "ФИО",
            "Почта"
    };

    private final Object[] broadcastsHeader = new String[]{
            "Передача",
            "Рейтинг"
    };

    private final Object[] userHeader = new String[]{
            "ФИО агента",
            "Почта агента",
            "Телефон",
            "Реквизиты",
            "ID передачи",
            "ID рекламы",
            "Сумма",
            "Процент агента",
            "Дата",
            "Время"
    };

    private final Object[] ordersHeader = new String[]{
            "Договор",
            "ФИО агента",
            "Почта агента",
            "Реклама",
            "Сумма",
            "Процент агента",
            "Передача",
            "Рейтинг",
            "Дата",
            "Время"
    };

    private final JTable table_agents = new JTable();
    private final JTable table_broadcasts = new JTable();
    private final JTable table_user = new JTable();
    private final JTable table_orders = new JTable();

    private final JButton button = new JButton("Добавить заказ");
    private final JButton refreshButton = new JButton("Обновить");

    public User(int idCustomer) {
        this.idCustomer = idCustomer;

        getContentPane().setLayout(new BorderLayout());

        loadBroadcasts();
        loadAgents();
        loadUserTable();
        loadOrders();

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Передачи", new JScrollPane(table_broadcasts));
        tabs.addTab("Агенты", new JScrollPane(table_agents));
        tabs.addTab("Размещение рекламы", new JScrollPane(table_user));
        tabs.addTab("Мои заказы", new JScrollPane(table_orders));

        getContentPane().add(tabs, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(button);
        buttonPanel.add(refreshButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        button.addActionListener(e -> addOrder());

        refreshButton.addActionListener(e -> {
            loadBroadcasts();
            loadAgents();
            loadOrders();
        });

        this.setVisible(true);
    }

    private void addOrder() {
        int selectedRow = table_user.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите строку для добавления заказа");
            return;
        }

        if (table_user.isEditing()) {
            table_user.getCellEditor().stopCellEditing();
        }

        int row = table_user.convertRowIndexToModel(selectedRow);

        try {
            String fullName = getCellText(row, 0);
            String agentMail = getCellText(row, 1);
            String number = getCellText(row, 2);
            String details = getCellText(row, 3);
            String idBroadcastText = getCellText(row, 4);
            String idAdvertisingText = getCellText(row, 5);
            String summText = getCellText(row, 6);
            String agentPercentageText = getCellText(row, 7);
            String date = getCellText(row, 8);
            String time = getCellText(row, 9);

            if (fullName.isEmpty() || agentMail.isEmpty() || number.isEmpty() || details.isEmpty()
                    || idBroadcastText.isEmpty() || idAdvertisingText.isEmpty()
                    || summText.isEmpty() || agentPercentageText.isEmpty()
                    || date.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля строки заказа");
                return;
            }

            int idBroadcast = Integer.parseInt(idBroadcastText);
            int idAdvertising = Integer.parseInt(idAdvertisingText);

            BigDecimal summ = new BigDecimal(summText.replace(',', '.'));
            BigDecimal agentPercentage = new BigDecimal(agentPercentageText.replace(',', '.'));

            handler.create_order(
                    idCustomer,
                    fullName,
                    agentMail,
                    number,
                    details,
                    idBroadcast,
                    idAdvertising,
                    summ,
                    agentPercentage,
                    date,
                    time
            );

            JOptionPane.showMessageDialog(this, "Заказ добавлен");

            loadUserTable();
            loadOrders();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка SQL: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка данных: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String getCellText(int row, int column) {
        Object value = table_user.getModel().getValueAt(row, column);

        if (value == null) {
            return "";
        }

        return value.toString().trim();
    }

    private void loadBroadcasts() {
        try {
            broadcasts_data = handler.get_broadcast();

            DefaultTableModel model = new DefaultTableModel(broadcasts_data, broadcastsHeader);
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

    private void loadAgents() {
        try {
            agent_data = handler.get_agents();

            DefaultTableModel model = new DefaultTableModel(agent_data, agentHeader);
            table_agents.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка загрузки агентов: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadOrders() {
        try {
            orders_data = handler.get_user_orders(idCustomer);

            DefaultTableModel model = new DefaultTableModel(orders_data, ordersHeader);
            table_orders.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка загрузки заказов: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadUserTable() {
        user_data = new Object[][]{
                {"", "", "", "", "", "", "", "10.00", "2026-06-23", "15:00:00"}
        };

        DefaultTableModel model = new DefaultTableModel(user_data, userHeader);
        table_user.setModel(model);
    }
}
