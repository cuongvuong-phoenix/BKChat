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
            String userNickname = resultSet.getString(3);
            String userAvatar = resultSet.getString(4);

            user = new User(userName, userNickname, userAvatar);
        }

        return user;
    }

    public List<User> getUserList(User exceptUsers) throws SQLException {
        List<User> userList = new ArrayList<>();

        String query;

        ResultSet resultSet;

        if (exceptUsers != null) {
            query = "SELECT * FROM BKChat.tbl_user WHERE userName != ?";
            resultSet = DBController.getInstance().ExecQuery(query, exceptUsers.getUserName());
        } else {
            query = "SELECT * FROM BKChat.tbl_user";
            resultSet = DBController.getInstance().ExecQuery(query);
        }

        while (resultSet.next()) {
            String userName = resultSet.getString(1);
            String userNickname = resultSet.getString(3);
            String userAvatar = resultSet.getString(4);

            userList.add(new User(userName, userNickname, userAvatar));
        }

        return userList;
    }
}
