package viewer;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    String databaseName;
    Object[] columns;
    Object[][] data;
    ArrayList<String> tableNames;

    Database(String databaseName) {
        setDatabaseName(databaseName);
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public ArrayList<String> getTableNames() {
        return this.tableNames;
    }

    public void setTableNames() {
        String getTablesNames = "SELECT name FROM sqlite_master WHERE type =? AND name NOT LIKE ?";
        ArrayList<String> table = new ArrayList<>();
        try (Connection connection = connect()) {

            try (PreparedStatement getTables = connection.prepareStatement(getTablesNames)) {
                getTables.setString(1, "table");
                getTables.setString(2, "sqlite_%");

                try (ResultSet tables = getTables.executeQuery()) {
                    while (tables.next()) {
                        table.add(tables.getString("name"));
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        this.tableNames = table;
    }

    public boolean populateData(String getDataFromString) {
        boolean noError = true;
        ResultSetMetaData recordSetMetaData;

        ArrayList<ArrayList<String>> content = new ArrayList<>();

        int columnNumber;

        try (Connection connection = connect()) {

            try (Statement statement = connection.createStatement()) {

                try (ResultSet rs = statement.executeQuery(getDataFromString)) {

                    // get the columns
                    recordSetMetaData = rs.getMetaData();

                    // get columns number
                    columnNumber = recordSetMetaData.getColumnCount();
                    Object[] columns = new Object[columnNumber];
                    // get columns name
                    for (int i = 1; i <= recordSetMetaData.getColumnCount(); i++) {
                        columns[i - 1] = (recordSetMetaData.getColumnName(i));
                    }
                    setColumns(columns);

                    // loop for every row of recordset
                    while (rs.next()) {
                        ArrayList<String> tempArrayList = new ArrayList<>();
                        //content = new String[columnNumber];
                        for (int i = 0; i < columnNumber; i++) {
                            tempArrayList.add(rs.getString(i + 1));
                        }
                        content.add(tempArrayList);

                    }
                    setData(content);
                } catch (SQLException exception) {
                    exception.printStackTrace();
                    noError = false;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
                noError = false;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            noError = false;
        }

        return noError;
    }

    private Connection connect() {
        String url = "jdbc:sqlite:" + databaseName;

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return connection;
    }

    public void setColumns(Object[] columns) {
        this.columns = columns;
    }

    public Object[] getColumns() {
        return this.columns;
    }

    public void setData(ArrayList<ArrayList<String>> data) {
        Object[][] temp = new Object[data.size()][data.get(0).size()];

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).size(); j++) {
                temp[i][j] = data.get(i).get(j);
            }
        }
        this.data = temp;
    }

    public Object[][] getData() {
        return this.data;
    }
}