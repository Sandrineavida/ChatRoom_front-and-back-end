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

//    @Override
//    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws IOException {
//        String room = getRoomFromSession(session);
//        if (room != null) {
//            roomSessionsMap.computeIfAbsent(room, k -> new ArrayList<>()).add(session);
//            LOGGER.info("连接已建立: " + this.wsServerName + " - 房间: " + room);
//
////            // 发送历史消息
////            List<MessageSocket> history = messageSocketsHistory.getOrDefault(room, new ArrayList<>());
////            for (MessageSocket messageSocket : history) {
////                String jsonMessage = objectMapper.writeValueAsString(messageSocket);
////                session.sendMessage(new TextMessage(jsonMessage));
////            }
////
////            // Broadcast the updated user list
////            broadcastUserList(room);
//        } else {
//            session.close(CloseStatus.BAD_DATA);
//            LOGGER.warn("无法获取房间信息，关闭连接");
//        }
//    }
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws IOException {
        String room = getRoomFromSession(session);
        if (room != null) {
            roomSessionsMap.computeIfAbsent(room, k -> new ArrayList<>()).add(session);
            LOGGER.info("连接已建立: " + this.wsServerName + " - 房间: " + room);

            // 获取用户名，这取决于您如何在连接时获取用户名
            String username = getUserFromSession(session);
            if (username != null) {
                sessionUserMap.put(session, username);
            }

            // Update and broadcast the user list
            broadcastUserList(room);
        } else {
            session.close(CloseStatus.BAD_DATA);
            LOGGER.warn("无法获取房间信息，关闭连接");
        }
    }


//    @Override
//    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
//        String room = getRoomFromSession(session);
//        String user = sessionUserMap.remove(session);
//        if (room != null && user != null) {
//            // 用户离开聊天室的消息
//            MessageSocket leaveMessage = new MessageSocket();
//            leaveMessage.setUser(user);
//            leaveMessage.setMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " " + user + " has left the chatroom");
//            try {
//                broadcast(room, leaveMessage);
//            } catch (IOException e) {
//                LOGGER.error("广播消息失败", e);
//            }
//
//            roomSessionsMap.getOrDefault(room, new ArrayList<>()).remove(session);
//            LOGGER.info("连接已关闭: " + this.wsServerName + " - 房间: " + room);
//
//            // Broadcast the updated user list
//            try {
//                broadcastUserList(room);
//            } catch (IOException e) {
//                LOGGER.error("广播用户列表失败", e);
//            }
//        } else if (room == null) {
//            LOGGER.warn("无法找到房间");
//        } else {
//            LOGGER.warn("无法找到用户");
//        }
//    }
@Override
public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
    String room = getRoomFromSession(session);
    String user = sessionUserMap.remove(session);
    // Broadcast the updated user list
    try {
        broadcastUserList(room);
    } catch (IOException e) {
        LOGGER.error("广播用户列表失败", e);
    }
    LOGGER.info("用户已离开: " + this.wsServerName + " - 房间: " + room + " - 用户: " + user);
    if (room != null && user != null) {
        // 用户离开聊天室的消息
        MessageSocket leaveMessage = new MessageSocket();
        leaveMessage.setType("chat"); // Ensure type is set to 'chat'
        leaveMessage.setUser(user);
        leaveMessage.setMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " " + user + " has left the chatroom");
        try {
            broadcast(room, leaveMessage);
        } catch (IOException e) {
            LOGGER.error("广播消息失败", e);
        }

        // Remove the session from the roomSessionsMap
        List<WebSocketSession> sessions = roomSessionsMap.getOrDefault(room, new ArrayList<>());
        sessions.remove(session);
        if (sessions.isEmpty()) {
            roomSessionsMap.remove(room);
        } else {
            roomSessionsMap.put(room, sessions);
        }
        LOGGER.info("连接已关闭: " + this.wsServerName + " - 房间: " + room);


    } else if (room == null) {
        LOGGER.warn("无法找到房间!!!!!");
    } else {
        LOGGER.warn("无法找到用户!!!!!!!!!!!!!!!!!!!!@@@@@@@@");
    }
}



    //    @Override
