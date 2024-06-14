import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import "./MyCreatedChatRoom.css";
import RedirectButton from "./RedirectButton";
import ChatRoom from "./ChatRoom";
import Modal from "react-modal";
import Select from "react-select";

const MyCreatedChatRoom = () => {
    const [createdChats, setCreatedChats] = useState([]);
    const [user, setUser] = useState({});
    const [isChatRoomOpen, setChatRoomOpen] = useState(false);
    const [selectedRoomId, setSelectedRoomId] = useState(null);
    const [isAddUserModalOpen, setAddUserModalOpen] = useState(false);
    const [isRemoveUserModalOpen, setRemoveUserModalOpen] = useState(false);
    const [isViewUsersModalOpen, setViewUsersModalOpen] = useState(false);
    const [participants, setParticipants] = useState([]);
    const [allUsers, setAllUsers] = useState([]);
    const [selectedParticipants, setSelectedParticipants] = useState([]);
    const [selectedUsers, setSelectedUsers] = useState([]);
    const [viewParticipants, setViewParticipants] = useState([]);

    const fetchData = async () => {
        try {
            const token = sessionStorage.getItem('token');
            const response = await axios.get('http://localhost:8080/api/chatrooms/myCreated', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setCreatedChats(response.data);
        } catch (error) {
            console.error("There was an error fetching the created chatrooms!", error);
        }
    };

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

        const fetchAllUsers = async () => {
            try {
                const token = sessionStorage.getItem('token');
                const userId = sessionStorage.getItem('userId');
                const response = await axios.get('http://localhost:8080/api/users/all-for-login', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                const filteredParticipants = response.data.filter(user => user.id !== parseInt(userId));
                setAllUsers(filteredParticipants.map(user => ({
                    value: user.id,
                    label: `${user.firstName} ${user.lastName}`
                })));
            } catch (error) {
                console.error("There was an error fetching all users!", error);
            }
        };

        fetchData();
        fetchUserData();
        fetchAllUsers();
    }, []);

    const openChatRoom = (roomId) => {
        setSelectedRoomId(roomId);
        setChatRoomOpen(true);
    };

    const closeChatRoom = () => {
        setChatRoomOpen(false);
        setSelectedRoomId(null);
    };

    const openAddUserModal = (roomId) => {
        setSelectedRoomId(roomId);
        setAddUserModalOpen(true);
    };

    const closeAddUserModal = () => {
        setAddUserModalOpen(false);
        setSelectedRoomId(null);
    };

    const openRemoveUserModal = async (roomId) => {
        setSelectedRoomId(roomId);
        try {
            const token = sessionStorage.getItem('token');
            const response = await axios.get(`http://localhost:8080/api/chatrooms/${roomId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setParticipants(response.data.participants.map(user => ({
                value: user.id,
                label: `${user.firstName} ${user.lastName}`
            })));
            setRemoveUserModalOpen(true);
        } catch (error) {
            console.error("There was an error fetching the participants!", error);
        }
    };

    const closeRemoveUserModal = () => {
        setRemoveUserModalOpen(false);
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

    const handleAddUsers = async () => {
        try {
            const token = sessionStorage.getItem('token');
            await Promise.all(selectedUsers.map(user =>
                axios.patch(`http://localhost:8080/api/chatrooms/addUser/${selectedRoomId}`, { userId: user.value }, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                })
            ));
            closeAddUserModal();
            setSelectedUsers([]);
            // 更新聊天房间列表或用户列表
            fetchData();
        } catch (error) {
            console.error("Error adding users to chat room", error);
        }
    };

    const handleRemoveUsers = async () => {
        try {
            const token = sessionStorage.getItem('token');
            await Promise.all(selectedParticipants.map(user =>
                axios.patch(
                    `http://localhost:8080/api/chatrooms/removeUser/${selectedRoomId}`,
                    { userId: user.value }, // 请求数据
                    {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    }
                )
            ));
            closeRemoveUserModal();
            setSelectedParticipants([]);
            // 更新聊天房间列表或用户列表
            fetchData();
        } catch (error) {
            console.error("Error removing users from chat room", error);
        }
    };

    return (
        <div>

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
                        {createdChats && createdChats.map(chat => (
                            <tr key={chat.id}>
                                <td className="chat-title" onClick={() => openChatRoom(chat.id)}>{chat.title}</td>
                                <td>{chat.description}</td>
                                <td>
                                    <button onClick={() => openViewUsersModal(chat.id)}>View Users</button>
                                    <button onClick={() => openAddUserModal(chat.id)}>Add User</button>
                                    <button onClick={() => openRemoveUserModal(chat.id)}>Remove User</button>
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
                className="react-modal-content"
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
            <Modal
                isOpen={isAddUserModalOpen}
                onRequestClose={closeAddUserModal}
                contentLabel="Add Users"
                className="react-modal-content"
                overlayClassName="react-modal-overlay"
                ariaHideApp={false}
            >
                <h2>Add Users</h2>
                <Select
                    isMulti
                    options={allUsers}
                    value={selectedUsers}
                    onChange={setSelectedUsers}
                    className="basic-multi-select"
                    classNamePrefix="select"
                />
                <div className="modal-button-container">
                    <button className="modal-button" onClick={handleAddUsers}>Add</button>
                    <button className="modal-button cancel" onClick={closeAddUserModal}>Cancel</button>
                </div>
            </Modal>
            <Modal
                isOpen={isRemoveUserModalOpen}
                onRequestClose={closeRemoveUserModal}
                contentLabel="Remove Users"
                className="react-modal-content"
                overlayClassName="react-modal-overlay"
                ariaHideApp={false}
            >
                <h2>Remove Users</h2>
                <Select
                    isMulti
                    options={participants}
                    value={selectedParticipants}
                    onChange={setSelectedParticipants}
                    className="basic-multi-select"
                    classNamePrefix="select"
                />
                <div className="modal-button-container">
                    <button className="modal-button" onClick={handleRemoveUsers}>Remove</button>
                    <button className="modal-button cancel" onClick={closeRemoveUserModal}>Cancel</button>
                </div>
            </Modal>
        </div>
    );
};

export default MyCreatedChatRoom;
