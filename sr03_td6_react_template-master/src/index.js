// import React from 'react';
// import ReactDOM from 'react-dom/client';
// import './chat.css';
// import App from './App';
import reportWebVitals from './reportWebVitals';
// import {
//     BrowserRouter,
//     Routes,
//     Route,
// } from "react-router-dom";
// import ChatList from "./components/ChatList";
//
// const root = ReactDOM.createRoot(document.getElementById('root'));
// root.render(
//     <BrowserRouter>
//         <Routes>
//             <Route path="/" element={<App />} />
//             <Route path="/chats" element={<ChatList />} />
//         </Routes>
//     </BrowserRouter>
// );
//
// // If you want to start measuring performance in your app, pass a function
// // to log results (for example: reportWebVitals(console.log))
// // or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// reportWebVitals();
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Routes, Route, useLocation } from "react-router-dom";
import App from './App';
import ChatList from "./components/ChatList";
import CreateChatRoom from "./components/CreateChatRoom";
import MyCreatedChatRoom from "./components/MyCreatedChatRoom"; // 引入新组件
import MyInvitedChatRoom from "./components/MyInvitedChatRoom"; // 引入新组件
import ChatRoom from "./components/ChatRoom"; // 引入新组件
import './index.css';



const root = ReactDOM.createRoot(document.getElementById('root'));

const LoadCss = ({children}) => {
    const location = useLocation();

    React.useEffect(() => {
        if (location.pathname === '/') {
            require('./components/Login.css');
        } else if (location.pathname.startsWith('/chatroom/')) {
            require('./components/ChatRoom.css');
        } else if (location.pathname === '/chat'){
            require('./components/ChatList.css');
        } else if (location.pathname === '/create-chat'){
            require('./components/CreateChatRoom.css');
        } else if (location.pathname === '/my-created-chat'){
            require('./components/MyCreatedChatRoom.css');
        } else if (location.pathname === '/my-invited-chat'){
            require('./components/MyInvitedChatRoom.css');
        }
    }, [location]);

    return children;
};

root.render(
    <BrowserRouter>
        <LoadCss>
            <Routes>
                <Route path="/" element={<App />} />
                <Route path="/chat" element={<ChatList />} />
                <Route path="/create-chat" element={<CreateChatRoom />} />
                <Route path="/my-created-chat" element={<MyCreatedChatRoom />} />
                <Route path="/my-invited-chat" element={<MyInvitedChatRoom />} />
                <Route path="/chatroom/:roomId" element={<ChatRoom />} />
            </Routes>
        </LoadCss>
    </BrowserRouter>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
