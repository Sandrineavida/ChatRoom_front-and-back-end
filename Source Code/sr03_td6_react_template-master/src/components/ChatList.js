import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import ChatRoom from './ChatRoom';
import Modal from 'react-modal';
import Select from 'react-select';
import './ChatList.css';

const ChatList = () => {
    const [chats, setChats] = useState([]);
    const [user, setUser] = useState({});
    const [isChatRoomOpen, setChatRoomOpen] = useState(false);
    const [selectedRoomId, setSelectedRoomId] = useState(null);
    const [isAddUserModalOpen, setAddUserModalOpen] = useState(false);
    const [isRemoveUserModalOpen, setRemoveUserModalOpen] = useState(false);
    const [isViewUsersModalOpen, setViewUsersModalOpen] = useState(false);
    const [isConfirmDeleteModalOpen, setConfirmDeleteModalOpen] = useState(false); // 新增确认删除对话框状态
    const [roomToDelete, setRoomToDelete] = useState(null); // 新增状态存储将要删除的聊天室ID
    const [participants, setParticipants] = useState([]);
    const [allUsers, setAllUsers] = useState([]);
    const [selectedParticipants, setSelectedParticipants] = useState([]);
    const [selectedUsers, setSelectedUsers] = useState([]);
    const [chatRoomCreator, setChatRoomCreator] = useState(null); // 用于存储聊天室创建者

    useEffect(() => {
        document.body.classList.remove('login-page');
        document.body.classList.add('chatlist-page');

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

        const fetchChatData = async () => {
            try {
                const token = sessionStorage.getItem('token');
                const response = await axios.get('http://localhost:8080/api/chatrooms/all', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setChats(response.data);
            } catch (error) {
                console.error("There was an error fetching the chatrooms!", error);
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

        fetchUserData();
        fetchChatData();
        fetchAllUsers();

        return () => {
            document.body.classList.remove('chatlist-page');
        };
    }, []);

    const fetchChatData = async () => {
        try {
            const token = sessionStorage.getItem('token');
            const response = await axios.get('http://localhost:8080/api/chatrooms/all', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setChats(response.data);
        } catch (error) {
            console.error("There was an error fetching the chatrooms!", error);
        }
    };

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
            setChatRoomCreator(response.data.createdBy); // 获取创建者
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
            console.log("View Users Response:", response.data); // Add log
            setParticipants(response.data.participants.map(user => ({
                value: user.id,
                label: `${user.firstName} ${user.lastName}`
            })));
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
            fetchChatData();
        } catch (error) {
            console.error("Error adding users to chat room", error);
        }
    };

    const handleRemoveUsers = async () => {
        try {
            const token = sessionStorage.getItem('token');
            await Promise.all(selectedParticipants.map(user =>
                axios.patch(`http://localhost:8080/api/chatrooms/removeUser/${selectedRoomId}`, { userId: user.value }, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                })
            ));
            closeRemoveUserModal();
            setSelectedParticipants([]);
            fetchChatData();
        } catch (error) {
            console.error("Error removing users from chat room", error);
        }
    };

    const openConfirmDeleteModal = (roomId) => {
        setRoomToDelete(roomId);
        setConfirmDeleteModalOpen(true);
    };

    const closeConfirmDeleteModal = () => {
        setRoomToDelete(null);
        setConfirmDeleteModalOpen(false);
    };

    const handleDeleteChatRoom = async () => {
        try {
            const token = sessionStorage.getItem('token');
            await axios.delete(`http://localhost:8080/api/chatrooms/delete/${roomToDelete}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            closeConfirmDeleteModal();
            // 重新获取聊天室数据以刷新列表
            fetchChatData();
        } catch (error) {
            console.error("Error deleting chat room", error);
        }
    };

    return (
        <div className="chatlist-page">

            <div className="chatlist-content">
                <aside className="chatlist-aside">
                    <div className="chatlist-container-fluid">
                        <div className="chatlist-navbar-brand">
                            <span style={{ fontFamily: "Russo One", fontSize: "30px"}}> Chat !</span>
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
                        </tr>
                        </thead>
                        <tbody>
                        {chats && chats.map(chat => (
                            <tr key={chat.id}>
                                <td className="chat-title" onClick={() => openChatRoom(chat.id)}>{chat.title}</td>
                                <td>{chat.description}</td>
                                <td>
                                    <button onClick={() => openViewUsersModal(chat.id)}>View Users</button>
                                    {chat.createdBy === user.mail && ( // 只显示创建者可以操作的按钮
                                        <>
                                            <button onClick={() => openAddUserModal(chat.id)}>Add User</button>
                                            <button onClick={() => openRemoveUserModal(chat.id)}>Remove User</button>
                                            <button className="delete-button" onClick={() => openConfirmDeleteModal(chat.id)}>Delete ChatRoom</button> {/* 修改为显示确认删除对话框 */}
                                        </>
                                    )}
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
                    {participants.map(participant => (
                        <li key={participant.value}>{participant.label}</li>
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
                className="react-modal-content add-user-modal"
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
                className="react-modal-content remove-user-modal"
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
            <Modal
                isOpen={isConfirmDeleteModalOpen}
                onRequestClose={closeConfirmDeleteModal}
                contentLabel="Confirm Delete"
                className="react-modal-content confirm-delete-modal"
                overlayClassName="react-modal-overlay"
                ariaHideApp={false}
            >
                <h2>Confirm Delete</h2>
                <p>Are you sure you want to delete this chat room?</p>
                <div className="modal-button-container">
                    <button className="modal-button" onClick={handleDeleteChatRoom}>Yes</button>
                    <button className="modal-button cancel" onClick={closeConfirmDeleteModal}>No</button>
                </div>
            </Modal>
        </div>
    );
};

export default ChatList;
