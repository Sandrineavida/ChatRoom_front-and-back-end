package fr.utc.sr03.chat_admin.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    private final String wsServerName;
    private final Map<WebSocketSession, String> sessionUserMap = new HashMap<>();
    private final Map<String, List<WebSocketSession>> roomSessionsMap = new HashMap<>();
    private final Map<String, List<MessageSocket>> messageSocketsHistory = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketHandler(String wsServerName) {
        this.wsServerName = wsServerName;
    }


    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws IOException {
        String room = getRoomFromSession(session);
        if (room != null) {
            roomSessionsMap.computeIfAbsent(room, k -> new ArrayList<>()).add(session);
            LOGGER.info("Connect with: " + this.wsServerName + " - room: " + room);

            String username = getUserFromSession(session);
            if (username != null) {
                sessionUserMap.put(session, username);
            }

            // Update and broadcast the user list
            broadcastUserList(room);
        } else {
            session.close(CloseStatus.BAD_DATA);
            LOGGER.warn("No information of room,connection closed");
        }
    }


@Override
public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
    String room = getRoomFromSession(session);
    String user = sessionUserMap.remove(session);
    // Broadcast the updated user list
    try {
        broadcastUserList(room);
    } catch (IOException e) {
        LOGGER.error("Failed to broadcast user list", e);
    }
    LOGGER.info("User left: " + this.wsServerName + " - room: " + room + " - user: " + user);
    if (room != null && user != null) {
        MessageSocket leaveMessage = new MessageSocket();
        leaveMessage.setType("chat"); // Ensure type is set to 'chat'
        leaveMessage.setUser(user);
        leaveMessage.setMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " " + user + " has left the chatroom");
        try {
            broadcast(room, leaveMessage);
        } catch (IOException e) {
            LOGGER.error("Failed to broadcast user list", e);
        }

        // Remove the session from the roomSessionsMap
        List<WebSocketSession> sessions = roomSessionsMap.getOrDefault(room, new ArrayList<>());
        sessions.remove(session);
        if (sessions.isEmpty()) {
            roomSessionsMap.remove(room);
        } else {
            roomSessionsMap.put(room, sessions);
        }
        LOGGER.info("connection closed: " + this.wsServerName + " - room: " + room);


    } else if (room == null) {
        LOGGER.warn("Can't find room!!!!!");
    } else {
        LOGGER.warn("Can't find user!!!!!!!!!!!!!!!!!!!!@@@@@@@@");
    }
}


@Override
protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws IOException {
    String room = getRoomFromSession(session);
    if (room != null) {
        String receivedMessage = message.getPayload();
        MessageSocket messageSocket = objectMapper.readValue(receivedMessage, MessageSocket.class);

        if ("users".equals(messageSocket.getType())) {
            LOGGER.info("Received user list request: " + receivedMessage);
            return;
        }

        if ("user_info".equals(messageSocket.getType())) {
            LOGGER.info("Received user info message: " + receivedMessage);
            session.getAttributes().put("username", messageSocket.getUser());
            sessionUserMap.put(session, messageSocket.getUser());

            LOGGER.info("User's information : " + this.wsServerName + " - room: " + room + " - user: " + messageSocket.getUser());

            // Broadcast the updated user list
            broadcastUserList(room);

            broadcast(room, messageSocket);
        }

        else {
            broadcast(room, messageSocket);
        }
    } else {
        LOGGER.warn("Received message, but can't confirm room: " + message.getPayload());
    }

    broadcastUserList(room);

    LOGGER.info("session map: " + sessionUserMap);
}


    private void broadcast(String room, MessageSocket messageSocket) throws IOException {
        String jsonMessage = objectMapper.writeValueAsString(messageSocket);
        List<WebSocketSession> sessions = roomSessionsMap.getOrDefault(room, new ArrayList<>());
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(jsonMessage));
            } else {
                LOGGER.warn("Attempted to send message to closed session");
            }
        }
    }



private void broadcastUserList(String room) throws IOException {
    List<WebSocketSession> sessions = roomSessionsMap.getOrDefault(room, new ArrayList<>());
    List<String> users = new ArrayList<>();
    for (WebSocketSession session : sessions) {
        String user = sessionUserMap.get(session);
        if (user != null) {
            users.add(user);
        }
    }
    MessageSocket userListMessage = new MessageSocket();
    userListMessage.setType("users");
    userListMessage.setUsers(users); // Set the list of users

    String jsonMessage = objectMapper.writeValueAsString(userListMessage);
    for (WebSocketSession session : sessions) {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(jsonMessage));
        } else {
            LOGGER.warn("Attempted to send message to closed session");
        }
    }
}


    private String getRoomFromSession(WebSocketSession session) {
        String uri = session.getUri().toString();
        String[] parts = uri.split("\\?");
        if (parts.length > 1) {
            for (String param : parts[1].split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && keyValue[0].equals("room")) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    private String getUserFromSession(WebSocketSession session) {
        return (String) session.getAttributes().get("username");
    }
}
