import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import Select from "react-select";
import "./CreateChatRoom.css";

const CreateChatRoom = () => {
    const [participants, setParticipants] = useState([]);
    const [selectedParticipants, setSelectedParticipants] = useState([]);
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [user, setUser] = useState({});
    const navigate = useNavigate();

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
                setError("Error fetching user data.");
            }
        };

        const fetchParticipants = async () => {
            try {
                const token = sessionStorage.getItem('token');
                const userId = sessionStorage.getItem('userId');
                const response = await axios.get('http://localhost:8080/api/users/all-for-login', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                // Filter out the current user
                const filteredParticipants = response.data.filter(user => user.id !== parseInt(userId));
                setParticipants(filteredParticipants.map(user => ({
                    value: user.id,
                    label: `${user.firstName} ${user.lastName}`
                })));
            } catch (error) {
                console.error("There was an error fetching the participants!", error);
                setError("Error fetching participants.");
            }
        };

        fetchUserData();
        fetchParticipants();
    }, []);

    const handleSubmit = async (event) => {
        event.preventDefault();

        const formData = new FormData(event.target);
        const jsonData = {};
        formData.forEach((value, key) => {
            if (jsonData[key]) {
                if (!Array.isArray(jsonData[key])) {
                    jsonData[key] = [jsonData[key]];
                }
                jsonData[key].push(value);
            } else {
                jsonData[key] = value;
            }
        });

        jsonData.participantIds = selectedParticipants.map(participant => participant.value);

        try {
            const token = sessionStorage.getItem('token');
            const response = await fetch('http://localhost:8080/api/chatrooms/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(jsonData),
            });
            const data = await response.text();
            setSuccessMessage(data);
            setTimeout(() => {
                navigate('/chat');
            }, 3000); // Redirect after 3 seconds
        } catch (error) {
            console.error('Error:', error);
            setError('Error creating chatroom.');
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

                    <div className="container mt-5">
                        <h2 style={{color:'#ffffff', fontFamily:'Russo One'}}>Create New ChatRoom</h2>
                        {error && <div className="alert alert-danger">{error}</div>}
                        <form id="createChatRoomForm" onSubmit={handleSubmit}>
                            <div className="mb-3">
                                <label htmlFor="title" className="form-label" style={{color:'#ffffff', fontFamily:'Russo One'}}>Title:</label>
                                <input type="text" id="title" name="title" className="form-control" required/>
                            </div>
                            <div className="mb-3">
                                <label htmlFor="description" className="form-label" style={{color:'#ffffff', fontFamily:'Russo One'}}>Description:</label>
                                <input type="text" id="description" name="description" className="form-control"/>
                            </div>
                            <div className="mb-3">
                                <label htmlFor="startTime" className="form-label" style={{color:'#ffffff', fontFamily:'Russo One'}}>Start Time:</label>
                                <input type="datetime-local" id="startTime" name="startTime" className="form-control"
                                       required/>
                            </div>
                            <div className="mb-3">
                                <label htmlFor="duration" className="form-label" style={{color:'#ffffff', fontFamily:'Russo One'}}>Duration (minutes):</label>
                                <input type="number" id="duration" name="duration" className="form-control" required/>
                            </div>
                            <div className="mb-3">
                                <label htmlFor="participants" className="form-label" style={{color:'#ffffff', fontFamily:'Russo One'}}>Select Participants:</label>
                                <Select
                                    id="participants"
                                    isMulti
                                    options={participants}
                                    value={selectedParticipants}
                                    onChange={setSelectedParticipants}
                                    className="basic-multi-select"
                                    classNamePrefix="select"
                                />
                            </div>
                            <button type="submit" className="btn btn-primary" style={{color:'#ffffff', fontFamily:'Russo One'}}>Create</button>
                        </form>
                        <div id="successMessage" className="alert alert-success mt-3"
                             style={{display: successMessage ? 'block' : 'none'}}>
                            {successMessage}
                        </div>
                    </div>
                </main>
            </div>
        </div>
    );
};

export default CreateChatRoom;
