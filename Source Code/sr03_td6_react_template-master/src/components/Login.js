import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./Login.css";

const Login = (props) => {
    const [mail, setMail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(''); // 用于显示错误信息
    const navigate = useNavigate();

    const handleLogin = async (event) => {
        event.preventDefault();

        console.log("mail = " + mail);
        console.log("password = " + password);

        try {
            const res = await axios.post("http://localhost:8080/api/login", { mail, password });
            sessionStorage.setItem("userId", res.data.id);
            sessionStorage.setItem("token", res.data.token);

            // 从响应体中提取Token
            const token = res.data.token;
            if (token) {
                console.log("token = " + token);
                sessionStorage.setItem("token", token);
            } else {
                console.error("Token not found in response");
            }

            setError(''); // 登录成功，重置错误状态
            navigate('/chat'); // 登录成功后跳转到ChatList页面
        } catch (err) {
            console.log(err);
            // 根据后端返回的错误信息显示相应的错误消息
            if (err.response && err.response.data && err.response.data.error) {
                setError(err.response.data.error);
            } else {
                setError('An unknown error occurred.');
            }
        }
    };

    useEffect(() => {
        // Function to create stars
        document.body.classList.add('login-page');
        function createStars(numberOfStars) {
            const starContainer = document.createElement('div');
            starContainer.className = 'star-container';
            document.body.appendChild(starContainer);

            const logoRect = document.querySelector('.logo').getBoundingClientRect();
            const loginRect = document.querySelector('.login').getBoundingClientRect();

            for (let i = 0; i < numberOfStars; i++) {
                let star = document.createElement('div');
                star.className = 'star';

                let x, y;
                do {
                    x = Math.random() * window.innerWidth;
                    y = Math.random() * window.innerHeight;
                } while (
                    (x >= logoRect.left && x <= logoRect.right && y >= logoRect.top && y <= logoRect.bottom) ||
                    (x >= loginRect.left && x <= loginRect.right && y >= loginRect.top && y <= loginRect.bottom)
                    );

                star.style.left = `${x}px`;
                star.style.top = `${y}px`;
                star.style.animationDuration = `${Math.random() * 3 + 1}s`;

                const yellowRatio = Math.random();
                const red = 255;
                const green = 255;
                const blue = 255 * (1 - yellowRatio * 0.2);
                star.style.backgroundColor = `rgb(${red}, ${green}, ${blue})`;

                starContainer.appendChild(star);
            }
        }

        createStars(222);

        return () => {
            const starContainer = document.querySelector('.star-container');
            if (starContainer) {
                starContainer.remove();
            }
        };
    }, []);

    return (
        <div className="login-container">
            <h1 className="logo">Chat . . . !</h1>
            <form id="loginForm" onSubmit={handleLogin} className="login">
                <input
                    type="email"
                    className={`form-control${error ? ' is-invalid' : ''}`}
                    id="mail"
                    value={mail}
                    onChange={e => setMail(e.target.value)}
                    placeholder={error.includes('Email') ? 'Incorrect email' : 'Email'}
                    required
                />
                <input
                    type="password"
                    className={`form-control${error ? ' is-invalid' : ''}`}
                    id="password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    placeholder={error.includes('password') ? 'Incorrect password' : 'Password'}
                    required
                />
                {error && <div className="error-message">{error}</div>}
                <button type="submit">Login</button>
            </form>
        </div>
    );
};

export default Login;

