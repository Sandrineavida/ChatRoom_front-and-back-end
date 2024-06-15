package fr.utc.sr03.chat_admin.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
            user.getInvitedChatRooms().add(this);
        }
    }

    public void removeParticipant(User user) {
        participants.remove(user);
        user.getInvitedChatRooms().remove(this);
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
