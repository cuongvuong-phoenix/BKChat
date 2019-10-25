package app.controllers;

import java.sql.*;

public class DBController {
    // Singleton
    private DBController() {
    }

    private static class DatabaseControllerHolder {
        private static final DBController INSTANCE = new DBController();
    }

    public static DBController getInstance() {
        return DatabaseControllerHolder.INSTANCE;
    }

    private String db_host = "jdbc:mariadb://localhost:3306/BKChat";
    private String db_username = "bimbal7";
    private String db_password = "bimbal7";

    public ResultSet ExecQuery(String query, Object... parameteres) {
        ResultSet resultSet = null;

        try (Connection connection = DriverManager.getConnection(db_host, db_username, db_password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Prevent SQL Injection

            if (!(preparedStatement instanceof CallableStatement)) {
                for (int i = 0; i < parameteres.length; i++) {
                    preparedStatement.setObject(i + 1, parameteres[i]);
                }

                resultSet = preparedStatement.executeQuery();
            } else {
                CallableStatement callableStatement = connection.prepareCall(query);

                for (int i = 0; i < parameteres.length; i++) {
                    callableStatement.setObject(i + 1, parameteres[i]);
                }

                resultSet = callableStatement.executeQuery();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return resultSet;
    }

    public int ExecUpdate(String query, Object... parameteres) {
        int resultUpdate = 0;

        try (Connection connection = DriverManager.getConnection(db_host, db_username, db_password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Prevent SQL Injection

            if (!(preparedStatement instanceof CallableStatement)) {
                for (int i = 0; i < parameteres.length; i++) {
                    preparedStatement.setObject(i + 1, parameteres[i]);
                }

                resultUpdate = preparedStatement.executeUpdate();
            } else {
                CallableStatement callableStatement = connection.prepareCall(query);

                for (int i = 0; i < parameteres.length; i++) {
                    callableStatement.setObject(i + 1, parameteres[i]);
                }

                resultUpdate = callableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return resultUpdate;
    }

    public boolean ExecQueryCheck(String query, Object... parameteres) {
        boolean resultCheck = false;

        try (Connection connection = DriverManager.getConnection(db_host, db_username, db_password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Prevent SQL Injection

            if (!(preparedStatement instanceof CallableStatement)) {
                for (int i = 0; i < parameteres.length; i++) {
                    preparedStatement.setObject(i + 1, parameteres[i]);
                }

                resultCheck = preparedStatement.execute();
            } else {
                CallableStatement callableStatement = connection.prepareCall(query);

                for (int i = 0; i < parameteres.length; i++) {
                    callableStatement.setObject(i + 1, parameteres[i]);
                }

                resultCheck = callableStatement.execute();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return resultCheck;
    }

    public Object ExecQueryTop(String query, Object... parameteres) {
        ResultSet resultSet = null;
        Object resultTop = null;

        try (Connection connection = DriverManager.getConnection(db_host, db_username, db_password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Prevent SQL Injection

            if (!(preparedStatement instanceof CallableStatement)) {
                for (int i = 0; i < parameteres.length; i++) {
                    preparedStatement.setObject(i + 1, parameteres[i]);
                }

                resultSet = preparedStatement.executeQuery();
            } else {
                CallableStatement callableStatement = connection.prepareCall(query);

                for (int i = 0; i < parameteres.length; i++) {
                    callableStatement.setObject(i + 1, parameteres[i]);
                }

                resultSet = callableStatement.executeQuery();
            }

            if (resultSet.next()) {
                resultTop = resultSet.getObject(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return resultTop;
    }

}
