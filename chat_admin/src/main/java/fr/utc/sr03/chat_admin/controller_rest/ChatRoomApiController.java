package fr.utc.sr03.chat_admin.controller_rest;

import fr.utc.sr03.chat_admin.dto.ChatRoomDTO;
import fr.utc.sr03.chat_admin.dto.UserDTO;
import fr.utc.sr03.chat_admin.model.ChatRoom;
import fr.utc.sr03.chat_admin.model.User;
import fr.utc.sr03.chat_admin.service.ChatRoomService;
import fr.utc.sr03.chat_admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chatrooms")
@CrossOrigin(origins="*", allowedHeaders="*")
public class ChatRoomApiController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> createChatRoom(@RequestBody ChatRoom chatRoom, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = principal.getName();
        Set<Long> participantIds = chatRoom.getParticipantIds();
        chatRoom.setParticipantIds(new HashSet<>());  // Clear to avoid persistence issues

        chatRoomService.createChatRoom(chatRoom, email, participantIds);
        return ResponseEntity.ok("ChatRoom created successfully.");
    }

    @GetMapping("/myCreated")
    public ResponseEntity<List<ChatRoomDTO>> getMyCreatedChatRooms(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(null);
        }

        String email = principal.getName();
        User user = userService.findByMail(email);
        List<ChatRoomDTO> createdChatRooms = chatRoomService.findByCreatedBy(user);
        return ResponseEntity.ok(createdChatRooms);
    }

    @GetMapping("/myJoined")
    public ResponseEntity<List<ChatRoomDTO>> getMyJoinedChatRooms(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(null);
        }

        String email = principal.getName();
        User user = userService.findByMail(email);
        List<ChatRoomDTO> joinedChatRooms = chatRoomService.findByParticipant(user);
        return ResponseEntity.ok(joinedChatRooms);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ChatRoomDTO>> getAllChatRoomsForUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(null);
        }

        String email = principal.getName();
        User user = userService.findByMail(email);
        List<ChatRoomDTO> createdChatRooms = chatRoomService.findByCreatedBy(user);
        List<ChatRoomDTO> joinedChatRooms = chatRoomService.findByParticipant(user);

        Set<ChatRoomDTO> allChatRooms = new HashSet<>(createdChatRooms);
        allChatRooms.addAll(joinedChatRooms);

        return ResponseEntity.ok(new ArrayList<>(allChatRooms));
    }


    @PatchMapping("/update/{id}")
    public ResponseEntity<String> updateChatRoom(@PathVariable Long id, @RequestBody ChatRoom updatedChatRoom, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = principal.getName(); //当前用户（应当为聊天室的创建者）
        User user = userService.findByMail(email);
        ChatRoom existingChatRoom = chatRoomService.findById(id);

        if (existingChatRoom == null) {
            return ResponseEntity.status(404).body("ChatRoom not found");
        }

        if (!existingChatRoom.getCreatedBy().equals(user)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        // 更新提供的字段
        if (updatedChatRoom.getTitle() != null) {
            existingChatRoom.setTitle(updatedChatRoom.getTitle());
        }
        if (updatedChatRoom.getDescription() != null) {
            existingChatRoom.setDescription(updatedChatRoom.getDescription());
        }
        if (updatedChatRoom.getStartTime() != null) {
            existingChatRoom.setStartTime(updatedChatRoom.getStartTime());
        }
        if (updatedChatRoom.getDuration() != null) {
            existingChatRoom.setDuration(updatedChatRoom.getDuration());
        }
        if (updatedChatRoom.getParticipants() != null) {
            Set<User> newParticipants = new HashSet<>();
            for (User participant : updatedChatRoom.getParticipants()) {
                User foundUser = userService.findById(participant.getId());
                if (foundUser != null) {
                    newParticipants.add(foundUser);
                }
            }
            existingChatRoom.getParticipants().retainAll(newParticipants);
            existingChatRoom.getParticipants().addAll(newParticipants);
        }

        chatRoomService.updateChatRoom(existingChatRoom);
        return ResponseEntity.ok("ChatRoom updated successfully.");
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteChatRoom(@PathVariable Long id, Principal principal) {
        System.out.println("!!!!!!!REACH!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        String email = principal.getName();
        boolean isDeleted = chatRoomService.deleteChatRoom(id, email);

        if (isDeleted) {
            return ResponseEntity.ok("ChatRoom deleted successfully.");
        } else {
            return ResponseEntity.status(403).body("Forbidden: You are not the owner of this ChatRoom or it does not exist.");
        }
    }

    @PatchMapping("/addUser/{id}")
    public ResponseEntity<String> addUserToChatRoom(@PathVariable Long id, @RequestBody Map<String, Long> payload, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long userId = payload.get("userId");
        String email = principal.getName();
        User currentUser = userService.findByMail(email);
        ChatRoom chatRoom = chatRoomService.findById(id);

        if (chatRoom == null) {
            return ResponseEntity.status(404).body("ChatRoom not found");
        }

        if (!chatRoom.getCreatedBy().equals(currentUser)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        User userToAdd = userService.findById(userId);
        if (userToAdd == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        chatRoom.getParticipants().add(userToAdd);
        chatRoomService.updateChatRoom(chatRoom);
        return ResponseEntity.ok("User added successfully.");
    }

    @PatchMapping("/removeUser/{id}")
    public ResponseEntity<String> removeUserFromChatRoom(@PathVariable Long id, @RequestBody Map<String, Long> payload, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long userId = payload.get("userId");
        String email = principal.getName();
        User currentUser = userService.findByMail(email);
        ChatRoom chatRoom = chatRoomService.findById(id);

        if (chatRoom == null) {
            return ResponseEntity.status(404).body("ChatRoom not found");
        }

        if (!chatRoom.getCreatedBy().equals(currentUser)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        User userToRemove = userService.findById(userId);
        if (userToRemove == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        chatRoom.getParticipants().remove(userToRemove);
        chatRoomService.updateChatRoom(chatRoom);
        return ResponseEntity.ok("User removed successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomDTO> getChatRoom(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(null);
        }

        ChatRoom chatRoom = chatRoomService.findById(id);
        if (chatRoom == null) {
            return ResponseEntity.status(404).body(null);
        }

        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setId(chatRoom.getId());
        chatRoomDTO.setTitle(chatRoom.getTitle());
        chatRoomDTO.setDescription(chatRoom.getDescription());
        chatRoomDTO.setStartTime(chatRoom.getStartTime());
        chatRoomDTO.setDuration(chatRoom.getDuration());
        chatRoomDTO.setCreatedBy(chatRoom.getCreatedBy().getMail());
        chatRoomDTO.setParticipants(chatRoom.getParticipants().stream()
                .map(user -> new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getMail(), user.getAdmin()))
                .collect(Collectors.toSet()));

        return ResponseEntity.ok(chatRoomDTO);
    }



}

