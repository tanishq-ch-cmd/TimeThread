package com.tanishqchcmd.timethread;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Student sender;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private LocalDateTime timestamp;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Student getSender() { return sender; }
    public void setSender(Student sender) { this.sender = sender; }
    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}