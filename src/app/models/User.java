package app.models;

public class User {
    private String userName;

    private String userStatus;

    private String userNickname;

    private String userAvatar;

    public User(String userName) {
        this.userName = userName;
        this.userStatus = "Offline";
        this.userNickname = "Default Nickname";
        this.userAvatar = "avatar-default.jpg";
    }

    public User(String userName, String userStatus) {
        this(userName);
        this.userStatus = userStatus;
    }

    public User(String userName, String userStatus, String userNickname) {
        this(userName, userStatus);
        this.userNickname = userNickname;
    }

    public User(String userName, String userStatus, String userNickname, String userAvatar) {
        this(userName, userStatus, userNickname);
        this.userAvatar = userAvatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}