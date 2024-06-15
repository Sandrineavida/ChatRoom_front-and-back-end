# ChatRoom-Projet-SR03

## 项目概述

本项目是一个基于 Spring Boot 和 React 的全栈应用程序，包含一个管理员页面和一个聊天页面。

## L'architecture de l'application

以下是本项目的架构图：

!(readme_picture/sr03_schema.drawio (1).png)



## 组件结构

### 用户 (Utilisateur)
- 用户通过浏览器与客户端进行交互。

### 客户端 (Client)
- 使用 React 构建前端。
- 使用 HTML、Thymeleaf、CSS 和 JavaScript (Bootstrap) 进行页面渲染和样式处理。

### 服务器 (Serveur)
- 使用 Spring Boot 作为后端框架，处理业务逻辑和 API 请求。

### 数据库 (Base de données)
- 使用 MySQL 作为数据库，用于存储和检索应用数据。

### 页面结构

- **Admin Page (HTMLs/Thymeleaf + CSS + JS(Bootstrap))**:
    - 管理员页面，使用 Thymeleaf 模板引擎渲染，包含 HTML、CSS 和 JavaScript (Bootstrap)。

- **Chat Page (React)**:
    - 聊天页面，使用 React 构建动态用户界面。

## 文件和目录结构

```plaintext
src/
├── components/
│   ├── ChatList.css
│   ├── ChatList.js
│   ├── ChatRoom.css
│   ├── ChatRoom.js
│   ├── CreateChatRoom.css
│   ├── CreateChatRoom.js
│   ├── Login.css
│   ├── Login.js
│   ├── MyCreatedChatRoom.css
│   ├── MyCreatedChatRoom.js
│   ├── MyInvitedChatRoom.css
│   ├── MyInvitedChatRoom.js
│   └── RedirectButton.js
├── App.js
├── index.css
├── index.js
└── reportWebVitals.js
