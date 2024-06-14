package fr.utc.sr03.chat_admin.repository;

import fr.utc.sr03.chat_admin.model.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> findAdminOnly();
}