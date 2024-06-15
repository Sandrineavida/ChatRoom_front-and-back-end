import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import "./MyInvitedChatRoom.css";
import RedirectButton from "./RedirectButton";
import ChatRoom from "./ChatRoom";
import Modal from "react-modal";
import Select from "react-select";

const MyInvitedChatRoom = () => {
    const [invitedChats, setInvitedChats] = useState([]);
    const [user, setUser] = useState({});
    const [isChatRoomOpen, setChatRoomOpen] = useState(false);
    const [selectedRoomId, setSelectedRoomId] = useState(null);
    const [isViewUsersModalOpen, setViewUsersModalOpen] = useState(false);
    const [viewParticipants, setViewParticipants] = useState([]);

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
            } catch (error) {
                console.error("There was an error fetching the user data!", error);
            }
        };

        const fetchData = async () => {
            try {
                const token = sessionStorage.getItem('token');
                const response = await axios.get('http://localhost:8080/api/chatrooms/myJoined', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setInvitedChats(response.data);
            } catch (error) {
                console.error("There was an error fetching the invited chatrooms!", error);
            }
        };

        fetchUserData();
        fetchData();
    }, []);

    const openChatRoom = (roomId) => {
        setSelectedRoomId(roomId);
        setChatRoomOpen(true);
    };

    const closeChatRoom = () => {
        setChatRoomOpen(false);
        setSelectedRoomId(null);
    };

    const openViewUsersModal = async (roomId) => {
        setSelectedRoomId(roomId);
        try {
            const token = sessionStorage.getItem('token');
            const response = await axios.get(`http://localhost:8080/api/chatrooms/${roomId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setViewParticipants(response.data.participants);
            setViewUsersModalOpen(true);
        } catch (error) {
            console.error("There was an error fetching the participants!", error);
        }
    };

    const closeViewUsersModal = () => {
        setViewUsersModalOpen(false);
        setSelectedRoomId(null);
    };

    return (
        <div className="chatlist-page">
            <div className="chatlist-content">
                <aside className="chatlist-aside">
                    <div className="chatlist-container-fluid">
                        <div className="chatlist-navbar-brand">
                            <span style={{fontFamily: "Russo One", fontSize: "30px"}}> Chat !</span>
                        </div>
                        <div className="chatlist-navbar-nav">
                            <Link className="chatlist-nav-link" to="/chat">
                                ✬ Accueil
                            </Link>
                            <Link className="chatlist-nav-link" to="/create-chat">
                                ✩ Planifier une discussion
                            </Link>
                            <Link className="chatlist-nav-link" to="/my-created-chat">
                                ✭ Mes salons de discussion
                            </Link>
                            <Link className="chatlist-nav-link" to="/my-invited-chat">
                                ✯ Mes invitations
                            </Link>
                            {user.admin && (
                                <Link className="chatlist-nav-link" to="http://localhost:8080/login">
                                    ✧ Go to Admin Page
                                </Link>
                            )}
                        </div>
                    </div>
                </aside>
                <main className="chatlist-main">
                    <header className="chatlist-header">
                        <span style={{fontFamily: "Russo One", fontSize: "25px"}}>
                            Hello, {user.firstName} !
                        </span>
                    </header>

                    <table className="chatlist-table">
                        <thead>
                        <tr>
                            <th>Titre</th>
                            <th>Description</th>
                            <th>Actions</th>
                            {/* 新增 Actions 列 */}
                        </tr>
                        </thead>
                        <tbody>
                        {invitedChats && invitedChats.map(chat => (
                            <tr key={chat.id}>
                                <td className="chat-title" onClick={() => openChatRoom(chat.id)}>{chat.title}</td>
                                <td>{chat.description}</td>
                                <td>
                                    <button onClick={() => openViewUsersModal(chat.id)}>View Users</button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </main>
            </div>
            {isChatRoomOpen && (
                <ChatRoom
                    isOpen={isChatRoomOpen}
                    onRequestClose={closeChatRoom}
                    roomId={selectedRoomId}
                />
            )}
            <Modal
                isOpen={isViewUsersModalOpen}
                onRequestClose={closeViewUsersModal}
                contentLabel="View Users"
                className="react-modal-content view-users-modal"
                overlayClassName="react-modal-overlay"
                ariaHideApp={false}
            >
                <h2>View Users</h2>
                <ul>
                    {viewParticipants.map(user => (
                        <li key={user.id}>{user.firstName} {user.lastName}</li>
                    ))}
                </ul>
                <div className="modal-button-container">
                    <button className="modal-button cancel" onClick={closeViewUsersModal}>Close</button>
                </div>
            </Modal>
        </div>
    );
};

export default MyInvitedChatRoom;
