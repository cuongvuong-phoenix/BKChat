package app.controllers;

import app.models.Message;
import app.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MessageController {
    private MessageController() {
    }

    private static class MessageControllerHolder {
        private static final MessageController INSTANCE = new MessageController();
    }

    public static MessageController getInstance() {
        return MessageControllerHolder.INSTANCE;
    }

    public List<Message> getMessageList(User userFrom, User userTo) throws SQLException {
        List<Message> messageList = new ArrayList<>();

        String query = "CALL BKChat.USP_GetMessageList(?, ?)";
        ResultSet resultSet = DBController.getInstance().ExecQuery(query, userFrom.getUserName(), userTo.getUserName());

        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            Timestamp timeSent = resultSet.getTimestamp(2);
            Timestamp timeRecv = resultSet.getTimestamp(3);
            String content = resultSet.getString(4);

            if (userFrom.getUserName().equals(resultSet.getString(5))) {
                messageList.add(new Message(id, timeSent, timeRecv, content, userFrom, userTo));
            } else {
                messageList.add(new Message(id, timeSent, timeRecv, content, userTo, userFrom));
            }
        }

        return messageList;
    }

    public Message insertMessage(String content, User userFrom, User userTo) throws SQLException {
        Message message = null;

        String query = "CALL BKChat.USP_InsertMessage (?, ?, ?)";
        ResultSet resultSet = DBController.getInstance().ExecQuery(query, content, userFrom.getUserName(), userTo.getUserName());

        if (resultSet.next()) {
            int id = resultSet.getInt(1);
            Timestamp timeSent = resultSet.getTimestamp(2);
            Timestamp timeRecv = resultSet.getTimestamp(3);
            String msgContent = resultSet.getString(4);

            message = new Message(id, timeSent, timeRecv, msgContent, userFrom, userTo);
        }

        return message;
    }
}
