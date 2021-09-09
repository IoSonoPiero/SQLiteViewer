package viewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class SQLiteViewer extends JFrame {

    JTextField filenameTextField;
    JButton openButton;
    JComboBox<String> tablesComboBox;
    JTextArea queryTextArea;
    JButton executeButton;
    JTable table;
    JScrollPane contentTable;

    DefaultTableModel model;

    Database database;
    Object[] columns;

    Object[][] data;

    public SQLiteViewer() {
        super("SQLite Viewer");
        initComponents();
    }

    private void cleanComboBox() {
        tablesComboBox.removeAllItems();
    }

    private void populateComboBox(ArrayList<String> tables) {
        for (String table : tables) {
            tablesComboBox.addItem(table);
        }
    }

    private void initComponents() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 650);
        setLocationRelativeTo(null);

        //setLayout(new FlowLayout(FlowLayout.LEFT));
        setLayout(null);

        filenameTextField = new JTextField();
        filenameTextField.setName("FileNameTextField");
        filenameTextField.setBounds(10, 10, 680, 30);
        add(filenameTextField);

        openButton = new JButton("Open");
        openButton.setName("OpenFileButton");
        openButton.setBounds(700, 10, 70, 30);
        openButton.addActionListener(e -> {
            // get the database name in filenameTextField and open it
            String databaseName = filenameTextField.getText().trim();

            // Check if file is existant

            File file = new File(databaseName);

            if (file.exists() && !file.isDirectory()) {
                database = new Database(databaseName);
                database.setTableNames();
                ArrayList<String> tables = database.getTableNames();
                cleanComboBox();
                populateComboBox(tables);
                unlockComponents();
            } else {
                JOptionPane.showMessageDialog(new Frame(), "File doesn't exist!");
                lockComponents();
            }
        });
        add(openButton);

        tablesComboBox = new JComboBox<>();
        tablesComboBox.setName("TablesComboBox");
        tablesComboBox.setBounds(10, 50, 760, 30);
        tablesComboBox.addActionListener(e -> {
            // clean the queryTextArea
            // fill it with the query
            if (tablesComboBox.getItemCount() > 0) {
                String item = tablesComboBox.getSelectedItem().toString();
                String SQL = "SELECT * FROM " + item + ";";
                queryTextArea.removeAll();
                queryTextArea.setText(SQL);
            }
        });
        add(tablesComboBox);

        queryTextArea = new JTextArea();
        queryTextArea.setName("QueryTextArea");
        queryTextArea.setBounds(10, 90, 660, 100);
        add(queryTextArea);

        executeButton = new JButton("Execute");
        executeButton.setName("ExecuteQueryButton");
        executeButton.setBounds(680, 90, 90, 40);

        model = new DefaultTableModel();
        table = new JTable(model);
        table.setName("Table");
        contentTable = new JScrollPane(table);
        contentTable.setBounds(10, 200, 760, 400);
        add(contentTable);

        executeButton.addActionListener(e -> {

            if (database.populateData(queryTextArea.getText())) {
                model.setRowCount(0);

                columns = database.getColumns();
                data = database.getData();

                model.setColumnIdentifiers(columns);

                for (Object[] row : data) {
                    model.addRow(row);
                }
            } else {
                JOptionPane.showMessageDialog(new Frame(), "SQL error syntax!");
            }
        });
        add(executeButton);

        lockComponents();
        tablesComboBox.setEnabled(true);
        setVisible(true);
    }

    private void lockComponents() {
        tablesComboBox.setEnabled(false);
        queryTextArea.setEnabled(false);
        executeButton.setEnabled(false);

    }

    private void unlockComponents() {
        tablesComboBox.setEnabled(true);
        queryTextArea.setEnabled(true);
        executeButton.setEnabled(true);

    }
}