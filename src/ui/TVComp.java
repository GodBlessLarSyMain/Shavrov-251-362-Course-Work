package ui;

import database.admin_handler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class TVComp extends Window {

    private Object[][] data = new Object[][]{};

    private Object[] columnsHeader = new String[]{
            "id_date_time",
            "Передача",
            "Реклама",
            "Агент",
            "Почта",
            "Договор",
            "Сумма",
            "Дата",
            "Время"
    };

    private admin_handler handler = new admin_handler();
    private JTable table1 = new JTable();

    private JButton button = new JButton("Apply");

    public TVComp() {
        getContentPane().setLayout(new BorderLayout());

        loadData();

        JScrollPane scrollPane = new JScrollPane(table1);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(button);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        button.addActionListener(e -> {
            int selectedRow = table1.getSelectedRow();

            if (table1.isEditing()) {
                table1.getCellEditor().stopCellEditing();
            }

            int modelRow = table1.convertRowIndexToModel(selectedRow);

            try {
                int idDateTime = Integer.parseInt(table1.getModel().getValueAt(modelRow, 0).toString());
                int idBroadcast = Integer.parseInt(table1.getModel().getValueAt(modelRow, 1).toString());
                int idAdvertising = Integer.parseInt(table1.getModel().getValueAt(modelRow, 2).toString());
                int idAgent = Integer.parseInt(table1.getModel().getValueAt(modelRow, 3).toString());
                String mail = table1.getModel().getValueAt(modelRow, 4).toString();
                int idAgreement = Integer.parseInt(table1.getModel().getValueAt(modelRow, 5).toString());
                BigDecimal summ = new BigDecimal(table1.getModel().getValueAt(modelRow, 6).toString());
                String date = table1.getModel().getValueAt(modelRow, 7).toString();
                String time = table1.getModel().getValueAt(modelRow, 8).toString();

                handler.update_admin_data(
                        idDateTime,
                        idBroadcast,
                        idAdvertising,
                        idAgent,
                        mail,
                        idAgreement,
                        summ,
                        date,
                        time
                );

                JOptionPane.showMessageDialog(this, "Данные обновлены");

                loadData();

            } catch (SQLException ex) {}
            catch (Exception ex) {}
        });
        this.setVisible(true);
    }


    private void loadData() {
        try {
            data = handler.get_admin_data();

            DefaultTableModel model = new DefaultTableModel(data, columnsHeader);
            table1.setModel(model);

            if (table1.getColumnModel().getColumnCount() > 0) {
                table1.removeColumn(table1.getColumnModel().getColumn(0));
            }

        } catch (SQLException e) {}
    }

    private Object[][] get_data(){
        return data;
    }
}