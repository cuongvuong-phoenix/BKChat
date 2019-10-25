package app.models;

public class User {
    private int id;

    private String userName;

    private String userPass;

    private String userNickname;

    private boolean userAdmin;

    private String userAvatar;

    public User(String userName) {
        this.userName = userName;
        this.userNickname = "Default Nickname";
    }

    public User(String userName, String userNickname) {
        this.userName = userName;
        this.userNickname = userNickname;
    }

    public User(String userName, String userPass, String userNickname, boolean userAdmin, String userAvatar) {
        this.userName = userName;
        this.userPass = userPass;
        this.userNickname = userNickname;
        this.userAdmin = userAdmin;
        this.userAvatar = userAvatar;
    }

    public User(int id, String userName, String userPass, String userNickname, boolean userAdmin, String userAvatar) {
        this.id = id;
        this.userName = userName;
        this.userPass = userPass;
        this.userNickname = userNickname;
        this.userAdmin = userAdmin;
        this.userAvatar = userAvatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isUserAdmin() {
        return userAdmin;
    }

    public void setUserAdmin(boolean userAdmin) {
        this.userAdmin = userAdmin;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
}