//    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws IOException {
//        String room = getRoomFromSession(session);
//        if (room != null) {
//            String receivedMessage = message.getPayload();
//            MessageSocket messageSocket = objectMapper.readValue(receivedMessage, MessageSocket.class);
//
//            if ("users".equals(messageSocket.getType())) {
//                LOGGER.info("NND!!!!!!!!!!!!&&&&&&&&&&&&: " + receivedMessage);
//                return;
//            }
//
//            // 处理用户信息消息
//            if ("user_info".equals(messageSocket.getType())) {
//                LOGGER.info("NND!!!!!!!!!!!@@@@@@@@@@@@@@@@@@@@@!########^&&&&&&&&&&&&&&&&&&&&&: " + receivedMessage);
//                session.getAttributes().put("username", messageSocket.getUser());
//                sessionUserMap.put(session, messageSocket.getUser());
//
//                LOGGER.info("用户信息已更新: " + this.wsServerName + " - 房间: " + room + " - 用户: " + messageSocket.getUser());
//
//                // Broadcast the updated user list
//                broadcastUserList(room);
//
////                messageSocketsHistory.computeIfAbsent(room, k -> new ArrayList<>()).add(messageSocket);
//                broadcast(room, messageSocket);
//            } else {
////                messageSocketsHistory.computeIfAbsent(room, k -> new ArrayList<>()).add(messageSocket);
//                broadcast(room, messageSocket);
//            }
//        } else {
//            LOGGER.warn("收到消息，但无法确定房间: " + message.getPayload());
//        }
//        LOGGER.info("session map: " + sessionUserMap);
//    }
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

        // 处理用户信息消息
        if ("user_info".equals(messageSocket.getType())) {
            LOGGER.info("Received user info message: " + receivedMessage);
            session.getAttributes().put("username", messageSocket.getUser());
            sessionUserMap.put(session, messageSocket.getUser());

            LOGGER.info("用户信息已更新: " + this.wsServerName + " - 房间: " + room + " - 用户: " + messageSocket.getUser());

            // Broadcast the updated user list
            broadcastUserList(room);

            broadcast(room, messageSocket);
        }

        // 处理普通聊天消息
        else {
            broadcast(room, messageSocket);
        }
    } else {
        LOGGER.warn("收到消息，但无法确定房间: " + message.getPayload());
    }

    // 在处理完消息后更新用户列表
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


//    private void broadcastUserList(String room) throws IOException {
//        List<WebSocketSession> sessions = roomSessionsMap.getOrDefault(room, new ArrayList<>());
//        List<String> users = new ArrayList<>();
//        for (WebSocketSession session : sessions) {
//            String user = sessionUserMap.get(session);
//            if (user != null) {
//                users.add(user);
//            }
//        }
//        MessageSocket userListMessage = new MessageSocket();
//        userListMessage.setType("users");
//        userListMessage.setUsers(users); // Set the list of users
//
//        String jsonMessage = objectMapper.writeValueAsString(userListMessage);
//        for (WebSocketSession session : sessions) {
//            if (session.isOpen()) {
//                session.sendMessage(new TextMessage(jsonMessage));
//            } else {
//                LOGGER.warn("Attempted to send message to closed session");
//            }
//        }
//    }
//    private void broadcastUserList(String room) throws IOException {
//        List<WebSocketSession> sessions = roomSessionsMap.getOrDefault(room, new ArrayList<>());
//        List<String> users = new ArrayList<>();
//        for (WebSocketSession session : sessions) {
//            String user = sessionUserMap.get(session);
//            if (user != null) {
//                users.add(user);
//            }
//        }
//        MessageSocket userListMessage = new MessageSocket();
//        userListMessage.setType("users");
//        userListMessage.setUsers(users); // Set the list of users
//
//        String jsonMessage = objectMapper.writeValueAsString(userListMessage);
//        for (WebSocketSession session : sessions) {
//            if (session.isOpen()) {
//                session.sendMessage(new TextMessage(jsonMessage));
//            } else {
//                LOGGER.warn("Attempted to send message to closed session");
//            }
//        }
//    }
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
