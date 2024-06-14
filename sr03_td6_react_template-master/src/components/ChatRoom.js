import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import Modal from 'react-modal';
import './ChatRoom.css';

const ChatRoom = ({ isOpen, onRequestClose, roomId }) => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [user, setUser] = useState(null);
    const [onlineUsers, setOnlineUsers] = useState([]);
    const ws = useRef(null);

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const token = sessionStorage.getItem('token');
                const userId = sessionStorage.getItem('userId');
                const response = await axios.get(`http://localhost:8080/api/users/${userId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setUser(response.data);
                console.log("User data:", response.data.firstName);

                // Establish WebSocket connection after setting user data
                ws.current = new WebSocket(`ws://localhost:8080/chat?room=${roomId}`);

                ws.current.onopen = () => {
                    console.log(`WebSocket connection opened for room: ${roomId}`);

                    if (response.data) {
                        const enterMessage = {
                            type: 'user_info',
                            user: response.data.firstName + ' ' + response.data.lastName,
                            message: `${new Date().toLocaleTimeString()} ${response.data.firstName} ${response.data.lastName} has entered the chatroom`
                        };
                        ws.current.send(JSON.stringify(enterMessage));
                    } else {
                        console.error("RESPONSE data not found!");
                    }
                };

                ws.current.onmessage = (event) => {
                    try {
                        const message = JSON.parse(event.data);
                        console.log("Received message:", message);
                        setMessages((prevMessages) => [...prevMessages, message]);

                        // Update online users if the message type is 'users'
                        if (message.type === 'users') {
                            setOnlineUsers(message.users);
                        }
                    } catch (error) {
                        console.error("Failed to parse message:", error);
                    }
                };

                ws.current.onclose = (event) => {
                    console.log(`WebSocket connection closed for room: ${roomId}`, event);
                };

                ws.current.onerror = (error) => {
                    console.error('WebSocket error:', error);
                };

            } catch (error) {
                console.error("There was an error fetching the user data!", error);
            }
        };

        fetchUserData();

        return () => {
            if (ws.current) {
                ws.current.close();
            }
        };
    }, [roomId]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (input && user) {
            try {
                const message = {
                    type: 'chat',
                    user: user.firstName + ' ' + user.lastName,
                    message: input
                };
                ws.current.send(JSON.stringify(message));
                setInput('');
            } catch (error) {
                console.error('Failed to send message:', error);
            }
        }
    };

    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={() => {
                if (ws.current) {
                    ws.current.close();
                }
                onRequestClose();
            }}
            contentLabel="Chat Room"
            className="react-modal-content"
            overlayClassName="react-modal-overlay"
            ariaHideApp={false}
        >
            <button className="close-button" onClick={() => {
                if (ws.current) {
                    ws.current.close();
                }
                onRequestClose();
            }}>×</button>
            <div className="chat-container">
                <div className="chat-window">
                    <div className="messages-container">
                        {messages.filter(msg => msg.user !== null).map((msg, index) => (
                            <div key={index} className={`message ${msg.type} ${msg.user === (user?.firstName + ' ' + user?.lastName) ? 'current-user' : 'other-user'}`}>
                                {msg.type === 'chat' ? (
                                    <>
                                        <span className="message-user">{msg.user}</span>
                                        <span className="message-text">{msg.message}</span>
                                    </>
                                ) : (
                                    <span className="message-text">{msg.message}</span>
                                )}
                            </div>
                        ))}
                    </div>
                </div>
                <div className="online-users">
                    <h2 style={{color: '#ffffff', fontFamily: 'Russo One, sans-serif', fontSize: '25px'}}>Online Users</h2>
                    <ul>
                        {onlineUsers.map((user, index) => (
                            <li key={index} style={{color: "#b9d0ff",
                                backgroundColor: 'transparent',
                                fontFamily: 'ZCOOL QingKe HuangYou, monospace',
                                fontWeight: 'bold',
                                fontSize: '22px',
                                border: 'none'}}>{user}</li>
                        ))}
                    </ul>
                </div>
            </div>
            <form className="chat-form" onSubmit={handleSubmit}>
                <input
                    type="text"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                />
                <button type="submit">Send</button>
            </form>
        </Modal>
    );
};

export default ChatRoom;
