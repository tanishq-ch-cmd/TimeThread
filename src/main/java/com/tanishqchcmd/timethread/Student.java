package com.tanishqchcmd.timethread;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // --- NEW PROFILE FIELDS ---
    private LocalDate dateJoined;
    private String profilePicture;

    // Upgraded to Integer to safely handle NULL database rows
    private Integer xp = 0;
    private Integer level = 1;

    // Safe Getters
    public Integer getXp() { return xp == null ? 0 : xp; }
    public void setXp(Integer xp) { this.xp = xp; }

    public Integer getLevel() { return level == null ? 1 : level; }
    public void setLevel(Integer level) { this.level = level; }

    // THE RPG ENGINE: Safely initializes nulls and recursively handles multi-leveling
    public void addXp(Integer amount) {
        if (this.xp == null) this.xp = 0;
        if (this.level == null) this.level = 1;

        this.xp += amount;
        while (this.xp >= 100) {
            this.xp -= 100;
            this.level++;
        }
    }

    // Automatically set the join date when the user is created.
    @PrePersist
    protected void onCreate() {
        if (dateJoined == null) {
            dateJoined = LocalDate.now();
        }
    }

    @ManyToMany
    @JoinTable(
            name = "student_group",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<Group> groups = new ArrayList<>();

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name){ this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getDateJoined() { return dateJoined; }
    public void setDateJoined(LocalDate dateJoined) { this.dateJoined = dateJoined; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public List<Group> getGroups() { return groups; }
    public void setGroups(List<Group> groups) { this.groups = groups; }

}