package fr.utc.sr03.chat_admin.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "sr03_users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) // strategy=GenerationType.IDENTITY => obligatoire pour auto increment mysql
    private long id;
    @Column(name = "firstname")
    @Size(min = 2)
    @NotEmpty(message = "firstname obligatoire")
    private String firstName;
    @Column(name = "lastname")
//    @Size(min = 2)
//    @NotEmpty(message = "lastname obligatoire")
    private String lastName;
    private String mail;
    private String password;
    private Boolean admin;

    @Column(name = "is_locked")
    private boolean isLocked;

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }


    // One user can create many chat rooms
    @OneToMany(mappedBy = "createdBy")
    private Set<ChatRoom> createdChatRooms = new HashSet<>(); // 初始化集合

    @ManyToMany(mappedBy = "participants")
    private Set<ChatRoom> invitedChatRooms = new HashSet<>(); // 初始化集合


    // Helper methods for createdChatRooms
    public void addCreatedChatRoom(ChatRoom chatRoom) {
        if (createdChatRooms == null) {
            createdChatRooms = new HashSet<>();
        }
        createdChatRooms.add(chatRoom);
        chatRoom.setCreatedBy(this); // 设置反向关联
    }

    public void removeCreatedChatRoom(ChatRoom chatRoom) {
        if (createdChatRooms != null && createdChatRooms.contains(chatRoom)) {
            createdChatRooms.remove(chatRoom);
            chatRoom.setCreatedBy(null); // 解除关联
        }
    }

    // Helper methods for invitedChatRooms
    public void acceptChatRoomInvitation(ChatRoom chatRoom) {
        if (invitedChatRooms == null) {
            invitedChatRooms = new HashSet<>();
        }
        invitedChatRooms.add(chatRoom);
        chatRoom.getParticipants().add(this); // 设置反向关联
    }

    public void declineChatRoomInvitation(ChatRoom chatRoom) {
        if (invitedChatRooms != null && invitedChatRooms.contains(chatRoom)) {
            invitedChatRooms.remove(chatRoom);
            chatRoom.getParticipants().remove(this); // 解除关联
        }
    }



    public User() {
// 无参数构造函数：实体类必须有一个无参数的构造函数（可以是protected或public）
    }

    public Set<ChatRoom> getCreatedChatRooms() {
        return createdChatRooms;
    }

    public void setCreatedChatRooms(Set<ChatRoom> createdChatRooms) {
        this.createdChatRooms = createdChatRooms;
    }

    public Set<ChatRoom> getInvitedChatRooms() {
        return invitedChatRooms;
    }

    public void setInvitedChatRooms(Set<ChatRoom> invitedChatRooms) {
        this.invitedChatRooms = invitedChatRooms;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    /*
    public static void main(String[] args) {
        // 创建用户实例
        User user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Winchester");
        user1.setMail("john@winchester.com");
        user1.setPassword("password");
        user1.setAdmin(true);

        User user2 = new User();
        user2.setFirstName("Sam");
        user2.setLastName("Winchester");
        user2.setMail("sam@winchester.com");
        user2.setPassword("password");
        user2.setAdmin(false);

        User user3 = new User();
        user3.setFirstName("Dean");
        user3.setLastName("Winchester");
        user3.setMail("dean@winchester.com");
        user3.setPassword("password");
        user3.setAdmin(false);

        User user4 = new User();
        user4.setFirstName("Daryl");
        user4.setLastName("Dixon");
        user4.setMail("daryl@dixon.com");
        user4.setPassword("password");
        user4.setAdmin(false);

        User user5 = new User();
        user5.setFirstName("Ricky");
        user5.setLastName("Boy");
        user5.setMail("ricky@dixon.com");
        user5.setPassword("password");
        user5.setAdmin(false);

        // 创建聊天室实例
        ChatRoom chatRoom1 = new ChatRoom();
        chatRoom1.setTitle("SPN");
        chatRoom1.setDescription("Super Natural Universe");
        chatRoom1.setStartTime(LocalDateTime.now());
        chatRoom1.setDuration(60);

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setTitle("Hell Yeah");
        chatRoom2.setDescription("NTR");
        chatRoom2.setStartTime(LocalDateTime.now());
        chatRoom2.setDuration(60);

        // 测试聊天室创建者
        user1.addCreatedChatRoom(chatRoom1);
//        user3.addCreatedChatRoom(chatRoom1);
        user1.addCreatedChatRoom(chatRoom2);

        // 测试添加参与者
        chatRoom1.addParticipant(user1);
        chatRoom1.addParticipant(user2);
        chatRoom1.addParticipant(user3);

        chatRoom2.addParticipant(user1);
        chatRoom2.addParticipant(user4);
        chatRoom2.addParticipant(user5);


        // 打印信息以验证关联是否正确
        System.out.println("ChatRoom Title: " + chatRoom1.getTitle());
        System.out.println("Created By: [" + chatRoom1.getCreatedBy().getFirstName() + " " + chatRoom1.getCreatedBy().getLastName() + "]");
        System.out.println("Participants: ");
        for (User participant : chatRoom1.getParticipants()) {
            System.out.println("- " + participant.getFirstName() + " " + participant.getLastName());
        }

        System.out.println("ChatRoom Title: " + chatRoom2.getTitle());
        System.out.println("Created By: [" + chatRoom2.getCreatedBy().getFirstName() + " " + chatRoom2.getCreatedBy().getLastName() + "]");
        System.out.println("Participants: ");
        for (User participant : chatRoom2.getParticipants()) {
            System.out.println("- " + participant.getFirstName() + " " + participant.getLastName());
        }

        // 确认用户已被邀请到聊天室
        System.out.println(user1.getFirstName() + "'s Invited ChatRooms: ");
        for (ChatRoom invitedChatRoom : user1.getInvitedChatRooms()) {
            System.out.println("- " + invitedChatRoom.getTitle());
        }

        // 确认用户已创建聊天室
        System.out.println(user1.getFirstName() + "'s Created ChatRooms: ");
        for (ChatRoom createdChatRoom : user1.getCreatedChatRooms()) {
            System.out.println("- " + createdChatRoom.getTitle());
        }
    }
*/
}
