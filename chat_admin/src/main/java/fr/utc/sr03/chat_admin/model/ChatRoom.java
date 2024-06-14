package fr.utc.sr03.chat_admin.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/*@Entity

    @Entity 注解声明这个类是一个JPA实体。
    这意味着 ChatRoom 类的实例将被持久化到数据库中。
    每个 ChatRoom 实例对应数据库中的一行记录。
* */
@Entity
public class ChatRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private Integer duration;

    /*关系映射
    @ManyToOne 注解表示多对一的关系，即多个 ChatRoom 实例可以被同一个 User 实例创建。
    * 这是一个常见的关系类型，用于表示多个子实体共享一个父实体。
    @JoinColumn(name = "created_by") 指定了用于连接 ChatRoom 实体和 User 实体的外键列名。
    * 在这里，created_by 列用作外键，它存储了创建聊天室的用户的 id 值。
    * 这意味着 ChatRoom 表将包含一个 created_by 列，指向 User 表的主键。
    * */
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Many users can be invited to one chat room
    @ManyToMany
    @JoinTable(
            name = "chat_room_participants",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants;
    @Transient
    private Set<Long> participantIds = new HashSet<>();


    public ChatRoom() {
        // 初始化参与者集合确保它不为空
        this.participants = new HashSet<>();
        this.participantIds = new HashSet<>();
    }

    // Helper methods for managing participants
    public void addParticipant(User user) {
        participants.add(user);
        if (user != createdBy){
            user.getInvitedChatRooms().add(this); // 设置反向关联
        }
    }

    public void removeParticipant(User user) {
        participants.remove(user);
        user.getInvitedChatRooms().remove(this); // 解除关联
    }

    // Getters and Setters
    public Set<User> getParticipants() {
        return participants;
    }
    public Set<Long> getParticipantIds() {
        return participantIds;
    }


    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }
    public void setParticipantIds(Set<Long> participantIds) {
        this.participantIds = participantIds;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
