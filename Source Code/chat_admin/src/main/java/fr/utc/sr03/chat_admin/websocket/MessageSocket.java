package fr.utc.sr03.chat_admin.websocket;

import java.util.List;

public class MessageSocket {
    private String user;
    private String message;
    private String type; // Utilisé pour distinguer les types de messages
    private List<String> users; // Utilisé pour transmettre la liste des utilisateurs

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

