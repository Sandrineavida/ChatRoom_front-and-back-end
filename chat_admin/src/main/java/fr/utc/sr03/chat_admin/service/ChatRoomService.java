package fr.utc.sr03.chat_admin.service;

import fr.utc.sr03.chat_admin.dto.ChatRoomDTO;
import fr.utc.sr03.chat_admin.dto.UserDTO;
import fr.utc.sr03.chat_admin.model.ChatRoom;
import fr.utc.sr03.chat_admin.model.User;
import fr.utc.sr03.chat_admin.repository.ChatRoomRepository;
import fr.utc.sr03.chat_admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    public void createChatRoom(ChatRoom chatRoom, String email, Set<Long> participantIds) {
        User creator = userRepository.findByMail(email);
        chatRoom.setCreatedBy(creator);

        for (Long participantId : participantIds) {
            User participant = userRepository.findById(participantId).orElseThrow(() -> new RuntimeException("Participant not found"));
            chatRoom.addParticipant(participant);
        }

        chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoomDTO> findByCreatedBy(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByCreatedBy(user);
        return chatRooms.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ChatRoomDTO> findByParticipant(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByParticipantsContaining(user);
        return chatRooms.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ChatRoomDTO convertToDTO(ChatRoom chatRoom) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setId(chatRoom.getId());
        dto.setTitle(chatRoom.getTitle());
        dto.setDescription(chatRoom.getDescription());
        dto.setStartTime(chatRoom.getStartTime());
        dto.setDuration(chatRoom.getDuration());
        dto.setCreatedBy(chatRoom.getCreatedBy().getMail());
        dto.setParticipants(chatRoom.getParticipants().stream()
                .map(user -> new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getMail(), user.getAdmin()))
                .collect(Collectors.toSet()));
        return dto;
    }


    public boolean deleteChatRoom(Long id, String email) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(id);
        if (chatRoomOptional.isPresent()) {
            ChatRoom chatRoom = chatRoomOptional.get();
            if (chatRoom.getCreatedBy().getMail().equals(email)) {
                chatRoomRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }




    public void updateChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

    public ChatRoom findById(Long id) {
        return chatRoomRepository.findById(id).orElse(null);
    }


}

