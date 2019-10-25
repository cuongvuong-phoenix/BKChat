package app.controllers;

import app.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private UserController() {
    }

    private static class UserControllerHolder {
        private static final UserController INSTANCE = new UserController();
    }

    public static UserController getInstance() {
        return UserControllerHolder.INSTANCE;
    }

    public User getUser(String userName) throws SQLException {
        User user = null;

        String query = "SELECT * FROM BKChat.tbl_user WHERE userName = ?";
        ResultSet resultSet = DBController.getInstance().ExecQuery(query, userName);

        if (resultSet.next()) {
            int id = resultSet.getInt(1);
            String userPass = resultSet.getString(3);
            String userNickname = resultSet.getString(4);
            boolean userAdmin = resultSet.getBoolean(5);
            String userAvatar = resultSet.getString(6);

            user = new User(id, userName, userPass, userNickname, userAdmin, userAvatar);
        }

        return user;
    }

    public List<User> getUserList(User exceptUsers) throws SQLException {
        List<User> userList = new ArrayList<>();

        String query;

        ResultSet resultSet;

        if (exceptUsers != null) {
            query = "SELECT * FROM BKChat.tbl_user WHERE id != ?";
            resultSet = DBController.getInstance().ExecQuery(query, exceptUsers.getId());
        } else {
            query = "SELECT * FROM BKChat.tbl_user";
            resultSet = DBController.getInstance().ExecQuery(query);
        }


        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            String userName = resultSet.getString(2);
            String userPass = resultSet.getString(3);
            String userNickname = resultSet.getString(4);
            boolean userAdmin = resultSet.getBoolean(5);
            String userAvatar = resultSet.getString(6);

            userList.add(new User(id, userName, userPass, userNickname, userAdmin, userAvatar));
        }

        return userList;
    }
}
