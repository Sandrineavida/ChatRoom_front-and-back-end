
//package fr.utc.sr03.chat_admin.controller;
//
//import fr.utc.sr03.chat_admin.dto.ChatRoomDTO;
//import fr.utc.sr03.chat_admin.model.ChatRoom;
//import fr.utc.sr03.chat_admin.model.User;
//import fr.utc.sr03.chat_admin.service.ChatRoomService;
//import fr.utc.sr03.chat_admin.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.security.Principal;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@RestController
//@RequestMapping("/api/chatrooms")
//public class ChatRoomController {
//
//    @Autowired
//    private ChatRoomService chatRoomService;
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/create")
//    public ResponseEntity<String> createChatRoom(@RequestBody ChatRoom chatRoom, Principal principal) {
//        if (principal == null) {
//            return ResponseEntity.status(401).body("Unauthorized");
//        }
//
//        String email = principal.getName();
//        Set<Long> participantIds = chatRoom.getParticipantIds();
//        chatRoom.setParticipantIds(new HashSet<>());  // Clear to avoid persistence issues
//
//        chatRoomService.createChatRoom(chatRoom, email, participantIds);
//        return ResponseEntity.ok("ChatRoom created successfully.");
//    }
//
//    @GetMapping("/myCreated")
//    public ResponseEntity<List<ChatRoomDTO>> getMyCreatedChatRooms(Principal principal) {
//        if (principal == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//
//        String email = principal.getName();
//        User user = userService.findByMail(email);
//        List<ChatRoomDTO> createdChatRooms = chatRoomService.findByCreatedBy(user);
//        return ResponseEntity.ok(createdChatRooms);
//    }
//
//    @GetMapping("/myJoined")
//    public ResponseEntity<List<ChatRoomDTO>> getMyJoinedChatRooms(Principal principal) {
//        if (principal == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//
//        String email = principal.getName();
//        User user = userService.findByMail(email);
//        List<ChatRoomDTO> joinedChatRooms = chatRoomService.findByParticipant(user);
//        return ResponseEntity.ok(joinedChatRooms);
//    }
//
//
//}
//
