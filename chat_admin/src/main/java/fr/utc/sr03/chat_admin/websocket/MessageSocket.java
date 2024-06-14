package fr.utc.sr03.chat_admin.websocket;

import java.util.List;

public class MessageSocket {
    private String user;
    private String message;
    private String type; // 新增字段，用于区分消息类型
    private List<String> users; // 新增字段，用于传递用户列表

    // getters and setters
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}

