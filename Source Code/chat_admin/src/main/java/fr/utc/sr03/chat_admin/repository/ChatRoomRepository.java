package fr.utc.sr03.chat_admin.repository;

import fr.utc.sr03.chat_admin.model.ChatRoom;
import fr.utc.sr03.chat_admin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByCreatedBy(User user);
    List<ChatRoom> findByParticipantsContaining(User participant);

}
